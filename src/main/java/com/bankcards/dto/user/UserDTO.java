package com.bankcards.dto.user;

import com.bankcards.entity.enums.RoleUsers;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@RequiredArgsConstructor
//@Schema(description = "DTO для представления информации о пользователе")
public class UserDTO {
    private long id;

    //@Schema(description = "Email пользователя", example = "testuser@example.com")
    private String email;

    //@Schema(description = "Полное имя пользователя", example = "John Doe")
    private String fullName;

    //@Schema(description = "Роль пользователя", example = "USER")
    private RoleUsers role;

    public UserDTO(long id, String email, String fullName, RoleUsers role) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }

    public UserDTO(String email, String fullName, RoleUsers role) {
        this.role = role;
        this.fullName = fullName;
        this.email = email;
    }
}
