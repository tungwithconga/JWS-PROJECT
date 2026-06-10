package ra.project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ra.project.dto.request.AppointmentRequest;
import ra.project.dto.request.ChangePasswordRequest;
import ra.project.dto.response.ApiResponse;
import ra.project.entity.Appointment;
import ra.project.security.principal.UserPrincipal;
import ra.project.service.AppointmentService;
import ra.project.service.MedicalRecordService;
import ra.project.service.UserService;
import ra.project.entity.MedicalRecord;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patient")
@RequiredArgsConstructor
public class PatientController {

    private final AppointmentService appointmentService;
    private final UserService userService;
    private final MedicalRecordService medicalRecordService;

    @PostMapping("/appointments")
    public ResponseEntity<ApiResponse<Appointment>> createAppointment(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody AppointmentRequest request) {
        Appointment appointment = appointmentService.createAppointment(
                principal.getUser().getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<Appointment>builder()
                        .success(true)
                        .message("Đặt lịch khám thành công")
                        .data(appointment)
                        .build()
        );
    }

    @GetMapping("/appointments")
    public ResponseEntity<ApiResponse<List<Appointment>>> getMyAppointments(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<Appointment> appointments = appointmentService.getAppointmentsByPatient(
                principal.getUser().getId());
        return ResponseEntity.ok(
                ApiResponse.<List<Appointment>>builder()
                        .success(true)
                        .message("Lấy lịch sử khám thành công")
                        .data(appointments)
                        .build()
        );
    }

    @GetMapping("/medical-records")
    public ResponseEntity<ApiResponse<List<MedicalRecord>>> getMyRecords(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<MedicalRecord> records = medicalRecordService.getRecordsByPatient(
                principal.getUser().getId());
        return ResponseEntity.ok(
                ApiResponse.<List<MedicalRecord>>builder()
                        .success(true)
                        .message("Lấy hồ sơ bệnh án thành công")
                        .data(records)
                        .build()
        );
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(principal.getUsername(), request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Đổi mật khẩu thành công")
                        .data(null)
                        .build()
        );
    }
}
