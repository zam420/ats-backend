package com.deepconnect.ats.auth.model;

import java.time.Instant;
import java.time.LocalDateTime;

import org.hibernate.validator.constraints.Length;

import com.deepconnect.ats.user.model.User;

import jakarta.validation.constraints.Email;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
public class RefreshToken {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, unique = true, length = 100)
    @NotBlank(message = "{field.user.email}{validation.notBlank}")
    @Length(min = 7, max = 50, message = "{field.user.email}{validation.length}")
    @Email(message = "{field.user.email}{validation.email}")
    private String email;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;

    @Column(nullable = false)
    private LocalDateTime lastRefreshedDateTime;
    
}
