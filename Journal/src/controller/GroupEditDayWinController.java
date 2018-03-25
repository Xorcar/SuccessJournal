package controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import application.Main;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Group;
import model.TableDat;
import model.Visiting;

public class GroupEditDayWinController
{

    @FXML
    private TableView<TableDat> tablGroup;
    @FXML
    private Button btnRefresh;
    @FXML
    private Button btnInfo;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnDelete;
    
    @FXML
    private ChoiceBox<String> chbDate;
    @FXML
    private CheckBox cheIfNewDate;
    @FXML
    private DatePicker dteNewDate;

    private String groupName;

    private void fillTable(LocalDate date)
    {
	TableView<TableDat> table = tablGroup;
	tablGroup = new TableView<TableDat>();

	ObservableList<TableDat> list = TableDat.getList(groupName, date);
	list.sort(new Comparator<TableDat>()
	{
	    @Override
	    public int compare(TableDat td1, TableDat td2)
	    {
		return td1.getName().compareTo(td2.getName());
	    }
	});

	for (int i = 0; i < list.size(); i++)
	{
	    list.get(i).setNum(i + 1);
	}
	System.out.println("Size of list in table: " + list.size());
	System.out.println(list.get(0).isPresence());
	table.getItems().clear();
	table.setItems(list);

	table.getColumns().get(2).setText(date.toString());

	tablGroup = table;

    }

    private void initTable()
    {
	TableView<TableDat> table = tablGroup;
	tablGroup = new TableView<TableDat>();

	table.setPlaceholder(new Label("Виберіть день"));
	
	TableColumn<TableDat, String> colNum = new TableColumn<TableDat, String>("№");
	TableColumn<TableDat, String> colName = new TableColumn<TableDat, String>("Ім'я");
	TableColumn<TableDat, String> colDate = new TableColumn<TableDat, String>("0000-00-00");
	TableColumn<TableDat, String> colMark = new TableColumn<TableDat, String>("Оцінка");
	TableColumn<TableDat, Boolean> colPres = new TableColumn<TableDat, Boolean>("Прис");

	table.setEditable(true);

	colNum.setCellValueFactory(new PropertyValueFactory<>("num"));

	colName.setCellValueFactory(new PropertyValueFactory<>("name"));
	colMark.setCellValueFactory(new PropertyValueFactory<>("mark"));

	colMark.setCellFactory(TextFieldTableCell.<TableDat>forTableColumn());

	// On Cell edit commit
	colMark.setOnEditCommit((CellEditEvent<TableDat, String> event) -> {
	    TablePosition<TableDat, String> pos = event.getTablePosition();

	    String newMark = event.getNewValue();

	    int row = pos.getRow();
	    TableDat person = event.getTableView().getItems().get(row);

	    person.setMark(newMark);
	});

	colPres.setCellValueFactory(new Callback<CellDataFeatures<TableDat, Boolean>, ObservableValue<Boolean>>()
	{

	    @Override
	    public ObservableValue<Boolean> call(CellDataFeatures<TableDat, Boolean> param)
	    {
		TableDat person = param.getValue();

		SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(person.isPresence());

		// Note: singleCol.setOnEditCommit(): Not work for CheckBoxTableCell.

		// When "Single?" column change.
		booleanProp.addListener(new ChangeListener<Boolean>()
		{

		    @Override
		    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
			    Boolean newValue)
		    {
			person.setPresence(newValue);
		    }
		});
		return booleanProp;
	    }
	});

	colPres.setCellFactory(new Callback<TableColumn<TableDat, Boolean>, //
		TableCell<TableDat, Boolean>>()
	{
	    @Override
	    public TableCell<TableDat, Boolean> call(TableColumn<TableDat, Boolean> p)
	    {
		CheckBoxTableCell<TableDat, Boolean> cell = new CheckBoxTableCell<TableDat, Boolean>();
		cell.setAlignment(Pos.CENTER);
		return cell;
	    }
	});

	colNum.setPrefWidth(30);
	colName.setPrefWidth(140);
	colMark.setPrefWidth(50);
	colPres.setPrefWidth(40);
	
