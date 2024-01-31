package year.exp.forge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisteredUser extends Registered {
    private String about;
    private String position;
    private Map<String, String> contacts;
    private List<Education> educations;
}
