package com.utkanos.sweater.controllers;

import com.utkanos.sweater.domains.Post;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/post")
public class PostViewController {

    @GetMapping("/{id}")
    public String getPostInfo(
            @PathVariable("id") Post post,
            Model model
            ) {
        model.addAttribute("post", post);
        return "postPage";
    }

}
