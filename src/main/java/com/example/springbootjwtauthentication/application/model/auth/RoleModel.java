package com.example.springbootjwtauthentication.application.model.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "roles")
public class RoleModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="name",unique = true)
    private String name;

    @JsonIgnore
    @Column(name = "created_at")
    private Timestamp createdAt;

    @JsonIgnore
    @Column(name = "updated_at")
    private Timestamp  updatedAt;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<UserModel> users = new HashSet<>();
}
