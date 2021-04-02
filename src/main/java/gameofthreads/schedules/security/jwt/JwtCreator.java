package gameofthreads.schedules.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Calendar;
import java.util.Map;

@Component
public class JwtCreator {
    private final static long EXPIRE_TIME_SECONDS = 600;
    private final RSAPublicKey publicKey;
    private final RSAPrivateKey privateKey;

    public JwtCreator(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public String createWithClaims(String subject, Map<String, String> claims){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Instant.now().plusSeconds(EXPIRE_TIME_SECONDS).toEpochMilli());

        JWTCreator.Builder jwtBuilder = JWT.create().withSubject(subject);
        claims.forEach(jwtBuilder::withClaim);

        return jwtBuilder
                .withExpiresAt(calendar.getTime())
                .sign(Algorithm.RSA256(publicKey, privateKey));
    }

}
