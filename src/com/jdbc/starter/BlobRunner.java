package com.jdbc.starter;

import com.jdbc.starter.util.ConnectionManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;

public class BlobRunner {
    public static void main(String[] args) throws SQLException, IOException {
        //blob - bytea in postgres
        //clob - TEXT in postgres

//        saveImg();
        getImage();
    }

    private static void saveImg() throws SQLException, IOException {
        var sql = """
               UPDATE aircraft
               SET image = ?
               WHERE id = 1
               """;
        try(var connection = ConnectionManager.get();
            var preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setBytes(1, Files.readAllBytes(Path.of("resources", "boing.jpeg")));
            preparedStatement.executeUpdate();
        }
    }

    private static void getImage() throws SQLException, IOException {
        var sql = """
               SELECT image
               FROM aircraft
               WHERE id = ?
               """;
        try(var connection = ConnectionManager.get();
            var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, 1);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                var image = resultSet.getBytes("image");
                Files.write(Path.of("resources", "boing_new.jpeg"), image, StandardOpenOption.CREATE);
            }
        }
    }

//    private static void saveImg() throws SQLException, IOException {
//       var sql = """
//               UPDATE aicraft
//               SET image = ?
//               WHERE id = 1
//               """;
//        try(var connection = ConnectionManager.get();
//            var preparedStatement = connection.prepareStatement(sql)) {
//            connection.setAutoCommit(false);
//            var blob = connection.createBlob();
//            blob.setBytes(1, Files.readAllBytes(Path.of("resources", "boing.jpeg")));
//
//            preparedStatement.setBlob(1, blob);
//            preparedStatement.executeUpdate();
//            connection.commit();
//        }
//    }
}
