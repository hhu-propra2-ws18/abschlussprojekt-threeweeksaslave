package propra2.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    void init();

    void store(MultipartFile file, String fileName, Long productId);

    Path load(String filename, Long productId);

    Resource loadAsResource(String filename, Long productId);

    void deleteAll();

    void deleteFile(Long productId);
}
