package year.exp.forge.services;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import year.exp.forge.util.StringUtil;
import year.exp.forge.domain.Certificate;
import year.exp.forge.domain.Diploma;
import year.exp.forge.domain.StudentTicket;
import year.exp.forge.domain.User;
import year.exp.forge.dto.*;
import year.exp.forge.persistence.EducationRepository;

import java.util.List;
import java.util.Map;


@Service
public class EducationService {

    @Setter(onMethod_ = {@Autowired})
    EducationRepository educationRepository;

    @Setter(onMethod_ = {@Autowired})
    UserService userService;

    public boolean addEducation(User user, EducationRequest dto) {
        if (StringUtil.notEmpty(dto.getDiplomaNumber()) && StringUtil.notEmpty(dto.getDiplomaNumber())) {
            Diploma diploma = new Diploma();
            diploma.setUserId(user.getId());
            diploma.setSerialCode(dto.getSerialCode());
            diploma.setNumber(dto.getDiplomaNumber());
            educationRepository.save(diploma);
        }
        if (StringUtil.notEmpty(dto.getDiplomaNumber())) {
            StudentTicket studentTicket = new StudentTicket();
            studentTicket.setNumber(dto.getStudentTicket());
            studentTicket.setUserId(user.getId());
            educationRepository.save(studentTicket);
        }
        if (StringUtil.notEmpty(dto.getCertificateCompany()) && StringUtil.notEmpty(dto.getCertificateVerifyData())) {
            Certificate certificate = new Certificate();
            certificate.setResource(dto.getCertificateCompany());
            certificate.setDataToCheck(dto.getCertificateVerifyData());
            certificate.setUserId(user.getId());
            educationRepository.save(certificate);
        }
        return true;
    }

    public Page<year.exp.forge.dto.Education> list (PageRequest pageRequest) {
        Page<year.exp.forge.domain.Education> page = educationRepository.findAll(pageRequest);
        return page.map(this::convertForApprove);
    }

    private year.exp.forge.dto.Education convertForApprove(year.exp.forge.domain.Education entity) {
        switch (entity.getType()) {
            case STUDENT_TICKET -> {
                year.exp.forge.domain.StudentTicket studentTicket = (year.exp.forge.domain.StudentTicket) entity;
                year.exp.forge.dto.StudentTicket dto = new year.exp.forge.dto.StudentTicket();
                dto.setNumber(studentTicket.getNumber());
                dto.setUniversity(null);
                dto.setId(studentTicket.getId());
                dto.setPerson(getPerson(studentTicket.getUserId()));
                return dto;
            }
            case DIPLOMA -> {
                year.exp.forge.domain.Diploma diploma = (year.exp.forge.domain.Diploma) entity;
                year.exp.forge.dto.Diploma dto = new year.exp.forge.dto.Diploma();
                dto.setId(diploma.getId());
                dto.setNumber(diploma.getNumber());
                dto.setSerialCode(diploma.getSerialCode());
                dto.setUniversity(null);
                dto.setMajor(null);
                dto.setDegree(null);
                dto.setPerson(getPerson(diploma.getUserId()));
                return dto;
            }
            case CERTIFICATE -> {
                year.exp.forge.domain.Certificate certificate = (year.exp.forge.domain.Certificate) entity;
                year.exp.forge.dto.Certificate dto = new year.exp.forge.dto.Certificate();
                dto.setId(certificate.getId());
                dto.setResource(certificate.getResource());
                dto.setResult(null);
                dto.setDataToCheck(certificate.getDataToCheck());
                dto.setPerson(getPerson(certificate.getUserId()));
                return dto;
            }
        }
        throw new RuntimeException("What is this type of education? Type: " + entity.getType());
    }

    private Person getPerson(String id) {
        User user = userService.getUser(id);
        Person person = new Person();
        person.setFirstName(user.getName());
        person.setMiddleName(user.getFatherName());
        person.setLastName(user.getSurname());
        return person;
    }

    public void delete(String id) {
        educationRepository.deleteById(id);
    }

    public void save(Education entity) {
        year.exp.forge.domain.Education education = educationRepository.getByCriteria(Map.of("id", entity.getId()));
        User user = userService.getUser(education.getUserId());
        List<Education> educations = user.getEducations();
        educations.add(entity);
        user.setEducations(educations);
        userService.userRepository.save(user);
        educationRepository.deleteById(entity.getId());
    }
}
