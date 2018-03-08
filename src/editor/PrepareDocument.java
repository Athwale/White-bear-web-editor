/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor;

import editor.swingComponents.VideoJLabel;
import editor.swingComponents.ListItemJPanel;
import editor.swingComponents.PictureJLabel;
import editor.swingComponents.ListJPanel;
import editor.swingComponents.RightSidePanelJPanel;
import editor.swingComponents.LeftSidePanelJPanel;
import editor.swingComponents.RightPanelImageJPanel;
import editor.swingComponents.LinkJLabel;
import java.awt.Component;
import java.util.LinkedList;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Ondřej Mejzlík
 */
public class PrepareDocument {

    // Jedinou instanci teto tridy zalozime zde. Nelze vytvorit dalsi instanci
    // pouze ziskad odkaz na tuto. Dalsi instance stejne neni potreba.
    private static final PrepareDocument INSTANCE = new PrepareDocument();
    private final String NBSP = "&nbsp;";
    private final String IN_TEXT = "inText";
    private final LinkedList<String> styles;
    private final LinkedList<String> tagEnds;
    private final LinkedList<LinkJLabel> linkList;
    private String currentTag;
    private Gui parentFrame;

    /**
     * Konstruktor umisti do seznamu naposledy pouzitych stylu defaultni cernou
     * barvu.
     */
    private PrepareDocument() {
        this.tagEnds = new LinkedList<>();
        this.styles = new LinkedList<>();
        this.linkList = new LinkedList<>();
        this.styles.push("black");
    }

    /**
     * Tovarni metoda vraci odkaz na jedinou instanci PrepareDocument.
     *
     * @return odkaz na tuto instanci.
     */
    public static PrepareDocument getInstance() {
        return PrepareDocument.INSTANCE;
    }

    /**
     * Vymaze obsah ridicich promennych instance. Pokazde, kdyz je pouzita
     * instance PrepareDocument, na novy dokument a proto musi byt vynulovany
     * jeji ridici promenne.
     */
    private void clear() {
        this.tagEnds.clear();
        this.styles.clear();
        this.linkList.clear();
        this.styles.push("black");
        this.currentTag = "";
    }

    /**
     * Zpracuje predanou prenosku HTML souboru. Do JTextPane souboru vlozi text,
     * obrazky, videa, seznamy a odkazy. Vyplni informace o souboru HTML
     * description, keywords, datum a nadpis clanku. Ulozi do prenosky hlavni
     * obrazek a jeho figcaption a alt. Ulozi do prenosky seznam postranich
     * obrazku.
     *
     * @param inputFile HTML prenoska obsahujici naparsovany HTML soubor.
     * @param directory Pracovni adresar s webem white-bear.
     * @param parentFrame JFrame ze ktereho se s timto objektem pracuje. Slouzi
     * k deaktivaci hlavniho okna v pripade, ze je otevrene nejake editacni
     * okno.
     * @return Naplnena HTMLFile prenoska.
     * @throws ConversionException V pripade, ze pri zpracovavani souboru dojde
     * k chybe.
     */
    public HTMLFile ProcessFile(HTMLFile inputFile, String directory, Gui parentFrame) throws ConversionException {
        // Pri kazdem zavolani na novy dokument se musi stavajici obsah ridicich promennych vymazat.
        this.clear();
        this.parentFrame = parentFrame;
        inputFile.clear();
        JTextPane textPane = inputFile.getTextPane();
        Document openHtml = inputFile.getHTMLDocument();

        // Vybere cast html obsahujici hlavni textovy obsah.
        // Div s class mainText je ve strance jen jeden.
        Elements classMainText = openHtml.getElementsByClass("mainText");
        Elements children = classMainText.first().children();

        // Rozdeluje html na nejvyssi urovni na jednotlive useky tesne pod 
        // mainText.
        try {
            for (Element node : children) {
                String name = node.tagName();
                switch (name) {
                    case "h3":
                        this.processH3(node, textPane);
                        break;
                    case "p":
                        this.processText(node, textPane);
                        break;
                    case "ol":
                        this.processList(node, textPane, "numbered");
                        break;
                    case "ul":
                        this.processList(node, textPane, "notNumbered");
                        break;
                    case "div":
                        // Vklada obrazky a flash youtube video
                        this.processDiv(node, textPane, directory);
                        break;
                }
            }
        } catch (BadLocationException ex) {
            throw new ConversionException("Error while inserting text into text pane. Bad location.");
        }
        this.fillDescriptionHeadingKeywordsDate(inputFile);
        this.makeLeftPanel(inputFile, directory);
        this.makeRightPanel(inputFile, directory);
        return inputFile;
    }

