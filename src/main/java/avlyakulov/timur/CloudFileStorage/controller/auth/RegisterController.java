package avlyakulov.timur.CloudFileStorage.controller.auth;

import avlyakulov.timur.CloudFileStorage.custom_exceptions.UserLoginAlreadyExistException;
import avlyakulov.timur.CloudFileStorage.dto.UserDto;
import avlyakulov.timur.CloudFileStorage.service.UserService;
import avlyakulov.timur.CloudFileStorage.util.validator.LoginAndPasswordValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
public class RegisterController {

    private UserService userService;

    private LoginAndPasswordValidator loginAndPasswordValidator;

    @Autowired
    public RegisterController(UserService userService, LoginAndPasswordValidator loginAndPasswordValidator) {
        this.userService = userService;
        this.loginAndPasswordValidator = loginAndPasswordValidator;
    }

    @GetMapping
    public String getRegisterPage(Model model) {
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);
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