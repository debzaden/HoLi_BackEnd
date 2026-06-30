package com.example.ai_travel_agent_app.tools;

import java.util.List;
import java.util.function.Function;
import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import com.example.ai_travel_agent_app.model.Worker;
import com.example.ai_travel_agent_app.model.Booking;
import com.example.ai_travel_agent_app.model.Category;
import com.example.ai_travel_agent_app.model.Customer;
import com.example.ai_travel_agent_app.model.PolicyDocument;
import com.example.ai_travel_agent_app.service.worker.WorkerService;
import com.example.ai_travel_agent_app.service.admin.CategoryService;
import com.example.ai_travel_agent_app.service.PolicyService;
import com.example.ai_travel_agent_app.repository.BookingRepository;
import com.example.ai_travel_agent_app.repository.customer.CustomerRepository;

@Component
public class CustomerAgentTools {

    private final WorkerService workerService;
    private final CategoryService categoryService;
    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final PolicyService policyService;

    // Request records for function parameters
    public record SearchWorkersByCategoryRequest(String categoryName) {}
    public record SearchWorkersByLocationRequest(String location) {}
    public record SearchWorkersByServiceAndLocationRequest(String serviceName, String location) {}
    public record GetCustomerBookingsRequest(String username) {}
    public record SearchPolicyInfoRequest(String query) {}
    public record ParseBookingRequest(String naturalLanguageQuery) {}

    public CustomerAgentTools(WorkerService workerService, 
                            CategoryService categoryService,
                            BookingRepository bookingRepository,
                            CustomerRepository customerRepository,
                            PolicyService policyService) {
        this.workerService = workerService;
        this.categoryService = categoryService;
        this.bookingRepository = bookingRepository;
        this.customerRepository = customerRepository;
        this.policyService = policyService;
    }

    @Bean("searchWorkersByCategory")
    @Description("Tìm kiếm workers/người làm việc theo TÊN DỊCH VỤ cụ thể mà worker cung cấp. " +
                 "Tool này tìm kiếm trong SERVICE NAME (tên dịch vụ) và SERVICE DESCRIPTION (mô tả dịch vụ) của worker. " +
                 "VD: 'Dọn dẹp nhà cửa', 'Vệ sinh công nghiệp', 'Nấu ăn gia đình', 'Gia sư Toán lớp 10', 'Sửa chữa điện nước', 'Massage thư giãn'. " +
                 "Sử dụng khi khách hỏi về worker cung cấp dịch vụ cụ thể nào đó. " +
                 "Chỉ trả về workers ĐANG HOẠT ĐỘNG và có dịch vụ CHỨA từ khóa tìm kiếm. " +
                 "Input: categoryName - tên dịch vụ hoặc từ khóa liên quan đến dịch vụ cần tìm (tiếng Việt)")
    public Function<SearchWorkersByCategoryRequest, String> searchWorkersByCategory() {
        return request -> {
            try {
                System.out.println("🔧 [TOOL CALLED] searchWorkersByCategory: " + request.categoryName());
                List<Worker> workers = workerService.searchWorkersByCategory(request.categoryName());
                
                if (workers.isEmpty()) {
                    return generateWorkerHTML("Không tìm thấy worker nào cho danh mục: " + request.categoryName(), workers);
                }

                return generateWorkerHTML("Tìm thấy " + workers.size() + " worker cho danh mục '" + request.categoryName() + "':", workers);
                
            } catch (Exception e) {
                System.err.println("❌ Error in searchWorkersByCategory: " + e.getMessage());
                e.printStackTrace();
                return "❌ Lỗi khi tìm kiếm worker: " + e.getMessage();
            }
        };
    }

