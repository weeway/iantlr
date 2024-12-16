package com.lark.antlr.clickhouse.impl;

import com.lark.antlr.clickhouse.ClickHouseLexer;
import com.lark.antlr.clickhouse.ClickHouseParser;
import com.lark.antlr.clickhouse.impl.ClickHouseParserListenerImpl;
import com.lark.antlr.clickhouse.impl.ClickHouseParserRewriterVisitor;
import com.lark.antlr.clickhouse.impl.ClickHouseParserVisitorUtil;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * @author weiwei.huang
 * @since 2023/8/24
 */
public class ClickhouseMain {

    static String insertSql = "select  p_ds as qry_date,  t2.SongName  as  song_name,  o.songid  as song_id,  o.rank  as song_rank,  o.cnt  as play_cnt, 'daily' as tag\n" +
            "from  (select  p_ds,  d.1.1  as  songid,  d.1.2  as  cnt,  d.2  as  rank\n" +
            "from  (  select  p_ds,  arrayJoin(arrayZip(groupArray((songid,  cnt)),  arrayEnumerate(groupArray((songid,  cnt)))))  d\n" +
            "from  (select  p_ds,  JSONExtractString(eventattr,  'songid')  as  songid,count(1)  as  cnt\n" +
            "from  kdwcdm.dwd_log_webchatpoint_dd_ext\n" +
            "where  p_ds  between  '${LastDay}'  and  '${LastDay}'  and  event  =  '45020107'\n" +
            "and  JSONExtractString(eventattr,  'prevsource')  =  '2'\n" +
            "group  by  songid,  p_ds\n" +
            "order  by  p_ds,  cnt  desc)  a\n" +
            "group  by  p_ds\n" +
            ")  t  where  rank  <=  3000)  o\n" +
            "left  join  mysql_newkdwfdm.kdw_bas_song  t2\n" +
            "on  o.songid  =  t2.SongID\n" +
            "order  by  p_ds  desc,  rank";

    static String selectSql = "select JSONExtractString(json, 'name') from kdwcomp.kdw_tbl_company_info";
    public static void main(String[] args) {
        // testListener();
        testRewriterVisitor();
    }

    public static void testRewriterVisitor() {
        ClickHouseLexer lexer = new ClickHouseLexer(CharStreams.fromString(selectSql));
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        ClickHouseParser parser = new ClickHouseParser(tokenStream);

        ParseTree pt = parser.selectStmt();
        ClickHouseParserRewriterVisitor visitor = new ClickHouseParserRewriterVisitor(tokenStream);
        // pt.accept(visitor);
        String result = visitor.visit(pt);

        // 输出调整后的sql
        System.out.println("Final rewritten Sql: " + visitor.rewriter.getText());

        // visit 之后的原始字符流
        System.out.println("Visit result: " + result);
    }
    public static String getTextWithWhitespace(ParseTree tree) {
        StringBuilder builder = new StringBuilder();
        traverse(tree, builder);
        return builder.toString();
    }

    public static void traverse(ParseTree tree, StringBuilder builder) {
        if (tree instanceof TerminalNode) {
            TerminalNode terminal = (TerminalNode) tree;
            builder.append(terminal.getSymbol().getText()).append(" ");
        } else if (tree instanceof ParserRuleContext) {
            ParserRuleContext ctx = (ParserRuleContext) tree;
            for (ParseTree child : ctx.children) {
                traverse(child, builder);
            }
        }
    }

    public static void testVisitor() {
        ClickHouseLexer lexer = new ClickHouseLexer(CharStreams.fromString(selectSql));
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        ClickHouseParser parser = new ClickHouseParser(tokenStream);

        ParseTree pt = parser.selectStmt();
        ClickHouseParserVisitorUtil visitor = new ClickHouseParserVisitorUtil(tokenStream);
        pt.accept(visitor);
        System.out.println(pt.getText());
        System.out.println(getTextWithWhitespace(pt));
    }
    public static void testListener() {
        ClickHouseLexer lexer = new ClickHouseLexer(CharStreams.fromString(insertSql));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ClickHouseParser parser = new ClickHouseParser(tokens);

        ParseTree pt = parser.selectStmt();
        ParseTreeWalker walker = new ParseTreeWalker();
        ClickHouseParserListenerImpl listener = new ClickHouseParserListenerImpl();
        walker.walk(listener, pt);
    }
}
