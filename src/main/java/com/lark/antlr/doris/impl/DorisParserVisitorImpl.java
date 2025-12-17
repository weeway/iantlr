package com.lark.antlr.doris.impl;

import com.lark.antlr.doris.DorisParser;
import com.lark.antlr.doris.DorisParserBaseVisitor;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DorisParserVisitorImpl extends DorisParserBaseVisitor<String> {
    private static final Logger logger = LoggerFactory.getLogger(DorisParserVisitorImpl.class);

    public Set<String> sourceTables = new HashSet<>();
    public Set<String> targetTables = new HashSet<>();
    public DorisParserVisitorImpl(CommonTokenStream commonTokenStream) {
    }

    /**
     * TableNameContext 对应 g4 语法文件中的 `#tableName` 规则
     * 是宽泛的表名定义，需要提取 multipartIdentifier 作为表名
     * @param ctx the parse tree
     * @return
     */
    @Override
    public String visitTableName(DorisParser.TableNameContext ctx) {
        if (hasAncestorByRuleIndex(ctx, DorisParser.RULE_fromClause)){
            sourceTables.add(ctx.multipartIdentifier().getText());
            logger.info("visitTableName, source table: "+ctx.multipartIdentifier().getText());
        }else {
            targetTables.add(ctx.multipartIdentifier().getText());
            logger.info("visitTableName, target table: "+ctx.multipartIdentifier().getText());
        }
        return visitChildren(ctx);
    }

    @Override
    public String visitTruncateTable(DorisParser.TruncateTableContext ctx) {
        targetTables.add(ctx.multipartIdentifier().getText());
        return visitChildren(ctx);
    }

    @Override
    public String visitDataDesc(DorisParser.DataDescContext ctx) {
        if(hasAncestorByClassName(ctx, "DorisParser$LoadContext")){
            targetTables.add(ctx.targetTableName.getText());
        }

        sourceTables.add(ctx.filePaths.stream().map(x->x.getText()).collect(Collectors.joining(",")));

        return visitChildren(ctx);
    }

    @Override
    public String visitLoad(DorisParser.LoadContext ctx) {
        // load 语句表名解析
        return visitChildren(ctx);
    }

    @Override
    public String visitMultipartIdentifier(DorisParser.MultipartIdentifierContext ctx) {
        // create/alter 表名解析
        // 方法1：通过确定父语句来确定是alter还是create
        // if (hasAncestorByClassName(ctx, "DorisParser$AlterTableContext")
        //         || hasAncestorByClassName(ctx, "DorisParser$CreateTableContext")){
        //     targetTables.add(ctx.getText());
        // }
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
        targetTables.add(ctx.multipartIdentifier().getText());
        return visitChildren(ctx);
    }

    @Override public String visitAlterTable(DorisParser.AlterTableContext ctx){
        // alterTable表名解析
        // 方式2: 直接通过访问alterTable子句别名访问
        targetTables.add(ctx.tableName.getText());
        return visitChildren(ctx);
    }

    @Override public String visitDropTable(DorisParser.DropTableContext ctx){
        // dropTable表名解析
        targetTables.add(ctx.multipartIdentifier().getText());
        return visitChildren(ctx);
    }

    @Override public String visitDelete(DorisParser.DeleteContext ctx){
        // deleteFrom表名解析
        targetTables.add(ctx.multipartIdentifier().getText());
        return visitChildren(ctx);
    }

    @Override public String visitUpdate(DorisParser.UpdateContext ctx){
        // updateTable表名解析
        targetTables.add(ctx.multipartIdentifier().getText());
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
