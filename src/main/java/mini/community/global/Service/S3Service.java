package mini.community.global.Service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 서버에서 직접 업로드
    public String uploadFile(MultipartFile file, String dirName) throws IOException {
        String fileName = dirName + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), null)
                .withCannedAcl(CannedAccessControlList.Private));
    // 업로드후, 프리사인 URL 변환 (유효기간 1시간)
        return generatePresignedUrl(fileName, 3600);
    }

    // 프리사인 URL만 발급(프론트용)
    public String generatePresignedUrl(String fileName, int expireSeconds) {
        Date expiration = new Date(System.currentTimeMillis() + expireSeconds * 1000L);
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, fileName)
                .withMethod(HttpMethod.GET)
                .withExpiration(expiration);

        URL url = amazonS3.generatePresignedUrl(request);
        return url.toString();
    }

    // 삭제 기능
    public void delete(String fileUrl) {
        String key = fileUrl.substring(fileUrl.indexOf(".com/") + 5);
        amazonS3.deleteObject(bucket, key);
    }


}