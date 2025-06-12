package com.shopsProject.management.dto;

import com.shopsProject.management.domain.Role;

public record LoginResponse(String accessToken, String refreshToken, Role role) {

}
