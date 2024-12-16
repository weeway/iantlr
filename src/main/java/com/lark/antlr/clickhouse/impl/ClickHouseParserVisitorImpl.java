package com.lark.antlr.clickhouse.impl;

import com.lark.antlr.clickhouse.ClickHouseParser;
import com.lark.antlr.clickhouse.ClickHouseParserBaseVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author weiwei.huang
 * @since 2023/8/24
 */
public class ClickHouseParserVisitorImpl extends ClickHouseParserBaseVisitor<List<String>> {

    @Override
    protected List<String> aggregateResult(List<String> aggregate, List<String> nextResult) {
        if (aggregate != null){
            if( nextResult != null ) {aggregate.addAll(nextResult);}
            return aggregate;
        }else if ( nextResult !=null ){
            return nextResult;
        }

        return new ArrayList<String>();
    }

    @Override
    public List<String> visitTableIdentifier(ClickHouseParser.TableIdentifierContext ctx) {
        List<String> tables = new ArrayList<String>();
        if (!"com.lark.antlr.ClickHouseParser$TableExprIdentifierContext"
                .equals(ctx.getParent().getClass().getName())){
            return tables;
        }

        if ( ctx.databaseIdentifier() != null) {
            tables.add(ctx.getChild(0).getText() + "." + ctx.getChild(2).getText());
        }else if ( ctx.identifier() != null){
            tables.add(ctx.getChild(0).getText());
        }

        return tables;
    }
}
