/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.swingComponents;

import editor.Gui;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.TextAttribute;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 * Trida reprezentujici html odkaz. Vytvari modry podtrzeny jLabel, ktery v sobe
 * uchovava String URL odkazu a po kliknuti mysi umoznuje v dialogovem okne
 * zmenit URL a text odkazu.
 *
 * @author Ondřej Mejzlík
 */
public final class LinkJLabel extends javax.swing.JLabel {

    private int position;
    private String href;
    private String text;
    // JFrame se zobrazuje na taskbaru, JDialog ne.
    private JFrame jFrameLinkProperties;
    private Gui parentFrame;
    private JTextField jTextFieldURL;
    private JTextField jTextFieldText;

    /**
     * Konstruktor vytvari novy link s nazvem Empty Link a zacatkem adresy
     * http:// na zadane pozici.
     *
     * @param parentFrame JFrame ze ktereho se s timto odkazem pracuje. Slouzi k
     * deaktivaci hlavniho okna v pripade, ze je otevrene nejake editacni okno.
     * @param pos Pozice linku v dokumentu.
     */
    public LinkJLabel(Gui parentFrame, int pos) {
        this(parentFrame, "http://", "Empty Link", pos);
    }

    /**
     * Vytvari novy link a nastavuje jeho obsah. Obsah je zkontrolovan a pokud
     * je nekorektni, je misto obrazku vlozen chybovy text.
     *
     * @param parentFrame JFrame ze ktereho se s timto odkazem pracuje. Slouzi k
     * deaktivaci hlavniho okna v pripade, ze je otevrene nejake editacni okno.
     * @param href hypertextovy odkaz.
     * @param text text odkazu.
     * @param position pozice linku v textu.
     */
    public LinkJLabel(Gui parentFrame, String href, String text, int position) {
        super();
        this.parentFrame = parentFrame;
        this.position = position;
        this.href = href;
        // Volani private metody s nastavujicim kodem predchazi chybe volani
        // prekrytelne metody v konstruktoru. 
        this.makeLinkDialog();
        // SetUpThiLabel nevolame pokazde pri zpracovani novych dat, protoze
        // by pokazde pridal novy objekt mouse listeneru a zabiral pamet.
        this.setUpThisLabel();
        if (!(this.processData(href, text, position))) {
            this.displayError();
        }
    }

