package com.utkanos.sweater.controllers;

import com.utkanos.sweater.domains.Message;
import com.utkanos.sweater.domains.User;
import com.utkanos.sweater.service.MessageService;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Stream;

@Controller
public class MainController {

    @Autowired
    private MessageService messageService;


    //@RequestParam - запрашиваемый параметр, ищется из url или из форм post метода. required - обязательный параметр или нет
    @GetMapping()
    public String greeting(Map<String, Object> model) {
        return "index";
    }


    @GetMapping("/main")
    public String main(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false, defaultValue = "") String tagFilter,
            @RequestParam(required = false, defaultValue = "") String textFilter,
            Map<String, Object> model
    ) {
        ArrayList<Message> messages = new ArrayList<>();
        //если у нас какой либо из фильтров не пустой, то берем сообщения по этому фильтру
        if (tagFilter != null && !tagFilter.isEmpty()) {
            messageService.findByTag(tagFilter).forEach(messages::add);
        } else if(textFilter != null && !textFilter.isEmpty()) {
            messageService.findByText(textFilter).forEach(messages::add);
        } else {
            messageService.findAll().forEach(messages::add);
        }

        messages.sort((mess1, mess2) -> mess2.getTime().compareTo(mess1.getTime()));

        model.put("messages", messages);
        model.put("textFilter", textFilter);
        model.put("tagFilter", tagFilter);

        return "main";
    }

    //пост запрос для отлавливания отправки формы с главного экрана
    @PostMapping("/main")
    public String addMessage(
            @AuthenticationPrincipal User user,
            @Valid Message message,
            BindingResult bindingResult,
            Model model,
            @RequestParam("file") MultipartFile file
    ) throws IOException, FileSizeLimitExceededException {
        message.setAuthor(user);

        //проверяем на ошибки по аннотациям из класса Message
        if (bindingResult.hasErrors()) {
            model.mergeAttributes(ControllerUtils.getErrorMap(bindingResult));
            model.addAttribute("message", message);
        } else {
            boolean res = messageService.addMessage(message, file);
            model.addAttribute("response", "OK");
        }

        return "redirect:/main";
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
