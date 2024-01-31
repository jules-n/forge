package year.exp.forge.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import year.exp.forge.domain.Role;

import java.net.URL;

@EqualsAndHashCode(callSuper = true)
@Data
public class Moderator extends Registered {
    public Moderator() {
        super();
    }

    public Moderator(String name, String surname, String email, Role role, URL photoLink) {
        super(name, surname, email, role, photoLink);
    }
}
