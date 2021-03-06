package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.print.Printer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 0.6
 */
public class SqlLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlLogger.class);

    private Printer paramPrinter;

    public SqlLogger(Printer paramPrinter) {
        this.paramPrinter = paramPrinter;
    }

    public void log(String sql) {
        if (LOGGER.isInfoEnabled()) {
            StringBuilder log = new StringBuilder(sql);
            LOGGER.info(log.toString());
        }
    }

    public void log(String sql, Series<?> params) {
        if (LOGGER.isInfoEnabled()) {

            int plen = params.size();
            String label = plen == 1 ? "1 param [" : plen + " params [";

            StringBuilder log = new StringBuilder(sql)
                    .append(" | bind ")
                    .append(label)
                    .append(paramPrinter.toString(params))
                    .append("] ");

            LOGGER.info(log.toString());
        }
    }

    public void log(String sql, DataFrame paramsBatch) {
        if (LOGGER.isInfoEnabled()) {

            int ph = paramsBatch.height();
            String slabel = ph == 1 ? "1 set of " : ph + " sets of ";

            int pw = paramsBatch.width();
            String plabel = pw == 1 ? "1 param [" : pw + " params [";



            StringBuilder log = new StringBuilder(sql)
                    .append(" | bind ")
                    .append(slabel)
                    .append(plabel)
                    .append(paramPrinter.toString(paramsBatch))
                    .append("]");

            LOGGER.info(log.toString());
        }
    }
}