    /**
     * Vlozi do predaneho JTextPane predany komponent. Komponent se vlozi na
     * konec.
     *
     * @param textPane Panel do ktereho vkladat.
     * @param component Komponent ktery vkladat.
     */
    private void insertComponentToPane(JTextPane textPane, Component component) {
        StyledDocument doc = textPane.getStyledDocument();
        textPane.setCaretPosition(doc.getLength());
        textPane.insertComponent(component);
    }

    /**
     * Vlozi do predaneho JTextPane zpracpvany HTML seznam ve forme ListJPanelu.
     * Podle typu se polozky seznamu bud cisluji, nebo necisluji. Polozky
     * seznamu jsou spracovany stejnou metodou, jako odstavec p, proto mohou
     * obsahovat formatovant text a odkazy.
     *
     * @param node Noda ul nebo ol
     * @param textPane Pane do ktereho se seznam vlozi
     * @param type Typ jestli se cisluje nebo necusluje ("numbered",
     * "notNumbered")
     * @throws BadLocationException V pripade, ze se nezdari vlozit text do
     * TextPane polozek.
     * @throws ConversionException V pripade, ze dojde k chybe pri prevodu textu
     * v metode processText.
     */
    private void processList(Element node, JTextPane textPane, String type) throws BadLocationException, ConversionException {
        StyledDocument doc = textPane.getStyledDocument();
        ListJPanel newList = new ListJPanel(type);
        Elements htmlListItems = node.children();
        for (Element listItem : htmlListItems) {
            ListItemJPanel newItem = new ListItemJPanel(newList);
            JTextPane listItemTextPane = newItem.getTextPane();
            try {
                this.processText(listItem, listItemTextPane);
            } catch (ConversionException ex) {
                throw new ConversionException("A href is not first attribute in an <a> tag in a list item.");
            }
            newList.addItem(newItem);
        }
        if (type.equals("numbered")) {
            newList.renumber();
        }
        this.insertComponentToPane(textPane, newList);
        doc.insertString(doc.getLength(), "\n", doc.getStyle(this.styles.peek()));
    }

    /**
     * Vlozi do dokumentu obrazek nebo video. Za vlozenym objektem odradkuje.
     *
     * @param div Element div, ktery se zpracovava.
     * @param doc Dokument, do ktereho se objekt vklada.
     * @throws ConversionException Pokud je poradi elementu v div spatne.
     */
    private void processDiv(Element div, JTextPane textPane, String directory) throws ConversionException, BadLocationException {
        // Pokud zaciname divem s class inText, je to obrazek.
        StyledDocument doc = textPane.getStyledDocument();
        try {
            if (div.hasClass(this.IN_TEXT)) {
                Element centerDiv = div.child(0);
                Element link = centerDiv.child(0);
                Element image = link.child(0);
                if (!(centerDiv.hasClass("center"))) {
                    throw new ConversionException("An image has no div with class center.");
                }
                if (!(link.tagName().equals("a"))) {
                    throw new ConversionException("An image div block has wrong order of elements. <a> has to be first.");
                }
                if (!(image.tagName().equals("img"))) {
                    throw new ConversionException("An image div block has wrong order of elements. <img> has to be second.");
                }
                // Velikost obrazku se nepredava, vezme se z obrazku na ktery ukazuje src. Protoze obrazek ve strance
                // je stejne velky jako ten na disku. Link odkazu na originalni obrazek se generuje automaticky z thumbnailu
                // a originalni soubor musi existovat.
                String thumbnail = directory + "/" + image.attr("src");
                String alt = image.attr("alt");
                PictureJLabel picture = new PictureJLabel(parentFrame, thumbnail, alt);
                this.insertComponentToPane(textPane, picture);
            } else {
                // Pokud div neobsahuje obrazek, obsahuje video, jina moznost neni povolena.
                Element iframe = div.child(0);
                if (!(div.hasClass("center"))) {
                    throw new ConversionException("A div inside text is not centered. This is not allowed.");
                }
                if (!(iframe.tagName().equals("iframe"))) {
                    throw new ConversionException("A div does not contain image or video. Or tagname is wrong. This is not allowed.");
                }
                String title = iframe.attr("title");
                String src = iframe.attr("src");
                VideoJLabel video = new VideoJLabel(parentFrame, title, src);
                this.insertComponentToPane(textPane, video);
            }
            doc.insertString(doc.getLength(), "\n", doc.getStyle(this.styles.peek()));
        } catch (IndexOutOfBoundsException ex) {
            throw new ConversionException("A div inside text has forbidden structure.");
        }
    }

