package model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Student {
	
	private String name;
	private String birthday;
	private boolean isMale;

	
	/**
	 * 
	 * @param name of Student
	 * @param birthday of Student in "yyyy-MM-dd" format. Ex.: "2000-12-30".
	 * @param ismale true if male, false if female.
	 */
	public Student(String name, String birthday, boolean ismale) {
		this.name = name;
		this.birthday = birthday;
		this.isMale = ismale;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getBirthday() {
		return birthday;
	}

	/**
	 * @return true if student is male, false if female.
	 */
	public boolean isMale() {
		return isMale;
	}

	/**
	 * @param isMale true if student is male, false if female.
	 */
	public void setMale(boolean isMale) {
		this.isMale = isMale;
	}

	/**
	 * Get presence statistic of student
	 * @param studentName name of student
	 * @param curGroupName name of group
	 * @param dates dates to look between them like "2018-02-10 2018-02-15" or ""
	 * @return list of statistics {average marks, count of presences, count of absence}
	 */
	public static List<String> getPresenceStatistic(String studentName,
			String curGroupName, String mainGroupName, String dates)
	{
		List<String> list = new ArrayList<String>();
		
		String querryGroupPart = "";
		if(curGroupName != "<Âñ³>")
		{
			querryGroupPart = " AND g.name = '" + curGroupName + "'";
		}
		
		String querry1 = "SELECT COUNT(v.visitingId) "
				+ "FROM Students AS s INNER JOIN Groups AS g ON "
				+ "s.groupId = g.groupId INNER JOIN Visitings as v ON v.studentId = s.studentId "
				+ "WHERE s.name = '" + studentName + "' AND s.birthdate IN (SELECT s2.birthdate "
				+ "FROM Students AS s2 INNER JOIN Groups AS g2 ON s2.groupId = g2.groupId AND g2.name = '"
				+ mainGroupName + "')" + querryGroupPart;
		
		
		
		String querry2 = "SELECT COUNT(v.visitingId) FROM Students AS s INNER JOIN Groups AS g ON "
				+ "s.groupId = g.groupId INNER JOIN Visitings as v ON v.studentId = s.studentId "
				+ "WHERE s.name = '" + studentName + "' AND s.birthdate IN (SELECT s2.birthdate "
				+ "FROM Students AS s2 INNER JOIN Groups AS g2 ON s2.groupId = g2.groupId AND g2.name = '"
				+ mainGroupName + "') AND v.presence != 0" + querryGroupPart;
		
		String querry3 = "SELECT AVG(v.mark)"
				+ "FROM Students AS s INNER JOIN Groups AS g ON "
				+ "s.groupId = g.groupId INNER JOIN Visitings as v ON v.studentId = s.studentId "
				+ "WHERE s.name = '" + studentName + "' AND s.birthdate IN (SELECT s2.birthdate "
				+ "FROM Students AS s2 INNER JOIN Groups AS g2 ON s2.groupId = g2.groupId AND g2.name = '"
				+ mainGroupName + "') AND v.mark != 0" + querryGroupPart;
		
		if (dates != "")
		{
			String[] splittedDates = dates.split(" ");
			querry1 += " AND v.date >= '" + splittedDates[0] + "' AND v.date <= '"
					+ splittedDates[1] + "'";
			querry2 += " AND v.date >= '" + splittedDates[0] + "' AND v.date <= '"
					+ splittedDates[1] + "'";
		}
		querry1 += ";";
		querry2 += ";";
		
		
		DB db = DB.conn();
		List<String> strList1 = db.select(querry1);
		List<String> strList2 = db.select(querry2);
		List<String> strList3 = db.select(querry3);
		
		for(String str : strList1)
		{
			String[] split1 = str.split("\n");
			String[] split2 = strList2.get(0).split("\n");
			String[] split3 = strList3.get(0).split("\n");

			list.add(split3[0]);
			////////////
			Integer.parseInt(split1[0]);
			Integer.parseInt(split2[0]);
			//////////////TODO DELETE /\
			list.add("" + (Integer.parseInt(split1[0]) - Integer.parseInt(split2[0])));
			list.add(split2[0]);
		}
		
		return list;
	}
	
	public static List<Student> getStudents(String groupName)
	{
		List<Student> list = new LinkedList<Student>();

		String querry = "SELECT s.name, birthdate, ismale FROM Students AS s, Groups AS g WHERE "
				+ "g.name = '" + groupName
				+ "' AND s.groupid = g.groupid ORDER BY s.name;";

		DB db = DB.conn();
		List<String> strList = db.select(querry);

		for(String str : strList)
		{
			String[] split = str.split("\n");
			boolean isNewStudentMale = Integer.parseInt(split[2]) == 1;
			list.add(new Student(split[0], split[1], isNewStudentMale));
		}
		System.out.println("Size of students list of '" + groupName + "': " + list.size());
		return list;
	}
	
	public static Student getStudent (String studentName, String groupName)
	{
		Student student = new Student("default name", "1970-12-30", true);

		String querry = "SELECT s.name, birthdate, ismale FROM Students AS s "
				+ "INNER JOIN Groups AS g ON s.groupid = g.groupid WHERE "
				+ "g.name = '" + groupName
				+ "' AND s.name = '" + studentName + "';";

		DB db = DB.conn();
		List<String> strList = db.select(querry);

		for(String str : strList)
		{
			String[] split = str.split("\n");
			boolean isNewStudentMale = Integer.parseInt(split[2]) == 1;
			student = new Student(split[0], split[1], isNewStudentMale);
		}
		return student;
	}
	
	public static Student create(String name, String birthday, boolean isMale, String groupName)
	{
		Student newStudent = new Student(name, birthday, isMale);
		
		String querry = "INSERT INTO Students (name, birthdate, ismale, groupid) "
				+ "VALUES ('" + newStudent.getName() + "', '"
				+ newStudent.getBirthday() + "', "
				+ ((newStudent.isMale()) ? 1 : 0) 
				+ ", (SELECT groupid FROM Groups WHERE name = '" + groupName
				+ "'));";

		DB db = DB.conn();
		
		db.executeUpdate(querry);
		return newStudent;
	}
	
	public static void delete(String studentName, String groupName)
	{
		String querry = "DELETE FROM Students WHERE groupid = "
				+ "(SELECT FROM Groups WHERE name = '" + groupName
				+ "'), name = '" + studentName + "';";

		DB db = DB.conn();
		
		db.executeUpdate(querry);
	}
	
	public static Student change(String oldName, String newName, String birthday, boolean isMale, String groupName)
	{
		Student newStudent = new Student(newName, birthday, isMale);
		
		String querry = "UPDATE Students SET name = '" + newStudent.getName()
				+ "' , birthdate = '" + newStudent.getBirthday()
				+ "', ismale = " + (newStudent.isMale() ? 1 : 0)
				+ " WHERE name = '" + oldName 
				+ "' AND groupid IN (SELECT groupid FROM Groups WHERE name = '" + groupName
				+ "');";

		DB db = DB.conn();
		
		db.executeUpdate(querry);
		return newStudent;
	}
}
