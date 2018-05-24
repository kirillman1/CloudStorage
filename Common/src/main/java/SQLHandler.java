import java.sql.*;

public class SQLHandler {
    //private static final String driverName = "com.mysql.cj.jdbc.Driver";

    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement preparedStatement;

    public static void connect() throws SQLException{
            //Class.forName(driverName);
            connection = DriverManager.getConnection(Configuration.DB_HOST, Configuration.DB_LOGIN, Configuration.DB_PASSWORD);
            statement = connection.createStatement();
    }

    public static void createTable() throws SQLException{
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTO_INCREMENT, first_name VARCHAR(50), last_name VARCHAR(50), " +
                "email VARCHAR(50) UNIQUE NOT NULL , password INTEGER NOT NULL );");
    }

    public static void addNewUser (String firstName, String lastName, String email, String password) throws SQLException{
        createTable();
        int passwordHash = password.hashCode();
        String inputSQL = "INSERT INTO users (first_name, last_name, email, password) VALUES (?, ?, ?, ?);";
        preparedStatement = connection.prepareStatement(inputSQL);
        preparedStatement.setString(1,firstName);
        preparedStatement.setString(2,lastName);
        preparedStatement.setString(3,email);
        preparedStatement.setInt(4,passwordHash);
        preparedStatement.executeUpdate();
    }

    public static boolean checkUsername(String email) throws SQLException {
        connect();
        String findUserSQL = "SELECT email FROM users WHERE email = ?;";
        preparedStatement = connection.prepareStatement(findUserSQL);
        preparedStatement.setString(1,email);
        ResultSet rsFindUser = preparedStatement.executeQuery();
        return rsFindUser.next();
    }

    public static boolean checkPassword(String email, String password) throws SQLException {
        int passwordHash = password.hashCode();
        String checkPasswordSQL = "SELECT email, password FROM users WHERE email = ? AND password = ?;";
        preparedStatement = connection.prepareStatement(checkPasswordSQL);
        preparedStatement.setString(1,email);
        preparedStatement.setInt(2,passwordHash);
        ResultSet rsCheckPassword = preparedStatement.executeQuery();
        return rsCheckPassword.next();
    }


    public static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
