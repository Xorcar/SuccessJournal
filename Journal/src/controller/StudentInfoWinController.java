package controller;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import model.Group;
import model.Student;

public class StudentInfoWinController {

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
	
	private String studentName = "";
	private String mainGroupName = "";
	
	private void initLabels()
	{
		Student student = Student.getStudent(studentName, mainGroupName);
		
		labName.setText(student.getName());
		labBirthdate.setText(student.getBirthday());
		labAge.setText("" + ChronoUnit.YEARS.between(LocalDate.parse(student.getBirthday()), LocalDate.now()));
		labSex.setText(student.isMale()? "Хлопчик" : "Дівчинка");
	}
	
	private void initStatisticLabels(String curGroupName)
	{
		String dates;
		if (cheAllTime.isSelected())
		{
			dates = "";
		}
		else
		{
			dates = dteFrom.getValue().toString() + " " + dteTo.getValue().toString();
		}
		
		List<String> statistics = Student.getPresenceStatistic(studentName,
				curGroupName, mainGroupName, dates);
		
		labAvgMark.setText(statistics.get(0));
		labPres.setText(statistics.get(1));
		labAbs.setText(statistics.get(2));
		
	}
	
	private void initChoiceBox()
	{
		List<String> listOfGroupNames = Group.getGroupNames(studentName, mainGroupName);
		listOfGroupNames.add(0, "<Всі>");
		
		choGroup.setItems(FXCollections.observableArrayList(
				listOfGroupNames.toArray()));

		choGroup.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
				initStatisticLabels(choGroup.getItems().get((Integer) number2).toString());
				//TODO fill action for choice box ///fillTable(((Group) chbGroupsCon.getItems().get((Integer) number2)).getName());
			}
		});
		
		choGroup.getSelectionModel().select(0);
		
	}
	
	private void initCheckBox()
	{
		cheAllTime.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				if(dteFrom.isDisabled() && dteTo.isDisabled())
				{
					dteFrom.setDisable(false);
					dteTo.setDisable(false);
				}
				else
				{
					dteFrom.setDisable(true);
					dteTo.setDisable(true);
				}
			}
		});
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
		
		btnShow.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				initStatisticLabels(choGroup.getSelectionModel().getSelectedItem().toString());
			}
		});
	}
}
