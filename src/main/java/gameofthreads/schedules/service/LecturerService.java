package gameofthreads.schedules.service;

import gameofthreads.schedules.dto.request.AddLecturerRequest;
import gameofthreads.schedules.entity.LecturerEntity;
import gameofthreads.schedules.message.ErrorMessage;
import gameofthreads.schedules.repository.LecturerRepository;
import gameofthreads.schedules.util.Validator;
import io.vavr.control.Either;
import io.vavr.control.Try;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LecturerService {
    private final LecturerRepository lecturerRepository;

    public LecturerService(LecturerRepository lecturerRepository) {
        this.lecturerRepository = lecturerRepository;
    }

    public List<LecturerEntity> getAll() {
        return lecturerRepository.findAll();
    }

    @Transactional
    public Either<Object, Boolean> delete(Integer id) {
        if (lecturerRepository.findById(id).isEmpty()) {
            return Either.left(ErrorMessage.WRONG_LECTURER_ID.asJson());
        }
        lecturerRepository.deleteById(id);
        return Either.right(true);
    }

    public Either<Object, LecturerEntity> add(AddLecturerRequest lecturerRequest) {
        if (!Validator.validateEmail(lecturerRequest.email)) {
            return Either.left(ErrorMessage.INCORRECT_EMAIL.asJson());
        }

        LecturerEntity lecturerEntity = new LecturerEntity(lecturerRequest);
        Try<LecturerEntity> trySave = Try.of(() -> lecturerRepository.save(lecturerEntity));

        return trySave
                .map(Either::right)
                .getOrElse(() -> Either.left(ErrorMessage.NOT_AVAILABLE_EMAIL.asJson()));
    }

    @Transactional
    public Either<Object, LecturerEntity> update(Integer id, AddLecturerRequest lecturerRequest) {
        if (!Validator.validateEmail(lecturerRequest.email)) {
            return Either.left(ErrorMessage.INCORRECT_EMAIL.asJson());
        }

        Optional<LecturerEntity> entity = lecturerRepository.findById(id);

        if (entity.isEmpty()) {
            return Either.left(ErrorMessage.WRONG_LECTURER_ID.asJson());
        }

        LecturerEntity lecturerEntity = entity.map(lecturer -> {
            lecturer.setName(lecturerRequest.name);
            lecturer.setSurname(lecturerRequest.surname);
            lecturer.setEmail(lecturerRequest.email);
            lecturer.setActiveSubscription(lecturerRequest.activeSubscription);
            return lecturer;
        }).get();

        lecturerRepository.save(lecturerEntity);
        return Either.right(lecturerEntity);
    }

}
