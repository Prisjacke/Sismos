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
//            EditableBayesNet, ParentSet

class  extends  {

    static final long serialVersionUID = 1L;
    int m_nTargetNode;
    String m_sValue;
    Estimator m_CPT[];
    FastVector m_children;
    Estimator m_childAtts[][];
    Attribute m_att;
    final EditableBayesNet this$0;

    public void undo() {
        try {
            m_Instances.insertAttributeAt(m_att, m_nTargetNode);
            SerializedObject serializedobject = new SerializedObject(m_CPT);
            m_Distributions[m_nTargetNode] = (Estimator[])(Estimator[])serializedobject.getObject();
            for (int i = 0; i < m_children.size(); i++) {
                int j = ((Integer)m_children.elementAt(i)).intValue();
                m_Instances.insertAttributeAt(m_att, m_nTargetNode);
                SerializedObject serializedobject1 = new SerializedObject(m_childAtts[i]);
                m_Distributions[j] = (Estimator[])(Estimator[])serializedobject1.getObject();
            }

        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void redo() {
        try {
            delNodeValue(m_nTargetNode, m_sValue);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public String getUndoMsg() {
        return (new StringBuilder()).append("Value ").append(m_sValue).append(" added to node ").append(getNodeName(m_nTargetNode)).toString();
    }

    public String getRedoMsg() {
        return (new StringBuilder()).append("Value ").append(m_sValue).append(" removed from node ").append(getNodeName(m_nTargetNode)).toString();
    }

    (int i, String s) {
        this$0 = EditableBayesNet.this;
        super(EditableBayesNet.this);
        try {
            m_nTargetNode = i;
            m_sValue = s;
            m_att = m_Instances.attribute(i);
            SerializedObject serializedobject = new SerializedObject(m_Distributions[i]);
            m_CPT = (Estimator[])(Estimator[])serializedobject.getObject();
            m_children = new FastVector();
            for (int j = 0; j < getNrOfNodes(); j++) {
                if (EditableBayesNet.access$900(EditableBayesNet.this)[j].contains(i)) {
                    m_children.addElement(Integer.valueOf(j));
                }
            }

            m_childAtts = new Estimator[m_children.size()][];
            for (int k = 0; k < m_children.size(); k++) {
                int l = ((Integer)m_children.elementAt(k)).intValue();
                m_childAtts[k] = m_Distributions[l];
            }

        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
