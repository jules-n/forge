package year.exp.forge.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Diploma extends Education {
    String number;
    String serialCode;
    String university;
    String degree;
    String major;

    @Override
    public Type getType() {
        return Type.DIPLOMA;
    }
}
