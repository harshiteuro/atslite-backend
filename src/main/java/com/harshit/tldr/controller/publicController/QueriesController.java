package com.harshit.tldr.controller.publicController;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tldr")
public class QueriesController {

    @GetMapping
    public ResponseEntity<String> hello(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated()){
            return new ResponseEntity<>("Hello-world", HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>("Authentication failed, please try again",HttpStatus.BAD_GATEWAY);
        }
    }
}
