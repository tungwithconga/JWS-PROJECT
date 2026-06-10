package ra.project.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ra.project.dto.response.UserResponse;
import ra.project.entity.User;
import ra.project.exception.BadRequestException;
import ra.project.exception.ResourceNotFoundException;
import ra.project.repository.UserRepository;
import ra.project.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<UserResponse> getUsers(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<User> users;
        if (keyword == null || keyword.isEmpty()) {
            users = userRepository.findAll(pageable);
        } else {
            users = userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                    keyword, keyword, pageable
            );
        }
        return users.map(this::toUserResponse);
    }

    @Override
    public UserResponse createUser(User user) {
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        User saved = userRepository.save(user);
        return toUserResponse(saved);
    }

    @Override
    public UserResponse updateUser(Long id, User userUpdate) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User không tồn tại với ID: " + id));
        user.setFullName(userUpdate.getFullName());
        user.setEmail(userUpdate.getEmail());
        user.setPhone(userUpdate.getPhone());
        user.setRole(userUpdate.getRole());
        User saved = userRepository.save(user);
        return toUserResponse(saved);
    }

    @Override
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User không tồn tại với ID: " + id));
        user.setIsActive(false);
        userRepository.save(user);
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User không tồn tại"));
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new BadRequestException("Mật khẩu cũ không chính xác");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .role(user.getRole())
                .active(user.getIsActive())
                .build();
    }
}
