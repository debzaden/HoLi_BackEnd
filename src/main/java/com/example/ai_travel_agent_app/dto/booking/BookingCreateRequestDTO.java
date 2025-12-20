package com.example.ai_travel_agent_app.dto.booking;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingCreateRequestDTO {

    @NotNull(message = "Worker ID không được để trống")
    private Long workerId;

    @NotNull(message = "Service ID không được để trống")
    private Long serviceId;

    @NotNull(message = "Ngày làm việc không được để trống")
    private LocalDate workDate;

    @NotNull(message = "Buổi làm việc không được để trống")
    private String timeSlot; // "morning", "afternoon", "evening"

    @NotNull(message = "Giờ bắt đầu không được để trống")
    private LocalTime startTime;

    @NotNull(message = "Thời lượng không được để trống")
    @Min(value = 1, message = "Thời lượng tối thiểu là 1 giờ")
    @Max(value = 4, message = "Thời lượng tối đa là 4 giờ")
    private Integer duration; // số giờ làm việc (1-4)

    private String notes; // ghi chú

    @NotNull(message = "Địa chỉ không được để trống")
    private String address;

    private String voucherCode; // mã voucher (tùy chọn)
}
