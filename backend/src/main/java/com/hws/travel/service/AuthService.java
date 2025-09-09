package com.hws.travel.service;

import com.hws.travel.entity.User;

public interface AuthService {
    User authenticate(String email, String password);
}
