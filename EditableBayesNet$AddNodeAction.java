// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;


// Referenced classes of package weka.classifiers.bayes.net:
//            EditableBayesNet

class m_nPosY extends m_nPosY {

    static final long serialVersionUID = 1L;
    String m_sName;
    int m_nPosX;
    int m_nPosY;
    int m_nCardinality;
    final EditableBayesNet this$0;

    public void undo() {
        try {
            deleteNode(getNrOfNodes() - 1);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void redo() {
        try {
            addNode(m_sName, m_nCardinality, m_nPosX, m_nPosY);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    (String s, int i, int j, int k) {
        this$0 = EditableBayesNet.this;
        super(EditableBayesNet.this);
        m_sName = s;
        m_nCardinality = i;
        m_nPosX = j;
        m_nPosY = k;
    }
}
