package TechStore.app.config;

import TechStore.app.constant.ERole;
import TechStore.app.service.AuthenticationService;
import TechStore.app.util.FunctionInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class AuthenticationFilter extends OncePerRequestFilter {
    private static final List<String> adminLevel = Collections.singletonList("/admin");
    private static final List<String> customerLevel = Collections.singletonList("/api");
    private static final List<String> internalAuthorLevel = Collections.singletonList("/internal");
    private static final List<String> internalGame = Collections.singletonList("/game-inter");
    private static final List<String> adminSignLevel = Arrays.asList("/sign-in", "/reset-code", "/create-market-place-key", "verify-otp", "/test");
    private static final List<String> customSignLevel = Arrays.asList("/sign-up", "/sign-in", "/products");
    @Autowired
    private AuthenticationService authenticationService;

    @Override
    public void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws IOException, ServletException {
        Authentication authentication = null;
        String path = httpServletRequest.getRequestURI()
                .substring(httpServletRequest.getContextPath().length());

        authentication = buildAuthentication(path, httpServletRequest);

        if (authentication != null) {
            Object accountObj = authentication.getPrincipal();
            if (StringUtils.hasText((String) accountObj)) {
                String account = (String) accountObj;
                if (!ObjectUtils.isEmpty(account)) {
                    httpServletResponse.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                }
            }
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private Authentication buildAuthentication(String path, HttpServletRequest httpServletRequest) {
        Authentication authentication = null;
        if (match(path, adminLevel, String::startsWith)) {
            authentication = authenticationForAdminLevel(path, httpServletRequest);
        } else {
            authentication = authenticationForCustomerLevel(path, httpServletRequest);
        }
        return authentication;
    }

    private Authentication authenticationForCustomerLevel(String path, HttpServletRequest httpServletRequest) {
        if (match(path, customSignLevel, String::endsWith)) {
            return authenticationService.getAuthenticationCustomSignLevel(httpServletRequest);
        }
        boolean isInternal = match(path, internalGame, String::startsWith);
        return authenticationService.getAuthentication(httpServletRequest, ERole.USER.getName(), false, isInternal);
    }

    private Authentication authenticationForAdminLevel(String path, HttpServletRequest httpServletRequest) {
        if (match(path, adminSignLevel, String::endsWith)) {
            return authenticationService.getAuthenticationAdminSignLevel(httpServletRequest);
        } else {
            return authenticationService.getAuthentication(path, httpServletRequest, true);
        }
    }

    private static boolean match(
            String input,
            List<String> list,
            FunctionInterface.Function2<String, String, Boolean> predicate
    ) {
        for (String item : list) {
            if (Boolean.TRUE.equals(predicate.apply(input, item))) {
                return true;
            }
        }

        return false;
    }
}
