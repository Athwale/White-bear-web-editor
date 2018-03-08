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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * Trida reprezentuje flashove video v textu stranky. Vytvari obrazek
 * reprezentujici video, ktery se muze vlozit do textu. Kliknuti na obrazek
 * zobrazi dialogove okno, kde jde nastavit odkaz a popis videa.
 *
 * @author Ondřej Mejzlík
 */
public final class VideoJLabel extends JLabel {

    private String title;
    private String src;
    private JFrame jFrameVideoProperties;
    private Gui parentFrame;
    private JTextField jTextFieldSrc;
    private JTextField jTextFieldTitle;

    /**
     * Konstruktor Vytvari prazdne video s implicitnim obsahem Empty title a
     * zacatkem URL adresy http://.
     *
     * @param parentFrame JFrame ze ktereho se s timto videem pracuje. Slouzi k
     * deaktivaci hlavniho okna v pripade, ze je otevrene nejake editacni okno.
     */
    public VideoJLabel(Gui parentFrame) {
        this(parentFrame, "Empty title", "http://");
    }

    /**
     * Konstruktor vytvari objekt videa s obsahem z predanych parametru. Obsah
     * je zkontrolovan a pokud je nekorektni, je misto obrazku vlozen chybovy
     * text.
     *
     * @param parentFrame JFrame ze ktereho se s timto videem pracuje. Slouzi k
     * deaktivaci hlavniho okna v pripade, ze je otevrene nejake editacni okno.
     * @param title Nazev videa.
     * @param src Odkaz na video.
     */
    public VideoJLabel(Gui parentFrame, String title, String src) {
        super();
        this.parentFrame = parentFrame;
        this.makeDialog();
        // SetUpThiLabel nevolame pokazde pri zpracovani novych dat, protoze
        // by pokazde pridal novy objekt mouse listeneru a zabiral pamet.
        this.setUpThisLabel();
        if (!(this.processData(title, src))) {
            this.displayError();
        }
    }

    /**
     * Zobrazi misto videa chybovou hlasku, ze se vlozeni videa nezdarilo. K
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
        this.setText("Error inserting flash video");
    }

    /**
     * Metoda se spousti z konstruktoru a pri zmene udaju v dialogovem okne.
     * Zkontroluje udaje, ktere se maji videu priradit a pokud jsou vsechny
     * spravne, jsou vlozeny do promennych instance a nastaveny do labelu.
     *
     * @param title popis videa
     * @param src odkaz na video
     * @return True pokud jsou vsechny udaje sprave, false jinak.
     */
    private boolean processData(String title, String src) {
        if (this.checkTitleEmpty(title)) {
            if (this.checkSrc(src)) {
                this.title = title;
                this.src = src;
                this.jTextFieldSrc.setText(this.src);
                this.jTextFieldTitle.setText(this.title);
                this.setTextUnderImage();
                return true;
            }
        }
        return false;
    }

    /**
     * Nastavi novy text pod obrazek vlozeneho videa. Tento text se vezme z
     * promenne title po zpracovani metodou processData.
     */
    private void setTextUnderImage() {
        this.setText("Flash: " + this.title);
    }

