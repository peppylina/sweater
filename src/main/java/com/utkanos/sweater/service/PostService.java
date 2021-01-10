package com.utkanos.sweater.service;

import com.utkanos.sweater.domains.Post;
import com.utkanos.sweater.domains.User;
import com.utkanos.sweater.repos.PostRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
public class PostService {

    //эта аннотация означает, что Spring автоматически инициализирует данную переменную
    //Это переменная базы данных, ее репозиторий, через который мы будем общаться с бд
    @Autowired
    private PostRepo postRepo;

    //Эта переменная, которая вытягивается из файла application.properties и подставляется в uploadPath
    @Value("${upload.path}")
    private String uploadPath;

    public boolean addPost(Post post, MultipartFile file) throws IOException {
        post.setTime(new Date().getTime());
        //если не пустой файл, то добавляем его в бд
        if (file != null && !file.isEmpty()) {
            File uploadDir = new File(uploadPath);
            //если нет такого, то сохздаем директорию
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            //генерируем рандомное имя файла
            String uuidFile = UUID.randomUUID().toString();
            String resFilename = String.format("%s.%s", uuidFile, file.getOriginalFilename());
            //делаем полный путь к файлу
            String resUploadPath = String.format("%s/%s", uploadPath, resFilename);
            //загружаем файл
            file.transferTo(new File(resUploadPath));
            //передаем в сообщение название файла
            post.setFilename(resFilename);
        }
        postRepo.save(post);
        return true;
    }

    public Iterable<Post> findByTag(String tagFilter) {
        return postRepo.findByTag(tagFilter);
    }

    public Iterable<Post> findByText(String textFilter) {
        return postRepo.findByText(textFilter);
    }

    public Iterable<Post> findAll() {
        return postRepo.findAll();
    }

    public void deletePost(Post post) {
        postRepo.delete(post);
    }

    public Iterable<Post> findByUserId(User user) {
        return postRepo.findByAuthor(user);
    }

    /*
    * Ищет посты, которые выкладывали люди, на которых подписан пользователь
    * */
    public Iterable<Post> findMyAll(User user, Long startIndex) {
        return postRepo.findMyAll(user.getId(), startIndex, 5L);
    }

    public Iterable<Post> findMyByTag(String tagFilter, User user) {
        return postRepo.findMyByTag(tagFilter, user.getId());
    }

    public Iterable<Post> findMyByText(String textFilter, User user) {
        return postRepo.findMyByText(textFilter, user.getId());
    }
}
