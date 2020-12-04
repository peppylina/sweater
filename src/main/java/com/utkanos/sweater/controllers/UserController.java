package com.utkanos.sweater.controllers;

import com.utkanos.sweater.domains.Message;
import com.utkanos.sweater.domains.Role;
import com.utkanos.sweater.domains.User;
import com.utkanos.sweater.repos.MessageRepo;
import com.utkanos.sweater.service.MessageService;
import com.utkanos.sweater.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users")
//теперь по этим адресам может ходить только ADMIN(аналогично можно указать в WebSecurityConfig)
//@PreAuthorize("hasAuthority('ADMIN')")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String getUsers(
            Model model
    ) {
        model.addAttribute("users", userService.findAll());

        return "userList";
    }

    @GetMapping("/{user}")
    public String userDetails(
            @PathVariable User user,
            Model model
    ) {
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());

        return "userEdit";
    }

    @PostMapping("/delete")
    public String deleteUserPost(
            @RequestParam(name = "id") User user
    ) {
        Iterable<Message> messages = messageService.findByUserId(user);
        for (Message mess : messages) {
            messageService.deleteMessage(mess);
        }
        userService.deleteById(user.getId());
        return "redirect:/users";
    }

    @PostMapping
    public String saveChanges(
            @RequestParam Map<String, String> editForm,
            @RequestParam String password,
            @RequestParam String email,
            @RequestParam String username,
            @RequestParam(name = "id") User user,
            @AuthenticationPrincipal User curUser,
            Model model
    ) {
        //если поменяли на пустой логин
        if (username == null || username.equals("")) {
            model.addAttribute("usernameError", "Can't be empty email!");
            model.addAttribute("roles", Role.values());
            model.addAttribute("user", user);
            return "userEdit";
        }

        //если поменяли username, но такое имя пользователя занято
        if (!username.equals(user.getUsername()) && !userService.isUsernameFree(username)) {
            model.addAttribute("usernameError", "This username is not free!");
            model.addAttribute("roles", Role.values());
            model.addAttribute("user", user);
            return "userEdit";
        }

        //если пустой емейл, а до этого емейл был
        if (user.getEmail() != null && (email == null || email.equals(""))) {
            model.addAttribute("emailError", "Can not be empty email!");
            model.addAttribute("roles", Role.values());
            model.addAttribute("user", user);
            return "userEdit";
        }

        //иначе сохраняем данные, которые ввели
        //если ввели пароль, значит хотят его поменять
        if(password != null && !password.equals("")) {
            user.setPassword(passwordEncoder.encode(password));
        }

        user.setUsername(username);
        user.setEmail(email);

        //делаем из enum сет
        Set<String> roles = Arrays.stream(Role.values())
                .map(Role::name)
                .collect(Collectors.toSet());
        //очищаем список ролей, чтобы записать те роли, которые пришли от формы
        user.getRoles().clear();
        //записываем новые роли
        for (String key : editForm.keySet()) {
            if(roles.contains(key)) {
                user.getRoles().add(Role.valueOf(key));
            }
        }

        //чтобы не ругался валидатор
        user.setPassword2("ok");

        //если все это делали с текущим пользователем
        if (user.getId().equals(curUser.getId())) {
            curUser = user;
            userService.saveUserToDb(curUser);
        }

        userService.saveUserToDb(user);
        return "redirect:/users";
    }

}
