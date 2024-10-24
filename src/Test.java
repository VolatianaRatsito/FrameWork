package mg.controller;

import mg.controller.principal.*;
import mg.Models.Genre;
import mg.Models.Personne;
import mg.annotation.*;
import mg.tool.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;



@Controller
public class Test {

    @Url(value="/formulaire")
    @Get()
    public ModelAndView showFormulaire() {
        ModelAndView mav = new ModelAndView("formulaire");
        return mav;
    }

    @Url(value="/upload")  
    @Post()
    public String uploadFile(@RequestParameter("file") MultiPart file) {
    if (file == null || file.getFileContent() == null) {
        return "Aucun fichier sélectionné pour le téléchargement.";
    }
    
    long fileSize = file.getFileSize();
    if (fileSize > 0) {
        try {
            String uploadDirectory = "C:\\apache-tomcat-10.1.7\\webapps\\uploads\\";
            File uploadDir = new File(uploadDirectory);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            String fileName = file.getFileName();
            String filePath = uploadDirectory + fileName;
            try (InputStream fileContent = file.getFileContent(); 
                 FileOutputStream outputStream = new FileOutputStream(new File(filePath))) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fileContent.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            // Retourner un message de succès
            return "Fichier téléchargé avec succès : " + fileName; // Ajoutez ceci
        } catch (IOException e) {
            return "Échec du téléchargement du fichier : " + e.getMessage();
        }
    } else {
        return "Aucun fichier sélectionné pour le téléchargement.";
    }
}


}
