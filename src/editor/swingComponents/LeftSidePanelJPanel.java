/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.swingComponents;

import editor.Gui;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.java.balloontip.BalloonTip;
import net.java.balloontip.styles.ToolTipBalloonStyle;

/**
 * Reprezentuje levy bocni panel okna editoru clanku white-bear webu. Obsahuje
 * JLabel do ktereho se vklada obrazek o presne velikosti 300x225px. Jinak je
 * vyhozeno chybove okno. Po klepnuti na obrazek ho lze vymenit za jiny v
 * dialogovem okne. Trida automaticky kontroluje ze vybirany obrzek se nachazi
 * ve spravne slozce thumbnails. K obrazku uchovava alt a figcaption jejichz
 * obsah se kontroluje. Pokud je nepovoleny obarvi se prislusny jTextField
 * cervene jinak zelene.
 *
 * @author Ondřej Mejzlík
 */
// Final zpusobi, ze nelze vytvaret potomky teto tridy, kteri by mohli prekryt
// metodu ktera se vola z konstruktoru a tim pokazit chovani teto tridy.
public class LeftSidePanelJPanel extends JPanel {

    private static final int IMAGE_WIDTH = 300;
    private static final int IMAGE_HEIGHT = 225;
    protected static final Color WRONG_RED_FIELD = new Color(255, 204, 204);
    protected static final Color CORRECT_GREEN_FIELD = new Color(204, 255, 204);

    protected final BalloonTip ALT_EMPTY;
    protected final BalloonTip ALT_DEFAULT;
    protected final BalloonTip FIGCAPTION_EMPTY;
    protected final BalloonTip FIGCAPTION_DEFAULT;
    private final ToolTipBalloonStyle BALLOON_STYLE;

    protected JLabel jLabelMainPhoto;
    protected JTextField jTextFieldMainAlt;
    protected JTextField jTextFieldMainFigcaption;
    private JFrame jFramePictureProperties;
    protected Gui parentFrame;
    protected BufferedImage image;
    private String alt;
    private String oldThumbnailLocation;
    private String thumbnailLocation;
    private String figcaption;
    private String originalLocation;
    private JTextField jTextFieldOriginal;
    private JTextField jTextFieldThumbnail;
    private final JFileChooser chooser;
    // Defaultni error stete je true, na false se zmeni az po uspesnem vlozeni
    // obrazku.
    private boolean errorState = true;

    /**
     * Konstruktor, vytvari novy panel s prazdnym obrazkem a defaultnimi
     * popiskami. Protoze volani jineho konstruktoru musi byt prvni, nelze zde
     * nahrat defaultni obrazek. Proto se vola 2. konstruktor se specialnimi
     * parametry a ten se o vytvoreni noveho prazdneho obrazku postara.
     *
     * @param parentFrame JFrame ze ktereho se s timto panelem pracuje. Slouzi k
     * deaktivaci hlavniho okna v pripade, ze je otevrene nejake editacni okno.
     */
    public LeftSidePanelJPanel(Gui parentFrame) {
        this(parentFrame, "EmptyLabel", "Empty alt", "Empty figcaption", null);
    }

    /**
     * Konstruktor zakladajici levy panel s defaultnim obsahem s moznosti
     * nastavit pracovni adresar, odkud se obrazek vybira.
     *
     * @param parentFrame JFrame ze ktereho se s timto panelem pracuje. Slouzi k
     * deaktivaci hlavniho okna v pripade, ze je otevrene nejake editacni okno.
     * @param directory Pracovni adresar.
     */
    public LeftSidePanelJPanel(Gui parentFrame, String directory) {
        this(parentFrame, "EmptyLabel", "Empty alt", "Empty figcaption", directory);
    }

