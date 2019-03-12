package com.springboot.service.serviceImpl;

import com.springboot.dto.AppointmentDto;
import com.springboot.dto.DiagnosisDto;
import com.springboot.dto.PatientDto;
import com.springboot.dto.UserDto;
import com.springboot.entity.*;
import com.springboot.repository.DiagnosisRepository;
import com.springboot.service.AppointmentService;
import com.springboot.service.DiagnosisService;
import com.springboot.service.PatientService;
import com.springboot.service.UserService;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service("diagnosisServiceImpl")
@Transactional
public class DiagnosisServiceImpl implements DiagnosisService {

    @Autowired
    @Qualifier("mapper")
    Mapper mapper;

    @Autowired
    @Qualifier("diagnosisRepository")
    DiagnosisRepository diagnosisRepository;

    @Autowired
    @Qualifier("patientServiceImpl")
    PatientService patientService;

    @Autowired
    @Qualifier("userServiceImpl")
    UserService userService;

    @Autowired
    @Qualifier("appointmentServiceImpl")
    AppointmentService appointmentService;

    @Override
    public List<DiagnosisDto> findAll() {
        List<Diagnosis> diagnoses = diagnosisRepository.findAll();

        List<DiagnosisDto> diagnosisDtos = new ArrayList<>();
        diagnoses.forEach(diagnosis -> diagnosisDtos.add(mapper.map(diagnosis,DiagnosisDto.class)));
        return diagnosisDtos;
    }

    @Override
    public DiagnosisDto findById(String id) {
        Optional<Diagnosis> diagnosisOptional = diagnosisRepository.findById(id);

        if (diagnosisOptional.isPresent()) {
            DiagnosisDto diagnosisDto = mapper.map(diagnosisOptional.get(),DiagnosisDto.class);
            return diagnosisDto;
        } else return null;
    }

    @Override
    public void save(DiagnosisDto diagnosisDto) {
        Diagnosis diagnosis = mapper.map(diagnosisDto,Diagnosis.class);

        UserDto userDto = userService.findById(diagnosisDto.getUserDto().getId());
        diagnosis.setUser(mapper.map(userDto, User.class));

        PatientDto patientDto = patientService.findById(diagnosisDto.getPatientDto().getId());
        diagnosis.setPatient(mapper.map(patientDto, Patient.class));

        AppointmentDto appointmentDto = appointmentService.findByID(diagnosisDto.getAppointmentDto().getId());
        diagnosis.setAppointment(mapper.map(appointmentDto, Appointment.class));

        diagnosis.setBasicInformation(new BasicInformation());
        this.getModifiedInfo(diagnosis.getBasicInformation(), "1", 1);

        diagnosisRepository.save(diagnosis);

    }

    private BasicInformation getModifiedInfo(BasicInformation basicInformation, String userID, Integer ClinicCode) {
        if (basicInformation.getCreateBy() != null) {
            basicInformation.setUpdateBy(userID);
            basicInformation.setUpdateDtm(new Date());
            basicInformation.setUpdateClinic(ClinicCode);
        }else {
            basicInformation.setCreateBy(userID);
            basicInformation.setCreateDtm(new Date());
            basicInformation.setCreateClinic(ClinicCode);
            basicInformation.setUpdateBy(userID);
            basicInformation.setUpdateDtm(new Date());
            basicInformation.setUpdateClinic(ClinicCode);
        }

        return basicInformation;
    }
}
