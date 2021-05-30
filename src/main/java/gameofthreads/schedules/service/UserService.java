package gameofthreads.schedules.service;

import gameofthreads.schedules.dto.request.AddUserRequest;
import gameofthreads.schedules.dto.response.UserResponse;
import gameofthreads.schedules.dto.response.UserResponseList;
import gameofthreads.schedules.entity.EmailEntity;
import gameofthreads.schedules.entity.LecturerEntity;
import gameofthreads.schedules.entity.Role;
import gameofthreads.schedules.entity.UserEntity;
import gameofthreads.schedules.message.ErrorMessage;
import gameofthreads.schedules.repository.EmailRepository;
import gameofthreads.schedules.repository.LecturerRepository;
import gameofthreads.schedules.repository.UserRepository;
import io.vavr.control.Either;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final EmailRepository emailRepository;
    private final UserRepository userRepository;

    public UserService(PasswordEncoder passwordEncoder, EmailRepository emailRepository, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.emailRepository = emailRepository;
        this.userRepository = userRepository;
    }

    private boolean isUserARole(JwtAuthenticationToken jwtToken, String role) {
        return jwtToken.getTokenAttributes().get("scope").equals(role);
    }

    @Transactional
    public UserResponseList getAll() {
        List<UserResponse> users = userRepository.findAll()
                .stream()
                .map(UserEntity::getEmailEntity)
                .map(UserResponse::new)
                .collect(Collectors.toList());

        return new UserResponseList(users);
    }

    @Transactional
    public Pair<HttpStatus, ?> get(Integer id, JwtAuthenticationToken jwtToken) {
        Optional<UserEntity> userEntity = userRepository.findById(id);
        String tokenOwnerId = (String) jwtToken.getTokenAttributes().get("userId");
        Integer tokenOwnerIdAsInt = Integer.parseInt(tokenOwnerId);

        if (isUserARole(jwtToken, Role.LECTURER.name()) && !tokenOwnerIdAsInt.equals(id)) {
            return Pair.of(HttpStatus.FORBIDDEN, ErrorMessage.FORBIDDEN_USER.asJson());
        }

        if (userEntity.isEmpty()) {
            return Pair.of(HttpStatus.BAD_REQUEST, ErrorMessage.WRONG_USER_ID.asJson());
        }

        return Pair.of(HttpStatus.OK, userEntity.get().buildDto());
    }

    @Transactional
    public Either<Object, Boolean> delete(Integer id) {
        if (userRepository.findById(id).isEmpty()) {
            return Either.left(ErrorMessage.WRONG_USER_ID.asJson());
        }
        userRepository.deleteById(id);
        return Either.right(true);
    }

    @Transactional
    public Either<Object, UserResponse> add(AddUserRequest userRequest) {
        Optional<EmailEntity> lecturerEmail = emailRepository
                .findByEmail(userRequest.email)
                .filter(email -> email.getLecturer() != null);

        if (lecturerEmail.isEmpty()) {
            return Either.left(ErrorMessage.NO_LECTURER_WITH_EMAIL.asJson());
        }

        String password = passwordEncoder.encode(userRequest.password);

        EmailEntity emailEntity = lecturerEmail.get();
        UserEntity userEntity = new UserEntity(password);
        userEntity.setEmail(emailEntity);
        emailEntity.setUser(userEntity);
        emailRepository.save(emailEntity);

        return Either.right(new UserResponse(emailEntity));
    }

    @Transactional
    public Either<Object, UserResponse> update(Integer id, String password) {
        Optional<UserEntity> userEntity = userRepository.findById(id);

        if (userEntity.isEmpty()) {
            return Either.left(ErrorMessage.WRONG_USER_ID.asJson());
        }

        if (password != null) {
            UserEntity user = userEntity.get();
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
        }

        return Either.right(new UserResponse(Objects.requireNonNull(userEntity.get().getEmailEntity())));
    }
}
