package utils.db;

import static utils.LogUtil.error;
import static utils.LogUtil.info;
import static utils.LogUtil.warn;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.StringUtils;

import com.microsoft.sqlserver.jdbc.SQLServerDriver;

import utils.properties.FrameworkProperties;
import utils.properties.TestConstant;
import utils.properties.TestProperties;

public class DbUtils implements Closeable {
    private static final String MYSQL_CLASS = "com.mysql.jdbc.Driver";

    private Connection conn = null;

    /// Connecting to MYSQL data source
    public static void openConnection(DbUtils dbUtils) throws SQLException {
        dbUtils.open(TestProperties.getProperty(TestConstant.WRITE_CONN), TestProperties.getProperty(TestConstant.WRITE_UID),
                TestProperties.getProperty(TestConstant.WRITE_PWD));
    }

    public static void openLocalConnection(DbUtils dbUtils) throws SQLException {
        dbUtils.open(TestProperties.getProperty(TestConstant.WRITE_CONN), TestProperties.getProperty(TestConstant.WRITE_UID),
                TestProperties.getProperty(TestConstant.WRITE_PWD));
    }
    public static DbUtils openConnection() throws SQLException {
        DbUtils dbUtils = new DbUtils();
        dbUtils.open(TestProperties.getProperty(TestConstant.WRITE_CONN), TestProperties.getProperty(TestConstant.WRITE_UID),
                TestProperties.getProperty(TestConstant.WRITE_PWD));
        return dbUtils;
    }

    /// Connecting to SQL Server data source
    private void openSQLServerConnection(String tableName) throws SQLException {
        if (conn != null) {
            warn("Connection was already established, please check");
        } else {

            BasicDataSource ds = new BasicDataSource();
            ds.setDriverClassName(SQLServerDriver.class.getName());
            ds.setUrl(getSQLServerDbConnectionUrl(tableName));
            ds.setUsername(TestProperties.getProperty(TestConstant.WRITE_UID));
            ds.setPassword(TestProperties.getProperty(TestConstant.WRITE_PWD));

            try {
                conn = ds.getConnection();
                conn.setAutoCommit(true);
                info(String.format("Successfully connected to DB [%s]", ds.getUrl()));
            } catch (SQLException ex) {
                throw new SQLException(String.format("Didn't connect to DB [%s]", ds.getUrl()));
            }
        }

    }

    /// Connecting to SQL Server data source
    public static void connectToSQLServer(DbUtils dbUtils, String tableName) throws SQLException {
        dbUtils.openSQLServerConnection(tableName);
    }

    private String getSQLServerDbConnectionUrl(String partnerTableName) {
        if (StringUtils.isEmpty(partnerTableName)) {
            warn("Please provide proper table name");
        }
        return String.format("%s;databaseName=%s", TestProperties.getProperty(TestConstant.WRITE_CONN), partnerTableName);
    }


    /**
     * @param dbUtils
     */
    public static void closeDBconnection(DbUtils dbUtils) {
        dbUtils.close();
    }

    public static String convertListToSqlINCondition(List<String> list) {
        return "'" + StringUtils.join(list, "','") + "'";
    }

    private void open(String connString,
                      String user,
                      String password) throws SQLException {

        try {
            Class.forName(MYSQL_CLASS);
        } catch (ClassNotFoundException nfe) {
            error(MYSQL_CLASS + " not found in path. Please provide and try again.");
        }

        try {
            conn = DriverManager.getConnection(connString, user, password);
            conn.setAutoCommit(true);

            info("Successfully connected to DB [" + connString + "]");
        } catch (SQLException e) {
            error("Could not connect to DB. Message: " + e.getMessage(), e);
            throw new SQLException("Could not connect to the DB with the given parameters: " + connString + ", " + user + ", check property file for password");
        }
    }

    public void close() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                error("Exception thrown during closing connection to db: " + e.getMessage(), e);
            }
            info("Closed DB Connection");
        }
    }

    public ResultSet query(String query) throws SQLException {
        info("Query Statement [" + query + "]");
        if (conn == null) {
            info("No DB Connection exists. Ensure 'open' is called first with correct parameters.");
            return null;
        }

        Statement stmt = conn.createStatement();
        return stmt.executeQuery(query);
    }

    public int updateQuery(String query) throws SQLException {
        String allowUpdates = FrameworkProperties.allowDbUpdates();
        int count = 0;
        if (allowUpdates != null && (allowUpdates.equalsIgnoreCase("true") || allowUpdates.equalsIgnoreCase("on"))) {
            info("Update Statement [" + query + "]");
            if (conn == null) {
                info("No DB Connection exists. Ensure 'open' is called first with correct parameters.");
                return -1;
            }

            Statement stmt = null;
            try {
                stmt = conn.createStatement();
                count = stmt.executeUpdate(query);
            } finally {
                stmt.close();
            }
        } else {
            throw new SQLException("DB Updates are not allowed. No updates or deletes will be performed. To enable set system property 'db.allowupdates' equal to 'true'");
        }

        return count;
    }


}