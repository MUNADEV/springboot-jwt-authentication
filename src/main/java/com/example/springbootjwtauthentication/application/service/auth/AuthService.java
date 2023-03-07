package com.example.springbootjwtauthentication.application.service.auth;

import com.example.springbootjwtauthentication.application.model.auth.RoleModel;
import com.example.springbootjwtauthentication.application.model.auth.UserModel;
import com.example.springbootjwtauthentication.application.repository.auth.RoleRepository;
import com.example.springbootjwtauthentication.application.repository.auth.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    public List<UserModel> getUsers() {
        return userRepository.findAll();
    }

    public List<RoleModel> getRoles(){
        return roleRepository.findAll();
    }

}
