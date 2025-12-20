package com.example.ai_travel_agent_app.tools;

import java.util.List;
import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import com.example.ai_travel_agent_app.model.Worker;
import com.example.ai_travel_agent_app.model.Category;
import com.example.ai_travel_agent_app.service.worker.WorkerService;
import com.example.ai_travel_agent_app.service.admin.CategoryService;

@Component
public class CustomerAgentTools {

    private final WorkerService workerService;
    private final CategoryService categoryService;

    // Request records for function parameters
    public record SearchWorkersByCategoryRequest(String categoryName) {}
    public record SearchWorkersByLocationRequest(String location) {}

    public CustomerAgentTools(WorkerService workerService, CategoryService categoryService) {
        this.workerService = workerService;
        this.categoryService = categoryService;
    }

    @Bean("searchWorkersByCategory")
    @Description("Search for workers by service category")
    public Function<SearchWorkersByCategoryRequest, String> searchWorkersByCategory() {
        return request -> {
            try {
                List<Worker> workers = workerService.searchWorkersByCategory(request.categoryName());
                
                if (workers.isEmpty()) {
                    return generateWorkerHTML("Không tìm thấy worker nào cho danh mục: " + request.categoryName(), workers);
                }

                return generateWorkerHTML("Tìm thấy " + workers.size() + " worker cho danh mục '" + request.categoryName() + "':", workers);
                
            } catch (Exception e) {
                return "❌ Lỗi khi tìm kiếm worker: " + e.getMessage();
            }
        };
    }

    @Bean("searchWorkersByLocation")
    @Description("Search for workers by location")
    public Function<SearchWorkersByLocationRequest, String> searchWorkersByLocation() {
        return request -> {
            try {
                List<Worker> workers = workerService.searchWorkersByLocation(request.location());
                
                if (workers.isEmpty()) {
                    return generateWorkerHTML("Không tìm thấy worker nào tại khu vực: " + request.location(), workers);
                }

                return generateWorkerHTML("Tìm thấy " + workers.size() + " worker tại khu vực '" + request.location() + "':", workers);
                
            } catch (Exception e) {
                return "❌ Lỗi khi tìm kiếm worker: " + e.getMessage();
            }
        };
    }

    @Bean("getServiceCategories")
    @Description("Get all service categories")
    public Function<Void, String> getServiceCategories() {
        return request -> {
            try {
                List<Category> categories = categoryService.getAllCategories();
                
                StringBuilder html = new StringBuilder();
                html.append("<div class='category-grid'>");
                html.append("<h3 style='color: #374151; margin-bottom: 16px; font-weight: 600;'>📋 Danh mục dịch vụ có sẵn:</h3>");
                html.append("<div class='grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4'>");
                
                for (Category category : categories) {
                    html.append("<div class='category-card bg-white p-4 rounded-lg border border-gray-200 hover:shadow-md transition-shadow cursor-pointer' ");
                    html.append("onclick=\"window.setMessage('Tìm kiếm worker danh mục ").append(category.getCategoryName()).append("')\">");
                    html.append("<h4 class='font-medium text-purple-700 mb-2'>").append(category.getCategoryName()).append("</h4>");
                    if (category.getCategoryDescription() != null) {
                        html.append("<p class='text-sm text-gray-600'>").append(category.getCategoryDescription()).append("</p>");
                    }
                    html.append("</div>");
                }
                
                html.append("</div>");
                html.append("</div>");
                
                return html.toString();
                
            } catch (Exception e) {
                return "❌ Lỗi khi lấy danh sách danh mục: " + e.getMessage();
            }
        };
    }

