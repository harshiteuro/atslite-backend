package com.harshit.tldr.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {
    public String username;
    public String password;
    public String email;
    public String roles;
    public String error;
}
