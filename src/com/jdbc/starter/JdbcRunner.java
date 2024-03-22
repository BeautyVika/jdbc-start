package com.jdbc.starter;

import com.jdbc.starter.util.ConnectionManager;

import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JdbcRunner {

    public static void main(String[] args) throws SQLException {
//        Long flightId = 2L;
//        var result = getTicketsByFlightId(flightId);
//        System.out.println(result);
//        var result = getFlightsBetween(LocalDate.of(2020, 1, 1).atStartOfDay(), LocalDateTime.now());
//        System.out.println(result);
        try{
            checkMetaData();
        }finally {
            ConnectionManager.closePool();
        }
    }

    public static void checkMetaData() throws SQLException {
        try(var connection = ConnectionManager.get()){
            var metadata = connection.getMetaData();
            var catalogs = metadata.getCatalogs();
            while(catalogs.next()){
               var catalog = catalogs.getString(1);

                var schemas = metadata.getSchemas(); //return ResultSet
                while (schemas.next()){
                    var schema = schemas.getString("TABLE_SCHEM");
                    var tables = metadata.getTables(catalog, schema, "%", null);
                    if(schema.equals("public")){
                        while (tables.next()){
                            System.out.println(tables.getString("TABLE_NAME"));
                        }
                    }
                }
            }
        }
    }

    private  static List<Long> getFlightsBetween(LocalDateTime start, LocalDateTime end) throws SQLException {
        String sql = """
               SELECT id
               FROM flight
               WHERE departure_date BETWEEN ? AND ?
                """;
        List<Long> result = new ArrayList<>();

        try(var connection = ConnectionManager.get();
            var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setFetchSize(50);
            preparedStatement.setQueryTimeout(10);
            preparedStatement.setMaxRows(100);

            preparedStatement.setTimestamp(1, Timestamp.valueOf(start));
            preparedStatement.setTimestamp(2, Timestamp.valueOf(end));

            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result.add(resultSet.getObject("id", Long.class));
            }
        }
        return result;
    }

    private static List<Long> getTicketsByFlightId(Long flightId) throws SQLException {
        String sql = """
                SELECT id
                FROM ticket
                WHERE flight_id = ?
                """;
        List<Long> result = new ArrayList<>();

        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, flightId);

            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
//                result.add(resultSet.getLong("id"));
                result.add(resultSet.getObject("id", Long.class));
            }
        }

        return result;
    }
}




//public class JdbcRunner {
//
//    public static void main(String[] args) throws SQLException {
//        Class<Driver> driverClass = Driver.class;
//        String sql = """
//                SELECT *
//                FROM ticket
//                """;
//        try (var connection = ConnectionManager.get();
//             var statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
//            System.out.println(connection.getSchema());
//            System.out.println(connection.getTransactionIsolation());
//
//            var executeResult = statement.executeQuery(sql);
//            while (executeResult.next()) {
//                System.out.println(executeResult.getLong("id"));
//                System.out.println(executeResult.getString("passenger_no"));
//                System.out.println(executeResult.getBigDecimal("cost"));
//                System.out.println("-----------");
//            }
//        }
//    }
//}

