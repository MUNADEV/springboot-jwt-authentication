package com.example.springbootjwtauthentication.application.controller.auth;

import com.example.springbootjwtauthentication.application.model.auth.RoleModel;
import com.example.springbootjwtauthentication.application.model.auth.UserModel;
import com.example.springbootjwtauthentication.application.model.dto.JwtDTO;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

/**
 * Controller class that handles login, user creation, and authentication token refreshing
 */
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
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public List<UserModel> users() {
        for(UserModel user: authService.getUsers())
            //System.out.println(user.getRoles().toString());
            System.out.println();

        return authService.getUsers();
    }

    /**
     * Endpoint for user login. Validates the input loginDTO and returns a UserDTO on successful login.
     * Returns an error message if input is invalid or an internal server error occurred.
     * @param loginDTO Input object containing user email and password.
     * @param bindingResult Object that holds the validation result of the input object.
     * @return Object that contains either a UserDTO or an error message with appropriate HTTP status.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<String>("Requires email and password valid", HttpStatus.BAD_REQUEST);
        }

        try {
            return new ResponseEntity<UserDTO>(userService.login(loginDTO), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint for creating a new user. Validates the input userDTO and returns a UserModel on successful creation.
     * Returns an error message if input is invalid or an internal server error occurred.
     * @param usuario Input object containing user information
     * @param validaciones Object that holds the validation result of the input object
     * @return Object that contains either a UserModel or an error message with appropriate HTTP status.
     * @throws Exception Thrown if an error occurred while creating the user
     */
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

    /**
     * Endpoint for refreshing an authentication token. Takes a JwtDTO object containing a refresh token
     * and returns a new JWT on successful refresh.
     * @param jwtDto Object containing the refresh token.
     * @return Object that contains either a JwtDTO with the new token or an error message with appropriate HTTP status
     * @throws ParseException Thrown if an error occurred while parsing the refresh token.
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAuthenticationToken(@RequestBody JwtDTO jwtDto) throws ParseException {
        String token = jwtProvider.refreshToken(jwtDto);

        if (token != null) {
            JwtDTO jwt = new JwtDTO();
            jwt.setToken(token);
            return ResponseEntity.ok(jwt);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Refresh token is not valid");
        }
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
