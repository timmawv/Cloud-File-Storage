package avlyakulov.timur.CloudFileStorage.minio;

import avlyakulov.timur.CloudFileStorage.IntegrationBaseTest;
import avlyakulov.timur.CloudFileStorage.minio.dto.FileRenameRequest;
import avlyakulov.timur.CloudFileStorage.minio.dto.FileRequest;
import avlyakulov.timur.CloudFileStorage.minio.dto.FileResponse;
import avlyakulov.timur.CloudFileStorage.user.User;
import avlyakulov.timur.CloudFileStorage.user.UserRepository;
import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = IntegrationBaseTest.class)
class MinioServiceTest {

    @Autowired
    private MinioService minioService;

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("timur", "hardPassword123");
    }

    @BeforeAll
    void clearUsers() {
        userRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void baseMinioConfiguration_mainBucketWasCreated() throws Exception {
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket("user-files").build());
        assertThat(found).isTrue();
    }

    @Test
    void uploadFile_fileWasUploaded() {
        userRepository.save(user);
        Integer userId = userRepository.findByLogin(user.getLogin()).get().getId();
        MultipartFile multipartFile = new MockMultipartFile("video", "video.mp4", "video/mp4", "content".getBytes());
        FileRequest fileRequest = new FileRequest("dir/dir/", new MultipartFile[]{multipartFile});

        minioService.uploadFile(fileRequest, userId);

        List<FileResponse> userFiles = minioService.getUserFiles("dir/dir/", userId);
        assertThat(userFiles).hasSize(1);
        FileResponse fileResponse = userFiles.get(0);
        assertThat(fileResponse.getObjectName()).isEqualTo(multipartFile.getOriginalFilename());
    }

    @Test
    void uploadFile_anotherUserCantGetIt() {
        userRepository.save(user);
        userRepository.save(new User("dima", "hardPassword123"));
        Integer firstUserId = userRepository.findByLogin(user.getLogin()).get().getId();
        Integer secondUserId = userRepository.findByLogin("dima").get().getId();
        MultipartFile multipartFile = new MockMultipartFile("video", "video.mp4", "video/mp4", "content".getBytes());
        FileRequest fileRequest = new FileRequest("dir/dir/", new MultipartFile[]{multipartFile});

        minioService.uploadFile(fileRequest, firstUserId);

        List<FileResponse> userFiles = minioService.getUserFiles("dir/dir/", secondUserId);
        assertThat(userFiles).hasSize(0);
    }

    @Test
    void renameFile_renameWasCompleted() {
        userRepository.save(user);
        Integer userId = userRepository.findByLogin(user.getLogin()).get().getId();
        MultipartFile multipartFile = new MockMultipartFile("video", "video.mp4", "video/mp4", "content".getBytes());
        FileRequest fileRequest = new FileRequest("dir/dir/", new MultipartFile[]{multipartFile});
        minioService.uploadFile(fileRequest, userId);

        minioService.updateFileName(new FileRenameRequest("video.mp4", "dir/dir/video.mp4", "vidos", false), userId);

        List<FileResponse> userFiles = minioService.getUserFiles("dir/dir/", userId);
        assertThat(userFiles.get(0).getObjectName()).isEqualTo("vidos.mp4");
    }

    @Test
    void deleteFile_fileWasDeleted() {
        userRepository.save(user);
        Integer userId = userRepository.findByLogin(user.getLogin()).get().getId();
        MultipartFile multipartFile = new MockMultipartFile("video", "video.mp4", "video/mp4", "content".getBytes());
        FileRequest fileRequest = new FileRequest("dir/dir/", new MultipartFile[]{multipartFile});
        minioService.uploadFile(fileRequest, userId);

        minioService.removeFile("dir/dir/video.mp4", false, userId);

        List<FileResponse> userFiles = minioService.getUserFiles("dir/dir/", userId);
        assertThat(userFiles).hasSize(0);
    }

    @Test
    void searchFiles_fileWasFound() {
        userRepository.save(user);
        Integer userId = userRepository.findByLogin(user.getLogin()).get().getId();
        MultipartFile multipartFile = new MockMultipartFile("video", "video.mp4", "video/mp4", "content".getBytes());
        FileRequest fileRequest = new FileRequest("dir/dir/", new MultipartFile[]{multipartFile});
        minioService.uploadFile(fileRequest, userId);

        List<FileResponse> searchFiles = minioService.searchFiles("vi", userId);

        assertThat(searchFiles).hasSize(1);
        assertThat(searchFiles.get(0).getObjectName()).isEqualTo(multipartFile.getOriginalFilename());
    }

    @Test
    void searchFiles_anotherUserTryToFound_fileNotFound() {
        userRepository.save(user);
        userRepository.save(new User("dima", "hardPassword123"));
        Integer firstUserId = userRepository.findByLogin(user.getLogin()).get().getId();
        Integer secondUserId = userRepository.findByLogin("dima").get().getId();
        MultipartFile multipartFile = new MockMultipartFile("video", "video.mp4", "video/mp4", "content".getBytes());
        FileRequest fileRequest = new FileRequest("dir/dir/", new MultipartFile[]{multipartFile});
        minioService.uploadFile(fileRequest, firstUserId);

        List<FileResponse> searchFiles = minioService.searchFiles("vi", secondUserId);
        assertThat(searchFiles).hasSize(0);
    }
}