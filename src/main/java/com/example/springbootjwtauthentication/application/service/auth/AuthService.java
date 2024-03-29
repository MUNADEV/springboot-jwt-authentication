package com.example.springbootjwtauthentication.application.service.auth;


import com.example.springbootjwtauthentication.application.model.auth.RoleModel;
import com.example.springbootjwtauthentication.application.model.auth.UserModel;
import com.example.springbootjwtauthentication.application.model.dto.LoginDTO;
import com.example.springbootjwtauthentication.application.model.dto.UserDTO;
import com.example.springbootjwtauthentication.application.repository.auth.RoleRepository;
import com.example.springbootjwtauthentication.application.repository.auth.UserRepository;
import com.example.springbootjwtauthentication.security.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtProvider jwtProvider;

    /**
     * Creates a new user based on the given userDTO
     * @param userDTO the userDTO object containing the user's information
     * @return the created UserModel object
     * @throws Exception if a role specified in userDTO is not found in the database
     */
    public UserModel create(UserDTO userDTO) throws Exception  {

        UserModel user = new UserModel();

        user._setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        //TODO check in new method
        Set<String> roles = userDTO.getRolesEnum();

        if (roles == null || roles.size() < 2) {
            throw new Exception("User must have at least 2 roles");
        }
        for (String role: roles) {
            if(Arrays.asList("user", "customer", "employee", "administrator").contains(role.toLowerCase())) {
                user.getRoles().add(roleRepository.findByName(role).orElseThrow(()-> new Exception("Error to get role")));
            }
        }
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        user = userRepository.save(user);
        return user;
    }

    /**
     * Authenticates a user based on the provided loginDTO and generates a JWT token for the authenticated user
     * @param loginDTO the loginDTO object containing the user's email and password
     * @return the UserDTO object for the authenticated user, containing user information and JWT token
     */
    public UserDTO login(LoginDTO loginDTO) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));
        System.out.println(auth.toString());
        SecurityContextHolder.getContext().setAuthentication(auth);

        UserModel user = userRepository.findByEmail(loginDTO.getEmail()).orElse(null);

        if(user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        List<String> roles = user.getRoles()
                .stream()
                .map(RoleModel::getName)
                .toList();

        String token = jwtProvider.generateToken(loginDTO.getEmail(),roles);

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setUsername(user._getUsername());
        userDTO.setPassword(user.getPassword());
        userDTO.setRoles(user.getRoles());
        userDTO.setToken(token);

        return userDTO;
    }

    public List<RoleModel> getRoles() {
        return roleRepository.findAll();
    }
}
