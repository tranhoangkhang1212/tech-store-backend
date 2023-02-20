package TechStore.app.service;

import TechStore.app.dto.TokenInfoDto;
import TechStore.app.dto.VerifyTokenDto;
import TechStore.app.exception.JWTVerifyException;

public interface AuthorJWTService {
    TokenInfoDto generateToken(String email, Long id, String role, boolean isEmployee);
    VerifyTokenDto verifyToken(String token, boolean isEmployee, boolean isInternal) throws JWTVerifyException;
}