    /**
     * Vlozi do dokumentu nadpis h3.
     *
     * @param h3 Nadpis k vlozeni.
     * @param doc Dokument, do ktereho se nadpis vklada.
     * @throws BadLocationException Pokud dojde k chybe pri vkladani.
     */
    private void processH3(Element h3, JTextPane textPane) throws ConversionException, BadLocationException {
        StyledDocument doc = textPane.getStyledDocument();
        String heading = h3.text();
        String color = "blackHeading3";
        int end = h3.toString().indexOf(">", 0) + 1;
        this.currentTag = h3.toString().substring(0, end);
        if (this.currentTag.contains("class")) {
            color = this.identifyColor() + "Heading";
        }
        AttributeSet style = doc.getStyle(color);
        doc.insertString(doc.getLength(), heading, style);
        style = doc.getStyle(this.styles.peek());
        doc.insertString(doc.getLength(), "\n", style);
    }

    /**
     * Zpracovava obsah textoveho tagu, vklada text do dokumentu panelu textPane
     * podle stylu, ktery ziska rozebiranim obsahu odstavce. Vlozi do prave
     * zpracovavaneho odstavc i objekty odkazu. Odstavec ukonci prazdnym radkem.
     *
     * @param e Textovy element ke zpracovani.
     * @param textPane JTextPane do ktereho se zpracovany text vlozi.
     * @throws BadLocationException V pripade ze nastane chyba pri vkladani
     * textu
     * @throws ConversionException V pripade ze nastane chyba pri prevodu textu.
     */
    private void processText(Element e, JTextPane textPane) throws BadLocationException, ConversionException {
        StyledDocument doc = textPane.getStyledDocument();
        String contents = e.html();
        // Nahrazeni nedelitelnych mezer normalnimi, v editoru je to jedno.
        contents = contents.replaceAll(NBSP, " ");
        // Nahradit <br> s mezerou za normalni <br> mezera za br vznika v html protoze prirozene za slovem je mezera,
        // nicmene <br> odradkuje a mezera neni potrebna.
        contents = contents.replaceAll("<br> ", "<br>");
        int pLenght = contents.length();
        // Projdeme cely obsah odstavce.
        for (int i = 0; i < pLenght; i++) {
            if (contents.charAt(i) == '<') {
                String tag = this.identifyTag(contents, i);
                switch (tag) {
                    case "strong":
                        this.setStrongStyle();
                        break;
                    case "/strong":
                        this.removeStrongStyle();
                        break;
                    case "span":
                        this.setSpanStyleColor();
                        break;
                    case "/span":
                        this.unsetSpanStyleColor();
                        break;
                    case "br":
                        // Na stylu nezalezi,
                        doc.insertString(doc.getLength(), "\n", doc.getStyle(this.styles.peek()));
                        break;
                    case "a":
                        // kdyz se dorazi na tag, ktery ma byt reprezentovan 
                        // grafickym objektem, bude jeho pozice ulozena a objekt
                        // bude vlozen pozdeji do textpane.
                        // Delku textu odkazu je nutno preskocit, protoze odkaz bude vlozen jako objekt pozdeji.
                        i += this.processA(doc.getLength(), i, contents);
                        break;
                    case "/a":
                        this.popATag();
                        break;
                    default:
                        System.err.println("default");
                        break;
                }
                // Dojdeme li k nejakemu tagu, musime ho preskocit.
                // Potom pokracujeme dalsi iteraci cyklu cimz se ziska dalsi znak.
                i += this.currentTag.length() - 1;
                continue;
            }
            int position = doc.getLength();
            String charToInsert = String.valueOf(contents.charAt(i));
            AttributeSet style = doc.getStyle(this.styles.peek());
            doc.insertString(position, charToInsert, style);
        }
        // Vlozeni odkazu do textu.
        if (!(this.linkList.isEmpty())) {
            this.insertLinks(textPane);
        }
        // Odstavec konci prazdnym radkem.
        doc.insertString(doc.getLength(), "\n\n", doc.getStyle(this.styles.peek()));
    }

