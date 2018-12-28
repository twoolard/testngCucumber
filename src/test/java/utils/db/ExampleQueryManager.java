package utils.db;

import java.sql.ResultSet;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import utils.properties.TestConstant;
import utils.properties.TestProperties;

public class ExampleQueryManager {

    public ExampleQueryManager() {
    }

    ;


    public void deleteMethod(String name, String clientId) throws Exception {
        if (StringUtils.isBlank(name) || StringUtils.isBlank(clientId)) {
            throw new IllegalArgumentException("name or ClientID is empty.");
        }

        DbUtils db = null;

        try {
            db = new DbUtils();
            DbUtils.openConnection(db);
            // Find the campaign activation id
            String Id = selectMethod(name, clientId);
            // Remove any campaign activation entities for this activation
            db.updateQuery("delete from <table> where id='" + Id + "';");
            // Delete the campaign activation
            db.updateQuery("delete from <table> where id='" + Id + "' and client_id='" + clientId + "';");
        } finally {
            DbUtils.closeDBconnection(db);
        }

    }


    public void insertMethod(String name) throws Exception {

        StringBuilder query = new StringBuilder();

        //Use format method and '%s' to parameterize values
        String values = String.format("('%s','%s','%s','%s','%s', '%s')",
                new Date().getTime(),
                TestProperties.getProperty(TestConstant.CLIENT_ID),
                TestProperties.getProperty(TestConstant.PARTNER_ID),
                TestProperties.getProperty(TestConstant.Another_CLIENT_ID),
                name, "PUBLISHED");

        query.append("INSERT INTO <table> ");
        query.append("(ValueA, ValueB, ValueC, ValueB, name, published) ");
        query.append("VALUES ");
        query.append(values);

        DbUtils db = null;

        try {
            db = new DbUtils();
            DbUtils.openConnection(db);
            db.updateQuery(query.toString());
        } finally {
            DbUtils.closeDBconnection(db);
        }

    }

    public void updateId(String Id) throws Exception {
        if (StringUtils.isBlank(Id)) {
            throw new IllegalArgumentException("Id is empty");
        }

        DbUtils db = null;
        try {
            db = new DbUtils();
            DbUtils.openConnection(db);
            db.updateQuery("update <table> set column=4 where id='" + Id + "';");
        } finally {
            DbUtils.closeDBconnection(db);
        }
    }

    public String selectMethod(String name, String clientId) throws Exception {
        if (StringUtils.isBlank(name) || StringUtils.isBlank(clientId)) {
            throw new IllegalArgumentException("Name or ClientId is empty.");
        }

        DbUtils db = null;
        String Id = "";
        try {
            db = new DbUtils();
            DbUtils.openConnection(db);
            String query = String.format("select id from <table> where name='%s' and client_id='%s' limit 1;", name, clientId);
            ResultSet rs = db.query(query);
            while (rs.next()) {
                Id = rs.getString(1);
            }
        } finally {
            DbUtils.closeDBconnection(db);
        }

        return Id;
    }
}
