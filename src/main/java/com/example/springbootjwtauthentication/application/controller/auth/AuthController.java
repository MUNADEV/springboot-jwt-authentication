package com.example.springbootjwtauthentication.application.controller.auth;

import com.example.springbootjwtauthentication.application.model.auth.RoleModel;
import com.example.springbootjwtauthentication.application.model.auth.UserModel;
import com.example.springbootjwtauthentication.application.model.dto.LoginDTO;
import com.example.springbootjwtauthentication.application.model.dto.UserDTO;
import com.example.springbootjwtauthentication.application.service.auth.AuthService;
import com.example.springbootjwtauthentication.security.UserService;
import com.example.springbootjwtauthentication.security.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    JwtProvider jwtProvider;

    @GetMapping("/users")
    public List<UserModel> users() {
        for(UserModel user: authService.getUsers())
            //System.out.println(user.getRoles().toString());
            System.out.println();

        return authService.getUsers();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO usuarioLogin, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<String>("Requires email and password", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<UserDTO>(userService.login(usuarioLogin), HttpStatus.OK);

    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody UserDTO usuario, BindingResult validaciones)
            throws Exception {

        if (validaciones.hasErrors()) {
            return new ResponseEntity<String>("Incomplete", HttpStatus.BAD_REQUEST);
        }

        try {
            return new ResponseEntity<UserModel>(userService.create(usuario), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
/*
    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(@RequestBody JwtDTO jwtDto) throws ParseException {
        String token = JwtProvider.refreshToken(jwtDto);
        return new ResponseEntity(token, HttpStatus.OK);
    }*/

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAuthenticationToken(@RequestBody String request) throws ParseException {
        String token = jwtProvider.refreshToken(request);
        return new ResponseEntity(token, HttpStatus.OK);
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
