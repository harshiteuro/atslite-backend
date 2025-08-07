package com.harshit.tldr.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {

    private String email;
    private String password;

}

// ✅ What is @Data in Lombok?
// - @Data is a Lombok annotation that generates boilerplate code for a class.
// - It automatically generates:
//   - Getters and setters for all fields
//   - toString()
//   - equals() and hashCode()
//   - A required-arguments constructor
//
// ➡️ It saves time and keeps the code clean when working with simple data models (like DTOs or entities).