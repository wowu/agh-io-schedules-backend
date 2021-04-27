package gameofthreads.schedules.service;

import gameofthreads.schedules.dto.request.AddUserRequest;
import gameofthreads.schedules.dto.response.UserResponse;
import gameofthreads.schedules.dto.response.UserResponseList;
import gameofthreads.schedules.entity.LecturerEntity;
import gameofthreads.schedules.entity.UserEntity;
import gameofthreads.schedules.message.ErrorMessage;
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

    public UserService(UserRepository userRepository, LecturerRepository lecturerRepository) {
        this.userRepository = userRepository;
        this.lecturerRepository = lecturerRepository;
    }

    public UserResponseList getAll() {
        List<UserResponse> users = userRepository.findAll().stream()
                .map(UserResponse::new)
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
        Optional<LecturerEntity> lecturerEntity = lecturerRepository.findByEmail(userRequest.email);
        if (lecturerEntity.isEmpty()) {
            return Either.left(ErrorMessage.NO_LECTURER_WITH_EMAIL.asJson());
        }

        UserEntity userEntity = new UserEntity(userRequest);
        userEntity.setLecturer(lecturerEntity.get());
        Try<UserEntity> trySave = Try.of(() -> userRepository.save(userEntity));

        return trySave.isSuccess() ?
                Either.right(new UserResponse(userEntity)) :
                Either.left(ErrorMessage.NOT_AVAILABLE_EMAIL.asJson());
    }

    @Transactional
    public Either<Object, UserResponse> update(Integer id, AddUserRequest userRequest) {
        Optional<UserEntity> entity = userRepository.findById(id);

        if (entity.isEmpty()) {
            return Either.left(ErrorMessage.WRONG_USER_ID.asJson());
        }

        UserEntity userEntity = entity.map(user -> {
            user.setPassword(userRequest.password);
            user.getLecturer().setActiveSubscription(userRequest.activeSubscription);
            return user;
        }).get();

        userRepository.save(userEntity);
        return Either.right(new UserResponse(userEntity));
    }

}
