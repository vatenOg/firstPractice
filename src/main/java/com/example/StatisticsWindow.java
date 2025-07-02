package com.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.Map;

public class StatisticsWindow extends Stage {
    private HabitManager habitManager;
    
    public StatisticsWindow(Stage parentStage, HabitManager habitManager) {
        this.habitManager = habitManager;
        
        initOwner(parentStage);
        setTitle("Статистика привычек");
        setWidth(800);
        setHeight(600);
        
        createContent();
    }
    
    private void createContent() {
        BorderPane root = new BorderPane();
        
        // Заголовок
        Label titleLabel = new Label("Статистика по всем привычкам");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-padding: 20px;");
        
        HBox titleBox = new HBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        root.setTop(titleBox);
        
        // Основной контент
        ScrollPane scrollPane = new ScrollPane(createStatisticsContent());
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        root.setCenter(scrollPane);
        
        Scene scene = new Scene(root);
        setScene(scene);
    }
    
    private VBox createStatisticsContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        
        // Общая статистика
        content.getChildren().add(createGeneralStatistics());
        
        // Статистика по частоте привычек
        content.getChildren().add(createFrequencyChart());
        
        // Детальная статистика по каждой привычке
        content.getChildren().add(createDetailedStatistics());
        
        return content;
    }
    
    private VBox createGeneralStatistics() {
        VBox generalStats = new VBox(10);
        generalStats.setStyle("-fx-border-color: #cccccc; -fx-border-width: 2; -fx-padding: 15; -fx-background-color: #f9f9f9;");
        
        Label sectionTitle = new Label("Общая статистика");
        sectionTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(30);
        statsGrid.setVgap(10);
        
        // Вычисляем статистику
        int totalHabits = habitManager.getTotalHabits();
        int completedToday = habitManager.getCompletedTodayCount();
        double overallSuccess = habitManager.getOverallSuccessRate();
        int longestStreak = habitManager.getLongestCurrentStreak();
        
        Habit bestHabit = habitManager.getBestPerformingHabit();
        Habit worstHabit = habitManager.getWorstPerformingHabit();
        
        // Добавляем статистику в сетку
        addStatItem(statsGrid, 0, 0, "Всего привычек:", String.valueOf(totalHabits));
        addStatItem(statsGrid, 0, 1, "Выполнено сегодня:", String.valueOf(completedToday));
        addStatItem(statsGrid, 0, 2, "Общий процент успеха:", String.format("%.1f%%", overallSuccess));
        addStatItem(statsGrid, 0, 3, "Самая длинная серия:", String.valueOf(longestStreak) + " дней");
        
        if (bestHabit != null) {
            addStatItem(statsGrid, 1, 0, "Лучшая привычка:", 
                bestHabit.getName() + " (" + String.format("%.1f%%", bestHabit.getSuccessPercentage()) + ")");
        }
        
        if (worstHabit != null) {
            addStatItem(statsGrid, 1, 1, "Требует внимания:", 
                worstHabit.getName() + " (" + String.format("%.1f%%", worstHabit.getSuccessPercentage()) + ")");
        }
        
        generalStats.getChildren().addAll(sectionTitle, statsGrid);
        return generalStats;
    }
    
    private void addStatItem(GridPane grid, int col, int row, String label, String value) {
        Label labelNode = new Label(label);
        labelNode.setStyle("-fx-font-weight: bold;");
        
        Label valueNode = new Label(value);
        valueNode.setStyle("-fx-text-fill: #2c5282;");
        
        VBox itemBox = new VBox(5);
        itemBox.getChildren().addAll(labelNode, valueNode);
        
        grid.add(itemBox, col, row);
    }
    
    private VBox createFrequencyChart() {
        VBox chartContainer = new VBox(10);
        chartContainer.setStyle("-fx-border-color: #cccccc; -fx-border-width: 2; -fx-padding: 15;");
        
        Label chartTitle = new Label("Распределение привычек по частоте");
        chartTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Создаем столбчатую диаграмму
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Частота");
        yAxis.setLabel("Количество привычек");
        
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Частота выполнения привычек");
        barChart.setLegendVisible(false);
        barChart.setPrefHeight(300);
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        
        Map<String, Integer> frequencyMap = habitManager.getHabitsByFrequency();
        for (Map.Entry<String, Integer> entry : frequencyMap.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        
        barChart.getData().add(series);
        
        chartContainer.getChildren().addAll(chartTitle, barChart);
        return chartContainer;
    }
    
    private VBox createDetailedStatistics() {
        VBox detailedStats = new VBox(10);
        detailedStats.setStyle("-fx-border-color: #cccccc; -fx-border-width: 2; -fx-padding: 15;");
        
        Label sectionTitle = new Label("Детальная статистика по привычкам");
        sectionTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        GridPane habitsGrid = new GridPane();
        habitsGrid.setHgap(20);
        habitsGrid.setVgap(15);
        
        // Заголовки таблицы
        Label[] headers = {
            new Label("Название"),
            new Label("Частота"),
            new Label("Успех (%)"),
            new Label("Текущая серия"),
            new Label("Макс. серия"),
            new Label("Статус")
        };
        
        for (int i = 0; i < headers.length; i++) {
            headers[i].setStyle("-fx-font-weight: bold; -fx-background-color: #e2e8f0; -fx-padding: 8px;");
            habitsGrid.add(headers[i], i, 0);
        }
        
        // Данные по привычкам
        int row = 1;
        for (Habit habit : habitManager.getHabits()) {
            Label nameLabel = new Label(habit.getName());
            Label frequencyLabel = new Label(habit.getFrequency());
            Label successLabel = new Label(String.format("%.1f%%", habit.getSuccessPercentage()));
            Label currentStreakLabel = new Label(String.valueOf(habit.getCurrentStreak()));
            Label maxStreakLabel = new Label(String.valueOf(habit.getMaxStreak()));
            
            Label statusLabel = new Label(habit.isCompletedToday() ? "✓ Выполнено" : "○ Не выполнено");
            statusLabel.setStyle(habit.isCompletedToday() ? 
                "-fx-text-fill: green; -fx-font-weight: bold;" : 
                "-fx-text-fill: red;");
            
            // Стиль для строк
            String rowStyle = "-fx-padding: 8px; -fx-background-color: " + 
                (row % 2 == 0 ? "#f7fafc" : "white") + ";";
            
            nameLabel.setStyle(rowStyle);
            frequencyLabel.setStyle(rowStyle);
            successLabel.setStyle(rowStyle);
            currentStreakLabel.setStyle(rowStyle);
            maxStreakLabel.setStyle(rowStyle);
            statusLabel.setStyle(statusLabel.getStyle() + rowStyle);
            
            habitsGrid.add(nameLabel, 0, row);
            habitsGrid.add(frequencyLabel, 1, row);
            habitsGrid.add(successLabel, 2, row);
            habitsGrid.add(currentStreakLabel, 3, row);
            habitsGrid.add(maxStreakLabel, 4, row);
            habitsGrid.add(statusLabel, 5, row);
            
            row++;
        }
        
        if (habitManager.getHabits().isEmpty()) {
            Label noDataLabel = new Label("Нет данных для отображения");
            noDataLabel.setStyle("-fx-text-fill: gray; -fx-font-style: italic; -fx-padding: 20px;");
            detailedStats.getChildren().addAll(sectionTitle, noDataLabel);
        } else {
            detailedStats.getChildren().addAll(sectionTitle, habitsGrid);
        }
        
        return detailedStats;
    }
}