import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // Аннотация будет доступна во время выполнения
@Target(ElementType.METHOD)       // Аннотация может быть применена к методам
public @interface Task {
    String value(); // Метаданные аннотации
}
