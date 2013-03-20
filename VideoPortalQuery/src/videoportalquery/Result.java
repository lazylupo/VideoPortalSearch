
package videoportalquery;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Result {

    /**
     * Größe des Puffers zur Ausgabe.
     */
    private static final short OUTPUT_BUFFER_SIZE = 2048;

    public Result() {
    }

    private List<ResultItem> results = new ArrayList<ResultItem>();
    private List<ResultItem> saveImages = new ArrayList<ResultItem>();
    private List<ResultItem> negativeRelevanceFeedbackList = new ArrayList<ResultItem>();
    private List<ResultItem> positiveRelevanceFeedbackList = new ArrayList<ResultItem>();

    public List<ResultItem> getResults() {
        return results;
    }

    public void setResults(List<ResultItem> results) {
        this.results = results;
    }

    public int getResultsCount(){
        return getResults().size();
    }

    public List<ResultItem> getSaveImages() {
        return saveImages;
    }

    public void setSaveImages(List<ResultItem> saveImages) {
        this.saveImages = saveImages;
    }

    public List<ResultItem> getNegativeRelevanceFeedbackList() {
        return negativeRelevanceFeedbackList;
    }

    public void setNegativeRelevanceFeedbackList(List<ResultItem> negativeRelevanceFeedbackList) {
        this.negativeRelevanceFeedbackList = negativeRelevanceFeedbackList;
    }

    public List<ResultItem> getPositiveRelevanceFeedbackList() {
        return positiveRelevanceFeedbackList;
    }

    public void setPositiveRelevanceFeedbackList(List<ResultItem> positiveRelevanceFeedbackList) {
        this.positiveRelevanceFeedbackList = positiveRelevanceFeedbackList;
    }

    public int saveImagesZIP(File destination) throws IOException {
        int result = 0;

        try {
            File temp = File.createTempFile("myTemp", Long.toString(System.nanoTime()));
            temp.delete();
            temp.mkdir();
            for (ResultItem r : getSaveImages()) {
                result++;
                URL url = new URL(r.getImageURL());
                FileOutputStream fout = new FileOutputStream(new File(temp, result + ".jpg"));
                InputStream urlDownload = url.openStream();
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = urlDownload.read(buffer)) != -1) {
                    fout.write(buffer, 0, bytesRead);
                }
                fout.close();
                urlDownload.close();
            }
            zip(destination, temp);
        } catch (Exception ex) {
            throw new IOException();
        }
        
        return result;
    }


    /**
     * Packt den Inhalt eines Ordners in ein ZIP-komprimiertes Archiv.
     *
     * @param destination Ziel-Pfad des ZIP-Archivs
     * @param source Quelle der Dateien, aus denen das Archiv erstellt werden
     *               soll
     * @throws IOException Bei fehlerhaftem Bearbeiten des ZIP-Archives
     */
    private static void zip(File destination, File sourceFile)
            throws IOException {
        File[] sourceFiles = sourceFile.listFiles();

        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(
            destination));
        for (int i = 0; i < sourceFiles.length; i++) {
            File item = sourceFiles[i];

                String fileName = item.getName();
                ZipEntry zipItem = new ZipEntry(fileName);
                zipItem.setSize(item.length());
                zipItem.setTime(item.lastModified());
                zipItem.setMethod(ZipEntry.DEFLATED);

                BufferedInputStream fileIn = new BufferedInputStream(
                    new FileInputStream(item));
                zipOut.putNextEntry(zipItem);
                byte[] buffer = new byte[OUTPUT_BUFFER_SIZE];
                int num;

                while ((num = fileIn.read(buffer)) >= 0) {
                    zipOut.write(buffer, 0, num);
                }
                fileIn.close();

        }
        zipOut.close();
    }
}
