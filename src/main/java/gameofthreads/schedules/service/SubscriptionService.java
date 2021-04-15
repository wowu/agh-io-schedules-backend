package gameofthreads.schedules.service;

import gameofthreads.schedules.dto.request.AddSubscriptionRequest;
import gameofthreads.schedules.entity.ConferenceEntity;
import gameofthreads.schedules.entity.SubscriptionEntity;
import gameofthreads.schedules.message.ErrorMessage;
import gameofthreads.schedules.repository.ConferenceRepository;
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
    private final ConferenceRepository conferenceRepository;
    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(ConferenceRepository conferenceRepository, SubscriptionRepository subscriptionRepository) {
        this.conferenceRepository = conferenceRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Transactional
    public Either<String, Boolean> addSubscription(AddSubscriptionRequest requestData){
        if(!Validator.validateEmailList(requestData.emailList)){
            return Either.left(ErrorMessage.INCORRECT_EMAIL_LIST.asJson());
        }

        Optional<ConferenceEntity> conferenceEntity = conferenceRepository.findById(requestData.conferenceId);

        if(conferenceEntity.isPresent()){
            List<SubscriptionEntity> subscriptionEntities = requestData.emailList
                    .stream()
                    .map(email -> new SubscriptionEntity(email, conferenceEntity.get()))
                    .collect(Collectors.toList());

            subscriptionRepository.saveAll(subscriptionEntities);
            return  Either.right(true);
        }

        return Either.left(ErrorMessage.WRONG_CONFERENCE_ID.asJson());
    }

}
