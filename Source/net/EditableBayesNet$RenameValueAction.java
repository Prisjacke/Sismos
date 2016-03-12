// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;


// Referenced classes of package weka.classifiers.bayes.net:
//            EditableBayesNet

class > extends > {

    static final long serialVersionUID = 1L;
    final EditableBayesNet this$0;

    public void undo() {
        renameNodeValue(m_nTargetNode, m_sNewName, m_sOldName);
    }

    public void redo() {
        renameNodeValue(m_nTargetNode, m_sOldName, m_sNewName);
    }

    public String getUndoMsg() {
        return (new StringBuilder()).append("Value of node ").append(getNodeName(m_nTargetNode)).append(" changed from ").append(m_sNewName).append(" to ").append(m_sOldName).toString();
    }

    public String getRedoMsg() {
        return (new StringBuilder()).append("Value of node ").append(getNodeName(m_nTargetNode)).append(" changed from ").append(m_sOldName).append(" to ").append(m_sNewName).toString();
    }

    (int i, String s, String s1) {
        this$0 = EditableBayesNet.this;
        super(EditableBayesNet.this, i, s, s1);
    }
}
