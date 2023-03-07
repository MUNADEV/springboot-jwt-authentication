package com.example.springbootjwtauthentication.security;


import com.example.springbootjwtauthentication.application.model.auth.RoleModel;
import com.example.springbootjwtauthentication.application.model.auth.UserModel;
import com.example.springbootjwtauthentication.application.model.dto.LoginDTO;
import com.example.springbootjwtauthentication.application.model.dto.UserDTO;
import com.example.springbootjwtauthentication.application.model.enums.Roles;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
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

    public UserModel create(UserDTO userDTO) throws Exception  {

        UserModel user = new UserModel();
        user.setUsrname(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        //Set<RoleModel> roles = new HashSet<>();
        RoleModel role1 = roleRepository.findByName(Roles.user.name()).orElseThrow(()-> new Exception("Role not found in database"));
        RoleModel role2 = roleRepository.findByName(Roles.customer.name()).orElseThrow(()-> new Exception("Role not found in database"));
        user.getRoles().add(role1);
        user.getRoles().add(role2);
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        user = userRepository.save(user);
        return user;
    }

    public UserDTO login(LoginDTO loginDTO) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getClave()));
        System.out.println(auth.toString());
        SecurityContextHolder.getContext().setAuthentication(auth);

        UserModel user = userRepository.findByEmail(loginDTO.getEmail()).orElse(null);
        List<String> roles = user.getRoles()
                .stream()
                .map(RoleModel::getName)
                .toList();

        String token = jwtProvider.generateToken(loginDTO.getEmail(),roles);

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setUsername(user.getUsername());
        userDTO.setPassword(user.getPassword());
        userDTO.setRoles(user.getRoles());
        userDTO.setToken(token);

        return userDTO;
    }
}
