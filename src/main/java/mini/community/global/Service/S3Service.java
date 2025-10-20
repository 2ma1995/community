package mini.community.global.Service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 서버에서 직접 업로드
    public String uploadFile(MultipartFile file, String key) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        amazonS3.putObject(new PutObjectRequest(bucket, key, file.getInputStream(), metadata)
                .withCannedAcl(CannedAccessControlList.Private));

        // 업로드후, 프리사인 URL 변환 (유효기간 1시간)
        return generatePresignedUrl(key, 3600);
    }

    // 프리사인 URL만 발급(프론트용)
    public String generatePresignedUrl(String fileName, int expireSeconds) {
        Date expiration = new Date(System.currentTimeMillis() + expireSeconds * 1000L);
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, fileName)
                .withMethod(HttpMethod.GET)
                .withExpiration(expiration);
        return amazonS3.generatePresignedUrl(request).toString();
    }

    // S3 삭제 기능
    public void deleteByKeyOrUrl(String keyOrUrl) {
        if (keyOrUrl == null || keyOrUrl.isBlank()) return;

        String key = keyOrUrl.contains("amazonaws.com")
                ? extractS3Key(keyOrUrl)
                : keyOrUrl;

        if (key == null || key.isBlank()) {
            log.warn("삭제 실패: key 파싱 불가 keyOrUrl={}", keyOrUrl);
            return;
        }
        try {
            amazonS3.deleteObject(bucket, key);
            log.info("S3 이미지 삭제 완료:{}", key);
        } catch (Exception e) {
            log.warn("S3 삭제 실패: {} - {}", key, e.getMessage());
        }
    }

    // prefix 아래 모든 객체 삭제
    public void deleteAllUnderPrefix(String prefix) {
        if (prefix == null) return;
        ListObjectsV2Request listReq = new ListObjectsV2Request().withBucketName(bucket).withPrefix(prefix);
        ListObjectsV2Result result;
        do {
            result = amazonS3.listObjectsV2(listReq);
            if (result.getObjectSummaries().isEmpty()) break;

            List<DeleteObjectsRequest.KeyVersion> keys = new ArrayList<>();
            for (S3ObjectSummary s : result.getObjectSummaries()) {
                keys.add(new DeleteObjectsRequest.KeyVersion(s.getKey()));
            }
            DeleteObjectsRequest delReq = new DeleteObjectsRequest(bucket).withKeys(keys).withQuiet(true);
            amazonS3.deleteObjects(delReq);
        } while (result.isTruncated());
        log.info("S3 prefix 정리 완료:{}", prefix);
    }

    /**
     * S3 URL에서 key만 추출
     * ex) https://bucket.s3.region.amazonaws.com/dir/uuid_filename.png?... -> dir/uuid_filename.png
     */
    public String extractS3Key(String s3Url) {
        try {
            String withoutParams = s3Url.split("\\?")[0];
            int idx = withoutParams.indexOf(".amazonaws.com/");
            if (idx == -1) return null;
            return withoutParams.substring(idx + ".amazonaws.com/".length());
        } catch (Exception e) {
            return null;
        }
    }

    // s3에서 파일 다운로드 (byte[] 반환) / 파일 비교 (중복 업로드 방지)
    public byte[] downloadFile(String key) {
        try (S3Object s3Object = amazonS3.getObject(bucket, key);
             S3ObjectInputStream inputStream = s3Object.getObjectContent()) {
            return inputStream.readAllBytes();
        } catch (Exception e) {
            log.warn("S3 파일 다운로드 실패: {}", e.getMessage());
            return null;
        }
    }

    // MultipartFile 해시 계산
    public String calculateFileHash(MultipartFile file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(file.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("파일 해시 계산 실패", e);
        }
    }

    // byte[] 해시 계산 (기존 s3 파일 비교)
    public String calculateFileHash(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(bytes);
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("해시 계산 실패", e);
        }
    }

    // S3 내 동일 해시 파일 존재 확인 (중복 업로드 방지용)
    public boolean existsSameFile(String prefix, String newFileHash) {
        ListObjectsV2Request listReq = new ListObjectsV2Request().withBucketName(bucket).withPrefix(prefix);
        ListObjectsV2Result result;
        do{
            result = amazonS3.listObjectsV2(listReq);
            for (S3ObjectSummary s : result.getObjectSummaries()) {
                byte[] bytes = downloadFile(s.getKey());
                if (bytes != null) continue;
                    String hash = calculateFileHash(bytes);
                    if (newFileHash.equals(hash)) {
                        log.info("같은 해시의 파일 발견: {}", s.getKey());
                        return true;
                    }
                }
        } while (result.isTruncated());
        return false;
    }

    // 유저 이미지 정리
    public void cleanupUserImages(Long userId, String currentImage) {
        // 디렉토리 정리
        String currentPrefix = "profile/" + userId + "/";
        deleteAllUnderPrefix(currentPrefix);

        // 레거시(언더바) 규칙 정리: profile/{userId}_ 로 시작하는 것
        deleteAllUnderPrefix("profile/" + userId + "_");

        // DB가 가리키는 현재 URL이 있다면 확실히 제거 (키 직삭제)
        if (currentImage != null && currentImage.contains("amazonaws.com")) {
            String s3Key = extractS3Key(currentImage);
            if (s3Key != null) {
                deleteByKeyOrUrl(s3Key);
            }
        }
    }

}