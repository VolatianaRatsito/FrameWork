package mg.tool;

import java.io.InputStream;

public class MultiPart {
    private String fileName;
    private long fileSize;
    private InputStream fileContent;

    public MultiPart(){}

    public MultiPart(String fileName, long fileSize, InputStream fileContent) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileContent = fileContent;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public InputStream getFileContent() {
        return fileContent;
    }

    public void setFileContent(InputStream fileContent) {
        this.fileContent = fileContent;
    }
}
