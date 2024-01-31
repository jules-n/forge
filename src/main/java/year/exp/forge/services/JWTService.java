package year.exp.forge.services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class JWTService {

    @SneakyThrows
    public String getEmail(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        String idToken = header.substring(7);
        FirebaseToken firebaseToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
        return firebaseToken.getEmail();
    }
}
