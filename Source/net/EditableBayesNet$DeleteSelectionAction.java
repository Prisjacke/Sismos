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

class  extends  {

    static final long serialVersionUID = 1L;
    FastVector m_nodes;
    Attribute m_att[];
    Estimator m_CPT[][];
    ParentSet m_ParentSet[];
    FastVector m_deleteArcActions;
    int m_nPosX[];
    int m_nPosY[];
    final EditableBayesNet this$0;

    public void undo() {
        try {
            for (int i = 0; i < m_nodes.size(); i++) {
                int k = ((Integer)m_nodes.elementAt(i)).intValue();
                m_Instances.insertAttributeAt(m_att[i], k);
            }

            int j = m_Instances.numAttributes();
            ParentSet aparentset[] = new ParentSet[j];
            int ai[] = new int[j];
            for (int l = 0; l < j; l++) {
                ai[l] = l;
            }

            for (int i1 = m_nodes.size() - 1; i1 >= 0; i1--) {
                int k1 = ((Integer)m_nodes.elementAt(i1)).intValue();
                for (int i2 = k1; i2 < j - 1; i2++) {
                    ai[i2] = ai[i2 + 1];
                }

            }

            int j1 = 0;
            for (int l1 = 0; l1 < j; l1++) {
                if (j1 < m_nodes.size() && (Integer)m_nodes.elementAt(j1) == Integer.valueOf(l1)) {
                    SerializedObject serializedobject = new SerializedObject(m_ParentSet[j1]);
                    aparentset[l1] = (ParentSet)serializedobject.getObject();
                    j1++;
                    continue;
                }
                aparentset[l1] = EditableBayesNet.access$600(EditableBayesNet.this)[l1 - j1];
                for (int j2 = 0; j2 < aparentset[l1].getNrOfParents(); j2++) {
                    int j3 = aparentset[l1].getParent(j2);
                    aparentset[l1].SetParent(j2, ai[j3]);
                }

            }

            EditableBayesNet.access$702(EditableBayesNet.this, aparentset);
            Estimator aestimator[][] = new Estimator[j][];
            j1 = 0;
            for (int k2 = 0; k2 < j; k2++) {
                if (j1 < m_nodes.size() && (Integer)m_nodes.elementAt(j1) == Integer.valueOf(k2)) {
                    SerializedObject serializedobject1 = new SerializedObject(m_CPT[j1]);
                    aestimator[k2] = (Estimator[])(Estimator[])serializedobject1.getObject();
                    j1++;
                } else {
                    aestimator[k2] = m_Distributions[k2 - j1];
                }
            }

            m_Distributions = aestimator;
            for (int l2 = 0; l2 < m_nodes.size(); l2++) {
                int k3 = ((Integer)m_nodes.elementAt(l2)).intValue();
                m_nPositionX.insertElementAt(Integer.valueOf(m_nPosX[l2]), k3);
                m_nPositionY.insertElementAt(Integer.valueOf(m_nPosY[l2]), k3);
                m_nEvidence.insertElementAt(Integer.valueOf(-1), k3);
                m_fMarginP.insertElementAt(new double[getCardinality(k3)], k3);
            }

            for (int i3 = 0; i3 < m_deleteArcActions.size(); i3++) {
                  = (m_deleteArcActions)m_deleteArcActions.elementAt(i3);
                .m_deleteArcActions();
            }

        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void redo() {
        try {
            for (int i = m_nodes.size() - 1; i >= 0; i--) {
                int j = ((Integer)m_nodes.elementAt(i)).intValue();
                deleteNode(j);
            }

        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public (FastVector fastvector) {
        this$0 = EditableBayesNet.this;
        super(EditableBayesNet.this);
        m_nodes = new FastVector();
        int i = fastvector.size();
        m_att = new Attribute[i];
        m_CPT = new Estimator[i][];
        m_ParentSet = new ParentSet[i];
        m_nPosX = new int[i];
        m_nPosY = new int[i];
        m_deleteArcActions = new FastVector();
        for (int j = 0; j < fastvector.size(); j++) {
            int k = ((Integer)fastvector.elementAt(j)).intValue();
            m_nodes.addElement(Integer.valueOf(k));
            m_att[j] = m_Instances.attribute(k);
            try {
                SerializedObject serializedobject = new SerializedObject(m_Distributions[k]);
                m_CPT[j] = (Estimator[])(Estimator[])serializedobject.getObject();
                serializedobject = new SerializedObject(EditableBayesNet.access$400(EditableBayesNet.this)[k]);
                m_ParentSet[j] = (ParentSet)serializedobject.getObject();
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
            m_nPosX[j] = getPositionX(k);
            m_nPosY[j] = getPositionY(k);
            for (int l = 0; l < getNrOfNodes(); l++) {
                if (!fastvector.contains(Integer.valueOf(l)) && EditableBayesNet.access$500(EditableBayesNet.this)[l].contains(k)) {
                    m_deleteArcActions.addElement(new (EditableBayesNet.this, k, l));
                }
            }

        }

    }
}
