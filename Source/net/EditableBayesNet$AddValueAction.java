// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;


// Referenced classes of package weka.classifiers.bayes.net:
//            EditableBayesNet

class m_sValue extends m_sValue {

    static final long serialVersionUID = 1L;
    int m_nTargetNode;
    String m_sValue;
    final EditableBayesNet this$0;

    public void undo() {
        try {
            delNodeValue(m_nTargetNode, m_sValue);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void redo() {
        addNodeValue(m_nTargetNode, m_sValue);
    }

    public String getUndoMsg() {
        return (new StringBuilder()).append("Value ").append(m_sValue).append(" removed from node ").append(getNodeName(m_nTargetNode)).toString();
    }

    public String getRedoMsg() {
        return (new StringBuilder()).append("Value ").append(m_sValue).append(" added to node ").append(getNodeName(m_nTargetNode)).toString();
    }

    (int i, String s) {
        this$0 = EditableBayesNet.this;
        super(EditableBayesNet.this);
        m_nTargetNode = i;
        m_sValue = s;
    }
}
