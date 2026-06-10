package ra.project.service;

import org.springframework.data.domain.Page;
import ra.project.dto.response.UserResponse;
import ra.project.entity.User;

public interface UserService {
    Page<UserResponse> getUsers(String keyword, int page, int size);
    UserResponse createUser(User user);
    UserResponse updateUser(Long id, User userUpdate);
    void deactivateUser(Long id);
    void changePassword(String username, String oldPassword, String newPassword);
}
