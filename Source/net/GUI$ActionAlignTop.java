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

    private static final long serialVersionUID = 0xffed088886e28661L;
    final GUI this$0;

    public void actionPerformed(ActionEvent actionevent) {
        m_BayesNet.alignTop(m_Selection.lected());
        m_jStatusBar.setText(m_BayesNet.lastActionMsg());
        a_undo.setEnabled(true);
        a_redo.setEnabled(false);
        repaint();
    }

    public () {
        this$0 = GUI.this;
        super(GUI.this, "Align Top", "Align Top", "aligntop", "");
    }
}
