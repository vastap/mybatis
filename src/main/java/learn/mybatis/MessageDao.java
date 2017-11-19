package learn.mybatis;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MessageDao {
    private final SqlSession sqlSession;

    /**
     * Создание DAO для работы с Message
     *
     * @param sqlSession Sql Session, которая предоставляется Spring Framework
     * @link <a href=http://www.mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/>Documentation</a>
     * @link <a href=http://www.mybatis.org/spring/using-api.html>MyBatis API</a>
     */
    public MessageDao(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    /**
     * Получить сообщение по его ID
     *
     * @param id ID сообщения
     * @return Сообщение, найденное по его ID
     */
    public Message selectMessageById(long id) {
        MessageMapper mapper = sqlSession.getMapper(MessageMapper.class);
        Message message = mapper.findById(id);
        return message;
    }

    /**
     * Получить сообщение по его ID (использует маппер через аннотации)
     *
     * @param id ID сообщения
     * @return Сообщение, найденное по его ID
     */
    public Message getMessageById(long id) {
        MessageMapper mapper = sqlSession.getMapper(MessageMapper.class);
        Message message = mapper.getById(id);
        return message;
    }

    /**
     * Добавить сообщение
     *
     * @param message Добавляемое сообщение
     * @return Добавленное сообщение (ID сообщения заполнено)
     */
    public Message addMessage(Message message) {
        MessageMapper mapper = sqlSession.getMapper(MessageMapper.class);
        mapper.addMessage(message);
        return message;
    }

    /**
     * Добавить список сообщений
     *
     * @param messageList Список сообщений для добавления
     * @return Список сохранённых сообщений (ID сообщений заполнены)
     */
    public List<Message> addMessages(List<Message> messageList) {
        MessageMapper mapper = sqlSession.getMapper(MessageMapper.class);
        mapper.addMessages(messageList);
        return messageList;
    }

}
