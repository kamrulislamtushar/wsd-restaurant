package com.wsd.restaurant.dto.response;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class Response<T> {
  public T data;
  public PageInfo pageInfo;
  public boolean success = true;
  public String message = "success";
  public Integer code = HttpStatus.OK.value();
  public String status = HttpStatus.OK.getReasonPhrase();

  public Response(T data, PageInfo pageInfo) {
    this.data = data;
    this.pageInfo = pageInfo;
  }

  public Response(T data) {
    this.data = data;
  }
}
