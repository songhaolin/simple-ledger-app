package com.ledger.config;

import com.ledger.exception.JwtValidationException;
import com.ledger.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * JWT拦截器
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(JwtInterceptor.class);

    private final JwtUtil jwtUtil;

    public JwtInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 获取请求路径
        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        // 跳过登录和注册接口的JWT验证
        if (requestURI != null && 
            (requestURI.contains("/users/login") || 
             requestURI.contains("/users/register") ||
             requestURI.contains("/categories"))) {
            return true;
        }

        // 获取Token
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for: {}", requestURI);
            throw new JwtValidationException("缺少或无效的Authorization头");
        }

        String token = authHeader.replace("Bearer ", "");

        try {
            // 验证Token
            if (!jwtUtil.validateToken(token)) {
                log.warn("Invalid JWT token for: {}", requestURI);
                throw new JwtValidationException("Token无效");
            }

            // 获取UserId
            String userId = jwtUtil.getUserIdFromToken(token);

            if (userId == null || userId.isEmpty()) {
                log.warn("Invalid userId from token for: {}", requestURI);
                throw new JwtValidationException("Token中无法获取用户ID");
            }

            // 设置UserId到请求属性
            request.setAttribute("userId", userId);
            request.setAttribute("isAuthenticated", true);

            log.debug("JWT token validated for userId: {}", userId);

            return true;
        } catch (JwtValidationException e) {
            // 重新抛出JWT验证异常
            throw e;
        } catch (Exception e) {
            log.error("JWT validation error: {}", e.getMessage());
            throw new JwtValidationException("Token验证失败");
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        // 可以在这里设置响应头等
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求完成后的清理工作
    }
}
