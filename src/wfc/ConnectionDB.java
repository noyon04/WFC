/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package wfc;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;




/**
 *
 * @author Hamiduzzaman Noyon
 */
public class ConnectionDB {
    public Connection getConnection() {
        try{
            Class.forName("org.h2.Driver");
            Connection cn = DriverManager.getConnection("jdbc:h2:./database/wfcdb;DB_CLOSE_DELAY=-1", "admin", "");
            //System.out.println("connected");
            return cn;
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null, "Datgabase Connection error"+ex);
            return null;
        }
    }
}
