
import java.sql.Connection;
import java.sql.DriverManager;

public class database {

    public static Connection connectDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            return DriverManager.getConnection(
                "jdbc:mysql://shortline.proxy.rlwy.net:29795/railway?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                "root",
                "BXKpsykeNGzLyiyYxNyqEQGwOtgLACeW"
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}