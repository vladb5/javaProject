import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Note {
    private String text;
    private LocalDateTime createdAt;

    // Конструктор
    public Note(String text) {
        this.text = text;
        this.createdAt = LocalDateTime.now();
    }

    // Конструктор для загрузки из файла (без установки текущего времени)
    public Note(String text, LocalDateTime createdAt) {
        this.text = text;
        this.createdAt = createdAt;
    }

    // Геттеры
    public String getText() {
        return text;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Для удобного вывода
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return text + " | " + createdAt.format(formatter);
    }

    // Для использования в HashMap, если ключ - текст
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return Objects.equals(text, note.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }
}
