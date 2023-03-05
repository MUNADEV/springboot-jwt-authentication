package com.example.springbootjwtauthentication.service.auth;

import com.example.springbootjwtauthentication.model.auth.UserModel;
import com.example.springbootjwtauthentication.repository.auth.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    @Autowired
    UserRepository userRepository;

    public List<UserModel> getUsers() {
        return userRepository.findAll();
    }
}