    @Bean("searchWorkersByLocation")
    @Description("Tìm kiếm workers/người giúp việc/dịch vụ theo địa điểm/khu vực. VD: 'HCM', 'Hà Nội', 'Đà Nẵng' , 'Hồ Chí Minh'. " +
                 "Sử dụng khi khách hỏi về worker ở địa điểm cụ thể. " +
                 "Trả về đúng các worker/người làm việc/giúp việc đúng địa điểm HCM/Hà Nội/Đà Nẵng/Hồ Chí Minh" +
                 "Input: location - tên địa điểm (tiếng Việt)")
    public Function<SearchWorkersByLocationRequest, String> searchWorkersByLocation() {
        return request -> {
            try {
                System.out.println("🔧 [TOOL CALLED] searchWorkersByLocation: " + request.location());
                List<Worker> workers = workerService.searchWorkersByLocation(request.location());
                
                if (workers.isEmpty()) {
                    return generateWorkerHTML("Không tìm thấy worker nào tại khu vực: " + request.location(), workers);
                }

                return generateWorkerHTML("Tìm thấy " + workers.size() + " worker tại khu vực '" + request.location() + "':", workers);
                
            } catch (Exception e) {
                System.err.println("❌ Error in searchWorkersByLocation: " + e.getMessage());
                e.printStackTrace();
                return "❌ Lỗi khi tìm kiếm worker: " + e.getMessage();
            }
        };
    }
    
    @Bean("searchWorkersByServiceAndLocation")
    @Description("Tìm kiếm workers theo CẢ dịch vụ VÀ địa điểm cụ thể. " +
                 "Sử dụng khi khách hỏi về worker cung cấp dịch vụ cụ thể TẠI một địa điểm. " +
                 "VD: 'tìm gia sư ở đà nẵng', 'worker dọn dẹp tại quận 1', 'massage ở hà nội', 'tìm người giúp việc dọn dẹp ở đà nẵng'. " +
                 "Input: serviceName - tên dịch vụ, location - tên địa điểm")
    public Function<SearchWorkersByServiceAndLocationRequest, String> searchWorkersByServiceAndLocation() {
        return request -> {
            try {
                System.out.println("🎯 [TOOL CALLED] searchWorkersByServiceAndLocation: service=" + 
                                 request.serviceName() + ", location=" + request.location());
                List<Worker> workers = workerService.searchWorkersByServiceAndLocation(
                        request.serviceName(), request.location());
                
                if (workers.isEmpty()) {
                    return generateWorkerHTML("Không tìm thấy worker cung cấp dịch vụ '" + request.serviceName() + 
                            "' tại khu vực '" + request.location() + "'", workers);
                }

                return generateWorkerHTML("Tìm thấy " + workers.size() + " worker cung cấp '" + 
                        request.serviceName() + "' tại '" + request.location() + "':", workers);
                
            } catch (Exception e) {
                System.err.println("❌ Error in searchWorkersByServiceAndLocation: " + e.getMessage());
                e.printStackTrace();
                return "❌ Lỗi khi tìm kiếm worker: " + e.getMessage();
            }
        };
    }

    @Bean("getServiceCategories")
    @Description("Lấy tất cả danh mục dịch vụ có sẵn trên hệ thống. " +
                 "Sử dụng khi khách hỏi: 'có dịch vụ gì?', 'danh sách dịch vụ', 'các loại công việc'. " +
                 "Không cần input parameter")
    public Function<Void, String> getServiceCategories() {
        return request -> {
            try {
                System.out.println("🔧 [TOOL CALLED] getServiceCategories");
                List<Category> categories = categoryService.getAllCategories();
                
                if (categories.isEmpty()) {
                    return "<p class='text-gray-500'>Hiện tại chưa có danh mục dịch vụ nào.</p>";
                }
                
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
                System.err.println("❌ Error in getServiceCategories: " + e.getMessage());
                e.printStackTrace();
                return "❌ Lỗi khi lấy danh sách danh mục: " + e.getMessage();
            }
        };
    }
    
