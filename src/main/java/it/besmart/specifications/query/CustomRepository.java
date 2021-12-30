package it.besmart.specifications.query;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import it.besmart.specifications.search.objects.CountWrapper;
import it.besmart.specifications.search.objects.SumWrapper;

@Repository
public class CustomRepository {

	@PersistenceContext
	private EntityManager em;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Double getSum(Specification<?> spec, Class clazz, DbFieldWrapper field) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Number> query = cb.createQuery(Number.class);
		Root root = query.from(clazz);

		Expression<? extends Number> sumExp = getFieldExpression(field, root, cb);
		
		if (spec != null) 
			query.where(spec.toPredicate(root, query, cb));
		
		Number w = (Number) em.createQuery(
				query
				.select(cb.sum(sumExp)))
				.getSingleResult();
		
		Double d = 0.0;
		if(w!=null) {
			d = w.doubleValue();
		}
			
		return d;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Double getAverage(Specification<?> spec, Class clazz, DbFieldWrapper field) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Number> query = cb.createQuery(Number.class);
		Root root = query.from(clazz);

		Expression<? extends Number> sumExp = getFieldExpression(field, root, cb);
		
		if (spec != null) 
			query.where(spec.toPredicate(root, query, cb));
		
		Number w = (Number) em.createQuery(
				query
				.select(cb.avg(sumExp)))
				.getSingleResult();
		
		Double d = 0.0;
		if(w!=null) {
			d = w.doubleValue();
		}
			
		return d;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Long getCount(Specification<?> spec, Class clazz) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> query = cb.createQuery(Long.class);
		Root root = query.from(clazz);

		if (spec != null) 
			query.where(spec.toPredicate(root, query, cb));
		