    /**
     * Generate HTML for worker list display
     */
    private String generateWorkerHTML(String title, List<Worker> workers) {
        StringBuilder html = new StringBuilder();
        
        html.append("<div class='worker-results'>");
        html.append("<h3 style='color: #374151; margin-bottom: 16px; font-weight: 600;'>").append(title).append("</h3>");
        
        if (workers.isEmpty()) {
            html.append("<p class='text-gray-500'>Hiện tại chưa có worker nào phù hợp với yêu cầu của bạn.</p>");
            html.append("<p class='text-sm text-gray-400 mt-2'>💡 Thử tìm kiếm với từ khóa khác hoặc mở rộng khu vực tìm kiếm.</p>");
        } else {
            html.append("<div class='worker-grid grid grid-cols-1 md:grid-cols-2 gap-6 mt-4'>");
            
            for (Worker worker : workers) {
                html.append(generateWorkerCard(worker));
            }
            
            html.append("</div>");
        }
        
        html.append("</div>");
        
        return html.toString();
    }

    /**
     * Generate HTML for individual worker card
     */
    private String generateWorkerCard(Worker worker) {
        StringBuilder card = new StringBuilder();
        
        card.append("<div class='worker-card bg-white rounded-lg border border-gray-200 overflow-hidden hover:shadow-lg transition-all duration-300'>");
        
        // Header with avatar and basic info
        card.append("<div class='p-4 border-b border-gray-100'>");
        card.append("<div class='flex items-center space-x-3'>");
        
        // Avatar
        String avatarUrl = worker.getUser().getAvatar();
        if (avatarUrl == null || avatarUrl.isEmpty()) {
            avatarUrl = "https://images.unsplash.com/photo-1494790108755-2616c27c26ac?w=150&h=150&fit=crop&crop=face";
        }
        card.append("<img src='").append(avatarUrl).append("' alt='Avatar' class='w-12 h-12 rounded-full object-cover'>");
        
        // Name and basic info
        card.append("<div class='flex-1'>");
        card.append("<h4 class='font-semibold text-gray-800'>").append(worker.getUser().getRealUserName()).append("</h4>");
        
        if (worker.getAddress() != null) {
            card.append("<p class='text-sm text-gray-500 flex items-center mt-1'>");
            card.append("<svg class='w-4 h-4 mr-1' fill='none' stroke='currentColor' viewBox='0 0 24 24'>");
            card.append("<path stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z'></path>");
            card.append("<path stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M15 11a3 3 0 11-6 0 3 3 0 016 0z'></path>");
            card.append("</svg>");
            card.append(worker.getAddress());
            card.append("</p>");
        }
        
        card.append("</div>");
        card.append("</div>");
        card.append("</div>");
        
        // Worker details
        card.append("<div class='p-4'>");
        
        // Description
        if (worker.getDescription() != null && !worker.getDescription().isEmpty()) {
            card.append("<p class='text-sm text-gray-600 mb-3 line-clamp-2'>").append(worker.getDescription()).append("</p>");
        }
        
        // Skills
        if (worker.getOtherSkill() != null && !worker.getOtherSkill().isEmpty()) {
            card.append("<div class='mb-3'>");
            card.append("<span class='text-xs font-medium text-purple-600 bg-purple-50 px-2 py-1 rounded'>Kỹ năng: ").append(worker.getOtherSkill()).append("</span>");
            card.append("</div>");
        }
        
        // Action buttons
        card.append("<div class='flex space-x-2 mt-4'>");
        card.append("<button onclick=\"window.setMessage('Xem chi tiết worker ").append(worker.getId()).append("')\" ");
        card.append("class='flex-1 bg-purple-600 hover:bg-purple-700 text-white text-sm font-medium py-2 px-4 rounded-lg transition-colors'>");
        card.append("Xem chi tiết");
        card.append("</button>");
        card.append("<button onclick=\"window.setMessage('Đặt lịch với worker ").append(worker.getId()).append("')\" ");
        card.append("class='flex-1 bg-green-600 hover:bg-green-700 text-white text-sm font-medium py-2 px-4 rounded-lg transition-colors'>");
        card.append("Đặt lịch");
        card.append("</button>");
        card.append("</div>");
        
        card.append("</div>");
        card.append("</div>");
        
        return card.toString();
    }
}