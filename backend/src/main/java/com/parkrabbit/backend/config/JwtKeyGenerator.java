package com.parkrabbit.backend.config;

public class JwtKeyGenerator {
    public static void main(String[] args) {
        System.out.println(
            io.jsonwebtoken.io.Encoders.BASE64.encode(
                io.jsonwebtoken.security.Keys
                    .secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256)
                    .getEncoded()
            )
        );
    }
}
