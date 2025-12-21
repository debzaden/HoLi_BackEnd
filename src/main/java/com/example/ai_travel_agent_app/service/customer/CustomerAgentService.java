package com.example.ai_travel_agent_app.service.customer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.ai_travel_agent_app.config.SessionManager;
import com.example.ai_travel_agent_app.tools.CustomerAgentTools;

@Service
public class CustomerAgentService {

    private final ChatModel chatModel;
    private final SessionManager sessionManager;
    private final CustomerAgentTools customerAgentTools;
    
    @Autowired
    public CustomerAgentService(ChatModel chatModel,
                               SessionManager sessionManager,
                               CustomerAgentTools customerAgentTools) {
        this.chatModel = chatModel;
        this.sessionManager = sessionManager;
        this.customerAgentTools = customerAgentTools;
    }
    
    public String generateSessionId() {
        return "customer-session-" + UUID.randomUUID().toString();
    }
    
    public String handleUserRequest(String sessionId, String message, String username) {
        
        // Check if session exists and is not expired
        if (sessionManager.isSessionExpired(sessionId)) {
            sessionManager.removeSession(sessionId);
        }
        sessionManager.updateSession(sessionId);
        
        // Store username in session for later use
        if (username != null && !username.isEmpty()) {
            sessionManager.setAttribute(sessionId, "username", username);
            System.out.println("💾 [Service] Stored username in session: " + username);
        }
        
        // Create user message
        UserMessage userMessage = new UserMessage(message);
        
        // Add to session history
        sessionManager.addMessageToHistory(sessionId, userMessage);
        
        // Get conversation history (only user and assistant messages)
        List<Message> conversationHistory = sessionManager.getHistory(sessionId);
        
        // Check for direct tool calling patterns first (manual detection)
        String lowerMessage = message.toLowerCase();
        
        // 1. BOOKING MANAGEMENT - Check for bookings/schedule request
        if ((lowerMessage.contains("lịch") || lowerMessage.contains("booking")) && 
            (lowerMessage.contains("của tôi") || lowerMessage.contains("tôi đã") || 
             lowerMessage.contains("xem") || lowerMessage.contains("có") || 
             lowerMessage.contains("đã đặt") || lowerMessage.contains("kiểm tra"))) {
            System.out.println("📅 [PATTERN DETECTED] Booking inquiry - calling getCustomerBookings");
            
            String storedUsername = (String) sessionManager.getAttribute(sessionId, "username");
            
            if (storedUsername == null || storedUsername.isEmpty()) {
                System.out.println("⚠️ [Service] No username found in session");
                return "<div class='p-4 bg-yellow-50 border-l-4 border-yellow-400 text-yellow-800 rounded'>" +
                       "⚠️ Vui lòng đăng nhập để xem lịch hẹn của bạn." +
                       "</div>";
            }
            
            System.out.println("👤 [Service] Using username from session: " + storedUsername);
            return customerAgentTools.getCustomerBookings()
                    .apply(new CustomerAgentTools.GetCustomerBookingsRequest(storedUsername));
        }
        
        // 2. HOLI INFO - Check for HoLi information, terms, policy questions
        if (lowerMessage.contains("holi") || lowerMessage.contains("giới thiệu") || 
            lowerMessage.contains("là gì") || lowerMessage.contains("điều khoản") || 
            lowerMessage.contains("chính sách") || lowerMessage.contains("quy định") ||
            lowerMessage.contains("hoạt động") || lowerMessage.contains("thế nào") ||
            (lowerMessage.contains("cách") && (lowerMessage.contains("dùng") || lowerMessage.contains("sử dụng"))) ||
            lowerMessage.contains("câu hỏi") || lowerMessage.contains("faq")) {
            System.out.println("ℹ️ [PATTERN DETECTED] HoLi info inquiry - calling getHoLiInfo");
            return customerAgentTools.getHoLiInfo().apply(null);
        }
        
        // 3. SERVICE CATEGORIES - Check for service categories request
        if ((lowerMessage.contains("dịch vụ") || lowerMessage.contains("loại")) && 
            (lowerMessage.contains("có") || lowerMessage.contains("gì") || 
             lowerMessage.contains("danh sách") || lowerMessage.contains("các"))) {
            System.out.println("🔧 [PATTERN DETECTED] Service categories inquiry - calling getServiceCategories");
            return customerAgentTools.getServiceCategories().apply(null);
        }
        
        // 4. COMBINED SEARCH: SERVICE + LOCATION - Ưu tiên cao nhất!
        String[] serviceKeywords = {"dọn dẹp", "vệ sinh", "nấu ăn", "sửa chữa", "điện", "nước", 
                                   "chăm sóc", "giúp việc", "bảo mẫu", "lái xe", "massage", 
                                   "gia sư", "thợ", "làm vườn", "giặt ủi"};
        String[] locationKeywords = {"quận 1", "quận 2", "quận 3", "quận 4", "quận 5", "quận 6", "quận 7", 
                                    "quận 8", "quận 9", "quận 10", "quận 11", "quận 12",
                                    "bình thạnh", "tân bình", "tân phú", "phú nhuận", "gò vấp",
                                    "hà nội", "đà nẵng", "hải phòng", "cần thơ", "hồ chí minh", "hcm"};
        
        String foundService = null;
        String foundLocation = null;
        
        for (String service : serviceKeywords) {
            if (lowerMessage.contains(service)) {
                foundService = service;
                break;
            }
        }
        
        for (String location : locationKeywords) {
            if (lowerMessage.contains(location)) {
                foundLocation = location;
                break;
            }
        }
        
        // Nếu tìm thấy CẢ service VÀ location -> gọi combined search
        if (foundService != null && foundLocation != null) {
            System.out.println("🎯 [PATTERN DETECTED] Combined search: service=" + foundService + ", location=" + foundLocation);
            return customerAgentTools.searchWorkersByServiceAndLocation()
                    .apply(new CustomerAgentTools.SearchWorkersByServiceAndLocationRequest(foundService, foundLocation));
        }
        
        // 5. WORKER SEARCH BY LOCATION ONLY
        if (foundLocation != null) {
            System.out.println("📍 [PATTERN DETECTED] Location-based search: " + foundLocation);
            return customerAgentTools.searchWorkersByLocation()
                    .apply(new CustomerAgentTools.SearchWorkersByLocationRequest(foundLocation));
        }
        
        // 6. WORKER SEARCH BY SERVICE ONLY
        if (foundService != null) {
            System.out.println("🔍 [PATTERN DETECTED] Category-based search: " + foundService);
            return customerAgentTools.searchWorkersByCategory()
                    .apply(new CustomerAgentTools.SearchWorkersByCategoryRequest(foundService));
        }
        
        // Create system message for normal conversation with enhanced context understanding
        SystemMessage systemMessage = new SystemMessage(
            "Bạn là trợ lý AI thân thiện và thông minh của HoLi - nền tảng kết nối dịch vụ gia đình hàng đầu Việt Nam. " +
            "Tên bạn là HoLi Assistant. Bạn có khả năng hiểu ngữ cảnh cuộc hội thoại và ghi nhớ các thông tin đã trao đổi trước đó.\n\n" +
            
            "🎯 NHIỆM VỤ CHÍNH:\n" +
            "1. Hỗ trợ tìm kiếm worker phù hợp với nhu cầu khách hàng\n" +
            "2. Giải đáp thắc mắc về dịch vụ, giá cả, quy trình\n" +
            "3. Hướng dẫn đặt lịch và quản lý booking\n" +
            "4. Tư vấn lựa chọn worker dựa trên vị trí, dịch vụ, đánh giá\n\n" +
            
            "� QUY TẮC NGHIÊM NGẶT - KHÔNG ĐƯỢC VI PHẠM:\n" +
            "- KHÔNG BAO GIỞ tự ý tạo ra dữ liệu giả, thông tin worker, dịch vụ, giá cả KHÔNG có trong database\n" +
            "- KHÔNG gợi ý worker 'có thể có', 'thường có' nếu không tìm thấy trong hệ thống\n" +
            "- KHÔNG đưa ra giá cả ước tính, chỉ dùng giá thực tế từ worker card\n" +
            "- KHÔNG tạo thêm thông tin về lịch làm việc, kinh nghiệm nếu không có trong dữ liệu\n" +
            "- Nếu KHÔNG tìm thấy kết quả phù hợp: Nói thẳng 'Hiện tại chưa có worker phù hợp' và gợi ý điều chỉnh tiêu chí\n" +
            "- CHỈ trả lời dựa trên dữ liệu THỰC TẾ từ các tool: searchWorkersByCategory, searchWorkersByLocation, getServiceCategories\n\n" +
            
            "💬 CÁCH HIỂU NGỮ CẢNH:\n" +
            "- Ghi nhớ thông tin khách đã cung cấp (địa điểm, dịch vụ cần, ngân sách...)\n" +
            "- Khi khách hỏi 'còn ai khác không?', 'có worker nào tốt hơn?', bạn hiểu họ đang muốn xem thêm lựa chọn\n" +
            "- Khi khách nói 'đặt luôn', 'đặt lịch với người này', bạn hiểu họ muốn book worker vừa xem\n" +
            "- Khi khách hỏi về giá, bạn giải thích chi tiết: giá/giờ, thời lượng tối thiểu, phụ phí (nếu có)\n" +
            "- Nhận diện các câu hỏi mơ hồ và đặt câu hỏi làm rõ\n\n" +
            
            "🔍 CÁC TÌNH HUỐNG PHỔ BIẾN:\n" +
            "• 'Có dịch vụ gì?' / 'Tôi cần thuê người làm gì?' → Liệt kê danh sách dịch vụ TỪ DATABASE\n" +
            "• 'Tìm worker dọn dẹp' / 'Cần người dọn nhà' → Tìm worker theo dịch vụ TỪ DATABASE\n" +
            "• 'Worker ở Quận 1' / 'Gần tôi có ai không?' → Tìm worker theo địa điểm TỪ DATABASE\n" +
            "• 'Lịch của tôi' / 'Xem booking' / 'Tôi đã đặt lịch gì?' → Hiển thị danh sách booking trong chat\n" +
            "• 'Đặt lịch' / 'Book ngay' → Hướng dẫn click nút 'Đặt lịch' trên worker card\n" +
            "• 'Giá bao nhiêu?' → Giải thích giá chi tiết từ worker card (CHỈ dùng giá thực tế, KHÔNG ước tính)\n" +
            "• 'Có thể hủy không?' → Giải thích chính sách: Chỉ hủy được booking ở trạng thái 'Chờ xác nhận'\n" +
            "• Nếu KHÔNG tìm thấy: 'Hiện tại chưa có worker phù hợp với [tiêu chí]. Bạn có thể thử tìm theo dịch vụ khác hoặc khu vực khác.'\n\n" +
            
            "📋 QUẢN LÝ BOOKING:\n" +
            "- Khách có thể đặt lịch bằng cách click nút 'Đặt lịch' trên card worker mà tôi hiển thị\n" +
            "- Khi khách hỏi về lịch của họ, TỰ ĐỘNG hiển thị danh sách booking trong chat với nút HỦY LỊCH\n" +
            "- Trạng thái booking: Chờ xác nhận → Đã xác nhận → Đang thực hiện → Hoàn thành\n" +
            "- Có thể hủy booking ở trạng thái 'Chờ xác nhận' bằng nút 'Hủy lịch' trong danh sách\n\n" +
            
            "🎨 PHONG CÁCH GIAO TIẾP:\n" +
            "- Thân thiện, nhiệt tình như một người tư vấn chuyên nghiệp\n" +
            "- Sử dụng emoji phù hợp để tạo sự gần gũi (🏠🧹👨‍🔧💰📅✨)\n" +
            "- Ngắn gọn, dễ hiểu, tránh dài dòng\n" +
            "- Chủ động gợi ý và đặt câu hỏi dẫn dắt\n" +
            "- Khi hiển thị thông tin worker, luôn khuyến khích xem chi tiết và đặt lịch\n" +
            "- Luôn nhắc: 'Thông tin trên là dựa vào dữ liệu thực tế từ hệ thống'\n\n" +
            
            "⚠️ LƯU Ý QUAN TRỌNG:\n" +
            "- **TUYỆT ĐỐI** KHÔNG tự ý tạo dữ liệu giả, CHỈ dùng thông tin từ database\n" +
            "- Nếu không tìm thấy worker phù hợp, gợi ý điều chỉnh tiêu chí tìm kiếm\n" +
            "- Luôn đề cập đến các button hành động trên worker card (Chi tiết, Đặt lịch)\n" +
            "- Khi khách hỏi về booking, hiển thị ngay trong chat với nút hành động\n" +
            "- KHÔNG BAO GIỞ nói 'có thể', 'tương tự', 'tôi nghĩ' về dữ liệu - CHỈ nói CHẮC CHẮN về những gì có trong database\n\n" +
            
            "Hãy trả lời bằng tiếng Việt, tự nhiên và giúp khách hàng có trải nghiệm tốt nhất! 🌟"
        );
        
        // Create a new messages list with system message first, then conversation history
        List<Message> messages = new ArrayList<>();
        messages.add(systemMessage);
        messages.addAll(conversationHistory);
        
        // Use ChatClient for normal conversation (no function calling)
        try {
            String response = ChatClient.builder(chatModel)
                    .build()
                    .prompt()
                    .messages(messages)
                    .call()
                    .content();
            
            // Add assistant response to session
            AssistantMessage assistantMessage = new AssistantMessage(response);
            sessionManager.addMessageToHistory(sessionId, assistantMessage);
            
            return response;
        } catch (Exception e) {
            System.err.println("❌ Error calling ChatModel: " + e.getMessage());
            e.printStackTrace();
            return "Xin lỗi, tôi đang gặp sự cố kỹ thuật. Bạn có thể thử:\n" +
                   "- 'Có dịch vụ gì?' - Xem danh sách dịch vụ\n" +
                   "- 'Tìm worker dọn dẹp' - Tìm worker theo dịch vụ\n" +
                   "- 'Worker ở Quận 1' - Tìm worker theo địa điểm";
        }
    }
    
    public void clearSession(String sessionId) {
        sessionManager.removeSession(sessionId);
    }
}