package TechStore.app.service.impl;

import TechStore.app.constant.ConstantApi;
import TechStore.app.dto.VerifyTokenDto;
import TechStore.app.exception.JWTVerifyException;
import TechStore.app.service.AuthenticationService;
import TechStore.app.service.AuthorJWTService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    @Value("${header.author.token}")
    private String headerKeyAccess;
    @Value("${header.value.access.default}")
    private String valueAccessDefault;
    @Value("${header.value.admin.default}")
    private String valueAdminAccessDefault;
    @Value("${header.internal.token}")
    private String internalKeyAccess;
    @Autowired
    private AuthorJWTService authorJWTService;

    @Override
    public Authentication getAuthenticationCustomSignLevel(HttpServletRequest request) {
        String token = request.getHeader(headerKeyAccess);
        if (StringUtils.hasText(token) && token.equals(valueAccessDefault)) {
            return new UsernamePasswordAuthenticationToken(ConstantApi.SYS_MARKETPLACE, null,
                    Collections.emptyList());
        }
        return null;
    }

    @Override
    public Authentication getAuthenticationAdminSignLevel(HttpServletRequest request){
        String token = request.getHeader(headerKeyAccess);
        if(StringUtils.hasText(token) && token.equals(valueAdminAccessDefault)){
            return new UsernamePasswordAuthenticationToken(ConstantApi.SYS_MARKETPLACE, null,
                    Collections.emptyList());
        }
        return null;
    }

    @Override
    public Authentication getAuthentication(HttpServletRequest request, String role, boolean isEmployee, boolean isInternal) {
        String token = request.getHeader(headerKeyAccess);
        if (StringUtils.hasText(token)) {
            String account = null;
            VerifyTokenDto verifyTokenDto;
            try {
                verifyTokenDto = authorJWTService.verifyToken(token, isEmployee, isInternal);
                if(role.equalsIgnoreCase(verifyTokenDto.getRole())) {
                    account = verifyTokenDto.getEmail();
                }
            } catch (JWTVerifyException e) {
                return null;
            }
            return account != null ? new UsernamePasswordAuthenticationToken(account, null,
                    Collections.emptyList()) : null;
        }
        return null;
    }

    @Override
    public Authentication getAuthentication(String path, HttpServletRequest request, boolean isEmployee) {
        String token = request.getHeader(headerKeyAccess);
        if (StringUtils.hasText(token)) {
            String account = null;
            String role = null;
            VerifyTokenDto verifyTokenDto;
            try {
                verifyTokenDto = authorJWTService.verifyToken(token, isEmployee, false);
                role = verifyTokenDto.getRole();
                account = verifyTokenDto.getEmail();
            } catch (JWTVerifyException e) {
                return null;
            }
            return account != null ? new UsernamePasswordAuthenticationToken(account, role,
                    Collections.emptyList()) : null;

        }
        return null;
    }

    @Override
    public Authentication getAuthorInternalLevel(HttpServletRequest request) {
        String token = request.getHeader(headerKeyAccess);
        //log.info("x-access-token: {}", token)
        if (StringUtils.hasText(token)) {
            if (token.equals(internalKeyAccess)) {
                return new UsernamePasswordAuthenticationToken(ConstantApi.SYS_MARKETPLACE, null,
                        Collections.emptyList());
            }
            try {
                VerifyTokenDto verifyTokenDto = authorJWTService.verifyToken(token, false, true);
                return new UsernamePasswordAuthenticationToken(verifyTokenDto.getEmail(), null,
                        Collections.emptyList());
            } catch (JWTVerifyException e) {
                return null;
            }
        }

        return null;
    }


}
