// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;


// Referenced classes of package weka.classifiers.bayes.net:
//            EditableBayesNet

class m_nY extends m_nY {

    static final long serialVersionUID = 1L;
    int m_nTargetNode;
    int m_nX;
    int m_nY;
    int m_nX2;
    int m_nY2;
    final EditableBayesNet this$0;

    public void undo() {
        setPosition(m_nTargetNode, m_nX, m_nY);
    }

    public void redo() {
        setPosition(m_nTargetNode, m_nX2, m_nY2);
    }

    public void setUndoPosition(int i, int j) {
        m_nX2 = i;
        m_nY2 = j;
    }

    (int i, int j, int k) {
        this$0 = EditableBayesNet.this;
        super(EditableBayesNet.this);
        m_nTargetNode = i;
        m_nX2 = j;
        m_nY2 = k;
        m_nX = getPositionX(i);
        m_nY = getPositionY(i);
    }
}
