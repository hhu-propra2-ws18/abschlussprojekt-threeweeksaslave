package propra2.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    void init();

    void store(MultipartFile file, String fileName, String productId);

    Stream<Path> loadAll();

    Path load(String filename, String productId, boolean dummyProductPicture);

    Resource loadAsResource(String filename, String productId);

    void deleteAll();

}
