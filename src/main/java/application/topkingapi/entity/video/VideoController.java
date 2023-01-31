package application.topkingapi.entity.video;

import application.topkingapi.model.Video;
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

    /**
     * ADMIN ONLY METHOD
     * @param file
     * @param tiers
     * @throws IOException
     */
    @PostMapping("/upload/tiers/{tiers}/notify/{method}")
    public void uploadVideo(@RequestBody MultipartFile file,
                            @PathVariable String tiers,
                            @PathVariable String method) throws IOException {
        videoOrchestrator.uploadVideoAndNotify(file, tiers, method);
    }

    /**
     * ADMIN ONLY METHOD
     * @param videoId
     * @param tiers
     */
    @PutMapping("update/id/{videoId}/tiers/{tiers}")
    public void updateTiersOnVideo(@PathVariable String videoId,
                                   @PathVariable String tiers) {
        videoOrchestrator.updateTiersOnVideo(videoId, tiers);
    }

    /**
     * ADMIN ONLY METHOD
     * @param videoId
     */
    @DeleteMapping("delete/id/{videoId}")
    public void deleteVideo(@PathVariable Long videoId) {
        videoOrchestrator.deleteVideo(videoId);
    }

    @GetMapping("retrieve/{tiers}")
    public ResponseEntity<List<Video>> getVideosForTier(@PathVariable String tiers) {
        List<Video> videos = videoOrchestrator.getVideosForTier(tiers);
        return new ResponseEntity<>(videos, HttpStatus.OK);
    }
}
