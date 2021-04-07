package gameofthreads.schedules.service;

import gameofthreads.schedules.domain.UserInfo;
import gameofthreads.schedules.dto.request.AuthRequest;
import gameofthreads.schedules.entity.User;
import gameofthreads.schedules.repository.UserRepository;
import io.vavr.control.Try;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TokenService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public TokenService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username).orElseThrow();
        return new UserInfo(user);
    }

    public Map<String, String> authenticate(AuthRequest authRequest) {
        UserInfo userInfo = Try.of(() -> loadUserByUsername(authRequest.username))
                .map(user -> (UserInfo) user)
                .getOrElseThrow(() -> new UsernameNotFoundException("Wrong email."));

        if (passwordEncoder.matches(authRequest.password, userInfo.getPassword())) {
            return createClaimsMap(userInfo);
        }

        throw new UsernameNotFoundException("Wrong password.");
    }

    public Map<String, String> refresh(String username){
        return Try.of(() -> loadUserByUsername(username))
                .map(user -> (UserInfo) user)
                .map(this::createClaimsMap)
                .getOrElseThrow(() -> new UsernameNotFoundException("Wrong email."));
    }

    private Map<String, String> createClaimsMap(UserInfo userInfo){
        Map<String, String> claims = new HashMap<>();
        String role = userInfo.getRole();
        Integer id = userInfo.getId();
        claims.put("scope", role);
        claims.put("userId", id.toString());
        return claims;
    }

}