    @Bean("searchPolicyInfo")
    @Description("Tìm kiếm thông tin về HoLi, chính sách, điều khoản, FAQ, hướng dẫn sử dụng. " +
                 "Sử dụng khi khách hỏi về: 'HoLi là gì?', 'giới thiệu', 'điều khoản', 'chính sách', 'quy định', " +
                 "'cách đặt lịch', 'cách hủy lịch', 'thanh toán như thế nào', 'bảo mật', 'worker có đáng tin không', " +
                 "'tôi có thể làm worker không', 'xác minh worker', 'đánh giá', 'chất lượng dịch vụ'. " +
                 "Input: query - câu hỏi hoặc từ khóa tìm kiếm")
    public Function<SearchPolicyInfoRequest, String> searchPolicyInfo() {
        return request -> {
            try {
                System.out.println("ℹ️ [TOOL CALLED] searchPolicyInfo: " + request.query());
                
                List<PolicyDocument> policies = policyService.searchPolicies(request.query());
                
                // Format and return HTML
                return policyService.formatPoliciesAsHtml(policies, request.query());
                
            } catch (Exception e) {
                System.err.println("❌ Error in searchPolicyInfo: " + e.getMessage());
                e.printStackTrace();
                return "<div class='p-4 bg-red-50 border-l-4 border-red-400 text-red-800'>" +
                       "<p class='font-semibold'>❌ Lỗi khi tìm kiếm thông tin</p>" +
                       "<p class='text-sm mt-1'>Xin lỗi, có lỗi xảy ra khi tìm kiếm thông tin. Vui lòng thử lại sau.</p>" +
                       "</div>";
            }
        };
    }
    
    @Bean("parseNaturalLanguageBooking")
    @Description("Parse câu đặt lịch bằng ngôn ngữ tự nhiên thành thông tin booking cụ thể. " +
                 "Sử dụng khi khách muốn đặt lịch với ngôn ngữ tự nhiên. " +
                 "VD: 'Đặt lịch dọn dẹp ngày mai lúc 9h', 'Tôi muốn thuê gia sư thứ 7 tuần sau 14h', " +
                 "'Book massage hôm nay 18h', 'Cần worker sửa chữa điện chiều mai'. " +
                 "Tool sẽ tự động parse ra: dịch vụ, ngày, giờ, thời lượng. " +
                 "Input: naturalLanguageQuery - câu đặt lịch bằng tiếng Việt hoặc tiếng Anh")
    public Function<ParseBookingRequest, String> parseNaturalLanguageBooking() {
        return request -> {
            try {
                System.out.println("📅 [TOOL CALLED] parseNaturalLanguageBooking: " + request.naturalLanguageQuery());
                
                String query = request.naturalLanguageQuery().toLowerCase();
                
                // Parse service type
                String service = parseServiceFromQuery(query);
                
                // Parse date and time
                String dateStr = parseDateFromQuery(query);
                String timeStr = parseTimeFromQuery(query);
                
                // Parse duration (default 2 hours if not specified)
                int duration = parseDurationFromQuery(query);
                
                // Generate booking form JSON
                StringBuilder json = new StringBuilder();
                json.append("<div data-booking-info='");
                json.append("{");
                json.append("\"service\":\"").append(escapeJson(service)).append("\",");
                json.append("\"date\":\"").append(dateStr).append("\",");
                json.append("\"time\":\"").append(timeStr).append("\",");
                json.append("\"duration\":").append(duration).append(",");
                json.append("\"originalQuery\":\"").append(escapeJson(request.naturalLanguageQuery())).append("\"");
                json.append("}' class='booking-parse-result'>");
                
                // Generate user-friendly HTML
                json.append("<div class='bg-gradient-to-r from-green-50 to-blue-50 p-4 rounded-lg border-l-4 border-green-500'>");
                json.append("<h3 class='font-bold text-green-700 mb-3'>✅ Đã hiểu yêu cầu đặt lịch của bạn!</h3>");
                json.append("<div class='space-y-2 text-sm text-gray-700'>");
                json.append("<div class='flex items-center gap-2'>");
                json.append("<span class='font-semibold'>🛠️ Dịch vụ:</span>");
                json.append("<span class='text-purple-600 font-medium'>").append(service).append("</span>");
                json.append("</div>");
                json.append("<div class='flex items-center gap-2'>");
                json.append("<span class='font-semibold'>📅 Ngày:</span>");
                json.append("<span class='text-blue-600 font-medium'>").append(dateStr).append("</span>");
                json.append("</div>");
                json.append("<div class='flex items-center gap-2'>");
                json.append("<span class='font-semibold'>🕐 Giờ:</span>");
                json.append("<span class='text-blue-600 font-medium'>").append(timeStr).append("</span>");
                json.append("</div>");
                json.append("<div class='flex items-center gap-2'>");
                json.append("<span class='font-semibold'>⏱️ Thời lượng:</span>");
                json.append("<span class='text-blue-600 font-medium'>").append(duration).append(" giờ</span>");
                json.append("</div>");
                json.append("</div>");
                json.append("<div class='mt-4 p-3 bg-white rounded border border-gray-200'>");
                json.append("<p class='text-sm text-gray-600 mb-2'>💡 <strong>Bước tiếp theo:</strong></p>");
                json.append("<p class='text-sm text-gray-600'>Tìm worker phù hợp với dịch vụ <strong>").append(service).append("</strong> để đặt lịch.</p>");
                json.append("</div>");
                json.append("</div>");
                json.append("</div>");
                
                return json.toString();
                
            } catch (Exception e) {
                System.err.println("❌ Error in parseNaturalLanguageBooking: " + e.getMessage());
                e.printStackTrace();
                return "<div class='p-4 bg-yellow-50 border-l-4 border-yellow-400 text-yellow-800'>" +
                       "<p class='font-semibold'>⚠️ Không thể hiểu yêu cầu đặt lịch</p>" +
                       "<p class='text-sm mt-1'>Vui lòng cung cấp rõ hơn: dịch vụ gì, ngày nào, giờ nào?<br>" +
                       "VD: 'Đặt lịch dọn dẹp ngày mai lúc 9h'</p>" +
                       "</div>";
            }
        };
    }
    