		return (Long) em.createQuery(
				query
				.select(cb.count(root)))
				.getSingleResult();
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<SumWrapper> getSumByGroup(Specification<?> spec, Class clazz, DbFieldWrapper field, GroupByWrapper groupBy) {
		
		if(groupBy==null || StringUtils.isAllBlank(groupBy.getField())) {
			List<SumWrapper> res = new ArrayList<>();
			res.add(new SumWrapper(getSum(spec, clazz, field)));
			return res;
		}
			
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SumWrapper> query = cb.createQuery(SumWrapper.class);
		Root root = query.from(clazz);
		Expression<?> groupExp;
		Expression<?> nameExp;
		Expression<? extends Number> sumExp = getFieldExpression(field, root, cb);
		
		if (spec != null) 
			query.where(spec.toPredicate(root, query, cb));
		
		if (groupBy.getTimeAggr() != null)
			groupExp = CustomSpecification.dateTruncation(cb, root.get(groupBy.getField()), groupBy.getTimeAggr());
		else {
			if(!StringUtils.isAllBlank(groupBy.getParentJoin())) {
				Join<Root, Object> joinGroup = root.join(groupBy.getParentJoin());
				groupExp = joinGroup.get(groupBy.getField());
			}
			else
				groupExp = root.get(groupBy.getField());
		}
		
		if(StringUtils.isAllBlank(groupBy.getNameField())) 
			nameExp = groupExp;
		else {
			if(!StringUtils.isAllBlank(groupBy.getParentJoin())) {
				Join<Root, Object> joinGroup = root.join(groupBy.getParentJoin());
				nameExp = joinGroup.get(groupBy.getNameField());
			}
			else
				nameExp = root.get(groupBy.getNameField());
		}

		
		return em.createQuery(
				query
				.groupBy(groupExp)
				.multiselect(nameExp, cb.sum(sumExp)))
				.getResultList() ;
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<SumWrapper> getAverageByGroup(Specification<?> spec, Class clazz, DbFieldWrapper field, GroupByWrapper groupBy) {
		
		if(groupBy==null || StringUtils.isAllBlank(groupBy.getField())) {
			List<SumWrapper> res = new ArrayList<>();
			res.add(new SumWrapper(getAverage(spec, clazz, field)));
			return res;
		}
			
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SumWrapper> query = cb.createQuery(SumWrapper.class);
		Root root = query.from(clazz);
		Expression<?> groupExp;
		Expression<?> nameExp;
		Expression<? extends Number> sumExp = getFieldExpression(field, root, cb);
		
		if (spec != null) 
			query.where(spec.toPredicate(root, query, cb));
		
		if (groupBy.getTimeAggr() != null)
			groupExp = CustomSpecification.dateTruncation(cb, root.get(groupBy.getField()), groupBy.getTimeAggr());
		else {
			if(!StringUtils.isAllBlank(groupBy.getParentJoin())) {
				Join<Root, Object> joinGroup = root.join(groupBy.getParentJoin());
				groupExp = joinGroup.get(groupBy.getField());
			}
			else
				groupExp = root.get(groupBy.getField());
		}
		
		if(StringUtils.isAllBlank(groupBy.getNameField())) 
			nameExp = groupExp;
		else {
			if(!StringUtils.isAllBlank(groupBy.getParentJoin())) {
				Join<Root, Object> joinGroup = root.join(groupBy.getParentJoin());
				nameExp = joinGroup.get(groupBy.getNameField());
			}
			else
				nameExp = root.get(groupBy.getNameField());
		}

		
		return em.createQuery(
				query
				.groupBy(groupExp)
				.multiselect(nameExp, cb.avg(sumExp)))
				.getResultList() ;
		
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<CountWrapper> getCountByGroup(Specification<?> spec, Class clazz, GroupByWrapper groupBy) {
		
		if(groupBy==null || StringUtils.isAllBlank(groupBy.getField())) {
			List<CountWrapper> res = new ArrayList<>();
			res.add(new CountWrapper(getCount(spec, clazz)));
			return res;
		}
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CountWrapper> query = cb.createQuery(CountWrapper.class);
		Root root = query.from(clazz);
		Expression<?> groupExp;
		Expression<?> nameExp;
		
		if (spec != null) 
			query.where(spec.toPredicate(root, query, cb));
		
		if (groupBy.getTimeAggr() != null)
			groupExp = CustomSpecification.dateTruncation(cb, root.get(groupBy.getField()), groupBy.getTimeAggr());
		else {
			if(!StringUtils.isAllBlank(groupBy.getParentJoin())) {
				Join<Root, Object> joinGroup = root.join(groupBy.getParentJoin());
				groupExp = joinGroup.get(groupBy.getField());
			}
			else
				groupExp = root.get(groupBy.getField());
		}
		
		if(StringUtils.isAllBlank(groupBy.getNameField())) 
			nameExp = groupExp;
		else {
			if(!StringUtils.isAllBlank(groupBy.getParentJoin())) {
				Join<Root, Object> joinGroup = root.join(groupBy.getParentJoin());
				nameExp = joinGroup.get(groupBy.getNameField());
			}
			else
				nameExp = root.get(groupBy.getNameField());
		}
		
		return em.createQuery(
				query
				.groupBy(groupExp)
				.multiselect(nameExp, cb.count(root)))
				.getResultList() ;

	}

	
	private Expression<? extends Number> getFieldExpression(DbFieldWrapper field, Root<?> root, CriteriaBuilder cb) {
		if(field.getType()!=null) {
			switch(field.getType()) {
				case DIFF:
					return cb.diff(root.get(field.getField1()), root.get(field.getField2()));

				case DIFF_DATE:
					
				    Expression<Time> timeDiff = cb.function(
				            "TIMEDIFF",
				            Time.class,
				            root.get(field.getField1()), 
				            root.get(field.getField2()));
				    
				    Expression<Integer> timeToSec = cb.function(
				            "TIME_TO_SEC",
				            Integer.class,
				            timeDiff );

					return timeToSec;
					
				default:
					return root.get(field.getField1());
				
			}
		}
		else
			return root.get(field.getField1());
	}
}
