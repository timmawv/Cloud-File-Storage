package avlyakulov.timur.CloudFileStorage.controller;

import avlyakulov.timur.CloudFileStorage.custom_exceptions.MinioGlobalFileException;
import avlyakulov.timur.CloudFileStorage.custom_exceptions.SearchQueryException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxUploadSize;

    @ExceptionHandler({MinioGlobalFileException.class})
    public String handleMinioGlobalException(Exception e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("fileUploadError", e.getMessage());
        return "redirect:/files";
    }

    @ExceptionHandler({SearchQueryException.class})
    public String handleSearchQueryException(Exception e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("fileSearchError", e.getMessage());
        return "redirect:/files";
    }

    @ExceptionHandler({MaxUploadSizeExceededException.class})
    public String handleMaxUploadSize(MaxUploadSizeExceededException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("fileUploadError", "Your file is too large. Max upload size is " + maxUploadSize);
        return "redirect:/files";
    }
}