package com.wsd.restaurant.repository;

import com.wsd.restaurant.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the OrderItems entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderItemsRepository extends JpaRepository<OrderItem, Long> {}
