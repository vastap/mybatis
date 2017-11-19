package learn.mybatis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Контроллер, отвечающий за маппинг на URL для работы с сообщениями.
 */
@RestController
@RequestMapping(value = "/messages")
public class MessageController {
    private MessageDao messageDao;

    /**
     * Получить сообщение по его ID (например, /messages/id/1)
     *
     * @param messageId ID сообщения
     * @return сообщение
     */
    @RequestMapping(value = "/id/{messageId}", method = RequestMethod.GET)
    public Message getMessage(@PathVariable long messageId) {
        return messageDao.selectMessageById(messageId);
    }

    /**
     * Получить сообщение по его ID (использует маппер через аннотации)
     *
     * @param messageId ID сообщения
     * @return сообщение
     */
    @RequestMapping(value = "/get/{messageId}", method = RequestMethod.GET)
    public Message getMessageById(@PathVariable long messageId) {
        return messageDao.getMessageById(messageId);
    }

    /**
     * Добавить одиночное сообщение (например, /messages/add?text=NewMessage)
     *
     * @param text Текст сообщения
     * @return Добавленное сообщение
     */
    @RequestMapping("/add")
    public Message addMessage(@RequestParam(value = "text", defaultValue = "empty") String text) {
        Message message = new Message(text);
        return messageDao.addMessage(message);
    }

    /**
     * Добавить несколько сообщений (например, /messages/addall?text=one&text=two)
     *
     * @param text Массив сообщений
     * @return Список добавленных сообщений
     */
    @RequestMapping("/addall")
    public List<Message> addMessages(@RequestParam("text") String[] text) {
        List<Message> messages = new ArrayList<>();
        for (String messageText : text) {
            messages.add(new Message(messageText));
        }
        return messageDao.addMessages(messages);
    }

    /**
     * Конструктор для инициализации контроллера. Обеспечивает инжект messageDao
     *
     * @param messageDao DAO для работы с Message
     */
    @Autowired
    public MessageController(MessageDao messageDao) {
        this.messageDao = messageDao;
    }

}