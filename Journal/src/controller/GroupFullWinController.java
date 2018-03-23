package controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import application.Main;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Group;
import model.Student;
import model.TableDat;
import model.Visiting;

public class GroupFullWinController {

	@FXML
	private TableView tblJournal;

	@FXML
	private DatePicker dteFrom;
	@FXML
	private DatePicker dteTo;
	@FXML
	private CheckBox cheAll;
	@FXML
	private Button btnRefresh;
	@FXML
	private Button btnEdit;
	
	private String groupName;
	
	private void clearTable()
	{
		tblJournal.getColumns().clear();
		tblJournal.getItems().clear();
	}
	
	private void fillTable(String groupName)
	{
		LocalDate from = LocalDate.parse("0000-01-01");
		LocalDate to = LocalDate.parse("9999-01-01");
		if(!cheAll.isSelected())
		{
			from = dteFrom.getValue();
			to = dteTo.getValue();
		}
		
		ObservableList<List<String>> dataList = Visiting.getAllVisitings(groupName, from.toString(), to.toString());

        TableColumn<List<String>, String> columnNum = new TableColumn<>("№");
        columnNum.setCellValueFactory(param -> {
            return new SimpleStringProperty(param.getValue().get(0));
        });
        TableColumn<List<String>, String> columnName = new TableColumn<>("Ім'я");
        columnName.setCellValueFactory(param -> {
            return new SimpleStringProperty(param.getValue().get(1));
        });
        tblJournal.getColumns().addAll(columnNum, columnName);
        

		TableView<List<String>> table = tblJournal;
		table.setEditable(true);
        
		if(dataList.size() >= 1)
		{
			for (int m = 0; m < (dataList.get(0).size() - 2) / 3; m++)
	        {
	            TableColumn<List<String>, String> column = new TableColumn<>("0000-00-00");
	            TableColumn<List<String>, String> columnM = new TableColumn<>("Оцінка");
	            TableColumn<List<String>, String> columnP = new TableColumn<>("Прис");
	            
	            column.getColumns().addAll(columnM, columnP);
	            table.getColumns().add(column);
	            System.out.println("column's index: "
	            		+ table.getColumns().indexOf(columnM.getParentColumn()));
	            
	            columnM.setCellValueFactory(param -> {
	                int index = param.getTableView().getColumns().indexOf(param.getTableColumn().getParentColumn());
	                String cellStr = param.getValue().get(3*index-3).equals("null") ? "0" : param.getValue().get(3*index-3);
	                return new SimpleStringProperty(cellStr);
	            });
	            columnP.setCellValueFactory(param -> {
	                int index = param.getTableView().getColumns().indexOf(param.getTableColumn().getParentColumn());
	                
	                String cellStr = param.getValue().get(3*index-2).equals("1") ? "✓" : "✗";
	                return new SimpleStringProperty(cellStr);
	            });
	            column.setText(dataList.get(0).get(3 * tblJournal.getColumns().indexOf(column) -4));

	        }
		}
        tblJournal = table;
        tblJournal.setItems(dataList);
	}

	private void initCheckBox()
	{
		cheAll.selectedProperty().addListener((ChangeListener<? super Boolean>) new ChangeListener<Boolean>() {
	        public void changed(ObservableValue<? extends Boolean> ov,
	                Boolean old_val, Boolean new_val) {
	        			if(new_val == false)
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
	
	private void initDatePickers()
	{
		if(LocalDate.now().getMonthValue() <= 6)
		{
			dteFrom.setValue(LocalDate.of(LocalDate.now().getYear(), 1, 1));
			dteTo.setValue(LocalDate.of(LocalDate.now().getYear(), 6, 30));
		}
		else
		{
			dteFrom.setValue(LocalDate.of(LocalDate.now().getYear(), 7, 1));
			dteTo.setValue(LocalDate.of(LocalDate.now().getYear(), 12, 31));
		}
	}
	
	private void openGroupEditDayWindow()
	{
		//TODO end
		System.out.println("Open new editing window: info about group " +
				groupName + " at date "  );
		
		mainWinController.betweenWindowsData = groupName;
		
        try {

			VBox root = (VBox) FXMLLoader.load(getClass().getResource("/view/GroupEditDayWindow.fxml"));
			
			Scene scene = new Scene(root);
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.setTitle("Редагування для групи " + groupName);
			stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	@FXML
	private void initialize() 
	{
		groupName = mainWinController.betweenWindowsData;
		initCheckBox();
		initDatePickers();
		
		fillTable(groupName);
		
		btnRefresh.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				clearTable();
				fillTable(groupName);
			}
		});
		
		btnEdit.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				openGroupEditDayWindow();
			}
		});
	}
	
}
