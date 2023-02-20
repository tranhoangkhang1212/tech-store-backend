package TechStore.app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenInfoDto {
    private String token;
    private long expiresAt;

    public TokenInfoDto(String token, long expiresAt) {
        this.token = token;
        this.expiresAt = expiresAt;
    }
}