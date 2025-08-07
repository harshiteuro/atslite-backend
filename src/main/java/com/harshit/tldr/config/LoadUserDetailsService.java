package com.harshit.tldr.config;

import com.harshit.tldr.entity.UserInfo;
import com.harshit.tldr.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class LoadUserDetailsService implements UserDetailsService {

    @Autowired
    public UserInfoService userInfoService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserInfo user = userInfoService.loadUserByEmail(email);

        if(user!=null){
            return org.springframework.security.core.userdetails.User.builder().password(user.getPassword()).username(user.getEmail()).roles(user.getRoles().toArray(new String[0])[0]).build();
        }

        throw new UsernameNotFoundException("User not found with username: " + email);
    }
}
