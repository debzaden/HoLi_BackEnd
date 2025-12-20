package com.example.ai_travel_agent_app.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ai_travel_agent_app.model.Discount;
import com.example.ai_travel_agent_app.model.DiscountType;
import com.example.ai_travel_agent_app.repository.DiscountRepository;
import com.example.ai_travel_agent_app.service.DiscountService;

@Service
public class DiscountServiceImpl implements DiscountService {

    @Autowired
    private DiscountRepository discountRepository;

    @Override
    public Discount validateVoucher(String code, float orderAmount) {
        Discount discount = discountRepository.findByCodeAndIsActiveTrue(code)
                .orElseThrow(() -> new RuntimeException("Mã voucher không hợp lệ hoặc đã hết hạn"));

        // Kiểm tra thời hạn
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(discount.getStartDate()) || now.isAfter(discount.getEndDate())) {
            throw new RuntimeException("Mã voucher đã hết hạn sử dụng");
        }

        // Kiểm tra số lần sử dụng
        if (discount.getUsedCount() >= discount.getUsageLimit()) {
            throw new RuntimeException("Mã voucher đã hết lượt sử dụng");
        }

        // Kiểm tra giá trị đơn hàng tối thiểu
        if (orderAmount < discount.getMinOrderAmount()) {
            throw new RuntimeException("Đơn hàng chưa đạt giá trị tối thiểu để sử dụng voucher");
        }

        return discount;
    }

    @Override
    public float calculateDiscountAmount(Discount discount, float orderAmount) {
        float discountAmount = 0;

        if (discount.getType() == DiscountType.PERCENTAGE) {
            discountAmount = orderAmount * (discount.getValue() / 100);
        } else if (discount.getType() == DiscountType.FIXED_AMOUNT) {
            discountAmount = discount.getValue();
        }

        // Áp dụng giới hạn giảm giá tối đa
        if (discount.getMaxDiscountAmount() > 0 && discountAmount > discount.getMaxDiscountAmount()) {
            discountAmount = discount.getMaxDiscountAmount();
        }

        return Math.min(discountAmount, orderAmount); // Không được giảm quá giá trị đơn hàng
    }

    @Override
    public void useVoucher(Discount discount) {
        discount.setUsedCount(discount.getUsedCount() + 1);
        discountRepository.save(discount);
    }
}
