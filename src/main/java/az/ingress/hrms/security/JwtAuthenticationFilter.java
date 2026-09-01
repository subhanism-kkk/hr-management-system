package az.ingress.hrms.security;

import az.ingress.hrms.dto.auth.CheckAccessRequest;
import az.ingress.hrms.service.AuthClient;
import az.ingress.hrms.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AuthClient authClient;


    @Override
    protected boolean shouldNotFilter(
            HttpServletRequest request
    ) {

        String uri = request.getRequestURI();

        return request.getMethod().equalsIgnoreCase("OPTIONS")
                || uri.startsWith("/swagger-ui/")
                || uri.startsWith("/v3/api-docs/");
    }


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorizationHeader =
                request.getHeader("Authorization");


        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            return;
        }

        String token =
                authorizationHeader.substring(7);


        if (!jwtService.isTokenValid(token)) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );
            return;
        }


        String username =
                jwtService.extractUsername(token);


        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        Collections.emptyList()
                );


        String url =
                request.getRequestURI();

        String method =
                request.getMethod();


        CheckAccessRequest checkAccessRequest =
                CheckAccessRequest.builder()
                        .url(url)
                        .method(method)
                        .build();


        boolean hasAccess;

        try {

            hasAccess =
                    authClient.checkAccess(
                            authorizationHeader,
                            checkAccessRequest
                    );

        } catch (Exception e) {


            response.setStatus(
                    HttpServletResponse.SC_SERVICE_UNAVAILABLE
            );
            return;
        }


        if (!hasAccess) {

            response.setStatus(
                    HttpServletResponse.SC_FORBIDDEN
            );
            return;
        }


        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);


        filterChain.doFilter(request, response);
    }
}