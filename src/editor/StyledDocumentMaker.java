/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor;

import java.awt.Color;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

/**
 *
 * @author Ondřej Mejzlík
 */
public class StyledDocumentMaker {

    private static final Color RED = new Color(255, 0, 0);
    private static final Color BLUE = new Color(26, 26, 255);
    private static final Color GREEN = new Color(36, 143, 36);
    private static final Color BLACK = new Color(0, 0, 0);
    private static final Color ORANGE = new Color(255, 102, 0);
    private static final Color PINK = new Color(255, 0, 255);
    private static final Color DARK_PINK = new Color(204, 0, 204);
    private static final Color DARK_PURPLE = new Color(51, 51, 153);
    private static final Color GREY = new Color(81, 81, 81);
    //private static final int H2_SIZE = 21;
    private static final int H3_SIZE = 18;

    /**
     * Vytvori novy prazdny stylovany dokument a prida mu vsechny potrebne
     * styly.
     *
     * @return Vytvoreny prazdny dokument se styly.
     */
    public static DefaultStyledDocument createStyledDocument() {
        DefaultStyledDocument doc = new DefaultStyledDocument();

        // Cerny text
        Style black = doc.addStyle("black", null);
        StyleConstants.setForeground(black, BLACK);

        // Cerny tucny text
        Style blackBold = doc.addStyle("blackBold", null);
        StyleConstants.setForeground(blackBold, BLACK);
        StyleConstants.setBold(blackBold, true);

        // Cerveny text
        Style red = doc.addStyle("red", null);
        StyleConstants.setForeground(red, RED);

        // Cerveny tucny text
        Style redBold = doc.addStyle("redBold", null);
        StyleConstants.setForeground(redBold, RED);
        StyleConstants.setBold(redBold, true);

        // Modry text
        Style blue = doc.addStyle("blue", null);
        StyleConstants.setForeground(blue, BLUE);

        // Modry tucny text
        Style blueBold = doc.addStyle("blueBold", null);
        StyleConstants.setForeground(blueBold, BLUE);
        StyleConstants.setBold(blueBold, true);

        // Modry podtrzeny text
        //Style blueUnderlined = doc.addStyle("blueUnderlined", null);
        //StyleConstants.setForeground(blueUnderlined, BLUE);
        //StyleConstants.setUnderline(blueUnderlined, true);
        // Zeleny text
        Style green = doc.addStyle("green", null);
        StyleConstants.setForeground(green, GREEN);

        // Zeleny tucny text
        Style greenBold = doc.addStyle("greenBold", null);
        StyleConstants.setForeground(greenBold, GREEN);
        StyleConstants.setBold(greenBold, true);

        // Oranzovy text
        Style orange = doc.addStyle("orange", null);
        StyleConstants.setForeground(orange, ORANGE);

        // Oranzovy tucny text
        Style orangeBold = doc.addStyle("orangeBold", null);
        StyleConstants.setForeground(orangeBold, ORANGE);
        StyleConstants.setBold(orangeBold, true);

        // Modry nadpis
        Style blueHeading = doc.addStyle("blueHeading", null);
        StyleConstants.setForeground(blueHeading, BLUE);
        StyleConstants.setFontSize(blueHeading, H3_SIZE);

        // Cerveny nadpis
        Style redHeading = doc.addStyle("redHeading", null);
        StyleConstants.setForeground(redHeading, RED);
        StyleConstants.setFontSize(redHeading, H3_SIZE);

        // Zeleny nadpis
        Style greenHeading = doc.addStyle("greenHeading", null);
        StyleConstants.setForeground(greenHeading, GREEN);
        StyleConstants.setFontSize(greenHeading, H3_SIZE);

        // Oranzovy nadpis
        Style orangeHeading = doc.addStyle("orangeHeading", null);
        StyleConstants.setForeground(orangeHeading, ORANGE);
        StyleConstants.setFontSize(orangeHeading, H3_SIZE);

        // Ruzovy nadpis
        Style pinkHeading = doc.addStyle("pinkHeading", null);
        StyleConstants.setForeground(pinkHeading, PINK);
        StyleConstants.setFontSize(pinkHeading, H3_SIZE);

        // Tmave ruzovy nadpis
        Style darkPinkHeading = doc.addStyle("darkPinkHeading", null);
        StyleConstants.setForeground(darkPinkHeading, DARK_PINK);
        StyleConstants.setFontSize(darkPinkHeading, H3_SIZE);

        // Tmave fialovy nadpis
        Style darkPurpleHeading = doc.addStyle("darkPurpleHeading", null);
        StyleConstants.setForeground(darkPurpleHeading, DARK_PURPLE);
        StyleConstants.setFontSize(darkPurpleHeading, H3_SIZE);

        // Sedy nadpis
        Style greyHeading = doc.addStyle("greyHeading", null);
        StyleConstants.setForeground(greyHeading, GREY);
        StyleConstants.setFontSize(greyHeading, H3_SIZE);

        // Cerny nadpis 3. urovne
        Style blackHeading3 = doc.addStyle("blackHeading3", null);
        StyleConstants.setForeground(blackHeading3, BLACK);
        StyleConstants.setFontSize(blackHeading3, H3_SIZE);

        // Cerny nadpis 2. urovne
        //Style blackHeading2 = doc.addStyle("blackHeading2", null);
        //StyleConstants.setForeground(blackHeading2, BLACK);
        //StyleConstants.setFontSize(blackHeading2, H2_SIZE);
        return doc;
    }
}