    /**
     * Zobrazi misto odkazu chybovou hlasku, ze se vlozeni nezdarilo. K chybe
     * muze dojit, pokud konstruktor dostane nepovolene parametry primo ze
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
        // Proc je zde potreba ulozit do promenne text a nefunguje setText()?
        this.text = "Error inserting link";
    }

    /**
     * Nastavi teto jLabel reprezentujici odkaz jeji modry podtrzeny font,
     * posune ji mirne nahoru, aby byla zarovnana s textem a nastavi jeji text.
     */
    @SuppressWarnings("unchecked")
    private void setUpThisLabel() {
        this.setForeground(new Color(0, 0, 255));
        Font font = new Font("Dialog", 0, 12);
        Map attributes = font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        this.setFont(font.deriveFont(attributes));

        // Pri vytvoreni link labelu se mu prida i listener na kliknuti mysi.
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                /**
                 * Obsluhuje kliknuti na label odkazu. Zobrazi dialogove okno s
                 * obsahem odkazu a deaktivuje hlavni okno editoru.
                 *
                 * @param evt Event predany od mysi. Neni pouzit.
                 */
                jFrameLinkProperties.setVisible(true);
                parentFrame.setEnabled(false);
            }
        });
        // Posunuti aby byl label zarovnany s textem.
        this.setAlignmentY(0.85f);
    }

    /**
     * Metoda se spousti z konstruktoru a pri zmene udaju v dialogovem okne.
     * Zkontroluje udaje, ktere se maji odkazu priradit a pokud jsou vsechny
     * spravne, jsou vlozeny do promennych instance a nastaveny do odkazu.
     *
     * @param href text url odkazu
     * @param text text odkazu
     * @param position pozice linku v textu
     * @return True pokud vsechny overeni projdou false jinak.
     */
    private boolean processData(String href, String text, int position) {
        if (this.checkURLFormat(href)) {
            if (this.checkPosition(position)) {
                if (this.checkText(text)) {
                    this.href = href;
                    this.text = text;
                    this.position = position;
                    this.jTextFieldURL.setText(this.href);
                    this.jTextFieldText.setText(this.text);
                    this.setText(this.text);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Zkontroluje jestli URL zadane k odkazu ma formu spravne adresy. Pokud
     * nema, tudiz nezacina na http/s://, potom je vyhozeno varovne okno a
     * metoda vraci false.
     *
     * @param href Text odkazu.
     * @return True pokud je odkaz spravne, false jinak.
     */
    private boolean checkURLFormat(String href) {
        if (!(href.startsWith("http://") || href.startsWith("https://") || href.startsWith("files"))) {
            if (!(href.endsWith(".html"))) {
                JOptionPane.showMessageDialog(null, "Wrong URL: " + href, "Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        return true;
    }

    /**
     * Zkontroluje jestli pozice odkazu je vetsi nez 0. Pokud neni je vyhozeno
     * chybove okno se zavaznou chybou. Pozici odkazu by uzivatel nemel mit
     * moznos ovlivnit do zapornych cisel a metoda vraci false.
     *
     * @param position cislo pozice.
     * @return True pokud je odkaz spravne, false jinak.
     */
    private boolean checkPosition(int position) {
        if (position < 0) {
            JOptionPane.showMessageDialog(null, "Serious error, link position negative", "Internal Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Zkontroluje jestli text k odkazu neni prazdny. Pokud je prazdny je
     * vyhozeno varovne okno a metoda vraci false.
     *
     * @param text Text k odkazu.
     * @return True pokud je odkaz spravne, false jinak.
     */
    private boolean checkText(String text) {
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Link text must not be empty", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Obsluhuje kliknuti na tlacitko OK v dialogovem okne. Spousti metodu
     * AcceptChanges, ktera ulozi nastaveni odkazu.
     *
     * @param evt Event predany od mysi. Neni pouzit.
     */
    private void jButtonLinkOkActionPerformed(ActionEvent evt) {
        this.acceptChanges();
    }

    /**
     * Obsluhuje kliknuti na tlacitko Storno v dialogovem okne. Spousti metodu
     * DiscardChanges, ktera schova dialogove okno a ignoruje zmeny.
     *
     * @param evt Event predany od mysi. Neni pouzit.
     */
    private void jButtonLinkStornoActionPerformed(ActionEvent evt) {
        this.discardChanges();
    }

    /**
     * Obsluhuje stisk klavesy v radcich dialogoveho okna. Pokud je klavesa
     * Enter okno se zavre a data ulozi. Pokud je escape, okno se zavre a zmeny
     * se ignoruji.
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
     * Uklada obsah dialogoveho okna do tohoto odkazu.
     */
    private void acceptChanges() {
        String newHref = this.jTextFieldURL.getText().trim();
        String newText = this.jTextFieldText.getText().trim();
        // Kontrola na Empty Link se provadi zde a ne v process data, protoze
        // odkaz musi jit vlozit z konstruktoru s empty obsahem.
        if (newText.contains("Empty Link")) {
            JOptionPane.showMessageDialog(null, "Insert a meaningful link text.", "Error", JOptionPane.WARNING_MESSAGE);
        } else if (newHref.equals("http://")) {
            JOptionPane.showMessageDialog(null, "Insert a correct link.", "Error", JOptionPane.WARNING_MESSAGE);
        } else if (this.processData(newHref, newText, this.position)) {
            this.jFrameLinkProperties.setVisible(false);
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
     * Zavre dialogove okno nastaveni odkazu a ignoruje zmeny.
     */
    private void discardChanges() {
        this.jTextFieldURL.setText(this.href);
        this.jTextFieldText.setText(this.text);
        this.jFrameLinkProperties.setVisible(false);
        this.enableParentFrame();
    }

    /**
     * Vraci URL z tohoto odkazu.
     *
     * @return URL odkazu.
     */
    public String getURL() {
        return this.href;
    }

    /**
     * Vraci text z tohoto odkazu.
     *
     * @return Text odkazu.
     */
    @Override
    public String getText() {
        return this.text;
    }

    /**
     * Nastavuje poziti tohoto odkazu v dokumentu.
     *
     * @param pos Nova pozice.
     */
    public void setPosition(int pos) {
        this.position = pos;
    }

    /**
     * Vraci pozici tohoto odkazu v dokumentu.
     *
     * @return Pozice odkazu v dokumentu.
     */
    public int getPosition() {
        return this.position;
    }

    /**
     * Vyrobi pro tento odkaz dialogove okno, kterym se budou provadet zmeny
     * odkazu. Vysledek ulozi do promenne tridy jFrameLinkProperties pro
     * pozdejsi pristup.
     */
    private void makeLinkDialog() {
        // Inicializace dialogu pro tento link. 
        this.jFrameLinkProperties = new javax.swing.JFrame();
        this.jTextFieldURL = new javax.swing.JTextField();
        this.jTextFieldText = new javax.swing.JTextField();
        JLabel jLabelURL = new javax.swing.JLabel();
        JLabel jLabelText = new javax.swing.JLabel();
        JButton jButtonLinkOk = new javax.swing.JButton();
        JButton jButtonLinkStorno = new javax.swing.JButton();

        this.jFrameLinkProperties.setAlwaysOnTop(true);
        this.jFrameLinkProperties.setTitle("Edit link");
        this.jFrameLinkProperties.setResizable(false);

        jLabelURL.setText("URL:");
        jLabelText.setText("Text:");
        jButtonLinkOk.setText("Ok");
        jButtonLinkStorno.setText("Storno");

        this.jTextFieldURL.setText(this.href);
        this.jTextFieldText.setText(this.getText());

        jButtonLinkOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLinkOkActionPerformed(evt);
            }
        });

        jButtonLinkStorno.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLinkStornoActionPerformed(evt);
            }
        });

        jTextFieldURL.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldKeyPressed(evt);
            }
        });

        jTextFieldText.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldKeyPressed(evt);
            }
        });

        this.jFrameLinkProperties.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                discardChanges();
            }
        });

        javax.swing.GroupLayout jFrameLinkPropertiesLayout = new javax.swing.GroupLayout(jFrameLinkProperties.getContentPane());
        jFrameLinkProperties.getContentPane().setLayout(jFrameLinkPropertiesLayout);
        jFrameLinkPropertiesLayout.setHorizontalGroup(
                jFrameLinkPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jFrameLinkPropertiesLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jFrameLinkPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jTextFieldURL)
                                .addComponent(jTextFieldText, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jFrameLinkPropertiesLayout.createSequentialGroup()
                                        .addGroup(jFrameLinkPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jLabelURL)
                                                .addComponent(jLabelText))
                                        .addGap(0, 0, Short.MAX_VALUE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jFrameLinkPropertiesLayout.createSequentialGroup()
                                        .addGap(0, 206, Short.MAX_VALUE)
                                        .addComponent(jButtonLinkOk, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButtonLinkStorno)))
                        .addContainerGap())
        );
        jFrameLinkPropertiesLayout.setVerticalGroup(
                jFrameLinkPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jFrameLinkPropertiesLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabelURL)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldURL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabelText)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jFrameLinkPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jButtonLinkStorno)
                                .addComponent(jButtonLinkOk))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        this.jFrameLinkProperties.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        this.jFrameLinkProperties.setLocationRelativeTo(null);
        this.jFrameLinkProperties.pack();
    }
}