    @Bean("getHoLiInfo")
    @Description("DEPRECATED - Sử dụng searchPolicyInfo thay thế. " +
                 "Tool này cung cấp thông tin tổng quan về HoLi. " +
                 "Không cần input parameter")
    public Function<Void, String> getHoLiInfo() {
        return request -> {
            try {
                System.out.println("⚠️ [TOOL CALLED] getHoLiInfo - DEPRECATED, using searchPolicyInfo instead");
                
                // Use PolicyService to get general info
                List<PolicyDocument> policies = policyService.getPoliciesByCategory("about");
                
                if (policies.isEmpty()) {
                    // Fallback to old static HTML if no policies found
                    return getFallbackHoLiInfo();
                }
                
                return policyService.formatPoliciesAsHtml(policies, "giới thiệu HoLi");
                
            } catch (Exception e) {
                System.err.println("❌ Error in getHoLiInfo: " + e.getMessage());
                e.printStackTrace();
                return getFallbackHoLiInfo();
            }
        };
    }
    
    private String getFallbackHoLiInfo() {
        return "<div class='space-y-4 text-sm'>" +
               "<div class='bg-gradient-to-r from-green-50 to-blue-50 p-4 rounded-lg border-l-4 border-green-500'>" +
               "<h3 class='font-bold text-green-700 mb-2 text-base'>🏠 Giới thiệu về HoLi</h3>" +
               "<p class='text-gray-700 leading-relaxed'>HoLi là nền tảng kết nối dịch vụ gia đình hàng đầu Việt Nam, giúp bạn dễ dàng tìm kiếm và thuê các worker chuyên nghiệp cho các công việc như dọn dẹp, sửa chữa, chăm sóc, và nhiều dịch vụ khác.</p>" +
               "</div>" +
               
               "<div class='bg-blue-50 p-4 rounded-lg border-l-4 border-blue-500'>" +
               "<h3 class='font-bold text-blue-700 mb-2 text-base'>🎯 Cách thức hoạt động</h3>" +
               "<ul class='space-y-2 text-gray-700'>" +
               "<li>✅ <strong>Tìm kiếm:</strong> Tìm worker theo dịch vụ hoặc địa điểm</li>" +
               "<li>✅ <strong>Đặt lịch:</strong> Chọn worker phù hợp và đặt lịch làm việc</li>" +
               "<li>✅ <strong>Xác nhận:</strong> Worker xác nhận và thực hiện công việc</li>" +
               "<li>✅ <strong>Thanh toán:</strong> Thanh toán sau khi hoàn thành</li>" +
               "<li>✅ <strong>Đánh giá:</strong> Đánh giá chất lượng dịch vụ</li>" +
               "</ul>" +
               "</div>" +
               
               "<div class='bg-purple-50 p-4 rounded-lg border-l-4 border-purple-500'>" +
               "<h3 class='font-bold text-purple-700 mb-2 text-base'>📋 Điều khoản & Chính sách</h3>" +
               "<ul class='space-y-2 text-gray-700'>" +
               "<li>📌 <strong>Hủy lịch:</strong> Chỉ hủy được khi booking ở trạng thái 'Chờ xác nhận'</li>" +
               "<li>📌 <strong>Thanh toán:</strong> Thanh toán trực tiếp với worker sau khi hoàn thành</li>" +
               "<li>📌 <strong>Bảo mật:</strong> Thông tin cá nhân được bảo mật tuyệt đối</li>" +
               "<li>📌 <strong>Đảm bảo:</strong> Tất cả worker đều được xác minh và đánh giá</li>" +
               "</ul>" +
               "</div>" +
               
               "<div class='bg-yellow-50 p-4 rounded-lg border-l-4 border-yellow-500'>" +
               "<h3 class='font-bold text-yellow-700 mb-2 text-base'>❓ Câu hỏi thường gặp</h3>" +
               "<ul class='space-y-2 text-gray-700'>" +
               "<li><strong>Q:</strong> Làm sao để đặt lịch?<br><strong>A:</strong> Tìm worker phù hợp, click nút 'Đặt lịch' và điền thông tin</li>" +
               "<li><strong>Q:</strong> Có thể hủy lịch không?<br><strong>A:</strong> Có, chỉ khi booking đang ở trạng thái 'Chờ xác nhận'</li>" +
               "<li><strong>Q:</strong> Thanh toán như thế nào?<br><strong>A:</strong> Thanh toán trực tiếp với worker sau khi hoàn thành công việc</li>" +
               "<li><strong>Q:</strong> Worker có đáng tin không?<br><strong>A:</strong> Tất cả worker đều được xác minh và có đánh giá từ khách hàng trước</li>" +
               "</ul>" +
               "</div>" +
               
               "<div class='bg-gray-100 p-3 rounded text-center text-xs text-gray-600'>" +
               "💡 Còn thắc mắc? Hãy hỏi tôi bất cứ điều gì về HoLi!" +
               "</div>" +
               "</div>";
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
        card.append("<button onclick=\"window.viewWorkerDetail(").append(worker.getId()).append(")\" ");
        card.append("class='flex-1 bg-purple-600 hover:bg-purple-700 text-white text-sm font-medium py-2 px-4 rounded-lg transition-colors'>");
        card.append("Xem chi tiết");
        card.append("</button>");
        card.append("<button onclick=\"window.bookWorker(").append(worker.getId()).append(")\" ");
        card.append("class='flex-1 bg-green-600 hover:bg-green-700 text-white text-sm font-medium py-2 px-4 rounded-lg transition-colors'>");
        card.append("Đặt lịch");
        card.append("</button>");
        card.append("</div>");
        
        card.append("</div>");
        card.append("</div>");
        
        return card.toString();
    }
    
