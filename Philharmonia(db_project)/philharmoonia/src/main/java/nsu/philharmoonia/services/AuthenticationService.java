package nsu.philharmoonia.services;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public interface AuthenticationService {
    Authentication authenticate(Authentication authenticationToken) throws AuthenticationException;
}
