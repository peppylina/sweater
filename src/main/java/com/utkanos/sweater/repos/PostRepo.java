package com.utkanos.sweater.repos;

import com.utkanos.sweater.domains.Post;
import com.utkanos.sweater.domains.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepo extends CrudRepository<Post, Long> {

    //этот метод создаст Spring автоматически по ключевым словам: find-найти, by-по какому ключу, Tag-название ключа, но можно и другой
    List<Post> findByTag(String tag);

    //пример поиска по тексту сообщений
    List<Post> findByText(String text);

    Iterable<Post> findByAuthor(User user);

    //чтобы можно было писать на нативном sql, надо указать параметр nativeQuery=true.
    @Query(value = "SELECT * FROM " +
                        "(SELECT DISTINCT p1.id, p1.filename, p1.tag, p1.text, p1.user_id, p1.time " +
                            "FROM post as p1 INNER JOIN user_subscriptions as us1 " +
                            "ON (us1.profile_id = p1.user_id AND us1.subscriber_id = :userId) OR (p1.user_id = :userId) " +
                        "UNION " +
                        "SELECT DISTINCT * FROM post as p2 WHERE p2.user_id = :userId ) as t " +
                    "ORDER BY t.time DESC LIMIT :startIndex, :amount",
            nativeQuery = true)
    List<Post> findMyAll(@Param("userId") Long userId, @Param("startIndex") Long startIndex, @Param("amount") Long amount);

    @Query(value = "SELECT * FROM " +
                        "(SELECT DISTINCT p1.id, p1.filename, p1.tag, p1.text, p1.user_id, p1.time " +
                            "FROM post as p1 INNER JOIN user_subscriptions as us1 " +
                            "ON (us1.profile_id = p1.user_id AND us1.subscriber_id = :userId) OR (p1.user_id = :userId) " +
                            "WHERE p1.id IS NOT NULL AND p1.tag = :tag " +
                        "UNION " +
                        "SELECT DISTINCT * FROM post as p2 WHERE p2.user_id = :userId AND p2.tag = :tag) as t " +
                    "ORDER BY t.time DESC",
            nativeQuery = true)
    List<Post> findMyByTag(@Param("tag") String tag, @Param("userId") Long userId);

    @Query(value = "SELECT * FROM " +
                        "(SELECT DISTINCT p1.id, p1.filename, p1.tag, p1.text, p1.user_id, p1.time " +
                            "FROM post as p1 INNER JOIN user_subscriptions as us1 " +
                            "ON (us1.profile_id = p1.user_id AND us1.subscriber_id = :userId) OR (p1.user_id = :userId) " +
                            "WHERE p1.id IS NOT NULL AND p1.text = :text " +
                        "UNION " +
                        "SELECT DISTINCT * FROM post as p2 WHERE p2.user_id = :userId AND p2.text = :text) as t " +
                    "ORDER BY t.time DESC",
            nativeQuery = true)
    List<Post> findMyByText(@Param("text") String textFilter, @Param("userId") Long id);
}
