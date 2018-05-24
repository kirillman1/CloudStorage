package kirillgontov.cloudstorage.client;

import java.io.FileInputStream;
import java.util.Properties;

public class Configuration {

    public static String SERVER_HOST;
    public static int SERVER_PORT;

    private static Properties properties = new Properties();

    static{
        try (FileInputStream fileInputStream = new FileInputStream("I:\\GitHub\\CloudStorage\\Client\\src\\main\\resources\\config.properties")) {
            properties.load(fileInputStream);
            SERVER_HOST = properties.getProperty("server.host");
            SERVER_PORT = Integer.parseInt(properties.getProperty("server.port"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
