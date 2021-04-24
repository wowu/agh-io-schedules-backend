package gameofthreads.schedules.service;

import gameofthreads.schedules.dto.request.AddLecturerRequest;
import gameofthreads.schedules.entity.LecturerEntity;
import gameofthreads.schedules.message.ErrorMessage;
import gameofthreads.schedules.repository.LecturerRepository;
import gameofthreads.schedules.util.Validator;
import io.vavr.control.Either;
import io.vavr.control.Try;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LecturerService {
    private final LecturerRepository lecturerRepository;

    public LecturerService(LecturerRepository lecturerRepository) {
        this.lecturerRepository = lecturerRepository;
    }

    public List<LecturerEntity> getAll(){
        return lecturerRepository.findAll();
    }

    public Either<String, Boolean> delete(Integer id){
        lecturerRepository.deleteById(id);
        return Either.right(true);
    }

    public Either<Object, LecturerEntity> add(AddLecturerRequest lecturerRequest){
        if(!Validator.validateEmail(lecturerRequest.email)){
            return Either.left(ErrorMessage.INCORRECT_EMAIL.asJson());
        }

        LecturerEntity lecturerEntity = new LecturerEntity(lecturerRequest);
        Try<LecturerEntity> trySave = Try.of(() -> lecturerRepository.save(lecturerEntity));

        return trySave
                .map(Either::right)
                .getOrElse(() -> Either.left(ErrorMessage.NOT_AVAILABLE_EMAIL.asJson()));
    }

    public Either<Object, LecturerEntity> update(AddLecturerRequest lecturerRequest){
        if(!Validator.validateEmail(lecturerRequest.email)){
            return Either.left(ErrorMessage.INCORRECT_EMAIL.asJson());
        }

        return lecturerRepository.findByEmail(lecturerRequest.email)
                .map(Either::right)
                .orElseGet(() -> Either.left(ErrorMessage.NOT_AVAILABLE_EMAIL.asJson()));
    }

}