    /**
     * Vlozi do textPane dokumentu odkazy na jejich mista z promenne linkList.
     *
     * @param pane TextPane do ktereho se odkazy vkladaji.
     */
    private void insertLinks(JTextPane textPane) {
        for (LinkJLabel link : this.linkList) {
            int position = link.getPosition();
            // Linky nelze vkladat pomoci metody insertComponentIntoPane
            // Protoze se musi vkladat na specificka mista a ne na konec.
            textPane.setCaretPosition(position);
            textPane.insertComponent(link);
        }
        // Po kazdem vlozeni linku do stavajiciho odstavce se musi seznam vymazat
        // a pripravit na dalsi odstavec.
        this.linkList.clear();
    }

    /**
     * Vytvori objekt odkazu a ulozi ho do promenne tridy, odkud bude posleze
     * vlozen do textPane.
     *
     * @return delka vnitrniho textu odkazu.
     */
    private int processA(int position, int start, String contents) throws ConversionException {
        // Ziskani hranic textu odkazu a nasledne samotneho textu.
        start += currentTag.length();
        int end = contents.indexOf("<", start);
        String linkText = contents.substring(start, end);
        // Ziskani obsahu href z tagu ulozeneho v promenne instance.
        String[] split = this.currentTag.split(" ");
        String href = split[1];
        if (!(href.startsWith("href="))) {
            throw new ConversionException("Href is not first attribute in <a>.");
        }
        String[] splitLink = href.split("\"");
        // Aktualizace pozice linku v textu o posunuti po vlozeni predchozich linku.
        // Link se chova jako jeden znak.
        position += this.linkList.size();
        LinkJLabel link = new LinkJLabel(parentFrame, splitLink[1], linkText, position);
        // Key je pozice v dokumentu. V jednom dokumentu nemohou byt dva linky
        // na stejne pozici.
        this.linkList.addLast(link);
        return linkText.length();
    }

    /**
     * Odstrani ze seznamu koncovych znacek koncovku k a. Pokud koncovka a neni
     * v hlave zasobniku, je v html chyba a je vyhozena vyjimka.
     *
     * @throws ConversionException Pokud v hlave zasobniku koncovych znacek neni
     * a.
     */
    private void popATag() throws ConversionException {
        if (!(this.tagEnds.pop().equals("</a>"))) {
            throw new ConversionException("Missing </a> endtag.");
        }
    }

    /*
     * Dostupne styly:
     * black  blackBold  blackHeading3
     * green  greenBold  redHeading    darkPinkHeading
     * red    redBold    greenHeading  pinkHeading
     * blue   blueBold   blueHeading   darkPurupleHeading
     * orange orangeBold greyHeading   orangeHeading     
     */
    /**
     * Nastavuje barvu pisma pro span do seznamu stylu. Uvnitr spanu mohou byt
     * jen barvy red, blue, green a orange. Ostatni barvy jsou vyhrazeny pro
     * nadpisy.
     */
    private void setSpanStyleColor() {
        // Identifikace barvy stylu podle class
        String color = this.identifyColor();
        String currentStyle = this.styles.peek();
        if (currentStyle.contains("Bold")) {
            this.styles.push(color + "Bold");
        } else {
            this.styles.push(color);
        }
    }

    /**
     * Pri precteni konecne znacky span, se prislusny posledni barevny styl
     * odebere.
     */
    private void unsetSpanStyleColor() throws ConversionException {
        if (!(this.tagEnds.pop().equals("</span>"))) {
            throw new ConversionException("Missing </span> endtag.");
        }
        this.styles.pop();
    }

    /**
     * Identifikuje barvu stylu v soucasnem tagu. Pokud jde o nadpis, pripona
     * heading se prida az v prislusne metode resici nadpisy.
     *
     * @return jmeno barvy stylu.
     */
    private String identifyColor() {
        if (this.currentTag.contains("red")) {
            return "red";
        }
        if (this.currentTag.contains("blue")) {
            return "blue";
        }
        if (this.currentTag.contains("orange")) {
            return "orange";
        }
        if (this.currentTag.contains("green")) {
            return "green";
        }
        if (this.currentTag.contains("pink0")) {
            return "pink";
        }
        if (this.currentTag.contains("pink1")) {
            return "darkPink";
        }
        if (this.currentTag.contains("darkPurple")) {
            return "darkPurple";
        } else {
            return "grey";
        }
    }

