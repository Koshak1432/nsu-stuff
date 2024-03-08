package nsu.fit.crackhash.listeners;

import com.rabbitmq.client.Channel;
import nsu.fit.crackhash.model.dto.CrackHashWorkerResponse;
import nsu.fit.crackhash.services.HashService;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@EnableRabbit
public class RabbitResponseListener {
    private final HashService service;

    public RabbitResponseListener(HashService service) {
        this.service = service;
    }

    @RabbitListener(queues = {"${rabbitmq.queue.response.name}"})
    public void processResponse(CrackHashWorkerResponse response, Channel channel,
                                @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        service.updateAnswers(response);
        channel.basicAck(tag, false);
    }
}
