package mini.community.Profile.service;

import com.amazonaws.AmazonServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mini.community.Profile.entity.Profile;
import mini.community.Profile.repository.ProfileRepository;
import mini.community.global.Service.S3Service;
import mini.community.global.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {
    private final ProfileRepository profileRepository;
    private final S3Service s3Service;

    public String saveImage(Long userId, MultipartFile file) {
        log.info("🔍 [ImageService] userId={} -> searching profile...", userId);
        Optional<Profile> profileOpt = profileRepository.findByUser_Id(userId);
        if (profileOpt.isEmpty()) {
            log.warn("❌ Profile not found for userId={}", userId);
        } else {
            log.info("✅ Profile found: id={}, image={}", profileOpt.get().getId(), profileOpt.get().getImage());
        }
        try {
// 해당 유저의 프로필 조회
            Profile profile = profileRepository.findByUser_Id(userId).orElseThrow(() -> new BadRequestException("프로필을 찾을수 없습니다."));

//s3 업로드
            String imageUrl = s3Service.uploadFile(file, "profile");

//프로필에 이미지 URL저장
            profile.setImage(imageUrl);
            profileRepository.save(profile);
// 프리사인 URL 반환 (1시간 유효)
            return imageUrl;
        }catch (AmazonServiceException e){
            throw new BadRequestException("S3 업로드 실패" + e.getErrorMessage());
        }
        catch (Exception e) {
            throw new BadRequestException("Image upload failed" + e.getMessage());
        }
    }
}
