// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.JLabel;

// Referenced classes of package weka.classifiers.bayes.net:
//            EditableBayesNet, GUI

class init> extends init> {

    private static final long serialVersionUID = 0xfff8c19f04fe8465L;
    final GUI this$0;

    public void actionPerformed(ActionEvent actionevent) {
        m_sFileName = "";
        m_BayesNet = new EditableBayesNet(true);
        updateStatus();
        layoutGraph();
        a_datagenerator.setEnabled(false);
        m_BayesNet.clearUndoStack();
        m_jStatusBar.setText("New Network");
        m_Selection = new <init>(GUI.this);
        repaint();
    }

    public et() {
        this$0 = GUI.this;
        super(GUI.this, "New", "New Network", "new", "");
    }
}
