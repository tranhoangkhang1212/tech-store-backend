package TechStore.app.dto;

import lombok.Data;

@Data
public class VerifyTokenDto {
    private String email;
    private String role;
    private Long userId;
}