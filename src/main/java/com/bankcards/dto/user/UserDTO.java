package com.bankcards.dto.user;

import com.bankcards.entity.enums.RoleUsers;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@RequiredArgsConstructor
@Schema(description = "DTO для представления информации о пользователе")
public class UserDTO {
    private long id;

    @NotBlank(message = "Email is required")
    @Email
    @Schema(description = "Email пользователя", example = "testuser@example.com")
    private String email;

    private String fullName;

    @NotBlank(message = "Last name is required")
    @Schema(description = "Фамилия пользователя", example = "Иванов")
    private String lastName;

    @NotBlank(message = "First name is required")
    @Schema(description = "Имя пользователя", example = "Иван")
    private String firstName;

    @NotBlank(message = "Middle name is required")
    @Schema(description = "Отчество пользователя", example = "Иванович")
    private String middleName;

    @NotBlank(message = "Phone number is required")
    @Schema(description = "Номер телефона пользователя", example = "+79206460555")
    private String phoneNumber;

    @Schema(description = "Роль пользователя", example = "USER")
    private RoleUsers role;

    public UserDTO(long id, String email, String fullName, String lastName, String firstName, String middleName, String phoneNumber, RoleUsers role) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }
}
