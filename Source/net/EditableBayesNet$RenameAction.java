// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;


// Referenced classes of package weka.classifiers.bayes.net:
//            EditableBayesNet

class m_sOldName extends m_sOldName {

    static final long serialVersionUID = 1L;
    int m_nTargetNode;
    String m_sNewName;
    String m_sOldName;
    final EditableBayesNet this$0;

    public void undo() {
        setNodeName(m_nTargetNode, m_sOldName);
    }

    public void redo() {
        setNodeName(m_nTargetNode, m_sNewName);
    }

    (int i, String s, String s1) {
        this$0 = EditableBayesNet.this;
        super(EditableBayesNet.this);
        m_nTargetNode = i;
        m_sNewName = s1;
        m_sOldName = s;
    }
}
