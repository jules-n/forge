package year.exp.forge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import year.exp.forge.domain.Type;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiplomaBrief extends Education {
    String university;
    String degree;
    String major;

    @Override
    public Type getType() {
        return Type.DIPLOMA;
    }
}
