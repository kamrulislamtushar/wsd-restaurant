package com.wsd.restaurant.repository;

import com.wsd.restaurant.domain.Order;
import com.wsd.restaurant.dto.SaleDayInfo;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Order entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
  Page<Order> findByOrderTimeAfter(Instant today, Pageable pageable);

  @Query("SELECT MAX(o.totalPrice) FROM Order o WHERE o.orderTime >= :startDate AND o.orderTime < :endDate")
  Double findTodayHighestTotalPrice(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

  @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.orderTime >= :startDate AND o.orderTime < :endDate")
  Double findTodayTotalSaleAmount(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

  List<Order> findAllByUserId(@Param("userId") Long userId);

  @Query("SELECT new com.wsd.restaurant.dto.SaleDayInfo((o.orderTime) as saleDay, SUM(o.totalPrice) as totalSale)"
      + " FROM Order o "
      + "WHERE o.orderTime >= :startDate AND o.orderTime <= :endDate "
      + "GROUP BY saleDay "
      + "ORDER BY totalSale DESC "
      + "LIMIT 1")
  SaleDayInfo findMaxSaleDay(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);


}
