package com.example.ai_travel_agent_app.service.customer.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.ai_travel_agent_app.dto.customer.WorkerCardDTO;
import com.example.ai_travel_agent_app.dto.customer.WorkerSearchCriteria;
import com.example.ai_travel_agent_app.dto.service.ServiceResponseDTO;
import com.example.ai_travel_agent_app.model.Category;
import com.example.ai_travel_agent_app.model.Worker;
import com.example.ai_travel_agent_app.model.WorkerStatus;
import com.example.ai_travel_agent_app.repository.ServiceRepository;
import com.example.ai_travel_agent_app.repository.worker.WorkerRepository;
import com.example.ai_travel_agent_app.service.customer.PublicWorkerService;

@Service
public class PublicWorkerServiceImpl implements PublicWorkerService {

    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private ServiceRepository serviceRepository;
    
    @Autowired
    private com.example.ai_travel_agent_app.repository.ReviewRepository reviewRepository;
    
    @Autowired
    private com.example.ai_travel_agent_app.repository.BookingRepository bookingRepository;

    @Override
    public Page<WorkerCardDTO> getActiveWorkers(Pageable pageable, String location, String service, String priceRange) {
        // Sử dụng random order từ database
        List<Worker> allActiveWorkers = workerRepository.findByStatusRandomOrder(WorkerStatus.ACTIVE);

        // Lọc workers có ít nhất 1 service active
        List<Worker> workersWithActiveServices = allActiveWorkers.stream()
                .filter(worker -> worker.getServices() != null &&
                        worker.getServices().stream().anyMatch(s -> s.isActive()))
                .collect(Collectors.toList());

        // Áp dụng các filter
        List<WorkerCardDTO> filteredWorkers = workersWithActiveServices.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // Lọc theo location nếu có
        if (location != null && !location.trim().isEmpty() && !location.equalsIgnoreCase("Ngẫu Nhiên")) {
            filteredWorkers = filteredWorkers.stream()
                    .filter(worker -> worker.getLocation() != null &&
                            worker.getLocation().toLowerCase().contains(location.toLowerCase()))
                    .collect(Collectors.toList());
        }

        // Lọc theo service nếu có
        if (service != null && !service.trim().isEmpty() && !service.equalsIgnoreCase("Ngẫu Nhiên")) {
            filteredWorkers = filteredWorkers.stream()
                    .filter(worker -> worker.getServices() != null &&
                            worker.getServices().stream()
                                    .anyMatch(s -> s.toLowerCase().contains(service.toLowerCase())))
                    .collect(Collectors.toList());
        }

        // Lọc theo priceRange nếu có
        if (priceRange != null && !priceRange.trim().isEmpty() && !priceRange.equalsIgnoreCase("Ngẫu Nhiên")) {
            filteredWorkers = filteredWorkers.stream()
                    .filter(worker -> filterByPriceRange(worker.getPrice(), priceRange))
                    .collect(Collectors.toList());
        }

        // Phân trang
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredWorkers.size());

        if (start > filteredWorkers.size()) {
            return new PageImpl<>(List.of(), pageable, filteredWorkers.size());
        }

        List<WorkerCardDTO> pageContent = filteredWorkers.subList(start, end);

