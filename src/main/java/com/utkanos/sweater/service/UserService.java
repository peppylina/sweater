package com.utkanos.sweater.service;

import com.utkanos.sweater.domains.Role;
import com.utkanos.sweater.domains.User;
import com.utkanos.sweater.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MailSender mailSender;

    @Autowired
    public PasswordEncoder passwordEncoder;

    @Value("${activation.url}")
    private String activationUrl;

    @Value("${upload.path}/avas")
    private String avasPath;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("Username Not found!");
        }
        return userRepo.findByUsername(username);
    }

    public boolean addNewUser(User user) {
        //ищем такого пользователя в базе данных
        User userFromDb = userRepo.findByUsername(user.getUsername());

        //если такой пользователь есть, то говорим об этом
        if (userFromDb != null) {
            return false;
        }

        //иначе добавляем активность и роль и добавляем его в бд
        user.setActive(false);
        user.setRoles(Collections.singleton(Role.USER));
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        //Добавляем activation code, Пока он существует, пользователь не подтвердил свою почту.
        user.setActivationCode(UUID.randomUUID().toString());

        userRepo.save(user);

        //И отправляем сообщение на почту, для подтверждения электронной почты
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            sendActivationCode(user);
        }

        return true;
    }

    public void sendActivationCode(User user) {
        String text = String.format("To activate your account in sweater, please, go to this link:\n" +
                "%s%s", activationUrl, user.getActivationCode());
        mailSender.sendMail("Activate your account!", user.getEmail(), text);
    }

    public boolean activateUser(String activationCode) {
        User user = userRepo.findByActivationCode(activationCode);

        //если есть пользователь с таким кодом, то активируем его и удаляем у него такой код
        if (user != null) {
            user.setActivationCode(null);
            user.setActive(true);
            userRepo.save(user);
            return true;
        }
        return false;
    }

    public boolean isUsernameFree(String username) {
        User user = userRepo.findByUsername(username);
        return user == null;
    }

    public void saveUserToDb(User user) {
        userRepo.save(user);
    }

    /*
    * Возвращает имя файла, если получилось загрузить, и выкидывает ошибку в противном случае
    * */
    public String setUserAva(User user, MultipartFile file) throws Exception{
        if (file != null && !file.isEmpty()) {
            //сперва удаляем старую аву, если она есть
            deleteAva(user);
            //тепер загружаем новую
            File uploadDir = new File(avasPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            //генерируем рандомное имя файла
            String uuidFile = UUID.randomUUID().toString();
            String resFilename = String.format("%s.%s", uuidFile, file.getOriginalFilename());
            //делаем полный путь к файлу
            String resUploadPath = String.format("%s/%s", avasPath, resFilename);
            //загружаем файл
            file.transferTo(new File(resUploadPath));
            return resFilename;
        }
        return user.getAva();
    }

    public boolean updateUserInfo(User user, User curUser) {
        //сохраняем новый логин
        curUser.setUsername(user.getUsername());

        //если поменяли пароль, то меняем его, иначе если поле пустое, то ниего не трогаем
        if (!user.getPassword2().equals("NOT_CHANGED")) {
            curUser.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            curUser.setPassword(user.getPassword());
        }
        curUser.setPassword2("OK");
        user.setPassword2("OK");

        //если поменяли почту
        if (user.getEmail() != null && curUser.getEmail() != null && !user.getEmail().equals(curUser.getEmail())) {
            curUser.setActivationCode(UUID.randomUUID().toString());
            curUser.setActive(false);

            //отправляем код активации
            sendActivationCode(curUser);

        }

        saveUserToDb(curUser);

        return true;
    }

    public List<User> findAll() {
        return userRepo.findAll();
    }

    public void deleteById(Long id) {
        userRepo.deleteById(id);
    }

    /*
    * Удаление авы из папки на сервере и из бд
    * */
    public boolean deleteAva(User curUser){
        //удаляем из папки
        File file = new File(String.format("%s/%s", avasPath, curUser.getAva()));
        if (file.delete()) {
            //Удаляем запись в базе данных
            curUser.setAva(null);
            userRepo.save(curUser);
            return true;
        }
        return false;
    }

    /*
    * Обновляет информацию о текущем пользователе в SpringSecurity
    * */
    public void updateAuthPrincipal(User user) {
        SecurityContext ctx = SecurityContextHolder.getContext();
        User principal = (User) ctx.getAuthentication().getPrincipal();
        //если нам подсунули того же пользователя, что и авторизован, то обновляем инфу о нем
        if(user.equals(principal)) {
            Authentication token =
                    new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
            ctx.setAuthentication(token);
        }
    }
}
