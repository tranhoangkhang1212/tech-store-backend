package TechStore.app.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserToken {
    private String token;
    private Long userId;
    private String origin;
    private String userAgent;
    private Date expiresAt;
}

