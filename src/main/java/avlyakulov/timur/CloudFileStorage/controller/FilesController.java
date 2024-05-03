package avlyakulov.timur.CloudFileStorage.controller;

import avlyakulov.timur.CloudFileStorage.config.security.PersonDetails;
import avlyakulov.timur.CloudFileStorage.minio.MinioService;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/files")
public class FilesController {

    private MinioService minioService;

    @Autowired
    public FilesController(MinioService minioService) {
        this.minioService = minioService;
    }

    @GetMapping
    public String getFilesPage(@AuthenticationPrincipal PersonDetails personDetails,
                               @RequestParam(name = "path", required = false) Optional<String> path,
                               Model model) {
        if (personDetails == null)
            return "/pages/main-page";
        model.addAttribute("login", personDetails.getUsername());

        String pathFromUrl = "/";
        if (path.isPresent())
            pathFromUrl = path.get().concat("/");

        Iterable<Result<Item>> objectsFromStorage = minioService.getObjectsFromStorage(personDetails.getUserId(), pathFromUrl);

        objectsFromStorage.forEach(f -> {
            try {
                System.out.println(f.get().objectName());

                System.out.println(f.get().isDir());
            } catch (Exception e) {
                log.error("Error minio in controller");
            }
        });
        return "pages/files-page";
    }

    @PostMapping
    public String uploadFileToServer(@AuthenticationPrincipal PersonDetails personDetails,
                                     @RequestPart("file") MultipartFile file) {
        Integer userId = personDetails.getUserId();
        minioService.uploadFile(file, userId);
        return "redirect:/files";
    }
}