public Boolean isRppEcpExists(String fileId, String companyId) {
    Assert.notNull(dbDao);

    StringBuilder sb = new StringBuilder();

    sb.append("SELECT COUNT(*) ")
      .append("FROM FE_FILE_DUITNOW_ECP FE1 ")
      .append("WHERE FE1.RPP_FEFILEREF = :fileId ")
      .append("  AND FE1.RPP_STATUS = 'PE' ")
      .append("  AND EXISTS ( ")
      .append("      SELECT 1 ")
      .append("      FROM FE_FILE_DUITNOW_ECP FE2 ")
      .append("      JOIN FE_TRANSACTIONQ FT ")
      .append("        ON FE2.RPP_FEFILEREF = FT.FE_FILE_FILEID ")
      .append("      WHERE FE2.RPP_STATUS = 'SC' ")
      .append("        AND FE2.RPP_ERROR_REASON IS NULL ")
      .append("        AND FT.FE_PRODUCTID IN ('43', '44', '54', '55') ")
      .append("        AND FT.FE_FILE_FILEID <> :fileId ")
      .append("        AND FT.FE_COMPANYID = :companyId ")
      .append("        AND FT.FE_FILE_PAYMENTSTMP < SYSDATE ")
      .append("        AND FT.FE_FILE_PAYMENTSTMP >= SYSDATE - 365 ")
      .append("        AND FE2.RPP_ID_VALUE = FE1.RPP_ID_VALUE ")
      .append("        AND FE2.RPP_PAYMENT_MODE = FE1.RPP_PAYMENT_MODE ")
      .append("        AND NOT ( ")
      .append("            FT.FE_FILE_PAYMENTSTMP >= SYSDATE - 1 ")
      .append("            AND NOT EXISTS ( ")
      .append("                SELECT 1 ")
      .append("                FROM FE_FILE_DUITNOW_ECP FE3 ")
      .append("                JOIN FE_TRANSACTIONQ FT3 ")
      .append("                  ON FE3.RPP_FEFILEREF = FT3.FE_FILE_FILEID ")
      .append("                WHERE FE3.RPP_STATUS = 'SC' ")
      .append("                  AND FE3.RPP_ERROR_REASON IS NULL ")
      .append("                  AND FT3.FE_PRODUCTID IN ('43', '44', '54', '55') ")
      .append("                  AND FT3.FE_FILE_FILEID <> :fileId ")
      .append("                  AND FT3.FE_COMPANYID = :companyId ")
      .append("                  AND FE3.RPP_ID_VALUE = FE1.RPP_ID_VALUE ")
      .append("                  AND FE3.RPP_PAYMENT_MODE = FE1.RPP_PAYMENT_MODE ")
      .append("                  AND FT3.FE_FILE_PAYMENTSTMP < FT.FE_FILE_PAYMENTSTMP ")
      .append("                  AND FT3.FE_FILE_PAYMENTSTMP >= FT.FE_FILE_PAYMENTSTMP - 365 ")
      .append("            ) ")
      .append("        ) ")
      .append("  ) ");

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
