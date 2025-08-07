package com.harshit.tldr.service;

import com.harshit.tldr.entity.UserInfo;
import com.harshit.tldr.pojo.UserModel;
import com.harshit.tldr.repository.UserInfoRepo;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

@Component
public class UserInfoService {

    private final UserInfoRepo repository;
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    public UserInfoService(UserInfoRepo repository) {
        this.repository = repository;
//        this.encoder = encoder;
    }

    @Transactional
    public UserModel createUser(UserModel user){

        if (repository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setPassword(encoder.encode(user.getPassword()));
        userInfo.setRoles(new ArrayList<>());
        userInfo.getRoles().add(user.getRoles());
        userInfo.setUsername(user.getUsername());
        userInfo.setEmail(user.getEmail());
        userInfo.setQueries(new HashMap<>());
        repository.save(userInfo);
        return user;
    }

    public UserInfo loadUserByName(String username){
        return repository.findByUsername(username);
    }

    public UserInfo loadUserByEmail(String email){
        Optional<UserInfo> usrInfo=repository.findByEmail(email);
        return usrInfo.orElse(null);
    }
}
