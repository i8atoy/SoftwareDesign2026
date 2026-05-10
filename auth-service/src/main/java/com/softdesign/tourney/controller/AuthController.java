package com.softdesign.tourney.controller;


import com.softdesign.tourney.models.UserEntity;
import jakarta.validation.Valid;
import org.springframework.ui.Model;
import com.softdesign.tourney.dto.RegistrationDto;
import com.softdesign.tourney.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {
    private UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String getRegister(Model model) {
        RegistrationDto registrationDto = new RegistrationDto();
        model.addAttribute("registrationDto", registrationDto);
        return "register";
    }

    @PostMapping("/register/save")
    public String saveRegister(@Valid @ModelAttribute("registrationDto") RegistrationDto registrationDto,
                               BindingResult bindingResult,
                               Model model) {

        UserEntity existingUser = userService.findByUsername(registrationDto.getUserName());
        if (existingUser != null && existingUser.getUserName() != null) {
            bindingResult.rejectValue("username", "error.user", "This username is already taken");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("registrationDto", registrationDto);
            return "register";
        }

        userService.saveUser(registrationDto);
        return "redirect:/login?success";
    }

    @GetMapping("/login")
    public String getLogin() {
        return "login";
    }



}
