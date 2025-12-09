package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GradeDAO {

    public static void insertGrade(String studentId, String details) {
        String sql = "INSERT INTO grades(student_id, details) VALUES(?,?)";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, details);
            ps.executeUpdate();
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    public static String getLatestDetails(String studentId) {
        String sql = "SELECT details FROM grades WHERE student_id=? ORDER BY id DESC LIMIT 1";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) return rs.getString("details");
        } catch (SQLException ex) { ex.printStackTrace(); }
        return null;
    

    }
}
