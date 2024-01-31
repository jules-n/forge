package year.exp.forge.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = Invitation.collection)
@Builder
public class Invitation {
    public static final String collection = "invitations";
    @Id
    private String id;
    String userId;
    String initiativeId;
    Membership membership;
}
