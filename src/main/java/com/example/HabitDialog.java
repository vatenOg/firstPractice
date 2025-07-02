package com.example;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class HabitDialog extends Dialog<Habit> {
    private TextField nameField;
    private TextArea descriptionArea;
    private ComboBox<String> frequencyComboBox;
    
    public HabitDialog(Stage parentStage, String title, Habit existingHabit) {
        setTitle(title);
        setHeaderText(null);
        
        // Устанавливаем владельца диалога
        initOwner(parentStage);
        
        // Создаем кнопки
        ButtonType saveButtonType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Создаем форму
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        // Поле названия
        nameField = new TextField();
        nameField.setPromptText("Название привычки");
        nameField.setPrefWidth(300);
        
        // Поле описания
        descriptionArea = new TextArea();
        descriptionArea.setPromptText("Описание привычки");
        descriptionArea.setPrefRowCount(3);
        descriptionArea.setPrefWidth(300);
        descriptionArea.setWrapText(true);
        
        // Выбор частоты
        frequencyComboBox = new ComboBox<>();
        frequencyComboBox.getItems().addAll(
            "Ежедневно",
            "Еженедельно", 
            "Несколько раз в неделю",
            "Ежемесячно",
            "По необходимости"
        );
        frequencyComboBox.setValue("Ежедневно");
        frequencyComboBox.setPrefWidth(300);
        
        // Заполняем поля, если редактируем существующую привычку
        if (existingHabit != null) {
            nameField.setText(existingHabit.getName());
            descriptionArea.setText(existingHabit.getDescription());
            frequencyComboBox.setValue(existingHabit.getFrequency());
        }
        
        // Добавляем элементы в сетку
        grid.add(new Label("Название:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Описание:"), 0, 1);
        grid.add(descriptionArea, 1, 1);
        grid.add(new Label("Частота:"), 0, 2);
        grid.add(frequencyComboBox, 1, 2);
        
        // Валидация
        Button saveButton = (Button) getDialogPane().lookupButton(saveButtonType);
        saveButton.setOnAction(event -> {
            if (validateInput()) {
                // Валидация прошла, диалог закроется автоматически
            } else {
                // Предотвращаем закрытие диалога
                event.consume();
            }
        });
        
        getDialogPane().setContent(grid);
        
        // Устанавливаем фокус на поле названия
        nameField.requestFocus();
        
        // Преобразователь результата
        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType && validateInput()) {
                return new Habit(
                    nameField.getText().trim(),
                    descriptionArea.getText().trim(),
                    frequencyComboBox.getValue()
                );
            }
            return null;
        });
    }
    
    private boolean validateInput() {
        String name = nameField.getText().trim();
        
        if (name.isEmpty()) {
            showValidationAlert("Название привычки не может быть пустым!");
            nameField.requestFocus();
            return false;
        }
        
        if (name.length() > 100) {
            showValidationAlert("Название привычки не должно превышать 100 символов!");
            nameField.requestFocus();
            return false;
        }
        
        if (frequencyComboBox.getValue() == null) {
            showValidationAlert("Выберите частоту выполнения!");
            frequencyComboBox.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void showValidationAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка валидации");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}