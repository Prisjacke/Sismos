// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.SerializedObject;
import weka.estimators.Estimator;

// Referenced classes of package weka.classifiers.bayes.net:
//            ParentSet, EditableBayesNet

class m_nTargetNode extends m_nTargetNode {

    static final long serialVersionUID = 1L;
    int m_nTargetNode;
    Attribute m_att;
    Estimator m_CPT[];
    ParentSet m_ParentSet;
    FastVector m_deleteArcActions;
    int m_nPosX;
    int m_nPosY;
    final EditableBayesNet this$0;

    public void undo() {
        try {
            m_Instances.insertAttributeAt(m_att, m_nTargetNode);
            int i = m_Instances.numAttributes();
            ParentSet aparentset[] = new ParentSet[i];
            int j = 0;
            for (int k = 0; k < i; k++) {
                if (k == m_nTargetNode) {
                    SerializedObject serializedobject = new SerializedObject(m_ParentSet);
                    aparentset[k] = (ParentSet)serializedobject.getObject();
                    j = 1;
                    continue;
                }
                aparentset[k] = EditableBayesNet.access$200(EditableBayesNet.this)[k - j];
                for (int l = 0; l < aparentset[k].getNrOfParents(); l++) {
                    int k1 = aparentset[k].getParent(l);
                    if (k1 >= m_nTargetNode) {
                        aparentset[k].SetParent(l, k1 + 1);
                    }
                }

            }

            EditableBayesNet.access$302(EditableBayesNet.this, aparentset);
            Estimator aestimator[][] = new Estimator[i][];
            j = 0;
            for (int i1 = 0; i1 < i; i1++) {
                if (i1 == m_nTargetNode) {
                    SerializedObject serializedobject1 = new SerializedObject(m_CPT);
                    aestimator[i1] = (Estimator[])(Estimator[])serializedobject1.getObject();
                    j = 1;
                } else {
                    aestimator[i1] = m_Distributions[i1 - j];
                }
            }

            m_Distributions = aestimator;
            for (int j1 = 0; j1 < m_deleteArcActions.size(); j1++) {
                m_nTargetNode m_ntargetnode = (m_deleteArcActions)m_deleteArcActions.elementAt(j1);
                m_ntargetnode.ndo();
            }

            m_nPositionX.insertElementAt(Integer.valueOf(m_nPosX), m_nTargetNode);
            m_nPositionY.insertElementAt(Integer.valueOf(m_nPosY), m_nTargetNode);
            m_nEvidence.insertElementAt(Integer.valueOf(-1), m_nTargetNode);
            m_fMarginP.insertElementAt(new double[getCardinality(m_nTargetNode)], m_nTargetNode);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void redo() {
        try {
            deleteNode(m_nTargetNode);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    (int i) {
        this$0 = EditableBayesNet.this;
        super(EditableBayesNet.this);
        m_nTargetNode = i;
        m_att = m_Instances.attribute(i);
        try {
            SerializedObject serializedobject = new SerializedObject(m_Distributions[i]);
            m_CPT = (Estimator[])(Estimator[])serializedobject.getObject();
            serializedobject = new SerializedObject(EditableBayesNet.access$000(EditableBayesNet.this)[i]);
            m_ParentSet = (ParentSet)serializedobject.getObject();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        m_deleteArcActions = new FastVector();
        for (int j = 0; j < getNrOfNodes(); j++) {
            if (EditableBayesNet.access$100(EditableBayesNet.this)[j].contains(i)) {
                m_deleteArcActions.addElement(new init>(EditableBayesNet.this, i, j));
            }
        }

        m_nPosX = getPositionX(m_nTargetNode);
        m_nPosY = getPositionY(m_nTargetNode);
    }
}
