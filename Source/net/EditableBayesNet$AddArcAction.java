// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import weka.core.FastVector;
import weka.core.SerializedObject;
import weka.estimators.Estimator;

// Referenced classes of package weka.classifiers.bayes.net:
//            EditableBayesNet

class m_CPT extends m_CPT {

    static final long serialVersionUID = 1L;
    FastVector m_children;
    int m_nParent;
    Estimator m_CPT[][];
    final EditableBayesNet this$0;

    public void undo() {
        try {
            for (int i = 0; i < m_children.size(); i++) {
                int j = ((Integer)m_children.elementAt(i)).intValue();
                deleteArc(m_nParent, j);
                SerializedObject serializedobject = new SerializedObject(m_CPT[i]);
                m_Distributions[j] = (Estimator[])(Estimator[])serializedobject.getObject();
            }

        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void redo() {
        try {
            for (int i = 0; i < m_children.size(); i++) {
                int j = ((Integer)m_children.elementAt(i)).intValue();
                addArc(m_nParent, j);
            }

        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    (int i, int j) {
        this$0 = EditableBayesNet.this;
        super(EditableBayesNet.this);
        try {
            m_nParent = i;
            m_children = new FastVector();
            m_children.addElement(Integer.valueOf(j));
            SerializedObject serializedobject = new SerializedObject(m_Distributions[j]);
            m_CPT = new Estimator[1][];
            m_CPT[0] = (Estimator[])(Estimator[])serializedobject.getObject();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    m_CPT(int i, FastVector fastvector) {
        this$0 = EditableBayesNet.this;
        super(EditableBayesNet.this);
        try {
            m_nParent = i;
            m_children = new FastVector();
            m_CPT = new Estimator[fastvector.size()][];
            for (int j = 0; j < fastvector.size(); j++) {
                int k = ((Integer)fastvector.elementAt(j)).intValue();
                m_children.addElement(Integer.valueOf(k));
                SerializedObject serializedobject = new SerializedObject(m_Distributions[k]);
                m_CPT[j] = (Estimator[])(Estimator[])serializedobject.getObject();
            }

        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
