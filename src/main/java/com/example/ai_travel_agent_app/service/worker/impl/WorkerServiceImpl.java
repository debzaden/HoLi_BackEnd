package com.example.ai_travel_agent_app.service.worker.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.example.ai_travel_agent_app.model.*;
import com.example.ai_travel_agent_app.repository.BookingRepository;

import com.example.ai_travel_agent_app.repository.ReviewRepository;
import com.example.ai_travel_agent_app.repository.worker.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ai_travel_agent_app.dto.service.ServiceResponseDTO;
import com.example.ai_travel_agent_app.dto.worker.CerResponse;
import com.example.ai_travel_agent_app.dto.worker.UpdateProfileRequest;
import com.example.ai_travel_agent_app.dto.worker.VerifyIdentityRequest;
import com.example.ai_travel_agent_app.dto.worker.WorkerProfileResponse;
import com.example.ai_travel_agent_app.repository.UserRepository;
import com.example.ai_travel_agent_app.repository.worker.WorkerRepository;
import com.example.ai_travel_agent_app.service.CloudinaryService;
import com.example.ai_travel_agent_app.service.NotificationService;
import com.example.ai_travel_agent_app.service.ServiceService;
import com.example.ai_travel_agent_app.service.worker.WorkerCertificateService;
import com.example.ai_travel_agent_app.service.worker.WorkerService;

@Service
public class WorkerServiceImpl implements WorkerService {

    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkerCertificateService workerCertificateService;
    @Autowired
    @Lazy
    private ServiceService serviceService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ReviewRepository reviewRepository;


    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    private WalletRepository walletRepository;

    @Override
    public Worker getWorkerByUser(User user) {
        return workerRepository.findFirstByUser(user).orElseThrow(() -> new RuntimeException("Worker not found"));
    }

    // get all

