package ra.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ra.project.dto.response.ApiResponse;
import ra.project.entity.Appointment;
import ra.project.entity.MedicalRecord;
import ra.project.security.principal.UserPrincipal;
import ra.project.service.AppointmentService;
import ra.project.service.MedicalRecordService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/doctor")
@RequiredArgsConstructor
public class DoctorController {

    private final AppointmentService appointmentService;
    private final MedicalRecordService medicalRecordService;

    @GetMapping("/appointments")
    public ResponseEntity<ApiResponse<List<Appointment>>> getMyAppointments(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<Appointment> appointments = appointmentService.getAppointmentsByDoctor(
                principal.getUser().getId());
        return ResponseEntity.ok(
                ApiResponse.<List<Appointment>>builder()
                        .success(true)
                        .message("Lấy danh sách lịch khám thành công")
                        .data(appointments)
                        .build()
        );
    }

    @PutMapping("/appointments/{id}/approve")
    public ResponseEntity<ApiResponse<Appointment>> approveAppointment(@PathVariable Long id) {
        Appointment appointment = appointmentService.approveAppointment(id);
        return ResponseEntity.ok(
                ApiResponse.<Appointment>builder()
                        .success(true)
                        .message("Phê duyệt lịch khám thành công")
                        .data(appointment)
                        .build()
        );
    }

    @PutMapping("/appointments/{id}/cancel")
    public ResponseEntity<ApiResponse<Appointment>> cancelAppointment(@PathVariable Long id) {
        Appointment appointment = appointmentService.cancelAppointment(id);
        return ResponseEntity.ok(
                ApiResponse.<Appointment>builder()
                        .success(true)
                        .message("Từ chối lịch khám thành công")
                        .data(appointment)
                        .build()
        );
    }

    @PutMapping("/appointments/{id}/complete")
    public ResponseEntity<ApiResponse<Appointment>> completeAppointment(@PathVariable Long id) {
        Appointment appointment = appointmentService.completeAppointment(id);
        return ResponseEntity.ok(
                ApiResponse.<Appointment>builder()
                        .success(true)
                        .message("Hoàn thành lịch khám thành công")
                        .data(appointment)
                        .build()
        );
    }

    @PostMapping("/records/upload")
    public ResponseEntity<ApiResponse<MedicalRecord>> uploadMedicalRecord(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam Long patientId,
            @RequestParam(required = false) Long appointmentId,
            @RequestParam String diagnosis,
            @RequestParam String prescription,
            @RequestParam(required = false) MultipartFile file) {
        MedicalRecord record = medicalRecordService.createRecord(
                principal.getUser().getId(), patientId, appointmentId,
                diagnosis, prescription, file);
        return ResponseEntity.ok(
                ApiResponse.<MedicalRecord>builder()
                        .success(true)
                        .message("Tải lên hồ sơ bệnh án thành công")
                        .data(record)
                        .build()
        );
    }
}
