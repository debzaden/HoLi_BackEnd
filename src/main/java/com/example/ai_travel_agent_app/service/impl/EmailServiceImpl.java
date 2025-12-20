package com.example.ai_travel_agent_app.service.impl;

import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.example.ai_travel_agent_app.dto.EmailDetails;
import com.example.ai_travel_agent_app.model.Booking;
import com.example.ai_travel_agent_app.model.Worker;
import com.example.ai_travel_agent_app.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public String sendEmail(EmailDetails emailDetails) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(emailDetails.getRecipient());
        helper.setSubject(emailDetails.getSubject());
        helper.setText(emailDetails.getMsgBody(), true); // Enable HTML content
        javaMailSender.send(message);
        return null;
    }

    @Override
    public void sendBookingNotificationToWorker(Booking booking) {
        try {
            String subject = "🔔 Bạn có yêu cầu đặt lịch mới";
            String content = buildWorkerNotificationEmail(booking);

            EmailDetails emailDetails = EmailDetails.builder()
                    .recipient(booking.getWorker().getUser().getEmail())
                    .subject(subject)
                    .msgBody(content)
                    .build();

            sendEmail(emailDetails);
        } catch (Exception e) {
            // Log error but don't fail the booking process
            System.err.println("Failed to send email to worker: " + e.getMessage());
        }
    }

    @Override
    public void sendBookingConfirmationToCustomer(Booking booking) {
        try {
            String subject = "✅ Xác nhận đặt lịch thành công";
            String content = buildCustomerConfirmationEmail(booking);

            EmailDetails emailDetails = EmailDetails.builder()
                    .recipient(booking.getCustomer().getUser().getEmail())
                    .subject(subject)
                    .msgBody(content)
                    .build();

            sendEmail(emailDetails);
        } catch (Exception e) {
            System.err.println("Failed to send confirmation email to customer: " + e.getMessage());
        }
    }

    @Override
    public void sendBookingAcceptedToCustomer(Booking booking) {
        try {
            String subject = "🎉 Yêu cầu đặt lịch đã được chấp nhận";
            String content = buildBookingAcceptedEmail(booking);

            String customerEmail = booking.getCustomer().getUser().getEmail();

            EmailDetails emailDetails = EmailDetails.builder()
                    .recipient(customerEmail)
                    .subject(subject)
                    .msgBody(content)
                    .build();

            sendEmail(emailDetails);
        } catch (Exception e) {
            System.err.println("Failed to send accepted email to customer: " + e.getMessage());
        }
    }

    @Override
    public void sendBookingRejectedToCustomer(Booking booking, String reason) {
        try {
            String subject = "❌ Yêu cầu đặt lịch đã bị từ chối";
            String content = buildBookingRejectedEmail(booking, reason);

            String customerEmail = booking.getCustomer().getUser().getEmail();

            EmailDetails emailDetails = EmailDetails.builder()
                    .recipient(customerEmail)
                    .subject(subject)
                    .msgBody(content)
                    .build();

            sendEmail(emailDetails);
        } catch (Exception e) {
            System.err.println("Failed to send rejected email to customer: " + e.getMessage());
        }
    }

    @Override
    public void sendBookingCompletedToCustomer(Booking booking) {
        try {
            String subject = "🌟 Công việc đã hoàn thành";
            String content = buildBookingCompletedEmail(booking);

            EmailDetails emailDetails = EmailDetails.builder()
                    .recipient(booking.getCustomer().getUser().getEmail())
                    .subject(subject)
                    .msgBody(content)
                    .build();

            sendEmail(emailDetails);
        } catch (Exception e) {
            System.err.println("Failed to send completed email to customer: " + e.getMessage());
        }
    }

    // Helper methods to build email content
    private String buildWorkerNotificationEmail(Booking booking) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String customerName = booking.getCustomer().getUser().getRealUserName();
        String serviceName = booking.getService().getServiceName();
        String startTime = booking.getStartTime().format(formatter);
        String endTime = booking.getEndTime().format(formatter);
        String location = booking.getLocation();

        return String.format(
                """
                        <html>
                        <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                            <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                                <h2 style="color: #2e7d32; text-align: center;">🔔 Yêu cầu đặt lịch mới</h2>

                                <div style="background-color: #f5f5f5; padding: 20px; border-radius: 8px; margin: 20px 0;">
                                    <h3 style="color: #1976d2; margin-top: 0;">Thông tin khách hàng:</h3>
                                    <p><strong>Tên:</strong> %s</p>
                                    <p><strong>Số điện thoại:</strong> %s</p>

                                    <h3 style="color: #1976d2;">Thông tin công việc:</h3>
                                    <p><strong>Dịch vụ:</strong> %s</p>
                                    <p><strong>Thời gian:</strong> %s - %s</p>
                                    <p><strong>Địa điểm:</strong> %s</p>
                                    <p><strong>Tổng tiền:</strong> %,.0f đ</p>

                                    %s
                                </div>

                                <div style="text-align: center; margin: 30px 0;">
                                    <p style="color: #666;">Vui lòng vào ứng dụng để xác nhận hoặc từ chối yêu cầu này.</p>
                                    <p style="color: #d32f2f; font-weight: bold;">⏰ Hạn xác nhận: trong vòng 24 giờ</p>
                                </div>

                                <div style="text-align: center; font-size: 12px; color: #999; border-top: 1px solid #eee; padding-top: 20px;">
                                    <p>Email này được gửi tự động từ hệ thống HoLi</p>
                                </div>
                            </div>
                        </body>
                        </html>
                        """,
                customerName,
                booking.getCustomer().getPhoneNumber() != null ? booking.getCustomer().getPhoneNumber()
                        : "Chưa cung cấp",
                serviceName,
                startTime,
                endTime,
                location,
                booking.getTotalPrice(),
                booking.getSpecialRequest() != null
                        ? "<p><strong>Ghi chú:</strong> " + booking.getSpecialRequest() + "</p>"
                        : "");
    }

    private String buildCustomerConfirmationEmail(Booking booking) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String workerName = getWorkerDisplayName(booking);
        String serviceName = booking.getService().getServiceName();
        String startTime = booking.getStartTime().format(formatter);
        String endTime = booking.getEndTime().format(formatter);

        return String.format(
                """
                        <html>
                        <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                            <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                                <h2 style="color: #2e7d32; text-align: center;">✅ Đặt lịch thành công</h2>

                                <div style="background-color: #e8f5e8; padding: 20px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #4caf50;">
                                    <p style="margin-top: 0; font-size: 16px;">Cảm ơn bạn đã sử dụng dịch vụ HoLi!</p>
                                    <p>Yêu cầu đặt lịch của bạn đã được ghi nhận và đang chờ người làm việc xác nhận.</p>
                                </div>

                                <div style="background-color: #f5f5f5; padding: 20px; border-radius: 8px; margin: 20px 0;">
                                    <h3 style="color: #1976d2; margin-top: 0;">Chi tiết đặt lịch:</h3>
                                    <p><strong>Mã đặt lịch:</strong> #%d</p>
                                    <p><strong>Dịch vụ:</strong> %s</p>
                                    <p><strong>Người thực hiện:</strong> %s</p>
                                    <p><strong>Thời gian:</strong> %s - %s</p>
                                    <p><strong>Địa điểm:</strong> %s</p>
                                    <p><strong>Tổng tiền:</strong> %,.0f đ</p>
                                </div>

                                <div style="background-color: #fff3cd; padding: 15px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #ffc107;">
                                    <p style="margin: 0; color: #856404;"><strong>⏰ Lưu ý:</strong> Người làm việc sẽ xác nhận trong vòng 24 giờ. Bạn sẽ nhận được email thông báo khi có cập nhật.</p>
                                </div>

                                <div style="text-align: center; font-size: 12px; color: #999; border-top: 1px solid #eee; padding-top: 20px;">
                                    <p>Email này được gửi tự động từ hệ thống HoLi</p>
                                </div>
                            </div>
                        </body>
                        </html>
                        """,
                booking.getBookingId(),
                serviceName,
                workerName,
                startTime,
                endTime,
                booking.getLocation(),
                booking.getTotalPrice());
    }

    private String buildBookingAcceptedEmail(Booking booking) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String workerName = getWorkerDisplayName(booking);
        String serviceName = booking.getService().getServiceName();
        String startTime = booking.getStartTime().format(formatter);

        return String.format(
                """
                        <html>
                        <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                            <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                                <h2 style="color: #2e7d32; text-align: center;">🎉 Yêu cầu đã được chấp nhận</h2>

                                <div style="background-color: #e8f5e8; padding: 20px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #4caf50;">
                                    <p style="margin-top: 0; font-size: 16px;"><strong>Tin vui!</strong> Yêu cầu đặt lịch #%d của bạn đã được chấp nhận.</p>
                                    <p><strong>%s</strong> sẽ thực hiện dịch vụ <strong>%s</strong> vào lúc <strong>%s</strong>.</p>
                                </div>

                                <div style="background-color: #f5f5f5; padding: 20px; border-radius: 8px; margin: 20px 0;">
                                    <h3 style="color: #1976d2; margin-top: 0;">Thông tin liên hệ:</h3>
                                    <p><strong>Tên người làm việc:</strong> %s</p>
                                    <p><strong>Số điện thoại:</strong> %s</p>
                                </div>

                                <div style="background-color: #e3f2fd; padding: 15px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #2196f3;">
                                    <p style="margin: 0; color: #0d47a1;"><strong>💡 Lưu ý:</strong> Vui lòng chuẩn bị sẵn công cụ và không gian làm việc. Người làm việc sẽ đến đúng giờ đã hẹn.</p>
                                </div>

                                <div style="text-align: center; font-size: 12px; color: #999; border-top: 1px solid #eee; padding-top: 20px;">
                                    <p>Email này được gửi tự động từ hệ thống HoLi</p>
                                </div>
                            </div>
                        </body>
                        </html>
                        """,
                booking.getBookingId(),
                workerName,
                serviceName,
                startTime,
                workerName,
                booking.getWorker().getPhoneNumber() != null ? booking.getWorker().getPhoneNumber() : "Chưa cung cấp");
    }

    private String buildBookingRejectedEmail(Booking booking, String reason) {
        String serviceName = booking.getService().getServiceName();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String startTime = booking.getStartTime().format(formatter);

        return String.format(
                """
                        <html>
                        <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                            <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                                <h2 style="color: #d32f2f; text-align: center;">❌ Yêu cầu đã bị từ chối</h2>

                                <div style="background-color: #ffebee; padding: 20px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #f44336;">
                                    <p style="margin-top: 0; font-size: 16px;">Rất tiếc, yêu cầu đặt lịch #%d của bạn đã bị từ chối.</p>
                                    <p><strong>Dịch vụ:</strong> %s</p>
                                    <p><strong>Thời gian:</strong> %s</p>
                                </div>

                                %s

                                <div style="background-color: #e3f2fd; padding: 15px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #2196f3;">
                                    <p style="margin: 0; color: #0d47a1;"><strong>💡 Gợi ý:</strong> Bạn có thể thử đặt lịch với người làm việc khác hoặc chọn thời gian khác phù hợp hơn.</p>
                                </div>

                                <div style="text-align: center; font-size: 12px; color: #999; border-top: 1px solid #eee; padding-top: 20px;">
                                    <p>Email này được gửi tự động từ hệ thống HoLi</p>
                                </div>
                            </div>
                        </body>
                        </html>
                        """,
                booking.getBookingId(),
                serviceName,
                startTime,
                reason != null && !reason.trim().isEmpty() ? String.format(
                        """
                                <div style="background-color: #fff3cd; padding: 15px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #ffc107;">
                                    <p style="margin: 0; color: #856404;"><strong>Lý do:</strong> %s</p>
                                </div>
                                """,
                        reason) : "");
    }

    private String buildBookingCompletedEmail(Booking booking) {
        String workerName = getWorkerDisplayName(booking);
        String serviceName = booking.getService().getServiceName();

        return String.format(
                """
                        <html>
                        <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                            <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                                <h2 style="color: #2e7d32; text-align: center;">🌟 Công việc đã hoàn thành</h2>

                                <div style="background-color: #e8f5e8; padding: 20px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #4caf50;">
                                    <p style="margin-top: 0; font-size: 16px;"><strong>Tuyệt vời!</strong> Công việc #%d đã được hoàn thành.</p>
                                    <p><strong>%s</strong> đã hoàn thành dịch vụ <strong>%s</strong> cho bạn.</p>
                                </div>

                                <div style="background-color: #fff3cd; padding: 15px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #ffc107;">
                                    <p style="margin: 0; color: #856404;"><strong>💝 Đánh giá dịch vụ:</strong> Hãy dành ít phút để đánh giá chất lượng dịch vụ và chia sẻ trải nghiệm của bạn!</p>
                                </div>

                                <div style="text-align: center; margin: 30px 0;">
                                    <p style="font-size: 18px; color: #2e7d32;"><strong>Cảm ơn bạn đã sử dụng dịch vụ HoLi! 🙏</strong></p>
                                </div>

                                <div style="text-align: center; font-size: 12px; color: #999; border-top: 1px solid #eee; padding-top: 20px;">
                                    <p>Email này được gửi tự động từ hệ thống HoLi</p>
                                </div>
                            </div>
                        </body>
                        </html>
                        """,
                booking.getBookingId(),
                workerName,
                serviceName);
    }

    /**
     * Helper method để lấy tên worker với fallback
     */
    private String getWorkerDisplayName(Booking booking) {
        Worker worker = booking.getWorker();
        if (worker == null) {
            return "Người làm việc";
        }

        // Ưu tiên fullName từ Worker
        if (worker.getFullName() != null && !worker.getFullName().trim().isEmpty()) {
            return worker.getFullName();
        }

        // Fallback sang realUserName từ User
        if (worker.getUser() != null && worker.getUser().getRealUserName() != null
                && !worker.getUser().getRealUserName().trim().isEmpty()) {
            return worker.getUser().getRealUserName();
        }

        // Fallback sang email
        if (worker.getUser() != null && worker.getUser().getEmail() != null
                && !worker.getUser().getEmail().trim().isEmpty()) {
            return worker.getUser().getEmail().split("@")[0]; // Lấy phần trước @ của email
        }

        // Fallback cuối cùng
        return "Người làm việc";
    }
}
