package nsu.fit.crackhashworker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Import(RabbitConfig.class)
@EnableTransactionManagement
public class Config {

}