    @Override
    public void verificateIdentity(VerifyIdentityRequest workerData, String email) {
        // upload load status
        try {
            String frontId = cloudinaryService.uploadFile(workerData.getFrontId(), "/Worker");
            String backId = cloudinaryService.uploadFile(workerData.getBackId(), "/Worker");
            String avatar = cloudinaryService.uploadFile(workerData.getAvatar(), "/Worker");

            Worker worker = getWorkerByEmail(email);
            User user = worker.getUser();

            // Update phone number if provided and set PHONE_VERIFIED status
            if (workerData.getPhoneNumber() != null && !workerData.getPhoneNumber().isEmpty()) {
                worker.setPhoneNumber(workerData.getPhoneNumber());
                worker.setPhoneVerifyDate(LocalDate.now());
            }

            if (worker.getStatus() == WorkerStatus.CCCD_VERIFING || worker.getStatus() == WorkerStatus.PHONE_VERIFING) {
                worker.setCCCD(workerData.getCccd());
                worker.setAddress(workerData.getAddress());
                worker.setUpdateDate(LocalDate.now());
                worker.setGender(workerData.getGender());
                worker.setOtherSkill(workerData.getOtherSkill());
                worker.setDescription(workerData.getDescription());
                worker.setStatus(WorkerStatus.PENDING);
                worker.setBackIdImage(backId);
                worker.setFrontIdImage(frontId);
                worker.setBirthDate(workerData.getDateOfBirth());
                worker.setCccdUpdateDate(LocalDate.now());
                workerRepository.save(worker);

                user.setAvatar(avatar);
                userRepository.save(user);

                // Create notification for admin about new worker verification
                String message = "Người làm: <a href='/admin/worker/" + worker.getId()
                        + "' style='color: #16a34a; font-weight: bold;'>" + user.getRealUserName()
                        + "</a> đã gửi xác minh danh tính. Vui lòng kiểm tra và xác thực!"
                        + "<a href='/admin/worker/" + worker.getId() + "' style='color: #1850ab; font-weight: bold;'>"
                        + "Kiểm tra" + "</a>";
                notificationService.createNotificationForAdmin("Xác thực người dùng", message, NotificationType.INFO);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updatePhoneNumber(String userEmail, String phoneNumber) {
        // get worker
        User user = userRepository.findFirstByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
        Worker worker = workerRepository.findFirstByUser(user).orElseThrow(() -> new RuntimeException("Worker not found"));

        if (worker.getStatus() == WorkerStatus.PHONE_VERIFING) {
            worker.setPhoneNumber(phoneNumber);
            worker.setStatus(WorkerStatus.CCCD_VERIFING);
            worker.setUpdateDate(LocalDate.now());
            worker.setPhoneVerifyDate(LocalDate.now());
            workerRepository.save(worker);
        }
    }

    @Override
    public Worker getWorkerByEmail(String userEmail) {
        User user = userRepository.findFirstByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
        Worker worker = workerRepository.findFirstByUser(user).orElseThrow(() -> new RuntimeException("Worker not found"));
        return worker;
    }

    @Override
    public WorkerProfileResponse getProfile(String workerEmail) {
        Worker worker = getWorkerByEmail(workerEmail);
        return toWorkerProfileResponse(worker);
    }

    @Transactional
    @Override
    public List<WorkerProfileResponse> getAll() {
        List<Worker> workers = workerRepository.findAll();
        List<WorkerProfileResponse> workerProfileResponses = workers.stream().map(this::toWorkerProfileResponse)
                .toList();

        return workerProfileResponses;
    }

    public WorkerProfileResponse toWorkerProfileResponse(Worker worker) {

        List<CerResponse> cerList = workerCertificateService.getCerByWorker(worker);
        List<ServiceResponseDTO> servcieList = serviceService.findAllByWorker(worker.getUser().getEmail());

        WorkerProfileResponse workerProfileResponse = new WorkerProfileResponse();
        workerProfileResponse.setId(worker.getId());
        workerProfileResponse.setName(worker.getUser().getRealUserName());
        workerProfileResponse.setAddress(worker.getAddress());
        workerProfileResponse.setCccd(worker.getCCCD());
        workerProfileResponse.setDateOfBirth(worker.getBirthDate());
        workerProfileResponse.setAvatar(worker.getUser().getAvatar());
        workerProfileResponse.setStatus(worker.getStatus());
        workerProfileResponse.setCccdFrontImage(worker.getFrontIdImage());
        workerProfileResponse.setCccdBackImage(worker.getBackIdImage());
        workerProfileResponse.setCertificates(cerList);
        workerProfileResponse.setPhone(worker.getPhoneNumber());
        workerProfileResponse.setEmail(worker.getUser().getEmail());
        workerProfileResponse.setOtherSkill(worker.getOtherSkill());
        workerProfileResponse.setDescription(worker.getDescription());
        workerProfileResponse.setGender(worker.getGender());
        workerProfileResponse.setAddress(worker.getAddress());
        workerProfileResponse.setServices(servcieList);
        workerProfileResponse.setRegisterDate(worker.getUser().getCreatedAt());
        workerProfileResponse.setPhoneVerifyDate(worker.getPhoneVerifyDate());
        workerProfileResponse.setCccdUpdateDate(worker.getCccdUpdateDate());
        workerProfileResponse.setActiveDate(worker.getActiveDate());

        Double rating = reviewRepository.getAverageRatingByWorker(worker);
        workerProfileResponse.setRating(rating);

        List<Booking> bookings = bookingRepository.findAllByWorker(worker);
        int  bookingCount = bookings.size();
        workerProfileResponse.setBookingCount(bookingCount);

        return workerProfileResponse;
    }

    @Override
    @Transactional
    public void updateWorkerStatus(Long workerId, WorkerStatus status, String rejectionReason) {
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found with ID: " + workerId));

        User user = worker.getUser();
        WorkerStatus oldStatus = worker.getStatus();

        // Update worker status
        worker.setStatus(status);
        worker.setUpdateDate(LocalDate.now());

        // If status is ACTIVE and worker was previously PENDING, set verification date
        if (status == WorkerStatus.ACTIVE && oldStatus == WorkerStatus.PENDING) {
            worker.setActiveDate(LocalDate.now());


            // Create notification for worker about approval
            String message = "Xin chúc mừng! Tài khoản của bạn đã được xác thực thành công. Bạn có thể bắt đầu cung cấp dịch vụ ngay bây giờ.";
            notificationService.createNotification(user.getEmail(), "Xác thực tài khoản thành công", message,
                    NotificationType.SUCCESS);
            // create wallet
            Wallet wallet = new Wallet();
            wallet.setBalance(0);
            wallet.setCreatedAt(LocalDateTime.now());
            wallet.setUpdatedAt(LocalDateTime.now());
            wallet.setWorker(worker);
            walletRepository.save(wallet);
        }

        // If status is INACTIVE and there's a rejection reason, store it and notify
        // worker
        if (status == WorkerStatus.INACTIVE && rejectionReason != null && !rejectionReason.trim().isEmpty()) {
            worker.setRejectionReason(rejectionReason);

            // Create notification for worker about rejection
            String message = "Rất tiếc, tài khoản của bạn chưa được xác thực. Lý do: " + rejectionReason;
            notificationService.createNotification(user.getEmail(), "Xác thực tài khoản không thành công", message,
                    NotificationType.ERROR);
        }

        workerRepository.save(worker);
    }

    @Override
    public Worker getWorkerById(Long workerId) {
        return workerRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found with ID: " + workerId));
    }

    @Override
    public void updateProfile(String email, UpdateProfileRequest request) {
        Worker worker = getWorkerByEmail(email);
        worker.setAddress(request.getAddress());
        worker.setDescription(request.getDescription());
        worker.setOtherSkill(request.getOtherSkill());
        worker.setUpdateDate(LocalDate.now());
        workerRepository.save(worker);
    }

    // New methods for AI Agent
    @Override
    public List<Worker> searchWorkersByCategory(String categoryName) {
        return workerRepository.findByServicesCategories_CategoryNameContainingIgnoreCaseAndStatus(
                categoryName, WorkerStatus.ACTIVE);
    }

    @Override
    public List<Worker> searchWorkersByLocation(String location) {
        return workerRepository.findByAddressContainingIgnoreCaseAndStatus(location, WorkerStatus.ACTIVE);
    }

    @Override
    public Worker findById(Long workerId) {
        return getWorkerById(workerId);
    }

}
