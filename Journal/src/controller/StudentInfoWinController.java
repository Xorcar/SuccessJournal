package controller;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.Group;
import model.Student;
import model.Visiting;

public class StudentInfoWinController
{

    @FXML
    private Label labName;
    @FXML
    private Label labBirthdate;
    @FXML
    private Label labAge;
    @FXML
    private Label labSex;
    @FXML
    private Label labAvgMark;
    @FXML
    private Label labPres;
    @FXML
    private Label labAbs;

    @FXML
    private ChoiceBox choGroup;

    @FXML
    private CheckBox cheAllTime;

    @FXML
    private DatePicker dteFrom;
    @FXML
    private DatePicker dteTo;

    @FXML
    private Button btnShow;

    @FXML
    private LineChart<String, Number> chrtMark;
    @FXML
    private StackedBarChart<String, Number> chrtPresenceAll;
    @FXML
    private PieChart chrtPresence;

    private String studentName = "";
    private String mainGroupName = "";

    private void initLabels()
    {
	Student student = Student.getStudent(studentName, mainGroupName);

	labName.setText(student.getName());
	labBirthdate.setText(student.getBirthday());
	labAge.setText("" + ChronoUnit.YEARS.between(LocalDate.parse(student.getBirthday()), LocalDate.now()));
	labSex.setText(student.isMale() ? "Хлопчик" : "Дівчинка");
    }

    private void initStatisticLabels(String curGroupName)
    {
	String dates;
	if (cheAllTime.isSelected())
	{
	    dates = "";
	} else
	{
	    dates = dteFrom.getValue().toString() + " " + dteTo.getValue().toString();
	}

	List<String> statistics = Student.getPresenceStatistic(studentName, curGroupName, mainGroupName, dates);

	labAvgMark.setText(statistics.get(0) == "null" ? "0" : statistics.get(0));
	labPres.setText(statistics.get(1));
	labAbs.setText(statistics.get(2));

    }

