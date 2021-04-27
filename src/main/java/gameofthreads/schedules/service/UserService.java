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
import io.vavr.control.Try;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final LecturerRepository lecturerRepository;
    private final EmailRepository emailRepository;

    public UserService(UserRepository userRepository, LecturerRepository lecturerRepository, EmailRepository emailRepository) {
        this.userRepository = userRepository;
        this.lecturerRepository = lecturerRepository;
        this.emailRepository = emailRepository;
    }

    public UserResponseList getAll() {
        List<UserResponse> users = userRepository.findAll().stream()
                .map(userEntity -> new UserResponse(userEntity, userEntity.getEmail().getLecturer().isActiveSubscription()))
                .collect(Collectors.toList());
        return new UserResponseList(users);
    }

    @Transactional
    public Either<Object, Boolean> delete(Integer id) {
        if (userRepository.findById(id).isEmpty()) {
            return Either.left(ErrorMessage.WRONG_USER_ID.asJson());
        }
        userRepository.deleteById(id);
        return Either.right(true);
    }

    public Either<Object, UserResponse> add(AddUserRequest userRequest) {
        Optional<EmailEntity> emailEntity = emailRepository.findByEmail(userRequest.email);
        if (emailEntity.isEmpty()) {
            return Either.left(ErrorMessage.NO_LECTURER_WITH_EMAIL.asJson());
        }

        UserEntity userEntity = new UserEntity(userRequest);
        userEntity.setEmail(emailEntity.get());
        Try<UserEntity> trySave = Try.of(() -> userRepository.save(userEntity));

        return trySave.isSuccess() ?
                Either.right(new UserResponse(userEntity, userRequest.activeSubscription)) :
                Either.left(ErrorMessage.NOT_AVAILABLE_EMAIL.asJson());
    }

    @Transactional
    public Either<Object, UserResponse> update(Integer id, AddUserRequest userRequest) {
        Optional<UserEntity> entity = userRepository.findById(id);

        if (entity.isEmpty()) {
            return Either.left(ErrorMessage.WRONG_USER_ID.asJson());
        }

        Optional<LecturerEntity> lecturerEntity = lecturerRepository.findByEmail_Email(entity.get().getEmail().getEmail());

        if (lecturerEntity.isEmpty()) {
            return Either.left(ErrorMessage.NO_LECTURER_WITH_EMAIL.asJson());
        }

        UserEntity userEntity = entity.map(user -> {
            user.setPassword(userRequest.password);
            lecturerEntity.get().setActiveSubscription(userRequest.activeSubscription);
            return user;
        }).get();

        userRepository.save(userEntity);
        return Either.right(new UserResponse(userEntity, userRequest.activeSubscription));
    }

}
