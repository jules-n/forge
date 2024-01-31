package year.exp.forge.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentTicket extends Education {
    String number;
    String university;

    @Override
    public Type getType() {
        return Type.STUDENT_TICKET;
    }
}
