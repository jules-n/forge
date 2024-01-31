package year.exp.forge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import year.exp.forge.domain.Role;

import java.net.URL;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Registered {
    private String name;
    private String surname;
    private String email;
    private Role role;

    private URL photoLink;
}