    @Bean("getCustomerBookings")
    @Description("Lấy danh sách lịch hẹn (bookings) của khách hàng hiện tại. " +
                 "Sử dụng khi khách hỏi: 'Lịch của tôi', 'Xem booking', 'Lịch hẹn nào', 'Tôi đã đặt lịch gì'. 'Tôi muốn hủy lịch hẹn', 'Hủy lịch hẹn của tôi'" +
                 "Input: username - tên đăng nhập của khách hàng")
    public Function<GetCustomerBookingsRequest, String> getCustomerBookings() {
        return request -> {
            try {
                System.out.println("📅 [TOOL CALLED] getCustomerBookings for user: " + request.username());
                
                if (request.username() == null || request.username().isEmpty()) {
                    return "<div class='p-4 bg-yellow-50 border-l-4 border-yellow-400 text-yellow-800 rounded'>" +
                           "⚠️ Vui lòng đăng nhập để xem lịch hẹn của bạn." +
                           "</div>";
                }
                
                System.out.println("🔍 [DEBUG] Searching customer with username/email: '" + request.username() + "'");
                
                // Try to find by username first
                Customer customer = customerRepository.findByUser_UserName(request.username())
                        .orElse(null);
                
                // If not found by username, try email (since JWT might use email as username)
                if (customer == null) {
                    System.out.println("🔍 [DEBUG] Not found by username, trying email...");
                    customer = customerRepository.findByUser_Email(request.username())
                            .orElse(null);
                }
                
                if (customer == null) {
                    System.err.println("⚠️ [DEBUG] Customer not found for username/email: '" + request.username() + "'");
                    System.err.println("💡 [DEBUG] Please check if this user exists in Customer table");
                    return "<div class='p-4 bg-yellow-50 border-l-4 border-yellow-400 text-yellow-800 rounded'>" +
                           "⚠️ Không tìm thấy thông tin khách hàng với email: <strong>" + request.username() + "</strong><br>" +
                           "Bạn có thể cần đăng ký làm khách hàng trước." +
                           "</div>";
                }
                
                System.out.println("✅ [DEBUG] Found customer: ID=" + customer.getId() + ", Name=" + customer.getUser().getRealUserName());
                List<Booking> bookings = bookingRepository.findAllByCustomer(customer);
                
                if (bookings.isEmpty()) {
                    return "<div class='p-4 bg-blue-50 border-l-4 border-blue-400 text-blue-800 rounded'>" +
                           "📅 Bạn chưa có lịch hẹn nào.<br>" +
                           "Hãy tìm worker và nhấn nút 'Đặt lịch' để đặt lịch mới!" +
                           "</div>";
                }
                
                return generateBookingsJSON(bookings);
                
            } catch (Exception e) {
                System.err.println("❌ Error in getCustomerBookings: " + e.getMessage());
                e.printStackTrace();
                return "<div class='p-4 bg-red-50 border-l-4 border-red-400 text-red-800 rounded'>" +
                       "❌ Lỗi khi lấy danh sách lịch hẹn: " + e.getMessage() +
                       "</div>";
            }
        };
    }
    
