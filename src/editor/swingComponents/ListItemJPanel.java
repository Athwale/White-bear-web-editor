/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.swingComponents;

import editor.StyledDocumentMaker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;

/**
 *
 * @author Ondřej Mejzlík
 */
public final class ListItemJPanel extends JPanel {

    private final ListJPanel container;
    private JTextPane jTextPane;
    private JLabel number;

    public ListItemJPanel(ListJPanel container) {
        this.container = container;
        this.setUpThisPanel();
        this.jTextPane.setStyledDocument(StyledDocumentMaker.createStyledDocument());
    }

    public JTextPane getTextPane() {
        return this.jTextPane;
    }

    public void setNumber(int number) {
        this.number.setText(String.valueOf(number));
    }

    private void jButtonRemoveActionPerformed(ActionEvent evt) {
        container.removeItem(this);
    }

    private void setUpThisPanel() {
        JButton jButtonRemove = new JButton(new ImageIcon(getClass().getResource("/editor/images/redCross.png")));
        JScrollPane jScrollPane1 = new JScrollPane();
        this.jTextPane = new JTextPane();
        this.number = new JLabel();

        jButtonRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemoveActionPerformed(evt);
            }
        });
        jScrollPane1.setViewportView(jTextPane);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(this);
        this.setLayout(jPanel2Layout);

        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap(5, 5)
                        .addComponent(this.number, 20, 20, 20)
                        .addComponent(jScrollPane1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonRemove, 25, 25, 25)
                        .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(this.number)
                        .addContainerGap(35, 35))
                .addComponent(jScrollPane1)
                .addComponent(jButtonRemove, 90, 90, 90)
        );
    }
}
