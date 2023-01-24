package ghkwhd.upload.file;

import ghkwhd.upload.domain.UploadFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Component
public class FileStore {

    @Value("${file.dir}")
    private String fileDir;

    public String getFullPath(String filename) {
        return fileDir + filename;
    }


    // 여러 개 업로드 하는 메서드
    public List<UploadFile> storeFiles(List<MultipartFile> multipartFiles) throws IOException {
        List<UploadFile> storeFileResult = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                // 업로드하는 메서드를 반복
                storeFileResult.add(storeFile(multipartFile));
            }
        }
        return storeFileResult;
    }


    // 하나만 업로드 하는 메서드
    public UploadFile storeFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);

        // 파일 저장
        multipartFile.transferTo(new File(getFullPath(storeFileName)));

        return new UploadFile(originalFilename, storeFileName);
    }


    // 저장할 파일명 생성
    // 서버에 저장할 때 uuid를 사용하되 사용자가 업로드한 파일의 확장자는 가져와서 저장하도록
    private static String createStoreFileName(String originalFilename) {

        String uuid = UUID.randomUUID().toString();

        // 확장자 가져오기
        String ext = extractExt(originalFilename);

        // 서버에 저장할 파일명
        return uuid + "." + ext;
    }


    // 확장자 추출하는 메서드
    private static String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

}
