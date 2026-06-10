package ra.project.dto.response;

import lombok.*;
import ra.project.common.Role;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private String username;
    private Role role;
}
