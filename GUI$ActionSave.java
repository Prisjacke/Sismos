// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import weka.gui.ExtensionFileFilter;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI, EditableBayesNet

class ef1 extends ef1 {

    private static final long serialVersionUID = 0xffb7903631f12becL;
    ExtensionFileFilter ef1;
    final GUI this$0;

    public void actionPerformed(ActionEvent actionevent) {
        if (!m_sFileName.equals("")) {
            saveFile(m_sFileName);
            m_BayesNet.isSaved();
            m_jStatusBar.setText((new StringBuilder()).append("Saved as ").append(m_sFileName).toString());
        } else
        if (saveAs()) {
            m_BayesNet.isSaved();
            m_jStatusBar.setText((new StringBuilder()).append("Saved as ").append(m_sFileName).toString());
        }
    }

    boolean saveAs() {
        JFileChooser jfilechooser = new JFileChooser(System.getProperty("user.dir"));
        jfilechooser.addChoosableFileFilter(ef1);
        jfilechooser.setDialogTitle("Save Graph As");
        if (!m_sFileName.equals("")) {
            jfilechooser.setSelectedFile(new File(m_sFileName));
        }
        int i = jfilechooser.showSaveDialog(GUI.this);
        if (i == 0) {
            String s = jfilechooser.getSelectedFile().toString();
            if (!s.endsWith(".xml")) {
                s = s.concat(".xml");
            }
            saveFile(s);
            return true;
        } else {
            return false;
        }
    }

    protected void saveFile(String s) {
        try {
            FileWriter filewriter = new FileWriter(s);
            filewriter.write(m_BayesNet.toXMLBIF03());
            filewriter.close();
            m_sFileName = s;
            m_jStatusBar.setText((new StringBuilder()).append("Saved as ").append(m_sFileName).toString());
        }
        catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
    }

    public t() {
        this$0 = GUI.this;
        super(GUI.this, "Save", "Save Graph", "save", "ctrl S");
        ef1 = new ExtensionFileFilter(".xml", "XML BIF files");
    }

    public ef1(String s, String s1, String s2, String s3) {
        this$0 = GUI.this;
        super(GUI.this, s, s1, s2, s3);
        ef1 = new ExtensionFileFilter(".xml", "XML BIF files");
    }
}
