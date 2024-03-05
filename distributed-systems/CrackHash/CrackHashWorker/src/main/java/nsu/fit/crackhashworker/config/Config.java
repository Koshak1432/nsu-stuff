package nsu.fit.crackhashworker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
@Import(RabbitConfig.class)
public class Config {

//    @Bean("threadPoolTaskExecutor")
//    public Executor asyncExecutor() {
//        return Executors.newSingleThreadExecutor();
//    }
}