    private String generateBookingsJSON(List<Booking> bookings) {
        StringBuilder json = new StringBuilder();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        
        json.append("<div data-bookings='[");
        
        for (int i = 0; i < bookings.size(); i++) {
            Booking booking = bookings.get(i);
            if (i > 0) json.append(",");
            
            json.append("{");
            json.append("\"id\":").append(booking.getBookingId()).append(",");
            json.append("\"serviceName\":\"").append(escapeJson(getServiceName(booking))).append("\",");
            json.append("\"workerName\":\"").append(escapeJson(getWorkerName(booking))).append("\",");
            json.append("\"status\":\"").append(booking.getStatus().toString()).append("\",");
            
            if (booking.getStartTime() != null) {
                json.append("\"date\":\"").append(booking.getStartTime().format(dateFormatter)).append("\",");
                json.append("\"startTime\":\"").append(booking.getStartTime().format(timeFormatter)).append("\",");
                if (booking.getEndTime() != null) {
                    json.append("\"endTime\":\"").append(booking.getEndTime().format(timeFormatter)).append("\",");
                }
            }
            
            json.append("\"location\":\"").append(escapeJson(booking.getLocation() != null ? booking.getLocation() : "")).append("\",");
            json.append("\"specialRequest\":\"").append(escapeJson(booking.getSpecialRequest() != null ? booking.getSpecialRequest() : "")).append("\",");
            json.append("\"totalPrice\":").append(booking.getTotalPrice());
            json.append("}");
        }
        
        json.append("]' class='bookings-data'></div>");
        
        return json.toString();
    }
    
    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
    
