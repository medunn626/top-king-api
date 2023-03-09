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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;

@Service
public class VideoService {
    private final VideoRepo videoRepo;
    private static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    @Value("${private_key_id}")
    private String privateKeyId;

    @Value("${client_email}")
    private String clientEmail;

    @Value("${client_id}")
    private String clientId;

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
        videoToSave.setOrderNumber(videoRepo.count() + 1);

        return videoRepo.save(videoToSave);
    }

    private String saveToDrive(MultipartFile multipartFile) throws IOException {
        // Create credentials and app
        GoogleCredential credential = GoogleCredential
                .fromStream(getJsonSecretData())
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
                .fromStream(getJsonSecretData())
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
                .fromStream(getJsonSecretData())
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

    public void updateVideos(List<Video> videos) {
        videoRepo.saveAll(videos);
    }
    private InputStream getJsonSecretData() {
        JSONObject obj = new JSONObject();
        obj.put("type", "service_account");
        obj.put("private_key_id", privateKeyId);
        obj.put("private_key", getBase64());
        obj.put("client_email", clientEmail);
        obj.put("client_id", clientId);
        String str = obj.toString();
        return new ByteArrayInputStream(str.getBytes());
    }

    private String getBase64() {
        return "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC919napOuQUWas\n87WeusXCF0mQqWR+IjkL6ZNJ//u/oOhE97GLDIXRA+Z3LSr8hdPY9W+NCQGhHvaG\nbGcClZ49NrYe4nFshXyqqvIqd85aWhViomlin59joSrNX0tsyHdsL+zrDQMx3/uH\nyYeT1vZwxCQ6voNXB1tw6Y9Y98+rqsRlrc3t2SWgr9MW/oUIacpOyc8RBWhPJjil\n5ZzX3KVARi6OpHYBLcauzUh8E1smlSa1Gal9VGa4R+Pn0S9Kw7sFIx7n+H2rUR0n\nbnac4x9cqznWHAL589exsUjpH5LU9TITMMPQ9+Xrg3cMgem5YMiQ499GWWa/DnGa\nS3s/PQd3AgMBAAECggEASQPZ4EQJ7cnBCcf6FICVSt8WBjOAWRrkgfZmFdIEi2Fh\ntBToDijU1tz/KNsQa2s1XX6YVtAd9lAoVO8gd1gAMl72kM5a/XghyKpS1Y6aqRl4\nnCUHD02BShpUkgSSx9YaSFvubUDl96rD3SffozbMNfbaUFWxwiksPDM4VOmKz+UF\n1L+abAGPZcx2E5hT1/KnriVsKLGB7XQbiOLdlQvp2gaNJm0NQLAVFCUKbYXbZO20\noXy5+mHTIRfziNPHBVoelP25ls3WFzFiq/xCIibtyTfwnSM5ATiWhgw399HdAPFc\nf7VQQHL8uVd3FvtANyKKvSOHfGIjykovCIQwHt1WUQKBgQDvqGVEAO0fT0sVS/A7\nqcYoy9bVE5dz6TvP1OVSQaPOsDhetVRNmB4laseU7JA2ReYm/Wbd4k7ADVaqdHJt\nUmBIZryCsh7lOAULRoKGjAU7kJamDdIUR81BxHzpX4b/hoTKWgKwxDFtX2O7hcDo\nGI4F0dIOhy1FXbPuxS+3sdRbAwKBgQDKydzUUHQ6FL3QQb0FgJ383pvaQq264iCD\nIaiTocPEcci3e+8WS09P+OesbNXzBcNlsfTbf8O4opUzrIlvobkluWSnf7E0dhCi\n7/vxYc1BIdfS7GLQXVmDEewZyJZP9jzrGDRPkEl0gHZsI95dwyoLnGndQp6CTsm/\n92I+AandfQKBgF1ib7s05TD7E9XDlmOZPwbsjtTOYoifDFhqq8UGoM7MKdr1q1jk\n+nI4DncASx1q1UjCGxBAu9DoIaof3+qrW6s6pSAESjelQYnoOro022EfcSRZZE/U\nvq1u5AGH4LG2+A1lT4ETofLtZY5PiyClWMn5vXE9yS0rWw6iNXrNx2KDAoGBAMiR\n56zG9m4L3cGBg6dRkvFsa9HwaUySI34PaGC8ephtwgxYtBzk18lNcjcEXohDhwOq\ng3gmYjrX86JsYHLdDfbV60wP7ADrVYESw6n3BcAJ7SFdVE6qRcJxk4fc8W6dKZuN\nERAwsbZc6MQEpgcu5QMe7UY+gfB4ZOtNjwmtM4kxAoGAOcapcdxxrCSMeiTPsQop\nA3Zz/a4zUxBqE8y6u0OEe2nZCnRLdVLivvxYecjIayEWiuMIv6sygJ9S5ZecmriH\njO3vbY5EZgKlMlOlTEwCyuIQTb5Fwxb3a8OQ/hknWlmsZk6JOefY7nMKPLAZpSbB\nmlWULqe7s090glpIcGehpSQ=\n-----END PRIVATE KEY-----\n";
    }

}
