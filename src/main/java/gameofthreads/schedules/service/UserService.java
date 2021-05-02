package gameofthreads.schedules.service;

import gameofthreads.schedules.dto.request.AddUserRequest;
import gameofthreads.schedules.dto.response.UserResponse;
import gameofthreads.schedules.dto.response.UserResponseList;
import gameofthreads.schedules.entity.EmailEntity;
import gameofthreads.schedules.entity.LecturerEntity;
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
    private final LecturerRepository lecturerRepository;

    public UserService(PasswordEncoder passwordEncoder, EmailRepository emailRepository,
                       UserRepository userRepository, LecturerRepository lecturerRepository) {

        this.passwordEncoder = passwordEncoder;
        this.emailRepository = emailRepository;
        this.userRepository = userRepository;
        this.lecturerRepository = lecturerRepository;
    }

    private boolean isUserARole(JwtAuthenticationToken jwtToken, String role) {
        return jwtToken.getTokenAttributes().get("scope").equals(role);
    }

    @Transactional
    public UserResponseList getAll() {
        List<UserResponse> users = lecturerRepository.fetchWithUser()
                .stream()
                .map(LecturerEntity::getEmailEntity)
                .filter(emailEntity -> emailEntity.getUser() != null)
                .map(UserResponse::new)
                .collect(Collectors.toList());

        return new UserResponseList(users);
    }

    @Transactional
    public Pair<HttpStatus, ?> get(Integer id, JwtAuthenticationToken jwtToken) {
        Optional<UserEntity> userEntity = userRepository.findById(id);
        Optional<LecturerEntity> lecturerEntity = lecturerRepository.findByEmail_Email((String) jwtToken.getTokenAttributes().get("sub"));

        if (userEntity.isEmpty()) {
            return Pair.of(HttpStatus.BAD_REQUEST, ErrorMessage.WRONG_USER_ID.asJson());
        }

        EmailEntity emailEntity = userEntity.get().getEmailEntity();

        if (emailEntity.getUser() == null) {
            return Pair.of(HttpStatus.BAD_REQUEST, ErrorMessage.NO_USER_WITH_EMAIL.asJson());
        }

        if (isUserARole(jwtToken, "LECTURER") && lecturerEntity.isPresent() &&
                lecturerEntity.stream()
                        .map(LecturerEntity::getEmailEntity)
                        .map(EmailEntity::getEmail).noneMatch(s -> s.equals(emailEntity.getEmail()))) {
            return Pair.of(HttpStatus.FORBIDDEN, ErrorMessage.FORBIDDEN_USER.asJson());
        }

        return Pair.of(HttpStatus.OK, new UserResponse(emailEntity));
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
        UserEntity userEntity = new UserEntity(password);

        userEntity.setEmail(lecturerEmail.get());
        lecturerEmail.get().setUser(userEntity);
        userRepository.save(userEntity);

        return Either.right(new UserResponse(userEntity.getEmailEntity()));
    }

    @Transactional
    public Either<Object, UserResponse> update(Integer id, String password, Boolean activeSubscription, String email) {
        Optional<UserEntity> userEntity = userRepository.findById(id);

        if (userEntity.isEmpty()) {
            return Either.left(ErrorMessage.WRONG_USER_ID.asJson());
        }

        if (activeSubscription != null) {
            userEntity
                    .map(UserEntity::getEmailEntity)
                    .map(EmailEntity::getLecturer)
                    .ifPresent(lecturer -> {
                        lecturer.setActiveSubscription(activeSubscription);
                        lecturerRepository.save(lecturer);
                    });
        }

        if (password != null) {
            UserEntity user = userEntity.get();
            user.setPassword(password);
            userRepository.save(user);
        }

        if(email != null){
            userEntity.map(UserEntity::getEmailEntity)
                    .ifPresent(entity -> {
                        entity.setEmail(email);
                        emailRepository.save(entity);
                    });
        }

        return Either.right(new UserResponse(Objects.requireNonNull(userEntity.get().getEmailEntity())));
    }
}
