package learn.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Интерфейс маппера.
 * <p>Описывает контракт взаимодействия с маппером.
 * За маппером прячется xml, в котором должны быть объявлены такие же названия методов.
 * Если названия будут отличаться - MyBatis не сможет соотнести методы в классе и методы в XML и упадёт.
 */
@Mapper
public interface MessageMapper {
    Message findById(long id);

    @Select("SELECT * FROM message WHERE id = #{id}")
    Message getById(long id);

    // Return type of mapped insert method can be void or int (count of inserted rows)
    int addMessage(Message message);

    int addMessages(List<Message> messageList);
}