package MySQL;
import org.apache.commons.dbcp.BasicDataSource;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Yinon on 07/06/2017.
 */
public class ConnectionManager {
    private static ConnectionManager     datasource;
    private BasicDataSource ds;

    private ConnectionManager() throws IOException, SQLException, PropertyVetoException {
        ds = new BasicDataSource();
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setUsername("root");
        String dbName = "Shafa";
        String userName = "root";
        String password = System.getenv("SHAFA_PASS");//"root";
        ds.setPassword(password);
        String hostname = "shafa1.ce1sh3jg1tvc.eu-west-1.rds.amazonaws.com";//"localhost";//
        String port = "3306";
        String jdbcUrl = "jdbc:mysql://" + hostname + ":" + port + "/" + dbName;
        ds.setUrl(jdbcUrl);

        // the settings below are optional -- dbcp can work with defaults
        ds.setMinIdle(5);
        ds.setMaxIdle(20);
        ds.setMaxOpenPreparedStatements(180);

    }

    public static ConnectionManager getInstance() throws IOException, SQLException, PropertyVetoException {
        if (datasource == null) {
            datasource = new ConnectionManager();
            return datasource;
        } else {
            return datasource;
        }
    }

    public Connection getConnection() throws SQLException {
        return this.ds.getConnection();
    }

}
