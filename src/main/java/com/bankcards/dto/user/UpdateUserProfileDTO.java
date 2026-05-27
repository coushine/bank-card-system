package com.bankcards.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Schema(description = "Обновление данных профиля")
@Data
public class UpdateUserProfileDTO {

    @NotBlank(message = "Требуется Email")
    @Email(message = "Некорректный формат Email")
    private String email;

    @NotBlank(message = "Требуется фамилия")
    private String lastName;

    @NotBlank(message = "Требуется имя")
    private String firstName;

    @NotBlank(message = "Требуется отчество")
    private String middleName;

    @Schema(description = "номер телефона", example = "+79001234567")
    @NotBlank(message = "требуется номер телефона")
    @Pattern(regexp = "^(\\+7|8)[0-9]{10}$", message = "Неверный формат номера телефона")
    private String phoneNumber;
}
