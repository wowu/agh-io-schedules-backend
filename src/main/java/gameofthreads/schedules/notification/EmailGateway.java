package gameofthreads.schedules.notification;

import org.springframework.stereotype.Component;

@Component
public class EmailGateway {
    private final EmailQueue emailQueue;
    private final QueueInitializer queueInitializer;

    public EmailGateway(EmailQueue emailQueue, QueueInitializer queueInitializer) {
        this.emailQueue = emailQueue;
        this.queueInitializer = queueInitializer;
    }

    public void reInit(){
        emailQueue.clear();
        queueInitializer.initialize();
    }

}
