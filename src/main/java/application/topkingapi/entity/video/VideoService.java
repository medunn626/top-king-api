package application.topkingapi.entity.video;

import application.topkingapi.model.Video;
import application.topkingapi.repo.VideoRepo;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VideoService {
    private final VideoRepo videoRepo;

    private static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    @Autowired
    public VideoService(VideoRepo videoRepo) {
        this.videoRepo = videoRepo;
    }

    public Video uploadVideo(MultipartFile file, String name, List<String> tiers) throws IOException {
        var docType = file.getContentType();
        var tiersToAdd = new ArrayList<>(tiers);
        tiersToAdd.add("admin");
        var driveId = saveToDrive(file);

        Video videoToSave = new Video(name, docType, driveId, tiers);

        return videoRepo.save(videoToSave);
    }

    private String saveToDrive(MultipartFile multipartFile) throws IOException {
        // Create credentials and app
        GoogleCredential credential = GoogleCredential
                .fromStream(new FileInputStream("src/main/resources/keys/client_secret_gd.json"))
                .createScoped(List.of("https://www.googleapis.com/auth/drive"));
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName("Top King Training")
                .build();

        // Create Google File
        File file = new File();
        file.setName(multipartFile.getOriginalFilename());

        // Create Java File
        java.io.File convFile = new java.io.File("targetFile.tmp");
        convFile.createNewFile();

        // Populate Java File with Request's MultipartFile info
        try(InputStream multipartFileInputStream = multipartFile.getInputStream()) {
            Files.copy(multipartFileInputStream, convFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        // Populate Google File with Java File info
        FileContent content = new FileContent(multipartFile.getContentType(), convFile);

        // Upload Google File
        File savedFile = service.files().create(file, content)
                .setFields("id")
                .execute();

        // Make link viewable to anyone
        Permission permission = new Permission();
        permission.setType("anyone");
        permission.setRole("reader");
        service.permissions().create(savedFile.getId(), permission).execute();

        return savedFile.getId();
    }

    public List<Video> getVideosForTier(String userTier) throws IOException {
        var videos = videoRepo.findAll().stream()
                .filter(video -> video.getProductTiersAppliedTo().contains(userTier))
                .toList();
        return getFromDriveAndUpdateVideo(videos);
    }

    public List<Video> getVideoSummaryForAdmin() {
        return videoRepo.findAll();
    }

    private List<Video> getFromDriveAndUpdateVideo(List<Video> retrievedVideos) throws IOException {
        // Create credentials and app
        GoogleCredential credential = GoogleCredential
                .fromStream(new FileInputStream("src/main/resources/keys/client_secret_gd.json"))
                .createScoped(List.of("https://www.googleapis.com/auth/drive"));
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName("Top King Training")
                .build();

        // Get Files and return source links on videos
        List<Video> videosToReturn = new ArrayList<>();
        var fileList = service.files().list().setFields("files(id, webContentLink)").execute();
        for (var vid : retrievedVideos) {
            var matchingDriveVidSourceLink = fileList.getFiles().stream()
                    .filter(file -> vid.getDriveId().equals(file.getId()))
                    .map(File::getWebContentLink)
                    .collect(Collectors.joining());
            vid.setDriveSourceLink(matchingDriveVidSourceLink);
            videosToReturn.add(vid);
        }
        return videosToReturn;
    }

    public void deleteVideo(Long videoId) throws IOException {
        var videoRecord = videoRepo.getReferenceById(videoId);
        var driveId = videoRecord.getDriveId();

        deleteFromDrive(driveId);
        videoRepo.deleteById(videoId);
    }

    private void deleteFromDrive(String driveId) throws IOException {
        GoogleCredential credential = GoogleCredential
                .fromStream(new FileInputStream("src/main/resources/keys/client_secret_gd.json"))
                .createScoped(List.of("https://www.googleapis.com/auth/drive"));
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName("Top King Training")
                .build();
        service.files().delete(driveId).execute();
    }

    public void updateTiersOnVideo(Long videoId, List<String> updatedTiers) {
        var existingVideo = videoRepo.getReferenceById(videoId);
        var tiersToUpdate = new ArrayList<>(updatedTiers);
        tiersToUpdate.add("admin");
        existingVideo.setProductTiersAppliedTo(tiersToUpdate);
        videoRepo.save(existingVideo);
    }

    public void updateVideoName(Long videoId, String updatedName) {
        var existingVideo = videoRepo.getReferenceById(videoId);
        existingVideo.setDocName(updatedName);
        videoRepo.save(existingVideo);
    }


}
