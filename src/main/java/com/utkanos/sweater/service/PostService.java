package com.utkanos.sweater.service;

import com.utkanos.sweater.domains.Post;
import com.utkanos.sweater.domains.User;
import com.utkanos.sweater.repos.PostRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

    public Iterable<Post> findByTag(String tagFilter, Pageable pageable) {
        return postRepo.findByTag(tagFilter, pageable);
    }

    public Iterable<Post> findByText(String textFilter, Pageable pageable) {
        return postRepo.findByText(textFilter, pageable);
    }

    public Page<Post> findAll(Pageable pageable) {
        return postRepo.findAll(pageable);
    }

    public void deletePost(Post post) {
        postRepo.delete(post);
    }

    public Page<Post> findByUserId(User user, Pageable pageable) {
        return postRepo.findByAuthor(user, pageable);
    }

    public ArrayList<Post> findByUserId(User user) {
        return postRepo.findByAuthor(user);
    }

    /*
    * Ищет посты, которые выкладывали люди, на которых подписан пользователь
    * */
    public Page<Post> findMyAll(User user, Pageable pageable) {
        return postRepo.findMyAll(user.getId(), pageable);
    }

    public Page<Post> findMyByTag(String tagFilter, User user, Pageable pageable) {
        return postRepo.findMyByTag(tagFilter, user.getId(), pageable);
    }

    public Page<Post> findMyByText(String textFilter, User user, Pageable pageable) {
        return postRepo.findMyByText(textFilter, user.getId(), pageable);
    }
}
