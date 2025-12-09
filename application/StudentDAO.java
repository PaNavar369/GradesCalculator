package application;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentDAO {

    // Check if student exists
    public static boolean exists(String studentId) {
        String sql = "SELECT 1 FROM students WHERE id=?";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // Insert a new student
    public static void insert(String id, String name, String imagePath) {
        String sql = "INSERT INTO students(id, name, image_path) VALUES(?,?,?)";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, name);
            ps.setString(3, imagePath);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Update student's profile photo
    public static void updateImage(String id, String imagePath) {
        String sql = "UPDATE students SET image_path=? WHERE id=?";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, imagePath);
            ps.setString(2, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Get student's profile photo path
    public static String getImagePath(String id) {
        String sql = "SELECT image_path FROM students WHERE id=?";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) return rs.getString("image_path");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // Copy the chosen profile image into project folder and return the saved path
    public static String copyProfileImage(File sourceFile, String studentId) throws Exception {
        File photosDir = new File("photos");
        if (!photosDir.exists()) photosDir.mkdirs();

        // Get file extension
        String extension = "";
        int dotIndex = sourceFile.getName().lastIndexOf('.');
        if (dotIndex > 0) {
            extension = sourceFile.getName().substring(dotIndex);
        }

        // Destination file: photos/studentId.ext
        File destFile = new File(photosDir, studentId + extension);

        // Copy file
        java.nio.file.Files.copy(sourceFile.toPath(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        return destFile.getAbsolutePath();
    }
}
