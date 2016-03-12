// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import weka.core.SerializedObject;

// Referenced classes of package weka.classifiers.bayes.net:
//            MarginCalculator, GUI, EditableBayesNet

class this._cls1
    implements ActionListener {

    final this._cls1 this$1;

    public void actionPerformed(ActionEvent actionevent) {
        try {
            String as[] = m_BayesNet.getValues(m_nCurrentNode);
            int i;
            for (i = 0; i < as.length && !as[i].equals(actionevent.getActionCommand()); i++) { }
            if (i == as.length) {
                i = -1;
            }
            if (i < as.length) {
                m_jStatusBar.setText((new StringBuilder()).append("Set evidence for ").append(m_BayesNet.getNodeName(m_nCurrentNode)).toString());
                if (m_BayesNet.getEvidence(m_nCurrentNode) < 0 && i >= 0) {
                    m_BayesNet.setEvidence(m_nCurrentNode, i);
                    m_marginCalculatorWithEvidence.setEvidence(m_nCurrentNode, i);
                } else {
                    m_BayesNet.setEvidence(m_nCurrentNode, i);
                    SerializedObject serializedobject = new SerializedObject(m_marginCalculator);
                    m_marginCalculatorWithEvidence = (MarginCalculator)serializedobject.getObject();
                    for (int k = 0; k < m_BayesNet.getNrOfNodes(); k++) {
                        if (m_BayesNet.getEvidence(k) >= 0) {
                            m_marginCalculatorWithEvidence.setEvidence(k, m_BayesNet.getEvidence(k));
                        }
                    }

                }
                for (int j = 0; j < m_BayesNet.getNrOfNodes(); j++) {
                    m_BayesNet.setMargin(j, m_marginCalculatorWithEvidence.getMargin(j));
                }

            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        repaint();
    }

    () {
        this$1 = this._cls1.this;
        super();
    }
}
