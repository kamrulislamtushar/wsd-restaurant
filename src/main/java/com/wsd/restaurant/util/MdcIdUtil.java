package com.wsd.restaurant.util;

import java.util.UUID;
import org.slf4j.MDC;

public class MdcIdUtil {
  private MdcIdUtil() {
  }

  public static String generateMdcId() {
    return UUID.randomUUID().toString().toUpperCase();
  }

  public static String getMdcId() {
    return MDC.get(Constants.NAME_CO_RELATION_ID);
  }

  public static void setMdcId(String token) {
    MDC.put(Constants.NAME_CO_RELATION_ID, token);
  }

  public static void setNewMdcId() {
    MDC.put(Constants.NAME_CO_RELATION_ID, generateMdcId());
  }

  public static void removeMdcId() {
    MDC.remove(Constants.NAME_CO_RELATION_ID);
  }
}
