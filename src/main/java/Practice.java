import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.paint.Color;
import java.time.LocalDate;

public class Practice {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty frequency = new SimpleStringProperty();
    private final ObjectProperty<Color> color = new SimpleObjectProperty<>(Color.GREEN);
    private final ObservableMap<LocalDate, Boolean> completions = FXCollections.observableHashMap();

    public Practice(int id, String name, String description, String frequency, Color color) {
        this.id.set(id);
        this.name.set(name);
        this.description.set(description);
        this.frequency.set(frequency);
        this.color.set(color);
    }

    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }
    public String getDescription() { return description.get(); }
    public void setDescription(String description) { this.description.set(description); }
    public String getFrequency() { return frequency.get(); }
    public void setFrequency(String frequency) { this.frequency.set(frequency); }
    public Color getColor() { return color.get(); }
    public void setColor(Color color) { this.color.set(color); }
    public ObservableMap<LocalDate, Boolean> getCompletions() { return completions; }
    
    public void markCompletion(LocalDate date, boolean completed) {
        completions.put(date, completed);
    }
    
    public double getCompletionPercentage() {
        if (completions.isEmpty()) return 0;
        long completedDays = completions.values().stream().filter(b -> b).count();
        return (double) completedDays / completions.size() * 100;
    }
}