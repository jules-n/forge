package year.exp.forge.services;

import lombok.Setter;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import year.exp.forge.config.CustomHeaderInterceptor;
import year.exp.forge.dto.*;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Service
public class DiiaService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DiiaService.class);

    @Value("${diia.url.ident.document}")
    private String documentIdentification;
    @Value("${diia.session.token.path}")
    private String sessionTokenPath;
    @Value("${diia.acquirer.token}")
    private String acquirerToken;
    @Value("${diia.acquirer.auth}")
    private String authAcquirerToken;
    @Value("${diia.branch}")
    private String branch;

    private final RestTemplate restTemplate;

    @Setter(onMethod_ = {@Autowired})
    RedisService redisService;

    public DiiaService(){
        restTemplate = new RestTemplate();
    }

    public Boolean checkName(User dto){
        String token = getSessionToken();
        IdentificationRequest identification = new IdentificationRequest();
        identification.setBranchId(branch);
        identification.setBarcode(dto.getDiiaCode());
        Person person = new Person();
        person.setFirstName(dto.getName());
        person.setLastName(dto.getSurname());
        person.setMiddleName(dto.getFatherName());
        identification.setPerson(person);
        try {
            restTemplate.setInterceptors(
                    Collections.singletonList(
                            new CustomHeaderInterceptor("Authorization", "Bearer " + token)));

            IdentificationResult result = restTemplate.postForObject(documentIdentification, identification, IdentificationResult.class);
            assert result != null;
            return result.isSuccess();
        } catch (Exception ex) {
            logger.warn(ex.getMessage());
            return false;
        }
    }

    private String getSessionToken() {
        var sessionToken = redisService.getValue("diia_token");
        if (sessionToken == null) {
            sessionToken = getDiiaSessionToken();
            redisService.cacheValue("diia_token", sessionToken, 1, TimeUnit.HOURS);
        }
        return sessionToken.toString();
    }

    private String getDiiaSessionToken() {
        restTemplate.setInterceptors(
                Collections.singletonList(
                        new CustomHeaderInterceptor("Authorization", "Basic " + authAcquirerToken)));
        DiiaToken dto = restTemplate.getForObject(sessionTokenPath+acquirerToken, DiiaToken.class);
        assert dto != null;
        return dto.getToken();
    }
}
