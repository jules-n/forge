package year.exp.forge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import year.exp.forge.domain.Type;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertificateBrief extends Education {
    String result;
    @Override
    public Type getType() {
        return Type.CERTIFICATE;
    }

}
