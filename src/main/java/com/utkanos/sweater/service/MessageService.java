package com.utkanos.sweater.service;

import com.utkanos.sweater.domains.Message;
import com.utkanos.sweater.domains.User;
import com.utkanos.sweater.repos.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class MessageService {

    //эта аннотация означает, что Spring автоматически инициализирует данную переменную
    //Это переменная базы данных, ее репозиторий, через который мы будем общаться с бд
    @Autowired
    private MessageRepo messageRepo;

    //Эта переменная, которая вытягивается из файла application.properties и подставляется в uploadPath
    @Value("${upload.path}")
    private String uploadPath;

    public boolean addMessage(Message message, MultipartFile file) throws IOException {
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
            message.setFilename(resFilename);
        }
        messageRepo.save(message);
        return true;
    }

    public Iterable<Message> findByTag(String tagFilter) {
        return messageRepo.findByTag(tagFilter);
    }

    public Iterable<Message> findByText(String textFilter) {
        return messageRepo.findByText(textFilter);
    }

    public Iterable<Message> findAll() {
        return messageRepo.findAll();
    }

    public void deleteMessage(Message message) {
        messageRepo.delete(message);
    }

    public Iterable<Message> findByUserId(User user) {
        return messageRepo.findByAuthor(user);
    }
}
