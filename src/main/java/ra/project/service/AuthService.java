package ra.project.service;

import ra.project.dto.request.LoginRequest;
import ra.project.dto.request.RefreshTokenRequest;
import ra.project.dto.request.RegisterRequest;
import ra.project.dto.response.AuthResponse;
import ra.project.dto.response.UserResponse;

public interface AuthService {
    UserResponse registerPatient(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
    void logout(String token);
}
