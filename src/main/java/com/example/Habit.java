package com.example;

import java.time.LocalDate;
import java.util.*;

public class Habit {
	
    private String name;
    private String description;
    private String frequency; // Ежедневно, Еженедельно, и т.д.
    private Set<LocalDate> completedDates;
    private LocalDate createdDate;
    
    public Habit(String name, String description, String frequency) {
        this.name = name;
        this.description = description;
        this.frequency = frequency;
        this.completedDates = new HashSet<>();
        this.createdDate = LocalDate.now();
    }
    
    // Конструктор для загрузки из файла
    public Habit(String name, String description, String frequency, Set<LocalDate> completedDates, LocalDate createdDate) {
        this.name = name;
        this.description = description;
        this.frequency = frequency;
        this.completedDates = completedDates != null ? completedDates : new HashSet<>();
        this.createdDate = createdDate != null ? createdDate : LocalDate.now();
    }
    
    // Геттеры и сеттеры
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getFrequency() {
        return frequency;
    }
    
    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }
    
    public Set<LocalDate> getCompletedDates() {
        return new HashSet<>(completedDates);
    }
    
    public LocalDate getCreatedDate() {
        return createdDate;
    }
    
    // Методы для работы с выполнением
    public void markCompleted(LocalDate date) {
        completedDates.add(date);
    }
    
    public void unmarkCompleted(LocalDate date) {
        completedDates.remove(date);
    }
    
    public boolean isCompletedOnDate(LocalDate date) {
        return completedDates.contains(date);
    }
    
    public boolean isCompletedToday() {
        return isCompletedOnDate(LocalDate.now());
    }
    
    // Вычисление процента успешного выполнения за последние 30 дней
    public double getSuccessPercentage() {
        LocalDate startDate = LocalDate.now().minusDays(29);
        LocalDate endDate = LocalDate.now();
        
        int totalDays = 0;
        int completedDays = 0;
        
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            if (!currentDate.isBefore(createdDate)) { // Считаем только дни после создания привычки
                totalDays++;
                if (isCompletedOnDate(currentDate)) {
                    completedDays++;
                }
            }
            currentDate = currentDate.plusDays(1);
        }
        
        if (totalDays == 0) {
            return 0.0;
        }
        
        return (double) completedDays / totalDays * 100.0;
    }
    
    // Получить количество выполненных дней за период
    public int getCompletedDaysInPeriod(LocalDate startDate, LocalDate endDate) {
        int count = 0;
        LocalDate currentDate = startDate;
        
        while (!currentDate.isAfter(endDate)) {
            if (isCompletedOnDate(currentDate)) {
                count++;
            }
            currentDate = currentDate.plusDays(1);
        }
        
        return count;
    }
    
    // Получить текущую серию выполнения (streak)
    public int getCurrentStreak() {
        int streak = 0;
        LocalDate currentDate = LocalDate.now();
        
        while (isCompletedOnDate(currentDate)) {
            streak++;
            currentDate = currentDate.minusDays(1);
        }
        
        return streak;
    }
    
    // Получить максимальную серию выполнения
    public int getMaxStreak() {
        if (completedDates.isEmpty()) {
            return 0;
        }
        
        // Сортируем даты
        LocalDate[] sortedDates = completedDates.toArray(new LocalDate[0]);
        java.util.Arrays.sort(sortedDates);
        
        int maxStreak = 1;
        int currentStreak = 1;
        
        for (int i = 1; i < sortedDates.length; i++) {
            if (sortedDates[i].equals(sortedDates[i-1].plusDays(1))) {
                currentStreak++;
            } else {
                maxStreak = Math.max(maxStreak, currentStreak);
                currentStreak = 1;
            }
        }
        
        return Math.max(maxStreak, currentStreak);
    }
    
    @Override
    public String toString() {
        return name + " (" + frequency + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Habit habit = (Habit) obj;
        return name.equals(habit.name) && createdDate.equals(habit.createdDate);
    }
    
    @Override
    public int hashCode() {
        return name.hashCode() + createdDate.hashCode();
    }
}
