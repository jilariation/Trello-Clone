package ru.jilly.trelloclone.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        final String expired = (String) request.getAttribute("expired");
        System.out.println(expired);
        if (expired!=null){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,expired);
        }else{
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Invalid Login details");
        }
    }
}
