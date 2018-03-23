package controller;



import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import application.Main;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Pair;
import model.DB;
import model.Group;
import model.Student;
import model.TableDat;
import model.Visiting;

public class mainWinController {

	/**
	 * This variable is used to give information to other controllers.
	 * For example, give name and group of students to create his info window.
	 */
	public static String betweenWindowsData = "";
	
	//tab#1 controls ////////////////////////////////
	@FXML
	private ChoiceBox chbGroups;

	@FXML
	private CheckBox cheGroups;
	
	@FXML
	private TableView<TableDat> tablGroup;

	@FXML
	private Button btnRefresh;
	@FXML
	private Button btnInfo;
	@FXML
	private Button btnSave;
	@FXML
	private Button btnFullJournal;
	
	
	//tab#2 controls ////////////////////////////////

	@FXML
	private ChoiceBox chbGroupsCon;

	@FXML
	private Button btnRefreshGroups;
	@FXML
	private Button btnCreateGroup;
	@FXML
	private Button btnGroupStudentsInfo;
	@FXML
	private Button btnGroupStudentsChange;
	@FXML
	private Button btnGroupStudentsAdd;
	@FXML
	private Button btnGroupDaysSave;

	@FXML
	private ListView lstGroupStudents;

	@FXML
	private CheckBox chkGroupMon;
	@FXML
	private CheckBox chkGroupTue;
	@FXML
	private CheckBox chkGroupWed;
	@FXML
	private CheckBox chkGroupThu;
	@FXML
	private CheckBox chkGroupFri;
	@FXML
	private CheckBox chkGroupSat;
	@FXML
	private CheckBox chkGroupSun;

	@FXML
	private TextField txtfGroupName;

	@FXML
	private DatePicker dteGroupFrom;
	@FXML
	private DatePicker dteGroupTo;

	//tab#3 controls ////////////////////////////////
	
	@FXML
	private TextField txtfBdPath;
	@FXML
	private Button btnChooseBd;
	@FXML
	private Button btnCreateBd;



