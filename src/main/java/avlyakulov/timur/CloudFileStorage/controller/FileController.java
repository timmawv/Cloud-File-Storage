package avlyakulov.timur.CloudFileStorage.controller;

import avlyakulov.timur.CloudFileStorage.config.security.PersonDetails;
import avlyakulov.timur.CloudFileStorage.dto.CreateDirRequest;
import avlyakulov.timur.CloudFileStorage.dto.CreateFileDto;
import avlyakulov.timur.CloudFileStorage.dto.FileResponse;
import avlyakulov.timur.CloudFileStorage.dto.UpdateFileNameDto;
import avlyakulov.timur.CloudFileStorage.minio.MinioService;
import avlyakulov.timur.CloudFileStorage.util.converter.PathToBreadcrumbConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        List<FileResponse> files = minioService.getUserFiles(pathFromUrl, personDetails.getUserId());
        model.addAttribute("files", files);
        model.addAttribute("path", pathFromUrl);
        if (pathFromUrl.isBlank()) {
            return "pages/files-page";
        }
        String[] links = PathToBreadcrumbConverter.convertPathToBreadcrumb(pathFromUrl);
        model.addAttribute("pathList", links);
        return "pages/files-page";
    }

    @GetMapping("/upload")
    public ResponseEntity<Object> downloadFile(@AuthenticationPrincipal PersonDetails personDetails, @RequestParam("file") String filePath) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodeStringUtf8(getFileName(filePath)) + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(minioService.downloadFile(filePath, personDetails.getUserId())));
    }

    @GetMapping("/search")
    public String searchFile(@AuthenticationPrincipal PersonDetails personDetails, @RequestParam("query") String filePrefix, Model model) {
        model.addAttribute("login", personDetails.getUsername());
        //todo make validation for file query search, now we can search empty files
        List<FileResponse> files = minioService.searchFiles(filePrefix, personDetails.getUserId());
        model.addAttribute("files", files);
        return "pages/search-page";
    }

    @PostMapping
    public String uploadFileToServer(@AuthenticationPrincipal PersonDetails personDetails, CreateFileDto createFileDto) {
        minioService.uploadFile(createFileDto, personDetails.getUserId());
        return "redirect:/files?path=".concat(encodeStringUtf8(createFileDto.getPath()));
    }

    @PostMapping("/directory")
    public String uploadEmptyDirToServer(@AuthenticationPrincipal PersonDetails personDetails, CreateDirRequest createDirRequest) {
        minioService.uploadEmptyDir(createDirRequest, personDetails.getUserId());
        return "redirect:/files?path=".concat(encodeStringUtf8(createDirRequest.getPath()));
    }

    @DeleteMapping
    public String deleteFile(@AuthenticationPrincipal PersonDetails personDetails, @RequestParam("isDir") Boolean isDir, @RequestParam("file") String filePath) {
        String pathToFileDirectory = minioService.removeFile(filePath, isDir, personDetails.getUserId());
        return "redirect:/files?path=".concat(encodeStringUtf8(pathToFileDirectory));
    }

    @PatchMapping
    public String updateFileName(@AuthenticationPrincipal PersonDetails personDetails, @ModelAttribute("renameFileForm") UpdateFileNameDto updateFileNameDto) {
        String pathToFile = minioService.updateFileName(updateFileNameDto, personDetails.getUserId());
        return "redirect:/files?path=".concat(encodeStringUtf8(pathToFile));
    }

    private String encodeStringUtf8(String string) {
        return URLEncoder.encode(string, StandardCharsets.UTF_8);
    }

    private String getFileName(String filePath) {
        int lastIndexSlash = filePath.lastIndexOf("/");
        return filePath.substring(lastIndexSlash + 1);
    }
}