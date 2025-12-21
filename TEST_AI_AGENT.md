# 🧪 HƯỚNG DẪN TEST AI AGENT - CHAT WITH DATABASE

## 📋 Các Thay Đổi Đã Thực Hiện

### 1. **ChatClientConfig.java** (MỚI)
```java
@Configuration
public class ChatClientConfig {
    @Bean
    public ChatClient.Builder chatClientBuilder(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
            .defaultFunctions("searchWorkersByCategory", "searchWorkersByLocation", "getServiceCategories");
    }
}
```
**Mục đích**: Enable function calling cho ChatClient

### 2. **CustomerAgentService.java** - CẬP NHẬT
**Thay đổi chính**:
- ✅ System prompt bằng tiếng Việt với hướng dẫn chi tiết về cách sử dụng functions
- ✅ Xóa manual tool calling logic (if/else checks)
- ✅ Thêm `.functions()` vào ChatClient call để enable automatic function calling
- ✅ Spring AI sẽ tự động quyết định khi nào gọi function dựa trên user query

### 3. **CustomerAgentTools.java** - CẬP NHẬT
**Cải tiến**:
- ✅ @Description bằng tiếng Việt với ví dụ cụ thể
- ✅ Thêm System.out.println để track function calls
- ✅ Thêm error logging với printStackTrace
- ✅ Xử lý empty categories list

### 4. **application.yaml** - CẬP NHẬT
```yaml
logging:
  level:
    org.springframework.ai.chat.client: DEBUG
    org.springframework.ai.chat.memory: DEBUG
    org.springframework.ai.chat.model: DEBUG
    com.example.ai_travel_agent_app.service.customer: DEBUG
    com.example.ai_travel_agent_app.tools: DEBUG
```

### 5. **CustomerChat.jsx** - CẬP NHẬT Frontend
**Cải tiến**:
- ✅ Thêm `isHtml` flag để detect HTML content
- ✅ Cải thiện rendering: HTML sử dụng `w-full`, text thường dùng `max-w-xs`
- ✅ Thêm `whitespace-pre-wrap` cho text messages
- ✅ Better error logging với response details
- ✅ Welcome message thân thiện hơn với bullet points

---

## 🚀 CÁCH TEST

### **Bước 1: Build & Run Backend**
```bash
cd d:\MCP_AI\ai-travel-agent-app\ai-traevl-agent-app
mvn clean install
mvn spring-boot:run
```

### **Bước 2: Kiểm Tra Console Logs**
Khi application start, bạn sẽ thấy:
```
✅ ChatClientConfig loaded
✅ CustomerAgentTools beans registered:
   - searchWorkersByCategory
   - searchWorkersByLocation
   - getServiceCategories
```

### **Bước 3: Test Qua Frontend**

#### **Test Case 1: Lấy Danh Sách Dịch Vụ**
**Input**: 
- "Có dịch vụ gì?"
- "Danh sách dịch vụ"
- "Các loại công việc"

**Expected Output**:
- 🔧 Console log: `[TOOL CALLED] getServiceCategories`
- Frontend hiển thị HTML grid với các category cards
- Mỗi card có onclick để trigger search

#### **Test Case 2: Tìm Worker Theo Category**
**Input**:
- "Tìm worker dọn dẹp"
- "Có ai làm vệ sinh không?"
- "Cần thợ điện"

**Expected Output**:
- 🔧 Console log: `[TOOL CALLED] searchWorkersByCategory: Dọn dẹp`
- Frontend hiển thị HTML worker cards với:
  - Avatar, tên, địa chỉ
  - Skills, description
  - Buttons: "Xem chi tiết", "Đặt lịch"

#### **Test Case 3: Tìm Worker Theo Location**
**Input**:
- "Tìm worker ở Quận 1"
- "Worker tại Hà Nội"
- "Người làm ở Đà Nẵng"

**Expected Output**:
- 🔧 Console log: `[TOOL CALLED] searchWorkersByLocation: Quận 1`
- Frontend hiển thị HTML worker cards filtered by location