    private void initChoiceBox()
    {
	List<String> listOfGroupNames = Group.getGroupNames(studentName, mainGroupName);
	listOfGroupNames.add(0, "<Всі>");

	choGroup.setItems(FXCollections.observableArrayList(listOfGroupNames.toArray()));

	choGroup.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>()
	{
	    @Override
	    public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2)
	    {
		initStatisticLabels(choGroup.getItems().get((Integer) number2).toString());
		// TODO fill action for choice box ///fillTable(((Group)
		// chbGroupsCon.getItems().get((Integer) number2)).getName());
	    }
	});

	choGroup.getSelectionModel().select(0);

    }

    private void initCheckBox()
    {
	cheAllTime.setOnAction(new EventHandler<ActionEvent>()
	{
	    @Override
	    public void handle(ActionEvent e)
	    {
		if (dteFrom.isDisabled() && dteTo.isDisabled())
		{
		    dteFrom.setDisable(false);
		    dteTo.setDisable(false);
		} else
		{
		    dteFrom.setDisable(true);
		    dteTo.setDisable(true);
		}
	    }
	});
    }

    private void initCharts()
    {
	List<List<Visiting>> allVisits = new LinkedList<>();

	String dateFrom = "0000-01-01";
	String dateTo = "9999-01-01";
	if (!cheAllTime.isSelected())
	{
	    dateFrom = dteFrom.getValue().toString();
	    dateTo = dteTo.getValue().toString();
	}

	if (choGroup.getValue().equals("<Всі>"))
	{
	    for (int i = 1; i < choGroup.getItems().size(); i++)
	    {
		allVisits.add(Visiting.getVisitingsOfStudent((String) choGroup.getItems().get(i), studentName, dateFrom,
			dateTo));
	    }
	} else
	{
	    allVisits.add(Visiting.getVisitingsOfStudent((String) choGroup.getValue(), studentName, dateFrom, dateTo));
	}

	final LineChart<String, Number> lineChart = chrtMark;

	lineChart.setAnimated(false);
	lineChart.getXAxis().setLabel("Дата");
	lineChart.getYAxis().setLabel("Оцінка");
	lineChart.setTitle("Успішність");
	List<Series<String, Number>> seriesList = new LinkedList<>();
	for (List<Visiting> l : allVisits)
	{
	    XYChart.Series<String, Number> series = new XYChart.Series<>();
	    for (Visiting v : l)
	    {
		series.getData().add(new XYChart.Data(v.getDate().toString(), v.getMark()));
	    }
	    if (choGroup.getValue().equals("<Всі>"))
	    {
		series.setName((String) choGroup.getItems().get(allVisits.indexOf(l) + 1));
	    } else
	    {
		series.setName((String) choGroup.getValue());
	    }
	    seriesList.add(series);
	}
	lineChart.setData(FXCollections.observableList(seriesList));

	if (choGroup.getValue().equals("<Всі>"))
	{
	    VBox parent = (VBox) chrtMark.getParent();
	    if (!parent.getChildren().contains(chrtPresenceAll))
	    {
		parent.getChildren().add(chrtPresenceAll);
	    }
	    if (parent.getChildren().contains(chrtPresence))
	    {
		parent.getChildren().remove(chrtPresence);
	    }

	    StackedBarChart<String, Number> sbc = chrtPresenceAll;
	    sbc.setTitle("Country Summary");

	    String dates = "";
	    if (!cheAllTime.isSelected())
	    {
		dates = dateFrom + " " + dateTo;
	    }

	    final XYChart.Series<String, Number> seriesPres = new XYChart.Series<String, Number>();
	    final XYChart.Series<String, Number> seriesAbs = new XYChart.Series<String, Number>();

	    seriesPres.setName("Присутність");
	    seriesAbs.setName("Відсутність");
	    for (int i = 1; i < choGroup.getItems().size(); i++)
	    {
		List<String> l = Student.getPresenceStatistic(studentName, (String) choGroup.getItems().get(i),
			mainGroupName, dates);
		seriesPres.getData().add(new XYChart.Data<String, Number>((String) choGroup.getItems().get(i),
			Integer.parseInt(l.get(1))));
		seriesAbs.getData().add(new XYChart.Data<String, Number>((String) choGroup.getItems().get(i),
			Integer.parseInt(l.get(2))));
	    }
	    List<Series<String, Number>> series = new LinkedList<>();
	    series.add(seriesPres);
	    series.add(seriesAbs);
	    sbc.getXAxis().setLabel("Група");
	    sbc.getYAxis().setLabel("Кількість");
	    sbc.setTitle("Відвідуваність");

	    sbc.setData(FXCollections.observableList(series));

	} else
	{
	    VBox parent = (VBox) chrtMark.getParent();
	    if (parent.getChildren().contains(chrtPresenceAll))
	    {
		parent.getChildren().remove(chrtPresenceAll);
	    }
	    if (!parent.getChildren().contains(chrtPresence))
	    {
		parent.getChildren().add(chrtPresence);
	    }

	    StackedBarChart<String, Number> sbc = chrtPresenceAll;

	    String dates = "";
	    if (!cheAllTime.isSelected())
	    {
		dates = dateFrom + " " + dateTo;
	    }

	    List<String> statistic = Student.getPresenceStatistic(studentName, (String) choGroup.getValue(),
		    mainGroupName, dates);

	    List<PieChart.Data> series = new LinkedList<>();
	    series.add(new PieChart.Data("Присутність", Integer.parseInt(statistic.get(1))));
	    series.add(new PieChart.Data("Відсутність", Integer.parseInt(statistic.get(2))));
	    chrtPresence.setData(FXCollections.observableList(series));
	    chrtPresence.setTitle("Відвідуваність");
	}
    }

    @FXML
    private void initialize()
    {
	String[] data = mainWinController.betweenWindowsData.split("\n");

	mainGroupName = data[0];
	studentName = data[1];
	initLabels();
	dteFrom.setValue(LocalDate.now().minusMonths(1));
	dteTo.setValue(LocalDate.now());
	initChoiceBox();
	initCheckBox();
	initCharts();

	btnShow.setOnAction(new EventHandler<ActionEvent>()
	{
	    @Override
	    public void handle(ActionEvent e)
	    {
		initStatisticLabels(choGroup.getSelectionModel().getSelectedItem().toString());
		initCharts();
	    }
	});
    }
}
