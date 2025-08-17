package com.harshit.tldr.service;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtService {

    public static final String SECRET = "5367566859703373367639792F423F452848284D6251655468576D5A71347437";

    public String generateToken(String email) { // Use email as username
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, email);
    }

    private String createToken(Map<String, Object> claims, String email) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30)) //30min
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }


    public Boolean validateToken(String token, UserDetails userDetails) {
        try{
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (ExpiredJwtException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}


// ✅ public String generateToken(String email)
// Purpose: Generate a new JWT for the given email (used as the username).
//
// How it works:
// - Creates an empty map of custom claims (can be used to add roles, etc. later).
// - Passes it with the email to createToken.

// ---

// ✅ private String createToken(Map\<String, Object> claims, String email)
// Purpose: Build and return a JWT string.
//
// How it works:
// - Uses Jwts.builder() from the JJWT library.
// - Sets:
//   - Claims (empty or with custom data).
//   - Subject (the user's email/username).
//   - Issue time (iat) → current time.
//   - Expiration time → 30 minutes from now.
//   - Signing key and algorithm (HMAC SHA-256).
// - .compact() returns the encoded token string.

// ---

// ✅ private Key getSignKey()
// Purpose: Convert your Base64 SECRET string into a secure signing key.
//
// How it works:
// - Decodes the secret using Base64.
// - Converts it into an HMAC key with Keys.hmacShaKeyFor(...).
//
// ➡️ This key is used to sign and verify JWTs.

// ---

// ✅ public String extractUsername(String token)
// Purpose: Extracts the subject (i.e., email/username) from the token.
//
// How it works:
// - Calls extractClaim(...) with a lambda to fetch Claims::getSubject.

// ---

// ✅ public Date extractExpiration(String token)
// Purpose: Extracts the token's expiration timestamp.
//
// How it works:
// - Uses extractClaim(...) with a lambda to get Claims::getExpiration.

// ---

// ✅ public <T> T extractClaim(String token, Function\<Claims, T> claimsResolver)
// Purpose: Generic method to extract any claim from the token.
//
// How it works:
// - Parses the JWT and applies the resolver function to the claims.
// - Example: if the function is Claims::getSubject, it returns the username.

// ---

// ✅ private Claims extractAllClaims(String token)
// Purpose: Fully parses the JWT and returns all claims inside it.
//
// How it works:
// - Validates the token using your signing key.
// - If the token is valid and not tampered with, returns the claims payload.

// ---

// ✅ private Boolean isTokenExpired(String token)
// Purpose: Checks whether the token has expired.
//
// How it works:
// - Gets the expiration date using extractExpiration.
// - Compares it to the current system time.

// ---

// ✅ public Boolean validateToken(String token, UserDetails userDetails)
// Purpose: Final check to confirm if the token is valid for a given user.
//
// How it works:
// - Extracts the username from the token.
// - Checks:
//   - Username in token == userDetails.getUsername()
//   - Token is not expired
//
// ✅ If both are true → token is considered valid.
