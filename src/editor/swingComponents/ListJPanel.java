/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.swingComponents;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Reprezentuje seznam. Polozky sezamu se daji pridavat a kazdou lze samostatne
 * odebrat. Hlavni panel, kterym je tato trida ma nastaveny BorderLayout a
 * obsahuje v sobe tlacitko na pridani dalsi polozky a panel, do ktereho se
 * polozky pridavaji. Tento panel ma jiny layout GridLayout, ktery umoznuje
 * dynamicky menit velikost jak se nove polozky pridavaji.
 *
 * @author Ondřej Mejzlík
 */
public final class ListJPanel extends JPanel {

    private JPanel mainPanel;
    private LinkedList<ListItemJPanel> items;
    private String type;

    /**
     * Konstruktor seznamu. Spousti metodu, ktera nastavuje jak tento panel
     * vypada a funguje. Protoze pokud se prekrytelne metody spousti z
     * konstruktoru, vypisuje se chyba.
     *
     * @param type Typ seznamu, jestli je cislovany "numbered" nebo
     * "notNumbered".
     */
    public ListJPanel(String type) {
        this.items = new LinkedList<>();
        this.type = type;
        this.setUpThisPanel();
    }

    /**
     * Vymaze ze seznamu polozku, kterou dostane jako parametr. Polozky seznamu
     * jsou az v mainPanel a proto se remove vola na nem.
     *
     * @param item Polozka ke smazani.
     */
    public void removeItem(ListItemJPanel item) {
        this.mainPanel.remove(item);
        this.items.remove(item);
        this.renumber();
        this.validate();
        this.repaint();
    }

    /**
     * Prida do seznamu polozku, kterou dostane jako parametr. Polozku prida i
     * do seznamu polozek LinkedList, kery se pouziva k precislovani.
     *
     * @param item Polozka k pridani.
     */
    public void addItem(ListItemJPanel item) {
        this.items.add(item);
        this.mainPanel.add(item);
        this.renumber();
        this.validate();
        this.repaint();
    }

    /**
     * Obsluhuje kliknuti na tlacitko pridani nove polozky seznamu. Vytvori
     * novou polozku a preda ji odkaz na tento panel, aby polozka zpet mohla
     * poslat zpet pozadavek na sebesmazani.
     *
     * @param evt Nepouzity event z tlacitka.
     */
    private void jButtonAddItemActionPerformed(ActionEvent evt) {
        ListItemJPanel newItem = new ListItemJPanel(this);
        this.addItem(newItem);
    }

    /**
     * Precisluje znovu vsechny polozky seznamu pokud je seznam typu numbered.
     */
    public void renumber() {
        if (this.type.equals("numbered")) {
            int count = 1;
            for (ListItemJPanel listItem : this.items) {
                listItem.setNumber(count);
                count++;
            }
        }
    }

    /**
     * Nastavuje jak tento panel vypada a vklada ovladaci tlacitko.
     */
    private void setUpThisPanel() {
        JButton jButtonAddItem = new JButton();
        jButtonAddItem.setPreferredSize(new Dimension(60, 25));
        jButtonAddItem.setText("Add");
        jButtonAddItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddItemActionPerformed(evt);
            }
        });
        // Parametr konstruktoru urcuje rozestup mezi komponenty.
        this.setLayout(new BorderLayout(0, 2));
        // Parametr konstruktoru urcuje pocet radku a sloupcu.
        this.mainPanel = new JPanel(new GridLayout(0, 1));
        this.add(mainPanel, BorderLayout.BEFORE_FIRST_LINE);
        this.add(jButtonAddItem, BorderLayout.LINE_START);
    }

}
