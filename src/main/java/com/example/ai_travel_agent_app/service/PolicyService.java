package com.example.ai_travel_agent_app.service;

import com.example.ai_travel_agent_app.model.PolicyDocument;
import com.example.ai_travel_agent_app.model.PolicyResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PolicyService {
    
    private List<PolicyDocument> allPolicies = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @PostConstruct
    public void loadPolicies() {
        try {
            log.info("🔄 Starting to load policies from JSON file...");
            ClassPathResource resource = new ClassPathResource("policies/holi_policies.json");
            
            if (!resource.exists()) {
                log.error("❌ Policy file does not exist: policies/holi_policies.json");
                allPolicies = new ArrayList<>();
                return;
            }
            
            log.info("📄 Policy file found, reading content...");
            PolicyResponse policyResponse = objectMapper.readValue(resource.getInputStream(), PolicyResponse.class);
            
            if (policyResponse == null) {
                log.error("❌ PolicyResponse is null after parsing JSON");
                allPolicies = new ArrayList<>();
                return;
            }
            
            if (policyResponse.getPolicies() == null) {
                log.error("❌ getPolicies() returned null");
                allPolicies = new ArrayList<>();
                return;
            }
            
            allPolicies = policyResponse.getPolicies();
            log.info("✅ Successfully loaded {} policies", allPolicies.size());
            
            // Log first 3 policy titles for verification
            if (!allPolicies.isEmpty()) {
                log.info("📋 Sample policies loaded:");
                allPolicies.stream().limit(3).forEach(p -> 
                    log.info("   - {} ({})", p.getTitle(), p.getId())
                );
            }
            
        } catch (IOException e) {
            log.error("❌ Failed to load policies from JSON file: {}", e.getMessage(), e);
            allPolicies = new ArrayList<>();
        } catch (Exception e) {
            log.error("❌ Unexpected error while loading policies: {}", e.getMessage(), e);
            allPolicies = new ArrayList<>();
        }
    }
    
    /**
     * Search policies by query string matching against title, content, and keywords
     * @param query Search query
     * @return List of matching policy documents
     */
    public List<PolicyDocument> searchPolicies(String query) {
        if (query == null || query.trim().isEmpty()) {
            return allPolicies; // Return all if no query
        }
        
        log.info("🔍 Searching policies for query: '{}'", query);
        String normalizedQuery = removeVietnameseTones(query.toLowerCase().trim());
        log.debug("Normalized query: '{}'", normalizedQuery);
        
        List<PolicyDocument> results = allPolicies.stream()
                .filter(policy -> matchesPolicy(policy, normalizedQuery, query.toLowerCase()))
                .collect(Collectors.toList());
        
        log.info("✅ Found {} policies matching query: '{}'", results.size(), query);
        if (results.isEmpty()) {
            log.warn("⚠️ No policies found for query: '{}'. Available keywords: {}", 
                query, allPolicies.stream()
                    .flatMap(p -> p.getKeywords().stream())
                    .distinct()
                    .collect(Collectors.joining(", ")));
        }
        
        return results;
    }
    
    /**
     * Get policy by ID
     */
    public PolicyDocument getPolicyById(String id) {
        return allPolicies.stream()
                .filter(policy -> policy.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Get policies by category
     */
    public List<PolicyDocument> getPoliciesByCategory(String category) {
        return allPolicies.stream()
                .filter(policy -> policy.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }
    
    /**
     * Get all policies
     */
    public List<PolicyDocument> getAllPolicies() {
        return new ArrayList<>(allPolicies);
    }
    
    /**
     * Check if a policy matches the query
     */
    private boolean matchesPolicy(PolicyDocument policy, String normalizedQuery, String originalQuery) {
        // Split query into individual words for better matching
        String[] queryWords = normalizedQuery.split("\\s+");
        String[] originalWords = originalQuery.split("\\s+");
        
        // Check if ANY word in query matches title, content, or keywords
        for (String word : originalWords) {
            if (word.length() < 2) continue; // Skip very short words
            
            // Exact match in original query (case-insensitive)
            if (policy.getTitle().toLowerCase().contains(word) ||
                policy.getContent().toLowerCase().contains(word) ||
                policy.getKeywords().stream().anyMatch(keyword -> keyword.toLowerCase().contains(word) || word.contains(keyword.toLowerCase()))) {
                log.debug("✓ Policy '{}' matches word: '{}'", policy.getTitle(), word);
                return true;
            }
        }
        
        // Normalized match (without tones)
        String normalizedTitle = removeVietnameseTones(policy.getTitle().toLowerCase());
        String normalizedContent = removeVietnameseTones(policy.getContent().toLowerCase());
        
        for (String word : queryWords) {
            if (word.length() < 2) continue;
            
            if (normalizedTitle.contains(word) || normalizedContent.contains(word)) {
                log.debug("✓ Policy '{}' matches normalized word: '{}'", policy.getTitle(), word);
                return true;
            }
        }
        
        // Check normalized keywords with word splitting
        for (String keyword : policy.getKeywords()) {
            String normalizedKeyword = removeVietnameseTones(keyword.toLowerCase());
            for (String word : queryWords) {
                if (word.length() < 2) continue;
                if (normalizedKeyword.contains(word) || word.contains(normalizedKeyword)) {
                    log.debug("✓ Policy '{}' matches keyword '{}' with word: '{}'", policy.getTitle(), keyword, word);
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Remove Vietnamese tones for better matching
     */
    private String removeVietnameseTones(String str) {
        if (str == null) return "";
        
        str = str.replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a");
        str = str.replaceAll("[èéẹẻẽêềếệểễ]", "e");
        str = str.replaceAll("[ìíịỉĩ]", "i");
        str = str.replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o");
        str = str.replaceAll("[ùúụủũưừứựửữ]", "u");
        str = str.replaceAll("[ỳýỵỷỹ]", "y");
        str = str.replaceAll("đ", "d");
        
        return str;
    }
    
    /**
     * Format search results as HTML for display
     */
    public String formatPoliciesAsHtml(List<PolicyDocument> policies, String query) {
        if (policies.isEmpty()) {
            return "<div class='p-4 bg-yellow-50 border-l-4 border-yellow-400 text-yellow-800'>" +
                   "<p class='font-semibold'>⚠️ Không tìm thấy thông tin</p>" +
                   "<p class='text-sm mt-1'>Xin lỗi, tôi không tìm thấy thông tin phù hợp với câu hỏi của bạn. " +
                   "Vui lòng thử lại với từ khóa khác hoặc liên hệ hỗ trợ.</p>" +
                   "</div>";
        }
        
        // Limit to top 3 results for better readability
        List<PolicyDocument> limitedPolicies = policies.size() > 3 
            ? policies.subList(0, 3) 
            : policies;
        
        StringBuilder html = new StringBuilder();
        html.append("<div class='space-y-4'>");
        
        // Show info message if results were limited
      
        
        for (PolicyDocument policy : limitedPolicies) {
            html.append("<div class='bg-white border border-gray-200 rounded-lg shadow-sm p-4 hover:shadow-md transition-shadow'>");
            
            // Title and category badge
            html.append("<div class='flex items-start justify-between mb-2'>");
            html.append("<h3 class='text-lg font-semibold text-gray-900'>").append(policy.getTitle()).append("</h3>");
            html.append("<span class='ml-2 px-2 py-1 text-xs font-medium rounded-full ")
                .append(getCategoryColor(policy.getCategory()))
                .append("'>")
                .append(getCategoryLabel(policy.getCategory()))
                .append("</span>");
            html.append("</div>");
            
            // Section (if exists)
            if (policy.getSection() != null && !policy.getSection().isEmpty()) {
                html.append("<p class='text-sm font-medium text-blue-600 mb-2'>📍 ")
                    .append(policy.getSection())
                    .append("</p>");
            }
            
            // Content
            html.append("<div class='text-gray-700 text-sm leading-relaxed mb-3'>");
            html.append(formatContent(policy.getContent()));
            html.append("</div>");
            
            // Keywords
            if (policy.getKeywords() != null && !policy.getKeywords().isEmpty()) {
                html.append("<div class='flex flex-wrap gap-1 mt-2'>");
                for (String keyword : policy.getKeywords()) {
                    html.append("<span class='px-2 py-1 text-xs bg-gray-100 text-gray-600 rounded'>")
                        .append(keyword)
                        .append("</span>");
                }
                html.append("</div>");
            }
            
            // Last updated
            if (policy.getLastUpdated() != null) {
                html.append("<p class='text-xs text-gray-400 mt-2'>")
                    .append("Cập nhật: ").append(policy.getLastUpdated())
                    .append("</p>");
            }
            
            html.append("</div>");
        }
        
        html.append("</div>");
        
        return html.toString();
    }
    
    private String getCategoryColor(String category) {
        return switch (category.toLowerCase()) {
            case "booking" -> "bg-blue-100 text-blue-800";
            case "cancellation" -> "bg-red-100 text-red-800";
            case "payment" -> "bg-green-100 text-green-800";
            case "privacy" -> "bg-purple-100 text-purple-800";
            case "worker_verification" -> "bg-yellow-100 text-yellow-800";
            case "review" -> "bg-pink-100 text-pink-800";
            case "service_quality" -> "bg-indigo-100 text-indigo-800";
            case "usage_terms" -> "bg-gray-100 text-gray-800";
            case "faq" -> "bg-orange-100 text-orange-800";
            case "about" -> "bg-teal-100 text-teal-800";
            default -> "bg-gray-100 text-gray-800";
        };
    }
    
    private String getCategoryLabel(String category) {
        return switch (category.toLowerCase()) {
            case "booking" -> "Đặt Lịch";
            case "cancellation" -> "Hủy Lịch";
            case "payment" -> "Thanh Toán";
            case "privacy" -> "Bảo Mật";
            case "worker_verification" -> "Xác Minh";
            case "review" -> "Đánh Giá";
            case "service_quality" -> "Chất Lượng";
            case "usage_terms" -> "Điều Khoản";
            case "faq" -> "Câu Hỏi Thường Gặp";
            case "about" -> "Giới Thiệu";
            default -> category;
        };
    }
    
    private String formatContent(String content) {
        if (content == null) return "";
        
        // Convert newlines to <br> and preserve formatting
        String formatted = content.replace("\n", "<br>");
        
        // Make bullet points
        formatted = formatted.replace("- ", "• ");
        
        return formatted;
    }
}
