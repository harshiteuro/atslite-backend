package com.harshit.tldr.repository;

import com.harshit.tldr.entity.UserInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserInfoRepo extends MongoRepository<UserInfo, String> {
    UserInfo findByUsername(String username);
    Optional<UserInfo> findByEmail(String email); // Use 'email' if that is the correct field for login
}
