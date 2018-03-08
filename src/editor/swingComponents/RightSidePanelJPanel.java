/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.swingComponents;

import java.util.Collections;
import java.util.LinkedList;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

/**
 *
 * @author Ondřej Mejzlík
 */
public final class RightSidePanelJPanel extends JPanel {

    // Tree set automaticky radi prvky podle jejich hash code.
    private LinkedList<RightPanelImageJPanel> images;
    private BoxLayout layout;
    private int orderCounter = 1;

    /**
     * Bezparametricky konstruktor. Vola metodu setUpThisPanel a zaklada
     * potrebne promenne.
     */
    public RightSidePanelJPanel() {
        this.setUpThisPanel();
        this.images = new LinkedList<>();
    }

    /**
     * Vraci true pokud maji vsechny obrazky v tomto panelu zadane korektni
     * udaje. Pokud zadny obrazek nemame, vraci se true.
     *
     * @return Vraci true pokud maji vsechny obrazky v tomto panelu zadane
     * korektni udaje.
     */
    public boolean areAllImagesOK() {
        boolean result = true;
        for (RightPanelImageJPanel image : this.images) {
            result = result && image.areValuesOk();
        }
        return result;
    }

    /**
     * Prida predany panel s obrazkem do linked listu vsech obrazku pokud se
     * obrazek podarilo nacist. To se kontroluje pomoci errorState. Obrazek
     * nemusi pridavat do panelu, protoze metoda vola updateImages. Ktera
     * vsechny obrazky z panelu smaze a znovu nakresli ty, ktere jsou v linked
     * listu po jeho usporadani podle poradi obrazku.
     *
     * @param imagePanel Panel s obrazkem, ktery se pridava.
     */
    public void addImage(RightPanelImageJPanel imagePanel) {
        if (imagePanel.errorState() == false) {
            imagePanel.setOrder(this.orderCounter);
            this.orderCounter++;
            this.images.add(imagePanel);
            this.updateImages();
        }
    }

    /**
     * Smaze predany obrazek z linked listu tohoto panelu. Z panelu samotneho se
     * mazat nemusi, protoze vsechny obrazky v panelu budou smazany a
     * prekresleny metodou updateImages.
     *
     * @param imagePanel Panel s obrazkem, ktery se ma smazat.
     */
    public void removeImage(RightPanelImageJPanel imagePanel) {
        this.images.remove(imagePanel);
        this.updateImages();
    }

    /**
     * Usporada linked list s obrazky v tomto panelu podle jejich poradi, ktere
     * zadal uzivatel do pole Order a prekresli obrazky v panelu s novym
     * usporadanim.
     */
    public void updateImages() {
        Collections.sort(images);
        this.removeAll();
        for (RightPanelImageJPanel image : this.images) {
            this.add(image);
        }
        this.revalidate();
        this.repaint();
    }

    /**
     * Vraci list vsech obrazku v tomto panelu.
     *
     * @return list vsech obrazku v tomto panelu.
     */
    public LinkedList<RightPanelImageJPanel> getImages() {
        return this.images;
    }

    /**
     * Nastavuje layout manager pro tento panel.
     */
    private void setUpThisPanel() {
        // Box layout zachovava velikost vlozenych komponent. Druhy parametr
        // urcuje smer vkladani.
        this.layout = new BoxLayout(this, BoxLayout.Y_AXIS);
        this.setLayout(this.layout);
    }
}