    private String generateBookingsHTML(List<Booking> bookings) {
        StringBuilder html = new StringBuilder();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        
        html.append("<div class='space-y-3'>");
        html.append("<div class='font-semibold text-gray-900 mb-3 text-base'>");
        html.append("📅 Lịch hẹn của bạn (").append(bookings.size()).append(" lịch):");
        html.append("</div>");
        
        for (Booking booking : bookings) {
            html.append("<div class='bg-gray-50 rounded-lg p-3 border-2 border-gray-200'>");
            
            // Header: Service name and status
            html.append("<div class='flex items-start justify-between mb-2'>");
            html.append("<div class='flex-1'>");
            html.append("<div class='font-semibold text-gray-900 text-sm'>").append(getServiceName(booking)).append("</div>");
            html.append("<div class='text-xs text-gray-600 mt-1'>Worker: ").append(getWorkerName(booking)).append("</div>");
            html.append("</div>");
            html.append(getStatusBadge(booking.getStatus().toString()));
            html.append("</div>");
            
            // Booking details
            html.append("<div class='space-y-1 text-xs text-gray-600 mt-2'>");
            
            // Date and time
            if (booking.getStartTime() != null) {
                html.append("<div class='flex items-center gap-1'>");
                html.append("<span>📅</span>");
                html.append("<span>").append(booking.getStartTime().format(dateFormatter)).append("</span>");
                html.append("</div>");
                
                html.append("<div class='flex items-center gap-1'>");
                html.append("<span>🕐</span>");
                html.append("<span>").append(booking.getStartTime().format(timeFormatter));
                if (booking.getEndTime() != null) {
                    html.append(" - ").append(booking.getEndTime().format(timeFormatter));
                }
                html.append("</span>");
                html.append("</div>");
            }
            
            // Location
            if (booking.getLocation() != null && !booking.getLocation().isEmpty()) {
                html.append("<div class='flex items-center gap-1'>");
                html.append("<span>📍</span>");
                html.append("<span class='truncate'>").append(booking.getLocation()).append("</span>");
                html.append("</div>");
            }
            
            // Special request
            if (booking.getSpecialRequest() != null && !booking.getSpecialRequest().isEmpty()) {
                html.append("<div class='flex items-center gap-1'>");
                html.append("<span>📝</span>");
                html.append("<span class='truncate'>").append(booking.getSpecialRequest()).append("</span>");
                html.append("</div>");
            }
            
            // Price
            html.append("<div class='flex items-center gap-1 pt-2 border-t border-gray-300'>");
            html.append("<span>💰</span>");
            html.append("<span class='font-semibold text-green-600'>");
            html.append(String.format("%,.0f", booking.getTotalPrice())).append(" đ");
            html.append("</span>");
            html.append("</div>");
            
            html.append("</div>");
            html.append("</div>");
        }
        
        html.append("</div>");
        
        return html.toString();
    }
    
    private String getServiceName(Booking booking) {
        if (booking.getService() != null && booking.getService().getServiceName() != null) {
            return booking.getService().getServiceName();
        }
        return "Dịch vụ";
    }
    
    private String getWorkerName(Booking booking) {
        if (booking.getWorker() != null && booking.getWorker().getUser() != null) {
            return booking.getWorker().getUser().getRealUserName();
        }
        return "N/A";
    }
    
    private String getStatusBadge(String status) {
        String badge = "";
        switch (status) {
            case "PENDING":
                badge = "<span class='px-3 py-1 bg-yellow-100 text-yellow-800 text-xs font-semibold rounded-full'>🕒 Chờ xác nhận</span>";
                break;
            case "CONFIRMED":
                badge = "<span class='px-3 py-1 bg-blue-100 text-blue-800 text-xs font-semibold rounded-full'>✅ Đã xác nhận</span>";
                break;
            case "IN_PROGRESS":
                badge = "<span class='px-3 py-1 bg-purple-100 text-purple-800 text-xs font-semibold rounded-full'>🔄 Đang thực hiện</span>";
                break;
            case "COMPLETED":
                badge = "<span class='px-3 py-1 bg-green-100 text-green-800 text-xs font-semibold rounded-full'>✅ Hoàn thành</span>";
                break;
            case "CANCELLED":
                badge = "<span class='px-3 py-1 bg-red-100 text-red-800 text-xs font-semibold rounded-full'>❌ Đã hủy</span>";
                break;
            default:
                badge = "<span class='px-3 py-1 bg-gray-100 text-gray-800 text-xs font-semibold rounded-full'>" + status + "</span>";
        }
        return badge;
    }
    
    /**
     * Helper methods for parsing natural language booking queries
     */
    private String parseServiceFromQuery(String query) {
        // Common service keywords mapping
        if (query.contains("dọn dẹp") || query.contains("vệ sinh") || query.contains("clean")) {
            return "Dọn dẹp nhà cửa";
        } else if (query.contains("gia sư") || query.contains("dạy học") || query.contains("tutor")) {
            return "Gia sư";
        } else if (query.contains("massage") || query.contains("mát xa")) {
            return "Massage";
        } else if (query.contains("sửa chữa") || query.contains("repair") || query.contains("điện") || query.contains("nước")) {
            return "Sửa chữa điện nước";
        } else if (query.contains("nấu ăn") || query.contains("cook") || query.contains("bếp")) {
            return "Nấu ăn";
        } else if (query.contains("chăm sóc") || query.contains("care") || query.contains("người già")) {
            return "Chăm sóc người già";
        } else if (query.contains("giặt") || query.contains("ủi") || query.contains("laundry")) {
            return "Giặt ủi";
        } else {
            return "Dịch vụ tổng hợp";
        }
    }
    
