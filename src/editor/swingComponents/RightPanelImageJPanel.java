/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.swingComponents;

import editor.Gui;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.util.Objects;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Ondřej Mejzlík
 */
public class RightPanelImageJPanel extends LeftSidePanelJPanel implements Comparable<RightPanelImageJPanel> {

    private Image resizedImage;
    private final int width = 211;
    private final int height = 158;
    private final RightSidePanelJPanel rightPanelContainer;
    private JTextField jTextFieldOrder;
    private int order = 0;
    private boolean altOK = false;
    private boolean figcaptionOK = false;
    private boolean orderOK = false;

    /**
     * Konstruktor panelu s obrazkem, ktery prijde do praveho panelu postrannich
     * obrazku v editoru, vytvari prazdny obrazek s defaultnim obsahem.
     *
     * @param parentFrame JFrame ze ktereho se s timto panelem pracuje. Slouzi k
     * deaktivaci hlavniho okna v pripade, ze je otevrene nejake editacni okno.
     * @param container Panel obsahujici postranni obrazky.
     */
    public RightPanelImageJPanel(Gui parentFrame, RightSidePanelJPanel container) {
        this(parentFrame, container, "EmptyLabel", "Empty alt", "Empty figcaption", null);
    }

    /**
     * Konstruktor zakladajici defaultni obrazek s moznosti nastavit pracovni
     * adresar odkud se obrazky vybiraji.
     *
     * @param parentFrame JFrame ze ktereho se s timto panelem pracuje. Slouzi k
     * deaktivaci hlavniho okna v pripade, ze je otevrene nejake editacni okno.
     * @param container Panel obsahujici postranni obrazky.
     * @param directory Pracovni adresar.
     */
    public RightPanelImageJPanel(Gui parentFrame, RightSidePanelJPanel container, String directory) {
        this(parentFrame, container, "EmptyLabel", "Empty alt", "Empty figcaption", directory);
    }

    /**
     * Vytvari novy panel s postrannim obrazkem s pomoci predanych parametru.
     * Uchova odkaz na pravy panel, ktery slouzi k tomu, aby se pri kliknuti na
     * tlacitko smazat, predal pozadavek na odstraneni komponenty s timto
     * obrazkem panelu, ktery obrazky obsahuje. Konstruktor nastavi kazdemu
     * obrazku pri vytvoreni defaultni poradi, ktere si inkrementuje.
     *
     * @param parentFrame JFrame ze ktereho se s timto panelem pracuje. Slouzi k
     * deaktivaci hlavniho okna v pripade, ze je otevrene nejake editacni okno.
     * @param container Panel obsahujici postranni obrazky.
     * @param thumbnailLocation Cesta k obrazku. Musi byt ve slozce
     * /images/thumbnails webove stranky whitebear
     * @param alt Alt popis k obrazku, ktery se preda do html pri prevodu.
     * @param figcaption Figcaption html popis obrazku.
     * @param directory Pracovni adresar, slouzi k nastaveni kde se budou
     * vybirat obrazky.
     */
    public RightPanelImageJPanel(Gui parentFrame, RightSidePanelJPanel container, String thumbnailLocation, String alt, String figcaption, String directory) {
        super(parentFrame, thumbnailLocation, alt, figcaption, directory);
        this.rightPanelContainer = container;
    }

    /**
     * Vraci poradi nastavene tomuto obrazku.
     *
     * @return poradi nastavene tomuto obrazku.
     */
    public int getOder() {
        String order = this.jTextFieldOrder.getText().trim();
        try {
            return Integer.parseInt(order);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Order field does not contain a number.", "Error", JOptionPane.WARNING_MESSAGE);
        }
        return 0;
    }

    /**
     * Nastavi poradi tohoto obrazku.
     *
     * @param order Poradi obrazku.
     */
    public void setOrder(int order) {
        this.order = order;
        this.jTextFieldOrder.setText(String.valueOf(this.order));
    }

    @Override
    protected boolean checkMainAltTextField() {
        String newAlt = this.jTextFieldMainAlt.getText().trim();
        // Balloontipy u praveho panelu zobrazovat nebudeme, je problem s jejich
        // automatickym premistovanim pri mazani obrazku
        if (newAlt.isEmpty()) {
            jTextFieldMainAlt.setToolTipText("Side photo alt can not be empty.");
            jTextFieldMainAlt.setBackground(LeftSidePanelJPanel.WRONG_RED_FIELD);
            return false;
        } else if (newAlt.contains("Empty alt")) {
            jTextFieldMainAlt.setToolTipText("Insert a meaningful alt description.");
            jTextFieldMainAlt.setBackground(LeftSidePanelJPanel.WRONG_RED_FIELD);
            return false;
        } else {
            jTextFieldMainAlt.setToolTipText("Side photo alt description OK");
            jTextFieldMainAlt.setBackground(LeftSidePanelJPanel.CORRECT_GREEN_FIELD);
            return true;
        }
    }

    @Override
    protected boolean checkMainFigcaptionTextField() {
        String newFigcaption = this.jTextFieldMainFigcaption.getText().trim();
        if (newFigcaption.isEmpty()) {
            jTextFieldMainFigcaption.setToolTipText("Side photo figcaption can not be empty.");
            jTextFieldMainFigcaption.setBackground(LeftSidePanelJPanel.WRONG_RED_FIELD);
            return false;
        } else if (newFigcaption.contains("Empty figcaption")) {
            jTextFieldMainFigcaption.setToolTipText("Insert a meaningful figcaption.");
            jTextFieldMainFigcaption.setBackground(LeftSidePanelJPanel.WRONG_RED_FIELD);
            return false;
        } else {
            jTextFieldMainFigcaption.setToolTipText("Side photo figcaption description OK");
            jTextFieldMainFigcaption.setBackground(LeftSidePanelJPanel.CORRECT_GREEN_FIELD);
            return true;
        }
    }

