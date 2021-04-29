package gameofthreads.schedules.service;

import gameofthreads.schedules.dto.response.AddSubscription;
import gameofthreads.schedules.dto.response.GetSubscribers;
import gameofthreads.schedules.entity.EmailEntity;
import gameofthreads.schedules.entity.ScheduleEntity;
import gameofthreads.schedules.entity.SubscriptionEntity;
import gameofthreads.schedules.message.ErrorMessage;
import gameofthreads.schedules.repository.EmailRepository;
import gameofthreads.schedules.repository.ScheduleRepository;
import gameofthreads.schedules.repository.SubscriptionRepository;
import gameofthreads.schedules.util.Validator;
import io.vavr.control.Either;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubscriptionService {
    private final ScheduleRepository scheduleRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final EmailRepository emailRepository;

    public SubscriptionService(ScheduleRepository scheduleRepository, SubscriptionRepository subscriptionRepository, EmailRepository emailRepository) {
        this.scheduleRepository = scheduleRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.emailRepository = emailRepository;
    }

    private Either<Object, ScheduleEntity> findScheduleById(Integer scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .map(Either::right)
                .orElseGet(() -> Either.left(ErrorMessage.WRONG_CONFERENCE_ID.asJson()));
    }

    private Either<Object, ScheduleEntity> fetchScheduleWithSubscriptions(Integer scheduleId) {
        return scheduleRepository.fetchWithSubscriptions(scheduleId)
                .map(Either::right)
                .orElseGet(() -> Either.left(ErrorMessage.WRONG_CONFERENCE_ID.asJson()));
    }

    public Either<Object, AddSubscription> add(Integer scheduleId, String email) {
        if (!Validator.validateEmail(email)) {
            return Either.left(ErrorMessage.INCORRECT_EMAIL.asJson());
        }

        EmailEntity emailEntity = emailRepository.findByEmail(email).orElse(new EmailEntity(email));

        var scheduleEntity = findScheduleById(scheduleId);

        if (scheduleEntity.isRight() && scheduleEntity.get().getSubscriptions().stream().filter(subscriptionEntity -> subscriptionEntity.getEmail().equals(email)).collect(Collectors.toSet()).size() > 0) {
            return Either.left(ErrorMessage.EXISTING_SUBSCRIPTION.asJson());
        }

        return scheduleEntity
                .map(schedule -> new SubscriptionEntity(emailEntity, schedule))
                .map(subscriptionRepository::save)
                .map(AddSubscription::new);
    }

    public Either<Object, GetSubscribers> findAll(Integer scheduleId) {
        return fetchScheduleWithSubscriptions(scheduleId)
                .map(ScheduleEntity::getSubscriptions)
                .map(set -> set.stream().map(AddSubscription::new))
                .map(set -> set.sorted(Comparator.comparingInt(response -> response.id)))
                .flatMap(set -> Either.right(new GetSubscribers(set.collect(Collectors.toList()))));
    }

    @Transactional
    public Either<Object, Boolean> delete(Integer subscriptionId) {
        Optional<SubscriptionEntity> subscriptionEntity = subscriptionRepository.findById(subscriptionId);

        if (subscriptionEntity.isEmpty()) {
            return Either.left(ErrorMessage.WRONG_SUBSCRIPTION_ID.asJson());
        }

        subscriptionEntity.get().setActive(false);
        subscriptionRepository.save(subscriptionEntity.get());

        return Either.right(true);
    }

}
