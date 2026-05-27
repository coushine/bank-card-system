package com.bankcards.entity;

import com.bankcards.entity.enums.RoleUsers;
//import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@RequiredArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Email(message = "Invalid email format")
    @Column(name = "email", nullable = false)
    private String email;

    @NotBlank(message = "Last name is required")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotBlank(message = "First name is required")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "Middle name is required")
    @Column(name = "middle_name", nullable = false)
    private String middleName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(\\+7|8)\\d{10}$", message = "Phone must match Russian format")
    @Column(name = "phone_number")
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING) //Нужно для того, чтобы JPA/Hibernate сохранял enum как VARCHAR в sql
    @Column(name = "role", nullable = false)
    private RoleUsers role;

    //Указываем bi-directional связь.
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CardEntity> cardEntities = new ArrayList<>();
}
