package ra.project.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ra.project.common.Role;
import ra.project.dto.request.LoginRequest;
import ra.project.dto.request.RefreshTokenRequest;
import ra.project.dto.request.RegisterRequest;
import ra.project.dto.response.AuthResponse;
import ra.project.dto.response.UserResponse;
import ra.project.entity.TokenBlacklist;
import ra.project.entity.User;
import ra.project.exception.ConflictException;
import ra.project.exception.ForbiddenException;
import ra.project.exception.UnauthorizedException;
import ra.project.repository.TokenBlacklistRepository;
import ra.project.repository.UserRepository;
import ra.project.security.jwt.JwtService;
import ra.project.service.AuthService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final AuthenticationManager authenticationManager;

    @Override
    public UserResponse registerPatient(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username đã tồn tại");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email đã tồn tại");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .role(Role.PATIENT)
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);

        return UserResponse.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .fullName(savedUser.getFullName())
                .phone(savedUser.getPhone())
                .role(savedUser.getRole())
                .active(savedUser.getIsActive())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Sai tài khoản hoặc mật khẩu");
        }

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Sai tài khoản hoặc mật khẩu"));

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new ForbiddenException("Tài khoản đã bị khóa");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        String username = jwtService.extractUsername(refreshToken);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("Người dùng không tồn tại"));

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new ForbiddenException("Tài khoản đã bị khóa");
        }
        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new UnauthorizedException("Token gửi lên không phải RefreshToken");
        }
        if (!jwtService.isTokenValid(refreshToken, user.getUsername())) {
            throw new UnauthorizedException("RefreshToken không hợp lệ hoặc đã hết hạn");
        }

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    @Override
    public void logout(String accessToken) {
        Date expirationDate = jwtService.extractExpiration(accessToken);
        LocalDateTime expiredAt = expirationDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        TokenBlacklist tb = TokenBlacklist.builder()
                .tokenString(accessToken)
                .revokedAt(LocalDateTime.now())
                .expiredAt(expiredAt)
                .build();

        tokenBlacklistRepository.save(tb);
    }
}
