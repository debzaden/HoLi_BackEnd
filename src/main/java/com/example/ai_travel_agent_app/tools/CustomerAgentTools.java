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
import com.example.ai_travel_agent_app.service.worker.WorkerService;
import com.example.ai_travel_agent_app.service.admin.CategoryService;
import com.example.ai_travel_agent_app.repository.BookingRepository;
import com.example.ai_travel_agent_app.repository.customer.CustomerRepository;

@Component
public class CustomerAgentTools {

    private final WorkerService workerService;
    private final CategoryService categoryService;
    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;

    // Request records for function parameters
    public record SearchWorkersByCategoryRequest(String categoryName) {}
    public record SearchWorkersByLocationRequest(String location) {}
    public record SearchWorkersByServiceAndLocationRequest(String serviceName, String location) {}
    public record GetCustomerBookingsRequest(String username) {}

    public CustomerAgentTools(WorkerService workerService, 
                            CategoryService categoryService,
                            BookingRepository bookingRepository,
                            CustomerRepository customerRepository) {
        this.workerService = workerService;
        this.categoryService = categoryService;
        this.bookingRepository = bookingRepository;
        this.customerRepository = customerRepository;
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
                 "VD: 'tìm gia sư ở đà nẵng', 'worker dọn dẹp tại quận 1', 'massage ở hà nội'. " +
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
    
    @Bean("getHoLiInfo")
    @Description("Cung cấp thông tin về HoLi, giới thiệu nền tảng, điều khoản sử dụng, chính sách, câu hỏi thường gặp. " +
                 "Sử dụng khi khách hỏi: 'HoLi là gì?', 'giới thiệu', 'điều khoản', 'chính sách', 'quy định', 'làm thế nào để', 'cách thức hoạt động'. " +
                 "Không cần input parameter")
    public Function<Void, String> getHoLiInfo() {
        return request -> {
            try {
                System.out.println("ℹ️ [TOOL CALLED] getHoLiInfo");
                
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
                
            } catch (Exception e) {
                System.err.println("❌ Error in getHoLiInfo: " + e.getMessage());
                e.printStackTrace();
                return "❌ Lỗi khi lấy thông tin: " + e.getMessage();
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
}