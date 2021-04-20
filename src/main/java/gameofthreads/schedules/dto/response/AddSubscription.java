package gameofthreads.schedules.dto.response;

import gameofthreads.schedules.entity.SubscriptionEntity;

import java.io.Serializable;

public class AddSubscription implements Serializable {
    public final Integer id;
    public final String email;

    public AddSubscription(SubscriptionEntity subscriptionEntity) {
        this.id = subscriptionEntity.getId();
        this.email = subscriptionEntity.getEmail();
    }

}
