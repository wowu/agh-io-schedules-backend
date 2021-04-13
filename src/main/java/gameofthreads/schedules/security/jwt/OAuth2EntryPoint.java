package gameofthreads.schedules.security.jwt;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.server.resource.BearerTokenError;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

public class OAuth2EntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
            throws IOException {

        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String errorMessage = "Insufficient authentication details";
        Map<String, String> parameters = new LinkedHashMap<>();


        if (e instanceof OAuth2AuthenticationException) {
            OAuth2Error error = ((OAuth2AuthenticationException) e).getError();
            parameters.put("error", error.getErrorCode());

            if (StringUtils.hasText(error.getDescription())) {
                errorMessage = error.getDescription();
                parameters.put("error_description", errorMessage);
            }

            if (StringUtils.hasText(error.getUri())) {
                parameters.put("error_uri", error.getUri());
            }

            if (error instanceof BearerTokenError) {
                BearerTokenError bearerTokenError = (BearerTokenError) error;

                if (StringUtils.hasText(bearerTokenError.getScope())) {
                    parameters.put("scope", bearerTokenError.getScope());
                }

                status = ((BearerTokenError) error).getHttpStatus();
            }
        }

        String message = "{ \"ERROR\": \"" + errorMessage + "\" }";

        String wwwAuthenticate = computeWWWAuthenticateHeaderValue(parameters);
        response.addHeader("WWW-Authenticate", wwwAuthenticate);
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(message);
    }

    private String computeWWWAuthenticateHeaderValue(Map<String, String> parameters) {
        StringJoiner wwwAuthenticate = new StringJoiner(", ", "Bearer ", "");

        if (!parameters.isEmpty()) {
            parameters.forEach((k, v) -> wwwAuthenticate.add(k + "=\"" + v + "\""));
        }

        return wwwAuthenticate.toString();
    }

}