    /**
     * Vytvori panel ze zadanych parametru, u kterycho kontroluje jejich
     * spravnost. V pripade chyby se obrazek nevlozi a je vyhozena varovna
     * hlaska.
     *
     * @param parentFrame JFrame ze ktereho se s timto panelem pracuje. Slouzi k
     * deaktivaci hlavniho okna v pripade, ze je otevrene nejake editacni okno.
     * @param thumbnailLocation Cesta k obrazku. Musi byt ve slozce
     * /images/thumbnails webove stranky whitebear
     * @param alt Alt popis k obrazku, ktery se preda do html pri prevodu.
     * @param figcaption Figcaption html popis obrazku.
     * @param directory Pracovni adresar, slouzi k urceni kde se vybiraji
     * obrazky.
     */
    public LeftSidePanelJPanel(Gui parentFrame, String thumbnailLocation, String alt, String figcaption, String directory) {
        this.parentFrame = parentFrame;
        this.chooser = new JFileChooser();
        this.chooser.addChoosableFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "bmp"));
        this.chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (directory != null) {
            this.chooser.setCurrentDirectory(new File(directory));
        }
        this.makeDialog();
        // SetUpThisPanel muzeme zavolat pred kontrolou udaju, protoze na nich nezalezi.
        this.setUpThisPanel();
        // Balloon tipy nechceme vytvaret pokazde znovu v metode checkTextField, staci jednou a ihned je schovame.
        this.BALLOON_STYLE = new ToolTipBalloonStyle(new Color(255, 253, 217), Color.BLACK);

        this.ALT_EMPTY = new BalloonTip(this.jTextFieldMainAlt, new JLabel("Alt can not be empty"),
                this.BALLOON_STYLE, BalloonTip.Orientation.LEFT_ABOVE, BalloonTip.AttachLocation.ALIGNED, 0, 0, false);

        this.ALT_DEFAULT = new BalloonTip(this.jTextFieldMainAlt, new JLabel("Insert meaningful alt description"),
                this.BALLOON_STYLE, BalloonTip.Orientation.LEFT_ABOVE, BalloonTip.AttachLocation.ALIGNED, 0, 0, false);

        this.FIGCAPTION_EMPTY = new BalloonTip(this.jTextFieldMainFigcaption, new JLabel("Figcaption can not be empty"),
                this.BALLOON_STYLE, BalloonTip.Orientation.LEFT_BELOW, BalloonTip.AttachLocation.ALIGNED, 0, 0, false);

        this.FIGCAPTION_DEFAULT = new BalloonTip(this.jTextFieldMainFigcaption, new JLabel("Insert meaningful figcaption"),
                this.BALLOON_STYLE, BalloonTip.Orientation.LEFT_BELOW, BalloonTip.AttachLocation.ALIGNED, 0, 0, false);

        this.ALT_EMPTY.setVisible(false);
        this.ALT_DEFAULT.setVisible(false);
        this.FIGCAPTION_EMPTY.setVisible(false);
        this.FIGCAPTION_DEFAULT.setVisible(false);
        if (thumbnailLocation.equals("EmptyLabel") && (alt.equals("Empty alt")) && (figcaption.equals("Empty figcaption"))) {
            thumbnailLocation = getClass().getResource("/editor/images/thumbnails/emptyImage.png").getPath();
        }
        // Kontrola parametru musi byt v tomto poradi, pokud by prvni bylo process image dojde k chybe vkladani.
        if (this.checkAltEmpty(alt) && this.checkFigcaptionEmpty(figcaption) && this.processImage(thumbnailLocation)) {
            // SetUpThiLabel nevolame pokazde pri zpracovani novych dat, protoze
            // by pokazde pridal novy objekt mouse listeneru a zabiral pamet.
            this.setUpPictureLabel(this.jLabelMainPhoto);
            this.jTextFieldMainAlt.setText(alt);
            this.jTextFieldMainFigcaption.setText(figcaption);
            this.alt = alt;
            this.figcaption = figcaption;
        } else {
            this.displayError();
        }
    }

    /**
     * Zobrazi misto obrazku chybovou hlasku, ze se vlozeni obrazku nezdarilo. K
     * chybe muze dojit, pokud konstruktor dostane nepovolene parametry primo ze
     * souboru, ktery cte parser. Pokud dojde z zadani nepovolenych udaju
     * uzivatelem, situace se resi varovnym oknem a uzivatel musi chybu opravit.
     */
    private void displayError() {
        this.jLabelMainPhoto.setAlignmentY(0.85f);
        this.jLabelMainPhoto.setForeground(new Color(255, 0, 0));
        Font font = new Font("Dialog", 0, 12);
        Map attributes = font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        this.jLabelMainPhoto.setFont(font.deriveFont(attributes));
        this.jLabelMainPhoto.setText("Error inserting image");
        this.errorState = true;
    }

    /**
     * Vraci hodnotu errorState, ktera je true, pokud se nepodarilo nahrat
     * obrazek.
     * 
     * @return Vraci hodnotu errorState, ktera je true, pokud se nepodarilo nahrat
     * obrazek.
     */
    public boolean errorState() {
        return this.errorState;
    }
    
    /**
     * Pri vymeneni jednoho leveho panelu za jiny pokud zustanou na predchozim
     * videt bubliny, nezmizi a zobrazuji se dal pres vymeneny panel. Pred
     * vymenou panelu se u predchoziho musi bubliny schovat.
     */
    public void hideBalloonTips() {
        this.ALT_EMPTY.setVisible(false);
        this.ALT_DEFAULT.setVisible(false);
        this.FIGCAPTION_EMPTY.setVisible(false);
        this.FIGCAPTION_DEFAULT.setVisible(false);
    }

    /**
     * Spousti se pri zpracovani obrazku pri vytvareni noveho panelu, nebo pri
     * potvrzeni zmeny obrazku v dialogovem okne. Zkontroluje jestli predane
     * parametry obrazku jsou validni. Pokud jsou a vsechny testy projdou, je
     * obrazek vytvoren. Testy jsou: Obrazek musi byt ve slozce images/thumbnail
     * webu. Obrazek musi jit precist. Velikost obrazku musi byt 300x225px. K
     * obrazku musi existovat jeho originalni nezmensena verze ve slozce
     * images/original.
     *
     * @param thumbnailLocation Umisteni nahledove velikosti obrazku na disku
     */
    private boolean processImage(String thumbnailLocation) {
        BufferedImage newImage = null;
        if (checkThumbnail(thumbnailLocation)) {
            try {
                newImage = ImageIO.read(new File(thumbnailLocation));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Could not read image.", "Error", JOptionPane.WARNING_MESSAGE);
                this.displayError();
            }
            if (newImage != null) {
                if (checkImageSize(newImage)) {
                    if (checkImageOriginal(thumbnailLocation)) {
                        this.originalLocation = thumbnailLocation.replace("thumbnails", "original");
                        this.image = newImage;
                        this.thumbnailLocation = thumbnailLocation;
                        this.jTextFieldThumbnail.setText(this.thumbnailLocation);
                        this.jTextFieldOriginal.setText(originalLocation);
                        this.setPicture();
                        // Pokud projde pouziti vybraneho obrazku, je vse spravne.
                        // Nastavime error state na false.
                        this.errorState = false;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Zkontroluje, ze predany alt parametr obrazku neni prazdny. Pokud je
     * prazdny, je vyhozeno varovne okno.
     *
     * @param alt alt popis obrazku.
     * @return true pokud neni prazdny, false jinak.
     */
    private boolean checkAltEmpty(String alt) {
        if (alt.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Main article photo alt must not be empty.", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Zkontroluje, ze predany figcaption parametr obrazku neni prazdny. Pokud
     * je prazdny, je vyhozeno varovne okno.
     *
     * @param figcaption figcaption popis obrazku.
     * @return true pokud neni prazdny, false jinak.
     */
    private boolean checkFigcaptionEmpty(String figcaption) {
        if (figcaption.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Main article photo figcaption must not be empty.", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Zkontroluje, ze umisteni thumbnailu obrazku na disku je ve slozce
     * images/thumbnails a ze soubor lze precist. Pokud nektera z podminek
     * neprojde, je vyhozeno varovne okno.
     *
     * @param location Umisteni nahledove velikosti obrazku na disku.
     * @return true pokud vsechny podminky projdou, false jinak.
     */
    private boolean checkThumbnail(String location) {
        File thumbnail = new File(location);
        if (!(location.contains("images/thumbnails/"))) {
            JOptionPane.showMessageDialog(null, "Inserting image which is not in thumbnail folder.", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (thumbnail.canRead() && thumbnail.exists() && !(thumbnail.isDirectory())) {
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "Could not read image thumbnail.", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }

    /**
     * Zkontroluje ze obrazek ma velikost 300x225px. Pokud nektera podminka
     * neprojde, je vyhozeno varovne okno.
     *
     * @param image Obrazek ke kontrole.
     * @return true pokud ma obrazek povolenou velikost, false jinak.
     */
    private boolean checkImageSize(BufferedImage image) {
        if (image.getHeight() != LeftSidePanelJPanel.IMAGE_HEIGHT) {
            JOptionPane.showMessageDialog(null, "Image height is not 225 px.", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        } else if (image.getWidth() != LeftSidePanelJPanel.IMAGE_WIDTH) {
            JOptionPane.showMessageDialog(null, "Image width is not 300 px.", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Zkontroluje ze k thumbnailu existuje jeho originalni verze ve slozce
     * images/original. Pokud neexistuje, je vyhozeno varovne okno.
     *
     * @param pathToThumbnail Cesta k thumbnailu na disku.
     * @return true pokud original existuje, false jinak.
     */
    private boolean checkImageOriginal(String pathToThumbnail) {
        String hrefToOriginal = pathToThumbnail.replace("thumbnails", "original");
        File original = new File(hrefToOriginal);
        if (original.exists() && !(original.isDirectory())) {
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "Could not find original size image.", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }

    /**
     * Vraci true pokud jsou zadane udaje alt a figcaption korektni.
     *
     * @return Vraci true pokud jsou zadane udaje alt a figcaption korektni.
     */
    public boolean areValuesOk() {
        return this.checkMainAltTextField() && this.checkMainFigcaptionTextField();
    }

    /**
     * Zkontroluje obsah radku MainAlt. Pokud je obsah prazdny nebo defaultni
     * Empty alt, radek se obarvi do cervena jinak do zelena. A zobrazi bublinu
     * s pricinou chyby. Balloontipy se zobrazi jen, pokud je promenna
     * showBalloonTips nastavena na true.
     *
     * @return Vraci true pokud jsou zadane udaje spravne.
     */
    protected boolean checkMainAltTextField() {
        String newAlt = this.jTextFieldMainAlt.getText().trim();
        if (newAlt.isEmpty()) {
            jTextFieldMainAlt.setToolTipText("Main photo alt can not be empty.");
            jTextFieldMainAlt.setBackground(LeftSidePanelJPanel.WRONG_RED_FIELD);
            this.ALT_EMPTY.setVisible(true);
            return false;
        } else if (newAlt.contains("Empty alt")) {
            jTextFieldMainAlt.setToolTipText("Insert a meaningful alt description.");
            jTextFieldMainAlt.setBackground(LeftSidePanelJPanel.WRONG_RED_FIELD);
            this.ALT_DEFAULT.setVisible(true);
            return false;
        } else {
            this.ALT_EMPTY.setVisible(false);
            this.ALT_DEFAULT.setVisible(false);
            jTextFieldMainAlt.setToolTipText("Main photo alt description OK");
            jTextFieldMainAlt.setBackground(LeftSidePanelJPanel.CORRECT_GREEN_FIELD);
            return true;
        }
    }

    /**
     * Zkontroluje obsah radku MainFigcaption. Pokud je obsah prazdny nebo
     * defaultni Empty figcaption, radek se obarvi do cervena jinak do zelena. A
     * zobrazi bublinu s pricinou chyby. Balloontipy se zobrazi jen, pokud je
     * promenna showBalloonTips nastavena na true.
     *
     * @return Vraci true pokud jsou zadane udaje spravne.
     */
    protected boolean checkMainFigcaptionTextField() {
        String newFigcaption = this.jTextFieldMainFigcaption.getText().trim();
        if (newFigcaption.isEmpty()) {
            jTextFieldMainFigcaption.setToolTipText("Main photo figcaption can not be empty.");
            jTextFieldMainFigcaption.setBackground(LeftSidePanelJPanel.WRONG_RED_FIELD);
            this.FIGCAPTION_EMPTY.setVisible(true);
            return false;
        } else if (newFigcaption.contains("Empty figcaption")) {
            jTextFieldMainFigcaption.setToolTipText("Insert a meaningful figcaption.");
            jTextFieldMainFigcaption.setBackground(LeftSidePanelJPanel.WRONG_RED_FIELD);
            this.FIGCAPTION_DEFAULT.setVisible(true);
            return false;
        } else {
            this.FIGCAPTION_EMPTY.setVisible(false);
            this.FIGCAPTION_DEFAULT.setVisible(false);
            jTextFieldMainFigcaption.setToolTipText("Main photo figcaption description OK");
            jTextFieldMainFigcaption.setBackground(LeftSidePanelJPanel.CORRECT_GREEN_FIELD);
            return true;
        }
    }

    /**
     * Nastavi obrazek do jLabelMainPhoto v tomto panelu.
     */
    protected void setPicture() {
        // Pred vymenou ikony predchozi smazeme.
        ImageIcon newIcon = new ImageIcon(this.image);
        // Vytvoreni nove ikony
        this.jLabelMainPhoto.setIcon(newIcon);
        this.jLabelMainPhoto.revalidate();
        // Po vytvoreni ikony se puvodni obrazek muze smazat a uvolnit pamet.
        newIcon.getImage().flush();
        this.image.flush();
        this.image = null;
    }

    /**
     * Obsluhuje kliknuti na tlacitko OK v dialogovem okne. Spousti metodu
     * acceptChanges, ktera ulozi nastaveni odkazu.
     *
     * @param evt Event predany od mysi. Neni pouzit.
     */
    private void jButtonPictureOkActionPerformed(ActionEvent evt) {
        this.acceptChanges();
    }

    /**
     * Obsluhuje kliknuti na tlacitko Storno v dialogovem okne. Spousti metodu
     * discardChanges, ktera ignoruje nove hodnoty a vrati puvodni.
     *
     * @param evt Event predany od mysi. Neni pouzit.
     */
    private void jButtonPictureStornoActionPerformed(ActionEvent evt) {
        this.discardChanges();
    }

    /**
     * Obsluhuje kliknuti na tlacitko Browse v dialogovem okne. Zazalohuje
     * puvodni obrazek pro pripad, ze se uzivatel rozhodne zmacknout Storno a
     * pote vyhodi okno vyberu noveho souboru. Po potvrzeni vyberu obrazku,
     * naplni alt a figcaption defaultnimi hodnotami.
     *
     * @param evt Event predany od mysi. Neni pouzit.
     */
    private void jButtonPictureBrowseActionPerformed(ActionEvent evt) {
        // Zaloha puvodniho obrazku kvuli stornu.
        this.oldThumbnailLocation = this.thumbnailLocation;
        if (this.chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selection = chooser.getSelectedFile();
            String path = selection.getAbsolutePath();
            // Zobrazi novy obrazek v okne editoru.
            // Pokud je obrazek korektni a muze byt pouzit, priradi se mu
            // defaultni hodnoty popisu.
            if (this.processImage(path)) {
                this.jTextFieldMainAlt.setText("Empty alt");
                this.jTextFieldMainFigcaption.setText("Empty figcaption");
            }
        }
    }

    /**
     * Uklada zmeny provedene v dialogovem okne. Ulozi novy obrazek jako pristi
     * predchozi, viz. metoda browse a posle obrazek ke zpracovani metode
     * processImage a schova okno.
     */
    private void acceptChanges() {
        String thumbnail = this.jTextFieldThumbnail.getText().trim();
        if (this.processImage(thumbnail)) {
            this.oldThumbnailLocation = this.thumbnailLocation;
            this.jFramePictureProperties.setVisible(false);
            this.enableParentFrame();
        }
    }

    /**
     * Aktivuje hlavni okno editoru. Hlavni okno se musi deaktivovat kdyz je
     * otevrene nejake editacni okno aby se uzivateli zabranilo zmenit soubor
     * nebo ulozit nekompletni udaje.
     */
    private void enableParentFrame() {
        parentFrame.setEnabled(true);
    }

    /**
     * Zrusi zmeny provedene v dialogovem okne. Pokud dialogove okno obsahuje
     * defaultni obrazek, neexistuje zadny, ke kteremu by se melo vratit. Jinak
     * vrati puvodni hodnoty predchoziho obrazku.
     */
    private void discardChanges() {
        if (this.thumbnailLocation.contains("emptyImage.png")) {
            this.jFramePictureProperties.setVisible(false);
        } else {
            this.processImage(this.oldThumbnailLocation);
            this.jFramePictureProperties.setVisible(false);
        }
        this.enableParentFrame();
    }

    /**
     * Obsluhuje stisk klavesy v radcich dialogoveho okna. Pokud je klavesa
     * Enter okno se zavre a data ulozi jako pri stisku OK. Pokud je escape,
     * okno se zavre a zmeny se ignoruji jako pri Storno.
     *
     * @param evt Event stisknute klavesy, ktera se vyhodnocuje.
     */
    private void jTextFieldKeyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            this.acceptChanges();
        }
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            this.discardChanges();
        }
    }

    /**
     * Nastavuje rozmisteni komponent v tomto panelu a dava jim jejich defaultni
     * texty a funkce. Protected protoze k teto tride vytvarime potomka a ten na
     * metodu musi videt, aby ji mohl prekryt. Public je zbytecne moc.
     */
    protected void setUpThisPanel() {
        jLabelMainPhoto = new javax.swing.JLabel();
        JLabel jLabel2 = new javax.swing.JLabel();
        jTextFieldMainAlt = new javax.swing.JTextField();
        JLabel jLabel3 = new javax.swing.JLabel();
        jTextFieldMainFigcaption = new javax.swing.JTextField();

        // Prida posluchace zmeny dokumentu k jTextField.
        jTextFieldMainAlt.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkMainAltTextField();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkMainAltTextField();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkMainAltTextField();
            }
        });

        jTextFieldMainFigcaption.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkMainFigcaptionTextField();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkMainFigcaptionTextField();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkMainFigcaptionTextField();
            }
        });

        jLabel2.setText("Alt:");
        jLabel3.setText("Figcaption:");

        javax.swing.GroupLayout thisLayout = new javax.swing.GroupLayout(this);
        this.setLayout(thisLayout);
        thisLayout.setHorizontalGroup(
                thisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jLabelMainPhoto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(thisLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldMainAlt))
                .addGroup(thisLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldMainFigcaption, javax.swing.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE))
        );
        thisLayout.setVerticalGroup(
                thisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(thisLayout.createSequentialGroup()
                        .addComponent(jLabelMainPhoto, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(thisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel2)
                                .addComponent(jTextFieldMainAlt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(thisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel3)
                                .addComponent(jTextFieldMainFigcaption, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
    }

    /**
     * Nastavi PictureLabelu tohoto panelu posluchace na mys a jeho fuknci.
     */
    private void setUpPictureLabel(JLabel pictureLabel) {
        pictureLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            /**
             * Obsluhuje kliknuti na obrazek. Zobrazi editacni okno a deaktivuje
             * hlavni okno editoru.
             *
             * @param evt Nepouzity event.
             */
            @Override
            public void mouseClicked(MouseEvent evt) {
                jFramePictureProperties.setVisible(true);
                // Zaloha puvodniho obrazku pri kliknuti na obrazek. V pripade
                // ze se zmackne Storno, program se bude chtit vratit k puvodnimu obrazku
                // ale pokud predtim nebyl obrazek zmenen pmoci Browse, zadny predchozi neni.
                oldThumbnailLocation = thumbnailLocation;
                parentFrame.setEnabled(false);
            }
        });
    }

    /**
     * Vola se z konstruktoru a pripravuje dialogove okno, ktere se zobrazi po
     * kliknuti na obrazek zobrazovany timto panelem.
     */
    private void makeDialog() {
        this.jFramePictureProperties = new JFrame();
        this.jTextFieldOriginal = new JTextField();
        this.jTextFieldThumbnail = new JTextField();
        JButton jButtonPictureOk = new JButton();
        JButton jButtonPictureStorno = new JButton();
        JButton jButtonPictureBrowse = new JButton();
        JLabel jLabel1 = new JLabel();
        JLabel jLabel4 = new JLabel();
        this.jTextFieldOriginal.setEditable(false);
        jButtonPictureOk.setText("Ok");
        jButtonPictureStorno.setText("Storno");
        jButtonPictureBrowse.setText("Browse");
        jLabel1.setText("Thumbnail src:");
        jLabel4.setText("Href to original:");

        this.jFramePictureProperties.setAlwaysOnTop(true);
        this.jFramePictureProperties.setTitle("Edit picture");
        this.jFramePictureProperties.setResizable(false);

        jButtonPictureOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPictureOkActionPerformed(evt);
            }
        });

        jButtonPictureStorno.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPictureStornoActionPerformed(evt);
            }
        });

        jTextFieldThumbnail.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldKeyPressed(evt);
            }
        });

        jButtonPictureBrowse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPictureBrowseActionPerformed(evt);
            }
        });

        this.jFramePictureProperties.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                discardChanges();
            }
        });

        javax.swing.GroupLayout jFramePicturePropertiesLayout = new javax.swing.GroupLayout(jFramePictureProperties.getContentPane());
        jFramePictureProperties.getContentPane().setLayout(jFramePicturePropertiesLayout);
        jFramePicturePropertiesLayout.setHorizontalGroup(
                jFramePicturePropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jFramePicturePropertiesLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jFramePicturePropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jTextFieldOriginal)
                                .addComponent(jTextFieldThumbnail)
                                .addGroup(jFramePicturePropertiesLayout.createSequentialGroup()
                                        .addGroup(jFramePicturePropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(jFramePicturePropertiesLayout.createSequentialGroup()
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                                                .addComponent(jLabel1)
                                                .addComponent(jLabel4))
                                        .addGap(0, 0, Short.MAX_VALUE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jFramePicturePropertiesLayout.createSequentialGroup()
                                        .addGap(0, 206, Short.MAX_VALUE)
                                        .addGroup(jFramePicturePropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jFramePicturePropertiesLayout.createSequentialGroup()
                                                        .addComponent(jButtonPictureOk, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(jButtonPictureStorno))
                                                .addComponent(jButtonPictureBrowse, javax.swing.GroupLayout.Alignment.TRAILING))))
                        .addContainerGap())
        );
        jFramePicturePropertiesLayout.setVerticalGroup(
                jFramePicturePropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jFramePicturePropertiesLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldThumbnail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jFramePicturePropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jButtonPictureBrowse)
                                .addGroup(jFramePicturePropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldOriginal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jFramePicturePropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jButtonPictureStorno)
                                .addComponent(jButtonPictureOk))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        this.jFramePictureProperties.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        this.jFramePictureProperties.setLocationRelativeTo(null);
        this.jFramePictureProperties.pack();
    }

    /**
     * Vraci alt popis obrazku.
     *
     * @return Alt popis obrazku.
     */
    public String getAlt() {
        return this.alt;
    }

    /**
     * Vraci figcaption k obrazku.
     *
     * @return figcaption k obrazku.
     */
    public String getFigcaption() {
        return this.figcaption;
    }

    /**
     * Vraci umisteni thumbnail obrazku na disku.
     *
     * @return umisteni thumbnail obrazku na disku.
     */
    public String getThumbnailLocation() {
        return this.thumbnailLocation;
    }

    /**
     * Vraci umisteni originalu obrazku na disku.
     *
     * @return umisteni originalu obrazku na disku.
     */
    public String getOriginalLocation() {
        return this.originalLocation;
    }
}
