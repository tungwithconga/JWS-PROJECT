package ra.project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "token_blacklist")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenBlacklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String tokenString;

    private LocalDateTime revokedAt;

    private LocalDateTime expiredAt;

    @PrePersist
    public void handleBeforeCreate() {
        this.revokedAt = LocalDateTime.now();
    }
}
