package TechStore.app.service;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

public interface AuthenticationService {
    Authentication getAuthenticationCustomSignLevel(HttpServletRequest request);

    Authentication getAuthenticationAdminSignLevel(HttpServletRequest request);

    Authentication getAuthentication(HttpServletRequest request, String role, boolean isEmployee, boolean isInternal);

    Authentication getAuthentication(String path, HttpServletRequest httpServletRequest, boolean isEmployee);
}
