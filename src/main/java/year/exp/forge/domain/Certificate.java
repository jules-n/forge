package year.exp.forge.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Certificate extends Education {
    String resource;
    String dataToCheck;
    String result;

    @Override
    public Type getType() {
        return Type.CERTIFICATE;
    }
}
