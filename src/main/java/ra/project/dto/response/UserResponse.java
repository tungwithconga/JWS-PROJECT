package ra.project.dto.response;

import lombok.*;
import ra.project.common.Role;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private Role role;
    private Boolean active;
}
