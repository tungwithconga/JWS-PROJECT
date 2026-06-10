package ra.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import ra.project.common.Role;
import ra.project.dto.response.UserResponse;
import ra.project.entity.User;
import ra.project.exception.BadRequestException;
import ra.project.exception.ResourceNotFoundException;
import ra.project.repository.UserRepository;
import ra.project.service.impl.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@hospital.com")
                .passwordHash("hashedPassword")
                .fullName("Test User")
                .phone("0912345678")
                .role(Role.PATIENT)
                .isActive(true)
                .build();
    }

    @Test
    @DisplayName("getUsers - trả về danh sách user phân trang")
    void getUsers_shouldReturnPageOfUsers() {
        Page<User> userPage = new PageImpl<>(List.of(testUser));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

        Page<UserResponse> result = userService.getUsers(null, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("testuser", result.getContent().get(0).getUsername());
    }

    @Test
    @DisplayName("getUsers - tìm kiếm theo keyword")
    void getUsers_withKeyword_shouldFilter() {
        Page<User> userPage = new PageImpl<>(List.of(testUser));
        when(userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                any(), any(), any(Pageable.class))).thenReturn(userPage);

        Page<UserResponse> result = userService.getUsers("test", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("deactivateUser - vô hiệu hóa user thành công")
    void deactivateUser_shouldSetInactive() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.deactivateUser(1L);

        assertFalse(testUser.getIsActive());
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("deactivateUser - user không tồn tại thì ném exception")
    void deactivateUser_notFound_shouldThrow() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.deactivateUser(99L));
    }

    @Test
    @DisplayName("changePassword - đổi mật khẩu thành công")
    void changePassword_shouldUpdatePassword() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPass", "hashedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("newHashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.changePassword("testuser", "oldPass", "newPass");

        assertEquals("newHashedPassword", testUser.getPasswordHash());
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("changePassword - mật khẩu cũ sai thì ném exception")
    void changePassword_wrongOldPassword_shouldThrow() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongOld", "hashedPassword")).thenReturn(false);

        assertThrows(BadRequestException.class,
                () -> userService.changePassword("testuser", "wrongOld", "newPass"));
    }
}
