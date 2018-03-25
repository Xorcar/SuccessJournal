package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import model.DB;
import model.Visiting;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;

public class Main extends Application
{

    /**
     * TODO check why on Windows dont work, need hardkodong into '/' Character that
     * separates path folders in current OS. ('/' in Windows)
     */
    // public static char separator = File.separatorChar;

    private void setConnectionToDb()
    {

	try
	{
	    String curFolder = new File(".").getCanonicalPath().toString();
	    if (new File(curFolder, "students.db").exists())
	    {
		DB.setDbLocation(curFolder + File.separatorChar + "students.db");
	    } else
	    {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Вікно помилки");
		alert.setHeaderText(
			"У теці з програмою не було знайдено базу даних\nза " + "замовчуванням (файл students.db)");
		alert.setContentText("Будь ласка, перед тим, як використовувати програму,\n"
			+ "виберіть файл бази даних\nчерез кнопку 'Вибрати файл бази даних'\n"
			+ "у вкладці 'Управління БД'.");
		alert.showAndWait();
	    }
	} catch (IOException e1)
	{
	    e1.printStackTrace();
	}
    }

    @Override
    public void start(Stage stage)
    {
	try
	{
	    setConnectionToDb();
	    AnchorPane root = (AnchorPane) FXMLLoader.load(getClass().getResource("/view/MainWindow.fxml"));

	    Scene scene = new Scene(root);
	    stage.setScene(scene);
	    stage.setTitle("Журнал");
	    stage.show();

	} catch (Exception e)
	{
	    e.printStackTrace();

	    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(AlertType.ERROR);
	    alert.setTitle("Сталася помилка");
	    alert.setHeaderText("Код помилки:");

	    StringWriter stackTraceWriter = new StringWriter();
	    e.printStackTrace(new PrintWriter(stackTraceWriter));

	    alert.setContentText(e.toString() + "\n" + stackTraceWriter.toString());

	    alert.showAndWait();
	}
    }

    public static void main(String[] args)
    {
	launch(args);
    }

    public static void fillDB()
    {
	List<String> students = new LinkedList<>();
	students.add("Кожумяк Денис");
	students.add("Вишня Андрій");
	students.add("Княжа Каріна");
	students.add("Воронко Сергій");
	students.add("Мудра Софія");

	Random r = new Random();
	LocalDate day = LocalDate.parse("2017-09-01");
	while (day.getDayOfWeek().getValue() != 3)
	{
	    day = day.plusDays(1);
	}
	Visiting visit;
	for (int i = 0; day.toString().compareTo("2017-12-12") < 0; i++)
	{
	    for (String s : students)
	    {
		if (r.nextBoolean())
		{
		    visit = new Visiting(false, 0, day.toString());
		} else
		{
		    visit = new Visiting(true, 5 + r.nextInt(7), day.toString());
		}
		Visiting.writeVisiting("Група 3", day, s, visit.getMark(), visit.isPresence());
	    }
	    day = day.plusDays(1);
	    for (String s : students)
	    {
		if (r.nextBoolean())
		{
		    visit = new Visiting(false, 0, day.toString());
		} else
		{
		    visit = new Visiting(true, 5 + r.nextInt(7), day.toString());
		}
		Visiting.writeVisiting("Група 3", day, s, visit.getMark(), visit.isPresence());
	    }
	    day = day.plusDays(6);
	}
    }
}
