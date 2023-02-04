package application.topkingapi.entity.video;

import application.topkingapi.model.Video;
import application.topkingapi.repo.VideoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class VideoService {
    private final VideoRepo videoRepo;

    @Autowired
    public VideoService(VideoRepo videoRepo) {
        this.videoRepo = videoRepo;
    }

    /**
     * ADMIN ONLY METHOD
     * @param file
     * @param tiers
     * @return
     * @throws IOException
     */
    public Video uploadVideo(MultipartFile file, String name, List<String> tiers) throws IOException {
        var docType = file.getContentType();
        var data = file.getBytes();
        var tiersToAdd = new ArrayList<>(tiers);
        tiersToAdd.add("admin");
        Video videoToSave = new Video(name, docType, data, tiers);

        return videoRepo.save(videoToSave);
    }

    /**
     * ADMIN ONLY METHOD
     * @param videoId
     * @param updatedTiers
     */
    public void updateTiersOnVideo(Long videoId, List<String> updatedTiers) {
        var existingVideo = videoRepo.getReferenceById(videoId);
        var tiersToUpdate = new ArrayList<>(updatedTiers);
        tiersToUpdate.add("admin");
        existingVideo.setProductTiersAppliedTo(tiersToUpdate);
        videoRepo.save(existingVideo);
    }

    /**
     * ADMIN ONLY METHOD
     * @param videoId
     * @param updatedName
     */
    public void updateVideoName(Long videoId, String updatedName) {
        var existingVideo = videoRepo.getReferenceById(videoId);
        existingVideo.setDocName(updatedName);
        videoRepo.save(existingVideo);
    }

    /**
     * ADMIN ONLY METHOD
     * @param videoId
     */
    public void deleteVideo(Long videoId) {
        videoRepo.deleteById(videoId);
    }

    public List<Video> getVideosForTier(String userTier) {
        return videoRepo.findAll().stream()
                .filter(video -> video.getProductTiersAppliedTo().contains(userTier))
                .toList();
    }

    public Video downloadVideo(Long videoId) throws Exception {
        return videoRepo.findById(videoId)
                .orElseThrow(() -> new Exception("Cannot find existing video to download"));
    }
}
