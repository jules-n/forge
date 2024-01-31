package year.exp.forge.controllers;

import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import year.exp.forge.domain.Role;
import year.exp.forge.dto.Registered;
import year.exp.forge.dto.Social;
import year.exp.forge.dto.User;
import year.exp.forge.services.UserService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URL;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Setter(onMethod_ = {@Autowired})
    private ModelMapper modelMapper;

    @Setter(onMethod_ = {@Autowired})
    UserService userService;

    @GetMapping("/isRegistered")
    public ResponseEntity<Boolean> isRegistered(HttpServletRequest request) {
        return ResponseEntity.ok(userService.isRegistered(request));
    }

    @GetMapping("/role")
    public ResponseEntity<Role> getRole(HttpServletRequest request) {
        return ResponseEntity.ok(userService.getRole(request));
    }

    @PostMapping
    public ResponseEntity<Boolean> register(HttpServletRequest request, @RequestBody User dto) {
        return ResponseEntity.ok(userService.register(request, dto));
    }

    @GetMapping
    public ResponseEntity<Registered> get(HttpServletRequest request) {
        year.exp.forge.domain.User user = userService.getUser(request);
        return ResponseEntity.ok(modelMapper.map(user, Registered.class));
    }

    @GetMapping("{email}")
    public ResponseEntity<Registered> get(@PathVariable("email") String email) {
        year.exp.forge.domain.User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(modelMapper.map(user, Registered.class));
    }

    @PostMapping("/uploadPhoto")
    public ResponseEntity<URL> uploadPhoto(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        URL signedUrl = userService.uploadPhoto(request, file);
        return ResponseEntity.ok(signedUrl);
    }

    @PostMapping("/uploadFile")
    public ResponseEntity uploadFile(HttpServletRequest request, @RequestParam("file") MultipartFile file) throws IOException, InterruptedException {
        userService.uploadFile(request, file);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity update(HttpServletRequest request, @RequestBody Social social) {
        userService.update(request, social);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/list")
    public ResponseEntity<Page<Registered>> list(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "5") int size) {
        Page<Registered> list = userService.list(PageRequest.of(page, size));
        return ResponseEntity.ok(list);
    }

    @PostMapping("/search")
    public ResponseEntity<List<Registered>> search(@RequestParam String request) {
        if (request.contains("\"")) {
            request = request.replaceAll("\"", "");
        }
        List<Registered> list = userService.search(request);
        return ResponseEntity.ok(list);
    }
}
