package com.example;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class HabitManager {
    private List<Habit> habits;
    private static final String DATA_FILE = "habits.json";
    private Gson gson;
    
    public HabitManager() {
        this.habits = new ArrayList<>();
        
        // Настройка Gson для работы с LocalDate
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateAdapter());
        this.gson = gsonBuilder.setPrettyPrinting().create();
    }
    
    // Управление привычками
    public void addHabit(Habit habit) {
        habits.add(habit);
    }
    
    public void removeHabit(Habit habit) {
        habits.remove(habit);
    }
    
    public void updateHabit(Habit oldHabit, Habit newHabit) {
        int index = habits.indexOf(oldHabit);
        if (index != -1) {
            // Сохраняем историю выполнения и дату создания
            newHabit = new Habit(
                newHabit.getName(),
                newHabit.getDescription(),
                newHabit.getFrequency(),
                oldHabit.getCompletedDates(),
                oldHabit.getCreatedDate()
            );
            habits.set(index, newHabit);
        }
    }
    
    public List<Habit> getHabits() {
        return new ArrayList<>(habits);
    }
    
    public Habit getHabitByName(String name) {
        return habits.stream()
                .filter(habit -> habit.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
    
    // Статистические методы
    public int getTotalHabits() {
        return habits.size();
    }
    
    public int getCompletedTodayCount() {
        return (int) habits.stream()
                .filter(Habit::isCompletedToday)
                .count();
    }
    
    public double getOverallSuccessRate() {
        if (habits.isEmpty()) {
            return 0.0;
        }
        
        double totalPercentage = habits.stream()
                .mapToDouble(Habit::getSuccessPercentage)
                .sum();
        
        return totalPercentage / habits.size();
    }
    
    public Habit getBestPerformingHabit() {
        return habits.stream()
                .max(Comparator.comparingDouble(Habit::getSuccessPercentage))
                .orElse(null);
    }
    
    public Habit getWorstPerformingHabit() {
        return habits.stream()
                .min(Comparator.comparingDouble(Habit::getSuccessPercentage))
                .orElse(null);
    }
    
    public int getLongestCurrentStreak() {
        return habits.stream()
                .mapToInt(Habit::getCurrentStreak)
                .max()
                .orElse(0);
    }
    
    public Map<String, Integer> getHabitsByFrequency() {
        Map<String, Integer> frequencyMap = new HashMap<>();
        
        for (Habit habit : habits) {
            String frequency = habit.getFrequency();
            frequencyMap.put(frequency, frequencyMap.getOrDefault(frequency, 0) + 1);
        }
        
        return frequencyMap;
    }
    
    // Сохранение и загрузка данных
    public void saveHabits() {
        try {
            List<HabitData> habitDataList = new ArrayList<>();
            
            for (Habit habit : habits) {
                HabitData data = new HabitData();
                data.name = habit.getName();
                data.description = habit.getDescription();
                data.frequency = habit.getFrequency();
                data.createdDate = habit.getCreatedDate();
                data.completedDates = new ArrayList<>(habit.getCompletedDates());
                
                habitDataList.add(data);
            }
            
            try (FileWriter writer = new FileWriter(DATA_FILE)) {
                gson.toJson(habitDataList, writer);
            }
            
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении данных: " + e.getMessage());
        }
    }
    
    public void loadHabits() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return;
        }
        
        try (FileReader reader = new FileReader(DATA_FILE)) {
            Type listType = new TypeToken<List<HabitData>>(){}.getType();
            List<HabitData> habitDataList = gson.fromJson(reader, listType);
            
            if (habitDataList != null) {
                habits.clear();
                
                for (HabitData data : habitDataList) {
                    Set<LocalDate> completedDates = new HashSet<>(data.completedDates);
                    Habit habit = new Habit(
                        data.name,
                        data.description,
                        data.frequency,
                        completedDates,
                        data.createdDate
                    );
                    habits.add(habit);
                }
            }
            
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке данных: " + e.getMessage());
        }
    }
    
    // Вспомогательные классы для сериализации
    private static class HabitData {
        String name;
        String description;
        String frequency;
        LocalDate createdDate;
        List<LocalDate> completedDates;
    }
    
    // Адаптер для сериализации LocalDate
    private static class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        
        @Override
        public JsonElement serialize(LocalDate date, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(formatter.format(date));
        }
        
        @Override
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            return LocalDate.parse(json.getAsString(), formatter);
        }
    }
}