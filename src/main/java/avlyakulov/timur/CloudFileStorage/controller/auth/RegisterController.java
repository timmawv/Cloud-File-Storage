package avlyakulov.timur.CloudFileStorage.controller.auth;

import avlyakulov.timur.CloudFileStorage.user.UserDto;
import avlyakulov.timur.CloudFileStorage.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegisterController {

    private final UserService userService;

    @GetMapping
    public String getRegisterPage(Model model) {
        model.addAttribute("user", new UserDto());
        return "auth/registration";
    }

    @PostMapping
    public String registerUser(@ModelAttribute("user") @Valid UserDto userDto, BindingResult bindingResult, Model model) {
        return userService.saveUser(userDto, bindingResult, model);
    }
}