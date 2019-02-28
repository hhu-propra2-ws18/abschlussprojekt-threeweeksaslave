package propra2.storage;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.SocketUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;
    private final StorageProperties properties;

    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
    	this.properties = properties;
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public void store(MultipartFile file, String fileName, Long productId) {
        String filename = StringUtils.cleanPath(fileName);
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new StorageException(
                        "Cannot store file with relative path outside current directory "
                                + filename);
            }
            try (InputStream inputStream = file.getInputStream()) {
            	Path currentRootLocation = Paths.get(properties.getLocation() + "/" + productId);
				try {
					Files.createDirectories(currentRootLocation);
				}
				catch (IOException e) {
					throw new StorageException("Could not initialize storage", e);
				}
                Files.copy(inputStream, currentRootLocation.resolve(filename),
                    StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }
    }


    @Override
    public Path load(String filename, Long productId) {
		Path currentRootLocation = Paths.get(properties.getLocation() + "/" + productId);
		for(String fileEnding : properties.fileEndings) {
			Path runningCurrentRootLocation = Paths.get(currentRootLocation.toString() + "/" + filename + fileEnding);
			if (Files.exists(runningCurrentRootLocation) || Files.isReadable(runningCurrentRootLocation)) {
				return runningCurrentRootLocation;
			}
		}
		currentRootLocation = Paths.get("src/main/resources/static/img");
		return currentRootLocation.resolve("dummyProductPicture.JPG");

    }

    @Override
    public Resource loadAsResource(String filename, Long productId) {
        try {
            Path file = load(filename, productId);
            return new UrlResource(file.toUri());
        }
        catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void deleteFile(Long productId){
        Path fileLocation = Paths.get(rootLocation.toString()+ "/"+ productId);
        FileSystemUtils.deleteRecursively(fileLocation.toFile());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        }
        catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }
}
