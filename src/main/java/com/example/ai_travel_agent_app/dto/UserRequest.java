package com.example.ai_travel_agent_app.dto;



import com.example.ai_travel_agent_app.annotation.UniqueEmail;
import com.example.ai_travel_agent_app.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {


    @NotBlank(message = "Vui lòng nhập họ và tên")
    private String userName;

    @Email(message = "Nhập đúng định dạng email")
    @NotBlank(message = "Vui lòng nhập email")
    @UniqueEmail
    private String email;

    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password;

    private String role;

    @Override
    public String toString() {
        return "UserModel{" +
                "userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
