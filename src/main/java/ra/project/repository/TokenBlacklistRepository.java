package ra.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ra.project.entity.TokenBlacklist;

public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {
    boolean existsByTokenString(String tokenString);
}
