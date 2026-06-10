package ra.project.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ra.project.common.Role;
import ra.project.entity.User;
import ra.project.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@hospital.com")
                    .passwordHash(passwordEncoder.encode("Admin@123"))
                    .fullName("System Admin")
                    .phone("0900000000")
                    .role(Role.ADMIN)
                    .isActive(true)
                    .build();
            userRepository.save(admin);
        }

        if (!userRepository.existsByUsername("doctor1")) {
            User doctor = User.builder()
                    .username("doctor1")
                    .email("doctor1@hospital.com")
                    .passwordHash(passwordEncoder.encode("Doctor@123"))
                    .fullName("Bác sĩ Nguyễn Văn A")
                    .phone("0911111111")
                    .role(Role.DOCTOR)
                    .isActive(true)
                    .build();
            userRepository.save(doctor);
        }

        if (!userRepository.existsByUsername("patient1")) {
            User patient = User.builder()
                    .username("patient1")
                    .email("patient1@hospital.com")
                    .passwordHash(passwordEncoder.encode("Patient@123"))
                    .fullName("Bệnh nhân Trần Văn B")
                    .phone("0922222222")
                    .role(Role.PATIENT)
                    .isActive(true)
                    .build();
            userRepository.save(patient);
        }
    }
}
