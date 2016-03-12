// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.JLabel;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI, EditableBayesNet

class  extends  {

    private static final long serialVersionUID = 0xfff8c1b3bdcebe61L;
    final GUI this$0;

    public void actionPerformed(ActionEvent actionevent) {
        copy();
        m_BayesNet.deleteSelection(m_Selection.elected());
        m_jStatusBar.setText(m_BayesNet.lastActionMsg());
        m_Selection.r();
        a_undo.setEnabled(true);
        a_redo.setEnabled(false);
        repaint();
    }

    public () {
        this$0 = GUI.this;
        super(GUI.this, "Cut", "Cut Nodes", "cut", "ctrl X");
    }
}
