package com.utkanos.sweater.controllers;

import com.utkanos.sweater.domains.User;
import com.utkanos.sweater.domains.dto.CaptchaResponseDTO;
import com.utkanos.sweater.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;

@Controller
@RequestMapping("/registration")
public class RegistrationController {

    private final static String CAPTCHA_URL =  "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s";

    @Autowired
    private UserService userService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${recaptcha.secret}")
    private String recaptchaSecret;

    @GetMapping
    public String registration() {
        return "registration";
    }

    @PostMapping
    public String regUser(
            @RequestParam(name = "g-recaptcha-response") String captchaResponse,
            @Valid User user,
            BindingResult bindingResult,
            Model model
    ) {
        String urlFromCaptcha = String.format(CAPTCHA_URL, recaptchaSecret, captchaResponse);
        CaptchaResponseDTO response =
                restTemplate.postForObject(urlFromCaptcha, Collections.emptyList(), CaptchaResponseDTO.class);

        if (response == null || !response.isSuccess()) {
            model.addAttribute("captchaError", "Refill the captcha!");
            return "registration";
        }

        if (user.getPassword() != null && !user.getPassword().equals(user.getPassword2())) {
            model.addAttribute("passwordError", "passwords are not equals");
            return "registration";
        }

        if(bindingResult.hasErrors()) {
            Map<String, String> errorsMap = ControllerUtils.getErrorMap(bindingResult);
            model.mergeAttributes(errorsMap);
            return "registration";
        }

        boolean result = userService.addNewUser(user);
        if (result) {
            model.addAttribute("message", "Good! Go on link in email message to activate account!");
            return "login";
        }
        else {
            model.addAttribute("usernameError", "This username is not free!");
            return "registration";
        }

    }

    @GetMapping("/activation/{activationCode}")
    public String activateUser(
            @PathVariable String activationCode,
            Model model
    ) {
        try{
            boolean activationResult = userService.activateUser(activationCode);
            String message = activationResult ? "Activation success!" : "No such activation code!";
            String mode = activationResult ? "success" : "danger";
            model.addAttribute("message", message);
            model.addAttribute("mode", mode);
        } catch (Throwable th) {
            model.addAttribute("message", th.getMessage());
            model.addAttribute("mode", "danger");
        }
        return "login";
    }
}
