package nsu.fit.crackhash.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    private final String exchangeName;
    private final String taskQueue;
    private final String responseQueue;
    private final String taskRouting;
    private final String responseRouting;

    public RabbitConfig(@Value("${rabbitmq.exchange.name}") String exchangeName,
                        @Value("${rabbitmq.queue.task.name}") String taskQueue,
                        @Value("${rabbitmq.queue.response.name}") String responseQueue,
                        @Value("${rabbitmq.routing.task.key}") String taskRouting,
                        @Value("${rabbitmq.routing.response.key}") String responseRouting) {
        this.exchangeName = exchangeName;
        this.taskQueue = taskQueue;
        this.responseQueue = responseQueue;
        this.taskRouting = taskRouting;
        this.responseRouting = responseRouting;
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(exchangeName, true, false);
    }

    @Bean
    public Queue taskQueue() {
        return new Queue(taskQueue, true);
    }

    @Bean
    public Queue responseQueue() {
        return new Queue(responseQueue, true);
    }

    @Bean
    public Binding bindingTaskQueue() {
        return BindingBuilder.bind(taskQueue()).to(directExchange()).with(taskRouting);
    }

    @Bean
    public Binding bindingResponseQueue() {
        return BindingBuilder.bind(responseQueue()).to(directExchange()).with(responseRouting);
    }

    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean("myRabbitTemplate")
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }
}
