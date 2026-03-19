package com.bankcards.entity;

import com.bankcards.entity.enums.RoleUsers;
//import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")

@Getter
@Setter
@RequiredArgsConstructor
//@ToString(exclude = {"password"})
//@Accessors(chain = true)
//@Hidden
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotBlank(message = "Email is required")
    @Column(name = "email", nullable = false)
    @jakarta.validation.constraints.NotNull
    private String email;

    @NotBlank(message = "Full name is required")
    @NotNull
    private String fullName;

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
