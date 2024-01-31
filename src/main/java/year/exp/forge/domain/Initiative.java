package year.exp.forge.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = Initiative.collection)
@Builder
public class Initiative {
    public static final String collection = "initiatives";
    @Id
    private String id;

    private String name;

    private String description;

    private String privateLink;

    private Status status;

    private List<String> members;
}
