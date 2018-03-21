package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class DB {
	private static String dbLocation = "jdbc:sqlite:students.db";
	private static DB instance;
	private static Connection connection = null;

	public static DB conn()
	{
		try {
			if (connection == null)
			{
				connection = DriverManager.getConnection(dbLocation);
			}
			if (instance == null)
			{
				instance = new DB();
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return instance;
	}

	public void test()
	{
		//Connection connection = null;
		try
		{
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:sample.db");
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);  // set timeout to 30 sec.

			statement.executeUpdate("drop table if exists person");
			statement.executeUpdate("create table person (id integer, name string)");
			statement.executeUpdate("insert into person values(1, 'leo')");
			statement.executeUpdate("insert into person values(2, 'yui')");
			ResultSet rs = statement.executeQuery("select * from person");
			while(rs.next())
			{
				// read the result set
				System.out.println("name = " + rs.getString("name"));
				System.out.println("id = " + rs.getInt("id"));
			}
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
		}
		finally
		{
			try
			{
				if(connection != null)
					connection.close();
			}
			catch(SQLException e)
			{
				// connection close failed.
				System.err.println(e);
			}
		}
	}

	public List<String> getGroupNames()
	{
		//Connection connection = null;
		List<String> groupNames = new LinkedList<String>();
		try
		{
			// create a database connection
			connection = DriverManager.getConnection(dbLocation);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);  // set timeout to 30 sec.

			ResultSet rs = statement.executeQuery("SELECT name FROM Groups");
			while(rs.next())
			{
				groupNames.add(rs.getString("name"));
			}
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
		}
		finally
		{
			try
			{
				if(connection != null)
					connection.close();
			}
			catch(SQLException e)
			{
				// connection close failed.
				System.err.println(e);
			}
		}
		return groupNames;
	}

	public void executeUpdate(String querry)
	{
		System.out.println(querry);
		//Connection connection = null;
		try
		{
			// create a database connection
			connection = DriverManager.getConnection(dbLocation);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);  // set timeout to 30 sec.
			statement.executeUpdate(querry);
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
		}
		finally
		{
			try
			{
				if(connection != null)
					connection.close();
			}
			catch(SQLException e)
			{
				// connection close failed.
				System.err.println(e);
			}
		}
	}

	public  List<String> select(String querry)
	{
		System.out.println(querry);
		//Connection connection = null;
		List<String> list = new LinkedList<String>();
		try
		{
			connection = DriverManager.getConnection(dbLocation);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);  // set timeout to 30 sec.

			ResultSet rs = statement.executeQuery(querry);
			ResultSetMetaData rsmd = rs.getMetaData();

			int columnsNumber = rsmd.getColumnCount();
			String current = "";
			while(rs.next())
			{
				current = "";
				int i = 1;
				while(i <= columnsNumber) {
					current += rs.getString(i++) + "\n";
				}
				list.add(current);
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
		}
		finally
		{
			try
			{
				if(connection != null)
					connection.close();
			}
			catch(SQLException e)
			{
				System.err.println(e);
			}
		}
		
		System.out.println("data from SELECT:");
		for(String s : list)
		{
			System.out.println("\"" + s + "\"");
		}
		
		return list;
	}

}
