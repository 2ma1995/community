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

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

    private final ProfileRepository profileRepository;
    private final S3Service s3Service;

    public String saveImage(Long userId, MultipartFile file) {
        try {
            // 해당 유저의 프로필 조회
            Profile profile = profileRepository.findByUser_Id(userId)
                    .orElseThrow(() -> new BadRequestException("프로필을 찾을수 없습니다."));

            final String prefix = "profile/" + userId + "/";
            final String newFileName = file.getOriginalFilename() == null ? "image" : file.getOriginalFilename();
            final String newKey = prefix + newFileName;
//            log.info("[ImageService] userId={} -> 기존 이미지 확인: {}", userId, currentImage);

            // 새 이미지 해시 계산
            String newFileHash = s3Service.calculateFileHash(file);
            log.info("새 이미지 해시: {}", newFileHash);

            // 기존 이미지 존재 시 해시 비교
            if (s3Service.existsSameFile(prefix, newFileHash)) {
                log.info("동일 내용의 파일이 이미 S3에 존재 → 업로드 생략");
                return profile.getImage();
            }

            // 업로드 전 해당 유저의 경로 정리
            // - profile/{userId}/아래 모두 삭제
            // 과거에 업로드된 userId 기반 레걱시 키들도 정리
            s3Service.cleanupUserImages(userId, profile.getImage());

            // 새파일 이름 생성(profile/{userId}/{원본파일명})
            String imageUrl = s3Service.uploadFile(file, newKey);
            log.info("S3 업로드 완료: {}", imageUrl);

            //프로필에 이미지 URL저장 후 DB업데이트
            profile.setImage(imageUrl);
            profileRepository.save(profile);
            log.info("S3 업로드 완료:{}", imageUrl);

            // 프리사인 URL 반환
            return imageUrl;

        } catch (AmazonServiceException e) {
            log.error("S3 업로드 실패 : {}", e.getErrorMessage());
            throw new BadRequestException("S3 업로드 실패" + e.getErrorMessage());
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("이미지 업로드 중 예외 발생: {}", e.getMessage(), e);
            throw new BadRequestException("Image upload failed" + e.getMessage());
        }
    }
}
