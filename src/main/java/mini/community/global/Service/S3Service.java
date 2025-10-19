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
    public void deleteByKey(String fileUrl) {
//        if (fileUrl == null || fileUrl.isEmpty()) {
//            log.warn("삭제 요청한 S3키가 null 또는 비어있음");
//            return;
//        }
//        String key = extractS3Key(fileUrl);
//        if (key == null) return;
//        amazonS3.deleteObject(bucket, key);
//        log.info("S3 이미지 삭제 완료: {}", key);
        try {
            amazonS3.deleteObject(bucket, fileUrl);
            log.info("S3 이미지 삭제 완료:{}", fileUrl);
        } catch (Exception e) {
            log.warn("S3 삭제 실패:{}", e.getMessage());
        }
    }

    /**
     * S3 URL에서 key만 추출
     * ex) https://bucket.s3.region.amazonaws.com/dir/uuid_filename.png?... -> dir/uuid_filename.png
     */
    public String extractS3Key(String s3Url) {
        try {
            if (s3Url == null) return null;
            String withoutParams = s3Url.split("\\?")[0];
            int idx = withoutParams.indexOf(".amazonaws.com/");
            if (idx == -1) return null;
            return withoutParams.substring(idx + ".amazonaws.com/".length());
        } catch (Exception e) {
            log.warn("S3 Key 추출 중 오류 발생: {}", e.getMessage());
            return null;
        }
    }

    // s3에서 파일 다운로드 (byte[] 반환)
    public byte[] downloadFile(String s3Key) {
        try (S3Object s3Object = amazonS3.getObject(bucket, s3Key);
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
    public String calculateFileHash(byte[] fileBytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(fileBytes);
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("해시 계산 실패", e);
        }
    }

    // S3 내 동일 해시 파일 존재 확인 (중복 업로드 방지용)
    public boolean existsSameFile(String directory, String newFileHash) {
        try {
            ListObjectsV2Result result = amazonS3.listObjectsV2(bucket, directory + "/");
            for (S3ObjectSummary obj : result.getObjectSummaries()) {
                byte[] bytes = downloadFile(obj.getKey());
                if (bytes != null) {
                    String existingHash = calculateFileHash(bytes);
                    if (existingHash.equals(newFileHash)) {
                        log.info("같은 해시의 파일 발견: {}", obj.getKey());
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("중복파일 검색 중 오류: {}", e.getMessage());
        }
        return false;
    }

}