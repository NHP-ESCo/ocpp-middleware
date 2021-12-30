package it.besmart.specifications.query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

import it.besmart.specifications.enums.Operations;
import it.besmart.specifications.enums.TimeStatType;


public class CustomSpecification implements Specification<Object> {

	private final Logger logger = LoggerFactory.getLogger(CustomSpecification.class);

	private final SearchCriteria criteria;
	

	@Override
	public Predicate toPredicate(Root<Object> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		
		//TODO manage different types of date in criteria (LocalDate, LocalDateTime, ZonedDateTime, DateTime)
		
		Predicate predicate = null;
		Operations operation = criteria.getOperation();
		JoinType joinType = JoinType.INNER;
		if(criteria.getJoinType()!=null)
			joinType = criteria.getJoinType();
		
		
		switch (operation) {

		case AFTER:
			LocalDateTime criteriaDateAfter = extractDateTime(criteria.getValue().toString(), false);
			
			if(criteriaDateAfter!=null)
				predicate = builder.greaterThanOrEqualTo(getDateField(root, criteria.getKey()), criteriaDateAfter);
			break;
		case AFTER_DATE:
			LocalDate criteriaDate = LocalDate.parse(criteria.getValue().toString());
			predicate = builder.greaterThanOrEqualTo(root.get(criteria.getKey()), criteriaDate);
			break;
		case AFTER_ZONED:
			ZonedDateTime criteriaZDateAfter = extractZonedDateTime(criteria.getValue().toString(), false);
			
			if(criteriaZDateAfter!=null)
				predicate = builder.greaterThanOrEqualTo(root.get(criteria.getKey()), criteriaZDateAfter);
			break;
		case ASC:
			String[] ascCriterias = criteria.getKey().split(",");

			List<Order> ascOrders = new ArrayList<>();
			for (int i = 0; i < ascCriterias.length; i++) {
				
				ascOrders.add(builder.asc(getField(root, ascCriterias[i])));
			}
			query.orderBy(ascOrders);
			break;

		case BEFORE:
			LocalDateTime criteriaDateBefore = extractDateTime(criteria.getValue().toString(), true);
			
			if(criteriaDateBefore!=null)
				predicate = builder.lessThanOrEqualTo(getDateField(root, criteria.getKey()), criteriaDateBefore);
			break;
		case BEFORE_DATE:
			LocalDate criteriaBeforeDate = LocalDate.parse(criteria.getValue().toString());
			predicate = builder.lessThanOrEqualTo(root.get(criteria.getKey()), criteriaBeforeDate);

			break;
		case BEFORE_ZONED:
			ZonedDateTime criteriaZDateBefore = extractZonedDateTime(criteria.getValue().toString(), true);
			
			if(criteriaZDateBefore!=null)
				predicate = builder.lessThanOrEqualTo(root.get(criteria.getKey()), criteriaZDateBefore);
			break;
		case IN_MONTH:
			
			predicate = builder.equal(dateTruncation(builder, root.get(criteria.getKey()), TimeStatType.MONTH), 
					criteria.getValue());
			
			break;
		case IN_YEAR:
			
			predicate = builder.equal(dateTruncation(builder, root.get(criteria.getKey()), TimeStatType.YEAR), 
					criteria.getValue());
			
			
			break;
		case DESC:
			String[] descCriterias = criteria.getKey().split(",");
			List<Order> descOrders = new ArrayList<>();
			for (int i = 0; i < descCriterias.length; i++) {
				descOrders.add(builder.desc(getField(root, descCriterias[i])));
			}
			query.orderBy(descOrders);
			break;

		case GREATER_THAN:
			predicate = builder.greaterThan(root.<String>get(criteria.getKey()), criteria.getValue().toString());
			break;
		case IN:
			final List<Predicate> orPredicates = new ArrayList<Predicate>();
			List<Object> objects = (List<Object>) criteria.getValue();
			for (Object l : objects) {
				orPredicates.add(builder.or(builder.equal(root.<String>get(criteria.getKey()), l)));
			}
			predicate = builder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));
			break;
		case IN_SET:
			final Set<Predicate> orSetPredicates = new HashSet<Predicate>();
			Set<Object> objectsSet = (Set<Object>) criteria.getValue();
			for (Object l : objectsSet) {
				orSetPredicates.add(builder.or(builder.equal(root.<String>get(criteria.getKey()), l)));
			}
			predicate = builder.or(orSetPredicates.toArray(new Predicate[orSetPredicates.size()]));
			break;
		case IS_NULL:
			predicate = builder.isNull(root.get(criteria.getKey()));
			break;
		case LIKE:
			if (getField(root, criteria.getKey()).getJavaType() == String.class) {
				predicate = builder.like(getStringField(root, criteria.getKey()), "%" + criteria.getValue() + "%");
			} else {
				predicate = builder.equal(getField(root, criteria.getKey()), criteria.getValue());
			}
			break;

		case EQUAL:
			predicate = builder.equal(getField(root, criteria.getKey()), criteria.getValue());
			break;	
		case LOWER_THAN:
			predicate = builder.lessThan(root.<String>get(criteria.getKey()), criteria.getValue().toString());
			break;
		case NOT_NULL:
			predicate = builder.isNotNull(root.get(criteria.getKey()));
			break;
		case NOT_LIKE:
			predicate = builder.notEqual(root.get(criteria.getKey()), criteria.getValue());
			break;
		case WITHIN:
//			ParameterExpression<Geometry> circleParm = cb.parameter(Geometry.class);
//		    cq.where(cb.isTrue(cb.function("st_within", Boolean.class, 
//		                                   root.get(Event_.location), circleParm)));
			break;
			
