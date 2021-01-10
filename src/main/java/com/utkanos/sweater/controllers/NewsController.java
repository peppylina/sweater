package com.utkanos.sweater.controllers;

import com.utkanos.sweater.domains.Post;
import com.utkanos.sweater.domains.User;
import com.utkanos.sweater.service.PostService;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.beans.factory.annotation.Autowired;
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
            @RequestParam(required = false, defaultValue = "0") String startIndex,
            Model model
    ) {
        ArrayList<Post> posts = new ArrayList<>();
        //если у нас какой либо из фильтров не пустой, то берем сообщения по этому фильтру
        if (tagFilter != null && !tagFilter.isEmpty()) {
            postService.findMyByTag(tagFilter, user).forEach(posts::add);
        } else if(textFilter != null && !textFilter.isEmpty()) {
            postService.findMyByText(textFilter, user).forEach(posts::add);
        } else {
            postService.findMyAll(user, Long.valueOf(startIndex)).forEach(posts::add);
        }

        posts.sort((mess1, mess2) -> mess2.getTime().compareTo(mess1.getTime()));

        model.addAttribute("posts", posts);
        model.addAttribute("textFilter", textFilter);
        model.addAttribute("tagFilter", tagFilter);
        model.addAttribute("startIndex", startIndex);

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
            @RequestParam("file") MultipartFile file
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

        ArrayList<Post> posts = new ArrayList<>();
        postService.findMyAll(user, Long.valueOf(startIndex)).forEach(posts::add);
        posts.sort(Comparator.comparing(Post::getTime));

        model.addAttribute("posts", posts);
        model.addAttribute("startIndex", startIndex);

        return "news";
    }

    @GetMapping("/news/more")
    public String getMoreNews(
            @AuthenticationPrincipal User user,
            @RequestParam Long startIndex,
            Model model
    ) {
        startIndex += 5;
        ArrayList<Post> posts = new ArrayList<>();
        postService.findMyAll(user, startIndex).forEach(posts::add);
        if (posts.isEmpty()) {
            startIndex -= 5;
            postService.findMyAll(user, startIndex).forEach(posts::add);
        }
        model.addAttribute("startIndex", startIndex);
        model.addAttribute("posts", posts);
        return "news";
    }

    @GetMapping("/news/less")
    public String getLessNews(
            @AuthenticationPrincipal User user,
            @RequestParam Long startIndex,
            Model model
    ) {
        startIndex -= 5;
        if (startIndex < 0) startIndex += 5;
        ArrayList<Post> posts = new ArrayList<>();
        postService.findMyAll(user, startIndex).forEach(posts::add);
        if (posts.isEmpty()) {
            startIndex += 5;
            postService.findMyAll(user, startIndex).forEach(posts::add);
        }
        model.addAttribute("startIndex", startIndex);
        model.addAttribute("posts", posts);
        return "news";
    }





















    //архив

    /*все запихнули в get main
    @PostMapping("/filter_by_tag")
    public String filterByTag(
            @RequestParam String tagFilter, Map<String, Object> model
    ) {
        if (tagFilter == null || tagFilter.isEmpty()) {
            return "redirect:/main";
        }

        Iterable<Message> messages = messageRepo.findByTag(tagFilter);

        model.put("messages", messages);

        return "main";
    }*/

    /*все запихнули в get main
    @PostMapping("/filter_by_text")
    public String filterByText(
            @RequestParam String textFilter, Map<String, Object> model
    ) {
        if (textFilter == null || textFilter.isEmpty()) {
            return "redirect:/main";
        }
        Iterable<Message> messages = messageRepo.findByText(textFilter);

        model.put("messages", messages);

        return "main";
    }*/
}
