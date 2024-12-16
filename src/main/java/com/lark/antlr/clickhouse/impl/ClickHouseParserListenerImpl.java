package com.lark.antlr.clickhouse.impl;

import com.lark.antlr.clickhouse.ClickHouseParser;
import com.lark.antlr.clickhouse.ClickHouseParserBaseListener;

/**
 * @author weiwei.huang
 * @since 2023/8/28
 */
public class ClickHouseParserListenerImpl extends ClickHouseParserBaseListener {
    @Override
    public void enterTableExprIdentifier(ClickHouseParser.TableExprIdentifierContext ctx) {
        String tableName = ctx.tableIdentifier().identifier().getText();
        ClickHouseParser.DatabaseIdentifierContext databaseNameIdfCxt = ctx.tableIdentifier().databaseIdentifier();
        if (databaseNameIdfCxt != null) {
            String databaseName = databaseNameIdfCxt.identifier().getText();
            System.out.println("table name: " + databaseName + "." + tableName);
        }
    }

    @Override
    public void enterColumnExprFunction(ClickHouseParser.ColumnExprFunctionContext ctx) {
        String functionStr = ctx.getText();
        String functionName = ctx.identifier().getText();

        System.out.println("functionExpr: " + functionStr);
        System.out.println("functionName: " + functionName);
    }
}
