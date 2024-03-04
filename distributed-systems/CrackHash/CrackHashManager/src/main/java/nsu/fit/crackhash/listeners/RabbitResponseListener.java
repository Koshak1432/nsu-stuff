package nsu.fit.crackhash.listeners;

import nsu.fit.crackhash.model.dto.CrackHashWorkerResponse;
import nsu.fit.crackhash.services.HashService;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@EnableRabbit
public class RabbitResponseListener {
    private final HashService service;

    public RabbitResponseListener(HashService service) {
        this.service = service;
    }

    @RabbitListener(queues = {"${rabbitmq.queue.response.name}"})
    public void processResponse(CrackHashWorkerResponse response) {
        service.updateAnswers(response);
    }
}
