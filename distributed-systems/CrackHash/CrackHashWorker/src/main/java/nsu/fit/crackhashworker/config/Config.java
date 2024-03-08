package nsu.fit.crackhashworker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(RabbitConfig.class)
public class Config {

}
