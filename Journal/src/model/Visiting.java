package model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Visiting {

	private boolean presence;
	private int mark;
	private LocalDate date;
	
	
	/**
	 * 
	 * @param presence
	 * @param mark
	 * @param date - String date in format "2016-12-31" ("yyyy-mm-dd")
	 */
	public Visiting(boolean presence, int mark, String date) {
		super();
		this.presence = presence;
		this.mark = mark;
		this.date = LocalDate.parse(date);

	}
	
	public Visiting(String strpresence, String strmark, String strdate) {
		
		super();
		this.presence = (strpresence == "1");
		this.mark = Integer.parseInt(strmark);
		this.date = LocalDate.parse(strdate);
	}

	public boolean isPresence() {
		return presence;
	}
	public void setPresence(boolean presence) {
		this.presence = presence;
	}
	public int getMark() {
		return mark;
	}
	public void setMark(int mark) {
		this.mark = mark;
	}
	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}


	public static List<Visiting> getVisitings(String group, String date)
	{
		List<Visiting> list = new LinkedList<Visiting>();

		String querry = "SELECT presence, mark, date FROM Visitings WHERE "
				+ "studentid = (SELECT s.studentid FROM Students AS s, Groups AS g WHERE "
				+ "g.groupid = s.groupid AND g.name = '" + group
				+ "' AND groupid = (SELECT groupid FROM Groups WHERE name = '" + group
				+ "') AND date = '" + date + "';";

		DB db = DB.conn();
		List<String> strList = db.select(querry);

		for(String str : strList)
		{
			String[] split = str.split("\n");
			list.add(new Visiting(split[0], split[1], split[2]));
		}
		
		return list;
	}
	
	public static ObservableList<List<String>> getAllVisitings (String groupName, String dateFrom, String dateTo)
	{
		ObservableList<List<String>> mainList = FXCollections.observableArrayList();
		//TODO quit problem with missed Visiting data of student
		String querry = "SELECT s.name, v.date, v.mark, v.presence FROM Students AS s "
				+ "INNER JOIN Groups AS g ON s.groupId = g.groupId "
				+ "LEFT JOIN Visitings AS v ON s.studentId = v.studentId WHERE g.name = '"
				+ groupName	+ "' AND ((v.date > '" + dateFrom + "' AND v.date < '" + dateTo
				+ "') OR v.date IS NULL) ORDER BY s.name, v.date;";

		DB db = DB.conn();
		List<String> strList = db.select(querry);

		String studentName = "";
		List<String> list = new LinkedList<String>();
		for(String str : strList)
		{
			String[] split = str.split("\n");
			if(studentName.equals(split[0]))
			{
				list.add(split[1]);
				list.add(split[2]);
				list.add(split[3]);
			}
			else if(studentName.equals(""))
			{
				studentName = split[0];
				list.add("0");
				list.add(split[0]);
				list.add(split[1]);
				list.add(split[2]);
				list.add(split[3]);
			}
			else
			{
				mainList.add(list);

				list = new LinkedList<>();
				studentName = split[0];
				list.add("0");
				list.add(split[0]);
				list.add(split[1]);
				list.add(split[2]);
				list.add(split[3]);
			}
		}
		mainList.add(list);
		
		int maxSize = 2;
		for(List<String> l : mainList)
		{
			if(l.size() > maxSize)
			{
				maxSize = l.size();
			}
		}
		for(int i = 0; i < maxSize; i++)
		{
			for(List<String> l : mainList)
			{
				while(l.size() < maxSize)
				{
					l.add("null");
				}
			}
		}
		
		return mainList;
	}
	
	/**
	 * Delete data about visitings from BD of chosen day of chosen group
	 * @param groupName name of Group, which to delete
	 * @param day which data about to delete
	 */
	public static void deleteDay(String groupName, LocalDate day)
	{
		String querry = "DELETE FROM Visitings WHERE date = '"
				+ day.toString() + "' AND groupid = (SELECT groupid FROM Groups WHERE name = '"
				+ groupName + "');";

		DB db = DB.conn();
		db.executeUpdate(querry);
	}
	
	/**
	 * Write info about visiting in DB
	 * @param groupName - name of group
	 * @param day - day of visiting to write
	 * @param studentName - name of student
	 * @param mark - mark, what student got
	 * @param presence - was student presence
	 */
	public static void writeVisiting(String groupName, LocalDate date, String studentName,
			int mark, boolean presence)
	{
		
		
		String querry = "INSERT INTO Visitings (groupid, studentid, mark, presence, date) VALUES ("
				+ "(SELECT groupid FROM Groups WHERE name = '" + groupName + "'), "
				+ "(SELECT studentid FROM Students WHERE name = '" + studentName + "'), "
				+ (mark == 0 ? "null" : mark) + ", " + (presence? 1 : 0)
				+ ", '" + date.toString() + "');";
		DB db = DB.conn();
		db.executeUpdate(querry);
	}
}
