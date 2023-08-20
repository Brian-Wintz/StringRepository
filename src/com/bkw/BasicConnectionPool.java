package com.bkw;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.List;
import java.util.ArrayList;

import com.bkw.translatedstring.TranslatedString;

public class BasicConnectionPool implements IConnectionPool {

    private String url;
    private String user;
    private String password;
    private List<Connection> connectionPool;
    private List<Connection> usedConnections = new ArrayList<>();
    private static int INITIAL_POOL_SIZE = 10;

    public static BasicConnectionPool create(String url, String user,String password) throws SQLException {

        // Build up an initial pool of INITIAL_POOL_SIZE connections
        List<Connection> pool = new ArrayList<>(INITIAL_POOL_SIZE);
        for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
            pool.add(createConnection(url, user, password));
        }
        return new BasicConnectionPool(url, user, password, pool);
    }

    // Create the connection pool (private internal only)
    private BasicConnectionPool(String url, String user, String password, List<Connection> pool) {
        connectionPool=pool;
        this.url=url;
        this.user=user;
        this.password=password;
    }
    // Retrieve a connection from the pool, removing it from the connectionPool collection and adding it to the usedConnections collection

    @Override
    public Connection getConnection() {
        Connection connection = connectionPool.remove(connectionPool.size() - 1);
        usedConnections.add(connection);
        return connection;
    }

    // Release a connection back to the pool, adding it back to the connectionPool collection and removing it from the usedConnections collection
    @Override
    public boolean releaseConnection(Connection connection) {
        connectionPool.add(connection);
        return usedConnections.remove(connection);
    }

    // Create a new connection
    private static Connection createConnection(String url, String user, String password) throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    // Returns the size of total connections (available + used)
    public int getSize() {
        return connectionPool.size() + usedConnections.size();
    }

    // Retrieve the URL for accessing the DB
    public String getUrl() {
        return this.url;
    }

    // Retrieve the user name used to authenticate access to the DB
    public String getUser() {
        return this.user;
    }

    // Retrieve the password used to authenticate access to the DB
    public String getPassword() {
        return this.password;
    }

    // Sample test of using this pool to retrieve data into Employee java object
    public static void main(String[] args) {
        BasicConnectionPool bcp=null;
        Connection conn=null;
        try {
            bcp=BasicConnectionPool.create("jdbc:mariadb://localhost:3306/sampledb","root","admin");
            conn=bcp.getConnection();
            Statement stmt = conn.createStatement();

            // Retrieve employee records into Employee instances
            TranslatedString ts=new TranslatedString();
            ResultSet rs = stmt.executeQuery("select * from "+ts.getTableName());
            while(rs.next()){
                TranslatedString ts2=new TranslatedString();
                for(TranslatedString.Field field: TranslatedString.Field.values()) {
                    String fieldName=field.getFieldName();
                    System.out.println(fieldName + ": " + rs.getString(fieldName));
                    if(field.getDataType()==GenericBean.DataType.STRING)
                        ts2.put(field,rs.getString(fieldName));
                    else {
                        if(field.getDataType()==GenericBean.DataType.DATE)
                            ts2.put(field,java.sql.Date.valueOf(rs.getString(fieldName)));
                        if(field.getDataType()==GenericBean.DataType.INTEGER)
                            ts2.put(field,Integer.valueOf(rs.getString(fieldName)));
                    }
                }
            }

            // Create a new string code
            ts=new TranslatedString();
            String insert="insert into "+ts.getTableName();
            ts.setStringCode("HELLO");
            ts.setLanguageCode("en");
            ts.setCountryCode("US");
            String fields="";
            String values="";
            for(TranslatedString.Field field: TranslatedString.Field.values()) {
                if(ts.containsKey(field)) {
                    fields+=(fields.length()>0?",":"")+field.getFieldName();
                    String value;
                    if(field.getDataType()==GenericBean.DataType.STRING)
                        value="'"+ts.get(field)+"'";
                    else
                        value=ts.get(field).toString();
                    values+=(values.length()>0?",":"")+value;
                }
            }
            insert+="("+fields+") values("+values+")";
            System.out.println("##insert:"+insert);
            stmt.executeUpdate(insert);

            // Update last created record
            String update="update "+ts.getTableName()+" set "+TranslatedString.Field.TICKET.getFieldName()+"=? where "+
                TranslatedString.Field.STRINGCODE.getFieldName()+"=? and "+
                TranslatedString.Field.LANGUAGECODE.getFieldName()+"=? and "+
                TranslatedString.Field.COUNTRYCODE.getFieldName()+"=?";
            PreparedStatement ps1=conn.prepareStatement(update);
            ps1.setString(1,"CME-200");
            ps1.setString(2,"HELLO");
            ps1.setString(3,"en");
            ps1.setString(4,"US");
            ps1.executeUpdate();

            // Delete last created record
            PreparedStatement ps=conn.prepareStatement("delete from "+ts.getTableName()+" where " +
                TranslatedString.Field.STRINGCODE.getFieldName()+"=? and "+
                TranslatedString.Field.LANGUAGECODE.getFieldName()+"=? and "+
                TranslatedString.Field.COUNTRYCODE.getFieldName()+"=?");

            ps.setString(1,"HELLO");
            ps.setString(2,"en");
            ps.setString(3,"US");
            ps.executeUpdate();
        } catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            if(bcp!=null && conn!=null) bcp.releaseConnection(conn);
        }
    }
}