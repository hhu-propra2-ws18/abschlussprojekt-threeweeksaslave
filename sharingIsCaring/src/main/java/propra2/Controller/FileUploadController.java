package propra2.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import propra2.database.Product;
import propra2.repositories.ProductRepository;
import propra2.storage.StorageFileNotFoundException;
import propra2.storage.StorageService;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/upload")
public class FileUploadController {

    private final StorageService storageService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    /**
     * get a list of all uploaded files
     * @param model
     * @return
     * @throws IOException
     */

    /**
     * load file
     *
     * @param filename
     * @param productId
     * @return
     */
    @GetMapping("/files/{productId}/{filename}")
    @ResponseBody
    public Resource serveFile(@PathVariable String filename, @PathVariable Long productId) {
        return storageService.loadAsResource(filename, productId);
    }

    /**
     * upload file
     *
     * @param model
     * @param file
     * @param fileName
     * @param productId
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/")
    public String handleFileUpload(Model model, @RequestParam("file") MultipartFile file, @RequestParam("fileName") String fileName,
                                   @RequestParam("productId") Long productId, RedirectAttributes redirectAttributes) {

        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            String[] originalFilenameArray = originalFilename.split("\\.");
            if (originalFilenameArray.length == 2) {
                deleteCurrentImage(productId, model);
                storageService.store(file, fileName + "." + originalFilenameArray[1], productId);
                redirectAttributes.addFlashAttribute("message",
                        "You successfully uploaded " + file.getOriginalFilename() + "!");
            }
        }
        Optional<Product> productopt = productRepository.findById(productId);
        if (productopt.isPresent()) {
            Product product = productopt.get();
            model.addAttribute(product);
        }
        return "editProductImage";
    }


    /**
     * delete current image
     *
     * @param productId
     * @param model
     * @return
     */
    @PostMapping("/{productId}/deleteCurrentImage")
    public String deleteCurrentImage(@PathVariable Long productId, Model model) {
        storageService.deleteFile(productId);
        Optional<Product> productopt = productRepository.findById(productId);
        if (productopt.isPresent()) {
            Product product = productopt.get();
            model.addAttribute(product);
        }
        return "editProductImage";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}
