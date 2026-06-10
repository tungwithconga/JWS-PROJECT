package ra.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ra.project.dto.request.LoginRequest;
import ra.project.dto.request.RegisterRequest;
import ra.project.dto.response.AuthResponse;
import ra.project.dto.response.UserResponse;
import ra.project.common.Role;
import ra.project.repository.TokenBlacklistRepository;
import ra.project.security.jwt.JwtService;
import ra.project.security.principal.UserDetailServiceImpl;
import ra.project.service.AuthService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailServiceImpl userDetailService;

    @MockBean
    private TokenBlacklistRepository tokenBlacklistRepository;

    @Test
    @DisplayName("POST /api/v1/auth/register - đăng ký thành công trả 201")
    void register_shouldReturn201() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .username("newuser")
                .email("new@hospital.com")
                .password("Pass@123")
                .fullName("New User")
                .phone("0933333333")
                .build();

        UserResponse response = UserResponse.builder()
                .id(1L)
                .username("newuser")
                .email("new@hospital.com")
                .fullName("New User")
                .role(Role.PATIENT)
                .active(true)
                .build();

        when(authService.registerPatient(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("newuser"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - đăng nhập thành công")
    void login_shouldReturn200() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .username("admin")
                .password("Admin@123")
                .build();

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken("access-token-xxx")
                .refreshToken("refresh-token-xxx")
                .tokenType("Bearer")
                .username("admin")
                .role(Role.ADMIN)
                .build();

        when(authService.login(any())).thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("access-token-xxx"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/register - validation lỗi trả 400")
    void register_invalidData_shouldReturn400() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .username("")
                .email("not-an-email")
                .password("123")
                .fullName("")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/auth/logout - đăng xuất thành công")
    void logout_shouldReturn200() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout")
                        .header("Authorization", "Bearer some-valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - thiếu username trả 400")
    void login_missingUsername_shouldReturn400() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .username("")
                .password("Pass@123")
                .build();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
