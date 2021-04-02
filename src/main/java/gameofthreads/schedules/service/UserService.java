package gameofthreads.schedules.service;

import gameofthreads.schedules.domain.UserInfo;
import gameofthreads.schedules.dto.request.AuthRequest;
import gameofthreads.schedules.entity.User;
import gameofthreads.schedules.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username).orElseThrow();
        return new UserInfo(user);
    }

    public Map<String, String> authenticate(AuthRequest authRequest){
        UserInfo user;

        try {
            user = (UserInfo) loadUserByUsername(authRequest.username);
        }catch (UsernameNotFoundException e){
            throw new UsernameNotFoundException("User doesn't exist.");
        }

        if (passwordEncoder.matches(authRequest.password, user.getPassword())) {
            Map<String, String> claims = new HashMap<>();

            String role = user.getRole();
            Integer id = user.getId();

            claims.put("role", role);
            claims.put("userId", id.toString());

            return claims;
        }

        throw new UsernameNotFoundException("Wrong password.");
    }

}
