package com.harshit.tldr.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

@Document("User")
@Getter
@Setter
public class UserInfo {

    @Id
    String id;
    String username;
    String password;

    @NotNull
    @Indexed(unique = true)
    private String email;
    List<String> roles;
    private Map<String, String> queries;
}
