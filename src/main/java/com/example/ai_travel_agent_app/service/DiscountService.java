package com.example.ai_travel_agent_app.service;

import com.example.ai_travel_agent_app.model.Discount;

public interface DiscountService {

    /**
     * Validate và lấy thông tin voucher
     */
    Discount validateVoucher(String code, float orderAmount);

    /**
     * Tính toán số tiền giảm giá
     */
    float calculateDiscountAmount(Discount discount, float orderAmount);

    /**
     * Sử dụng voucher (tăng usedCount)
     */
    void useVoucher(Discount discount);
}
