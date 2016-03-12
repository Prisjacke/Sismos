// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import weka.core.SerializedObject;
import weka.estimators.Estimator;

// Referenced classes of package weka.classifiers.bayes.net:
//            EditableBayesNet

class m_P extends m_P {

    static final long serialVersionUID = 1L;
    int m_nTargetNode;
    Estimator m_CPT[];
    double m_P[][];
    final EditableBayesNet this$0;

    public void undo() {
        try {
            SerializedObject serializedobject = new SerializedObject(m_CPT);
            m_Distributions[m_nTargetNode] = (Estimator[])(Estimator[])serializedobject.getObject();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void redo() {
        try {
            setDistribution(m_nTargetNode, m_P);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public String getUndoMsg() {
        return (new StringBuilder()).append("Distribution of node ").append(getNodeName(m_nTargetNode)).append(" changed").toString();
    }

    public String getRedoMsg() {
        return (new StringBuilder()).append("Distribution of node ").append(getNodeName(m_nTargetNode)).append(" changed").toString();
    }

    (int i, double ad[][]) {
        this$0 = EditableBayesNet.this;
        super(EditableBayesNet.this);
        try {
            m_nTargetNode = i;
            SerializedObject serializedobject = new SerializedObject(m_Distributions[i]);
            m_CPT = (Estimator[])(Estimator[])serializedobject.getObject();
            m_P = ad;
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
