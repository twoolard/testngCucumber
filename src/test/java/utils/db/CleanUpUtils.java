package utils.db;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import utils.LogUtil;


public class CleanUpUtils {

    private static final String ERROR_LOG_MESSAGE = "Error deleting leads from db. Error message: ";

    public static void cleanUp(String table, String idColumn, List<String> list) {

        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        try (DbUtils dbUtils = new DbUtils()) {
            DbUtils.openConnection(dbUtils);
            cleanUpTables(table, idColumn, list, dbUtils);
        } catch (Exception e) {
            LogUtil.error(ERROR_LOG_MESSAGE + e.getMessage(), e);
        }
    }

    public static void cleanUp(String table, List<String> list) {
        cleanUp(table, table + "_id", list);
    }

    /**
     * This method is deleting object from data base by it's id
     *
     * @param table   - table name
     * @param list    - list of id's to delete
     * @param dbUtils - connecton to db
     * @throws Exception
     */
    private static void cleanUpTables(String table, String idColumn, List<String> list, DbUtils dbUtils) throws Exception {

        StringBuilder deleteQuery = new StringBuilder();

        deleteQuery.append(String.format("DELETE FROM %s WHERE ", table));
        deleteQuery.append(idColumn);
        deleteQuery.append(" IN (" + DbUtils.convertListToSqlINCondition(list) + ")");

        dbUtils.updateQuery(deleteQuery.toString());
    }

}
