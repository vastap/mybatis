package learn.mybatis;

import lombok.Getter;
import lombok.Setter;

/**
 * Сущность предметной области - сообщение.
 * Каждое сообщение имеет содержание.
 */
public class Message {

    @Getter
    @Setter
    private long id;
    @Getter
    @Setter
    private String content;

    public Message(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public Message(String content) {
        this.content = content;
    }

}