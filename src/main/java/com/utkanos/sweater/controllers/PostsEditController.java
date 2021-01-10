package com.utkanos.sweater.controllers;

import com.utkanos.sweater.domains.Post;
import com.utkanos.sweater.domains.User;
import com.utkanos.sweater.exceptions.PermissionDeniedException;
import com.utkanos.sweater.service.PostService;
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
@RequestMapping("/posts")
public class PostsEditController {

    @Autowired
    private PostService postService;

    @GetMapping("/edit/{authorId}")
    public String editPost(
            @PathVariable(name = "authorId") User user,
            @AuthenticationPrincipal User curUser,
            @RequestParam(name = "post") Post post,
            Model model
            ) throws PermissionDeniedException {
        if (!curUser.getId().equals(post.getAuthorId())) {
            throw new PermissionDeniedException();
        }
        model.addAttribute("post", post);
        model.addAttribute("user", user);
        return "postEdit";
    }

    @PostMapping("/edit/{authorId}")
    public String saveChanges(
            @Valid Post post,
            @RequestParam("file") MultipartFile file,
            BindingResult bindingResult,
            Model model
    ) throws IOException {

        //проверяем на ошибки по аннотациям из класса Message
        if (bindingResult.hasErrors()) {
            model.mergeAttributes(ControllerUtils.getErrorMap(bindingResult));
            model.addAttribute("post", post);
        } else {
            boolean res = postService.addPost(post, file);
            model.addAttribute("status", "Saved");
            model.addAttribute("post", post);
        }

        return "postEdit";
    }

}
