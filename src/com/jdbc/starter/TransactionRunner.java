package com.jdbc.starter;

import com.jdbc.starter.util.ConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TransactionRunner {
    public static void main(String[] args) throws SQLException {
        long flightId = 8;
        var deleteFlightSQL = "DELETE FROM flight WHERE id = " + flightId;
        var deleteTicketSQL = "DELETE FROM ticket WHERE flight_id = " + flightId;

        Connection connection = null;
        Statement statement = null;
        try {
            connection = ConnectionManager.get();
            connection.setAutoCommit(false);

            statement = connection.createStatement();
            statement.addBatch(deleteTicketSQL);
            statement.addBatch(deleteFlightSQL);

            var ints = statement.executeBatch();

            connection.commit();
        }catch (Exception e) {
            if(connection != null){
                connection.rollback();
            }
            throw e;
        }finally {
            if(connection != null) {
                connection.close();
            }
            if(statement != null) {
                statement.close();
            }
        }
    }
}
