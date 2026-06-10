package ra.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentRequest {

    @NotNull(message = "Doctor ID không được để trống")
    private Long doctorId;

    @NotBlank(message = "Ngày khám không được để trống")
    private String appointmentDate;

    @NotBlank(message = "Khung giờ không được để trống")
    private String timeSlot;

    private String symptomDescription;
}
