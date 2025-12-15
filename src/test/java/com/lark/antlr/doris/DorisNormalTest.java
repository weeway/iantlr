package com.lark.antlr.doris;

import com.lark.antlr.common.ErrorListener;
import com.lark.antlr.doris.impl.DorisParserVisitorImpl;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class DorisNormalTest {
    private static final Logger logger = LoggerFactory.getLogger(DorisNormalTest.class);

    @DataProvider(name = "normalDorisSql")
    public Object[][] providerNormalDorisSql(){
        return new Object[][]{
//                {"selectSql", "SELECT name, address FROM dataware3.kdwcdm.kdw_table_number_one"},
//                {"insertOverwriteSql", "INSERT OVERWRITE TABLE kdwcdm.label_generate_business_ktv_train_sample\n" +
//                        "SELECT if(not isnull(a.customerid), a.customerid, b.customerid),\n" +
//                        " if(isnull(b.customerid), 0, 1) as is_positive\n" +
//                        "from(\n" +
//                        " SELECT customerid\n" +
//                        " from kdwcdm.label_generate_nearly_year_active_woman_cid as tbl1\n" +
//                        " order by random()\n" +
//                        " limit 90000\n" +
//                        ") a FULL join kdwcdm.label_generate_business_ktv_positive_sample b \n" +
//                        "on a.customerid = b.customerid"},
//                {"alterTablePartitionSql", "ALTER TABLE kdwbak.log_user_visit ADD PARTITION IF NOT EXISTS p_20241217 VALUES IN ('${LastDay}')"},
//                {"createTableSql", "CREATE TABLE if not exists kdwcdm.kdw_bas_wechat_company_coord (\n" +
//                        "  `p_ds` date NULL,\n" +
//                        "  `company_code` varchar(16) NULL,\n" +
//                        "  `latitude` float NULL,\n" +
//                        "  `longitude` float NULL, \n" +
//                        "  `customer_id` varchar(64) NULL DEFAULT \"-1\"\n" +
//                        ") ENGINE=OLAP\n" +
//                        "DUPLICATE KEY(`p_ds`, `company_code`)\n" +
//                        "COMMENT 'OLAP'\n" +
//                        "DISTRIBUTED BY HASH(`p_ds`, `company_code`) BUCKETS 10\n" +
//                        "PROPERTIES (\n" +
//                        "\"file_cache_ttl_seconds\" = \"0\",\n" +
//                        "\"light_schema_change\" = \"true\",\n" +
//                        "\"group_commit_interval_ms\" = \"10000\",\n" +
//                        "\"group_commit_data_bytes\" = \"134217728\"\n" +
//                        ");"},
                {"truncateTableSql", "truncate table kdwcdm.dws_kdwuser_middle_user_ordersong_daily_dip  partition (p_fixed_value)"},
                {"brokerLoadDataSql", "load label dwd_log_webchatpoint_dd_ext_hourfixed_value\n" +
                        "(\n" +
                        "    data infile ('s3://km-bigdata/flinkbatch/log_webchatpoint_hh/output/fixed_value/*')\n" +
                        "    into table dwd_log_webchatpoint_dd_ext_hour \n" +
                        "    partition (p_fixed_value)\n" +
                        "    columns terminated by \"|\"\n" +
                        ")\n" +
                        "with s3\n" +
                        "(\n" +
                        "    \"s3.endpoint\" = \"http://oss-cn-hangzhou-internal.aliyuncs.com\",\n" +
                        "    \"s3.access_key\" = \"ltai5t9sbtb3lnixmijekue9\",\n" +
                        "    \"s3.secret_key\"=\"7wnioka8qkvapfkvvkl6cnmjs6uepe\",\n" +
                        "    \"s3.region\" = \"oss-cn-hangzhou\"\n" +
                        ")"},
        };
    }

    @Test(dataProvider = "normalDorisSql")
    public void runParse(String label, String sql) {
        sql = sql.toUpperCase();
        logger.info("label=[{}], SQL=[{}]", label, sql);
        DorisLexer dorisLexer = new DorisLexer(CharStreams.fromString(sql));
        CommonTokenStream tokenStream = new CommonTokenStream(dorisLexer);

        DorisParser dorisParser = new DorisParser(tokenStream);
        dorisParser.removeErrorListeners();
        dorisParser.addErrorListener(new ErrorListener());
        ParseTree pt = dorisParser.multiStatements();
        DorisParserVisitorImpl visitor = new DorisParserVisitorImpl(tokenStream);

        String result = visitor.visit(pt);
        logger.info("label=[{}], sourceTables=[{}], targetTables=[{}]", label, visitor.sourceTables, visitor.targetTables);
        logger.info("label=[{}], visit result=[{}]", label, result);
    }
}
