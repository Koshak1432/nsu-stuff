package nsu.fit.crackhash.listeners;

import nsu.fit.crackhash.model.dto.CrackHashWorkerResponse;
import nsu.fit.crackhash.services.HashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitResponseListener {
    private final Logger logger = LoggerFactory.getLogger(RabbitResponseListener.class);
    private final HashService service;

    public RabbitResponseListener(HashService service) {
        this.service = service;
    }

    @RabbitListener(queues = {"${rabbitmq.queue.response.name}"})
    public void processResponse(CrackHashWorkerResponse response) {
        logger.info("Got a message from worker, going to update answers of {} request", response.getCrackHashWorkerResponse().getRequestId());
        service.updateAnswers(response);
        logger.info("Updated answers of {} request", response.getCrackHashWorkerResponse().getRequestId());
    }
}
