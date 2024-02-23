package utility;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;
import java.net.URISyntaxException;

public class TesseractFunctions {

    public static String[] extractText(File file) {
        File jarFile = null;
        String[] textArray = new String[0];
        Tesseract tesseract = new Tesseract();
        try {
            jarFile = new File(TesseractFunctions.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            String jarParent = jarFile.getParent();
            String tessDataPath = jarParent + File.separator + "tessdata";
            tesseract.setDatapath(tessDataPath);
            tesseract.setLanguage("eng");

            String text = tesseract.doOCR(file);
            textArray = text.split("\n");
            return textArray;
        }
        catch (TesseractException | URISyntaxException e) {

        }


        return textArray;
    }
}
