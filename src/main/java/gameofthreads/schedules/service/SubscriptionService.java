package gameofthreads.schedules.service;

import gameofthreads.schedules.dto.request.AddSubscriptionRequest;
import gameofthreads.schedules.entity.ScheduleEntity;
import gameofthreads.schedules.entity.SubscriptionEntity;
import gameofthreads.schedules.message.ErrorMessage;
import gameofthreads.schedules.repository.ScheduleRepository;
import gameofthreads.schedules.repository.SubscriptionRepository;
import gameofthreads.schedules.util.Validator;
import io.vavr.control.Either;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubscriptionService {
    private final ScheduleRepository scheduleRepository;
    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(ScheduleRepository scheduleRepository, SubscriptionRepository subscriptionRepository) {
        this.scheduleRepository = scheduleRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Transactional
    public Either<String, Boolean> addByAdmin(AddSubscriptionRequest requestData) {
        if (!Validator.validateEmailList(requestData.emailList)) {
            return Either.left(ErrorMessage.INCORRECT_EMAIL_LIST.asJson());
        }

        Optional<ScheduleEntity> conferenceEntity = scheduleRepository.findById(requestData.scheduleId);

        if (conferenceEntity.isEmpty()) {
            return Either.left(ErrorMessage.WRONG_CONFERENCE_ID.asJson());
        }

        List<SubscriptionEntity> subscriptionEntities = requestData.emailList
                .stream()
                .map(email -> new SubscriptionEntity(email, conferenceEntity.get()))
                .collect(Collectors.toList());

        subscriptionRepository.saveAll(subscriptionEntities);
        return Either.right(true);
    }

    public Either<String, Boolean> addUsingLink(String publicLink, String email) {
        if (!Validator.validateEmail(email)) {
            return Either.left(ErrorMessage.INCORRECT_EMAIL.asJson());
        }

        Optional<ScheduleEntity> conferenceEntity = scheduleRepository.findByPublicLink(publicLink);

        if (conferenceEntity.isEmpty()) {
            return Either.left(ErrorMessage.WRONG_CONFERENCE_ID.asJson());
        }

        subscriptionRepository.save(new SubscriptionEntity(email, conferenceEntity.get()));
        return Either.right(true);
    }

}
