package controllerdialog;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import model.Practice;

public class PracticeDialogController {
    @FXML private TextField nameField;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<String> frequencyCombo;
    @FXML private ColorPicker colorPicker;
    
    private Practice practice;
    private boolean okClicked = false;
    
    @FXML
    private void initialize() {
        frequencyCombo.getItems().addAll("Daily", "Weekly", "Monthly");
    }
    
    public void setPractice(Practice practice) {
        this.practice = practice;
        nameField.setText(practice.getName());
        descriptionArea.setText(practice.getDescription());
        frequencyCombo.setValue(practice.getFrequency());
        colorPicker.setValue(practice.getColor());
    }
    
    public boolean isOkClicked() { return okClicked; }
    public Practice getPractice() { return practice; }
    
    @FXML
    private void handleOk() {
        if (validateInput()) {
            if (practice == null) practice = new Practice(0, "", "", "", Color.GREEN);
            
            practice.setName(nameField.getText());
            practice.setDescription(descriptionArea.getText());
            practice.setFrequency(frequencyCombo.getValue());
            practice.setColor(colorPicker.getValue());
            
            okClicked = true;
            closeDialog();
        }
    }
    
    @FXML
    private void handleCancel() {
        closeDialog();
    }
    
    private boolean validateInput() {
        StringBuilder errors = new StringBuilder();
        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            errors.append("Name is required!\n");
        }
        if (frequencyCombo.getValue() == null) {
            errors.append("Frequency is required!\n");
        }
        
        if (errors.length() > 0) {
            showAlert("Invalid Input", "Please correct errors", errors.toString());
            return false;
        }
        return true;
    }
    
    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void closeDialog() {
        nameField.getScene().getWindow().hide();
    }
}