import java.io.FileInputStream;
import java.util.Properties;

public class Configuration {

    public static int SERVER_PORT;
    public static String DB_HOST;
    public static String DB_LOGIN;
    public static String DB_PASSWORD;
    public static String PATH;

    private static Properties properties = new Properties();

    static{
        try (FileInputStream fileInputStream = new FileInputStream("config.properties")) {
            properties.load(fileInputStream);
            SERVER_PORT = Integer.parseInt(properties.getProperty("server.port"));
            DB_HOST = properties.getProperty("db.host");
            DB_LOGIN = properties.getProperty("db.login");
            DB_PASSWORD = properties.getProperty("db.password");
            PATH = properties.getProperty("path");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
