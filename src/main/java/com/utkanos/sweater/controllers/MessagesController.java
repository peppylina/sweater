package com.utkanos.sweater.controllers;

import com.utkanos.sweater.domains.Message;
import com.utkanos.sweater.domains.User;
import com.utkanos.sweater.exceptions.PermissionDeniedException;
import com.utkanos.sweater.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@Controller
@RequestMapping("/messages")
public class MessagesController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/edit/{authorId}")
    public String editMessage(
            @PathVariable(name = "authorId") User user,
            @AuthenticationPrincipal User curUser,
            @RequestParam(name = "message") Message message,
            Model model
            ) throws PermissionDeniedException {
        if (!curUser.getId().equals(message.getAuthorId())) {
            throw new PermissionDeniedException();
        }
        model.addAttribute("message", message);
        model.addAttribute("user", user);
        return "messageEdit";
    }

    @PostMapping("/edit/{authorId}")
    public String saveChanges(
            @Valid Message message,
            @RequestParam("file") MultipartFile file,
            BindingResult bindingResult,
            Model model
    ) throws IOException {

        //проверяем на ошибки по аннотациям из класса Message
        if (bindingResult.hasErrors()) {
            model.mergeAttributes(ControllerUtils.getErrorMap(bindingResult));
            model.addAttribute("message", message);
        } else {
            boolean res = messageService.addMessage(message, file);
            model.addAttribute("status", "Saved");
            model.addAttribute("message", message);
        }

        return "messageEdit";
    }

}
