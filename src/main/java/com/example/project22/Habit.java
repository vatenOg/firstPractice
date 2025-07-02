package com.example.project22;

import javafx.beans.property.*;
import javafx.scene.paint.Color;
import java.time.LocalDate;

public class Habit {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty frequency = new SimpleStringProperty();
    private final ObjectProperty<Color> color = new SimpleObjectProperty<>(Color.GREEN);
    private final MapProperty<LocalDate, Boolean> completions = new SimpleMapProperty<>();

    public Habit(int id, String name, String description, String frequency, Color color) {
        this.id.set(id);
        this.name.set(name);
        this.description.set(description);
        this.frequency.set(frequency);
        this.color.set(color);
    }

    // Геттеры и сеттеры...
    public void markCompletion(LocalDate date, boolean completed) {
        completions.put(date, completed);
    }

    public double getCompletionPercentage() {
        if (completions.isEmpty()) return 0;
        long completedDays = completions.values().stream().filter(b -> b).count();
        return (double) completedDays / completions.size() * 100;
    }
}