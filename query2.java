public Integer getEffectiveTxnCount(String companyId, String toAccNo, String productId) {
    Assert.notNull(dbDao);

    StringBuilder sb = new StringBuilder();

    sb.append("WITH hist AS ( ")
      .append("    SELECT t.FE_TRANSTMP ")
      .append("    FROM FE_TRANSACTIONQ t ")
      .append("    WHERE t.FE_COMPANYID = :companyId ")
      .append("      AND t.FE_TOACCNO = :toAccNo ")
      .append("      AND t.FE_TRANSTATUS = :tranStatus ")
      .append("      AND t.FE_PRODUCTID = :productId ")
      .append("      AND t.FE_TRANSTMP < SYSDATE ")
      .append("), ")
      .append("recent_24h AS ( ")
      .append("    SELECT h.FE_TRANSTMP ")
      .append("    FROM hist h ")
      .append("    WHERE h.FE_TRANSTMP >= SYSDATE - 1 ")
      .append("), ")
      .append("older_365 AS ( ")
      .append("    SELECT h.FE_TRANSTMP ")
      .append("    FROM hist h ")
      .append("    WHERE h.FE_TRANSTMP >= SYSDATE - 365 ")
      .append(") ")
      .append("SELECT CASE ")
      .append("         WHEN NOT EXISTS (SELECT 1 FROM older_365) THEN 0 ")
      .append("         WHEN EXISTS ( ")
      .append("              SELECT 1 ")
      .append("              FROM recent_24h r ")
      .append("              WHERE NOT EXISTS ( ")
      .append("                    SELECT 1 ")
      .append("                    FROM hist p ")
      .append("                    WHERE p.FE_TRANSTMP < r.FE_TRANSTMP ")
      .append("                      AND p.FE_TRANSTMP >= r.FE_TRANSTMP - 365 ")
      .append("              ) ")
      .append("         ) THEN 0 ")
      .append("         ELSE (SELECT COUNT(*) FROM older_365) ")
      .append("       END AS txn_count ")
      .append("FROM dual");

    MapSqlParameterSource param = new MapSqlParameterSource();
    param.addValue("companyId", companyId);
    param.addValue("toAccNo", toAccNo);
    param.addValue("tranStatus", TranStatus.SUCCESS.name());
    param.addValue("productId", productId);

    Integer count = dbDao.queryForObject(
            sb.toString(),
            param,
            Integer.class,
            "FirstTimeTransferChecker-getEffectiveTxnCount"
    );

    logger.info("param:{}", param.getValues());
    logger.info("count:{}", count);

    return count == null ? 0 : count;
}
