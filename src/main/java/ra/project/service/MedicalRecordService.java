package ra.project.service;

import ra.project.entity.MedicalRecord;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MedicalRecordService {
    MedicalRecord createRecord(Long doctorId, Long patientId, Long appointmentId,
                               String diagnosis, String prescription, MultipartFile file);
    List<MedicalRecord> getRecordsByPatient(Long patientId);
    List<MedicalRecord> getRecordsByDoctor(Long doctorId);
}
