package com.wsd.restaurant.repository;

import com.wsd.restaurant.domain.Order;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Order entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
  Page<Order> findByOrderTimeAfter(Instant today, Pageable pageable);
}
