package gameofthreads.schedules.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import gameofthreads.schedules.dto.response.AuthResponse;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Calendar;
import java.util.Map;

@Component
public class JwtCreator {
    private final static long REFRESH_EXPIRATION_TIME =  2700; //45 minutes
    private final static long ACCESS_EXPIRATION_TIME = 600; // 10 minutes
    private final RSAPublicKey publicKey;
    private final RSAPrivateKey privateKey;

    public JwtCreator(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public AuthResponse createWithClaims(String subject, Map<String, String> claims){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Instant.now().plusSeconds(ACCESS_EXPIRATION_TIME).toEpochMilli());

        //create access token
        JWTCreator.Builder jwtBuilder = JWT.create().withSubject(subject);
        claims.forEach(jwtBuilder::withClaim);

        String token = jwtBuilder
                .withExpiresAt(calendar.getTime())
                .sign(Algorithm.RSA256(publicKey, privateKey));

        //create refresh token
        calendar.setTimeInMillis(Instant.now().plusSeconds(REFRESH_EXPIRATION_TIME).toEpochMilli());
        jwtBuilder = JWT.create().withSubject(subject);
        jwtBuilder.withClaim("refresh", "yes");

        String refreshToken = jwtBuilder
                .withExpiresAt(calendar.getTime())
                .sign(Algorithm.RSA256(publicKey, privateKey));

        return new AuthResponse(token, refreshToken);
    }

}
