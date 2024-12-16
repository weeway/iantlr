package com.lark.antlr.doris.impl;

import com.lark.antlr.doris.DorisParser;
import com.lark.antlr.doris.DorisParserBaseVisitor;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class DorisParserVisitorImpl extends DorisParserBaseVisitor<String> {
    private static final Logger logger = LoggerFactory.getLogger(DorisParserVisitorImpl.class);

    public Set<String> sourceTables = new HashSet<>();
    public Set<String> targetTables = new HashSet<>();
    public DorisParserVisitorImpl(CommonTokenStream commonTokenStream) {
    }

    @Override
    public String visitTableName(DorisParser.TableNameContext ctx) {
        if (hasAncestor(ctx, DorisParser.RULE_fromClause)){
            sourceTables.add(ctx.getText());
            logger.info("visitTableName, source table: "+ctx.getText());
        }else {
            targetTables.add(ctx.getText());
            logger.info("visitTableName, target table: "+ctx.getText());
        }
        return visitChildren(ctx);
    }

    @Override public String visitFromClause(DorisParser.FromClauseContext ctx) {
        // logger.info("visitFromClause");
        return visitChildren(ctx);
    }

    @Override public String visitInsertTable(DorisParser.InsertTableContext ctx) {

        targetTables.add(ctx.tableName.getText());
        return visitChildren(ctx);
    }

    private Boolean hasAncestor(ParserRuleContext ctx, int ancestorIdx) {
        ParserRuleContext current = ctx.getParent();  // Start with the parent
        while (current != null) {
            if (current.getRuleIndex() == ancestorIdx) {
                return true;  // Found the ancestor
            }
            current = current.getParent();  // Move up to the next parent
        }
        return false;
    }
}