		/** JOIN **/
		case JOIN:
			Join<Root, Object> joinGroup = root.join(criteria.getParentObject(), joinType);
			predicate = builder.equal(getField(joinGroup, criteria.getKey()), criteria.getValue());
			break;
		case JOIN_NOT:
			Join<Root, Object> joinNotGroup = root.join(criteria.getParentObject(), joinType);
			predicate = builder.notEqual(getField(joinNotGroup, criteria.getKey()), criteria.getValue());
			break;
		case LIKE_JOIN:
			Join<Root, Object> joinGroupLike = root.join(criteria.getParentObject(), joinType);
			predicate = builder.like(joinGroupLike.<String>get(criteria.getKey()), "%" + criteria.getValue() + "%");
			break;
		case JOIN_NOT_NULL:
			Join<Root, Object> joinGroupNotNull = root.join(criteria.getParentObject(), joinType);
			predicate = builder.isNotNull(getField(joinGroupNotNull, criteria.getKey()));
			break;
		case JOIN_NULL:
			Join<Root, Object> joinGroupNull = root.join(criteria.getParentObject(), joinType);
			predicate = builder.isNull(getField(joinGroupNull, criteria.getKey()));
			break;
		case IN_JOIN:
			final List<Predicate> orPredicatesJoin = new ArrayList<Predicate>();
			Join<Root, Object> joinGroupIn = root.join(criteria.getParentObject(), joinType);
			List<Object> objectsJoin = (List<Object>) criteria.getValue();
			for (Object l : objectsJoin) {
				orPredicatesJoin.add(builder.or(builder.equal(joinGroupIn.<String>get(criteria.getKey()), l)));
			}
			predicate = builder.or(orPredicatesJoin.toArray(new Predicate[orPredicatesJoin.size()]));

			break;
		case IN_MANYTOMANY:
			Join<Root, Object> join2Group = root.join(criteria.getParentObject(), joinType);
			query.distinct(true);
			predicate = builder.or(join2Group.in(criteria.getValue()));
			break;
		

		default:
			break;
		

		}

		return predicate;
	}

	
	public CustomSpecification(SearchCriteria criteria) {
		super();
		this.criteria = criteria;
	}
	
	
	private Expression<Object> getField(Root<?> root, String key ) {
		
		String[] comps = key.split("\\.");

		switch(comps.length) {
			case 1: return root.get(key);
			case 2: return root.get(comps[0]).get(comps[1]);
			default: return root.get(key);
		}
	}
	
	//TODO generalize over types
	private Expression<String> getStringField(Root<?> root, String key ) {
		
		String[] comps = key.split("\\.");

		switch(comps.length) {
			case 1: return root.get(key);
			case 2: return root.get(comps[0]).get(comps[1]);
			default: return root.get(key);
		}
	}
	
	
	private Expression<LocalDateTime> getDateField(Root<?> root, String key ) {
		
		String[] comps = key.split("\\.");

		switch(comps.length) {
			case 1: return root.get(key);
			case 2: return root.get(comps[0]).get(comps[1]);
			default: return root.get(key);
		}
		
		
	}

	private Expression<Object> getField(Join<Root, Object> joinGroup, String key ) {
		
		String[] comps = key.split("\\.");

		switch(comps.length) {
			case 1: return joinGroup.get(key);
			case 2: return joinGroup.get(comps[0]).get(comps[1]);
			default: return joinGroup.get(key);
		}
		
		
	}
	
	
	private LocalDateTime extractDateTime(String date, boolean inclusive) {
		LocalDateTime criteriaDate = null;
		date.replaceAll(" ", "T");
		
		int zoneIndex = date.indexOf("+");
		if(zoneIndex==-1) 
			zoneIndex = date.indexOf("Z");
		
		if(zoneIndex!=-1) {
			date = date.substring(0, zoneIndex);
			logger.debug("Preformatted date: " + date);
		}
		
		try {
			criteriaDate = LocalDateTime.parse(date);
			
		} catch (DateTimeParseException e) {
			try {
				if(inclusive)
					date+="T23:59:59";
				else
					date+="T00:00:00";
				criteriaDate = LocalDateTime.parse(date);
			}
			catch(DateTimeParseException e1) {
				logger.error("Errore parsing della data", e);
			}
		}
		
		return criteriaDate;
	}
	
	private ZonedDateTime extractZonedDateTime(String date, boolean inclusive) {
		ZonedDateTime criteriaDate = null;
		try {
			criteriaDate = ZonedDateTime.parse(date);
			
		} catch (DateTimeParseException e) {
			//zone missing?
			try {
				
				criteriaDate = ZonedDateTime.parse(date+"Z");
			}
			catch(DateTimeParseException e1) {
				
				//time missing?
				try {
					if(inclusive)
						date+="T23:59:59Z";
					else
						date+="T00:00:00Z";
					criteriaDate = ZonedDateTime.parse(date);
				}
				catch(DateTimeParseException e2) {
					logger.error("Errore parsing della data", e2);
				}
			}
			
			
		}
		
		return criteriaDate;
	}

	
	
	/** Extract day, month of date **/
	public static Expression<Integer> dateTruncation(CriteriaBuilder cb, Expression<Integer> path, TimeStatType type) {
		
		Expression<Integer> time = path;
		
		if(type!=null) {
			switch(type) {
			case DAY:
				time = cb.function("day", Integer.class, path);
				break;
			case MONTH:
				time = cb.function("month", Integer.class, path);
				break;
			case YEAR:
				time = cb.function("year", Integer.class, path);
				break;
			default:
				break;
			
			}
		}
		
		return time;
		
	}

}