    /**
     * Ubere ze zasobniku posledne pouzitych stylu tucnou verzi soucasneho stylu
     * a zkontroluje ze ukoncujici znacka strong je v zasobniku.
     */
    private void removeStrongStyle() throws ConversionException {
        String lastStyle = this.styles.pop();
        if (!(lastStyle.contains("Bold"))) {
            throw new ConversionException("Nonexistent bold style removed.");
        }
        if (!(this.tagEnds.pop().equals("</strong>"))) {
            throw new ConversionException("Missing </strong> endtag.");
        }
    }

    /**
     * Prida do zasobniku posledne pouzitych stylu tucnou verzi soucasneho
     * stylu.
     */
    private void setStrongStyle() throws ConversionException {
        String currentStyle = this.styles.peek();
        if (!(currentStyle.contains("Bold"))) {
            this.styles.push(currentStyle + "Bold");
        }
        if (this.styles.peek().contains("Heading")) {
            throw new ConversionException("Bold style added to heading.");
        }
    }

    /**
     * Identifikuje o jaky tag se jedna a nastavi ho do promenne instance
     * currentTag. Do seznamu uzavirajicich tagu prida prislusny konecny tag,
     * ktery se bude muset najit. Vraci jmeno tagu.
     *
     * @param contents Html obsah tagu, uvnitr ktereho byl nalezen dalsi tag.
     * @param start Index kde zacina nalezeny tag mensitkem.
     * @return Jmeno nalezeneho tagu.
     */
    private String identifyTag(String contents, int start) {
        // Najde index kde konci pocatecni tag vetsitkem.
        int end = contents.indexOf(">", start) + 1;
        this.currentTag = contents.substring(start, end);

        // Najde kde konci jmeno tagu.
        // Start ukazuje na < proto + 1.
        int i = start + 1;
        while ((contents.charAt(i) != ' ') && (contents.charAt(i) != '>')) {
            char ch = contents.charAt(i);
            i++;
        }
        String tagName = contents.substring(start + 1, i);
        // pokud narazime na br, koncova znacka se nepridava, br ji nema.
        if (tagName.equals("br")) {
            return tagName;
        }
        // pokud pri identifikaci narazime na koncovou znacku, nepridava se k ni
        // do zasobniku dalsi koncova znacka.
        if (!(tagName.charAt(0) == '/')) {
            this.tagEnds.addFirst("</" + tagName + ">");
        }
        return tagName;
    }