    /**
     * Zkontroluje udaj v radku s poradim obrazku. V radku musi byt napsane
     * cislo.
     *
     * @return true pokud je obsah radku vporadku, false jinak.
     */
    private boolean checkOrderField() {
        String orderText = this.jTextFieldOrder.getText().trim();
        if (orderText.matches("\\d+")) {
            jTextFieldOrder.setToolTipText("Order OK");
            jTextFieldOrder.setBackground(LeftSidePanelJPanel.CORRECT_GREEN_FIELD);
            this.orderOK = true;
            return true;
        } else {
            jTextFieldOrder.setToolTipText("Order must be set to number");
            jTextFieldOrder.setBackground(LeftSidePanelJPanel.WRONG_RED_FIELD);
            this.orderOK = false;
            return false;
        }
    }

    /**
     * Vraci true pokud jsou zadane udaje alt, figcaption a order korektni.
     *
     * @return Vraci true pokud jsou zadane udaje alt a figcaption korektni.
     */
    @Override
    public boolean areValuesOk() {
        return this.checkMainAltTextField() && this.checkMainFigcaptionTextField() && this.checkOrderField();
    }

    /**
     * Odesle scroll panelu, ktery obsahuje tento komponent pozadave, aby jej
     * smazal.
     */
    private void removeSelf() {
        // Schovani pripadnych balloon tipu, ktere by zustaly videt.
        this.hideBalloonTips();
        rightPanelContainer.removeImage(this);
    }

    /**
     * Nastavi obrazek tomuto panelu, ktery se zmensi na 211x158px.
     */
    @Override
    protected void setPicture() {
        this.resizedImage = this.image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        this.jLabelMainPhoto.setIcon(new ImageIcon(this.resizedImage));
    }

    /**
     * Urci zda jsou dva obrazky stejne. Obrazky povazujeme za stejne, pokud
     * maji stejne poradi protoze tato metoda se vyuziva pro serazeni obrazku
     * podle jejich poradi zadaneho uzivatelem.
     *
     * @param obj Objekt ktery musi byt RightPanelImageJPanel.
     * @return True pokud ma tento obrazek a predany obrazek stejne poradi.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RightPanelImageJPanel) {
            RightPanelImageJPanel job = (RightPanelImageJPanel) obj;
            return this.getOder() == (job.getOder());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.resizedImage);
        hash = 42 * hash + Objects.hashCode(this.jTextFieldOrder);
        return hash;
    }

    /**
     * Porovnava tento obrazek s predanym obrazkem podle jejich uzivatelem
     * zadaneho poradi. Vyuziva metodu equals. Pouziva se pro usporadani obrazku
     * v metode sort.
     *
     * @param panel Panel s obrazkem, se kterym se porovnava tento panel.
     * @return 0 pokud maji stejne poradi, -1 pokud ma tento obrazek mensi
     * poradi, 1 pokud ma tento obrazek vyssi poradi.
     */
    @Override
    public int compareTo(RightPanelImageJPanel panel) {
        if (this.equals(panel)) {
            return 0;
        } else if (this.getOder() < panel.getOder()) {
            return -1;
        } else {
            return 1;
        }
    }

    @Override
    protected void setUpThisPanel() {
        jLabelMainPhoto = new javax.swing.JLabel();
        JLabel jLabel2 = new javax.swing.JLabel();
        jTextFieldMainAlt = new javax.swing.JTextField();
        JLabel jLabel3 = new javax.swing.JLabel();
        jTextFieldMainFigcaption = new javax.swing.JTextField();
        JButton jButtonRemove = new javax.swing.JButton();
        JLabel jLabel1 = new javax.swing.JLabel();
        this.jTextFieldOrder = new javax.swing.JTextField();

        // Pokud by se nenastavil rozmer, velikost panelu obrazku by se 
        // prizpusobila nejdelsimu radku s textem a presahla velikost
        // kontejneru.
        Dimension size = new Dimension(310, 270);
        this.setMaximumSize(size);
        this.setMinimumSize(size);
        this.setPreferredSize(size);

        jLabel2.setText("Alt:");
        jLabel3.setText("Figcaption:");
        jButtonRemove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/editor/images/redCross.png"))); // NOI18N
        jLabel1.setText("Order:");

        jButtonRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeSelf();
            }
        });

        this.jTextFieldOrder.addFocusListener(new java.awt.event.FocusAdapter() {
            /**
             * Zavola na panelu s obrazky preusporadani obrazku v momente, kdy
             * radek s poradim uzivatel opusti.
             *
             * @param evt Nepouzity event.
             */
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                // Protoze v linked listu jsou ulozeny odkazy na instance panelu
                // s obrazky a ne kopie. Pokud se zde zmeni obsah radku s 
                // poradim, zmeni se i v listu a staci ho jen preusporadat
                // a znovu vsechny obrazky vykreslit.
                if (checkOrderField()) {
                    rightPanelContainer.updateImages();
                }
            }
        });

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

        jTextFieldOrder.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkOrderField();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                this.insertUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                this.insertUpdate(e);
            }
        });

        this.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabelMainPhoto, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonRemove))
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTextFieldOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jTextFieldMainAlt))
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTextFieldMainFigcaption))))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabelMainPhoto, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jButtonRemove))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel2)
                                .addComponent(jTextFieldMainAlt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel3)
                                .addComponent(jTextFieldMainFigcaption, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(jTextFieldOrder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
    }
}