	colDate.getColumns().addAll(colMark, colPres);
	table.getColumns().addAll(colNum, colName, colDate);

	tablGroup = table;
    }

    public void saveJournalBtnAction()
    {
	TableView<TableDat> table = tablGroup;

	ObservableList<TableDat> list = table.getItems();

	LocalDate date = LocalDate.parse(table.getColumns().get(2).getText());

	Visiting.deleteDay(groupName, date);

	System.out.println("save button, items list size: " + list.size());
	for (TableDat td : list)
	{
	    System.out.println(td.getName() + " " + td.getMark() + " " + td.isPresence());
	    Visiting.writeVisiting(groupName, date, td.getName(), Integer.parseInt(td.getMark()), td.isPresence());
	}
    }

    public void tableStudentsInfoBtnAction()
    {
	TableView<TableDat> table = tablGroup;

	String selectedStudentName = table.getSelectionModel().getSelectedItems().get(0).getName();

	// TODO finish
	System.out.println("Open new info window: " + selectedStudentName + " from " + groupName);

	mainWinController.betweenWindowsData = groupName + "\n" + selectedStudentName;

	try
	{

	    VBox root = (VBox) FXMLLoader.load(getClass().getResource("/view/StudentInfoWindow.fxml"));

	    Scene scene = new Scene(root);
	    Stage stage = new Stage();
	    stage.setScene(scene);
	    stage.setTitle("Інформація про " + selectedStudentName);
	    stage.show();
	} catch (IOException e)
	{
	    e.printStackTrace();
	}
    }

    private void deleteJournalBtnAction()
    {
	Visiting.deleteDay(groupName, getDate());
    }
    
    private LocalDate getDate()
    {
	LocalDate dateToReturn = LocalDate.now();
	if (cheIfNewDate.isSelected())
	{
	    dateToReturn = dteNewDate.getValue();
	} else
	{
	    System.out.println("Returned date in edit window: " + chbDate.getValue());
	    dateToReturn = LocalDate.parse(chbDate.getValue());
	}

	return dateToReturn;
    }

    private void fillChoiceBox()
    {
	List<String> dates = Group.getDates(groupName);

	chbDate.setItems(FXCollections.observableList(dates));

	chbDate.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>()
	{
	    @Override
	    public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2)
	    {
		fillTable(LocalDate.parse(chbDate.getItems().get(number2.intValue())));
	    }
	});
    }

    private void initCheckBox()
    {
	cheIfNewDate.selectedProperty().addListener((ChangeListener<? super Boolean>) new ChangeListener<Boolean>()
	{
	    public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val)
	    {
		if (new_val == false)
		{
		    dteNewDate.setDisable(true);
		} else
		{
		    dteNewDate.setDisable(false);
		}
	    }
	});
    }

    private void initDatePicker()
    {
	dteNewDate.setOnAction(new EventHandler<ActionEvent>()
	{
	    @Override
	    public void handle(ActionEvent e)
	    {
		fillTable(dteNewDate.getValue());
	    }
	});

    }

    @FXML
    private void initialize()
    {
	System.out.println("initialize of controller of group edit day window");

	groupName = mainWinController.betweenWindowsData;

	fillChoiceBox();
	initTable();
	initCheckBox();
	initDatePicker();
	
	btnRefresh.setOnAction(new EventHandler<ActionEvent>()
	{
	    @Override
	    public void handle(ActionEvent e)
	    {
		fillTable(getDate());
	    }
	});

	btnInfo.setOnAction(new EventHandler<ActionEvent>()
	{
	    @Override
	    public void handle(ActionEvent e)
	    {
		tableStudentsInfoBtnAction();
	    }
	});

	btnSave.setOnAction(new EventHandler<ActionEvent>()
	{
	    @Override
	    public void handle(ActionEvent e)
	    {
		saveJournalBtnAction();
	    }
	});

	btnDelete.setOnAction(new EventHandler<ActionEvent>()
	{
	    @Override
	    public void handle(ActionEvent e)
	    {
		deleteJournalBtnAction();
		fillTable(getDate());
	    }
	});
    }

}
