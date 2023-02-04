package application.topkingapi.entity.video;

import application.topkingapi.entity.user.UserService;
import application.topkingapi.mail.EmailSenderService;
import application.topkingapi.model.User;
import application.topkingapi.model.Video;
import application.topkingapi.twilio.SmsRequest;
import application.topkingapi.twilio.TwilioService;
import io.micrometer.common.util.StringUtils;
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
    private final TwilioService twilioService;

    private static final String ADMIN = "admin";

    public VideoOrchestrator(
            VideoService videoService,
            UserService userService,
            EmailSenderService emailSenderService,
            TwilioService twilioService
    ) {
        this.videoService = videoService;
        this.userService = userService;
        this.emailSenderService = emailSenderService;
        this.twilioService = twilioService;
    }

    public void uploadVideoAndNotify(MultipartFile file, String tiers, String method) throws IOException {
        List<String> tiersToSend = constructTiersToSent(tiers);
        uploadVideo(file, tiersToSend);
        if (!method.equals("N")) {
            notifyClient(tiersToSend, method, file.getOriginalFilename());
        }
    }

    public void updateTiersOnVideo(String videoId, String tiers) {
        List<String> tiersToSend = constructTiersToSent(tiers);
        videoService.updateTiersOnVideo(Long.valueOf(videoId), tiersToSend);
    }

    public void updateVideoName(String videoId, String name) {
        videoService.updateVideoName(Long.valueOf(videoId), name);
    }

    public void deleteVideo(Long videoId) {
        videoService.deleteVideo(videoId);
    }

    public List<Video> getVideosForTier(String tiers) {
        return videoService.getVideosForTier(tiers);
    }

    private List<String> constructTiersToSent(String tiers) {
        List<String> tiersToSend = Arrays.stream(tiers.split(""))
                .collect(Collectors.toCollection(ArrayList::new));
        tiersToSend.add(ADMIN);
        return tiersToSend;
    }

    private void uploadVideo(MultipartFile file, List<String> tiersToSend) throws IOException {
        videoService.uploadVideo(file, tiersToSend);
    }

    private void notifyClient(List<String> tiersToSend, String method, String videoName) {
        var nonAdminUsersUnderTier = userService.getAllUsers().stream()
                .filter(user -> !user.getProductTier().equals(ADMIN) && tiersToSend.contains(user.getProductTier()))
                .toList();
        if (method.equals("B") || method.equals("T")) {
            List<String> phoneNumbers = nonAdminUsersUnderTier.stream()
                    .map(User::getPhoneNumber)
                    .filter(StringUtils::isNotEmpty)
                    .toList();
            for (var number : phoneNumbers) {
                var request = new SmsRequest(number, "New video! " + videoName + " is live now.");
                twilioService.sendSms(request);
            }
        }

        if (method.equals("B") || method.equals("E")) {
            List<String> emails = nonAdminUsersUnderTier.stream()
                    .map(User::getEmail)
                    .toList();
            for (var email : emails) {
                emailSenderService.sendSimpleEmail(
                        email,
                        "New Video Alert!",
                        videoName + " is live now. Go check it out."
                );
            }
        }
    }
}
