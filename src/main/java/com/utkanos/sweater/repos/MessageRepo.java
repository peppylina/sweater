package com.utkanos.sweater.repos;

import com.utkanos.sweater.domains.Message;
import com.utkanos.sweater.domains.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MessageRepo extends CrudRepository<Message, Long> {

    //этот метод создаст Spring автоматически по ключевым словам: find-найти, by-по какому ключу, Tag-название ключа, но можно и другой
    List<Message> findByTag(String tag);

    //пример поиска по тексту сообщений
    List<Message> findByText(String text);

    Iterable<Message> findByAuthor(User user);
}
