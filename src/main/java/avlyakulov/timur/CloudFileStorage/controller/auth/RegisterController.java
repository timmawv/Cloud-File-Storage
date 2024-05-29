package avlyakulov.timur.CloudFileStorage.controller.auth;

import avlyakulov.timur.CloudFileStorage.exceptions.UserLoginAlreadyExistException;
import avlyakulov.timur.CloudFileStorage.user.UserDto;
import avlyakulov.timur.CloudFileStorage.user.UserService;
import avlyakulov.timur.CloudFileStorage.util.validator.LoginAndPasswordValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/registration")
@PreAuthorize("isAnonymous()")
@RequiredArgsConstructor
public class RegisterController {

    private final UserService userService;

    private final LoginAndPasswordValidator loginAndPasswordValidator;

    @GetMapping
    public String getRegisterPage(Model model) {
        model.addAttribute("user", new UserDto());
        return "/auth/registration";
    }

    @PostMapping
    public String registerUser(@ModelAttribute("user") @Valid UserDto userDto, BindingResult bindingResult, Model model) {
        loginAndPasswordValidator.validate(userDto, bindingResult);
        if (bindingResult.hasErrors()) {
            return "/auth/registration";
        }
        try {
            userService.saveUser(userDto);
        } catch (UserLoginAlreadyExistException e) {
            bindingResult.rejectValue("login", "", e.getMessage());
            return "/auth/registration";
        }
        model.addAttribute("success_registration", true);
        return "/auth/registration";
    }
}