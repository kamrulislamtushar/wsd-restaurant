package com.wsd.restaurant.dto;

import java.io.Serializable;
import java.time.Instant;

public class SaleDayInfo implements Serializable {
  private Instant saleDay;
  private Double totalSale;

  public SaleDayInfo(Instant saleDay, Double totalSale) {
    this.saleDay = saleDay;
    this.totalSale = totalSale;
  }

  public Instant getSaleDay() {
    return saleDay;
  }

  public void setSaleDay(Instant saleDay) {
    this.saleDay = saleDay;
  }

  public Double getTotalSale() {
    return totalSale;
  }

  public void setTotalSale(Double totalSale) {
    this.totalSale = totalSale;
  }
}
