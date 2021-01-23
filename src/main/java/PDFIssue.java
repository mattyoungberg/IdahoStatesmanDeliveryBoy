import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class PDFIssue {

    private final List<byte[]> pdfs;

    PDFIssue(List<byte[]> pdfs) {
        this.pdfs = pdfs;
    }

    void save(Path directory, String fileName) throws IOException {
        List<Path> pathsToPDFs = null;
        try {
            pathsToPDFs = createTempFiles(pdfs);
            if(!fileName.endsWith(".pdf"))
                fileName = fileName + ".pdf";
            Path finalPath = Paths.get(directory.toString(), fileName);
            mergeAndSave(pathsToPDFs, finalPath);
        } finally {
            if(pathsToPDFs != null)
                cleanUpTempFiles(pathsToPDFs);
        }
    }

    private static List<Path> createTempFiles(List<byte[]> pdfs) throws IOException {
        List<Path> paths = new ArrayList<>();
        Path currentPath;
        for(byte[] bytes : pdfs) {
            currentPath = Files.createTempFile(String.valueOf(System.currentTimeMillis()), ".pdf");
            Files.write(currentPath, bytes);
            paths.add(currentPath);
        }
        return paths;
    }

    private static void mergeAndSave(List<Path> pathsToPDFs, Path pathToSave) throws IOException {
        PDFMergerUtility mergerUtility = new PDFMergerUtility();
        List<File> pdfFiles = pathsToPDFs.stream()
                .map(Path::toFile)
                .collect(Collectors.toList());
        for(File pdf : pdfFiles)  // To register with method signature exception
            mergerUtility.addSource(pdf);
        mergerUtility.setDestinationFileName(pathToSave.toString());
        mergerUtility.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
    }

    private static void cleanUpTempFiles(List<Path> pathsToPDFs) throws IOException {
        for(Path path : pathsToPDFs)
            Files.deleteIfExists(path);
    }
}
