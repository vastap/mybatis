package learn.mybatis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс Spring Boot приложения.
 */
@SpringBootApplication
public class App {

    /**
     * Точка входа. Запускает Spring Boot приложение
     *
     * @param args Входные параметры при запуске приложения
     */
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
