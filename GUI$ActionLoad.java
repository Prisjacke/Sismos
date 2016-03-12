// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import weka.gui.ExtensionFileFilter;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI

class nit> extends nit> {

    private static final long serialVersionUID = 0xfff8c19f04fe8465L;
    final GUI this$0;

    public void actionPerformed(ActionEvent actionevent) {
        JFileChooser jfilechooser = new JFileChooser(System.getProperty("user.dir"));
        ExtensionFileFilter extensionfilefilter = new ExtensionFileFilter(".arff", "ARFF files");
        ExtensionFileFilter extensionfilefilter1 = new ExtensionFileFilter(".xml", "XML BIF files");
        jfilechooser.addChoosableFileFilter(extensionfilefilter);
        jfilechooser.addChoosableFileFilter(extensionfilefilter1);
        jfilechooser.setDialogTitle("Load Graph");
        int i = jfilechooser.showOpenDialog(GUI.this);
        if (i == 0) {
            String s = jfilechooser.getSelectedFile().toString();
            if (s.endsWith(extensionfilefilter.getExtensions()[0])) {
                initFromArffFile(s);
            } else {
                try {
                    readBIFFromFile(s);
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            m_jStatusBar.setText((new StringBuilder()).append("Loaded ").append(s).toString());
            updateStatus();
        }
    }

    public () {
        this$0 = GUI.this;
        super(GUI.this, "Load", "Load Graph", "open", "ctrl O");
    }
}
