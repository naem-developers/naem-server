package naem.server.service;

import static naem.server.exception.ErrorCode.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import naem.server.domain.post.Image;
import naem.server.domain.post.Post;
import naem.server.exception.CustomException;
import naem.server.repository.ImageRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3ServiceImpl implements S3Service {

    private final AmazonS3Client amazonS3Client;
    private final ImageRepository imageRepository;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;

    // 파일 업로드
    public List<String> uploadImage(List<MultipartFile> multipartFile, String dirName, Post post) {

        List<String> fileNameList = new ArrayList<>();
        List<String> imageUrl = new ArrayList<>();

        multipartFile.forEach(file -> {

            String fileName = createFileName(file.getOriginalFilename(), dirName);

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            // s3에 업로드
            try (InputStream inputStream = file.getInputStream()) {
                amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

            } catch (IOException e) {
                throw new CustomException(FILE_CAN_NOT_UPLOAD);
            }

            imageUrl.add(amazonS3Client.getUrl(bucket, fileName).toString());
            fileNameList.add(fileName);
        });

        try {
            saveUrl(imageUrl, post); // db에 uri 저장
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileNameList;
    }

    public void saveUrl(List<String> imageUrls, Post post) throws IOException {

        for (String imageUrl : imageUrls) {

            Image img = new Image();
            img.setImgurl(imageUrl);
            img.setPost(post);

            imageRepository.save(img);
        }
    }

    // 유니크한 파일의 이름을 생성하는 로직
    private String createFileName(String originalName, String dirName) {
        return dirName + "/" + UUID.randomUUID() + originalName;
    }
}