    private String parseDateFromQuery(String query) {
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        if (query.contains("hôm nay") || query.contains("today")) {
            return today.format(formatter);
        } else if (query.contains("ngày mai") || query.contains("tomorrow")) {
            return today.plusDays(1).format(formatter);
        } else if (query.contains("ngày kia") || query.contains("day after tomorrow")) {
            return today.plusDays(2).format(formatter);
        } else if (query.contains("tuần sau") || query.contains("next week")) {
            return today.plusWeeks(1).format(formatter);
        } else if (query.contains("thứ 2") || query.contains("monday")) {
            return getNextDayOfWeek(today, java.time.DayOfWeek.MONDAY).format(formatter);
        } else if (query.contains("thứ 3") || query.contains("tuesday")) {
            return getNextDayOfWeek(today, java.time.DayOfWeek.TUESDAY).format(formatter);
        } else if (query.contains("thứ 4") || query.contains("wednesday")) {
            return getNextDayOfWeek(today, java.time.DayOfWeek.WEDNESDAY).format(formatter);
        } else if (query.contains("thứ 5") || query.contains("thursday")) {
            return getNextDayOfWeek(today, java.time.DayOfWeek.THURSDAY).format(formatter);
        } else if (query.contains("thứ 6") || query.contains("friday")) {
            return getNextDayOfWeek(today, java.time.DayOfWeek.FRIDAY).format(formatter);
        } else if (query.contains("thứ 7") || query.contains("saturday")) {
            return getNextDayOfWeek(today, java.time.DayOfWeek.SATURDAY).format(formatter);
        } else if (query.contains("chủ nhật") || query.contains("sunday")) {
            return getNextDayOfWeek(today, java.time.DayOfWeek.SUNDAY).format(formatter);
        } else {
            // Default to tomorrow
            return today.plusDays(1).format(formatter);
        }
    }
    
    private java.time.LocalDate getNextDayOfWeek(java.time.LocalDate date, java.time.DayOfWeek dayOfWeek) {
        java.time.LocalDate result = date;
        while (result.getDayOfWeek() != dayOfWeek) {
            result = result.plusDays(1);
        }
        // If the day is today, get next week's occurrence
        if (result.equals(date)) {
            result = result.plusWeeks(1);
        }
        return result;
    }
    
    private String parseTimeFromQuery(String query) {
        // Extract time patterns like "9h", "14h30", "9:00", "2pm"
        java.util.regex.Pattern pattern1 = java.util.regex.Pattern.compile("(\\d{1,2})h(\\d{2})?");
        java.util.regex.Matcher matcher1 = pattern1.matcher(query);
        if (matcher1.find()) {
            String hour = matcher1.group(1);
            String minute = matcher1.group(2) != null ? matcher1.group(2) : "00";
            return String.format("%02d:%s", Integer.parseInt(hour), minute);
        }
        
        java.util.regex.Pattern pattern2 = java.util.regex.Pattern.compile("(\\d{1,2}):(\\d{2})");
        java.util.regex.Matcher matcher2 = pattern2.matcher(query);
        if (matcher2.find()) {
            return String.format("%02d:%s", Integer.parseInt(matcher2.group(1)), matcher2.group(2));
        }
        
        // Named times
        if (query.contains("sáng") || query.contains("morning")) {
            return "09:00";
        } else if (query.contains("trưa") || query.contains("noon")) {
            return "12:00";
        } else if (query.contains("chiều") || query.contains("afternoon")) {
            return "14:00";
        } else if (query.contains("tối") || query.contains("evening")) {
            return "18:00";
        }
        
        // Default time
        return "09:00";
    }
    
    private int parseDurationFromQuery(String query) {
        // Look for duration patterns like "2 tiếng", "3h", "4 giờ"
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d+)\\s*(tiếng|giờ|h|hour)");
        java.util.regex.Matcher matcher = pattern.matcher(query);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        
        // Default duration: 2 hours
        return 2;
    }
}