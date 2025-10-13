package mini.community.Profile.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    public void saveImage(MultipartFile file);
}
