package nsu.philharmoonia.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
//    private final AuthenticationManager authenticationManager;

    public AuthenticationServiceImpl() {
    }

//    @Autowired
//    public AuthenticationServiceImpl(AuthenticationManager authenticationManager) {
//        this.authenticationManager = authenticationManager;
//    }

    @Override
    public Authentication authenticate(Authentication proposedAuthenticationToken) throws AuthenticationException {
        Authentication authenticationToken =
    }
}
