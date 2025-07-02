package com.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class HabitTrackerApp extends Application {
    private HabitManager habitManager;
    private ObservableList<Habit> displayedHabits;
    private ListView<Habit> habitListView;
    private VBox calendarContainer;
    private ComboBox<String> filterComboBox;
    
    @Override
    public void start(Stage primaryStage) {
        habitManager = new HabitManager();
        displayedHabits = FXCollections.observableArrayList();
        
        primaryStage.setTitle("Трекер привычек");
        
        // Главный контейнер
        BorderPane root = new BorderPane();
        
        // Левая панель с привычками
        VBox leftPanel = createLeftPanel();
        root.setLeft(leftPanel);
        
        // Центральная панель с календарем
        ScrollPane calendarScroll = createCalendarPanel();
        root.setCenter(calendarScroll);
        
        // Верхняя панель с кнопками
        HBox topPanel = createTopPanel(primaryStage);
        root.setTop(topPanel);
        
        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Загружаем данные
        habitManager.loadHabits();
        refreshHabitList();
        updateCalendar();
    }
    
    private VBox createLeftPanel() {
        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(10));
        leftPanel.setPrefWidth(300);
        leftPanel.setStyle("-fx-background-color: #f0f0f0;");
        
        Label titleLabel = new Label("Мои привычки");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Фильтр привычек (индивидуальное задание Щеглов)
        Label filterLabel = new Label("Фильтр по выполнению:");
        filterComboBox = new ComboBox<>();
        filterComboBox.getItems().addAll("Все привычки", "Выполненные сегодня", "Невыполненные сегодня");
        filterComboBox.setValue("Все привычки");
        filterComboBox.setOnAction(e -> applyFilter());
        
        habitListView = new ListView<>(displayedHabits);
        habitListView.setCellFactory(listView -> new HabitListCell());
        habitListView.setPrefHeight(400);
        
        leftPanel.getChildren().addAll(titleLabel, filterLabel, filterComboBox, habitListView);
        return leftPanel;
    }
    
    private ScrollPane createCalendarPanel() {
        calendarContainer = new VBox(10);
        calendarContainer.setPadding(new Insets(10));
        
        ScrollPane scrollPane = new ScrollPane(calendarContainer);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }
    
    private HBox createTopPanel(Stage primaryStage) {
        HBox topPanel = new HBox(10);
        topPanel.setPadding(new Insets(10));
        topPanel.setAlignment(Pos.CENTER);
        
        Button addButton = new Button("Добавить привычку");
        addButton.setOnAction(e -> showAddHabitDialog(primaryStage));
        
        Button editButton = new Button("Редактировать");
        editButton.setOnAction(e -> editSelectedHabit(primaryStage));
        
        Button deleteButton = new Button("Удалить");
        deleteButton.setOnAction(e -> deleteSelectedHabit());
        
        Button statsButton = new Button("Статистика");
        statsButton.setOnAction(e -> showStatisticsWindow(primaryStage));
        
        topPanel.getChildren().addAll(addButton, editButton, deleteButton, statsButton);
        return topPanel;
    }
    
    private void applyFilter() {
        String selectedFilter = filterComboBox.getValue();
        List<Habit> filteredHabits;
        
        switch (selectedFilter) {
            case "Выполненные сегодня":
                filteredHabits = habitManager.getHabits().stream()
                    .filter(habit -> habit.isCompletedToday())
                    .collect(Collectors.toList());
                break;
            case "Невыполненные сегодня":
                filteredHabits = habitManager.getHabits().stream()
                    .filter(habit -> !habit.isCompletedToday())
                    .collect(Collectors.toList());
                break;
            default:
                filteredHabits = habitManager.getHabits();
                break;
        }
        
        displayedHabits.clear();
        displayedHabits.addAll(filteredHabits);
    }
    
    private void showAddHabitDialog(Stage parentStage) {
        HabitDialog dialog = new HabitDialog(parentStage, "Добавить привычку", null);
        Optional<Habit> result = dialog.showAndWait();
        
        if (result.isPresent()) {
            habitManager.addHabit(result.get());
            habitManager.saveHabits();
            refreshHabitList();
            updateCalendar();
        }
    }
    
    private void editSelectedHabit(Stage parentStage) {
        Habit selectedHabit = habitListView.getSelectionModel().getSelectedItem();
        if (selectedHabit == null) {
            showAlert("Выберите привычку для редактирования");
            return;
        }
        
        HabitDialog dialog = new HabitDialog(parentStage, "Редактировать привычку", selectedHabit);
        Optional<Habit> result = dialog.showAndWait();
        
        if (result.isPresent()) {
            habitManager.updateHabit(selectedHabit, result.get());
            habitManager.saveHabits();
            refreshHabitList();
            updateCalendar();
        }
    }
    
    private void deleteSelectedHabit() {
        Habit selectedHabit = habitListView.getSelectionModel().getSelectedItem();
        if (selectedHabit == null) {
            showAlert("Выберите привычку для удаления");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Подтверждение");
        confirmAlert.setHeaderText("Удаление привычки");
        confirmAlert.setContentText("Вы уверены, что хотите удалить привычку \"" + selectedHabit.getName() + "\"?");
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            habitManager.removeHabit(selectedHabit);
            habitManager.saveHabits();
            refreshHabitList();
            updateCalendar();
        }
    }
    
    private void showStatisticsWindow(Stage parentStage) {
        StatisticsWindow statsWindow = new StatisticsWindow(parentStage, habitManager);
        statsWindow.show();
    }
    
    private void refreshHabitList() {
        displayedHabits.clear();
        displayedHabits.addAll(habitManager.getHabits());
        applyFilter(); // Применяем текущий фильтр
    }
    
    private void updateCalendar() {
        calendarContainer.getChildren().clear();
        
        if (displayedHabits.isEmpty()) {
            Label noHabitsLabel = new Label("Добавьте привычки для отображения календаря");
            noHabitsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
            calendarContainer.getChildren().add(noHabitsLabel);
            return;
        }
        
        for (Habit habit : displayedHabits) {
            VBox habitCalendar = createHabitCalendar(habit);
            calendarContainer.getChildren().add(habitCalendar);
        }
    }
    
    private VBox createHabitCalendar(Habit habit) {
        VBox habitBox = new VBox(10);
        habitBox.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-padding: 10;");
        
        // Заголовок с названием привычки и процентом выполнения
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Label nameLabel = new Label(habit.getName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Label percentLabel = new Label(String.format("%.1f%%", habit.getSuccessPercentage()));
        percentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        
        headerBox.getChildren().addAll(nameLabel, percentLabel);
        
        // Календарь за последние 30 дней
        GridPane calendar = new GridPane();
        calendar.setHgap(2);
        calendar.setVgap(2);
        
        LocalDate startDate = LocalDate.now().minusDays(29);
        
        for (int i = 0; i < 30; i++) {
            LocalDate date = startDate.plusDays(i);
            Rectangle dayRect = new Rectangle(20, 20);
            
            if (habit.isCompletedOnDate(date)) {
                dayRect.setFill(Color.GREEN);
            } else {
                dayRect.setFill(Color.LIGHTGRAY);
            }
            
            dayRect.setStroke(Color.BLACK);
            dayRect.setStrokeWidth(0.5);
            
            // Подсказка с датой
            Tooltip tooltip = new Tooltip(date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            Tooltip.install(dayRect, tooltip);
            
            // Обработчик клика для отметки выполнения
            dayRect.setOnMouseClicked(e -> {
                if (date.isAfter(LocalDate.now())) {
                    showAlert("Нельзя отмечать будущие даты");
                    return;
                }
                
                if (habit.isCompletedOnDate(date)) {
                    habit.unmarkCompleted(date);
                    dayRect.setFill(Color.LIGHTGRAY);
                } else {
                    habit.markCompleted(date);
                    dayRect.setFill(Color.GREEN);
                }
                
                habitManager.saveHabits();
                refreshHabitList();
                
                // Обновляем процент
                percentLabel.setText(String.format("%.1f%%", habit.getSuccessPercentage()));
            });
            
            calendar.add(dayRect, i % 10, i / 10);
        }
        
        Label calendarLabel = new Label("Календарь выполнения (последние 30 дней):");
        calendarLabel.setStyle("-fx-font-size: 12px;");
        
        habitBox.getChildren().addAll(headerBox, calendarLabel, calendar);
        return habitBox;
    }
    
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

// Кастомная ячейка для списка привычек
class HabitListCell extends ListCell<Habit> {
    @Override
    protected void updateItem(Habit habit, boolean empty) {
        super.updateItem(habit, empty);
        
        if (empty || habit == null) {
            setText(null);
            setStyle("");
        } else {
            setText(habit.getName() + " (" + habit.getFrequency() + ")");
            
            // Выделяем выполненные сегодня привычки
            if (habit.isCompletedToday()) {
                setStyle("-fx-background-color: #e8f5e8;");
            } else {
                setStyle("");
            }
        }
    }
}