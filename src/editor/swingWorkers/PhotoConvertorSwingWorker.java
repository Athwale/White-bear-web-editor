/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.swingWorkers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import net.coobird.thumbnailator.Thumbnails;

/**
 * Prvni parametr Boolean urcuje co vraci metoda doInBackground, druhy parametr
 * Integer urcuje co se predava metode publish. Takto muzeme pristupovat ke vsem
 * promennym.
 *
 * @author Ondřej Mejzlík
 */
public class PhotoConvertorSwingWorker extends SwingWorker<Boolean, Integer> {

    private final int thumbnailWidth = 300;
    private final int thumbnailHeight = 225;
    private final int imageMaxSize = 534;

    private final File originalsDirectory;
    private final HashMap<String, String> loadedImages;
    private final JProgressBar jProgressBarImported;
    private final JButton jButtonBrowse;
    private final JButton jButtonImport;
    private final int imagesCount;
    private final boolean makeThumbnails;

    /**
     *
     * @param originalsDirectory
     * @param loadedImages
     * @param jProgressBarImported
     * @param jButtonBrowse
     * @param jButtonImport
     * @param makeThumbnails
     */
    public PhotoConvertorSwingWorker(File originalsDirectory, HashMap<String, String> loadedImages,
            JProgressBar jProgressBarImported, JButton jButtonBrowse, JButton jButtonImport, boolean makeThumbnails) {
        this.originalsDirectory = originalsDirectory;
        this.loadedImages = loadedImages;
        this.jProgressBarImported = jProgressBarImported;
        this.jButtonBrowse = jButtonBrowse;
        this.jButtonImport = jButtonImport;
        this.imagesCount = loadedImages.size();
        this.makeThumbnails = makeThumbnails;
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        BufferedImage thumbnailedImage = null;
        BufferedImage loadedOriginalImage = null;
        BufferedImage halfSizeImage = null;
        // Urcuje kolik obrazku bylo zpracovano, tato hodnota se zobrazuje
        // na progress baru.
        int processed = 1;
        for (String fileName : loadedImages.keySet()) {
            // Vlakno neprestane bezet samo pokud je zavolan cancel.
            if (isCancelled()) {
                break;
            }
            String imagePath = loadedImages.get(fileName);
            try {
                if (makeThumbnails) {
                    // Kontrola thumbnail slozky.
                    // Vime ze predany string obsahuje slovo originals.
                    // Musi existovat slozka v images/thumbnails se stejnym nazvem, jako slozka vybrana v original.
                    String thumbnailsPath = originalsDirectory.getAbsolutePath().replace("original", "thumbnails");
                    // Kontrola existence cilove slozky v thumbnails.
                    File targetThumbnailDirectory = new File(thumbnailsPath);
                    if (!(targetThumbnailDirectory.exists() && targetThumbnailDirectory.isDirectory())) {
                        JOptionPane.showMessageDialog(null, "Target directory in images/thumbnails does not exist.", "Error", JOptionPane.WARNING_MESSAGE);
                    } else {
                        // Nacteni obrazku.
                        File image = new File(imagePath);
                        // Obrazek musime prevest na buffered image kvuli kontrole 
                        // pomeru stran.
                        loadedOriginalImage = ImageIO.read(image);
                        // Umoznuje nacitat velke obrazky rychleji. 
                        loadedOriginalImage.setAccelerationPriority(1);
                        // Zaokrouhleni vysledku deleni na 2 mista. Vysldek je 
                        // 1.33 perioda. Pouzivame BigDecimal protoze double neni presny
                        // reprezentace cisla doublem se muze pri matematickych 
                        // operacich lisit v poslednich desetinnych cislech.
                        BigDecimal aspectRatio = new BigDecimal((double) loadedOriginalImage.getWidth() / loadedOriginalImage.getHeight());
                        aspectRatio = aspectRatio.setScale(2, RoundingMode.HALF_UP);
                        if (!(aspectRatio.doubleValue() == 1.33)) {
                            JOptionPane.showMessageDialog(null, fileName + " aspect ratio is not 4/3, skipping.", "Warning", JOptionPane.WARNING_MESSAGE);
                        } else {
                            // Vyrobeni ciloveho souboru originalniho obrazku.
                            // Obrazky v original jsou 50% zmensene, vytvoreni polovicniho obrazku a jeho ulozeni do original.
                            halfSizeImage = Thumbnails.of(loadedOriginalImage).scale(0.5).asBufferedImage();
                            // File separator vrati spravne lomitko pro dany system.
                            File fileIntoOriginal = new File(originalsDirectory.getAbsolutePath() + File.separator + fileName);
                            // Vytvorit thumbnail a ten ulozit do odpovidajici slozky v thumbnails.
                            thumbnailedImage = Thumbnails.of(loadedOriginalImage).size(thumbnailWidth, thumbnailHeight).asBufferedImage();
                            File fileIntoThumbnails = new File(thumbnailsPath + File.separator + fileName);
                            // Zjisteni pripony.
                            String[] split = fileName.split("\\.");
                            String ext = split[split.length - 1];
                            ImageIO.write(halfSizeImage, ext, fileIntoOriginal);
                            ImageIO.write(thumbnailedImage, ext, fileIntoThumbnails);
                            // Pocitadlo zvedame jen za uspesne ulozeni, na konci bude videt, kolik neproslo.                            
                            publish(processed);
                            // Zvednuti pocitadla zpracovanych souboru.
                            processed++;
                        }
                    }
                    // Je nastaveno jenom zkopirovat do original
                } else {
                    File image = new File(imagePath);
                    loadedOriginalImage = ImageIO.read(image);
                    loadedOriginalImage.setAccelerationPriority(1);
                    // Zkontrolujeme ze velikost nepresahla 534 x 534 px a zkopirujeme obrazek do images/original
                    if (loadedOriginalImage.getWidth() > imageMaxSize || loadedOriginalImage.getHeight() > imageMaxSize) {
                        JOptionPane.showMessageDialog(null, fileName + " image is larger than 534 x 534 px, skipping.", "Warning", JOptionPane.WARNING_MESSAGE);
                    } else {
                        // Zkopirovat obrazek.
                        File originalFile = new File(imagePath);
                        File fileIntoOriginal = new File(originalsDirectory.getAbsolutePath() + File.separator + fileName);
                        // Pripadne existujici obrazky se prepisi.
                        Files.copy(originalFile.toPath(), fileIntoOriginal.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        publish(processed);
                        // Zvednuti pocitadla zpracovanych souboru.
                        processed++;
                    }
                }
                // Uvolneni pameti pro dalsi obrazek.
                if (loadedOriginalImage != null) {
                    loadedOriginalImage.flush();
                    loadedOriginalImage = null;
                }
                if (halfSizeImage != null) {
                    halfSizeImage.flush();
                    halfSizeImage = null;
                }
                if (thumbnailedImage != null) {
                    thumbnailedImage.flush();
                    thumbnailedImage = null;
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Could not read image. " + ex.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
            } catch (ArithmeticException ex) {
                JOptionPane.showMessageDialog(null, "Error calculating aspect ratio " + ex.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
            }
        }

        return true;
    }

    @Override
    protected void done() {
        boolean status;
        try {
            status = get();
            jButtonBrowse.setEnabled(true);
            jButtonImport.setEnabled(true);
        } catch (InterruptedException | ExecutionException | CancellationException e) {
            JOptionPane.showMessageDialog(null, "Image processing stopped, operation canceled.", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Metoda process se vola metodou publish a muze bezpecne pracovat s gui
     * behem behu swing workeru. Aktualizuje jProgressBar podle poctu
     * importovanych obrazku.
     *
     * @param chunks Vystup z metody publish. Metoda muze byt zavolana vicekrat,
     * proto list.
     */
    @Override
    protected void process(List<Integer> chunks) {
        int mostRecentValue = chunks.get(chunks.size() - 1);
        jProgressBarImported.setValue(mostRecentValue);
        jProgressBarImported.setString(String.valueOf(mostRecentValue) + "/" + this.imagesCount);
    }
}
