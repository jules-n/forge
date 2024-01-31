package year.exp.forge.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = Education.COLLECTION)
public abstract class Education {
    public static final String COLLECTION = "educations";
    abstract public Type getType();
    private String userId;
    private Type type;
    @Id
    private String id;
}