#### **Test Case 4: General Chat (Không Gọi Function)**
**Input**:
- "Xin chào"
- "Cảm ơn bạn"
- "Giá dịch vụ như thế nào?"

**Expected Output**:
- AI response bằng tiếng Việt
- Không có function call log
- Plain text message

---

## 🔍 DEBUGGING

### **Nếu Function KHÔNG Được Gọi:**

1. **Kiểm tra Console Logs**
```
Tìm dòng:
[DEBUG] org.springframework.ai.chat.client - Available functions: [...]
```

2. **Verify ChatClientConfig Bean**
```java
// Thêm logging vào ChatClientConfig
@PostConstruct
public void init() {
    System.out.println("✅ ChatClientConfig initialized with functions: searchWorkersByCategory, searchWorkersByLocation, getServiceCategories");
}
```

3. **Check Tool Registration**
```java
// Thêm vào CustomerAgentTools constructor
public CustomerAgentTools(WorkerService workerService, CategoryService categoryService) {
    this.workerService = workerService;
    this.categoryService = categoryService;
    System.out.println("✅ CustomerAgentTools initialized");
}
```

### **Nếu Database Không Trả Về Data:**

1. **Test WorkerService Directly**
```java
// Thêm test endpoint
@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @Autowired
    private WorkerService workerService;
    
    @GetMapping("/workers/category/{name}")
    public List<Worker> testCategory(@PathVariable String name) {
        return workerService.searchWorkersByCategory(name);
    }
    
    @GetMapping("/workers/location/{location}")
    public List<Worker> testLocation(@PathVariable String location) {
        return workerService.searchWorkersByLocation(location);
    }
}
```

2. **Verify Database Has Data**
```sql
-- Check categories
SELECT * FROM Category;

-- Check workers
SELECT * FROM Worker WHERE status = 'ACTIVE';

-- Check services
SELECT * FROM Service;
```

---

## 📊 EXPECTED CONSOLE OUTPUT

### **Khi Function Call Thành Công:**
```
2025-12-21 10:30:15.123  DEBUG 12345 --- [nio-8080-exec-1] o.s.ai.chat.client.ChatClient           : Prompt: Tìm worker dọn dẹp
2025-12-21 10:30:15.456  DEBUG 12345 --- [nio-8080-exec-1] o.s.ai.chat.model.GoogleGenAI          : Requesting function call: searchWorkersByCategory
2025-12-21 10:30:15.789  INFO  12345 --- [nio-8080-exec-1] c.e.a.tools.CustomerAgentTools          : 🔧 [TOOL CALLED] searchWorkersByCategory: Dọn dẹp
2025-12-21 10:30:16.012  DEBUG 12345 --- [nio-8080-exec-1] o.s.ai.chat.client.ChatClient           : Function result returned
2025-12-21 10:30:16.345  DEBUG 12345 --- [nio-8080-exec-1] o.s.ai.chat.model.GoogleGenAI          : Final response generated
```

---

## ✅ CHECKLIST HOÀN THÀNH

- [x] ChatClientConfig.java created
- [x] CustomerAgentService.java updated (Vietnamese prompt + function calling)
- [x] CustomerAgentTools.java updated (Better descriptions + logging)
- [x] application.yaml logging levels configured
- [x] CustomerChat.jsx improved (HTML detection + better rendering)
- [x] WorkerService methods verified (searchWorkersByCategory, searchWorkersByLocation)

---

## 🎯 KẾT QUẢ MONG ĐỢI

✅ **User gửi query** → Spring AI phân tích → **Tự động gọi function** → Query database → **Trả về HTML** → Frontend render đẹp

✅ **Function calling hoàn toàn tự động** - không cần manual if/else

✅ **Gemini 2.5 Flash thông minh** - hiểu tiếng Việt và biết khi nào cần gọi function

✅ **Real-time data từ database** - không fake data

---

## 📞 SUPPORT

Nếu gặp vấn đề, check:
1. Console logs (backend)
2. Browser console (frontend)
3. Database có data không?
4. API endpoint `/customer/ask` có return đúng format không?
