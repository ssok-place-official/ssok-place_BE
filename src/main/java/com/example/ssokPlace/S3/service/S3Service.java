package com.example.ssokPlace.S3.service;

import com.example.ssokPlace.S3.dto.UploadResult;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Client s3;
    private final S3Presigner presigner;

    @Value("${cloud.aws.s3.bucket}" )
    private String bucketName;

    @Value("${cloud.aws.region.static}")
    private String region;

    private static final Tika tika = new Tika();

    public UploadResult uploadPublic(MultipartFile file, String dir) throws IOException {
        String key = buildKey(dir, file.getOriginalFilename());
        String contentType = detectContentType(file);

        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .acl(ObjectCannedACL.PUBLIC_READ) // 퍼블릭 버킷 허용 필요
                .contentType(contentType)
                .build();

        s3.putObject(req, RequestBody.fromBytes(file.getBytes()));
        return new UploadResult(key, publicUrl(key), contentType, file.getSize());
    }

    public void deleteObject(String key) {
        s3.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build());
    }

    public URL generateGetPresignedUrl(String key, Duration ttl) {
        GetObjectRequest get = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        GetObjectPresignRequest pre = GetObjectPresignRequest.builder()
                .signatureDuration(ttl)
                .getObjectRequest(get)
                .build();
        return presigner.presignGetObject(pre).url();
    }

    public URL generatePutPresignedUrl(String key, String contentType, Duration ttl) {
        PutObjectRequest put = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .build();
        PutObjectPresignRequest pre = PutObjectPresignRequest.builder()
                .signatureDuration(ttl)
                .putObjectRequest(put)
                .build();
        return presigner.presignPutObject(pre).url();
    }

    public UploadResult uploadPrivate(MultipartFile file, String dir) throws IOException {
        String key = buildKey(dir, file.getOriginalFilename());
        String contentType = detectContentType(file);

        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .build();

        s3.putObject(req, RequestBody.fromBytes(file.getBytes()));
        return new UploadResult(key, null, contentType, file.getSize());
    }

    private String detectContentType(MultipartFile file) throws IOException {
        try {
            String type = tika.detect(file.getInputStream(), file.getOriginalFilename());
            return (type != null) ? type : file.getContentType();
        } catch (Exception e) {
            return file.getContentType();
        }
    }

    public String publicUrl(String key){
        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + key;
    }

    private String buildKey(String dir, String originalFilename) {
        String baseDir = (dir == null || dir.isBlank()) ? "uploads" : dir.replaceAll("^/|/$", "");
        String safeName = (originalFilename == null || originalFilename.isBlank())
                ? "file"
                : originalFilename;
        String date = java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        return String.format("%s/%s/%s_%s", baseDir, date, UUID.randomUUID(), safeName);
    }
}
