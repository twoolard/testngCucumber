package utils.db;

import java.util.Date;

import utils.properties.TestConstant;
import utils.properties.TestProperties;

public class CustomerQueryManager {

    public void createCustomer(String name) throws Exception {

        StringBuilder query = new StringBuilder();

        //Use format method and '%s' to parameterize values
        String values = String.format("('%s','%s','%s','%s')",
                new Date().getTime(),
                TestProperties.getProperty(TestConstant.CLIENT_ID),
                name, "PUBLISHED");

        query.append("INSERT INTO customer ");
        query.append("(customer_id, client_id, name, status) ");
        query.append("VALUES ");
        query.append(values);

        DbUtils db = null;

        try {
            db = new DbUtils();
            DbUtils.openLocalConnection(db);
            db.updateQuery(query.toString());
        } finally {
            DbUtils.closeDBconnection(db);
        }
    }
}
