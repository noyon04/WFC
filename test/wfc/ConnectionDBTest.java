/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package wfc;

import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Hamiduzzaman Noyon
 */
public class ConnectionDBTest {

    private ConnectionDB connectionDB;

    @BeforeEach
    public void setUp() {
        connectionDB = new ConnectionDB();
    }

    @AfterEach
    public void tearDown() {
        connectionDB = null;
    }

    /**
     * Test of getConnection method, of class ConnectionDB.
     */
    @Test
    public void testGetConnection() {
        System.out.println("getConnection");
        Connection result = connectionDB.getConnection();
        assertNotNull(result, "Connection should not be null");

        // Check if the connection is not closed
        try {
            assertFalse(result.isClosed(), "Connection should be open");
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }

        // Close the connection
        try {
            result.close();
        } catch (SQLException e) {
            fail("SQLException occurred when closing the connection: " + e.getMessage());
        }
    }
}
