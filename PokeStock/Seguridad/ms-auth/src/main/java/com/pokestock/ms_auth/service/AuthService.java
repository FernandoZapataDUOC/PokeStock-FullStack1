package com.pokestock.ms_auth.service;

import com.pokestock.ms_auth.dto.AuthRequest;
import com.pokestock.ms_auth.dto.AuthResponse;
import com.pokestock.ms_auth.dto.RegisterRequest;

import java.util.Map;

public interface AuthService {
    AuthResponse login(AuthRequest request);
    Map<String, Object> register(RegisterRequest request);
}
