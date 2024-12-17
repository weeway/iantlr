package com.lark.antlr.doris;

import com.lark.antlr.common.ErrorListener;
import com.lark.antlr.common.SyntaxException;
import com.lark.antlr.doris.impl.DorisParserVisitorImpl;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class DorisExceptionTest {
    private static final Logger logger = LoggerFactory.getLogger(DorisExceptionTest.class);

    @DataProvider(name = "errorDorisSql")
    public Object[][] providerNormalDorisSql(){
        return new Object[][]{
                {
                    "alterTableWithCustomPartitionSql",
                    "ALTER TABLE kdwbak.log_user_visit ADD PARTITION IF NOT EXISTS p_$[yyyyMMdd-1] VALUES IN ('${LastDay}')",
                    "占位符：p_$[yyyyMMdd-1] 语法解析失败"
                },
        };
    }

    @Test(dataProvider = "errorDorisSql", expectedExceptions = {SyntaxException.class})
    public void runParse(String label, String sql, String comment) {
        sql = sql.toUpperCase();
        logger.info("label=[{}], SQL=[{}], 解析失败原因=[{}]", label, sql, comment);
        DorisLexer dorisLexer = new DorisLexer(CharStreams.fromString(sql));
        CommonTokenStream tokenStream = new CommonTokenStream(dorisLexer);

        DorisParser dorisParser = new DorisParser(tokenStream);
        dorisParser.removeErrorListeners();
        dorisParser.addErrorListener(new ErrorListener());
        ParseTree pt = dorisParser.multiStatements();
        DorisParserVisitorImpl visitor = new DorisParserVisitorImpl(tokenStream);
        visitor.visit(pt);
    }
}
