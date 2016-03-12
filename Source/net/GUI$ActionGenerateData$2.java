// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import weka.gui.ExtensionFileFilter;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI

class val.jTfFile
    implements ActionListener {

    final JTextField val$jTfFile;
    final g this$1;

    public void actionPerformed(ActionEvent actionevent) {
        JFileChooser jfilechooser = new JFileChooser(System.getProperty("user.dir"));
        ExtensionFileFilter extensionfilefilter = new ExtensionFileFilter(".arff", "Arff files");
        jfilechooser.addChoosableFileFilter(extensionfilefilter);
        jfilechooser.setDialogTitle("Save Instances As");
        int i = jfilechooser.showSaveDialog(_fld0);
        if (i == 0) {
            String s = jfilechooser.getSelectedFile().toString();
            val$jTfFile.setText(s);
        }
        g.setVisible(true);
    }

    () {
        this$1 = final_;
        val$jTfFile = JTextField.this;
        super();
    }
}
