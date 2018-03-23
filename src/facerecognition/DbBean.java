package facerecognition;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jacob Williams - 15008632
 * Contributors: n/a
 * Function: Handles database connection establishing and database functions.
 * Notes: N/A
 */
public class DbBean {
    Connection con;
    public cUser us = new cUser();
    

    public Connection getCon(){
        /* 
            Operation - Establishes database connection
            Inputs - n/a
            Outputs - database connection con.
        */
        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            con = DriverManager.getConnection(
                    "jdbc:derby://localhost:1527/faceRecognitionDB",
                    "jacob",
                    "root");
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DbBean.class.getName()).log(Level.SEVERE, null, 
                    ex);
        }
        return con;
    }
    
    
    public void setUser(String studentID){
        /*
            Operation - retrieves UID and IMAGECOUNT from FACEDB
            Inputs - String studentID
            Outputs - n/a
        */
        Connection con1 = getCon();
        try{
            String sql = "SELECT * FROM FACEDB WHERE \"UID\" = '" + studentID 
                    + "'";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            if(rs.next()){
                us.setID(rs.getString(1));
                us.setImageCount(rs.getInt(2));
            }
        }catch(Exception ex){
            System.out.println("ERROR IN SETUSER");
        }
    }
    
    public void updateImgCount(){
        /*
            Operation - Updates the IMAGECOUNT in FACEDB
            Inputs - n/a
            Outputs - n/a
        */
        try{
            String sql = "UPDATE FACEDB SET IMAGECOUNT = " + us.getImageCount() 
                    + " WHERE UID = '" + us.getID()+ "'";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.execute();
        }catch(Exception ex){
            System.out.println("ERROR IN UPDATEIMGCOUNT");
        }
    }
}
