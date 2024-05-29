package avlyakulov.timur.CloudFileStorage.minio;

import avlyakulov.timur.CloudFileStorage.config.security.PersonDetails;
import avlyakulov.timur.CloudFileStorage.minio.dto.DirRequest;
import avlyakulov.timur.CloudFileStorage.minio.dto.FileRequest;
import avlyakulov.timur.CloudFileStorage.minio.dto.FileResponse;
import avlyakulov.timur.CloudFileStorage.minio.dto.FileRenameRequest;
import avlyakulov.timur.CloudFileStorage.util.converter.BreadcrumbConverter;
import avlyakulov.timur.CloudFileStorage.util.strings.StringFileUtils;
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
public class MinioController {

    private final MinioService minioService;

    @GetMapping
    public String getFilesPage(@AuthenticationPrincipal PersonDetails personDetails,
                               @RequestParam(name = "path", required = false) Optional<String> path, Model model) {
        model.addAttribute("login", personDetails.getUsername());
        model.addAttribute("capacity", minioService.getUserCapacity(personDetails.getUserId()));
        String pathFromUrl = path.orElse(StringFileUtils.EMPTY_STRING);
        model.addAttribute("path", pathFromUrl);
        List<FileResponse> files = minioService.getUserFiles(pathFromUrl, personDetails.getUserId());
        model.addAttribute("files", files);
        if (pathFromUrl.isBlank()) {
            return "pages/files-page";
        }
        String[] breadcrumb = BreadcrumbConverter.convertPathToBreadcrumb(pathFromUrl);
        model.addAttribute("pathList", breadcrumb);
        return "pages/files-page";
    }

    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadFile(@AuthenticationPrincipal PersonDetails personDetails, @RequestParam("file") String filePath) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodeStringUtf8(StringFileUtils.getFileName(filePath)) + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(minioService.downloadFile(filePath, personDetails.getUserId())));
    }

    @GetMapping("/search")
    public String searchFile(@AuthenticationPrincipal PersonDetails personDetails, @RequestParam("query") String filePrefix, Model model) {
        model.addAttribute("login", personDetails.getUsername());
        List<FileResponse> files = minioService.searchFiles(filePrefix, personDetails.getUserId());
        model.addAttribute("files", files);
        return "pages/search-page";
    }

    @PostMapping
    public String uploadFileToServer(@AuthenticationPrincipal PersonDetails personDetails, FileRequest fileRequest) {
        minioService.uploadFile(fileRequest, personDetails.getUserId());
        return "redirect:/files?path=".concat(encodeStringUtf8(fileRequest.getPath()));
    }

    @PostMapping("/directory")
    public String uploadEmptyDirToServer(@AuthenticationPrincipal PersonDetails personDetails, DirRequest dirRequest) {
        minioService.uploadEmptyDir(dirRequest, personDetails.getUserId());
        return "redirect:/files?path=".concat(encodeStringUtf8(dirRequest.getPath()));
    }

    @DeleteMapping
    public String deleteFile(@AuthenticationPrincipal PersonDetails personDetails, @RequestParam("isDir") Boolean isDir, @RequestParam("file") String filePath) {
        String pathToFileDirectory = minioService.removeFile(filePath, isDir, personDetails.getUserId());
        return "redirect:/files?path=".concat(encodeStringUtf8(pathToFileDirectory));
    }

    @PatchMapping
    public String updateFileName(@AuthenticationPrincipal PersonDetails personDetails, @ModelAttribute("renameFileForm") FileRenameRequest fileRenameRequest) {
        String pathToFile = minioService.updateFileName(fileRenameRequest, personDetails.getUserId());
        return "redirect:/files?path=".concat(encodeStringUtf8(pathToFile));
    }

    private String encodeStringUtf8(String string) {
        return URLEncoder.encode(string, StandardCharsets.UTF_8);
    }
}