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
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import naem.server.domain.member.DisabledAuthImage;
import naem.server.domain.member.DisabledMemberInfo;
import naem.server.domain.post.Post;
import naem.server.domain.post.PostImage;
import naem.server.exception.CustomException;
import naem.server.repository.DisabledAuthImageRepository;
import naem.server.repository.PostImageRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3ServiceImpl implements S3Service {

    private final AmazonS3Client amazonS3Client;
    private final PostImageRepository imageRepository; // 게시글 사진 레포
    private final DisabledAuthImageRepository disabledAuthImageRepository; // 장애인 인증 사진 레포

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;

    /**
     * 장애인 인증 이미지 업로드
     */
    @Override
    public List<String> uploadDisabledAuthImage(List<MultipartFile> multipartFile, String dirName,
        DisabledMemberInfo disabledMemberInfo) {

        List<String> fileNameList = new ArrayList<>();
        List<String> imageUrl = new ArrayList<>();

        uploadS3(multipartFile, dirName, fileNameList, imageUrl);

        try {
            storeDisabledAuthInfoInDb(imageUrl, fileNameList, disabledMemberInfo); // db에 url 과 fileName 정보 저장
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileNameList;
    }

    /**
     * 게시글 이미지 업로드
     */
    @Override
    public List<String> uploadImage(List<MultipartFile> multipartFile, String dirName, Post post) {

        List<String> fileNameList = new ArrayList<>();
        List<String> imageUrl = new ArrayList<>();

        uploadS3(multipartFile, dirName, fileNameList, imageUrl);

        try {
            storeInfoInDb(imageUrl, fileNameList, post); // db에 url 과 fileName 정보 저장
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileNameList;
    }

    // S3 업로드 로직
    private void uploadS3(List<MultipartFile> multipartFile, String dirName, List<String> fileNameList,
        List<String> imageUrl) {

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

            fileNameList.add(fileName);
            imageUrl.add(amazonS3Client.getUrl(bucket, fileName).toString());
        });
    }

    /**
     * 장애인 인증 이미지 db 저장
     */
    public void storeDisabledAuthInfoInDb(List<String> imageUrls, List<String> fileNameList,
        DisabledMemberInfo disabledMemberInfo) throws IOException {
        for (int i = 0; i < imageUrls.size(); i++) {
            DisabledAuthImage img = new DisabledAuthImage();
            img.setImgUrl(imageUrls.get(i));
            img.setFileName(fileNameList.get(i));
            img.setDisabledMemberInfo(disabledMemberInfo);

            disabledAuthImageRepository.save(img);
        }
    }

    /**
     * 게시글 이미지 db 저장
     */
    public void storeInfoInDb(List<String> imageUrls, List<String> fileNameList, Post post) throws IOException {
        for (int i = 0; i < imageUrls.size(); i++) {
            PostImage img = new PostImage();
            img.setImgUrl(imageUrls.get(i));
            img.setFileName(fileNameList.get(i));
            img.setPost(post);

            imageRepository.save(img);
        }
    }

    // 유니크한 파일의 이름을 생성하는 로직
    private String createFileName(String originalName, String dirName) {
        return dirName + "/" + UUID.randomUUID() + getFileExtension(originalName);
    }

    // 파일의 확장자 명을 가져오는 로직 (file 형식 확인)
    private String getFileExtension(String fileName) {
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        if (fileExtension.equals(".jpeg") || fileExtension.equals(".jpg") || fileExtension.equals(".png")) {
            return fileExtension;
        } else {
            throw new CustomException(INVALID_FILE_ERROR);
        }
    }

    // 이미지 리스트 삭제
    @Override
    public void deletePostImages(List<PostImage> images) {
        for (PostImage postImage : images) {
            amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, postImage.getFileName()));
        }
    }

    // 장애인 인증 이미지 리스트 삭제
    @Override
    public void deleteDisabledAuthImages(List<DisabledAuthImage> images) {
        for (DisabledAuthImage disabledAuthImage : images) {
            amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, disabledAuthImage.getFileName()));
        }
    }
}
