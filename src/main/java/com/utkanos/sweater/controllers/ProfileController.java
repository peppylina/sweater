package com.utkanos.sweater.controllers;

import com.utkanos.sweater.domains.User;
import com.utkanos.sweater.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/profiles")
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping("/{userId}")
    public String getProfilePage(
            @PathVariable(name = "userId") User user,
            Model model
            ) {
        model.addAttribute("user", user);
        return "userProfile";
    }

    @GetMapping("/editProfile")
    public String getEditProfile(
            Model model, @AuthenticationPrincipal User user
    ){
        model.addAttribute("user", user);
        return "profileEdit";
    }

    @PostMapping("/editProfile")
    @Valid
    public String saveChanges(
            @AuthenticationPrincipal User curUser,
            @Valid User user,
            BindingResult  bindingResult,
            Model model
    ) {
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

        //если поменя почту
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


        curUser.setPassword(user.getPassword());
        curUser.setUsername(user.getUsername());
        curUser.setEmail(user.getEmail());
        curUser.setPassword2(user.getPassword2());
        curUser.setActivationCode(user.getActivationCode());
        curUser.setActive(user.isActive());
        model.addAttribute("isChanged", "true");
        userService.saveUserToDb(curUser);

        model.addAttribute("user", curUser);
        return "profileEdit";
    }

}
