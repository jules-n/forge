package year.exp.forge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import year.exp.forge.domain.Status;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Initiative {
    private String id;

    private String name;

    private String description;

    private String privateLink;

    private Status status;

    private List<Member> members;
}
