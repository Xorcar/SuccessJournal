package model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Group
{

    private String name;
    private List<DayOfWeek> daysOfWeek;
    private LocalDate startDate;
    private LocalDate finishDate;
    private List<Student> students;

    public Group() {
	super();
	this.name = "";
	this.students = new LinkedList<Student>();
	this.startDate = LocalDate.parse("2018-01-01");
	;
	this.finishDate = LocalDate.parse("2018-01-02");
	this.daysOfWeek = new LinkedList<DayOfWeek>();
    }

    public Group(String name) {
	super();
	this.name = name;

	this.students = new LinkedList<Student>();
    }

    public Group(String name, List<Student> students) {
	super();
	this.name = name;
	this.students = students;
    }

    public Group(String name, String daysOfWeek, String startDate, String finishDate) {
	super();
	this.name = name;
	this.students = new LinkedList<Student>();
	this.startDate = LocalDate.parse(startDate);
	this.finishDate = LocalDate.parse(finishDate);
	this.daysOfWeek = parseInDaysOfWeek(daysOfWeek);
    }

    public Group(String name, List<DayOfWeek> daysOfWeek, LocalDate startDate, LocalDate finishDate) {
	super();
	this.name = name;
	this.students = new LinkedList<Student>();
	this.startDate = startDate;
	this.finishDate = finishDate;
	this.daysOfWeek = daysOfWeek;
    }

    public String getName()
    {
	return name;
    }

    public void setName(String name)
    {
	this.name = name;
    }

    public List<Student> getStudents()
    {
	return students;
    }

    public static List<Group> getGroups()
    {
	List<Group> list = new LinkedList<Group>();
	String querry = "SELECT name, daysOfWeek, startDate, finishDate FROM Groups";
	DB db = DB.conn();
	List<String> strList = db.select(querry);
	for (String str : strList)
	{
	    String[] split = str.split("\n");

	    Group newGroup = new Group(split[0], split[1], split[2], split[3]);

	    list.add(newGroup);
	}
	System.out.println(list.toString());
	return list;
    }

    public static Group getGroup(String groupName)
    {
	Group group = new Group();
	String querry = "SELECT name, daysOfWeek, startDate, finishDate FROM Groups WHERE " + "name = '" + groupName
		+ "'";
	;
	DB db = DB.conn();
	List<String> strList = db.select(querry);
	for (String str : strList)
	{
	    String[] split = str.split("\n");

	    group = new Group(split[0], split[1], split[2], split[3]);

	}
	System.out.println(group.toString());
	return group;
    }

    public List<DayOfWeek> getDaysOfWeek()
    {
	return daysOfWeek;
    }

    public void setDaysOfWeek(List<DayOfWeek> daysOfWeek)
    {
	this.daysOfWeek = daysOfWeek;
    }

    public LocalDate getStartDate()
    {
	return startDate;
    }

    public void setStartDate(LocalDate startDate)
    {
	this.startDate = startDate;
    }

    public LocalDate getFinishDate()
    {
	return finishDate;
    }

    public void setFinishDate(LocalDate finishDate)
    {
	this.finishDate = finishDate;
    }

    public void setStudents(List<Student> students)
    {
	this.students = students;
    }

    /**
     * Get names of group of some student
     * 
     * @param studentName
     *            - name of student
     * @param groupName
     *            - name of 1 of his group
     * @return list of names of his groups (search students with same name and
     *         birthdays in all groups)
     */
    public static List<String> getGroupNames(String studentName, String groupName)
    {
	List<String> groupNames = new LinkedList<String>();
	String querry = "SELECT g.name FROM Groups AS g "
		+ "INNER JOIN Students AS s ON g.groupId = s.groupId WHERE s.name = '" + studentName
		+ "' AND s.birthdate IN (SELECT s2.birthdate FROM Students as s2 "
		+ "INNER JOIN Groups as g2 ON g2.groupId = s2.groupId WHERE g2.name = '" + groupName + "');";

	DB db = DB.conn();
	List<String> strList = db.select(querry);
	for (String str : strList)
	{
	    groupNames.add(str.split("\n")[0]);
	}
	return groupNames;
    }

    public static List<String> getDates(String groupName)
    {
	List<String> dates = new LinkedList<String>();
	String querry = "SELECT v.date FROM Groups AS g "
		+ "INNER JOIN Visitings AS v ON g.groupId = v.groupId WHERE g.name = '" + groupName
		+ "' GROUP BY v.date;";

	DB db = DB.conn();
	List<String> strList = db.select(querry);
	for (String str : strList)
	{
	    dates.add(str.split("\n")[0]);
	}
	return dates;
    }

    /**
     * Method transform text representation of days of work into util.List of
     * time.DaysOfWeek
     * 
     * @param str
     *            - String, containing numbers of days of week, separeted by " ".
     *            Ex: "1 3 4 7".
     * @return list of DayOfWeek singletons. Ex: {MONDAY, WEDNESDAY, THURSDAY,
     *         SUNDAY}
     */
    public static List<DayOfWeek> parseInDaysOfWeek(String str)
    {
	List<String> splittedStr = new LinkedList<String>();
	splittedStr = Arrays.asList(str.split(" "));

	List<DayOfWeek> daysOfWeek = new LinkedList<DayOfWeek>();

	for (String s : splittedStr)
	{
	    if (s.matches("-?\\d+"))
	    { // any positive or negetive integer or not!
		daysOfWeek.add(DayOfWeek.of(Integer.parseInt(s)));
	    }

	}
	return daysOfWeek;
    }

    /**
     * Method transform util.List of time.DaysOfWeek into text representation of
     * days of work
     * 
     * @param list
     *            - of DayOfWeek singletons. Ex: {MONDAY, WEDNESDAY, THURSDAY,
     *            SUNDAY}
     * @return String, containing numbers of days of week, separeted by " ". Ex: "1
     *         3 4 7".
     */
    public static String parseFromDaysOfWeek(List<DayOfWeek> list)
    {
	String result = "";
	for (DayOfWeek dow : list)
	{

	    result += "" + dow.getValue() + " ";
	}
	return result;
    }

    public static Group createNewGroup(String name)
    {
	Group newGroup = new Group(name, "", LocalDate.now().toString(), LocalDate.now().toString());
	String querry = "INSERT INTO Groups (name, daysOfWeek, startDate, finishDate) VALUES ('" + name + "', '', '"
		+ newGroup.getStartDate().toString() + "', '" + newGroup.getFinishDate().toString() + "');";
	DB db = DB.conn();
	db.executeUpdate(querry);
	return newGroup;
    }

    public static Group changeGroup(String oldName, String newName, LocalDate startDate, LocalDate finishDate,
	    List<DayOfWeek> daysOfWeek)
    {
	Group newGroup = new Group(newName, daysOfWeek, startDate, finishDate);
	String querry = "UPDATE Groups SET " + "name = '" + newName + "', daysOfWeek = '"
		+ Group.parseFromDaysOfWeek(daysOfWeek) + "', startDate = '" + startDate.toString()
		+ "', finishDate = '" + finishDate.toString() + "' WHERE name = '" + oldName + "';";
	DB db = DB.conn();
	db.executeUpdate(querry);
	return newGroup;
    }

    public static void deleteGroup(String groupName)
    {
	String querry = "DELETE FROM Groups WHERE name = '" + groupName + "';";
	DB db = DB.conn();
	db.executeUpdate(querry);
    }

    public String toString()
    {
	return this.name;
    }
}
