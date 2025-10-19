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

            String currentImage = profile.getImage();
            log.info("[ImageService] userId={} -> 기존 이미지 확인: {}", userId, currentImage);

            // 새 이미지 해시 계산
            String newFileHash = s3Service.calculateFileHash(file);
            log.info("새 이미지 해시: {}", newFileHash);

            // 기존 이미지 존재 시 해시 비교
            if (s3Service.existsSameFile("profile/" + userId, newFileHash)) {
                log.info("동일 내용의 파일이 이미 S3에 존재 → 업로드 생략");
                return currentImage;
            }

            // 기존 이미지가 존재하면 삭제
            if (currentImage != null && currentImage.contains("amazonaws.com")) {
                String oldKey = s3Service.extractS3Key(currentImage);
                if (oldKey != null){
                    if (oldKey.contains("profile/")&&!oldKey.contains("/" + userId+"/")){
                        log.warn("기존 구조의 파일 삭제 시도:{}",oldKey);
                        s3Service.deleteByKey(oldKey);
                    }else {
                        s3Service.deleteByKey(oldKey);
                    }
                    log.info("기존 이미지 삭제 완료: {}", oldKey);
                }
            }
//            if (currentImage != null && currentImage.contains(".amazonaws.com")) {
//                String currentS3Key = s3Service.extractS3Key(currentImage);
//                if (currentS3Key != null) {
//                    byte[] existingImageBytes = s3Service.downloadFile(currentS3Key);
//                    if (existingImageBytes != null) {
//                        String existingFileHash = s3Service.calculateFileHash(existingImageBytes);
//                        log.info("기존 이미지 해시: {}", existingFileHash);
//
//                        // 같은 해시이면 업로드 생략
//                        if (newFileHash.equals(existingFileHash)) {
//                            log.info("같은 내용의 이미지가 이미 존재하므로 업로드 및 삭제를 생략합니다.");
//                            return currentImage;
//                        }
//                        // 기존 이미지 삭제
//                        s3Service.delete(currentImage);
//                        log.info("기존 이미지 삭제 완료");
//                    }
//                }
//            }


            // 기존 이미지 있으면 삭제
//            if (currentImage != null && currentImage.contains("amazonaws.com")) {
//                String s3Key = s3Service.extractS3Key(currentImage);
//                if (s3Key != null) {
//                    s3Service.delete(s3Key);
//                    log.info("기존 이미지 삭제 완료: {}", s3Key);
//                } else {
//                    log.warn("기존 이미지의 S3 key 추출 실패");
//                }
//            }

            // 새파일 이름 생성(profile/{userId}/{원본파일명})
            String filename = file.getOriginalFilename();
            String s3Key = "profile/" + userId + "/" + filename;
            // s3 새 이미지 업로드
            String imageUrl = s3Service.uploadFile(file, s3Key);
            log.info("S3 업로드 완료: {}", imageUrl);

            //프로필에 이미지 URL저장 후 DB업데이트
            profile.setImage(imageUrl);
            profileRepository.save(profile);
            log.info("S3 업로드 완료:{}", imageUrl);

            // 프리사인 URL 반환 (1시간 유효)
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
