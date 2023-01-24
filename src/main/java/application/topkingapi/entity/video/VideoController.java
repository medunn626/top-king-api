package application.topkingapi.entity.video;

import application.topkingapi.model.Video;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("videos")
public class VideoController {
    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    /**
     * ADMIN ONLY METHOD
     * @param file
     * @param tiers
     * @throws IOException
     */
    @PostMapping("/upload/tiers/{tiers}")
    public void uploadVideo(@RequestBody MultipartFile file,
                            @PathVariable String tiers) throws IOException {
        List<String> tiersToSend = Arrays.stream(tiers.split(""))
                .collect(Collectors.toCollection(ArrayList::new));
        tiersToSend.add("admin");
        videoService.uploadVideo(file, tiersToSend);
    }

    /**
     * ADMIN ONLY METHOD
     * @param videoId
     * @param tiers
     */
    @PutMapping("update/id/{videoId}/tiers/{tiers}")
    public void updateTiersOnVideo(@PathVariable String videoId,
                                   @PathVariable String tiers) {
        List<String> tiersToSend = Arrays.stream(tiers.split(""))
                .collect(Collectors.toCollection(ArrayList::new));
        tiersToSend.add("admin");
        videoService.updateTiersOnVideo(Long.valueOf(videoId), tiersToSend);
    }

    /**
     * ADMIN ONLY METHOD
     * @param videoId
     */
    @DeleteMapping("delete/id/{videoId}")
    public void deleteVideo(@PathVariable Long videoId) {
        videoService.deleteVideo(videoId);
    }

    @GetMapping("retrieve/{tiers}")
    public ResponseEntity<List<Video>> getVideosForTier(@PathVariable String tiers) {
        List<Video> videos = videoService.getVideosForTier(tiers);
        return new ResponseEntity<>(videos, HttpStatus.OK);
    }
}