    /**
     * Zpracuje tag figure a vypreparuje z nej odkaz na thumbnail obrazku, alt
     * popis obrazku a figcaption k obrazku. Toto vraci jako pole String o trech
     * prvcich.
     *
     * @param figure Figure element ke zpracovani.
     * @param directory Pracovni adresar, slouzi ke konstrukci umisteni obrazku.
     * @return Pole String o 3 prvcich [thumbnailLocation, photoAlt,
     * photoFigcaption]
     * @throws ConversionException V pripade ze pri zpracovani neni nejaka cast
     * obsahu figure vporadku.
     */
    private String[] processFigure(Element figure, String directory) throws ConversionException {
        String photoAlt;
        String photoFigcaption;
        String thumbnailLocation;
        Elements children = figure.children();
        if (children.isEmpty()) {
            throw new ConversionException("A figure element has no contents, but must have one <a><img><figcaption>.");
        } else if (children.size() > 2) {
            throw new ConversionException("A figure element must have only two child elements one <a> and one <figcaption> in this order.");
        } else {
            Element a = children.first();
            if (!(a.tagName().equals("a"))) {
                throw new ConversionException("First element in a figure block has to be <a>.");
            } else {
                Element figcaption = children.last();
                if (!(figcaption.tagName().equals("figcaption"))) {
                    throw new ConversionException("Second element in a figure block has to be <figcaption>.");
                } else {
                    Elements img = a.children();
                    if (img.isEmpty()) {
                        throw new ConversionException("Img inside a figure element has no image.");
                    } else if (img.size() > 1) {
                        throw new ConversionException("There must be only one img inside a figure element.");
                    } else {
                        photoAlt = img.first().attr("alt");
                        if (photoAlt.isEmpty()) {
                            throw new ConversionException("Img alt attribute in a figure is empty.");
                        }
                        thumbnailLocation = img.first().attr("src");
                        if (thumbnailLocation.isEmpty()) {
                            throw new ConversionException("Thumbnail img src attribute in a figure is empty.");
                        } else {
                            thumbnailLocation = directory + "/" + thumbnailLocation;
                            photoFigcaption = figcaption.text();
                            if (photoFigcaption.isEmpty()) {
                                throw new ConversionException("Figcaption in a figure is empty.");
                            } else {
                                String[] output = new String[3];
                                output[0] = thumbnailLocation;
                                output[1] = photoAlt;
                                output[2] = photoFigcaption;
                                return output;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Vytvori novy objekt LeftSidePanelJPanel z hlavniho obrazku predaneho
     * dokumentu a ulozi tento panel do HTMLFile prenosky zpracovavaneho
     * dokumentu.
     *
     * @param inputFile Zpracovavany dokument ve sve prenosce.
     * @param directory Pracovni adresar, slouzi ke konstrukci umisteni obrazku.
     * @throws ConversionException V pripade, ze nektery udaj potrebny k
     * vytvoreni v HTML souboru chybi, nebo je spatny.
     */
    private void makeLeftPanel(HTMLFile inputFile, String directory) throws ConversionException {
        Document document = inputFile.getHTMLDocument();
        Element articleImg = document.getElementById("articleImg");
        if (articleImg == null) {
            throw new ConversionException("Figure element with id=\"articleImg\" is missing.");
        } else {
            String[] output = this.processFigure(articleImg, directory);
            LeftSidePanelJPanel leftPanel = new LeftSidePanelJPanel(parentFrame, output[0], output[1], output[2], directory);
            inputFile.setLeftPanel(leftPanel);
        }
    }

    /**
     *
     * @param inputFile
     * @param directory
     * @throws ConversionException
     */
    private void makeRightPanel(HTMLFile inputFile, String directory) throws ConversionException {
        Document document = inputFile.getHTMLDocument();
        RightSidePanelJPanel rightPanel = new RightSidePanelJPanel();
        Elements aside = document.getElementsByTag("aside");
        if (aside.isEmpty()) {
            throw new ConversionException("<aside> tag missing.");
        } else if (aside.size() > 1) {
            throw new ConversionException("Only one <aside> tag is allowed.");
        } else {
            Elements figures = aside.first().children();
            if (!(figures.isEmpty())) {
                for (Element figure : figures) {
                    String[] output = this.processFigure(figure, directory);
                    RightPanelImageJPanel image = new RightPanelImageJPanel(parentFrame, rightPanel, output[0], output[1], output[2], directory);
                    rightPanel.addImage(image);
                }
            }
            // Pokud zadne obrazky nemame, ulozi se prazdny pravy panel, jinak
            // se ulozi ten naplneny.
            inputFile.setRightPanel(rightPanel);
        }
    }

    /**
     * Nastavi do prenosky zpracovavaneho html dokumentu jeho description,
     * hlavni nadpis clanku, keywords, udaje o hlavnim obrazku a datum, ktere
     * vybere z elementu html souboru.
     *
     * @param inputFile Prenoska HTML dokumentu.
     * @throws ConversionException v pripade, ze chybi nektery z pozadovanych
     * tagu, nebo je prazdny. HTML soubory, ktere tento program zpracovava
     * museji byt korektni.
     */
    private void fillDescriptionHeadingKeywordsDate(HTMLFile inputFile) throws ConversionException {
        Document document = inputFile.getHTMLDocument();
        Elements metaDescription = document.getElementsByAttributeValue("name", "description");
        Elements metaKeywords = document.getElementsByAttributeValue("name", "keywords");
        Elements h2title = document.getElementsByTag("h2");
        Element dateElement = document.getElementById("date");
        String description = metaDescription.attr("content");
        String keywords = metaKeywords.attr("content");
        // Pokud by program chybejici tag vytvoril, mohl by v souboru zustat i
        // puvodni spatny tag se skomolenym jmenem a html by nebyl validni.
        // Uzivatel musi chybu opravit rucne.
        if (description.isEmpty()) {
            throw new ConversionException("Meta description tag is missing or is empty.");
        } else {
            inputFile.setDescription(description);
        }
        if (keywords.isEmpty()) {
            throw new ConversionException("Meta keywords tag is missing or is empty.");
        } else {
            inputFile.setKeywords(keywords);
        }
        Element titleElement = h2title.first();
        if (titleElement == null) {
            throw new ConversionException("H2 main article title is missing.");
        } else {
            inputFile.setMainHeading(titleElement.ownText());
        }
        if (dateElement == null) {
            throw new ConversionException("Element <p id=\"date\"> is missing.");
        } else {
            inputFile.setDate(dateElement.ownText());
        }
    }
}
