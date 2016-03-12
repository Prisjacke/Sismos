// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import javax.swing.JLabel;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI, EditableBayesNet

class val.dlg
    implements ActionListener {

    final aphVisualizerTableModel val$tm;
    final JDialog val$dlg;
    final GUI this$0;

    public void actionPerformed(ActionEvent actionevent) {
        val$tm.setData();
        try {
            m_BayesNet.setDistribution(m_nCurrentNode, val$tm.m_fProbs);
            m_jStatusBar.setText(m_BayesNet.lastActionMsg());
            updateStatus();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        val$dlg.setVisible(false);
    }

    aphVisualizerTableModel() {
        this$0 = final_gui;
        val$tm = aphvisualizertablemodel;
        val$dlg = JDialog.this;
        super();
    }
}
