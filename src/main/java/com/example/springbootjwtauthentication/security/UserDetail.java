package com.example.springbootjwtauthentication.security;

import com.example.springbootjwtauthentication.application.model.auth.UserModel;
import com.example.springbootjwtauthentication.application.repository.auth.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetail implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
        public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<UserModel> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty() ) {
            throw new UsernameNotFoundException("User with this username does not exists " + email);
        }

        UserModel usuario = optionalUser.get();

        if (!usuario.isEnabled()) {
            throw new DisabledException("The account is disabled");
        }

        if (!usuario.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException("The credentials have expired");
        }

        return usuario;

    }

}
