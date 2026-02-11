import java.sql.Connection;
import java.sql.DriverManager;

public class database {

    public static Connection connectDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/ainoggo?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                "root",
                "PaSsforAiNogGo"
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
