package jdbc;

import jdbc.util.ConnectionManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BlobClobRunner {
    public static void main(String[] args) throws SQLException, IOException {
        //blob - binary large object (image, word, excel, video, etc
        //clob - character large object
        //saveImage();
        getImage();
    }

   private static void saveImage() throws SQLException, IOException {

        String insertImageSQL = """
                UPDATE aircraft
                SET image = ?
                WHERE id = 3
                """;

        try (Connection connection = ConnectionManager.openConncetion();
             PreparedStatement preparedStatement = connection.prepareStatement(insertImageSQL)) {

            preparedStatement.setBytes(1, Files.readAllBytes(Path.of(ClassLoader.getSystemResource("boeing.jpg").getPath())));
            preparedStatement.executeUpdate();
        }
    }

    private static void getImage() throws SQLException, IOException {
        String getImageSQL = """
                SELECT image
                FROM aircraft
                WHERE id = ?
                """;

        try (Connection connection = ConnectionManager.openConncetion();
            PreparedStatement preparedStatement = connection.prepareStatement(getImageSQL)) {

            preparedStatement.setInt(1, 1);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                byte[] imageByte = resultSet.getBytes("image");
                Files.write(Path.of("new.jpg"), imageByte, StandardOpenOption.CREATE_NEW);
            }
        }
    }
}
