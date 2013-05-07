package cs.dcd.exportdict;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ExportDictionary {
	
	public ExportDictionary()
	{
		
	}
	
	public String readDirectoryPath(String tips) throws IOException
	{
		System.out.print(tips);
		InputStreamReader reader = new InputStreamReader(System.in);
		
		return new BufferedReader(reader).readLine();
	}
	
	public void generateDictionary(String table, String column, String outputPath)
	{
		String connectURI = "jdbc:sqlserver://localhost:1433;databaseName=EngineeringMedicine";
		String DBAdmin = "sa";
		String DBPassword = "wyq19900312";
		String sql = "select distinct " + column + " from " + table;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		PrintWriter fileOutput = null;
		
		try {
			DriverManager.registerDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver());
			connect = DriverManager.getConnection(connectURI,DBAdmin,DBPassword);
			stmt = connect.createStatement();
			rs = stmt.executeQuery(sql);
			
			fileOutput = new PrintWriter(new FileWriter(outputPath + "\\" + table + ".dic"));
			
			while (rs.next())
			{
				String temp = new String();
				temp = rs.getString(column);
				fileOutput.println(temp);
			}
			
			connect.close();
			fileOutput.close();
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
		}		
	}
	
}
