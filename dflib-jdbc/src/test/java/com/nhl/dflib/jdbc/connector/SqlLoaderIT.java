package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.unit.DataFrameAsserts;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.jdbc.Jdbc;
import com.nhl.dflib.jdbc.unit.BaseDbTest;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class SqlLoaderIT extends BaseDbTest {

    private JdbcConnector createConnector() {
        return Jdbc.connector(getDataSource());
    }

    @Test
    public void test() {

        T1.insert(1L, "n1", 50_000.01)
                .insert(2L, "n2", 120_000.)
                .insert(3L, "n3", 1_000.);

        DataFrame df = createConnector()
                .sqlLoader("SELECT \"id\", \"salary\" from \"t1\" WHERE \"id\" > 1")
                .load();

        new DataFrameAsserts(df, "id", "salary")
                .expectHeight(2)
                .expectRow(0, 2L, 120_000.)
                .expectRow(1, 3L, 1_000.);
    }

    @Test
    public void testReuse() {

        T1.insert(1L, "n1", 50_000.01)
                .insert(2L, "n2", 120_000.)
                .insert(3L, "n3", 1_000.);

        SqlLoader loader = createConnector().sqlLoader("SELECT \"id\", \"salary\" from \"t1\" WHERE \"id\" = ?");

        DataFrame df1 = loader.load(2L);
        new DataFrameAsserts(df1, "id", "salary")
                .expectHeight(1)
                .expectRow(0, 2L, 120_000.);

        DataFrame df2 = loader.load(1L);
        new DataFrameAsserts(df2, "id", "salary")
                .expectHeight(1)
                .expectRow(0, 1L, 50_000.01);
    }

    @Test
    public void testEmpty() {

        T1.insert(1L, "n1", 50_000.01);

        DataFrame df = createConnector()
                .sqlLoader("SELECT \"id\", \"salary\" from \"t1\" WHERE \"id\" > 1")
                .load();

        new DataFrameAsserts(df, "id", "salary").expectHeight(0);
    }

    @Test
    public void testColumnFunctions() {

        T1.insert(1L, "n1", 50_000.01)
                .insert(2L, "n2", 120_000.)
                .insert(3L, "n3", 1_000.);

        DataFrame df = createConnector()
                .sqlLoader("SELECT SUBSTR(\"name\", 2) as \"name\" from \"t1\" WHERE \"id\" > 1")
                .load();

        new DataFrameAsserts(df, "name")
                .expectHeight(2)
                .expectRow(0, "2")
                .expectRow(1, "3");
    }


    @Test
    public void testMaxRows() {

        T1.insert(1L, "n1", 50_000.01)
                .insert(2L, "n2", 120_000.)
                .insert(3L, "n3", 20_000.);

        DataFrame df = createConnector()
                .sqlLoader("SELECT * from \"t1\"")
                .maxRows(2)
                .load();

        new DataFrameAsserts(df, columnNames(T1))
                .expectHeight(2)
                .expectRow(0, 1L, "n1", 50_000.01)
                .expectRow(1, 2L, "n2", 120_000.);
    }

    @Test
    public void testParams() {

        LocalDate ld = LocalDate.of(1977, 02, 05);
        LocalDateTime ldt = LocalDateTime.of(2019, 02, 03, 1, 2, 5);
        LocalTime lt = LocalTime.of(5, 6, 8);

        byte[] bytes = new byte[]{3, 5, 11};
        long l1 = Integer.MAX_VALUE + 1L;

        T2.insert(l1, 67, 7.8, true, "s1", ldt, ld, lt, bytes)
                .insert(null, null, null, false, null, null, null, null, null);

        DataFrame df = createConnector()
                .sqlLoader("SELECT * from \"t2\"" +
                        " WHERE \"bigint\" = ?" +
                        " AND \"int\" = ?" +
                        " AND \"double\" = ?" +
                        " AND \"boolean\" = ?" +
                        " AND \"string\" = ?" +
                        " AND \"timestamp\" = ?" +
                        " AND \"date\" = ?" +
                        " AND \"time\" = ?" +
                        " AND \"bytes\" = ?")
                .load(l1, 67, 7.8, true, "s1", ldt, ld, lt, bytes);

        new DataFrameAsserts(df, columnNames(T2))
                .expectHeight(1)
                .expectRow(0, l1, 67, 7.8, true, "s1", ldt, ld, lt, bytes);
    }

    @Test
    public void testPrimitives() {

        T3.insert(-15, Long.MAX_VALUE - 1, 0.505, true);

        DataFrame df = createConnector()
                .sqlLoader("SELECT * from \"t3\"")
                .load();

        new DataFrameAsserts(df, "int", "long", "double", "boolean")
                .expectHeight(1)
                .expectIntColumns(0)
                .expectLongColumns(1)
                .expectDoubleColumns(2)
                .expectBooleanColumns(3)
                .expectRow(0, -15, Long.MAX_VALUE - 1, 0.505, true);
    }
}
