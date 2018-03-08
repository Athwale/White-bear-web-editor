/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor;

import editor.swingComponents.RightSidePanelJPanel;
import editor.swingComponents.LeftSidePanelJPanel;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import org.jsoup.nodes.Document;

/**
 * Prenoska pro html soubory naparsovane v DirectoryParser. Kazdy dokument
 * uchovava informaci o tom, zda v nem byla provedena zmena. Podle toho se bude
 * nebo nebude ukladat. Dale obsahuje samotny naparsovany html soubor pristupny
 * jako Document a vlastni instanci DefaultStyledDocument s pridanymi styly
 * pisma, ktera se zobrazuje v textPane v Gui a do niz se provadeji zmeny. Z
 * DefaultStyledDocument se html vytvari vlastni specialni tridou.
 *
 * @author Ondřej Mejzlík
 */
public class HTMLFile {

    private boolean isModified = false;
    private Document file;
    private DefaultStyledDocument styledDocument;
    private String articleMainHeading;
    private String keywords;
    private String description;
    private String date;
    private LeftSidePanelJPanel leftPanel;
    private RightSidePanelJPanel rightPanel;
    private final JTextPane textPane;

    /**
     * Konstruktor naparsovaneho html souboru. Vlozi predane udaje do promennych
     * a vyrobi novy stylovany dokument se styly pro tento soubor.
     *
     * @param file Naparsovany soubor od jsoup.
     * @param state true pokud je soubor zmenen, defaultne false.
     */
    public HTMLFile(Document file, boolean state) {
        this.file = file;
        this.isModified = state;
        this.styledDocument = this.makeStyledDocument();
        this.textPane = new JTextPane();
        this.textPane.setStyledDocument(this.styledDocument);
    }

    /**
     * Vymaze obsah promennych nastavovanych tridou PrepareDocument v teto
     * instanci. Vymazani promennych je pouzito pri kazdem prepracovani obsahu
     * souboru, kdyz je dany html soubor predan PrepareDocument na vygenerovani
     * obsahu GUI.
     */
    public void clear() {
        try {
            // Dokument je pri kazdem otevreni souboru potreba smazat a znovu zobrazit, tak se promitnou vsechny zmeny v html.
            this.styledDocument.remove(0, this.styledDocument.getLength());
        } catch (BadLocationException ex) {
            this.styledDocument = this.makeStyledDocument();
        }
        this.articleMainHeading = "";
        this.date = "";
        this.description = "";
        this.keywords = "";
    }

    /**
     * Zjisti jestli byl dokument zmenen.
     *
     * @return true pokud ano, false jinak.
     */
    public boolean isModified() {
        return this.isModified;
    }

    /**
     * Nastavi predany naparsovany html dokument do promenne instance.
     *
     * @param newFile novy dokument naparsovant jsoupem.
     */
    public void setHTMLDocument(Document newFile) {
        this.file = newFile;
    }

    /**
     * Nastavuje stylovany dokument tohoto souboru.
     *
     * @param doc novy stylovany dokument.
     */
    public void setStyledDocument(DefaultStyledDocument doc) {
        this.styledDocument = doc;
    }

    /**
     * Nastavuje zda byl soubor zmenen.
     *
     * @param value true pokud byl, false jinak.
     */
    public void setModified(boolean value) {
        this.isModified = value;
    }

    /**
     * Nastavuje description tohoto html file.
     *
     * @param description Obsah tagu meta description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Nastavuje keywords tohoto html file.
     *
     * @param keywords Obsah tagu meta keywords.
     */
    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    /**
     * Nastavuje datum tohoto html file.
     *
     * @param date Datum vytvoreni clanku.
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Nastavuje hlavni h2 nadpis clanku.
     *
     * @param heading nadpis clanku z tagu h2.
     */
    public void setMainHeading(String heading) {
        this.articleMainHeading = heading;
    }

    /**
     * Nastavuje obsah leveho panelu pro tento dokument. Levy panel obsahuje
     * informace o hlavnim obrazku dokumentu a jeho popisech.
     *
     * @param panel Obsah leveho panelu pro tento dokument.
     */
    public void setLeftPanel(LeftSidePanelJPanel panel) {
        this.leftPanel = panel;
    }

    /**
     * Nastavuje obsah pravy panel pro tento dokument. Pravy panel obsahuje
     * seznam postranich obrazku aside.
     *
     * @param panel Pravy panel pro tento dokument.
     */
    public void setRightPanel(RightSidePanelJPanel panel) {
        this.rightPanel = panel;
    }

    // Getry
    /**
     * Vraci pravy panel tohoto dokumentu.
     *
     * @return Pravy panel tohoto dokumentu.
     */
    public RightSidePanelJPanel getRightPanel() {
        return this.rightPanel;
    }

    /**
     * Vraci hlavni nadpis clanku (obsah h2).
     *
     * @return Hlavni nadpis clanku.
     */
    public String getMainHeading() {
        return this.articleMainHeading;
    }

    /**
     * Vraci levy panel tohoto dokumentu. Levy panel obsahuje informace o
     * hlavnim obrazku dokumentu a jeho popisech.
     *
     * @return Obsah levy panel tohoto dokumentu.
     */
    public LeftSidePanelJPanel getLeftPanel() {
        return this.leftPanel;
    }

    /**
     * Vraci datum tohoto html file.
     *
     * @return Datum vytvoreni clanku.
     */
    public String getDate() {
        return this.date;
    }

    /**
     * Vraci keywords tohoto html file.
     *
     * @return Obsah tagu keywords.
     */
    public String getKeywords() {
        return this.keywords;
    }

    /**
     * Vraci description tohoto html file.
     *
     * @return Obsah tagu description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Vraci naparsovany dokument ulozeny v tomto objektu.
     *
     * @return naparsovany html dokument.
     */
    public Document getHTMLDocument() {
        return this.file;
    }

    @Override
    public String toString() {
        return this.file.location() + " Modified: " + this.isModified;
    }

    /**
     * Vraci TextPane tohoto dokumentu.
     *
     * @return TextPane tohoto dokumentu.
     */
    public JTextPane getTextPane() {
        return this.textPane;
    }

    /**
     * Vytvori novy prazdny stylovany dokument a prida mu vsechny potrebne
     * styly.
     *
     * @return Vytvoreny prazdny dokument se styly.
     */
    private DefaultStyledDocument makeStyledDocument() {
        return StyledDocumentMaker.createStyledDocument();
    }
}
