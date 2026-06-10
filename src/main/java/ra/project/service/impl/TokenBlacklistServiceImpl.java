package ra.project.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ra.project.entity.TokenBlacklist;
import ra.project.repository.TokenBlacklistRepository;
import ra.project.security.jwt.JwtService;
import ra.project.service.TokenBlacklistService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final JwtService jwtService;

    @Override
    public void blacklistToken(String token) {
        Date expirationDate = jwtService.extractExpiration(token);
        LocalDateTime expiredAt = expirationDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        TokenBlacklist tb = TokenBlacklist.builder()
                .tokenString(token)
                .revokedAt(LocalDateTime.now())
                .expiredAt(expiredAt)
                .build();

        tokenBlacklistRepository.save(tb);
    }

    @Override
    public boolean isBlacklisted(String token) {
        return tokenBlacklistRepository.existsByTokenString(token);
    }
}