        return new PageImpl<>(pageContent, pageable, filteredWorkers.size());
    }

    @Override
    public WorkerCardDTO getWorkerDetail(Long workerId) {
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found"));

        if (worker.getStatus() != WorkerStatus.ACTIVE) {
            throw new RuntimeException("Worker is not active");
        }

        return convertToDTO(worker);
    }

    @Override
    public List<WorkerCardDTO> getFeaturedWorkers() {
        List<Worker> featuredWorkers = workerRepository.findByStatusAndServicesIsActiveTrueRandomOrder(WorkerStatus.ACTIVE)
                .stream()
                .filter(worker -> worker.getServices() != null &&
                        worker.getServices().stream().anyMatch(s -> s.isActive()))
                .limit(8) // Lấy 8 worker tiêu biểu
                .collect(Collectors.toList());

        return featuredWorkers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkerCardDTO> getTopWorkers() {
        List<Worker> topWorkers = workerRepository.findByStatusRandomOrder(WorkerStatus.ACTIVE)
                .stream()
                .filter(worker -> worker.getServices() != null &&
                        worker.getServices().stream().anyMatch(s -> s.isActive()))
                .limit(6) // Lấy 6 worker top
                .collect(Collectors.toList());

        return topWorkers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceResponseDTO> getWorkerServices(Long workerId) {
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found"));

        if (worker.getStatus() != WorkerStatus.ACTIVE) {
            throw new RuntimeException("Worker is not active");
        }

        // Lấy danh sách service active của worker
        List<com.example.ai_travel_agent_app.model.Service> activeServices = serviceRepository.findAllByWorkerAndIsActiveTrue(worker);

        return activeServices.stream()
                .map(this::convertToServiceResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<WorkerCardDTO> searchWorkers(WorkerSearchCriteria criteria, Pageable pageable) {
        // Lấy tất cả worker có status ACTIVE
        List<Worker> allActiveWorkers = workerRepository.findByStatus(WorkerStatus.ACTIVE);

        // Lọc workers có ít nhất 1 service active
        List<Worker> workersWithActiveServices = allActiveWorkers.stream()
                .filter(worker -> worker.getServices() != null &&
                        worker.getServices().stream().anyMatch(s -> s.isActive()))
                .collect(Collectors.toList());

        // Convert to DTO
        List<WorkerCardDTO> allWorkers = workersWithActiveServices.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // Áp dụng các filter
        List<WorkerCardDTO> filteredWorkers = allWorkers.stream()
                .filter(worker -> matchesSearchCriteria(worker, criteria))
                .collect(Collectors.toList());

        // Phân trang
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredWorkers.size());

        if (start > filteredWorkers.size()) {
            return new PageImpl<>(List.of(), pageable, filteredWorkers.size());
        }

        List<WorkerCardDTO> pageContent = filteredWorkers.subList(start, end);

        return new PageImpl<>(pageContent, pageable, filteredWorkers.size());
    }

    private boolean matchesSearchCriteria(WorkerCardDTO worker, WorkerSearchCriteria criteria) {
        // Tìm kiếm theo từ khóa
        if (criteria.getKeyword() != null && !criteria.getKeyword().trim().isEmpty()) {
            String keyword = criteria.getKeyword().toLowerCase();
            if (!(worker.getName().toLowerCase().contains(keyword) ||
                    (worker.getDescription() != null && worker.getDescription().toLowerCase().contains(keyword)) ||
                    (worker.getServices() != null && worker.getServices().stream()
                            .anyMatch(s -> s.toLowerCase().contains(keyword)))
                    ||
                    (worker.getOtherSkill() != null && worker.getOtherSkill().toLowerCase().contains(keyword)))) {
                return false;
            }
        }

        // Lọc theo địa điểm
        if (criteria.getLocation() != null && !criteria.getLocation().trim().isEmpty()) {
            if (worker.getLocation() == null ||
                    !worker.getLocation().toLowerCase().contains(criteria.getLocation().toLowerCase())) {
                return false;
            }
        }

        // Lọc theo category
        if (criteria.getCategory() != null && !criteria.getCategory().trim().isEmpty()) {
            if (worker.getCategories() == null ||
                    worker.getCategories().stream()
                            .noneMatch(cat -> cat.toLowerCase().contains(criteria.getCategory().toLowerCase()))) {
                return false;
            }
        }

        // Lọc theo giá (min/max)
        if (criteria.getMinPrice() != null) {
            if (worker.getPrice() == null || worker.getPrice() < criteria.getMinPrice()) {
                return false;
            }
        }

        if (criteria.getMaxPrice() != null) {
            if (worker.getPrice() == null || worker.getPrice() > criteria.getMaxPrice()) {
                return false;
            }
        }

        // Lọc theo đánh giá tối thiểu
        if (criteria.getMinRating() != null) {
            if (worker.getRating() == null || worker.getRating() < criteria.getMinRating()) {
                return false;
            }
        }

        // Lọc theo kinh nghiệm
        if (criteria.getExperience() != null && !criteria.getExperience().trim().isEmpty()) {
            if (worker.getExperience() == null ||
                    !worker.getExperience().toLowerCase().contains(criteria.getExperience().toLowerCase())) {
                return false;
            }
        }

        // TODO: Implement availableDate and timeSlot filtering when schedule system is
        // ready

        return true;
    }

    private WorkerCardDTO convertToDTO(Worker worker) {
        if (worker == null) {
            return null;
        }

        // Lấy service có giá thấp nhất và active
        Float minPrice = null;
        String primaryService = null;
        List<String> serviceNames = List.of();
        List<String> categories = List.of();

        if (worker.getServices() != null) {
            List<com.example.ai_travel_agent_app.model.Service> activeServices = worker.getServices().stream()
                    .filter(com.example.ai_travel_agent_app.model.Service::isActive)
                    .collect(Collectors.toList());

            if (!activeServices.isEmpty()) {
                minPrice = activeServices.stream()
                        .map(com.example.ai_travel_agent_app.model.Service::getPrice)
                        .min(Float::compare)
                        .orElse(0.0f);

                primaryService = activeServices.get(0).getServiceName();

                serviceNames = activeServices.stream()
                        .map(com.example.ai_travel_agent_app.model.Service::getServiceName)
                        .collect(Collectors.toList());

                categories = activeServices.stream()
                        .flatMap(service -> service.getCategories().stream())
                        .map(Category::getCategoryName)
                        .distinct()
                        .collect(Collectors.toList());
            }
        }

        return WorkerCardDTO.builder()
                .workerId(worker.getId())
                .name(worker.getUser().getRealUserName())
                .title(primaryService != null ? primaryService : "Dịch vụ đa dạng")
                .location(worker.getAddress() != null ? extractCityFromAddress(worker.getAddress()) : "Đà Nẵng")
                .image(worker.getUser().getAvatar())
                .avatar(worker.getUser().getAvatar())
                .birthYear(worker.getBirthDate() != null ? worker.getBirthDate().getYear() : null)
                .schedule("Thứ 2 – Thứ 6, 08:00 – 17:00") // Mặc định, sau này có thể lấy từ schedule
                .price(minPrice)
                .service(primaryService)
                .description(worker.getDescription())
                .otherSkill(worker.getOtherSkill()) // Thêm trường otherSkill
                .services(serviceNames)
                .rating(4.5f) // Mặc định, sau này có thể tính từ reviews
                .reviewCount(15) // Mặc định, sau này có thể tính từ reviews
                .isPro(false) // Mặc định, sau này có thể có logic pro
                .jobsDone(10) // Mặc định, sau này có thể tính từ bookings
                .gender(worker.getGender())
                .phoneNumber(worker.getPhoneNumber())
                .email(worker.getUser().getEmail())
                .createdAt(worker.getUpdateDate())
                .categories(categories)
                .isActive(true)
                .experience(worker.getOtherSkill())
                .rating(calculateAverageRating(worker))
                .reviewCount(calculateReviewCount(worker))
                .jobsDone(calculateCompletedJobs(worker))
                .build();
    }
    
    private Float calculateAverageRating(Worker worker) {
        List<com.example.ai_travel_agent_app.model.Booking> bookings = bookingRepository.findAllByWorker(worker);
        if (bookings.isEmpty()) {
            return 0.0f;
        }
        
        List<com.example.ai_travel_agent_app.model.Review> reviews = bookings.stream()
            .map(booking -> reviewRepository.findByBooking(booking))
            .filter(java.util.Optional::isPresent)
            .map(java.util.Optional::get)
            .collect(Collectors.toList());
            
        if (reviews.isEmpty()) {
            return 0.0f;
        }
        
        double avgRating = reviews.stream()
            .mapToInt(com.example.ai_travel_agent_app.model.Review::getRating)
            .average()
            .orElse(0.0);
            
        return (float) avgRating;
    }
    
    private Integer calculateReviewCount(Worker worker) {
        List<com.example.ai_travel_agent_app.model.Booking> bookings = bookingRepository.findAllByWorker(worker);
        if (bookings.isEmpty()) {
            return 0;
        }
        
        return (int) bookings.stream()
            .map(booking -> reviewRepository.findByBooking(booking))
            .filter(java.util.Optional::isPresent)
            .count();
    }
    
    private Integer calculateCompletedJobs(Worker worker) {
        List<com.example.ai_travel_agent_app.model.Booking> bookings = bookingRepository.findAllByWorker(worker);
        return (int) bookings.stream()
            .filter(b -> b.getStatus() == com.example.ai_travel_agent_app.model.BookingStatus.COMPLETED)
            .count();
    }

    private String extractCityFromAddress(String address) {
        // Đơn giản hóa: tách lấy phần cuối của địa chỉ
        if (address == null || address.trim().isEmpty()) {
            return "Đà Nẵng";
        }

        String[] parts = address.split(",");
        if (parts.length > 0) {
            String lastPart = parts[parts.length - 1].trim();
            return lastPart.isEmpty() ? "Đà Nẵng" : lastPart;
        }

        return "Đà Nẵng";
    }

    private boolean filterByPriceRange(Float price, String priceRange) {
        if (price == null) {
            return false;
        }

        switch (priceRange) {
            case "Dưới 100k":
                return price < 100000;
            case "100k - 150k":
                return price >= 100000 && price <= 150000;
            case "150k - 200k":
                return price > 150000 && price <= 200000;
            case "200k - 300k":
                return price > 200000 && price <= 300000;
            case "Trên 300k":
                return price > 300000;
            default:
                return true;
        }
    }

    private ServiceResponseDTO convertToServiceResponseDTO(com.example.ai_travel_agent_app.model.Service service) {
        ServiceResponseDTO dto = new ServiceResponseDTO();
        dto.setServiceId(service.getServiceId());
        dto.setServiceName(service.getServiceName());
        dto.setTitle(service.getServiceName()); // Thêm trường title
        dto.setServiceDescription(service.getServiceDescription());
        dto.setPrice(service.getPrice());
        dto.setExperience(service.getExperience());
        dto.setActive(service.isActive());

        // Convert categories
        if (service.getCategories() != null) {
            dto.setCategories(service.getCategories().stream()
                    .map(category -> {
                        var categoryDTO = new com.example.ai_travel_agent_app.dto.category.CategoryResponseDTO();
                        categoryDTO.setCategoryId(category.getCategoryId());
                        categoryDTO.setCategoryName(category.getCategoryName());
                        return categoryDTO;
                    })
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
