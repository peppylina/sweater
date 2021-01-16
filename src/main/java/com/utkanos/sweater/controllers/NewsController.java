package com.utkanos.sweater.controllers;

import com.utkanos.sweater.domains.Post;
import com.utkanos.sweater.domains.User;
import com.utkanos.sweater.service.PostService;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

@Controller
public class NewsController {

    @Autowired
    private PostService postService;


    //@RequestParam - запрашиваемый параметр, ищется из url или из форм post метода. required - обязательный параметр или нет
    @GetMapping()
    public String greeting(Map<String, Object> model) {
        return "index";
    }


    @GetMapping("/news")
    public String news(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false, defaultValue = "") String tagFilter,
            @RequestParam(required = false, defaultValue = "") String textFilter,
            @PageableDefault(sort = {"t.time"} , direction = Sort.Direction.DESC) Pageable pageable,
            Model model
    ) {
        Page<Post> page;
        //если у нас какой либо из фильтров не пустой, то берем сообщения по этому фильтру
        if (tagFilter != null && !tagFilter.isEmpty()) {
            page = postService.findMyByTag(tagFilter, user, pageable);
        } else if(textFilter != null && !textFilter.isEmpty()) {
            page = postService.findMyByText(textFilter, user, pageable);
        } else {
            page = postService.findMyAll(user, pageable);
        }

        model.addAttribute("page", page);
        model.addAttribute("url", "/news");
        model.addAttribute("textFilter", textFilter);
        model.addAttribute("tagFilter", tagFilter);

        return "news";
    }

    //пост запрос для отлавливания отправки формы с главного экрана
    @PostMapping("/news")
    public String addPost(
            @AuthenticationPrincipal User user,
            @Valid Post post,
            BindingResult bindingResult,
            Model model,
            @RequestParam(required = false, defaultValue = "0") String startIndex,
            @RequestParam("file") MultipartFile file,
            @PageableDefault(sort = {"t.time"} , direction = Sort.Direction.DESC) Pageable pageable
    ) throws IOException, FileSizeLimitExceededException {
        post.setAuthor(user);

        //проверяем на ошибки по аннотациям из класса Message
        if (bindingResult.hasErrors()) {
            model.mergeAttributes(ControllerUtils.getErrorMap(bindingResult));
            model.addAttribute("post", post);
        } else {
            boolean res = postService.addPost(post, file);
            model.addAttribute("response", "OK");
        }

        Page<Post> page = postService.findMyAll(user, pageable);

        model.addAttribute("url", "/news");
        model.addAttribute("page", page);
        return "news";
    }

}
