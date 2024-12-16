package com.lark.antlr.clickhouse.impl;

import com.lark.antlr.clickhouse.ClickHouseParser;
import com.lark.antlr.clickhouse.ClickHouseParserBaseVisitor;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author weiwei.huang
 * @since 2023/8/24
 */
public class ClickHouseParserVisitorUtil extends ClickHouseParserBaseVisitor<String> {
    static String TABLE_EXPR_CLASS = "com.lark.antlr.ClickHouseParser$TableExprIdentifierContext";
    public Set<String> sourceTables = new HashSet<String>();
    public Set<String> targetTables = new HashSet<String>();

    public TokenStreamRewriter rewriter;

    public ClickHouseParserVisitorUtil(CommonTokenStream commonTokenStream) {
        this.rewriter = new TokenStreamRewriter(commonTokenStream);
    }

    @Override
    public String visitInsertStmt(ClickHouseParser.InsertStmtContext ctx) {
        if (ctx.TABLE() != null){
            targetTables.add(ctx.TABLE().getText());
        }

        if (ctx.tableIdentifier() != null){
            if (ctx.tableIdentifier().databaseIdentifier() != null){
                targetTables.add(ctx.tableIdentifier().databaseIdentifier().getText()
                        +"."+ctx.tableIdentifier().identifier().getText());
            }else{
                targetTables.add(ctx.tableIdentifier().identifier().getText());
            }
        }
        return visitChildren(ctx);
    }

    @Override
    public String visitTableIdentifier(ClickHouseParser.TableIdentifierContext ctx) {
        if (!TABLE_EXPR_CLASS.equals(ctx.getParent().getClass().getName())){
            return null;
        }

        if ( ctx.databaseIdentifier() != null) {
            sourceTables.add(ctx.getChild(0).getText() + "." + ctx.getChild(2).getText());
        }else if ( ctx.identifier() != null){
            sourceTables.add(ctx.getChild(0).getText());
        }
        return null;
    }

    @Override
    public String visitSelectStmt(ClickHouseParser.SelectStmtContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public String visitColumnsExprColumn(ClickHouseParser.ColumnsExprColumnContext ctx) {
        ClickHouseParser.ColumnExprContext colCtx = ctx.columnExpr();
        List<ParseTree> pts = colCtx.children;
        pts.forEach(pt -> {
//            System.out.println(pt.getText());
        });
        return visitChildren(ctx);
    }

    @Override
    public String visitColumnExprFunction(ClickHouseParser.ColumnExprFunctionContext ctx){
        ctx.children.forEach(pt -> {
            System.out.println(pt.getText());
        });

        System.out.println();
        return "";
    }
}
