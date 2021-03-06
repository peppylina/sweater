package com.utkanos.sweater.controllers;

import com.utkanos.sweater.domains.User;
import com.utkanos.sweater.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SubscribersController {

    @Autowired
    private UserService userService;

    @Value("http://${hostname}")
    private String hostUrl;

    @GetMapping("/subscribers")
    public String getListOfSubscribers(
            Model model,
            @RequestParam(name = "id", required = false) User user,
            @AuthenticationPrincipal User curUser
    ) {
        if (user == null) model.addAttribute("user", curUser);
        else model.addAttribute("user", user);
        model.addAttribute("isSubscribersPage", true);
        model.addAttribute("hostUrl", hostUrl);
        return "listOfSubs";
    }

    @GetMapping("/subscriptions")
    public String getListOfSubscriptions(
            Model model,
            @RequestParam(name = "id", required = false) User user,
            @AuthenticationPrincipal User curUser
    ) {
        if (user == null) model.addAttribute("user", curUser);
        else model.addAttribute("user", user);
        model.addAttribute("isSubscribersPage", false);
        model.addAttribute("hostUrl", hostUrl);
        return "listOfSubs";
    }

    @PostMapping("/subscribe")
    public String subscribe(
            Model model,
            @RequestParam("id") User user,
            @AuthenticationPrincipal User me
    ) {
        user.getSubscribers().add(me);
        userService.saveUserToDb(user);
        return "redirect:/profiles/" + user.getId();
    }

    @PostMapping("/unsubscribe")
    public String unsubscribe(
            Model model,
            @RequestParam(name = "id") User user,
            @AuthenticationPrincipal User me
    ) {
        user.getSubscribers().remove(me);
        userService.saveUserToDb(user);
        return "redirect:/profiles/" + user.getId();
    }

}
