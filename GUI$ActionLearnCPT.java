// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI, EditableBayesNet

class setEnabled extends setEnabled {

    private static final long serialVersionUID = 0xfff8d0cf4a8fdc64L;
    final GUI this$0;

    public void actionPerformed(ActionEvent actionevent) {
        if (m_Instances == null) {
            JOptionPane.showMessageDialog(null, "Select instances to learn from first (menu Tools/Set Data)");
            return;
        }
        try {
            m_BayesNet.setData(m_Instances);
        }
        catch (Exception exception) {
            JOptionPane.showMessageDialog(null, (new StringBuilder()).append("Data set is not compatible with network.\n").append(exception.getMessage()).append("\nChoose other instances (menu Tools/Set Data)").toString());
            return;
        }
        try {
            m_BayesNet.estimateCPTs();
            m_BayesNet.clearUndoStack();
        }
        catch (Exception exception1) {
            exception1.printStackTrace();
        }
        updateStatus();
    }

    public () {
        this$0 = GUI.this;
        super(GUI.this, "Learn CPT", "Learn conditional probability tables", "learncpt", "");
        setEnabled(false);
    }
}
