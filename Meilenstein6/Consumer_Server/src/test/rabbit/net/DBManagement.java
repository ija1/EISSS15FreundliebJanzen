package test.rabbit.net;



import java.sql.*;

public class DBManagement {
	/**
	 * Database Connection information
	 */
	private static final String DB_DRIVER 		= "com.mysql.jdbc.Driver";
    private static final String DB_URL 			= "jdbc:mysql://localhost:3306/autismsupport";
    private static final String DB_USER 		= "root";
    private static final String DB_PASSWORD 	= "janjan1";
	
    /**
     * Create the DB Connection
     * @throws Exception
     */
    @SuppressWarnings("finally")
    public static Connection createConnection() throws Exception {
    	Connection con =null;
        try {
            Class.forName(DBManagement.DB_DRIVER);
            con = DriverManager.getConnection(DBManagement.DB_URL, DBManagement.DB_USER, DBManagement.DB_PASSWORD);
        } catch (Exception e) {
            throw e;
        }
        finally {
            return con;
        }
    }
    /**
     * Search for employees who are in same meeting
     * 
     * @param name
     * @throws SQLException
     * @throws Exception
     */
    public static String[] selectEmployees(Termin termin) throws SQLException, Exception {
        boolean insertStatus = false;
        final String[] employees;
        Connection dbConn = null;
        String response = null;
        try {
            try {
                dbConn = DBManagement.createConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Statement stmt = dbConn.createStatement();
            String query = "SELECT vorname, nachname FROM mitarbeitertermine WHERE subject='"+ termin.getBetreff()+"' AND vorname !='"+termin.getAutist().getVorName()+"' AND nachname !='"+ termin.getAutist().getNachName() +"' AND startDate = '" + getTime(termin.getStartDatum()) +"' AND endDate = '" + getTime(termin.getEndDatum())+"'";
            // execute the query, and get a java resultset
            ResultSet rs = stmt.executeQuery(query);
             System.out.println("query: " + query);
            // iterate through the java resultset

             int rowcount = 0;
             if (rs.last()) {
               rowcount = rs.getRow();
               rs.beforeFirst(); 
             }

            employees = new String[rowcount];
            int i=0;
            while (rs.next())
            {
            	employees[i] = "overlap"+rs.getString("vorname")+rs.getString("nachname");
            	i++;
            }
        } catch (SQLException sqle) {
            throw sqle;
        } catch (Exception e) {
            if (dbConn != null) {
                dbConn.close();
            }
            throw e;
        } finally {
            if (dbConn != null) {
                dbConn.close();
            }
        }
        
		return employees;
    }
    
    public static Timestamp getTime(long i) {
		java.util.Date time = new java.util.Date((long)i);
	    Timestamp h= new Timestamp(time.getTime());
	    return h;
	}
}