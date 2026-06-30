package com.example.ai_travel_agent_app.service;

import com.example.ai_travel_agent_app.model.PolicyDocument;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PolicyServiceTest {

    @Autowired
    private PolicyService policyService;

    @Test
    void testLoadPolicies() {
        List<PolicyDocument> allPolicies = policyService.getAllPolicies();
        assertNotNull(allPolicies, "Policies should not be null");
        assertEquals(16, allPolicies.size(), "Should load 16 policies");
    }

    @Test
    void testSearchByKeyword_HoLi() {
        List<PolicyDocument> results = policyService.searchPolicies("HoLi là gì");
        assertFalse(results.isEmpty(), "Should find results for 'HoLi là gì'");
        
        boolean hasAboutCategory = results.stream()
                .anyMatch(p -> p.getCategory().equals("about"));
        assertTrue(hasAboutCategory, "Should contain 'about' category");
    }

    @Test
    void testSearchByKeyword_Cancellation() {
        List<PolicyDocument> results = policyService.searchPolicies("hủy lịch");
        assertFalse(results.isEmpty(), "Should find results for 'hủy lịch'");
        
        boolean hasCancellationPolicy = results.stream()
                .anyMatch(p -> p.getId().equals("policy_002"));
        assertTrue(hasCancellationPolicy, "Should contain policy_002 (cancellation)");
    }

    @Test
    void testSearchByKeyword_Payment() {
        List<PolicyDocument> results = policyService.searchPolicies("thanh toán");
        assertFalse(results.isEmpty(), "Should find results for 'thanh toán'");
        
        boolean hasPaymentPolicy = results.stream()
                .anyMatch(p -> p.getCategory().equals("payment"));
        assertTrue(hasPaymentPolicy, "Should contain payment category");
    }

    @Test
    void testSearchWithoutTones() {
        // Test với query không dấu
        List<PolicyDocument> results = policyService.searchPolicies("chinh sach huy lich");
        assertFalse(results.isEmpty(), "Should find results even without Vietnamese tones");
    }

    @Test
    void testSearchByCategory() {
        List<PolicyDocument> faqPolicies = policyService.getPoliciesByCategory("faq");
        assertEquals(5, faqPolicies.size(), "Should have 5 FAQ policies");
        
        List<PolicyDocument> aboutPolicies = policyService.getPoliciesByCategory("about");
        assertEquals(3, aboutPolicies.size(), "Should have 3 about policies");
    }

    @Test
    void testGetPolicyById() {
        PolicyDocument policy = policyService.getPolicyById("policy_001");
        assertNotNull(policy, "Should find policy_001");
        assertEquals("booking", policy.getCategory(), "policy_001 should be booking category");
        assertEquals("Chính Sách Đặt Lịch", policy.getTitle(), "Title should match");
    }

    @Test
    void testFormatHtml_WithResults() {
        List<PolicyDocument> results = policyService.searchPolicies("HoLi");
        String html = policyService.formatPoliciesAsHtml(results, "HoLi");
        
        assertNotNull(html, "HTML should not be null");
        assertTrue(html.contains("div"), "Should contain HTML div tags");
        assertTrue(html.contains("class="), "Should contain CSS classes");
    }

    @Test
    void testFormatHtml_NoResults() {
        List<PolicyDocument> emptyResults = policyService.searchPolicies("giá vàng hôm nay");
        String html = policyService.formatPoliciesAsHtml(emptyResults, "giá vàng");
        
        assertTrue(html.contains("Không tìm thấy thông tin"), "Should show 'not found' message");
        assertTrue(html.contains("yellow"), "Should use yellow warning style");
    }

    @Test
    void testWorkerTrustQuery() {
        List<PolicyDocument> results = policyService.searchPolicies("worker có đáng tin không");
        assertFalse(results.isEmpty(), "Should find worker trust information");
        
        boolean hasVerificationPolicy = results.stream()
                .anyMatch(p -> p.getCategory().equals("worker_verification"));
        assertTrue(hasVerificationPolicy, "Should contain verification policy");
    }

    @Test
    void testBecomeWorkerQuery() {
        List<PolicyDocument> results = policyService.searchPolicies("tôi muốn làm worker");
        assertFalse(results.isEmpty(), "Should find information about becoming a worker");
        
        boolean hasFaqWorker = results.stream()
                .anyMatch(p -> p.getId().equals("faq_005"));
        assertTrue(hasFaqWorker, "Should contain faq_005 about becoming worker");
    }

    @Test
    void testKeywordsMatching() {
        // Test with exact keyword
        List<PolicyDocument> results1 = policyService.searchPolicies("booking");
        assertFalse(results1.isEmpty(), "Should find results for 'booking' keyword");
        
        // Test with Vietnamese keyword
        List<PolicyDocument> results2 = policyService.searchPolicies("đặt lịch");
        assertFalse(results2.isEmpty(), "Should find results for 'đặt lịch' keyword");
    }

    @Test
    void testAllCategoriesPresent() {
        List<PolicyDocument> allPolicies = policyService.getAllPolicies();
        
        String[] expectedCategories = {
            "booking", "cancellation", "payment", "privacy",
            "worker_verification", "review", "service_quality",
            "usage_terms", "faq", "about"
        };
        
        for (String category : expectedCategories) {
            boolean hasCategory = allPolicies.stream()
                    .anyMatch(p -> p.getCategory().equals(category));
            assertTrue(hasCategory, "Should have policies for category: " + category);
        }
    }
}
