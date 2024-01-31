package year.exp.forge.persistence;

import year.exp.forge.domain.User;

import java.util.Map;

public interface UserRepositoryCustom {
    User getByCriteria(Map<String, Object> criteria);
    void updateByCriteria(Map<String, Object> criteria, Map<String, Object> data);
}