    /**
     * Zkontroluje ze predany title k videu neni prazdny. Pokud je prazdny, je
     * zobrazeno varovne okno a metoda vrati false.
     *
     * @param title text popisu videa
     * @return True pokud neni prazdny, false jinak.
     */
    private boolean checkTitleEmpty(String title) {
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Flash video title must not be empty", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Zkontroluje ze predany odkaz na video zacina jako spravna URL adresa.
     * Pokud ne, je zobrazeno varovne okno a metoda vrati false.
     *
     * @param src odkaz na video
     * @return True pokud je odkaz korektni, false jinak.
     */
    private boolean checkSrc(String src) {
        if (src.startsWith("http://") || src.startsWith("https://")) {
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "Wrong flash video URL", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }

    /**
     * Nastavi teto label reprezentujici video jeji obrazek a umisti popis videa
     * pod obrazek. Pokud obrazek nelze precist, je vyhozeno chybove okno a
     * uzivatel musi chybu opravit.
     */
    private void setUpThisLabel() {
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            /**
             * Obsluhuje kliknuti na label odkazu. Zobrazi dialogove okno s
             * obsahem odkazu a deaktivuje hlavni okno editoru.
             *
             * @param evt Event predany od mysi. Neni pouzit.
             */
            @Override
            public void mouseClicked(MouseEvent evt) {
                jFrameVideoProperties.setVisible(true);
                parentFrame.setEnabled(false);
            }
        });
        String pictureLocation = getClass().getResource("/editor/images/original/flash.png").getPath();
        // Soubor s obrazkem bude vzdy v jaru dostupny.
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(pictureLocation));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Could not read flash video image.", "Error", JOptionPane.WARNING_MESSAGE);
            this.displayError();
        }
        this.setIcon(new ImageIcon(image));
        //this.setAlignmentY(0.85f);
        // Zpusobi ze text labelu se zobrazi pod obrazkem.
        this.setHorizontalTextPosition(JLabel.CENTER);
        this.setVerticalTextPosition(JLabel.BOTTOM);
        this.setText("Flash: ");
        this.setBorder(new CompoundBorder(new EmptyBorder(10, 20, 10, 5), new LineBorder(Color.black, 1)));
    }

    /**
     * Obsluhuje kliknuti na tlacitko OK v dialogovem okne. Spousti metodu
     * acceptChanges, ktera ulozi nastaveni odkazu.
     *
     * @param evt Event predany od mysi. Neni pouzit.
     */
    private void jButtonVideoOkActionPerformed(ActionEvent evt) {
        this.acceptChanges();
    }

    /**
     * Obsluhuje kliknuti na tlacitko Storno v dialogovem okne. Spousti metodu
     * discardChanges, ktera ignoruje nove hodnoty a vrati puvodni.
     *
     * @param evt Event predany od mysi. Neni pouzit.
     */
    private void jButtonVideoStornoActionPerformed(ActionEvent evt) {
        this.discardChanges();
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
     * Spousti se pri stisku OK v dialogovem okne. Zkontroluje nove zadane udaje
     * a pokud jsou vyhovujici, okno se zavre. Metoda nedovoluje aby v odkazu
     * zustaly defaultni hodnoty. Jakmile uzivatel jednou odkaz otevre, zadane
     * udaje by mely byt korektni a ne defaultni. V chybnem pripade je vyhozeno
     * varovne okno a uzivatel musi zadat korektni udaje.
     */
    private void acceptChanges() {
        String newTitle = this.jTextFieldTitle.getText().trim();
        String newSrc = this.jTextFieldSrc.getText().trim();
        // Kontrola na Empty title se provadi zde a ne v process data, protoze
        // video musi jit vlozit z konstruktoru s empty obsahem.
        if (newTitle.contains("Empty title")) {
            JOptionPane.showMessageDialog(null, "Insert a meaningful title.", "Error", JOptionPane.WARNING_MESSAGE);
        } else if (newSrc.equals("http://")) {
            JOptionPane.showMessageDialog(null, "Insert a correct link.", "Error", JOptionPane.WARNING_MESSAGE);
        } else if (this.processData(newTitle, newSrc)) {
            this.jFrameVideoProperties.setVisible(false);
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
     * Spousti se pri stisknuti tlacitka Storno. Ignoruje nove zadane udaje a
     * vraci puvodni.
     */
    private void discardChanges() {
        this.jTextFieldTitle.setText(this.title);
        this.jTextFieldSrc.setText(this.src);
        this.jFrameVideoProperties.setVisible(false);
        this.enableParentFrame();
    }

    /**
     * Vytvori dialogove okno pro toto video a ulozi jeho odkaz do promenne
     * instance.
     */
    private void makeDialog() {
        this.jFrameVideoProperties = new JFrame();
        this.jFrameVideoProperties.setAlwaysOnTop(true);
        this.jFrameVideoProperties.setTitle("Edit flash video");
        this.jFrameVideoProperties.setResizable(false);
        JLabel jLabel5 = new JLabel();
        JLabel jLabel6 = new JLabel();
        JLabel jLabel7 = new JLabel();
        jLabel5.setText("Title:");
        jLabel6.setText("Src:");
        jLabel7.setText("Size 534 x 405 px");
        JButton jButtonVideoOk = new JButton();
        JButton jButtonVideoStorno = new JButton();
        jButtonVideoOk.setText("Ok");
        jButtonVideoStorno.setText("Storno");
        this.jTextFieldSrc = new JTextField();
        this.jTextFieldTitle = new JTextField();

        jButtonVideoOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonVideoOkActionPerformed(evt);
            }
        });

        jButtonVideoStorno.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonVideoStornoActionPerformed(evt);
            }
        });

        jTextFieldSrc.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldKeyPressed(evt);
            }
        });

        jTextFieldTitle.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldKeyPressed(evt);
            }
        });

        /**
         * Hlida kliknuti na krizek pro zavreni. V tom pripade spousti metodu
         * discard changes.
         */
        this.jFrameVideoProperties.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                discardChanges();
            }
        });

        javax.swing.GroupLayout jFrameVideoPropertiesLayout = new javax.swing.GroupLayout(jFrameVideoProperties.getContentPane());
        jFrameVideoProperties.getContentPane().setLayout(jFrameVideoPropertiesLayout);
        jFrameVideoPropertiesLayout.setHorizontalGroup(
                jFrameVideoPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jFrameVideoPropertiesLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jFrameVideoPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jTextFieldSrc, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jTextFieldTitle)
                                .addGroup(jFrameVideoPropertiesLayout.createSequentialGroup()
                                        .addGroup(jFrameVideoPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jLabel5)
                                                .addComponent(jLabel6))
                                        .addGap(0, 0, Short.MAX_VALUE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jFrameVideoPropertiesLayout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 89, Short.MAX_VALUE)
                                        .addComponent(jButtonVideoOk, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButtonVideoStorno)))
                        .addContainerGap())
        );
        jFrameVideoPropertiesLayout.setVerticalGroup(
                jFrameVideoPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jFrameVideoPropertiesLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldSrc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jFrameVideoPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jButtonVideoStorno)
                                .addComponent(jButtonVideoOk)
                                .addComponent(jLabel7))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        this.jFrameVideoProperties.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        this.jFrameVideoProperties.setLocationRelativeTo(null);
        this.jFrameVideoProperties.pack();
    }
}
