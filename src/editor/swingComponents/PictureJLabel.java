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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.TextAttribute;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Trida reprezentuje obrazek, ktery lze vlozit do textu. K tomuto obrazku patri
 * i dialogove okno, ktere umoznuje obrazek zmenit a nastavit jeho parametry.
 *
 * @author Ondřej Mejzlík
 */
public final class PictureJLabel extends JLabel {

    private static final int IMAGE_WIDTH = 534;
    private static final int IMAGE_HEIGHT = 534;

    private BufferedImage image;
    private String oldAlt;
    private String alt;
    private String oldThumbnailLocation;
    private String thumbnailLocation;
    private String originalLocation;
    private String imageSize;
    private JFrame jFramePictureProperties;
    private Gui parentFrame;
    private JTextField jTextFieldOriginal;
    private JTextField jTextFieldThumbnail;
    private JTextField jTextFieldAlt;
    private JLabel jLabelImageSize;
    private final JFileChooser chooser;

    /**
     * Konstruktor, vytvari novy prazdny obrazek. Protoze volani jineho
     * konstruktoru musi byt prvni, nelze zde nahrat defaultni obrazek. Proto se
     * vola 2. konstruktor se specialnimi parametry a ten se o vytvoreni noveho
     * prazdneho obrazku postara.
     *
     * @param parentFrame JFrame ze ktereho se s timto obrazkem pracuje. Slouzi
     * k deaktivaci hlavniho okna v pripade, ze je otevrene nejake editacni
     * okno.
     */
    public PictureJLabel(Gui parentFrame) {
        this(parentFrame, "EmptyLabel", "EmptyAlt");
    }

