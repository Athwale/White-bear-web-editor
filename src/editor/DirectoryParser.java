/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor;

import java.io.File;
import java.io.IOException;
import java.util.TreeMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Ukolem tridy je projit seznam souboru v adresari, vybrat vsechny HTML5
 * soubory, zvalidovat je a vytvorit seznam souboru patricich k webu white-bear.
 * DirectoryParser je jedinacek. Nelze vytvorit jeho instanci.
 *
 * @author Ondřej Mejzlík
 */
public class DirectoryParser {

    private final TreeMap<String, HTMLFile> parsedFiles;
    private static final String HEADING_TEST_STRING = "White bear elektronika, chemie, pokusy, hammer, medvědi";
    private static final DirectoryParser INSTANCE = new DirectoryParser();

    /**
     * Bezparametricky konstruktor. Inicializuje kontejnery na soubory.
     */
    private DirectoryParser() {
        this.parsedFiles = new TreeMap<>();
    }

    /**
     * Tovarni metoda vraci odkaz na jedinou instanci DirectoryParser.
     *
     * @return odkaz na tuto instanci.
     */
    public static DirectoryParser getParser() {
        return DirectoryParser.INSTANCE;
    }

    /**
     * Vrati odkaz na mapu naparsovanych souboru. Mapa je prazdna, pokud nebyla
     * zavolana metoda parseDirectory.
     *
     * @return Mapa naparsovanych souboru tridena abecedne podle klicu.
     */
    public TreeMap<String, HTMLFile> getParsedFiles() {
        return this.parsedFiles;
    }

    /**
     * Overi jestli je predany naparsovany html dokument clanek webu white-bear.
     * Overeni se provadi tak, ze clanek webu ma pevne danou strukturu. V
     * jedinem nadpisu h1 na strance musi byt definovany text (v konstantni
     * promenne tridy), Ve strance musi byt jeden vyskyt elementu article, tento
     * element musi mit jeden atribut id s obsahem main. Ve strance musi byt
     * jeden element s tridou mainText.
     *
     * @param parsed naparsovany html soubor podezrely z clanku webu
     * @return true, pokud je soubor clanek webu, jinak false.
     */
    private boolean isWhiteBearArticle(Document parsed) {
        boolean isCorrect = false;
        Element heading = parsed.getElementById("heading");
        if (heading == null) {
            return false;
        }
        if (heading.text().equals(DirectoryParser.HEADING_TEST_STRING)) {
            isCorrect = true;
        }
        Elements selected = parsed.getElementsByTag("article");
        if (selected.size() != 1) {
            return false;
        } else {
            Attributes articleAttributes = selected.first().attributes();
            if (articleAttributes.size() > 1) {
                return false;
            }
            if (articleAttributes.asList().contains(new Attribute("id", "main"))) {
                isCorrect = true;
            }
        }
        selected = parsed.getElementsByClass("mainText");
        if (selected.size() != 1) {
            return false;
        }
        return isCorrect;
    }

    /**
     * Overi jestli je predany naparsovany html dokument menu webu white-bear.
     * Overeni se provadi tak, ze clanek webu ma pevne danou strukturu. V
     * jedinem nadpisu h1 na strance musi byt definovany text (v konstantni
     * promenne tridy). Ve strance je jeden vyskyt elementu article, ten ma dva
     * atributy id s obsahem main a class s obsahem wholePage. Ve strance je
     * jeden element s id sixItems.
     *
     * @param parsed naparsovany html soubor podezrely z menu webu.
     * @return true, pokud je soubor menu webu, jinak false.
     */
    private boolean isMenuPage(Document parsed) {
        boolean isCorrect = false;
        Element heading = parsed.getElementById("heading");
        if (heading == null) {
            return false;
        }
        if (heading.text().equals(DirectoryParser.HEADING_TEST_STRING)) {
            isCorrect = true;
        }
        Elements selected = parsed.getElementsByTag("article");
        if (selected.size() != 1) {
            return false;
        } else {
            Attributes articleAttributes = selected.first().attributes();
            if (articleAttributes.size() != 2) {
                return false;
            }
            if (articleAttributes.asList().contains(new Attribute("id", "main"))) {
                if ((articleAttributes.asList().contains(new Attribute("class", "wholePage")))) {
                    isCorrect = true;
                }
            }
        }
        Element menu = parsed.getElementById("sixItems");
        if (menu == null) {
            return false;
        }
        return isCorrect;
    }

    /**
     * Projde vsechny soubory v predanem adresari a pokusi se je naparsovat.
     * Pokud je soubor ke cteni i k zapisu a konci koncovkou .html, spusti se
     * parsovani a vysledny dokument se ulozi do objektu HTMLFile s tim, ze
     * nebyl modifikovan. Nakonec se vysledny objekt vlozi do mapy pod jmenem
     * souboru. Pred kazdym parsovanim noveho adresare se stavajici seznamy
     * souboru vymazou.
     *
     * @param directory Adresar, ktery se bude prohledava.
     * @throws IOException V pripade, ze soubor nema kodovani utf-8
     */
    public void parseDirectory(File directory) throws IOException {
        this.parsedFiles.clear();
        File[] contents = directory.listFiles();
        for (File file : contents) {
            if (file.isFile() && file.canRead() && file.canWrite()) {
                String fileName = file.getPath();
                if (fileName.endsWith(".html")) {
                    Document parsed;
                    parsed = Jsoup.parse(file, "UTF-8");
                    if (this.isWhiteBearArticle(parsed)) {
                        HTMLFile newParsedFile = new HTMLFile(parsed, false);
                        String name = file.getName();
                        this.parsedFiles.put(name, newParsedFile);
                    } else if (this.isMenuPage(parsed)) {
                        //HTMLFile newParsedFile = new HTMLFile(parsed, false);
                        //String name = file.getName();
                        //this.parsedFiles.put(name, newParsedFile);
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return this.hashCode() + "\n"
                + this.parsedFiles.keySet() + "\n"
                + this.parsedFiles.size();
    }
}
