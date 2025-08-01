package com.apiestudar.api_prodify.infrastructure.security;

import java.io.IOException;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
public class GrantedAuthorityDeserializer extends JsonDeserializer<GrantedAuthority> {

    @Override
    public GrantedAuthority deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String authority = p.getText(); // Supondo que o JSON contenha apenas o nome da autoridade (ex.: "ROLE_USER")
        return new SimpleGrantedAuthority(authority);
    }
}