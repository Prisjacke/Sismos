// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileReader;
import javax.swing.Action;
import javax.swing.JFileChooser;
import weka.core.Instances;
import weka.gui.ExtensionFileFilter;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI

class > extends > {

    private static final long serialVersionUID = 0xfff8c19f04fe8461L;
    final GUI this$0;

    public void actionPerformed(ActionEvent actionevent) {
        JFileChooser jfilechooser = new JFileChooser(System.getProperty("user.dir"));
        ExtensionFileFilter extensionfilefilter = new ExtensionFileFilter(".arff", "ARFF files");
        jfilechooser.addChoosableFileFilter(extensionfilefilter);
        jfilechooser.setDialogTitle("Set Data File");
        int i = jfilechooser.showOpenDialog(GUI.this);
        if (i == 0) {
            String s = jfilechooser.getSelectedFile().toString();
            try {
                m_Instances = new Instances(new FileReader(s));
                m_Instances.setClassIndex(m_Instances.numAttributes() - 1);
                a_learn.setEnabled(true);
                a_learnCPT.setEnabled(true);
                repaint();
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public () {
        this$0 = GUI.this;
        super(GUI.this, "Set Data", "Set Data File", "setdata", "ctrl A");
    }
}
