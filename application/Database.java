package application;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
	private static final String DB_URL = "jdbc:sqlite:test.db";

	public static void init() throws SQLException{
		try(Connection conn= connect(); Statement stmt = conn.createStatement()){
			String createStudents = """
	                CREATE TABLE IF NOT EXISTS students (
	                    id TEXT PRIMARY KEY,
	                    name TEXT,
	                    image_path TEXT
	                );
	                """;
	            stmt.execute(createStudents);

	            // grades table
	            String createGrades = """
	                CREATE TABLE IF NOT EXISTS grades (
	                    id INTEGER PRIMARY KEY AUTOINCREMENT,
	                    student_id TEXT,
	                    details TEXT,
	                    FOREIGN KEY(student_id) REFERENCES students(id)
	                );
	                """;
	            stmt.execute(createGrades);

	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
			
		
		// TODO Auto-generated method stub

	

	public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

}
