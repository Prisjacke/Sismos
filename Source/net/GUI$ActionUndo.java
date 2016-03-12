// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.JLabel;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI, EditableBayesNet

class setEnabled extends setEnabled {

    private static final long serialVersionUID = 0xfff534209bd2ce61L;
    final GUI this$0;

    public boolean isEnabled() {
        return m_BayesNet.canUndo();
    }

    public void actionPerformed(ActionEvent actionevent) {
        String s = m_BayesNet.undo();
        m_jStatusBar.setText((new StringBuilder()).append("Undo action performed: ").append(s).toString());
        a_redo.setEnabled(m_BayesNet.canRedo());
        a_undo.setEnabled(m_BayesNet.canUndo());
        m_Selection.lear();
        updateStatus();
        repaint();
    }

    public t() {
        this$0 = GUI.this;
        super(GUI.this, "Undo", "Undo", "undo", "ctrl Z");
        setEnabled(false);
    }
}
