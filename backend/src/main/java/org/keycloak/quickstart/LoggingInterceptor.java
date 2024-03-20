package org.keycloak.quickstart;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Map<String, Object> logMap = new HashMap<>();
        logMap.put("Request URL", request.getRequestURL().toString());
        logMap.put("Request Method", request.getMethod());
        logMap.put("Request Parameters", request.getParameterMap());

        try {
            String logJson = objectMapper.writeValueAsString(logMap);
            logger.info("Request: {}", logJson);
        } catch (Exception e) {
            logger.error("Error while logging request details", e);
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        Map<String, Object> logMap = new HashMap<>();
        logMap.put("Response Status", response.getStatus());

        try {
            String logJson = objectMapper.writeValueAsString(logMap);
            logger.info("Response: {}", logJson);
        } catch (Exception e) {
            logger.error("Error while logging response details", e);
        }
    }
}


