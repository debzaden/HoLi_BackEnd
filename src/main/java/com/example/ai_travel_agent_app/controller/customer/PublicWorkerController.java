package com.example.ai_travel_agent_app.controller.customer;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ai_travel_agent_app.dto.customer.WorkerCardDTO;
import com.example.ai_travel_agent_app.dto.customer.WorkerSearchCriteria;
import com.example.ai_travel_agent_app.service.customer.PublicWorkerService;

@RestController
@RequestMapping("/public")
public class PublicWorkerController {

    @Autowired
    private PublicWorkerService publicWorkerService;

    @GetMapping("/workers/search")
    public ResponseEntity<Page<WorkerCardDTO>> searchWorkers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Float minPrice,
            @RequestParam(required = false) Float maxPrice,
            @RequestParam(required = false) Integer minRating,
            @RequestParam(required = false) String experience,
            @RequestParam(required = false) String availableDate,
            @RequestParam(required = false) String timeSlot,
            @RequestParam(defaultValue = "rating") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort sort = sortDirection.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        WorkerSearchCriteria criteria = WorkerSearchCriteria.builder()
                .keyword(keyword)
                .location(location)
                .category(category)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .minRating(minRating)
                .experience(experience)
                .availableDate(availableDate)
                .timeSlot(timeSlot)
                .build();

        Page<WorkerCardDTO> workers = publicWorkerService.searchWorkers(criteria, pageable);
        return ResponseEntity.ok(workers);
    }

    @GetMapping("/workers")
    public ResponseEntity<List<WorkerCardDTO>> getActiveWorkers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String service,
            @RequestParam(required = false) String priceRange,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort sort = sortDirection.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<WorkerCardDTO> workers = publicWorkerService.getActiveWorkers(
                pageable, location, service, priceRange);

        return ResponseEntity.ok(workers.getContent());
    }

    @GetMapping("/workers/{workerId}")
    public ResponseEntity<WorkerCardDTO> getWorkerDetail(@PathVariable Long workerId) {
        WorkerCardDTO worker = publicWorkerService.getWorkerDetail(workerId);
        return ResponseEntity.ok(worker);
    }

    @GetMapping("/workers/featured")
    public ResponseEntity<List<WorkerCardDTO>> getFeaturedWorkers() {
        List<WorkerCardDTO> workers = publicWorkerService.getFeaturedWorkers();
        return ResponseEntity.ok(workers);
    }

    @GetMapping("/workers/top")
    public ResponseEntity<List<WorkerCardDTO>> getTopWorkers() {
        List<WorkerCardDTO> workers = publicWorkerService.getTopWorkers();
        return ResponseEntity.ok(workers);
    }

    @GetMapping("/workers/{workerId}/services")
    public ResponseEntity<List<Object>> getWorkerServices(@PathVariable Long workerId) {
        List<Object> services = publicWorkerService.getWorkerServices(workerId)
                .stream()
                .map(service -> (Object) service) // Convert to Object
                .collect(Collectors.toList());
        return ResponseEntity.ok(services);
    }
}
