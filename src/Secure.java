import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Secure { // this class will hide the secure data

    private final String url ="jdbc:mysql://localhost:3306/productsdb_alharbi"; //Database URL
    private final String user ="root"; // username
    private final String password ="123456"; //password

    private  final  String tableName ="productstbl_ammar"; // table name

    public Connection secureConnection() throws SQLException {
        return  DriverManager.getConnection(url,user,password);// return connection

    }

    public String getTableName() { //get table name
        return tableName;
    }
}
