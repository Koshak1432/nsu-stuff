package nsu.philharmoonia.services.impl;

import nsu.philharmoonia.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationServiceImpl(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void authenticate(Authentication proposedAuthenticationToken) throws AuthenticationException {
        Authentication authenticationToken = authenticationManager.authenticate(proposedAuthenticationToken);
        System.out.println("user " + authenticationToken.getName() + " has been authenticated");
//        return authenticationToken;
    }
}
