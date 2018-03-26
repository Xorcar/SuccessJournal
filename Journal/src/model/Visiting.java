package model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Visiting
{
    private boolean presence;
    private int mark;
    private LocalDate date;

    /**
     * 
     * @param presence
     * @param mark
     * @param date
     *            - String date in format "2016-12-31" ("yyyy-mm-dd")
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
	if (strmark.equals("null"))
	{
	    strmark = "0";
	}
	this.mark = Integer.parseInt(strmark);
	this.date = LocalDate.parse(strdate);
    }

    public boolean isPresence()
    {
	return presence;
    }

    public void setPresence(boolean presence)
    {
	this.presence = presence;
    }

    public int getMark()
    {
	return mark;
    }

    public void setMark(int mark)
    {
	this.mark = mark;
    }

    public LocalDate getDate()
    {
	return date;
    }

    public void setDate(LocalDate date)
    {
	this.date = date;
    }

    public static List<Visiting> getVisitings(String group, String date)
    {
	List<Visiting> list = new LinkedList<Visiting>();

	String querry = "SELECT presence, mark, date FROM Visitings WHERE "
		+ "studentid IN (SELECT s.studentid FROM Students AS s, Groups AS g WHERE "
		+ "g.groupid = s.groupid AND g.name = '" + group
		+ "' AND groupid = (SELECT groupid FROM Groups WHERE name = '" + group + "') AND date = '" + date
		+ "';";

	DB db = DB.conn();
	List<String> strList = db.select(querry);

	for (String str : strList)
	{
	    String[] split = str.split("\n");
	    list.add(new Visiting(split[0], split[1], split[2]));
	}

	return list;
    }

    public static ObservableList<List<String>> getAllVisitings(String groupName, String dateFrom, String dateTo)
    {
	ObservableList<List<String>> mainList = FXCollections.observableArrayList();

	List<Student> listStudents = Student.getStudents(groupName);
	for (int i = 0; i < listStudents.size(); i++)
	{
	    List<String> l = new LinkedList<>();
	    l.add("" + (i + 1));
	    l.add(listStudents.get(i).getName());
	    mainList.add(l);
	}

	String querry = "SELECT s.name, v.date, v.mark, v.presence " + "FROM Visitings AS v INNER JOIN Groups AS g "
		+ "ON v.groupId = g.groupId LEFT JOIN Students AS s " + "ON s.studentId = v.studentId WHERE g.name = '"
		+ groupName + "' AND ((v.date >= '" + dateFrom + "' AND v.date <= '" + dateTo
		+ "') OR v.date IS NULL) ORDER BY s.name, v.date;";

	DB db = DB.conn();
	List<String> strList = db.select(querry);

	String curDate = "";
	for (String str : strList)
	{
	    String[] split = str.split("\n");
	    if (curDate.equals(split[1]) || curDate.equals(""))
	    {
		addInfoInStatisticList(mainList, split[0], split[1], split[2], split[3]);

	    } else
	    {
		finishInfoInStatisticList(mainList);
		addInfoInStatisticList(mainList, split[0], split[1], split[2], split[3]);
		curDate = split[1];
	    }
	}
	finishInfoInStatisticList(mainList);

	String dates = dateFrom.toString() + " " + dateTo.toString();
	for (List<String> l : mainList)
	{
	    List<String> stat = Student.getPresenceStatistic(l.get(1), groupName, groupName, dates);
	    l.add(stat.get(0));
	    l.add(stat.get(1) + "/" + stat.get(2));
	}

	return mainList;
    }

    public static List<Visiting> getVisitingsOfStudent(String groupName, String studentName, String dateFrom,
	    String dateTo)
    {
	List<Visiting> visits = new LinkedList<>();
	String querry = "SELECT v.presence, v.mark, v.date " + "FROM Visitings AS v INNER JOIN Groups AS g "
		+ "ON v.groupId = g.groupId INNER JOIN Students AS s " + "ON s.studentId = v.studentId AND s.name = '"
		+ studentName + "' WHERE g.name = '" + groupName + "' AND (v.date >= '" + dateFrom + "' AND v.date <= '"
		+ dateTo + "') ORDER BY s.name, v.date;";

	DB db = DB.conn();
	List<String> strList = db.select(querry);

	for (String str : strList)
	{

	    String[] split = str.split("\n");
	    visits.add(new Visiting(split[0], split[1], split[2]));
	}

	return visits;
    }

    /**
     * Method for getting list of days, recorded in DB of some group. Like, in what
     * days group has visits in DB.
     * 
     * @param groupName
     *            - name of group
     * @return string list of dates of group, like {"2018-03-10", "2018-03-30"}
     */
    public static List<String> getDays(String groupName)
    {
	List<String> list = new LinkedList<>();

	String querry = "SELECT s.name, v.date, v.mark, v.presence FROM Students AS s "
		+ "INNER JOIN Groups AS g ON s.groupId = g.groupId "
		+ "LEFT JOIN Visitings AS v ON s.studentId = v.studentId WHERE g.name = '" + groupName
		+ "' ORDER BY v.date;";

	DB db = DB.conn();
	List<String> dbList = db.select(querry);

	for (String str : dbList)
	{
	    String strDate = str.split("\n")[0];
	    list.add(strDate);
	}

	return list;
    }

    /**
     * Delete data about visits from BD of chosen day of chosen group
     * 
     * @param groupName
     *            name of Group, which to delete
     * @param day
     *            which data about to delete
     */
    public static void deleteDay(String groupName, LocalDate day)
    {
	String querry = "DELETE FROM Visitings WHERE date = '" + day.toString()
		+ "' AND groupid IN (SELECT groupid FROM Groups WHERE name = '" + groupName + "');";

	DB db = DB.conn();
	db.executeUpdate(querry);
    }

    /**
     * Write info about visiting in DB
     * 
     * @param groupName
     *            - name of group
     * @param day
     *            - day of visiting to write
     * @param studentName
     *            - name of student
     * @param mark
     *            - mark, what student got
     * @param presence
     *            - was student presence
     */
    public static void writeVisiting(String groupName, LocalDate date, String studentName, int mark, boolean presence)
    {

	String querry = "INSERT INTO Visitings (groupid, studentid, mark, presence, date) VALUES ("
		+ "(SELECT groupid FROM Groups WHERE name = '" + groupName + "'), "
		+ "(SELECT studentid FROM Students WHERE name = '" + studentName
		+ "' AND groupid IN (SELECT groupid FROM Groups WHERE name = '" + groupName + "')), "
		+ (mark == 0 ? "null" : mark) + ", " + (presence ? 1 : 0) + ", '" + date.toString() + "');";
	DB db = DB.conn();
	db.executeUpdate(querry);
    }

    private static void addInfoInStatisticList(ObservableList<List<String>> mainList, String name, String date,
	    String mark, String presence)
    {
	for (List<String> l : mainList)
	{
	    if (l.get(1).equals(name))
	    {
		l.add(date);
		l.add(mark);
		l.add(presence);
	    }
	}
    }

    private static void finishInfoInStatisticList(ObservableList<List<String>> mainList)
    {
	int maxSize = 2;
	List<String> maxList = new LinkedList<>();
	for (List<String> l : mainList)
	{
	    if (l.size() > maxSize)
	    {
		maxSize = l.size();
		maxList = l;
	    }
	}
	for (int i = 0; i < maxSize; i++)
	{
	    for (List<String> l : mainList)
	    {
		while (l.size() < maxSize)
		{
		    l.add(maxList.get(l.size() - 1));
		    l.add("null");
		    l.add("null");
		}
	    }
	}
    }
}
