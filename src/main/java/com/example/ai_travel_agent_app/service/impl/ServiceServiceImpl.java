package com.example.ai_travel_agent_app.service.impl;


import com.example.ai_travel_agent_app.dto.category.CategoryResponseDTO;
import com.example.ai_travel_agent_app.dto.service.ServiceRequestDTO;
import com.example.ai_travel_agent_app.dto.service.ServiceResponseDTO;
import com.example.ai_travel_agent_app.model.Category;
import com.example.ai_travel_agent_app.model.NotificationType;
import com.example.ai_travel_agent_app.model.User;
import com.example.ai_travel_agent_app.model.Worker;
import com.example.ai_travel_agent_app.repository.CategoryRepository;
import com.example.ai_travel_agent_app.repository.ServiceRepository;
import com.example.ai_travel_agent_app.repository.UserRepository;
import com.example.ai_travel_agent_app.repository.worker.WorkerRepository;
import com.example.ai_travel_agent_app.service.CloudinaryService;
import com.example.ai_travel_agent_app.service.NotificationService;
import com.example.ai_travel_agent_app.service.ServiceService;
import com.example.ai_travel_agent_app.service.admin.CategoryService;
import com.example.ai_travel_agent_app.service.worker.WorkerService;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ServiceServiceImpl implements ServiceService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceServiceImpl.class);

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    @Lazy
    private WorkerService workerService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private NotificationService notificationService;


    @Override
    public ServiceResponseDTO insert(String userEmail, ServiceRequestDTO dto) {

        User user = userRepository.findFirstByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
        Worker worker = workerRepository.findFirstByUser(user).orElseThrow(() -> new RuntimeException("Worker not found"));

        List<Category> categories = categoryRepository.findByCategoryIdIn(List.of(dto.getCategories()));
        com.example.ai_travel_agent_app.model.Service service = new com.example.ai_travel_agent_app.model.Service();

        service.setServiceName(dto.getServiceName());
        service.setServiceDescription(dto.getServiceDescription());
        service.setActive(false);
        service.setWorker(worker);
        service.setPrice(dto.getPrice());
        service.setCreatedAt(LocalDate.now());
        service.setExperience(dto.getExperience());
        service.setCategories(new HashSet<>(categories));

        com.example.ai_travel_agent_app.model.Service newService = serviceRepository.save(service);

        // send notification to admin
        String message = "Người làm: <a href='/admin/worker/" + worker.getId() + "' style='color: #16a34a; font-weight: bold;'>" + user.getRealUserName() + "</a> đã tạo một dịch vụ mới. Vui lòng kiểm tra và xác thực!"
                + "<a href='/admin/worker/" + worker.getId() + "' style='color: #1850ab; font-weight: bold;'>" + " Kiểm tra" + "</a>";
        notificationService.createNotificationForAdmin("Xác thực dịch vụ", message, NotificationType.INFO);

        return toResponseDTO(newService);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceResponseDTO> findAll() {
        return serviceRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ServiceResponseDTO findByServiceId(Long id) {
        Optional<com.example.ai_travel_agent_app.model.Service> service = serviceRepository.findByServiceId(id);
        return toResponseDTO(service.get());
    }

    @Transactional
    @Override
    public ServiceResponseDTO update(Long id, ServiceRequestDTO dto) {

        com.example.ai_travel_agent_app.model.Service service = serviceRepository.findByServiceId(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        List<Category> categories = categoryRepository.findByCategoryIdIn(List.of(dto.getCategories()));
        service.setCategories(new HashSet<>(categories));

        // Cập nhật nếu có giá trị mới
        if (dto.getServiceName() != null && !dto.getServiceName().isEmpty()) {
            service.setServiceName(dto.getServiceName());
        }

        if (dto.getServiceDescription() != null && !dto.getServiceDescription().isEmpty()) {
            service.setServiceDescription(dto.getServiceDescription());
        }

        if (dto.getExperience() != null && !dto.getExperience().isEmpty()) {
            service.setExperience(dto.getExperience());
        }

        // Float nếu mặc định là 0.0, bạn có thể thêm điều kiện nếu cần
        if (dto.getPrice() > 0) {
            service.setPrice(dto.getPrice());
        }

        com.example.ai_travel_agent_app.model.Service updated = serviceRepository.save(service);
        return toResponseDTO(updated);
    }

    @Transactional
    @Override
    public boolean delete(Long id) {
        if (!serviceRepository.existsByServiceId(id)) {
            return false;
        }
        serviceRepository.deleteByServiceId(id);
        return true;
    }

    @Transactional
    @Override
    public List<ServiceResponseDTO> findAllByWorker(String userEmail) {
        Worker worker = workerService.getWorkerByEmail(userEmail);

        return serviceRepository.findAllByWorker(worker).stream().
                map(this::toResponseDTO).
                collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateStatusService(Long serviceId) {
        com.example.ai_travel_agent_app.model.Service service = serviceRepository.findByServiceId(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));
        boolean curretnActive = service.isActive();
        logger.info("Toggling service {} current active={}", serviceId, curretnActive);
        service.setActive(!curretnActive);

        String userEmail = service.getWorker().getUser().getEmail();

        if (curretnActive) {
            // active -> unactive
            String message = service.getServiceName() + " của bạn đã không được phê duyệt";
            notificationService.createNotification(userEmail, "Dịch vụ bi khóa", message, NotificationType.WARNING);
        } else {
            // unactive -> active
            String message = service.getServiceName() + " của bạn đã được phê duyệt";
            notificationService.createNotification(userEmail, "Dịch vụ đã được duyệt", message, NotificationType.SUCCESS);
        }
        com.example.ai_travel_agent_app.model.Service saved = serviceRepository.save(service);
        logger.info("Service {} new active={}", serviceId, saved.isActive());

    }

    public ServiceResponseDTO toResponseDTO(com.example.ai_travel_agent_app.model.Service service) {
        ServiceResponseDTO serviceResponseDTO = new ServiceResponseDTO();

        serviceResponseDTO.setServiceId(service.getServiceId());
        serviceResponseDTO.setServiceName(service.getServiceName());
        serviceResponseDTO.setServiceDescription(service.getServiceDescription());
        serviceResponseDTO.setActive(service.isActive());
        serviceResponseDTO.setPrice(service.getPrice());
        serviceResponseDTO.setExperience(service.getExperience());
        List<CategoryResponseDTO> categories = new ArrayList<>();

        Set<Category> categorySet = service.getCategories();
        for (Category category : categorySet) {
            categories.add(categoryService.toDTO(category));
        }
        serviceResponseDTO.setCategories(categories);

        return serviceResponseDTO;
    }


}