    /**
     * Vytvori obrazek ze zadanych parametru, u kterycho kontroluje jejich
     * spravnost. V pripade chyby se obrazek nevlozi a je vyhozena varovna
     * hlaska.
     *
     * @param parentFrame JFrame ze ktereho se s timto obrazkem pracuje. Slouzi
     * k deaktivaci hlavniho okna v pripade, ze je otevrene nejake editacni
     * okno.
     * @param pictureLocation Cesta k obrazku. Musi byt ve slozce
     * /images/thumbnails webove stranky whitebear
     * @param alt Alt popis k obrazku, ktery se preda do html pri prevodu.
     */
    public PictureJLabel(Gui parentFrame, String pictureLocation, String alt) {
        // Konstruktor predka se vola automaticky pokud je bezparametricky.
        super();
        this.parentFrame = parentFrame;
        this.chooser = new JFileChooser();
        this.chooser.addChoosableFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "bmp"));
        this.chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        this.makeDialog();
        // SetUpThiLabel nevolame pokazde pri zpracovani novych dat, protoze
        // by pokazde pridal novy objekt mouse listeneru a zabiral pamet
        this.setUpThisLabel();
        if (pictureLocation.equals("EmptyLabel") && (alt.equals("EmptyAlt"))) {
            pictureLocation = getClass().getResource("/editor/images/thumbnails/emptyImage.png").getPath();
            alt = "Empty alt";
        }
        if (!(this.processImage(pictureLocation, alt))) {
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
        this.setAlignmentY(0.85f);
        this.setForeground(new Color(255, 0, 0));
        Font font = new Font("Dialog", 0, 12);
        Map attributes = font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        this.setFont(font.deriveFont(attributes));
        this.setText("Error inserting image");
    }

    /**
     * Spousti se pri zpracovani obrazku pri vytvareni noveho PictureLabel, nebo
     * pri potvrzeni zmeny obrazku v dialogovem okne. Zkontroluje jestli predane
     * parametry obrazku jsou validni. Pokud jsou a vsechny testy projdou, je
     * obrazek vytvoren. Testy jsou: alt nesmi byt prazdny. Obrazek musi byt ve
     * slozce images/thumbnail webu. Obrazek musi jit precist. Velikost obrazku
     * nesmi presahnout 534x534px. K obrazku musi existovat jeho originalni
     * nezmensena verze ve slozce images/original.
     *
     * @param thumbnailLocation Umisteni nahledove velikosti obrazku na disku
     * @param alt alt (html) popis k obrazku.
     */
    private boolean processImage(String thumbnailLocation, String alt) {
        BufferedImage newImage = null;
        if (checkAltEmpty(alt)) {
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
                            this.alt = alt;
                            this.thumbnailLocation = thumbnailLocation;
                            this.jTextFieldAlt.setText(this.alt);
                            this.jTextFieldThumbnail.setText(this.thumbnailLocation);
                            this.jTextFieldOriginal.setText(originalLocation);
                            this.imageSize = this.image.getWidth() + " x " + this.image.getHeight() + " px";
                            this.jLabelImageSize.setText(this.imageSize);
                            this.setPicture();
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Nastavi teto Picture label novy obrazek po zpracovani metodou
     * processImage a jeho popisek. Obrazek a popisek se vezou z promennych
     * instance alt a image.
     */
    private void setPicture() {
        this.setText("Alt: " + this.alt);
        // Pred vymenou ikony predchozi smazeme.
        ImageIcon newIcon = new ImageIcon(this.image);
        // Vytvoreni nove ikony
        this.setIcon(newIcon);
        this.revalidate();
        // Po vytvoreni ikony se puvodni obrazek muze smazat a uvolnit pamet.
        newIcon.getImage().flush();
        this.image.flush();
        this.image = null;
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
            JOptionPane.showMessageDialog(null, "Alt must not be empty.", "Error", JOptionPane.WARNING_MESSAGE);
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
     * Zkontroluje ze obrazek neni vetsi nez dane meze (534x534px). Pokud
     * nektera podminka neprojde, je vyhozeno varovne okno.
     *
     * @param image Obrazek ke kontrole.
     * @return true pokud ma obrazek povolenou velikost, false jinak.
     */
    private boolean checkImageSize(BufferedImage image) {
        if (image.getHeight() > PictureJLabel.IMAGE_HEIGHT) {
            JOptionPane.showMessageDialog(null, "Image height larger than 534 px.", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        } else if (image.getWidth() > PictureJLabel.IMAGE_WIDTH) {
            JOptionPane.showMessageDialog(null, "Image width larger than 534 px.", "Error", JOptionPane.WARNING_MESSAGE);
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
     * Nastavi teto PictureLabel posluchace na mys a jeji obrazek.
     */
    private void setUpThisLabel() {
        this.addMouseListener(new java.awt.event.MouseAdapter() {
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
                oldAlt = alt;
                parentFrame.setEnabled(false);
            }
        });
        //this.setAlignmentY(0.85f);
        // Zpusobi ze text labelu se zobrazi pod obrazkem.
        this.setHorizontalTextPosition(JLabel.CENTER);
        this.setVerticalTextPosition(JLabel.BOTTOM);
        this.setText("Alt: ");
        this.setBorder(new CompoundBorder(new EmptyBorder(10, 20, 10, 5), new LineBorder(Color.black, 1)));
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
     * puvodni hodnoty pro pripad, ze se uzivatel rozhodne zmacknout Storno a
     * pote vyhodi okno vyberu noveho souboru. Vybrany obrazek doplni o
     * defaultni popis alt a posle ke zpracovani.
     *
     * @param evt Event predany od mysi. Neni pouzit.
     */
    private void jButtonPictureBrowseActionPerformed(ActionEvent evt) {
        // Zaloha puvodniho obrazku kvuli stornu.
        this.oldAlt = this.alt;
        this.oldThumbnailLocation = this.thumbnailLocation;
        if (this.chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selection = chooser.getSelectedFile();
            String path = selection.getAbsolutePath();
            // Zobrazi novy obrazek v okne editoru.
            this.processImage(path, "Empty alt");
        }
    }

    /**
     * Uklada zmeny provedene v dialogovem okne. Ulozi novy obrazek jako pristi
     * predchozi, viz. metoda browse. Pote zkontroluje, ze alt neobsahuje
     * defaultni "Empty alt" a posle obrazek ke zpracovani a ulozi zmeny.
     */
    private void acceptChanges() {
        String thumbnail = this.jTextFieldThumbnail.getText().trim();
        String altText = this.jTextFieldAlt.getText().trim();
        if (altText.contains("Empty alt")) {
            JOptionPane.showMessageDialog(null, "Insert a meaningful alt.", "Error", JOptionPane.WARNING_MESSAGE);
        } else if (this.processImage(thumbnail, altText)) {
            this.oldAlt = this.alt;
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
            this.processImage(this.oldThumbnailLocation, this.oldAlt);
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
     * Vola se z konstruktoru a pripravuje dialogove okno, ktere se zobrazi po
     * kliknuti na obrazek predstavovany timto labelem.
     */
    private void makeDialog() {
        this.jFramePictureProperties = new JFrame();
        this.jTextFieldOriginal = new JTextField();
        this.jTextFieldAlt = new JTextField();
        this.jTextFieldThumbnail = new JTextField();
        this.jLabelImageSize = new JLabel();
        JButton jButtonPictureOk = new JButton();
        JButton jButtonPictureStorno = new JButton();
        JButton jButtonPictureBrowse = new JButton();
        JLabel jLabel1 = new JLabel();
        JLabel jLabel2 = new JLabel();
        JLabel jLabel3 = new JLabel();
        JLabel jLabel4 = new JLabel();
        this.jTextFieldOriginal.setEditable(false);
        jButtonPictureOk.setText("Ok");
        jButtonPictureStorno.setText("Storno");
        jButtonPictureBrowse.setText("Browse");
        jLabel1.setText("Thumbnail src:");
        jLabel2.setText("Size:");
        jLabel3.setText("Alt:");
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

        jTextFieldAlt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldKeyPressed(evt);
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
                                .addComponent(jTextFieldAlt)
                                .addGroup(jFramePicturePropertiesLayout.createSequentialGroup()
                                        .addGroup(jFramePicturePropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(jFramePicturePropertiesLayout.createSequentialGroup()
                                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(jLabelImageSize))
                                                .addComponent(jLabel1)
                                                .addComponent(jLabel3)
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
                                .addGroup(jFramePicturePropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel2)
                                        .addComponent(jLabelImageSize)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldOriginal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldAlt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
}
