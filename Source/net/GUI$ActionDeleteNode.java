// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import weka.core.FastVector;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI, EditableBayesNet

class this._cls0 extends this._cls0 {

    private static final long serialVersionUID = 0xfff8c19ec963ba61L;
    final GUI this$0;

    public void actionPerformed(ActionEvent actionevent) {
        if (m_Selection.cted().size() > 0) {
            m_BayesNet.deleteSelection(m_Selection.cted());
            m_jStatusBar.setText(m_BayesNet.lastActionMsg());
            m_Selection.ActionMsg();
            updateStatus();
            repaint();
        } else {
            String as[] = new String[m_BayesNet.getNrOfNodes()];
            for (int i = 0; i < as.length; i++) {
                as[i] = m_BayesNet.getNodeName(i);
            }

            String s = (String)JOptionPane.showInputDialog(null, "Select node to delete", "Nodes", 0, null, as, as[0]);
            if (s != null && !s.equals("")) {
                int j = m_BayesNet.getNode2(s);
                deleteNode(j);
            }
        }
    }

    public () {
        this$0 = GUI.this;
        super(GUI.this, "Delete Node", "Delete Node", "delnode", "DELETE");
    }
}
