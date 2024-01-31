package year.exp.forge.services;


import com.google.cloud.storage.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.SignUrlOption;

import java.net.URL;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class GcpStorageService {

    @Value("${gcp.bucket.name}")
    private String bucketName;

    @Autowired
    Storage storage;

    public String upload(MultipartFile file, String folder) throws IOException {
        String fileName = folder+"/"+ UUID.randomUUID()+file.getOriginalFilename();
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).
                setContentType(file.getContentType()).build();
        storage.create(blobInfo, file.getBytes());
        return fileName;
    }

    public URL signFile(String fileName) {
        BlobId blobId = BlobId.of(bucketName, fileName);
        Blob blob = storage.get(blobId);
        if (blob == null) return null;
        BlobInfo blobInfo = blob.asBlobInfo();
        return storage.signUrl(blobInfo, 12l, TimeUnit.HOURS, SignUrlOption.withV4Signature());
    }

}
