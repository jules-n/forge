package year.exp.forge.controllers;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import year.exp.forge.domain.Initiative;
import year.exp.forge.domain.Status;
import year.exp.forge.domain.User;
import year.exp.forge.dto.Application;
import year.exp.forge.dto.InitiativeBrief;
import year.exp.forge.dto.InitiativeCreate;
import year.exp.forge.dto.Invitation;
import year.exp.forge.services.InitiativeService;
import year.exp.forge.services.UserService;
import year.exp.forge.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/initiatives")
public class InitiativesControllers {

    @Setter(onMethod_={@Autowired})
    InitiativeService initiativeService;

    @Setter(onMethod_={@Autowired})
    UserService userService;

    @PostMapping
    public ResponseEntity create(HttpServletRequest request, @RequestBody InitiativeCreate dto) {
        if (!StringUtil.notEmpty(dto.getName()) ||
                !StringUtil.notEmpty(dto.getDescription()) ||
                !StringUtil.notEmpty(dto.getPrivateLink())) {
            return ResponseEntity.badRequest().build();
        }
        User user = userService.getUser(request);
        initiativeService.create(user.getId(), dto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<InitiativeBrief>> list(HttpServletRequest request,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size,
                                                      @RequestParam(defaultValue = "all") String owner) {
        User user = userService.getUser(request);
        Page<Initiative> initiatives = initiativeService.list(PageRequest.of(page, size), owner, user.getId());
        return ResponseEntity.ok(initiatives.map(this::convert));
    }

    @GetMapping("/my")
    public ResponseEntity<List<InitiativeBrief>> list(HttpServletRequest request) {
        User user = userService.getUser(request);
        List<Initiative> initiatives = initiativeService.list(user.getId());
        List<InitiativeBrief> list = initiatives.stream().map(this::convert).toList();
        return ResponseEntity.ok(list);
    }

    @PostMapping("{id}/apply/")
    public ResponseEntity apply(HttpServletRequest request, @PathVariable("id") String initiativeId) {
        User user = userService.getUser(request);
        initiativeService.apply(initiativeId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("{id}/invite/")
    public ResponseEntity invite(HttpServletRequest request, @PathVariable("id") String initiativeId,
                                 @RequestParam String email) {
        User user = userService.getUser(request);
        if (!initiativeService.isMember(user.getId(), initiativeId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        User potentialMember = userService.getUserByEmail(email);
        initiativeService.invite(initiativeId, potentialMember.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("{id}")
    public ResponseEntity<year.exp.forge.dto.Initiative> get(HttpServletRequest request, @PathVariable("id") String initiativeId) {
        User user = userService.getUser(request);
        if (!initiativeService.isMember(user.getId(), initiativeId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(initiativeService.get(initiativeId));
    }

    @PutMapping("{id}")
    public ResponseEntity<Status> update(HttpServletRequest request, @PathVariable("id") String initiativeId, @RequestParam Status status) {
        User user = userService.getUser(request);
        if (!initiativeService.isMember(user.getId(), initiativeId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        status = initiativeService.updateStatus(initiativeId, status);
        return ResponseEntity.ok(status);
    }

    @GetMapping("{id}/applications")
    public ResponseEntity<Page<Application>> applications(HttpServletRequest request,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size,
                                                          @PathVariable("id") String initiativeId) {
        User user = userService.getUser(request);
        if (!initiativeService.isMember(user.getId(), initiativeId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Page<Application> list = initiativeService.applicationList(initiativeId, PageRequest.of(page, size));
        return ResponseEntity.ok(list);
    }

    @PostMapping("{initiativeId}/applications/{applicationId}")
    public ResponseEntity applications(HttpServletRequest request,
                                                          @PathVariable("initiativeId") String initiativeId,
                                                          @PathVariable("applicationId") String applicationId,
                                                          @RequestParam boolean decision) {
        User user = userService.getUser(request);
        if (!initiativeService.isMember(user.getId(), initiativeId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        initiativeService.setDecision(initiativeId, applicationId, decision);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/invitations")
    public ResponseEntity<Page<Invitation>> list(HttpServletRequest request, @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        Page<Invitation> response = initiativeService.invitations(request, PageRequest.of(page, size));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/invitations/{invitationId}")
    public ResponseEntity list(HttpServletRequest request, @PathVariable("invitationId") String invitationId,
                               @RequestParam("decision") boolean decision) {
        initiativeService.setDecision(request, invitationId, decision);
        return ResponseEntity.noContent().build();
    }

    private InitiativeBrief convert(Initiative initiative) {
        return new InitiativeBrief(initiative.getId(), initiative.getName(), initiative.getDescription());
    }
}
