package year.exp.forge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Candidate {
    private String id;

    private String email;

    private String about;

    private String position;

    private String keyWords;

    private List<Education> educations;
}
