package com.utkanos.sweater.controllers;

import com.utkanos.sweater.domains.User;
import com.utkanos.sweater.service.UserService;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/profiles")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Value("http://${hostname}")
    private String hostUrl;

    @GetMapping("/{userId}")
    public String getProfilePage(
            @PathVariable(name = "userId") User user,
            Model model
            ) {
        if (user == null) return "exceptions/error";
        model.addAttribute("user", user);
        model.addAttribute("hostUrl", hostUrl);
        return "userProfile";
    }

    @GetMapping("/editProfile")
    public String getEditProfile(
            Model model, @AuthenticationPrincipal User user
    ){
        model.addAttribute("user", user);
        model.addAttribute("hostUrl", hostUrl);
        return "profileEdit";
    }

    @PostMapping("/editProfile")
    @Valid
    public String saveChanges(
            @AuthenticationPrincipal User curUser,
            @Valid User user,
            BindingResult  bindingResult,
            @RequestParam("file") MultipartFile file,
            Model model
    ) throws IOException, FileSizeLimitExceededException {
        //Данные, которые по-любому возвращаются
        model.addAttribute("hostUrl", hostUrl);

        //если хоть одно поле непустое
        if ((user.getPassword() != null && !user.getPassword().equals("")) ||
                (user.getPassword2() != null && !user.getPassword2().equals(""))) {
            //если есть ошибки валидации, то выводим их
            if (bindingResult.hasErrors()) {
                Map<String, String> errorsMap = ControllerUtils.getErrorMap(bindingResult);
                model.mergeAttributes(errorsMap);
                model.addAttribute("user", curUser);
                return "profileEdit";
            }
        }

        //если username занят и был изменен
        if (!user.getUsername().equals(curUser.getUsername()) && !userService.isUsernameFree(user.getUsername())) {
            model.addAttribute("usernameError", "Username is not free!");
            model.addAttribute("user", curUser);
            return "profileEdit";
        }

        //если поля пароля непустые и не совпадают
        if (user.getPassword() != null && !user.getPassword().equals(user.getPassword2())) {
            model.addAttribute("password2Error", "Passwords are different!");
            model.addAttribute("user", curUser);
            return "profileEdit";
        }

        //если поменяли почту
        if (!curUser.getEmail().equals(user.getEmail())) {
            user.setActivationCode(UUID.randomUUID().toString());
            user.setActive(false);

            //отправляем код активации
            userService.sendActivationCode(user);
        } else {
            user.setActive(true);
        }

        //если старый пароль не совпадает с новым, то есть поменяли пароль
        if (user.getPassword() != null && !user.getPassword().equals("") &&
                !curUser.getPassword().equals(user.getPassword())) {
            user.setPassword(userService.passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(curUser.getPassword());
        }

        //Пробуем сохранить файл. если пустой, то так и останется null, иначе получим имя файла. Также ловим исключения
        String avaFilename = null;
        try {
            avaFilename = userService.setUserAva(curUser, file);
        } catch (Exception ex) {
            model.addAttribute("fileError", "Ошибка при добавлении Вашего изображения");
        }

        curUser.setAva(avaFilename);
        curUser.setPassword(user.getPassword());
        curUser.setUsername(user.getUsername());
        curUser.setEmail(user.getEmail());
        curUser.setPassword2(user.getPassword2());
        curUser.setActivationCode(user.getActivationCode());
        curUser.setActive(user.isActive());
        model.addAttribute("isChanged", "true");
        userService.saveUserToDb(curUser);

        //обновляем информацию для Spring Security.
        userService.updateAuthPrincipal(curUser);

        model.addAttribute("user", curUser);
        return "profileEdit";
    }

    @PostMapping("/deleteAva")
    public String deleteAva(
            @AuthenticationPrincipal User curUser,
            Model model
    ) {
        if(!userService.deleteAva(curUser)) {
            model.addAttribute("fileError", "Невозможно удалить изображение");
        }
        //обновляем информацию для Spring Security.
        userService.updateAuthPrincipal(curUser);

        model.addAttribute("user", curUser);
        model.addAttribute("hostUrl", hostUrl);
        return "profileEdit";
    }

}
