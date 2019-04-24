package com.nhl.dflib.csv;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.row.RowProxy;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

public class CsvSaver {

    private CSVFormat format;

    public CsvSaver() {
        this.format = CSVFormat.DEFAULT;
    }

    /**
     * Optionally sets the style or format of the imported CSV. CSVFormat comes from "commons-csv" library and
     * contains a number of predefined formats, such as CSVFormat.MYSQL, etc. It also allows to customize the format
     * further, by defining custom delimiters, line separators, etc.
     *
     * @param format a format object defined in commons-csv library
     * @return this loader instance
     */
    public CsvSaver format(CSVFormat format) {
        this.format = format;
        return this;
    }

    public void save(DataFrame df, File file) {

        try (FileWriter out = new FileWriter(file)) {
            save(df, out);
        } catch (IOException e) {
            throw new RuntimeException("Error writing CSV to " + file, e);
        }
    }

    public void save(DataFrame df, String fileName) {

        try (FileWriter out = new FileWriter(fileName)) {
            save(df, out);
        } catch (IOException e) {
            throw new RuntimeException("Error writing CSV to " + fileName, e);
        }
    }

    public void save(DataFrame df, Appendable out) {

        try {
            CSVPrinter printer = new CSVPrinter(out, format);
            printHeader(printer, df.getColumnsIndex());

            int len = df.width();
            for (RowProxy r : df) {
                printRow(printer, r, len);
            }

        } catch (IOException e) {
            throw new RuntimeException("Error writing CSV", e);
        }
    }

    public String saveToString(DataFrame df) {

        StringWriter out = new StringWriter();
        save(df, out);
        return out.toString();
    }

    private void printHeader(CSVPrinter printer, Index index) throws IOException {
        for (String label : index.getLabels()) {
            printer.print(label);
        }
        printer.println();
    }

    private void printRow(CSVPrinter printer, RowProxy row, int len) throws IOException {
        for (int i = 0; i < len; i++) {
            printer.print(row.get(i));
        }
        printer.println();
    }
}
