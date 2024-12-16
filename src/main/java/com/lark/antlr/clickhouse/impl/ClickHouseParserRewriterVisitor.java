package com.lark.antlr.clickhouse.impl;

import com.lark.antlr.clickhouse.ClickHouseParser;
import com.lark.antlr.clickhouse.ClickHouseParserBaseVisitor;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;

import java.util.List;

/**
 * @author weiwei.huang
 * @since 2023/8/24
 */
public class ClickHouseParserRewriterVisitor extends ClickHouseParserBaseVisitor<String> {
    public TokenStreamRewriter rewriter;
    public CommonTokenStream inputTokenStream;

    public ClickHouseParserRewriterVisitor(CommonTokenStream commonTokenStream) {
        this.inputTokenStream = commonTokenStream;
        this.rewriter = new TokenStreamRewriter(commonTokenStream);
    }

    @Override
    public String visitColumnExprFunction(ClickHouseParser.ColumnExprFunctionContext ctx){
        System.out.println("visitColumnExprFunction");
        String functionName = ctx.identifier().getText();

        if ("JSONExtractString".equals(functionName)) {
            // 改写函数
            List<ClickHouseParser.ColumnArgExprContext> ctxs = ctx.columnArgList().columnArgExpr();
            rewriter.replace(ctxs.get(1).start, ctxs.get(1).stop, "'$." + ctxs.get(1).getText().replace("'", "") + "'");
        }

        return visitChildren(ctx);
    }

    @Override
    public String visitColumnArgExpr(ClickHouseParser.ColumnArgExprContext ctx) {
        // System.out.println("ColumnArgExpr: "+ctx.getText());
        return null;
    }
}
