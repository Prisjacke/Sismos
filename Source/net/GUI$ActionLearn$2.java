// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI, EditableBayesNet

class this._cls1
    implements ActionListener {

    final g this$1;

    public void actionPerformed(ActionEvent actionevent) {
        try {
            m_BayesNet.buildClassifier(m_Instances);
            layoutGraph();
            updateStatus();
            m_BayesNet.clearUndoStack();
            g.setVisible(false);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        g.setVisible(false);
    }

    () {
        this$1 = this._cls1.this;
        super();
    }
}
