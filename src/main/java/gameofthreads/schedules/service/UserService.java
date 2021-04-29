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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final EmailRepository emailRepository;
    private final UserRepository userRepository;
    private final LecturerRepository lecturerRepository;

    public UserService(EmailRepository emailRepository, UserRepository userRepository,
                       LecturerRepository lecturerRepository) {

        this.emailRepository = emailRepository;
        this.userRepository = userRepository;
        this.lecturerRepository = lecturerRepository;
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

        UserEntity userEntity = new UserEntity(userRequest);
        userEntity.setEmail(lecturerEmail.get());
        lecturerEmail.get().setUser(userEntity);
        userRepository.save(userEntity);
        return Either.right(new UserResponse(userEntity.getEmailEntity()));
    }

    @Transactional
    public Either<Object, UserResponse> update(Integer id, String password, Boolean activeSubscription) {
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

        return Either.right(new UserResponse(Objects.requireNonNull(userEntity.get().getEmailEntity())));
    }
}
