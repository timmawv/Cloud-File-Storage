package avlyakulov.timur.CloudFileStorage.controller;

import avlyakulov.timur.CloudFileStorage.config.security.PersonDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/main-page")
public class MainPageController {

    @GetMapping
    public String getMainPage(@AuthenticationPrincipal PersonDetails personDetails, Model model) {
        if (personDetails == null)
            return "pages/main-page";
        model.addAttribute("login", personDetails.getUsername());
        return "pages/main-page";
    }
}