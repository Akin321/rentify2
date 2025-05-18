package com.example.demo.Repository;

import java.util.ArrayList;
import java.util.List;


import org.springframework.data.jpa.domain.Specification;

import com.example.demo.model.NewUserModel;
import com.example.demo.model.OrderItem;
import com.example.demo.model.OrderModel;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class OrderSpecifications {
	public static Specification<OrderItem> filterBy(List<String> filters,String keyword,NewUserModel user){
		return (root,query,cb)->{
			List<Predicate> predicates=new ArrayList<>();
			
			if (keyword != null && !keyword.trim().isEmpty()) {
	            // Join with orderItems
		           
		            predicates.add(cb.like(cb.lower(root.get("productName")), "%" + keyword.toLowerCase() + "%"));
		            
		       
	        }
			if(filters!=null && !filters.isEmpty()) {
				
				 List<Predicate> statusPredicates = new ArrayList<>();

		            for (String filter : filters) {
		                if (filter.equalsIgnoreCase("onTheWay")) {
		                    CriteriaBuilder.In<String> inClause = cb.in(root.get("orderStatus"));
		                    inClause.value("OrderPlaced");
		                    inClause.value("Shipped");
		                    inClause.value("OutForDelivery");
		                    statusPredicates.add(inClause);
		                } else if (filter.equalsIgnoreCase("Delivered")) {
		                    statusPredicates.add(cb.equal(root.get("orderStatus"), "Delivered"));
		                } else if (filter.equalsIgnoreCase("Cancelled")) {
		                    statusPredicates.add(cb.equal(root.get("orderStatus"), "Cancelled"));
		                }
		            }

		            if (!statusPredicates.isEmpty()) {
		                predicates.add(cb.or(statusPredicates.toArray(new Predicate[0])));
		            }
			
				
			}
			if (user != null) {
				 Join<OrderItem, OrderModel> orderJoin = root.join("order", JoinType.INNER); // or LEFT if needed
				    predicates.add(cb.equal(orderJoin.get("user"), user));
                
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
			
	}
}
