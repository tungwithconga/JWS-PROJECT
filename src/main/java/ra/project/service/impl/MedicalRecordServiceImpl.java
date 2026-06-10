package ra.project.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ra.project.entity.Appointment;
import ra.project.entity.MedicalRecord;
import ra.project.entity.User;
import ra.project.exception.ResourceNotFoundException;
import ra.project.repository.AppointmentRepository;
import ra.project.repository.MedicalRecordRepository;
import ra.project.repository.UserRepository;
import ra.project.service.CloudinaryService;
import ra.project.service.MedicalRecordService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public MedicalRecord createRecord(Long doctorId, Long patientId, Long appointmentId,
                                       String diagnosis, String prescription, MultipartFile file) {
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Bác sĩ không tồn tại"));
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Bệnh nhân không tồn tại"));

        MedicalRecord.MedicalRecordBuilder builder = MedicalRecord.builder()
                .doctor(doctor)
                .patient(patient)
                .diagnosis(diagnosis)
                .prescription(prescription);

        if (appointmentId != null) {
            Appointment appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Lịch khám không tồn tại"));
            builder.appointment(appointment);
        }

        if (file != null && !file.isEmpty()) {
            String fileUrl = cloudinaryService.uploadFile(file);
            builder.fileUrl(fileUrl);
        }

        return medicalRecordRepository.save(builder.build());
    }

    @Override
    public List<MedicalRecord> getRecordsByPatient(Long patientId) {
        return medicalRecordRepository.findByPatientId(patientId);
    }

    @Override
    public List<MedicalRecord> getRecordsByDoctor(Long doctorId) {
        return medicalRecordRepository.findByDoctorId(doctorId);
    }
}
