package it.besmart.specifications.query;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.JoinType;

import org.springframework.data.jpa.domain.Specification;
import it.besmart.specifications.enums.Operations;

public class CustomSpecificationBuilder {

	private final List<SearchCriteria> params;

	public CustomSpecificationBuilder() {
		
		params = new ArrayList<>();
	}
	
	public CustomSpecificationBuilder with(String key, Operations operation, Object value, String parent) {
		params.add(new SearchCriteria(key, operation, value, parent));
		return this;
	}

	public CustomSpecificationBuilder with(String key, Operations operation, Object value, String parent, JoinType joinType) {
		params.add(new SearchCriteria(key, operation, value, parent, joinType));
		return this;
	}
	
	public CustomSpecificationBuilder with(String key, Operations operation, Object value) {
		params.add(new SearchCriteria(key, operation, value));
		return this;
	}
	
	public CustomSpecificationBuilder remove(String key, Operations operation, Object value, String parent) {
		params.remove(new SearchCriteria(key, operation, value, parent));
		return this;
	}

	public <T> Specification<T> build() {
        if (params.isEmpty()) {
            return null;
        }
        List<Specification<T>> specs = new ArrayList<>();
        for (SearchCriteria param : params) {
            specs.add((Specification<T>) new CustomSpecification(param));
        }
        Specification<T> result = specs.get(0);
        for (int i = 1; i < specs.size(); i++) {
            result = Specification.where(result).and(specs.get(i));
        }
        return result;

	}
	
	
	public <T> Specification<T> buildOr() { //does not work when join TODO
        if (params.isEmpty()) {
            return null;
        }
        List<Specification<T>> specs = new ArrayList<>();
        for (SearchCriteria param : params) {
            specs.add((Specification<T>) new CustomSpecification(param));
        }
        Specification<T> result = specs.get(0);
        for (int i = 1; i < specs.size(); i++) {
            result = Specification.where(result).or(specs.get(i));
        }
        return result;

	}

	@Override
	public String toString() {
		return "CustomSpecificationBuilder [params=" + params + "]";
	}
}
