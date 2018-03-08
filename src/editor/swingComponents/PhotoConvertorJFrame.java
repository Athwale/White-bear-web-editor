/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.swingComponents;

import editor.swingWorkers.PhotoConvertorSwingWorker;
import java.awt.HeadlessException;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.HashMap;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Zrusit nahled.
 *
 * @author Ondřej Mejzlík
 */
public class PhotoConvertorJFrame extends JFrame {

    private JProgressBar jProgressBarImported;
    private JList<String> jListSelectedImages;
    private final JFileChooser imageChooser;
    private final JFileChooser directoryChooser;
    private PhotoConvertorSwingWorker convertorWorker = null;
    private JButton jButtonBrowse;
    private JButton jButtonImport;
    private JRadioButton jRadioButtonMakeThumbnails;
    private JRadioButton jRadioButtonJustCopy;
    private final HashMap<String, String> loadedImages;
    private File originalsDirectory;

    public PhotoConvertorJFrame() throws HeadlessException {
        this.setUpThisWindow();
        this.imageChooser = new JFileChooser();
        this.imageChooser.setDialogTitle("Select images");
        this.imageChooser.setApproveButtonText("Select");
        this.imageChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "bmp"));
        this.imageChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        this.imageChooser.setMultiSelectionEnabled(true);
        this.directoryChooser = new JFileChooser();
        this.directoryChooser.setDialogTitle("Save to:");
        this.directoryChooser.setApproveButtonText("Select");
        this.directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        this.directoryChooser.setMultiSelectionEnabled(false);
        this.loadedImages = new HashMap<>();
    }

    /**
     * Zobrazi vyberove okno pro vyber obrazku a vybrane soubory vepise do
     * listu.
     */
    private void jButtonBrowseActionPerformed() {
        int chooserValue = this.imageChooser.showOpenDialog(this);
        if (chooserValue == JFileChooser.APPROVE_OPTION) {
            this.clear();
            File[] selectedFiles = this.imageChooser.getSelectedFiles();
            for (File file : selectedFiles) {
                String path = file.getAbsolutePath();
                String name = file.getName();
                this.loadedImages.put(name, path);
            }
            String[] fileNames = new String[loadedImages.size()];
            this.jListSelectedImages.setListData(loadedImages.keySet().toArray(fileNames));
            this.jProgressBarImported.setString("0/" + loadedImages.size());
            this.jButtonImport.setEnabled(true);
        }
    }

    /**
     * Zkopiruje originalni obrazky do slozky images/original podslozka, dle
     * vyberu uzivatele, vytvori thumbnaily 300x225px a ty ulozi do slozky
     * thumbnails a stejne podslozky.
     */
    private void jButtonImportActionPerformed() {
        if (this.loadedImages.size() > 0) {
            // Zobrazeni okna pro vyber slozky kam obrazky ulozit. Tato slozka
            // musi byt v images/original.            
            int chooserValue = this.directoryChooser.showOpenDialog(this);
            if (chooserValue == JFileChooser.APPROVE_OPTION) {
                this.originalsDirectory = this.directoryChooser.getSelectedFile();
                if (!(originalsDirectory.getAbsolutePath().contains("images/original"))) {
                    JOptionPane.showMessageDialog(null, "Images must be saved into images/original directory.", "Error", JOptionPane.WARNING_MESSAGE);
                } else {
                    // Nastaveni progressbaru na pocet obrazku.
                    this.jProgressBarImported.setMinimum(0);
                    this.jProgressBarImported.setMaximum(loadedImages.size());
                    this.jButtonBrowse.setEnabled(false);
                    this.jButtonImport.setEnabled(false);
                    // Vyber zpracovani obrazku.
                    boolean thumbnails = true;
                    if (this.jRadioButtonMakeThumbnails.isSelected()) {
                        thumbnails = true;
                    } else if (this.jRadioButtonJustCopy.isSelected()) {
                        thumbnails = false;
                    }
                    this.convertorWorker = new PhotoConvertorSwingWorker(originalsDirectory, loadedImages,
                            jProgressBarImported, jButtonBrowse, jButtonImport, thumbnails);
                    this.convertorWorker.execute();
                }
            }
        }
    }

    /**
     * Vynuluje obsah okna.
     */
    private void clear() {
        // Vymazani predchozich nahranych obrazku
        this.loadedImages.clear();
        // Vypnuti import tlacitka, protoze nebude co importovat.
        this.jButtonImport.setEnabled(false);
        this.jButtonBrowse.setEnabled(true);
        // Vymazani obsahu seznamu.
        String[] empty = new String[0];
        this.jListSelectedImages.setListData(empty);
        // Vynulovani progress baru
        this.jProgressBarImported.setValue(0);
        this.jProgressBarImported.setString("0");
    }

    /**
     * Zastavi probihajici zpracovavani obrazku pri zavreni okna a vynuluje
     * promenne.
     */
    private void stopOperation() {
        // Pokusi se zrusit probihajici swing worker. True znamena, ze ho muze
        // interruptnout. Worker muze byt null pokud okno ukoncime bez nahrani 
        // obrazku.
        if (this.convertorWorker != null) {
            this.convertorWorker.cancel(true);
        }
        this.clear();
    }

    private void setUpThisWindow() {
        this.jProgressBarImported = new javax.swing.JProgressBar();
        JLabel jLabel1 = new javax.swing.JLabel();
        JLabel jLabel2 = new javax.swing.JLabel();
        JLabel jLabel3 = new javax.swing.JLabel();
        this.jButtonBrowse = new javax.swing.JButton();
        this.jButtonImport = new javax.swing.JButton();
        JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        this.jListSelectedImages = new javax.swing.JList<>();
        this.jRadioButtonMakeThumbnails = new javax.swing.JRadioButton();
        this.jRadioButtonJustCopy = new javax.swing.JRadioButton();
        ButtonGroup buttonGroup1 = new javax.swing.ButtonGroup();

        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setTitle("Import images");
        setAlwaysOnTop(true);
        setResizable(false);
        setLocation(200, 200);

        buttonGroup1.add(jRadioButtonMakeThumbnails);
        jRadioButtonMakeThumbnails.setSelected(true);
        jRadioButtonMakeThumbnails.setText("Make 300 x 225 px thumbnails");

        buttonGroup1.add(jRadioButtonJustCopy);
        jRadioButtonJustCopy.setText("Only copy to images/original");
        jProgressBarImported.setToolTipText("Imported photos");
        jProgressBarImported.setStringPainted(true);
        jProgressBarImported.setString("0/0");
        jLabel1.setText("Selected images:");
        jLabel2.setText("Aspect ratio");
        jLabel3.setText("Max size 534 x 534 px");
        jButtonBrowse.setText("Browse");
        jButtonImport.setText("Import");
        // Tlacitko import je implicitne vypnute, zapne se po nahrani obrazku.
        jButtonImport.setEnabled(false);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jListSelectedImages.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListSelectedImages.setToolTipText("Selected images");
        jScrollPane1.setViewportView(jListSelectedImages);

        jButtonBrowse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseActionPerformed();
            }
        });

        jButtonImport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonImportActionPerformed();
            }
        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopOperation();
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jProgressBarImported, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(jLabel3))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jButtonImport, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButtonBrowse, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(21, 21, 21)
                                    .addComponent(jLabel2))
                                .addComponent(jRadioButtonMakeThumbnails)
                                .addComponent(jRadioButtonJustCopy)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(jRadioButtonMakeThumbnails)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jRadioButtonJustCopy)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonBrowse)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonImport))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBarImported, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }
}
