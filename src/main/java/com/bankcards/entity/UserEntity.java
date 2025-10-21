package com.bankcards.entity;

import com.bankcards.entity.enums.RoleUsers;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import jakarta.validation.Valid;
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
@ToString(exclude = {"password"})
@Accessors(chain = true)
@Valid
@Hidden
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "fullname")
    private String fullName;

    @Column(name = "password", length = 1000)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CardEntity> cardEntities = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private RoleUsers role;


}
