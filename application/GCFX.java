package application;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class GCFX extends Application {
	// Declare components that require class scope
	Label lblNumber;
	TextField txtfNumber;
	TextArea txtMain;
	Button btnChangePhoto, btnAddGrades, btnExport;
	Label lblModule;
	Label CAGrade;
	Label Grade;
	Label weight1;
	Label weight2;
	TextField marks1;
	TextField marks2;
	ComboBox<String> selection= new ComboBox<String>();
	int w1;
	int w2;
	int m1value;
	int m2value;
	TextField weigh1;
	TextField weigh2;
	
	
	Button btncancel;
	Button btncheckGrade;
	Button btncheckresults;
	
	//Profile Image
	ImageView imvProfilePhoto;
	Image img;
	
	//Store chosen student number as a global variable
	String chosenNum = ""; 
	Stage ndw;
	//Your Details

	// Constructor
	public GCFX() {
		// Instantiate components using keyword 'new'
		img = new Image("/Assets/profile.jpg");
		imvProfilePhoto= new ImageView(img);
		txtMain= new TextArea();
		txtMain.setEditable(false);
		txtMain.setFocusTraversable(false);
		btnChangePhoto= new Button("Change Photo");
		btnAddGrades= new Button("Add Grades");
		btncheckresults= new Button("check results");
		btnExport= new Button("Export Results");
		lblNumber= new Label("Student Number");
		txtfNumber= new TextField();
		lblModule= new Label("Module");
		CAGrade= new Label("CAGrade");
		Grade= new Label("Exam/Project Grade");
		weight1= new Label("Weight");
		weight2= new Label("Weight");
		marks1= new TextField();
		marks2= new TextField();
		weigh1= new TextField();
		weigh2= new TextField();
		
		selection.getItems().addAll("Big Data Analytics", "Information Retrival And Webserach","Big Data management and Analytics","Data Mining algorithms and techniquies","Applied Data science","Cloud computing");
		selection.setValue("Please choose the module");
		weigh1.setText("50");
		weigh2.setText("50");
		btncancel= new Button("Cancel");
		btncheckGrade= new Button("Check Grade");
		
		
	} 

	// Event handling
	@Override 
	public void init() {
		
		// Handle events on check grades button (dialog)
		btncheckresults.setOnAction(e -> {
		    String studentId = txtfNumber.getText().trim();

		    if(studentId.isEmpty()) {
		        Alert alert = new Alert(Alert.AlertType.ERROR);
		        alert.setTitle("Error");
		        alert.setContentText("Please enter a student number");
		        alert.showAndWait();
		        return;
		    }

		    // Fetch latest txtMain content
		    String details = GradeDAO.getLatestDetails(studentId);
		    if(details != null) {
		        txtMain.setText(details); // populate txtMain
		    } else {
		        txtMain.clear();
		        Alert alert = new Alert(Alert.AlertType.INFORMATION);
		        alert.setTitle("No Results Found");
		        alert.setContentText("No stored results for this student");
		        alert.showAndWait();
		    }

		    // Fetch profile photo
		    String imagePath = StudentDAO.getImagePath(studentId);
		    if(imagePath != null) {
		        File imgFile = new File(imagePath);
		        if(imgFile.exists()) imvProfilePhoto.setImage(new Image(imgFile.toURI().toString()));
		    } else {
		        // optional: reset to default photo if none exists
		        imvProfilePhoto.setImage(new Image("/Assets/profile.jpg"));
		    }

		    chosenNum = studentId; // update chosenNum
		});

		  btnAddGrades.setOnAction( e->{
			  String studentNumber=txtfNumber.getText();
			  if(studentNumber.matches("\\d{8,}")) {
				  newdialogwindow();
			  }
			  else {
				  showalert1();
				  txtfNumber.clear();
			  }
			  
		  });
		// Handle events on the export button (save as txt)
		btnExport.setOnAction(e->exportresults());
			
	
		// Handle events on the change photo button (system dialog)
		btnChangePhoto.setOnAction(e -> {
		    String studentId = txtfNumber.getText().trim();

		    if(studentId.isEmpty()) {
		        Alert alert = new Alert(Alert.AlertType.ERROR);
		        alert.setTitle("Error");
		        alert.setContentText("Enter a student number before changing photo");
		        alert.showAndWait();
		        return;
		    }

		    FileChooser fc = new FileChooser();
		    fc.setTitle("Choose your profile picture");
		    fc.getExtensionFilters().add(
		        new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
		    );
		    File chosenFile = fc.showOpenDialog(null);

		    if (chosenFile != null) {
		        try {
		            // Copy image to project folder
		            String savedPath = StudentDAO.copyProfileImage(chosenFile, studentId);

		            if(StudentDAO.exists(studentId)) {
		                // Update existing student photo
		                StudentDAO.updateImage(studentId, savedPath);
		            } else {
		                // Insert new student with default name (you can modify to get name from a field)
		                String defaultName = "Student " + studentId;
		                StudentDAO.insert(studentId, defaultName, savedPath);
		            }

		            // Update ImageView
		            imvProfilePhoto.setImage(new Image(new File(savedPath).toURI().toString()));

		            Alert success = new Alert(Alert.AlertType.INFORMATION);
		            success.setTitle("Photo Updated");
		            success.setContentText("Profile photo saved successfully!");
		            success.showAndWait();

		        } catch (Exception ex) {
		            ex.printStackTrace();
		            Alert alert = new Alert(Alert.AlertType.ERROR);
		            alert.setTitle("Error");
		            alert.setContentText("Failed to save profile image");
		            alert.showAndWait();
		        }
		    }
		});


		  
		  btncancel.setOnAction(e->{
			  Platform.exit();
		  });
		  
		  btncheckGrade.setOnAction(e->{
			  if(marks1.getText().isEmpty() || marks2.getText().isEmpty() ) {
				  showalert2();
			  }
			  if( selection.getValue()==null || selection.getValue().equalsIgnoreCase("please choose the module")){
				  Alert alert =new Alert(Alert.AlertType.ERROR);
				  alert.setTitle("Error");
				  alert.setContentText("please select the mocule before grades check");
				  alert.showAndWait();
				  return;
				  
			  }
			  else {
				  String m1= marks1.getText();
				  m1value= Integer.parseInt(m1);
				  String m2= marks2.getText();
				  m2value= Integer.parseInt(m2);
				  String  wei1= weigh1.getText();
				  System.out.println(w1);
				   w1=Integer.parseInt(wei1);
				  System.out.println(w1);
				  String wei2= weigh2.getText();
				  w2=Integer.parseInt(wei2);
				  System.out.println(w2);
				  if(m1value>=1 && m1value <99 && m2value>=1 && m2value<99 && w1>=1 &&w1<99 && w2>=1 && w2<99) {
					  gradecalculation();
					  marks1.clear();
					  marks2.clear();
					  
				  }
			  }
			  ndw.close();
		  });
		  
		  
	
	}
	
	

	private void exportresults() {
		//  Auto-generated method stub
		if(txtMain.getText().trim().isEmpty()) {
			Alert empty= new Alert(Alert.AlertType.ERROR);
			empty.setTitle("Nothing to export");
			empty.setHeaderText("No results found");
			empty.setContentText("No calculated results to export");
			empty.showAndWait();
		}
		else
		{
			String details= txtMain.getText();
			GradeDAO.insertGrade(chosenNum, details);
		FileChooser fc= new FileChooser();
		fc.setTitle("choose where to save the file");
		fc.setInitialFileName("Results For "+ txtfNumber.getText()+".txt");
		ExtensionFilter extFilt= new FileChooser.ExtensionFilter("Text files(*.txt)","*.txt");
		fc.getExtensionFilters().add(extFilt);
		File chosenfile= fc.showSaveDialog(null);
		if(chosenfile!=null) {
			try {
				BufferedWriter buf= new BufferedWriter(new FileWriter(chosenfile));
				buf.write(txtMain.getText());
				buf.close();
				successalert(chosenfile);
				txtMain.clear();
				imvProfilePhoto.setImage(new Image("/Assets/profile.jpg"));
				txtfNumber.clear();
				
			}
			catch(Exception oopsie) {
				System.err.println("something went wrong with file choosen");
				oopsie.printStackTrace();
			}
		}
		}
	}

	

	

	private void successalert(File file) {
		//  Auto-generated method stub
		Alert successAlert= new Alert(Alert.AlertType.INFORMATION);
		successAlert.setTitle("Export Successful");
		successAlert.setHeaderText("Results Exported successfully");
		successAlert.setContentText("File saved as :" + file.getName()+"\nlocation: "+file.getParent());
		successAlert.showAndWait();
		//txtMain.clear();
		
	}

	private void gradecalculation() {
		//  Auto-generated method stub
		double percentage= ((m1value*w1)+(m2value*w2))/100;
		if(!txtfNumber.getText().matches(chosenNum)) {
			txtMain.clear();
			txtMain.appendText(selection.getValue()+"\n");
			txtMain.appendText("CA :"+marks1.getText()+"\t");
			txtMain.appendText("Exam  :"+ marks2.getText() +"\n");
			txtMain.appendText("Overall Grade :" +percentage +"%" +"\n");
			if(percentage<40) 
				txtMain.appendText("*** Module Fail. Repeat Assignment and Exam for next Sitting ***"+"\n");
			else if(m1value<40) 
				txtMain.appendText("*** Module Fail. Repeat the Exam in next Sitting ***"+ "\n");
			else if(m2value<40) 
				txtMain.appendText("*** Module Fail . Repeat the Exam and Assignment ***" +"\n");
			else 
				txtMain.appendText("*** Module Pass. ***"  +"\n");
			chosenNum=txtfNumber.getText();
		}
		else
		{
			txtMain.appendText("====================================="+"\n");
			txtMain.appendText(selection.getValue()+"\n");
			txtMain.appendText("CA :"+marks1.getText()+"\t");
			txtMain.appendText("Exam  :"+ marks2.getText() +"\n");
			txtMain.appendText("  Overall Grade :" +percentage +"%" +"\n");
			if(m1value<40) 
				txtMain.appendText("*** Module Fail. Repeat Assignment for next Sitting ***"+"\n");
			else if(m2value<40) 
				txtMain.appendText("*** Module Fail. Repeat the Exam in next Sitting ***"+ "\n");
			else if(m1value <40 && m2value<40) 
				txtMain.appendText("*** Module Fail . Repeat the Exam and Assignment ***" +"\n");
			else 
				txtMain.appendText("*** Module Pass. ***"+"\n");
		}
		
	}

	private void showalert2() {
		//  Auto-generated method stub
		Alert alert= new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Error");
		alert.setContentText("Values should not be left empty");
		alert.showAndWait();
		
	}

	private void newdialogwindow() {
		//  Auto-generated method stub
		  ndw= new Stage();
		  ndw.setTitle("Please enter the marks for student " + txtfNumber.getText());
		  ndw.setHeight(300);
		  ndw.setWidth(750);
		  GridPane gp= new GridPane();
		  gp.setHgap(15);
		  gp.setVgap(10);
		  gp.setPadding(new Insets(10,10,10,10));
		  
		  gp.add(lblModule, 0, 0);
		  gp.add(selection, 1,0);
		  gp.add(CAGrade, 0,1);
		  gp.add(marks1, 1, 1);
		  gp.add(weight1, 2, 1);
		  gp.add(weigh1, 3, 1);
		  gp.add(Grade, 0, 2);
		  gp.add(marks2, 1, 2);
		  gp.add(weight2, 2, 2);
		  gp.add(weigh2, 3, 2);
		  gp.add(btncancel, 3, 3);
		  gp.add(btncheckGrade, 4, 3);
		  
		  
		  Scene s1= new Scene(gp);
		  ndw.setScene(s1);
		  ndw.show();
		
	}

	private void showalert1() {
		//  Auto-generated method stub
		Alert alert= new Alert(Alert.AlertType.ERROR);
		alert.setTitle("error in loading");
		alert.setContentText("student number should not be less than 8...... please enter correct student number");
		alert.showAndWait();
		
		
	}
	
	
	

	// Window setup and layouts
	@Override
	public void start(Stage primaryStage) throws Exception {
		//Manage window title and size
		primaryStage.setTitle("Grades Calculator Project");

		// Manage default window width and height
		   primaryStage.setWidth(700);
		   primaryStage.setHeight(500);
		// Create main layout
		  HBox main= new HBox();
		  
		// Create sub-layouts
		 VBox vbleft= new VBox();
		 VBox vbright= new VBox();
		 VBox vrightNumber= new VBox();
		 HBox buttons= new HBox();
		 
		// Add components to sub-layouts
		 buttons.getChildren().add(btnAddGrades);
		 buttons.getChildren().add(btncheckresults);
		 vrightNumber.setMaxWidth(Double.MAX_VALUE);
		 buttons.spacingProperty().bind(
				    Bindings.max(10, vrightNumber.widthProperty().multiply(0.2))
				);
		 
		 vbright.getChildren().add(imvProfilePhoto);
		 vbright.getChildren().add(btnChangePhoto);
		 
		 vrightNumber.getChildren().add(lblNumber);
		 vrightNumber.getChildren().add(txtfNumber);
		 vrightNumber.getChildren().add(buttons);
		 vrightNumber.setSpacing(5);
		 vrightNumber.setAlignment(Pos.TOP_LEFT);
		
		 vbright.getChildren().add(vrightNumber);
		 //extra property added from oracle website
		 txtMain.styleProperty().bind(Bindings.concat("-fx-font-size: ",primaryStage.widthProperty().divide(50).asString(), "px;"));		
		 // Add sub-layouts to main layout
		 vbleft.getChildren().addAll(txtMain,btnExport);
		// Manage Padding and Spacing for Layouts
		 vbleft.setAlignment(Pos.TOP_LEFT);
		 vbright.setAlignment(Pos.BASELINE_CENTER);
		// Manage Alignment for Layouts
		  
		  vbleft.setPadding(new Insets(20,20,10,20));
		  vbright.setPadding(new Insets(20,10,5,10));
		  vbleft.setSpacing(5);
		//  vbright.setSpacing(10);
		  
		// Bind ImageView to 1/3rd of the width of the main window and preserve its ratio
		imvProfilePhoto.fitWidthProperty().bind(primaryStage.widthProperty().divide(3));
		imvProfilePhoto.fitHeightProperty().bind(primaryStage.heightProperty().divide(3));
		imvProfilePhoto.setPreserveRatio(true);
		// Bind width of changePhoto button to width of ImageView
		btnChangePhoto.prefWidthProperty().bind(imvProfilePhoto.fitWidthProperty());
		btnChangePhoto.prefWidthProperty().bind(Bindings.createDoubleBinding(()->imvProfilePhoto.getBoundsInParent().getWidth(),imvProfilePhoto.boundsInParentProperty()));
		HBox.setHgrow(vbleft,Priority.ALWAYS);
		HBox.setHgrow(vbright, Priority.ALWAYS);
		vbleft.setMaxWidth(Double.MAX_VALUE);
		vbright.setMaxWidth(Double.MAX_VALUE);
		vbleft.prefWidthProperty().bind(primaryStage.widthProperty().multiply(0.65));
		vbright.prefWidthProperty().bind(primaryStage.widthProperty().multiply(0.35));
		HBox.setHgrow(txtMain, Priority.ALWAYS);
		txtMain.setMaxWidth(Double.MAX_VALUE);
		
		
		VBox.setVgrow(txtMain,Priority.ALWAYS);
		txtMain.setMaxHeight(Double.MAX_VALUE);
		txtMain.setPrefSize(400, 300);
		
		//Create Scene (main layout as argument)
		 main.getChildren().add(vbleft);
		 main.getChildren().add(vbright);
		 Scene s= new Scene(main);
		// Apply a Stylesheet (global style of UI)
		 btnChangePhoto.setStyle("-fx-background-color:#4CAF50;-fx-text-fill:white");
		 btnExport.setStyle("-fx-background-color:#4CAF50;-fx-text-fill:white");
		 btnAddGrades.setStyle("-fx-background-color:#4CAF50;-fx-text-fill:white");
		 btncancel.setStyle("-fx-background-color:#F44336;-fx-text-fill:white");
		 btncheckGrade.setStyle("-fx-background-color:#4CAF50;-fx-text-fill:white");
		 btncheckresults.setStyle("-fx-background-color:#4CAF50;-fx-text-fill:white");
		 
		// Set scene
		primaryStage.setScene(s);
		// Show stage
		primaryStage.show();
	}

	// Launch application
	public static void main(String[] args) {
		launch(args);
		try {
			Database.init();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}