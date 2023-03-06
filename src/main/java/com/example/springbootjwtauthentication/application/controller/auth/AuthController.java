package com.example.springbootjwtauthentication.application.controller.auth;

import com.example.springbootjwtauthentication.application.model.auth.RoleModel;
import com.example.springbootjwtauthentication.application.model.auth.UserModel;
import com.example.springbootjwtauthentication.application.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @GetMapping("/users")
    public List<UserModel> users() {
        for(UserModel user: authService.getUsers())
            //System.out.println(user.getRoles().toString());
            System.out.println();


        return authService.getUsers();
    }
    @GetMapping("/index")
    public ResponseEntity<?> index() {
        return ResponseEntity.ok("Hello World");
    }

    @GetMapping("/roles")
    public List<RoleModel> roles(){
        return authService.getRoles();
    }
}
