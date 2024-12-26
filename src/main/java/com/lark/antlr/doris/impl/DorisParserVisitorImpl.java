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
        if (hasAncestorByRuleIndex(ctx, DorisParser.RULE_fromClause)){
            sourceTables.add(ctx.getText());
            logger.info("visitTableName, source table: "+ctx.getText());
        }else {
            targetTables.add(ctx.getText());
            logger.info("visitTableName, target table: "+ctx.getText());
        }
        return visitChildren(ctx);
    }

    @Override
    public String visitMultipartIdentifier(DorisParser.MultipartIdentifierContext ctx) {
        // create/alter 表名解析
        // 方法1：通过确定父语句来确定是alter还是create
        if (hasAncestorByClassName(ctx, "DorisParser$AlterTableContext")
                || hasAncestorByClassName(ctx, "DorisParser$CreateTableContext")){
            targetTables.add(ctx.getText());
        }
        return visitChildren(ctx);
    }

    @Override public String visitInsertTable(DorisParser.InsertTableContext ctx) {
        // insetTable表名解析
        targetTables.add(ctx.tableName.getText());
        return visitChildren(ctx);
    }

    @Override public String visitCreateTable(DorisParser.CreateTableContext ctx){
        // createTable表名解析
        // 方式2: 直接通过访问createTable子句别名访问
        logger.info("visitCreateTable: {}", ctx.multipartIdentifier().getText());
        return visitChildren(ctx);
    }

    @Override public String visitAlterTable(DorisParser.AlterTableContext ctx){
        // alterTable表名解析
        // 方式2: 直接通过访问alterTable子句别名访问
        logger.info("visitAlterTable: {}", ctx.tableName.getText());
        return visitChildren(ctx);
    }

    private Boolean hasAncestorByRuleIndex(ParserRuleContext ctx, int ancestorIdx) {
        ParserRuleContext current = ctx.getParent();  // Start with the parent
        while (current != null) {
            if (current.getRuleIndex() == ancestorIdx) {
                return true;  // Found the ancestor
            }
            current = current.getParent();  // Move up to the next parent
        }
        return false;
    }

    private Boolean hasAncestorByClassName(ParserRuleContext ctx, String className){
        ParserRuleContext current = ctx.getParent();  // Start with the parent
        while (current != null) {
            String[] parts = current.getClass().getName().split("\\.");
            if (parts[parts.length-1].equals(className)) {
                return true;  // Found the ancestor
            }
            current = current.getParent();  // Move up to the next parent
        }
        return false;
    }
}
