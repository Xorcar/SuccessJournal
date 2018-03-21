package controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
	private Button btnEdit;
	
	private String groupName;
	
	private void initTable()
	{
		/*TableView<Row> tableView = new TableView<>();

        List<Row> rows = makeSampleData();

        int max = getMaxCells(rows);
        makeColumns(max, tableView);
        tableView.getItems().addAll(rows);*/

        // Boilerplate code for showing the TableView
        //Scene scene = new Scene(tableView, 1000, 1000);
        //primaryStage.setScene(scene);
        //primaryStage.show();
	}
	
	private void fillTable(LocalDate from, LocalDate to, String groupName)
	{
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

	
	
	@FXML
	private void initialize() 
	{
        System.out.println("Initialize of group full window started");
		groupName = mainWinController.betweenWindowsData;
		//initTable();
		fillTable(LocalDate.parse("0000-01-01"), LocalDate.parse("9999-01-01"), groupName);
        System.out.println("Initialize of group full window finished");
	}
	
}