	private void fillChoiceBoxes()
	{
		chbGroups.getItems().clear();
		chbGroupsCon.getItems().clear();
		
		List<Group> groups = Group.getGroups();
		chbGroupsCon.setItems(FXCollections.observableArrayList(
				groups));
		if(!cheGroups.isSelected())
		{
			List<Group> groupsToday = Group.getGroups();
			for(int i = 0; i < groupsToday.size();)
			{
				if(!groupsToday.get(i).getDaysOfWeek().contains(LocalDate.now().getDayOfWeek()))
				{
					groupsToday.remove(i);
				}
				else
				{
					i++;
				}
			}
			chbGroups.setItems(FXCollections.observableArrayList(
					groupsToday));
		}
		else
		{
			chbGroups.setItems(FXCollections.observableArrayList(
					groups));
		}
		
		chbGroups.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
				fillTable(((Group) chbGroups.getItems().get((Integer) number2)).getName());
			}
		});
		
		chbGroupsCon.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
				System.out.println(chbGroupsCon.getItems().get((Integer) number2));
				fillGroupList(((Group) chbGroupsCon.getItems().get((Integer) number2)).getName());
				fillWorkDaysPane(((Group) chbGroupsCon.getItems().get((Integer) number2)).getName());
				//TODO add refresh of choiceBox
			}
		});
		
	}

	private void fillTable(String groupName)
	{
		TableView<TableDat> table = tablGroup;
		tablGroup = new TableView<TableDat>();

		ObservableList<TableDat> list = TableDat.getList(groupName, LocalDate.now());
		list.sort(new Comparator<TableDat>() {
			@Override
			public int compare(TableDat td1, TableDat td2) {
				return td1.getName().compareTo(td2.getName());
			}
		});

		for (int i = 0; i < list.size(); i++)
		{
			list.get(i).setNum(i + 1);
		}
		System.out.println("Size of list in table: " + list.size());
		System.out.println(list.get(0).isPresence());
		table.setItems(list);

		tablGroup = table;

	}

	private void initTable()
	{
		TableView<TableDat> table = tablGroup;
		tablGroup = new TableView<TableDat>();

		table.setPlaceholder(new Label("Виберіть групу з дітьми"));
		
		TableColumn<TableDat, String> colNum = new TableColumn<TableDat, String>("№");
		TableColumn<TableDat, String> colName = new TableColumn<TableDat, String>("Ім'я");
		TableColumn<TableDat, String> colDate = new TableColumn<TableDat, String>(LocalDate.now().toString());
		TableColumn<TableDat, String> colMark = new TableColumn<TableDat, String>("Оцінка");
		TableColumn<TableDat, Boolean> colPres = new TableColumn<TableDat, Boolean>("Прис");


		// Editable
		table.setEditable(true);

		
		// ==== FULL NAME (TEXT FIELD) ===
		colNum.setCellValueFactory(new PropertyValueFactory<>("num"));

		///////////////////////////////
		colName.setCellValueFactory(new PropertyValueFactory<>("name"));
		colMark.setCellValueFactory(new PropertyValueFactory<>("mark"));

		colMark.setCellFactory(TextFieldTableCell.<TableDat> forTableColumn());
		
		// On Cell edit commit
		colMark.setOnEditCommit((CellEditEvent<TableDat, String> event) -> {
			TablePosition<TableDat, String> pos = event.getTablePosition();

			String newMark = event.getNewValue();

			int row = pos.getRow();
			TableDat person = event.getTableView().getItems().get(row);

			person.setMark(newMark);
		});
		
		colPres.setCellValueFactory(new Callback<CellDataFeatures<TableDat, Boolean>, ObservableValue<Boolean>>() {

			@Override
			public ObservableValue<Boolean> call(CellDataFeatures<TableDat, Boolean> param) {
				TableDat person = param.getValue();

				SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(person.isPresence());

				// Note: singleCol.setOnEditCommit(): Not work for CheckBoxTableCell.

				// When "Single?" column change.
				booleanProp.addListener(new ChangeListener<Boolean>() {

					@Override
					public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
							Boolean newValue) {
						person.setPresence(newValue);
					}
				});
				return booleanProp;
			}
		});

		colPres.setCellFactory(new Callback<TableColumn<TableDat, Boolean>, //
				TableCell<TableDat, Boolean>>() {
			@Override
			public TableCell<TableDat, Boolean> call(TableColumn<TableDat, Boolean> p) {
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

	private void initCheckBox()
	{
		cheGroups.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				fillChoiceBoxes();
			}
		});
	}
	
	public void saveJournalBtnAction()
	{
		TableView<TableDat> table = tablGroup;

		ObservableList<TableDat> list = table.getItems();

		Group selectedGroup = (Group) chbGroups.getSelectionModel().getSelectedItem();
		String groupName = selectedGroup.getName();
		Visiting.deleteDay(groupName, LocalDate.now()); 
		
		System.out.println("save button, items list size: " + list.size());
		for(TableDat td : list)
		{
			System.out.println(td.getName() + " " + td.getMark() + " "
					+ td.isPresence());
			Visiting.writeVisiting(groupName, LocalDate.now(), td.getName(), Integer.parseInt(td.getMark()), td.isPresence());
		}
	}

	private void openFullJournal()
	{
		//TODO continue
		System.out.println("Open new info window: info about group " +
				((Group)(chbGroups.getSelectionModel().getSelectedItem())).getName());
		
		betweenWindowsData = ((Group)(chbGroups.getSelectionModel().getSelectedItem())).getName();
		
        try {

			VBox root = (VBox) FXMLLoader.load(getClass().getResource("/view/GroupFullWindow.fxml"));
			
			Scene scene = new Scene(root);
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.setTitle("Журнал групи " + betweenWindowsData);
			stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public void createGroupBtnAction()
	{
		TextInputDialog dialog = new TextInputDialog("назва нової групи");
		dialog.setTitle("Створення нової групи");
		dialog.setHeaderText("Для створення групи, введіть її назву та натисніть \"OK\"");
		dialog.setContentText("Будь ласка, введіть назву нової групи:");

		Optional<String> result = dialog.showAndWait();

		result.ifPresent(name -> Group.createNewGroup(name));
	}

	public void tableStudentsInfoBtnAction()
	{
		TableView<TableDat> table = tablGroup;

		String selectedStudentName = table.getSelectionModel().getSelectedItems().get(0).getName();

		Group selectedGroup = (Group) chbGroups.getSelectionModel().getSelectedItem();
		String groupName = selectedGroup.getName();
				
		//TODO finish
		System.out.println("Open new info window: " +
				selectedStudentName + " from " +
				groupName);
		
		betweenWindowsData = groupName + "\n" + selectedStudentName;
		
        try {

			VBox root = (VBox) FXMLLoader.load(getClass().getResource("/view/StudentInfoWindow.fxml"));
			
			Scene scene = new Scene(root);
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.setTitle("Інформація про " + selectedStudentName);
			stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public void groupStudentsInfoBtnAction()
	{
		//TODO when student info background will be ready and BD will be somewhat filled with
		
		System.out.println("Open new info window: " +
				lstGroupStudents.getSelectionModel().getSelectedItem().toString() + " from " +
				((Group)(chbGroupsCon.getSelectionModel().getSelectedItem())).getName());
		
		betweenWindowsData = ((Group)(chbGroupsCon.getSelectionModel().getSelectedItem())).getName() +
				"\n" + lstGroupStudents.getSelectionModel().getSelectedItem().toString();
		
        try {

			VBox root = (VBox) FXMLLoader.load(getClass().getResource("/view/StudentInfoWindow.fxml"));
			
			Scene scene = new Scene(root);
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.setTitle("Інформація про " + lstGroupStudents.getSelectionModel().getSelectedItem().toString());
			stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
	}

	public void groupStudentsChangeBtnAction()
	{
		// Create the custom dialog.
		String selectedGroupName = chbGroupsCon.getValue().toString();
		String selectedStudentName = lstGroupStudents.getSelectionModel().getSelectedItem().toString();
		Student selectedStudent = Student.getStudent(selectedStudentName, selectedGroupName);
		Dialog<List<String>> dialog = new Dialog<>();
		dialog.setTitle("Редагування учня '" + selectedStudentName
				+ "' в групі '"	+ selectedGroupName + "'");
		dialog.setHeaderText("Для редагування учня,"
				+ "\nвведіть його ім'я, стать та/або дату народження "
				+ "\nі натисніть 'OK'");
		// Set the icon (must be included in the project). TODO add image to dialogBoxes
		//dialog.setGraphic(new ImageView(this.getClass().getResource("login.png").toString()));

		// Set the button types.
		ButtonType okButtonType = new ButtonType("OK", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

		// Create the username and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField name = new TextField(selectedStudent.getName());
		name.setPromptText("Ім'я учня");
		DatePicker datePickerBirthday = new DatePicker();
		datePickerBirthday.setValue(LocalDate.parse(selectedStudent.getBirthday()));
		//CheckBox chbIsMale = new CheckBox();
		ToggleGroup tgGrGender = new ToggleGroup();

		RadioButton radbMale = new RadioButton("Хлопчик");
		radbMale.setToggleGroup(tgGrGender);

		RadioButton radbFemale = new RadioButton("Дівчинка");
		radbFemale.setToggleGroup(tgGrGender);

		if(selectedStudent.isMale())
		{
			radbMale.setSelected(true);
		}
		else
		{
			radbFemale.setSelected(true);
		}
		VBox vbxRadBut = new VBox();
		vbxRadBut.getChildren().addAll(radbMale, radbFemale);

		grid.add(new Label("Ім'я:"), 0, 0);
		grid.add(name, 1, 0);
		grid.add(new Label("День народження:"), 0, 1);
		grid.add(datePickerBirthday, 1, 1);
		grid.add(new Label("Стать:"), 0, 2);
		grid.add(vbxRadBut, 1, 2);

		// Enable/Disable login button depending on whether a username was entered.
		Node loginButton = dialog.getDialogPane().lookupButton(okButtonType);
		loginButton.setDisable(true);

		// Do some validation (using the Java 8 lambda syntax).
		name.textProperty().addListener((observable, oldValue, newValue) -> {
			loginButton.setDisable(newValue.trim().isEmpty());
		});

		dialog.getDialogPane().setContent(grid);

		// Request focus on the username field by default.
		Platform.runLater(() -> name.requestFocus());

		// Convert the result to a username-password-pair when the login button is clicked.
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == okButtonType) {
				List<String> returnResults = new LinkedList<>();
				returnResults.add(name.getText());
				returnResults.add(datePickerBirthday.getValue().toString());
				returnResults.add(radbMale.isSelected() ? "M" : "F");
				return returnResults;
			}
			return null;
		});

		Optional<List<String>> result = dialog.showAndWait();

		result.ifPresent(newStudent -> {
			System.out.println("Change student was:" + selectedStudent.getName()
				+ ", " + selectedStudent.getBirthday() + ", "
				+ selectedStudent.isMale() + ". New: " + newStudent.get(0)
			+ ", " + newStudent.get(1) + ", " + newStudent.get(2));

			Student.change(selectedStudent.getName(), newStudent.get(0), newStudent.get(1),
					newStudent.get(2).equals("M") ? true : false,
							selectedGroupName);
		});
		//TODO check if works
		fillGroupList(selectedGroupName);
	}

	public void groupStudentsAddBtnAction()
	{
		// Create the custom dialog.
		Dialog<List<String>> dialog = new Dialog<>();
		dialog.setTitle("Створення учня в групі '"
				+ chbGroupsCon.getSelectionModel().getSelectedItem().toString() + "'");
		dialog.setHeaderText("Для додавання нового учня,"
				+ "\nвведіть його ім'я, стать та дату народження "
				+ "\nі натисніть 'OK'");

		// Set the icon (must be included in the project). TODO add image to dialogBoxes
		//dialog.setGraphic(new ImageView(this.getClass().getResource("login.png").toString()));

		// Set the button types.
		ButtonType okButtonType = new ButtonType("OK", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

		// Create the username and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField name = new TextField();
		name.setPromptText("Ім'я учня");
		DatePicker datePickerBirthday = new DatePicker();
		datePickerBirthday.setValue(
				LocalDate.of(LocalDate.now().getYear() - 10,
						LocalDate.now().getMonthValue(),
						LocalDate.now().getDayOfMonth()));
		//CheckBox chbIsMale = new CheckBox();
		ToggleGroup tgGrGender = new ToggleGroup();

		RadioButton radbMale = new RadioButton("Хлопчик");
		radbMale.setToggleGroup(tgGrGender);
		radbMale.setSelected(true);

		RadioButton radBFemale = new RadioButton("Дівчинка");
		radBFemale.setToggleGroup(tgGrGender);

		VBox vbxRadBut = new VBox();
		vbxRadBut.getChildren().addAll(radbMale, radBFemale);

		grid.add(new Label("Ім'я:"), 0, 0);
		grid.add(name, 1, 0);
		grid.add(new Label("День народження:"), 0, 1);
		grid.add(datePickerBirthday, 1, 1);
		grid.add(new Label("Стать:"), 0, 2);
		grid.add(vbxRadBut, 1, 2);

		// Enable/Disable login button depending on whether a username was entered.
		Node loginButton = dialog.getDialogPane().lookupButton(okButtonType);
		loginButton.setDisable(true);

		// Do some validation (using the Java 8 lambda syntax).
		name.textProperty().addListener((observable, oldValue, newValue) -> {
			loginButton.setDisable(newValue.trim().isEmpty());
		});

		dialog.getDialogPane().setContent(grid);

		// Request focus on the username field by default.
		Platform.runLater(() -> name.requestFocus());

		// Convert the result to a username-password-pair when the login button is clicked.
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == okButtonType) {
				List<String> returnResults = new LinkedList<>();
				returnResults.add(name.getText());
				returnResults.add(datePickerBirthday.getValue().toString());
				returnResults.add(radbMale.isSelected() ? "M" : "F");
				return returnResults;
			}
			return null;
		});

		Optional<List<String>> result = dialog.showAndWait();

		result.ifPresent(newStudent -> {
			System.out.println("New student, name=" + newStudent.get(0)
			+ ", birthday=" + newStudent.get(1) + ", gender: " + newStudent.get(2));

			Student.create(newStudent.get(0), newStudent.get(1),
					newStudent.get(2).equals("M") ? true : false,
							chbGroupsCon.getSelectionModel().getSelectedItem().toString());
		});

		fillGroupList(((Group) chbGroupsCon.getValue()).getName());
	}

	private void groupSaveDaysBtnAction()
	{
		Group selectedGroup = (Group) chbGroupsCon.getSelectionModel().getSelectedItem();
		String oldName = selectedGroup.getName();
		String newName = txtfGroupName.getText();
		LocalDate startDate = dteGroupFrom.getValue();
		LocalDate finishDate = dteGroupTo.getValue();
		List<DayOfWeek> daysOfWeek = new LinkedList<DayOfWeek>();

		if(chkGroupMon.isSelected()) daysOfWeek.add(DayOfWeek.MONDAY);
		if(chkGroupTue.isSelected()) daysOfWeek.add(DayOfWeek.TUESDAY);
		if(chkGroupWed.isSelected()) daysOfWeek.add(DayOfWeek.WEDNESDAY);
		if(chkGroupThu.isSelected()) daysOfWeek.add(DayOfWeek.THURSDAY);
		if(chkGroupFri.isSelected()) daysOfWeek.add(DayOfWeek.FRIDAY);
		if(chkGroupSat.isSelected()) daysOfWeek.add(DayOfWeek.SATURDAY);
		if(chkGroupSun.isSelected()) daysOfWeek.add(DayOfWeek.SUNDAY);
		Group.changeGroup(oldName, newName, startDate, finishDate, daysOfWeek);
	}

	private void fillGroupList(String groupName)
	{
		List<Student> students = Student.getStudents(groupName);

		ListView<String> list = lstGroupStudents;
		lstGroupStudents.getItems().clear();
		ObservableList<String> items =FXCollections.observableArrayList ();

		students.sort(new Comparator<Student>() {
			@Override
			public int compare(Student st1, Student st2) {
				return st1.getName().compareTo(st2.getName());
			}
		});

		for(Student st : students)
		{
			items.add(st.getName());
		}

		list.setItems(items);
	}

	private void fillWorkDaysPane(String groupName)
	{
		Group gr = Group.getGroup(groupName);

		chkGroupMon.setSelected(false);
		chkGroupTue.setSelected(false);
		chkGroupWed.setSelected(false);
		chkGroupThu.setSelected(false);
		chkGroupFri.setSelected(false);
		chkGroupSat.setSelected(false);
		chkGroupSun.setSelected(false);
		
		for(DayOfWeek dow : gr.getDaysOfWeek())
		{
			switch (dow.getValue())
			{
			case 1: chkGroupMon.setSelected(true); break;
			case 2: chkGroupTue.setSelected(true); break;
			case 3: chkGroupWed.setSelected(true); break;
			case 4: chkGroupThu.setSelected(true); break;
			case 5: chkGroupFri.setSelected(true); break;
			case 6: chkGroupSat.setSelected(true); break;
			case 7: chkGroupSun.setSelected(true); break;
			}
		}
		txtfGroupName.setText(gr.getName());
		dteGroupFrom.setValue(gr.getStartDate());
		dteGroupTo.setValue(gr.getFinishDate());

	}

	private void chooseNewDb()
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Вибір бази даних");
		fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("DataBases", "*.db")
            );
		File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            DB.setDbLocation(file.getPath());
            txtfBdPath.setText(file.getPath());
        }
        
        fillChoiceBoxes();
	}
	
	private void createNewDb()
	{
		FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Створення нової бази даних");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("DataBases", "*.db")
            );
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            DB.createDb(file.getPath());
        }
	}
	
	private void initDbTextField()
	{
		try {
			String curFolder = new File(".").getCanonicalPath().toString();
			if(new File(curFolder, "students.db").exists())
			{
				txtfBdPath.setText((curFolder + File.separatorChar + "students.db"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void initialize() 
	{
		System.out.println("initialize of main controller");
		
		initDbTextField();
		fillChoiceBoxes();
		initTable();
		initCheckBox();
		
		btnRefresh.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				tablGroup.getColumns().clear();
				tablGroup.getItems().clear();
				initTable();
				fillTable(((Group) chbGroups.getValue()).getName());
			}
		});
		
		btnInfo.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				tableStudentsInfoBtnAction();
			}
		});
		
		btnSave.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				saveJournalBtnAction();
			}
		});

		btnFullJournal.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				openFullJournal();
			}
		});

		btnRefreshGroups.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				fillChoiceBoxes();
			}
		});

		btnCreateGroup.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				createGroupBtnAction();
			}
		});

		btnGroupStudentsInfo.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				groupStudentsInfoBtnAction();
			}
		});

		btnGroupStudentsChange.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				groupStudentsChangeBtnAction();
			}
		});

		btnGroupStudentsAdd.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				groupStudentsAddBtnAction();
			}
		});

		btnGroupDaysSave.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				groupSaveDaysBtnAction();
			}
		});

		btnChooseBd.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				chooseNewDb();
			}
		});
	
		btnCreateBd.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				createNewDb();
			}
		});
	
	}


}
