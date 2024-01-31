package year.exp.forge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import year.exp.forge.domain.Type;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Education {
    abstract public Type getType();
    private Type type;
    private String id;
}
