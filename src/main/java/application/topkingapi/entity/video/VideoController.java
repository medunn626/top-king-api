package application.topkingapi.entity.video;

import application.topkingapi.model.Video;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("videos")
public class VideoController {
    private final VideoOrchestrator videoOrchestrator;

    public VideoController(VideoOrchestrator videoOrchestrator) {
        this.videoOrchestrator = videoOrchestrator;
    }

    @PostMapping("/upload/tiers/{tiers}/name/{name}/notify/{method}")
    public void uploadVideo(@RequestBody MultipartFile file,
                            @PathVariable String tiers,
                            @PathVariable String name,
                            @PathVariable String method) throws IOException, MessagingException {
        videoOrchestrator.uploadVideoAndNotify(file, name, tiers, method);
    }

    @PutMapping("update/id/{videoId}/tiers/{tiers}")
    public void updateTiersOnVideo(@PathVariable String videoId,
                                   @PathVariable String tiers) {
        videoOrchestrator.updateTiersOnVideo(videoId, tiers);
    }

    @PutMapping("update/id/{videoId}/name/{name}")
    public void updateVideoName(@PathVariable String videoId,
                                @PathVariable String name) {
        videoOrchestrator.updateVideoName(videoId, name);
    }

    @DeleteMapping("delete/id/{videoId}")
    public void deleteVideo(@PathVariable Long videoId) throws IOException {
        videoOrchestrator.deleteVideo(videoId);
    }

    @GetMapping("retrieve/{tiers}")
    public ResponseEntity<List<Video>> getVideosForTier(@PathVariable String tiers) throws IOException {
        var video = videoOrchestrator.getVideosForTier(tiers);
        return new ResponseEntity<>(video, HttpStatus.OK);
    }

    @GetMapping("retrieve-all")
    public ResponseEntity<List<Video>> getVideoSummaryForAdmin() {
        var videos = videoOrchestrator.getVideoSummaryForAdmin();
        return new ResponseEntity<>(videos, HttpStatus.OK);
    }
}
