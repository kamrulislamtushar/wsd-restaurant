public Boolean isEcpExists(String fileId, String companyId) {
    Assert.notNull(dbDao);

    StringBuilder sb = new StringBuilder();

    sb.append("SELECT COUNT(*) ")
      .append("FROM FE_FILEECP FE1 ")
      .append("WHERE FE1.CM_FEFILEREF = :fileName ")
      .append("  AND FE1.CM_STATUS = 'PE' ")
      .append("  AND EXISTS ( ")
      .append("      SELECT 1 ")
      .append("      FROM FE_FILEECP FE2 ")
      .append("      JOIN FE_TRANSACTIONQ FT ")
      .append("        ON FE2.CM_FEFILEREF = FT.FE_FILE_FILEID ")
      .append("      WHERE FE2.CM_STATUS = 'SC' ")
      .append("        AND FE2.CM_ERROR_REASON IS NULL ")
      .append("        AND FT.FE_PRODUCTID IN ('16', '15') ")
      .append("        AND FT.FE_FILE_FILEID <> :fileName ")
      .append("        AND FT.FE_COMPANYID = :companyId ")
      .append("        AND FT.FE_FILE_PAYMENTSTMP < SYSDATE ")
      .append("        AND FT.FE_FILE_PAYMENTSTMP >= SYSDATE - 365 ")
      .append("        AND FE2.CM_BENE_ACCT = FE1.CM_BENE_ACCT ")
      .append("        AND FE2.CM_PAYMENT_MODE = FE1.CM_PAYMENT_MODE ")
      .append("        AND NOT ( ")
      .append("            FT.FE_FILE_PAYMENTSTMP >= SYSDATE - 1 ")
      .append("            AND NOT EXISTS ( ")
      .append("                SELECT 1 ")
      .append("                FROM FE_FILEECP FE3 ")
      .append("                JOIN FE_TRANSACTIONQ FT3 ")
      .append("                  ON FE3.CM_FEFILEREF = FT3.FE_FILE_FILEID ")
      .append("                WHERE FE3.CM_STATUS = 'SC' ")
      .append("                  AND FE3.CM_ERROR_REASON IS NULL ")
      .append("                  AND FT3.FE_PRODUCTID IN ('16', '15') ")
      .append("                  AND FT3.FE_FILE_FILEID <> :fileName ")
      .append("                  AND FT3.FE_COMPANYID = :companyId ")
      .append("                  AND FE3.CM_BENE_ACCT = FE1.CM_BENE_ACCT ")
      .append("                  AND FE3.CM_PAYMENT_MODE = FE1.CM_PAYMENT_MODE ")
      .append("                  AND FT3.FE_FILE_PAYMENTSTMP < FT.FE_FILE_PAYMENTSTMP ")
      .append("                  AND FT3.FE_FILE_PAYMENTSTMP >= FT.FE_FILE_PAYMENTSTMP - 365 ")
      .append("            ) ")
      .append("        ) ")
      .append("  ) ");

    MapSqlParameterSource param = new MapSqlParameterSource();
    param.addValue("fileName", fileId);
    param.addValue("companyId", companyId);

    Integer count = dbDao.queryForObject(
            sb.toString(),
            param,
            Integer.class,
            "FirstTimeTransferChecker-isEcpExists"
    );

    Logger.info("param:{}", param.getValues());
    Logger.info("count:{}", count);

    return count != null && count > 0;
}
