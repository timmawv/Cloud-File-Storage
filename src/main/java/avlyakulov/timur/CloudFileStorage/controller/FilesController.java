package avlyakulov.timur.CloudFileStorage.controller;

import avlyakulov.timur.CloudFileStorage.config.security.PersonDetails;
import avlyakulov.timur.CloudFileStorage.dto.FileResponse;
import avlyakulov.timur.CloudFileStorage.mapper.FileMapper;
import avlyakulov.timur.CloudFileStorage.minio.MinioService;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/files")
@RequiredArgsConstructor
public class FilesController {

    private final MinioService minioService;

    private final FileMapper fileMapper;

    @GetMapping
    public String getFilesPage(@AuthenticationPrincipal PersonDetails personDetails,
                               @RequestParam(name = "path", required = false) Optional<String> path,
                               Model model) {
        model.addAttribute("login", personDetails.getUsername());

        String pathFromUrl = "/";
        if (path.isPresent())
            pathFromUrl = path.get().concat("/");

        Iterable<Result<Item>> objectsFromStorage = minioService.getObjectsFromStorage(personDetails.getUserId(), pathFromUrl);
        List<Item> filesInDir = new ArrayList<>();
        for (Result<Item> item : objectsFromStorage) {
            try {
                filesInDir.add(item.get());
            } catch (Exception e) {
                log.error("Error during adding file to list in controller");
            }
        }
        List<FileResponse> fileResponses = fileMapper.mapFileToResponse(filesInDir);
        model.addAttribute("files", fileResponses);
        return "pages/files-page";
    }

    @PostMapping
    public String uploadFileToServer(@AuthenticationPrincipal PersonDetails personDetails,
                                     @RequestPart("file") MultipartFile[] files) {
        Integer userId = personDetails.getUserId();
        minioService.uploadFile(files, userId);
        return "redirect:/files";
    }

    @DeleteMapping
    public String deleteFile(@AuthenticationPrincipal PersonDetails personDetails, @ModelAttribute("file") String fileName) {
        String pathFromUrl = "/";
        String pathFileName = pathFromUrl.concat(fileName);
        minioService.deleteFile(pathFileName, personDetails.getUserId());
        return "redirect:/files";
    }

    @PatchMapping
    public String updateFileName(@AuthenticationPrincipal PersonDetails personDetails, @RequestParam("newFileName") String newFileName, @RequestParam("oldFileName") String oldFileName) {
        String[] split = oldFileName.split("\\.");
        String pathFromUrl = "/";
        String pathNewFileName = pathFromUrl.concat(newFileName).concat(".".concat(split[1]));
        String pathOldFileName = pathFromUrl.concat(oldFileName);
        minioService.updateFileName(pathNewFileName, pathOldFileName, personDetails.getUserId());
        return "redirect:/files";
    }
}