package com.example.ai_travel_agent_app.test;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.ai_travel_agent_app.service.admin.CategoryService;
import com.example.ai_travel_agent_app.service.worker.WorkerService;
import com.example.ai_travel_agent_app.model.Worker;
import com.example.ai_travel_agent_app.model.Category;
import com.example.ai_travel_agent_app.tools.CustomerAgentTools;

//@Component
public class DatabaseTestRunner implements CommandLineRunner {

    private final WorkerService workerService;
    private final CategoryService categoryService;
    private final CustomerAgentTools customerAgentTools;

    public DatabaseTestRunner(WorkerService workerService, 
                             CategoryService categoryService, 
                             CustomerAgentTools customerAgentTools) {
        this.workerService = workerService;
        this.categoryService = categoryService;
        this.customerAgentTools = customerAgentTools;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== TESTING DATABASE CONNECTIVITY ===");
        
        // Test CategoryService
        try {
            List<Category> categories = categoryService.getAllCategories();
            System.out.println("Categories found: " + categories.size());
            if (!categories.isEmpty()) {
                System.out.println("First category: " + categories.get(0).getCategoryName());
            }
        } catch (Exception e) {
            System.err.println("Error getting categories: " + e.getMessage());
        }

        // Test WorkerService - search by category
        try {
            List<Worker> workers = workerService.searchWorkersByCategory("dọn dẹp");
            System.out.println("Workers found for 'dọn dẹp': " + workers.size());
            if (!workers.isEmpty()) {
                Worker firstWorker = workers.get(0);
                System.out.println("First worker: " + firstWorker.getUser().getRealUserName());
                System.out.println("Worker address: " + firstWorker.getAddress());
            }
        } catch (Exception e) {
            System.err.println("Error searching workers by category: " + e.getMessage());
        }

        // Test WorkerService - search by location
        try {
            List<Worker> workers = workerService.searchWorkersByLocation("Hà Nội");
            System.out.println("Workers found in 'Hà Nội': " + workers.size());
        } catch (Exception e) {
            System.err.println("Error searching workers by location: " + e.getMessage());
        }

        // Test CustomerAgentTools
        try {
            String categoryResult = customerAgentTools.getServiceCategories().apply(null);
            System.out.println("AI Tools getServiceCategories result length: " + categoryResult.length());
            System.out.println("Contains HTML: " + categoryResult.contains("<div"));
        } catch (Exception e) {
            System.err.println("Error testing AI tools: " + e.getMessage());
        }

        try {
            CustomerAgentTools.SearchWorkersByCategoryRequest request = 
                new CustomerAgentTools.SearchWorkersByCategoryRequest("dọn dẹp");
            String workerResult = customerAgentTools.searchWorkersByCategory().apply(request);
            System.out.println("AI Tools searchWorkersByCategory result length: " + workerResult.length());
            System.out.println("Contains HTML: " + workerResult.contains("<div"));
        } catch (Exception e) {
            System.err.println("Error testing worker search: " + e.getMessage());
        }

        System.out.println("=== DATABASE TEST COMPLETED ===");
    }
}