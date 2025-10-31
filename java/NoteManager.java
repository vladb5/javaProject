import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.lang.reflect.Method;

public class NoteManager {

    private List<Note> notesList = new ArrayList<>();
    // Можно использовать Map, если текст заметок гарантированно уникален,
    // или если хотите быстрый поиск по тексту.
    // Для простоты примера, в основном используем List.
    // private Map<String, Note> notesMap = new HashMap<>();

    private static final String FILENAME = "notes.txt";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        NoteManager manager = new NoteManager();
        manager.loadNotesFromFile(); // Автозагрузка при старте

        // Проверка аннотации
        try {
            Method saveMethod = NoteManager.class.getMethod("saveToFile");
            if (saveMethod.isAnnotationPresent(Task.class)) {
                System.out.println("Аннотация к методу saveToFile: " + saveMethod.getAnnotation(Task.class).value());
            }
        } catch (NoSuchMethodException e) {
            System.err.println("Ошибка: Метод saveToFile не найден.");
        }

        manager.runMenu();
    }

    // Метод для сохранения в файл, помеченный аннотацией
    @Task("Сохранение заметок в файл")
    public void saveToFile() {
        Path path = Paths.get(FILENAME);
        List<String> linesToSave = notesList.stream()
                .map(note -> note.getText() + " | " + note.getCreatedAt().format(FORMATTER))
                .collect(Collectors.toList());

        try {
            Files.write(path, linesToSave, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("Сохранено в " + FILENAME);
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении файла: " + e.getMessage());
        }
    }

    // Метод для загрузки из файла
    public void loadNotesFromFile() {
        Path path = Paths.get(FILENAME);
        if (Files.exists(path)) {
            try {
                List<String> lines = Files.readAllLines(path);
                for (String line : lines) {
                    String[] parts = line.split(" \\| ");
                    if (parts.length == 2) {
                        String text = parts[0];
                        try {
                            LocalDateTime createdAt = LocalDateTime.parse(parts[1], FORMATTER);
                            notesList.add(new Note(text, createdAt));
                        } catch (DateTimeParseException e) {
                            System.err.println("Ошибка парсинга даты в строке: " + line + ". Пропускаем.");
                        }
                    } else {
                        System.err.println("Некорректный формат строки в файле: " + line + ". Пропускаем.");
                    }
                }
                System.out.println("Заметки загружены из " + FILENAME);
            } catch (IOException e) {
                System.err.println("Ошибка при загрузке файла: " + e.getMessage());
            }
        }
    }

    // Основное меню
    public void runMenu() {
        Scanner scanner = new Scanner(System.in);
        int choice;

        while (true) {
            printMenu();
            System.out.print("Выберите действие: ");
            try {
                choice = Integer.parseInt(scanner.nextLine()); // Читаем всю строку
            } catch (NumberFormatException e) {
                System.out.println("Некорректный ввод. Пожалуйста, введите число.");
                continue;
            }

            switch (choice) {
                case 1:
                    addNote(scanner);
                    break;
                case 2:
                    showAllNotes();
                    break;
                case 3:
                    deleteNote(scanner);
                    break;
                case 4:
                    saveToFile();
                    break;
                case 5:
                    loadNotesFromFile();
                    break;
                case 6:
                    runReminder();
                    break;
                case 0:
                    System.out.println("Выход из программы.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Некорректный выбор. Пожалуйста, попробуйте снова.");
            }
            System.out.println(); // Пустая строка для лучшей читаемости
        }
    }

    // Печать меню
    private void printMenu() {
        System.out.println("\n--- Менеджер заметок ---");
        System.out.println("1. Добавить заметку");
        System.out.println("2. Показать все заметки");
        System.out.println("3. Удалить заметку по номеру");
        System.out.println("4. Сохранить в файл");
        System.out.println("5. Загрузить из файла");
        System.out.println("6. Запустить напоминание");
        System.out.println("0. Выход");
    }

    // Добавление заметки
    private void addNote(Scanner scanner) {
        System.out.print("Введите текст заметки: ");
        String text = scanner.nextLine();
        if (text != null && !text.trim().isEmpty()) {
            Note newNote = new Note(text.trim());
            notesList.add(newNote);
            // Если используете Map: notesMap.put(text.trim(), newNote);
            System.out.println("Заметка добавлена!");
        } else {
            System.out.println("Текст заметки не может быть пустым.");
        }
    }

    // Показ всех заметок
    private void showAllNotes() {
        if (notesList.isEmpty()) {
            System.out.println("Список заметок пуст.");
            return;
        }

        System.out.println("\n--- Все заметки ---");
        // Использование Stream API и Lambda-выражений для фильтрации (пример)
        // Например, показать только заметки за последний час
        /*
        System.out.println("Заметки за последний час:");
        notesList.stream()
                .filter(n -> n.getCreatedAt().isAfter(LocalDateTime.now().minusHours(1)))
                .forEach(n -> System.out.println((notesList.indexOf(n) + 1) + ". " + n));
        */

        // Показать все заметки с нумерацией
        for (int i = 0; i < notesList.size(); i++) {
            System.out.println((i + 1) + ". " + notesList.get(i));
        }
    }

    // Удаление заметки по номеру
    private void deleteNote(Scanner scanner) {
        if (notesList.isEmpty()) {
            System.out.println("Список заметок пуст. Нечего удалять.");
            return;
        }

        System.out.print("Введите номер заметки для удаления: ");
        try {
            int number = Integer.parseInt(scanner.nextLine());
            if (number > 0 && number <= notesList.size()) {
                // Использование Анонимного класса для выполнения действия после удаления
                Runnable onDeleteAction = new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Заметка удалена!");
                    }
                };

                Note removedNote = notesList.remove(number - 1); // Удаление из List
                // Если используете Map: notesMap.remove(removedNote.getText());

                onDeleteAction.run(); // Выполнение действия после удаления

            } else {
                System.out.println("Некорректный номер заметки.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Некорректный ввод. Пожалуйста, введите число.");
        }
    }

    // Многопоточность - запуск напоминания
    private void runReminder() {
        System.out.println("Запуск напоминания...");

        // Использование Lambda-выражения для создания потока
        new Thread(() -> {
            System.out.println("Напоминание: у вас " + notesList.size() + " заметок!");
            try {
                Thread.sleep(2000); // Пауза 2 секунды
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Восстанавливаем флаг прерывания
                System.err.println("Поток напоминания был прерван.");
            }
            System.out.println("Проверка напоминаний завершена.");
        }).start(); // Запуск нового потока
    }
}
