package com.harshit.tldr.controller.publicController;

import com.harshit.tldr.entity.AuthRequest;
import com.harshit.tldr.entity.UserInfo;
import com.harshit.tldr.pojo.UserModel;
import com.harshit.tldr.service.JwtService;
import com.harshit.tldr.service.UserInfoService;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
public class UserController {

    @Autowired
    public UserInfoService userInfoService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/signup")
    public ResponseEntity<UserModel> signUp(@RequestBody UserModel user){
        try{
            UserModel newUser = userInfoService.createUser(user);
            return new ResponseEntity<>(newUser, HttpStatus.OK);
        } catch (IllegalArgumentException e){
            UserModel userModel = new UserModel();
            userModel.setError(e.getMessage());
            return new ResponseEntity<>(userModel,HttpStatus.BAD_REQUEST);
        }
    }

    //    Step	Description
    //1	Wraps username/password in UsernamePasswordAuthenticationToken
    //2	Passes token to AuthenticationManager.authenticate()
    //3	Calls UserDetailsService.loadUserByUsername()
    //4	Compares password using PasswordEncoder.matches()
    //5	Returns authenticated token (with roles)
    //6	Throws exception if authentication fails
    @PostMapping("/login")
    public String authenticateAndGetToken(@RequestBody AuthRequest authRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );
        // database password validation
        if (authentication.isAuthenticated()) {
            // generate jwt token
            return jwtService.generateToken(authRequest.getEmail());
        } else {
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }
}
