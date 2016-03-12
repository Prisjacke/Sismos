// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI, EditableBayesNet

class this._cls1
    implements ActionListener {

    final g this$1;

    public void actionPerformed(ActionEvent actionevent) {
        String s = fName.getText();
        if (s.length() <= 0) {
            JOptionPane.showMessageDialog(null, "Name should have at least one character");
            return;
        }
        int i = (new Integer(fCard.getText())).intValue();
        if (i <= 1) {
            JOptionPane.showMessageDialog(null, "Cardinality should be larger than 1");
            return;
        }
        try {
            if (X < 0x7fffffff) {
                m_BayesNet.addNode(s, i, X, Y);
            } else {
                m_BayesNet.addNode(s, i);
            }
            m_jStatusBar.setText(m_BayesNet.lastActionMsg());
            a_undo.setEnabled(true);
            a_redo.setEnabled(false);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        repaint();
        g.setVisible(false);
    }

    () {
        this$1 = this._cls1.this;
        super();
    }
}
