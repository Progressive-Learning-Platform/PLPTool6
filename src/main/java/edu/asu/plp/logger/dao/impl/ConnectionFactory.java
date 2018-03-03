package edu.asu.plp.logger.dao.impl;
import java.io.File;
import java.sql.*;
import edu.asu.plp.logger.model.DatabaseConnection;
import org.apache.commons.dbcp2.BasicDataSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/***
 * This is a singleton class which reads the logger_connection.xml file and
 * gets the connection parameters to connect to the log table.
 */
public class ConnectionFactory {
    private static BasicDataSource dataSource;
    private static DatabaseConnection dConn = new DatabaseConnection();
    private ConnectionFactory() {

    }

    /***
     * This function get called from the logger_connection.xml
     * @return DataConnection object which has URL, DriverClassName, Username and Password
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            dConn = readXML();
            dataSource = new BasicDataSource();
            dataSource.setUrl(dConn.getDBUrl());
            dataSource.setDriverClassName(dConn.getDBDriverClassName());
            dataSource.setUsername(dConn.getDBUsername());
            dataSource.setPassword(dConn.getDBPassword());
        }
        return dataSource.getConnection();
    }

    /***
     * This function reads the logger_connection.xml file and return the DatabaseConnection object.
     * @return DatabaseConnection object
     */
    public static DatabaseConnection readXML(){
        try {
            File fXmlFile = new File("src/main/resources/database/logger_connection.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("Connection");
            String Url, DBDriver, dbUsername, dbPassword;
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    Url = eElement.getElementsByTagName("url").
                            item(0).getTextContent();
                    DBDriver = eElement.getElementsByTagName("driver").
                            item(0).getTextContent();
                    dbUsername = eElement.getElementsByTagName("username").
                            item(0).getTextContent();
                    dbPassword = eElement.getElementsByTagName("password").
                            item(0).getTextContent();
                    if(!Url.isEmpty()) {
                        dConn.setDBUrl(eElement.getElementsByTagName("url").
                                item(0).getTextContent());
                    } else{
                        throw new Exception("Url attribute value is not configured in the XML file.");
                    }
                    if(!DBDriver.isEmpty()) {
                        dConn.setDBDriverClassName(eElement.getElementsByTagName("driver").
                                item(0).getTextContent());
                    } else{
                        throw new Exception("Driver attribute value is not configured in the XML file.");
                    }
                    if(!dbUsername.isEmpty()) {
                        dConn.setDBUsername(eElement.getElementsByTagName("username").
                                item(0).getTextContent());
                    } else{
                        throw new Exception("Username attribute value is not configured in the XML file.");
                    }
                    if(!dbPassword.isEmpty()) {
                        dConn.setDBPassword(eElement.getElementsByTagName("password").
                                item(0).getTextContent());
                    }else{
                        throw new Exception("Password attribute value is not configured in the XML file.");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        return dConn;
    }
}