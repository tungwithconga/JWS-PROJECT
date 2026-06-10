package ra.project.service;

import ra.project.dto.request.AppointmentRequest;
import ra.project.dto.response.ApiResponse;
import ra.project.entity.Appointment;

import java.util.List;

public interface AppointmentService {
    Appointment createAppointment(Long patientId, AppointmentRequest request);
    List<Appointment> getAppointmentsByPatient(Long patientId);
    List<Appointment> getAppointmentsByDoctor(Long doctorId);
    Appointment approveAppointment(Long appointmentId);
    Appointment cancelAppointment(Long appointmentId);
    Appointment completeAppointment(Long appointmentId);
}
