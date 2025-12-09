import java.sql.Connection;
import java.sql.DriverManager;
public class TestSqlite {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			Connection conn= DriverManager.getConnection("jdbc:sqlite:test.db");
			System.out.println("connected");
			conn.close();
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}

	}

}
