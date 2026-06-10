package ra.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ra.project.entity.Appointment;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDoctorId(Long doctorId);
    List<Appointment> findByDoctorIdAndAppointmentDateAndTimeSlot(
            Long doctorId, LocalDate date, String timeSlot
    );
}
