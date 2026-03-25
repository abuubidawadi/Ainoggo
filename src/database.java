import java.sql.Connection;
import java.sql.DriverManager;

public class database {

    public static Connection connectDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            return DriverManager.getConnection(
                "jdbc:mysql://crossover.proxy.rlwy.net:27408/railway?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                "root",
                "IgcmcGlSPEswVxgRhrNKhMdsUguxcYUh"
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}