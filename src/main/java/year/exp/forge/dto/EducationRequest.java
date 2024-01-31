package year.exp.forge.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EducationRequest {
    String diplomaNumber;
    String serialCode;
    String studentTicket;
    String certificateCompany;
    String certificateVerifyData;
}
