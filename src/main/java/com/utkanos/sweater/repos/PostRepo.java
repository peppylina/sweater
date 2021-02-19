package com.utkanos.sweater.repos;

import com.utkanos.sweater.domains.Post;
import com.utkanos.sweater.domains.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;

public interface PostRepo extends CrudRepository<Post, Long> {

    //этот метод создаст Spring автоматически по ключевым словам: find-найти, by-по какому ключу, Tag-название ключа, но можно и другой
    Page<Post> findByTag(String tag, Pageable pageable);

    //пример поиска по тексту сообщений
    Page<Post> findByText(String text, Pageable pageable);

    Page<Post> findAll(Pageable pageable);

    Page<Post> findByAuthor(User user, Pageable pageable);

    ArrayList<Post> findByAuthor(User user);

    //чтобы можно было писать на нативном sql, надо указать параметр nativeQuery=true.
    @Query(value = "SELECT * FROM " +
                        "(SELECT DISTINCT p1.id, p1.filename, p1.tag, p1.text, p1.user_id, p1.time " +
                            "FROM post as p1 INNER JOIN user_subscriptions as us1 " +
                            "ON (us1.profile_id = p1.user_id AND us1.subscriber_id = :userId) OR (p1.user_id = :userId) " +
                        "UNION " +
                        "SELECT DISTINCT * FROM post as p2 WHERE p2.user_id = :userId ) as t ",
            countQuery = "SELECT count(*) FROM (SELECT * FROM " +
            "(SELECT DISTINCT p1.id, p1.filename, p1.tag, p1.text, p1.user_id, p1.time " +
                    "FROM post as p1 INNER JOIN user_subscriptions as us1 " +
                    "ON (us1.profile_id = p1.user_id AND us1.subscriber_id = :userId) OR (p1.user_id = :userId) " +
                    "UNION " +
                    "SELECT DISTINCT * FROM post as p2 WHERE p2.user_id = :userId ) as t ) as ans",
            nativeQuery = true)
    Page<Post> findMyAll(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "SELECT * FROM " +
                        "(SELECT DISTINCT p1.id, p1.filename, p1.tag, p1.text, p1.user_id, p1.time " +
                            "FROM post as p1 INNER JOIN user_subscriptions as us1 " +
                            "ON (us1.profile_id = p1.user_id AND us1.subscriber_id = :userId) OR (p1.user_id = :userId) " +
                            "WHERE p1.id IS NOT NULL AND p1.tag = :tag " +
                        "UNION " +
                        "SELECT DISTINCT * FROM post as p2 WHERE p2.user_id = :userId AND p2.tag = :tag) as t ",
            countQuery = "SELECT count(*) FROM ( SELECT * FROM " +
                    "(SELECT DISTINCT p1.id, p1.filename, p1.tag, p1.text, p1.user_id, p1.time " +
                    "FROM post as p1 INNER JOIN user_subscriptions as us1 " +
                    "ON (us1.profile_id = p1.user_id AND us1.subscriber_id = :userId) OR (p1.user_id = :userId) " +
                    "WHERE p1.id IS NOT NULL AND p1.tag = :tag " +
                    "UNION " +
                    "SELECT DISTINCT * FROM post as p2 WHERE p2.user_id = :userId AND p2.tag = :tag) as t ) as ans ",
            nativeQuery = true)
    Page<Post> findMyByTag(@Param("tag") String tag, @Param("userId") Long userId, Pageable pageable);

    @Query(value = "SELECT * FROM " +
                        "(SELECT DISTINCT p1.id, p1.filename, p1.tag, p1.text, p1.user_id, p1.time " +
                            "FROM post as p1 INNER JOIN user_subscriptions as us1 " +
                            "ON (us1.profile_id = p1.user_id AND us1.subscriber_id = :userId) OR (p1.user_id = :userId) " +
                            "WHERE p1.id IS NOT NULL AND p1.text = :text " +
                        "UNION " +
                        "SELECT DISTINCT * FROM post as p2 WHERE p2.user_id = :userId AND p2.text = :text) as t ",
            countQuery = "SELECT count(*) FROM (SELECT * FROM " +
                    "(SELECT DISTINCT p1.id, p1.filename, p1.tag, p1.text, p1.user_id, p1.time " +
                    "FROM post as p1 INNER JOIN user_subscriptions as us1 " +
                    "ON (us1.profile_id = p1.user_id AND us1.subscriber_id = :userId) OR (p1.user_id = :userId) " +
                    "WHERE p1.id IS NOT NULL AND p1.text = :text " +
                    "UNION " +
                    "SELECT DISTINCT * FROM post as p2 WHERE p2.user_id = :userId AND p2.text = :text) as t ) as ans",
            nativeQuery = true)
    Page<Post> findMyByText(@Param("text") String textFilter, @Param("userId") Long id, Pageable pageable);
}
