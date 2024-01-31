package year.exp.forge.persistence;

import year.exp.forge.domain.Education;

import java.util.Map;

public interface EducationRepositoryCustom {
    Education getByCriteria(Map<String, Object> criteria);
}
