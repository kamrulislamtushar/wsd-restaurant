package com.wsd.restaurant.dto.response;

import java.util.Objects;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

@Data
@Builder
public class PageInfo {
  private Long totalElements;
  private int totalPages;
  private int currentPage;
  private int pageSize;

  public static <T> PageInfo of(Page<T> page) {
    if (Objects.nonNull(page)) {
      return PageInfo.builder()
          .totalElements(page.getTotalElements())
          .totalPages(page.getTotalPages())
          .currentPage(page.getPageable().getPageNumber())
          .pageSize(page.getPageable().getPageSize())
          .build();
    } else {
      return null;
    }
  }
}
