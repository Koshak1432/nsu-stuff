package nsu.philharmoonia.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;

@Configuration
@EnableGlobalAuthentication
public class SecurityConfig {
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) {
        try {
            authenticationManagerBuilder.inMemoryAuthentication()
                    .withUser("user").password("123").roles("USER")
                    .and().withUser("admin").password("admin").roles("USER", "ADMIN");
        } catch (Exception e) {
            System.err.println("Authentication init failed");
            e.printStackTrace();
        }
    }
}
