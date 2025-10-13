package mini.community.Profile.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageServiceImpl implements ImageService {
    @Override
    public void saveImage(MultipartFile file) {
        System.out.println("이미지 저장됨: "+ file.getOriginalFilename());
    }
}
