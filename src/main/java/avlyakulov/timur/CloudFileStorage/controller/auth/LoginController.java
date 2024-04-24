package avlyakulov.timur.CloudFileStorage.controller.auth;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
@PreAuthorize("isAnonymous()")
public class LoginController {

    @GetMapping
    public String getLoginPage() {
        return "/auth/login";
    }
}
