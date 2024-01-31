package year.exp.forge.dto;

import com.google.firebase.database.annotations.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @NotNull
    private String name;
    @NotNull
    private String surname;
    @NotNull
    private String fatherName;
    @NotNull
    private String diiaCode;
}
