package com.utkanos.sweater.controllers;

import com.utkanos.sweater.domains.User;
import com.utkanos.sweater.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/subscribers")
    public String getListOfSubscribers(
            Model model,
            @RequestParam("id") User user
    ) {
        model.addAttribute("isSubscribersPage", true);
        model.addAttribute("user", user);
        return "listOfSubs";
    }

    @GetMapping("/subscriptions")
    public String getListOfSubscriptions(
            Model model,
            @RequestParam("id") User user
    ) {
        model.addAttribute("isSubscribersPage", false);
        model.addAttribute("user", user);
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
