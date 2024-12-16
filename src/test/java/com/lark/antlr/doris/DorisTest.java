package com.lark.antlr.doris;

import com.lark.antlr.doris.impl.DorisParserVisitorImpl;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

public class DorisTest {
    private static final Logger logger
            = LoggerFactory.getLogger(DorisTest.class);
    @Test
    public void testDorisVisitorSelect(){
        DorisLexer dorisLexer = new DorisLexer(CharStreams.fromString("SELECT name, address FROM dataware3.kdwcdm.kdw_table_number_one"));
        CommonTokenStream tokenStream = new CommonTokenStream(dorisLexer);

        DorisParser dorisParser = new DorisParser(tokenStream);
        ParseTree pt = dorisParser.multiStatements();
        DorisParserVisitorImpl visitor = new DorisParserVisitorImpl(tokenStream);

        String result = visitor.visit(pt);
        System.out.println(result);
    }

    @Test
    public void testDorisVisitorInsert(){
        String sql = "INSERT OVERWRITE TABLE kdwcdm.label_generate_business_ktv_train_sample\n" +
                "SELECT if(not isnull(a.customerid), a.customerid, b.customerid),\n" +
                " if(isnull(b.customerid), 0, 1) as is_positive\n" +
                "from(\n" +
                " SELECT customerid\n" +
                " from kdwcdm.label_generate_nearly_year_active_woman_cid as tbl1\n" +
                " order by random()\n" +
                " limit 90000\n" +
                ") a FULL join kdwcdm.label_generate_business_ktv_positive_sample b \n" +
                "on a.customerid = b.customerid";

        sql = sql.toUpperCase();
        logger.info("SQL: {}", sql);
        DorisLexer dorisLexer = new DorisLexer(CharStreams.fromString(sql));
        CommonTokenStream tokenStream = new CommonTokenStream(dorisLexer);

        DorisParser dorisParser = new DorisParser(tokenStream);
        ParseTree pt = dorisParser.multiStatements();
        DorisParserVisitorImpl visitor = new DorisParserVisitorImpl(tokenStream);

        String result = visitor.visit(pt);
        logger.info("source tables:{}\ntarget tables:{}", visitor.sourceTables, visitor.targetTables);
    }
}
