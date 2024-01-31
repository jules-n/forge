package year.exp.forge.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = User.COLLECTION)
public class User {
    public static final String COLLECTION = "users";
    @Id
    private String id;
    @Indexed(unique = true)
    private String email;
    private String photoLink;
    private String name;
    private String surname;
    private String fatherName;
    private String about;
    private List<year.exp.forge.dto.Education> educations = new ArrayList<>();
    @NonNull
    private Role role;
    private String keyWords;
    private Map<String, String> contacts = new HashMap<>();
    private String position;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(getId(), user.getId()) && Objects.equals(getEmail(), user.getEmail()) && Objects.equals(getPhotoLink(), user.getPhotoLink()) && Objects.equals(getName(), user.getName()) && Objects.equals(getSurname(), user.getSurname()) && Objects.equals(getFatherName(), user.getFatherName()) && Objects.equals(getAbout(), user.getAbout()) && Objects.equals(getEducations(), user.getEducations()) && getRole() == user.getRole() && Objects.equals(getContacts(), user.getContacts()) && Objects.equals(getPosition(), user.getPosition());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getEmail(), getPhotoLink(), getName(), getSurname(), getFatherName(), getAbout(), getEducations(), getRole(), getContacts(), getPosition());
    }
}
