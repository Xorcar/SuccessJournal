package model;

import java.time.LocalDate;
import java.util.List;

import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TableDat {
	
	private int num;
	private String studentName;
	private Visiting visit;
	
	List<StringProperty> list = null;
	
	public TableDat(int num, String studentName, Visiting visit) {
		super();
		this.num = num;
		this.studentName = studentName;
		this.visit = visit;
	}
	
	public TableDat(String studentName, int mark, boolean presence, LocalDate date) {
		super();
		this.num = 0;
		this.studentName = studentName;
		this.visit = new Visiting(presence, mark, date.toString());
	}
	
 	public String getNum() {
		return "" + num + 1;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public Visiting getVisit() {
		return visit;
	}

	public void setVisit(Visiting visit) {
		this.visit = visit;
	}
	
	public String getMark()
	{
		return "" + visit.getMark();
	}
	
	public void setMark(String mark)
	{
		this.visit.setMark(Integer.parseInt(mark));
	}
	
	public boolean isPresence()
	{
		return visit.isPresence();
	}
	
	public void setPresence(boolean presence)
	{
		visit.setPresence(presence);
	}

	public String getName()
	{
		return studentName;
	}
	
	public static ObservableList<TableDat> getList(String groupName, LocalDate date)
	{
		ObservableList<TableDat> list = FXCollections.observableArrayList();
		
		String querry = "SELECT s.name, v.mark, v.presence "
				+ "FROM Students as s INNER JOIN Groups as g "
				+ "ON g.groupid = s.groupid "
				+ "AND g.name = '" + groupName + "' "
				+ "LEFT JOIN Visitings as v ON v.studentid = s.studentid "
				+ "AND v.groupid = g.groupid "
				+ "AND v.date = '" + date.toString() + "';";

		DB db = DB.conn();
		List<String> strList = db.select(querry);
		String[] split;
		int mark;
		boolean presence;
		for(String str : strList)
		{
			split = str.split("\n");
			if(split[1].equals("null")) mark = 0;
			else mark = Integer.parseInt(split[1]);
			
			if(split[2].equals("null")) presence = false;
			else presence = (split[2].equals("1") ? true : false);
			
			
			list.add(new TableDat(split[0], mark, presence, date));
		}
		
		return list;
	}
}
