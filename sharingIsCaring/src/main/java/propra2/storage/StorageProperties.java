package propra2.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.List;

@ConfigurationProperties("storage")
public class StorageProperties {

    /**
     * Folder location for storing files
     */
    public List<String> fileEndings = Arrays.asList(".jpg", ".png", ".JPG", ".PNG", ".jpeg",".JPEG", ".gif", ".GIF");

    private String location = "src/main/resources/static/upload-dir";

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
