package com.testing.ex.security;

import com.testing.ex.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

/**
 * Security filter that validates JWT tokens on incoming requests. If a valid
 * token is present the filter loads UserDetails and sets the Spring Security
 * context. Additionally, when the underlying UserDetails implementation is
 * {@link TestingUserDetails} the filter writes the user's id into the
 * request attribute "userId" for tenant-aware controllers.
 */
@RequiredArgsConstructor
public class JwtSecurityFilter extends OncePerRequestFilter {

    public final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final HandlerExceptionResolver exceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = extractToken(request);

            if(token == null){
                filterChain.doFilter(request, response);
                return;
            }

            String username = jwtService.extractUsername(token);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if(!jwtService.validateToken(token, userDetails)) {
                filterChain.doFilter(request, response);
                return;
            }

            if(userDetails instanceof TestingUserDetails testingUserDetails) {
                String userId = testingUserDetails.getId();
                request.setAttribute("userId", userId);
            }

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );

            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception e) {
            exceptionResolver.resolveException(request, response, null, e);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        return (authHeader != null && authHeader.startsWith("Bearer "))
                ? authHeader.substring(7)
                : null;
    }
}
