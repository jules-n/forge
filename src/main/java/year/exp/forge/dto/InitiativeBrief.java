package year.exp.forge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InitiativeBrief {
    private String id;

    private String name;

    private String description;
}
