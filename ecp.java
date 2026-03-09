public Boolean isRppEcpExists(String fileId, String companyId) {
    Assert.notNull(dbDao);

    StringBuilder sb = new StringBuilder();

    sb.append("WITH hist AS ( ")
      .append("    SELECT FT.FE_FILE_PAYMENTSTMP AS payment_ts ")
      .append("    FROM FE_FILE_DUITNOW_ECP FE2 ")
      .append("    JOIN FE_TRANSACTIONQ FT ")
      .append("      ON FE2.RPP_FEFILEEF = FT.FE_FILE_FILEID ")
      .append("    WHERE FE2.RPP_STATUS = 'SC' ")
      .append("      AND FE2.RPP_ERROR_REASON IS NULL ")
      .append("      AND FT.FE_PRODUCTID IN ('43', '44', '54', '55') ")
      .append("      AND FT.FE_FILE_FILEID <> :fileId ")
      .append("      AND FT.FE_COMPANYID = :companyId ")
      .append("      AND FT.FE_FILE_PAYMENTSTMP < CURRENT_TIMESTAMP ")
      .append("      AND FE2.RPP_ID_VALUE = FE1.RPP_ID_VALUE ")
      .append("      AND FE2.RPP_PAYMENT_MODE = FE1.RPP_PAYMENT_MODE ")
      .append("), ")
      .append("recent_24h AS ( ")
      .append("    SELECT h.payment_ts ")
      .append("    FROM hist h ")
      .append("    WHERE h.payment_ts >= CURRENT_TIMESTAMP - INTERVAL '24' HOUR ")
      .append("), ")
      .append("last_365 AS ( ")
      .append("    SELECT h.payment_ts ")
      .append("    FROM hist h ")
      .append("    WHERE h.payment_ts >= CURRENT_TIMESTAMP - INTERVAL '365' DAY ")
      .append(") ")
      .append("SELECT CASE ")
      .append("         WHEN NOT EXISTS (SELECT 1 FROM last_365) THEN 0 ")
      .append("         WHEN EXISTS ( ")
      .append("              SELECT 1 ")
      .append("              FROM recent_24h r ")
      .append("              WHERE NOT EXISTS ( ")
      .append("                    SELECT 1 ")
      .append("                    FROM hist p ")
      .append("                    WHERE p.payment_ts < r.payment_ts ")
      .append("                      AND p.payment_ts >= r.payment_ts - INTERVAL '365' DAY ")
      .append("              ) ")
      .append("         ) THEN 0 ")
      .append("         ELSE (SELECT COUNT(*) FROM last_365) ")
      .append("       END AS txn_count ")
      .append("FROM FE_FILE_DUITNOW_ECP FE1 ")
      .append("WHERE FE1.RPP_FEFILEREF = :fileId ")
      .append("  AND FE1.RPP_STATUS = 'PE'");

    MapSqlParameterSource param = new MapSqlParameterSource();
    param.addValue("fileId", fileId);
    param.addValue("companyId", companyId);

    Integer count = dbDao.queryForObject(
            sb.toString(),
            param,
            Integer.class,
            "FirstTimeTransferChecker-isRppEcpExists"
    );

    Logger.info("param:{}", param.getValues());
    Logger.info("count:{}", count);

    return count != null && count > 0;
}
