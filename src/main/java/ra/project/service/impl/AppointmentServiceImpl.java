package ra.project.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ra.project.common.AppointmentStatus;
import ra.project.dto.request.AppointmentRequest;
import ra.project.entity.Appointment;
import ra.project.entity.User;
import ra.project.exception.BadRequestException;
import ra.project.exception.ConflictException;
import ra.project.exception.ResourceNotFoundException;
import ra.project.repository.AppointmentRepository;
import ra.project.repository.UserRepository;
import ra.project.service.AppointmentService;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    @Override
    public Appointment createAppointment(Long patientId, AppointmentRequest request) {
        LocalDate date = LocalDate.parse(request.getAppointmentDate());

        List<Appointment> conflict = appointmentRepository
                .findByDoctorIdAndAppointmentDateAndTimeSlot(request.getDoctorId(), date, request.getTimeSlot());
        if (!conflict.isEmpty()) {
            throw new ConflictException("Bác sĩ đã có lịch trùng vào khung giờ này");
        }

        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Bệnh nhân không tồn tại"));
        User doctor = userRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Bác sĩ không tồn tại"));

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .appointmentDate(date)
                .timeSlot(request.getTimeSlot())
                .symptomDescription(request.getSymptomDescription())
                .status(AppointmentStatus.PENDING)
                .build();

        return appointmentRepository.save(appointment);
    }

    @Override
    public List<Appointment> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    @Override
    public List<Appointment> getAppointmentsByDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    @Override
    public Appointment approveAppointment(Long appointmentId) {
        Appointment appointment = findById(appointmentId);
        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            throw new BadRequestException("Chỉ có thể phê duyệt lịch ở trạng thái PENDING");
        }
        appointment.setStatus(AppointmentStatus.APPROVED);
        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment cancelAppointment(Long appointmentId) {
        Appointment appointment = findById(appointmentId);
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BadRequestException("Không thể hủy lịch đã hoàn thành");
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);
        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment completeAppointment(Long appointmentId) {
        Appointment appointment = findById(appointmentId);
        if (appointment.getStatus() != AppointmentStatus.APPROVED) {
            throw new BadRequestException("Chỉ có thể hoàn thành lịch đã được phê duyệt");
        }
        appointment.setStatus(AppointmentStatus.COMPLETED);
        return appointmentRepository.save(appointment);
    }

    private Appointment findById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lịch khám không tồn tại với ID: " + id));
    }
}
