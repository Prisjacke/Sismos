// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.event.ActionEvent;
import javax.swing.JLabel;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI, EditableBayesNet

class setEnabled extends setEnabled {

    private static final long serialVersionUID = 0xfff1a6a1f70c4e61L;
    final GUI this$0;

    public boolean isEnabled() {
        return m_BayesNet.canRedo();
    }

    public void actionPerformed(ActionEvent actionevent) {
        String s = m_BayesNet.redo();
        m_jStatusBar.setText((new StringBuilder()).append("Redo action performed: ").append(s).toString());
        m_Selection.lear();
        updateStatus();
        repaint();
    }

    public t() {
        this$0 = GUI.this;
        super(GUI.this, "Redo", "Redo", "redo", "ctrl Y");
        setEnabled(false);
    }
}
