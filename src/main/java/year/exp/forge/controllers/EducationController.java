package year.exp.forge.controllers;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import year.exp.forge.domain.Role;
import year.exp.forge.dto.CertificateBrief;
import year.exp.forge.dto.DiplomaBrief;
import year.exp.forge.dto.EducationRequest;
import year.exp.forge.dto.StudentTicketBrief;
import year.exp.forge.services.EducationService;
import year.exp.forge.services.UserService;
import year.exp.forge.domain.User;
import org.springframework.data.domain.PageRequest;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/educations")
public class EducationController {

    @Setter(onMethod_ = {@Autowired})
    UserService userService;

    @Setter(onMethod_ = {@Autowired})
    EducationService educationService;

    @PostMapping
    public ResponseEntity<Boolean> save(HttpServletRequest request, @RequestBody EducationRequest dto) {
        User user = userService.getUser(request);
        return ResponseEntity.ok(educationService.addEducation(user, dto));
    }

    @GetMapping
    public ResponseEntity<Page<year.exp.forge.dto.Education>> list(HttpServletRequest request, @RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "10") int size) {
        User user = userService.getUser(request);
        if (user.getRole() != Role.MODERATOR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(educationService.list(PageRequest.of(page, size)));
    }

    @DeleteMapping
    public ResponseEntity delete(HttpServletRequest request, @RequestParam(required = true) String id) {
        User user = userService.getUser(request);
        if (user.getRole() != Role.MODERATOR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            educationService.delete(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/approve/diploma")
    public ResponseEntity approve(HttpServletRequest request, @RequestBody DiplomaBrief diplomaBrief) {
        User user = userService.getUser(request);
        if (user.getRole() != Role.MODERATOR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        educationService.save(diplomaBrief);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/approve/ticket")
    public ResponseEntity approve(HttpServletRequest request, @RequestBody StudentTicketBrief studentTicketBrief) {
        User user = userService.getUser(request);
        if (user.getRole() != Role.MODERATOR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        educationService.save(studentTicketBrief);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/approve/certificate")
    public ResponseEntity approve(HttpServletRequest request, @RequestBody CertificateBrief certificateBrief) {
        User user = userService.getUser(request);
        if (user.getRole() != Role.MODERATOR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        educationService.save(certificateBrief);
        return ResponseEntity.noContent().build();
    }
}
