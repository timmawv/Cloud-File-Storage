package avlyakulov.timur.CloudFileStorage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/files")
public class FilesController {

    @GetMapping
    public String getFilesPage() {
        return "pages/files-page";
    }

    @PostMapping
    public String uploadFileToServer(@RequestParam("file") MultipartFile file) {
        System.out.println(file);
        return "redirect:/files";
    }
}