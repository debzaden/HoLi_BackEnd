package com.example.ai_travel_agent_app.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.example.ai_travel_agent_app.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ai_travel_agent_app.dto.booking.BookingCreateRequestDTO;
import com.example.ai_travel_agent_app.dto.booking.BookingDetailResponseDTO;
import com.example.ai_travel_agent_app.dto.booking.BookingResponseDTO;
import com.example.ai_travel_agent_app.dto.booking.BookingStatusUpdateDTO;
import com.example.ai_travel_agent_app.model.Booking;
import com.example.ai_travel_agent_app.model.BookingStatus;
import com.example.ai_travel_agent_app.model.Customer;
import com.example.ai_travel_agent_app.model.Discount;
import com.example.ai_travel_agent_app.model.Schedule;
import com.example.ai_travel_agent_app.model.ScheduleStatus;
import com.example.ai_travel_agent_app.model.User;
import com.example.ai_travel_agent_app.model.Worker;
import com.example.ai_travel_agent_app.repository.ServiceRepository;
import com.example.ai_travel_agent_app.repository.customer.CustomerRepository;
import com.example.ai_travel_agent_app.repository.worker.ScheduleRepository;
import com.example.ai_travel_agent_app.service.BookingService;
import com.example.ai_travel_agent_app.service.DiscountService;
import com.example.ai_travel_agent_app.service.EmailService;
import com.example.ai_travel_agent_app.service.UserService;
import com.example.ai_travel_agent_app.service.customer.WorkerAvailabilityService;
import com.example.ai_travel_agent_app.service.worker.WorkerService;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private WorkerService workerService;

    @Autowired
    private UserService userService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private DiscountService discountService;

    @Autowired
    private WorkerAvailabilityService availabilityService;

    @Autowired
    private EmailService emailService;

    @Override
    public List<BookingResponseDTO> getAllByWorkers(String userEmail) {
        Worker worker = workerService.getWorkerByEmail(userEmail);

        return bookingRepository.findAllByWorker(worker).stream()
                .map(this::toBookingResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingDetailResponseDTO createBooking(String customerEmail, BookingCreateRequestDTO request) {
        // Validate customer
        User customerUser = userService.findByEmail(customerEmail);
        Customer customer = customerRepository.findByUser(customerUser)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Validate service và worker
        com.example.ai_travel_agent_app.model.Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new RuntimeException("Service not found"));

        Worker worker = service.getWorker();
        if (!worker.getId().equals(request.getWorkerId())) {
            throw new RuntimeException("Service does not belong to this worker");
        }

        // Kiểm tra availability
        boolean isAvailable = availabilityService.isWorkerAvailable(
                request.getWorkerId(),
                request.getWorkDate(),
                request.getTimeSlot(),
                request.getStartTime(),
                request.getDuration());

        if (!isAvailable) {
            throw new RuntimeException("Worker không có sẵn trong thời gian này");
        }

        // Tính toán giá
        float originalPrice = service.getPrice() * request.getDuration();
        float discountAmount = 0;
        Discount discount = null;

        // Xử lý voucher nếu có
        if (request.getVoucherCode() != null && !request.getVoucherCode().trim().isEmpty()) {
            discount = discountService.validateVoucher(request.getVoucherCode(), originalPrice);
            discountAmount = discountService.calculateDiscountAmount(discount, originalPrice);
        }

        float totalPrice = originalPrice - discountAmount;

        // Tạo schedule cho booking này
        LocalDateTime startDateTime = LocalDateTime.of(request.getWorkDate(), request.getStartTime());
        LocalDateTime endDateTime = startDateTime.plusHours(request.getDuration());

        Schedule schedule = new Schedule();
        schedule.setWorker(worker);
        schedule.setStartTime(startDateTime);
        schedule.setEndTime(endDateTime);
        schedule.setStatus(ScheduleStatus.BOOKED);
        schedule = scheduleRepository.save(schedule);

        // Tạo booking
        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setWorker(worker);
        booking.setService(service);
        booking.setSchedule(schedule);
        booking.setLocation(request.getAddress());
        booking.setSpecialRequest(request.getNotes());
        booking.setOriginalPrice(originalPrice);
        booking.setDiscountAmount(discountAmount);
        booking.setTotalPrice(totalPrice);
        booking.setStartTime(startDateTime);
        booking.setEndTime(endDateTime);
        booking.setStatus(BookingStatus.PENDING);
        booking.setDiscount(discount);

        booking = bookingRepository.save(booking);

        // Sử dụng voucher nếu có
        if (discount != null) {
            discountService.useVoucher(discount);
        }

        // Gửi email yêu cầu xác nhận cho worker
        try {
            emailService.sendBookingNotificationToWorker(booking);
        } catch (Exception e) {
            // Log lỗi nhưng không dừng quá trình tạo booking
            System.err.println("Failed to send booking notification to worker: " + e.getMessage());
        }

        return toBookingDetailResponseDTO(booking);
    }

    @Override
    @Transactional
    public BookingDetailResponseDTO updateBookingStatus(String workerEmail, BookingStatusUpdateDTO request) {
        Worker worker = workerService.getWorkerByEmail(workerEmail);

        Booking booking = bookingRepository.findByBookingId(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Kiểm tra booking thuộc về worker này
        if (!booking.getWorker().getId().equals(worker.getId())) {
            throw new RuntimeException("Unauthorized to update this booking");
        }

        // Chỉ cho phép update booking ở trạng thái PENDING
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Booking không thể thay đổi trạng thái");
        }

        if ("CONFIRM".equals(request.getAction())) {
            booking.setStatus(BookingStatus.CONFIRMED);
        } else if ("REJECT".equals(request.getAction())) {
            booking.setStatus(BookingStatus.REJECTED);

            // Giải phóng schedule khi reject
            if (booking.getSchedule() != null) {
                Schedule schedule = booking.getSchedule();
                schedule.setStatus(ScheduleStatus.FREE);
                scheduleRepository.save(schedule);
            }
        } else {
            throw new RuntimeException("Invalid action");
        }

        booking = bookingRepository.save(booking);

        // Gửi email thông báo cho customer về trạng thái booking
        try {
            if ("CONFIRM".equals(request.getAction())) {
                emailService.sendBookingAcceptedToCustomer(booking);
            } else if ("REJECT".equals(request.getAction())) {
                String reason = request.getReason() != null ? request.getReason() : "Không có lý do cụ thể";
                emailService.sendBookingRejectedToCustomer(booking, reason);
            }
        } catch (Exception e) {
            // Log lỗi nhưng không dừng quá trình cập nhật
            System.err.println("Failed to send booking status email: " + e.getMessage());
        }

        return toBookingDetailResponseDTO(booking);
    }

    @Override
    public List<BookingDetailResponseDTO> getCustomerBookings(String customerEmail) {
        User customerUser = userService.findByEmail(customerEmail);
        Customer customer = customerRepository.findByUser(customerUser)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return bookingRepository.findAllByCustomer(customer).stream()
                .map(this::toBookingDetailResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BookingDetailResponseDTO getBookingDetail(Long bookingId) {
        Booking booking = bookingRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        return toBookingDetailResponseDTO(booking);
    }

    @Override
    @Transactional
    public BookingDetailResponseDTO completeBooking(Long bookingId) {
        Booking booking = bookingRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus(BookingStatus.COMPLETED);

        // Cập nhật schedule thành COMPLETED
        if (booking.getSchedule() != null) {
            Schedule schedule = booking.getSchedule();
            schedule.setStatus(ScheduleStatus.COMPLETED);
            scheduleRepository.save(schedule);
        }

        booking = bookingRepository.save(booking);

        // Gửi email thông báo hoàn thành cho customer
        try {
            emailService.sendBookingCompletedToCustomer(booking);
        } catch (Exception e) {
            // Log lỗi nhưng không dừng quá trình hoàn thành
            System.err.println("Failed to send booking completion email: " + e.getMessage());
        }

        return toBookingDetailResponseDTO(booking);
    }

    public BookingResponseDTO toBookingResponseDTO(Booking booking) {
        if (booking == null)
            return null;

        BookingResponseDTO.BookingResponseDTOBuilder builder = BookingResponseDTO.builder()
                .bookingId(booking.getBookingId())
                .location(booking.getLocation())
                .specialRequest(booking.getSpecialRequest())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime());

        // Service info
        if (booking.getService() != null) {
            com.example.ai_travel_agent_app.model.Service service = booking.getService();
            builder.serviceId(service.getServiceId())
                    .serviceName(service.getServiceName())
                    .servicePrice(service.getPrice());
        }

        // Customer info
        if (booking.getCustomer() != null) {
            Customer customer = booking.getCustomer();
            String customerName = customer.getUser() != null ? customer.getUser().getRealUserName() : "Khách hàng";
            if (customerName == null || customerName.trim().isEmpty()) {
                customerName = "Khách hàng";
            }
            builder.customerId(customer.getId())
                    .customerName(customerName)
                    .customerPhone(customer.getPhoneNumber());
        }

        // Worker info
        if (booking.getWorker() != null) {
            Worker worker = booking.getWorker();
            String workerName = worker.getFullName();
            if (workerName == null || workerName.trim().isEmpty()) {
                // Fallback to user's real name if worker fullName is empty
                workerName = worker.getUser() != null ? worker.getUser().getRealUserName() : "Chưa cập nhật tên";
            }
            builder.workerId(worker.getId())
                    .workerName(workerName)
                    .workerPhone(worker.getPhoneNumber());
        }

        return builder.build();
    }

    public BookingDetailResponseDTO toBookingDetailResponseDTO(Booking booking) {
        if (booking == null)
            return null;

        BookingDetailResponseDTO.BookingDetailResponseDTOBuilder builder = BookingDetailResponseDTO.builder()
                .bookingId(booking.getBookingId())
                .location(booking.getLocation())
                .specialRequest(booking.getSpecialRequest())
                .totalPrice(booking.getTotalPrice())
                .originalPrice(booking.getOriginalPrice())
                .discountAmount(booking.getDiscountAmount())
                .status(booking.getStatus())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .createdAt(booking.getCreatedAt());

        // Worker info
        if (booking.getWorker() != null) {
            Worker worker = booking.getWorker();
            String workerName = worker.getFullName();
            if (workerName == null || workerName.trim().isEmpty()) {
                // Fallback to user's real name if worker fullName is empty
                workerName = worker.getUser() != null ? worker.getUser().getRealUserName() : "Chưa cập nhật tên";
            }
            builder.workerId(worker.getId())
                    .workerName(workerName)
                    .workerPhone(worker.getPhoneNumber())
                    .workerAvatar(worker.getUser() != null ? worker.getUser().getAvatar() : null);
        }

        // Customer info
        if (booking.getCustomer() != null) {
            Customer customer = booking.getCustomer();
            String customerName = customer.getUser() != null ? customer.getUser().getRealUserName() : "Khách hàng";
            if (customerName == null || customerName.trim().isEmpty()) {
                customerName = "Khách hàng";
            }
            builder.customerId(customer.getId())
                    .customerName(customerName)
                    .customerPhone(customer.getPhoneNumber());
        }

        // Service info
        if (booking.getService() != null) {
            com.example.ai_travel_agent_app.model.Service service = booking.getService();
            builder.serviceId(service.getServiceId())
                    .serviceName(service.getServiceName())
                    .serviceDescription(service.getServiceDescription())
                    .servicePrice(service.getPrice());
        }

        // Discount info
        if (booking.getDiscount() != null) {
            Discount discount = booking.getDiscount();
            builder.voucherCode(discount.getCode())
                    .voucherDescription(discount.getDescription());
        }

        return builder.build();
    }

    @Override
    public List<BookingDetailResponseDTO> getWorkerBookings(String workerEmail) {
        Worker worker = workerService.getWorkerByEmail(workerEmail);

        List<Booking> bookings = bookingRepository.findAllByWorkerOrderByCreatedAtDesc(worker);
        return bookings.stream()
                .map(this::toBookingDetailResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BookingDetailResponseDTO getWorkerBookingDetail(String workerEmail, Long bookingId) {
        Worker worker = workerService.getWorkerByEmail(workerEmail);

        Booking booking = bookingRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Kiểm tra booking có thuộc về worker này không
        if (!booking.getWorker().getId().equals(worker.getId())) {
            throw new RuntimeException("You don't have permission to view this booking");
        }

        return toBookingDetailResponseDTO(booking);
    }

    @Override
    @Transactional
    public BookingDetailResponseDTO updateBookingStatus(String workerEmail, Long bookingId,
            com.example.ai_travel_agent_app.dto.booking.BookingStatusUpdateRequestDTO request) {
        Worker worker = workerService.getWorkerByEmail(workerEmail);

        Booking booking = bookingRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Kiểm tra booking có thuộc về worker này không
        if (!booking.getWorker().getId().equals(worker.getId())) {
            throw new RuntimeException("You don't have permission to update this booking");
        }

        // Chỉ có thể update từ PENDING
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Can only update status of pending bookings");
        }

        // Cập nhật status
        if ("CONFIRM".equals(request.getAction())) {
            booking.setStatus(BookingStatus.CONFIRMED);
        } else if ("REJECT".equals(request.getAction())) {
            booking.setStatus(BookingStatus.REJECTED);

            // Giải phóng schedule khi reject
            if (booking.getSchedule() != null) {
                Schedule schedule = booking.getSchedule();
                schedule.setStatus(ScheduleStatus.FREE);
                scheduleRepository.save(schedule);
            }
        } else {
            throw new RuntimeException("Invalid action. Must be CONFIRM or REJECT");
        }

        booking.setUpdatedAt(LocalDateTime.now());
        booking = bookingRepository.save(booking);

        // Gửi email thông báo cho customer về trạng thái booking
        try {
            if ("CONFIRM".equals(request.getAction())) {
                emailService.sendBookingAcceptedToCustomer(booking);
            } else if ("REJECT".equals(request.getAction())) {
                String reason = request.getReason() != null ? request.getReason() : "Không có lý do cụ thể";
                emailService.sendBookingRejectedToCustomer(booking, reason);
            }
        } catch (Exception e) {
            // Log lỗi nhưng không dừng quá trình cập nhật
            System.err.println("Failed to send booking status email: " + e.getMessage());
        }

        return toBookingDetailResponseDTO(booking);
    }

    @Override
    @Transactional
    public BookingDetailResponseDTO completeBooking(String workerEmail, Long bookingId) {
        Worker worker = workerService.getWorkerByEmail(workerEmail);

        Booking booking = bookingRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Kiểm tra booking có thuộc về worker này không
        if (!booking.getWorker().getId().equals(worker.getId())) {
            throw new RuntimeException("You don't have permission to complete this booking");
        }

        // Chỉ có thể complete từ CONFIRMED hoặc IN_PROGRESS
        if (booking.getStatus() != BookingStatus.CONFIRMED && booking.getStatus() != BookingStatus.IN_PROGRESS) {
            throw new RuntimeException("Can only complete confirmed or in-progress bookings");
        }

        booking.setStatus(BookingStatus.COMPLETED);

        // Cập nhật schedule thành COMPLETED
        if (booking.getSchedule() != null) {
            Schedule schedule = booking.getSchedule();
            schedule.setStatus(ScheduleStatus.COMPLETED);
            scheduleRepository.save(schedule);
        }

        booking.setUpdatedAt(LocalDateTime.now());
        booking = bookingRepository.save(booking);

        return toBookingDetailResponseDTO(booking);
    }

    @Override
    @Transactional
    public BookingDetailResponseDTO cancelBooking(String customerEmail, Long bookingId) {
        // Validate customer
        User customerUser = userService.findByEmail(customerEmail);
        Customer customer = customerRepository.findByUser(customerUser)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Booking booking = bookingRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Kiểm tra booking có thuộc về customer này không
        if (!booking.getCustomer().getId().equals(customer.getId())) {
            throw new RuntimeException("You don't have permission to cancel this booking");
        }

        // Chỉ có thể hủy booking ở trạng thái PENDING hoặc CONFIRMED
        if (booking.getStatus() != BookingStatus.PENDING && booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new RuntimeException("Can only cancel pending or confirmed bookings");
        }

        booking.setStatus(BookingStatus.CANCELLED);

        // Giải phóng schedule khi cancel
        if (booking.getSchedule() != null) {
            Schedule schedule = booking.getSchedule();
            schedule.setStatus(ScheduleStatus.FREE);
            scheduleRepository.save(schedule);
        }

        booking.setUpdatedAt(LocalDateTime.now());
        booking = bookingRepository.save(booking);

        return toBookingDetailResponseDTO(booking);
    }
}
