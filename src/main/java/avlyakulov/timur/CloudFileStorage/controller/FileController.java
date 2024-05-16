package avlyakulov.timur.CloudFileStorage.controller;

import avlyakulov.timur.CloudFileStorage.config.security.PersonDetails;
import avlyakulov.timur.CloudFileStorage.dto.CreateFileDto;
import avlyakulov.timur.CloudFileStorage.dto.FileResponse;
import avlyakulov.timur.CloudFileStorage.dto.UpdateFileNameDto;
import avlyakulov.timur.CloudFileStorage.minio.MinioService;
import avlyakulov.timur.CloudFileStorage.util.converter.PathToBreadcrumbConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final MinioService minioService;

    @GetMapping
    public String getFilesPage(@AuthenticationPrincipal PersonDetails personDetails,
                               @RequestParam(name = "path", required = false) Optional<String> path, Model model) {
        model.addAttribute("login", personDetails.getUsername());
        String pathFromUrl = path.orElse("");
        List<FileResponse> files = minioService.getUserFiles(personDetails.getUserId(), pathFromUrl);
        model.addAttribute("files", files);
        model.addAttribute("path", pathFromUrl);
        if (pathFromUrl.isBlank()) {
            return "pages/files-page";
        }
        String[] links = PathToBreadcrumbConverter.convertPathToBreadcrumb(pathFromUrl);
        model.addAttribute("pathList", links);
        return "pages/files-page";
    }

    @PostMapping
    public String uploadFileToServer(@AuthenticationPrincipal PersonDetails personDetails,
                                     @RequestPart("file") MultipartFile[] files,
                                     @RequestParam(name = "path") String pathFromUrl) {
        CreateFileDto createFileDto = new CreateFileDto(pathFromUrl, files);
        minioService.uploadFile(createFileDto, personDetails.getUserId());
        return "redirect:/files?path=".concat(encodePathToFile(pathFromUrl));
    }

    @DeleteMapping
    public String deleteFile(@AuthenticationPrincipal PersonDetails personDetails, @RequestParam("isDir") Boolean isDir, @RequestParam("file") String filePath) {
        String pathToFileDirectory = minioService.deleteFile(filePath, isDir, personDetails.getUserId());
        return "redirect:/files?path=".concat(encodePathToFile(pathToFileDirectory));
    }

    @PatchMapping
    public String updateFileName(@AuthenticationPrincipal PersonDetails personDetails, @ModelAttribute("renameFileForm") UpdateFileNameDto updateFileNameDto) {
        String pathToFile = minioService.updateFileName(updateFileNameDto, personDetails.getUserId());
        return "redirect:/files?path=".concat(encodePathToFile(pathToFile));
    }

    private String encodePathToFile(String filePath) {
        return URLEncoder.encode(filePath, StandardCharsets.UTF_8);
    }
}