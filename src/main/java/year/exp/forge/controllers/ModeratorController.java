package year.exp.forge.controllers;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import year.exp.forge.domain.Role;
import year.exp.forge.services.UserService;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/moderator")
public class ModeratorController {

    @Setter(onMethod_ = {@Autowired})
    UserService userService;

    @GetMapping
    public ResponseEntity<String> test(HttpServletRequest request) {
        if (userService.getRole(request).equals(Role.MODERATOR)) {
            return ResponseEntity.ok("better call Budanov");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PostMapping("/addModerator")
    public ResponseEntity addModerator(HttpServletRequest request, @RequestBody String email) {
        if (userService.getRole(request).equals(Role.MODERATOR)) {
            userService.addModerator(email.replaceAll("\"", ""));
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
