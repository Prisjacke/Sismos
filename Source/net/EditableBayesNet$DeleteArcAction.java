// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import weka.core.SerializedObject;
import weka.estimators.Estimator;

// Referenced classes of package weka.classifiers.bayes.net:
//            ParentSet, EditableBayesNet

class m_CPT extends m_CPT {

    static final long serialVersionUID = 1L;
    int m_nParents[];
    int m_nChild;
    int m_nParent;
    Estimator m_CPT[];
    final EditableBayesNet this$0;

    public void undo() {
        try {
            SerializedObject serializedobject = new SerializedObject(m_CPT);
            m_Distributions[m_nChild] = (Estimator[])(Estimator[])serializedobject.getObject();
            ParentSet parentset = new ParentSet();
            for (int i = 0; i < m_nParents.length; i++) {
                parentset.addParent(m_nParents[i], m_Instances);
            }

            EditableBayesNet.access$800(EditableBayesNet.this)[m_nChild] = parentset;
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void redo() {
        try {
            deleteArc(m_nParent, m_nChild);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    (int i, int j) {
        this$0 = EditableBayesNet.this;
        super(EditableBayesNet.this);
        try {
            m_nChild = j;
            m_nParent = i;
            m_nParents = new int[getNrOfParents(j)];
            for (int k = 0; k < m_nParents.length; k++) {
                m_nParents[k] = getParent(j, k);
            }

            SerializedObject serializedobject = new SerializedObject(m_Distributions[j]);
            m_CPT = (Estimator[])(Estimator[])serializedobject.getObject();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
