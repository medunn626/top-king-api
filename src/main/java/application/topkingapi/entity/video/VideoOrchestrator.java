package application.topkingapi.entity.video;

import application.topkingapi.entity.user.UserService;
import application.topkingapi.mail.EmailSenderService;
import application.topkingapi.model.User;
import application.topkingapi.model.Video;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class VideoOrchestrator {
    private final VideoService videoService;
    private final UserService userService;
    private final EmailSenderService emailSenderService;

    private static final String ADMIN = "admin";

    public VideoOrchestrator(
            VideoService videoService,
            UserService userService,
            EmailSenderService emailSenderService
    ) {
        this.videoService = videoService;
        this.userService = userService;
        this.emailSenderService = emailSenderService;
    }

    public void uploadVideoAndNotify(MultipartFile file, String name, String tiers, String method) throws Exception {
        List<String> tiersToSend = constructTiersToSent(tiers);
        uploadVideo(file, name, tiersToSend);
        if (!method.equals("N")) {
            notifyClient(tiersToSend, method, name);
        }
    }

    public void updateTiersOnVideo(String videoId, String tiers) {
        List<String> tiersToSend = constructTiersToSent(tiers);
        videoService.updateTiersOnVideo(Long.valueOf(videoId), tiersToSend);
    }

    public void updateVideoName(String videoId, String name) {
        videoService.updateVideoName(Long.valueOf(videoId), name);
    }

    public void updateVideos(List<Video> videos) {
        videoService.updateVideos(videos);
    }

    public void deleteVideo(Long videoId) throws IOException {
        videoService.deleteVideo(videoId);
    }

    public List<Video> getVideosForTier(String tiers) throws IOException {
        return videoService.getVideosForTier(tiers);
    }

    public List<Video> getVideoSummaryForAdmin() {
        return videoService.getVideoSummaryForAdmin();
    }

    private List<String> constructTiersToSent(String tiers) {
        List<String> tiersToSend = Arrays.stream(tiers.split(""))
                .collect(Collectors.toCollection(ArrayList::new));
        tiersToSend.add(ADMIN);
        return tiersToSend;
    }

    private void uploadVideo(MultipartFile file, String name, List<String> tiersToSend) throws Exception {
        videoService.uploadVideo(file, name, tiersToSend);
    }

    private void notifyClient(List<String> tiersToSend, String method, String videoName) throws MessagingException {
        var nonAdminUsersUnderTier = userService.getAllUsers().stream()
                .filter(user -> null != user.getProductTier()
                    && !user.getProductTier().equals(ADMIN)
                    && tiersToSend.contains(user.getProductTier()))
                .toList();
        if (method.equals("E")) {
            List<String> emails = nonAdminUsersUnderTier.stream()
                    .map(User::getEmail)
                    .toList();
            for (var email : emails) {
                emailSenderService.sendSimpleEmail(
                        email,
                        "New Video Alert!",
                        videoName + " is live now on Top King! <br/><br/>" +
                                "Click <a href='https://topkingtraining.com/content'>here</a>" +
                                " and go check it out."
                );
            }
        }
    }
}
