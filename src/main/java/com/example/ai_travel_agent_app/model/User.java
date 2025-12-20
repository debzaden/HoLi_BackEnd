package com.example.ai_travel_agent_app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, columnDefinition = "NVARCHAR(255)")
    private String userName;

    @Column(nullable = false, columnDefinition = "NVARCHAR(255)")
    @Email
    private String email;

    @NotBlank(message = "Not found password")
    @Column(nullable = false, columnDefinition = "NVARCHAR(255)")
    private String password;

    @Column(columnDefinition = "NVARCHAR(500)")
    private String avatar;

    @Column(columnDefinition = "NVARCHAR(100)")
    private String provider;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean enabled = false;

    private LocalDate createdAt;

    private LocalDate updatedAt;

    public User(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    public String getRealUserName() {
        return userName;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
