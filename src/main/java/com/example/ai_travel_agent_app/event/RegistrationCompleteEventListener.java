package com.example.ai_travel_agent_app.event;

import com.example.ai_travel_agent_app.dto.EmailDetails;
import com.example.ai_travel_agent_app.model.User;
import com.example.ai_travel_agent_app.service.EmailService;
import com.example.ai_travel_agent_app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Comments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
@Slf4j
public class RegistrationCompleteEventListener implements
        ApplicationListener<RegistrationCompleteEvent> {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        // Create the Verification Token for the User with link
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.saveVerificationTokenForUser(token, user);

        // Send Mail to user
        String url = event.getApplicationUrl()
                + "/verifyRegistration?token="
                + token;

        String subject = "HoLI - Xác thực tài khoản";
        String content = """
                        <!DOCTYPE html>
                        <html lang="vi">
                        <head>
                          <meta charset="UTF-8" />
                        </head>
                        <body style="background-color: #f0fdf4; padding: 20px;">
                          <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; background-color: white; border-radius: 10px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); padding: 30px;">
                            <h2 style="color: #16a34a;">Chào mừng bạn đến với HoLI!</h2>
                            <p>Xin chào <strong>%s</strong>,</p>
                            <p>Cảm ơn bạn đã đăng ký tài khoản tại <strong>HoLI – Hệ sinh thái kết nối dịch vụ đáng tin cậy</strong>.</p>
                            <p>Vui lòng xác thực tài khoản của bạn bằng cách nhấn vào nút bên dưới:</p>
                            <p><a href="%s" style="display: inline-block; background-color: #16a34a; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;">Xác thực tài khoản</a></p>
                            <p>Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email này.</p>
                            <p style="margin-top: 30px; color: #666;">Trân trọng,<br>Đội ngũ HoLI</p>
                          </div>
                        
                        </body>
                        </html>
                        """.formatted(user.getRealUserName(), url);



        // send VerificationEmail()
        EmailDetails emailDetails = new EmailDetails(user.getEmail(), content, subject);

        try {
            emailService.sendEmail(emailDetails);
        } catch (Exception exception)  {
            throw  new RuntimeException(exception.getMessage());
        }
        log.info("Click the link to verify your account: {}", url);
    }
}
