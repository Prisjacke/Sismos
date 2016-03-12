// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import weka.core.FastVector;

// Referenced classes of package weka.classifiers.bayes.net:
//            EditableBayesNet

class m_dY extends m_dY {

    static final long serialVersionUID = 1L;
    FastVector m_nodes;
    int m_dX;
    int m_dY;
    final EditableBayesNet this$0;

    public void undo() {
        for (int i = 0; i < m_nodes.size(); i++) {
            int j = ((Integer)m_nodes.elementAt(i)).intValue();
            setPosition(j, getPositionX(j) - m_dX, getPositionY(j) - m_dY);
        }

    }

    public void redo() {
        for (int i = 0; i < m_nodes.size(); i++) {
            int j = ((Integer)m_nodes.elementAt(i)).intValue();
            setPosition(j, getPositionX(j) + m_dX, getPositionY(j) + m_dY);
        }

    }

    public void setUndoPosition(int i, int j) {
        m_dX += i;
        m_dY += j;
    }

    (FastVector fastvector, int i, int j) {
        this$0 = EditableBayesNet.this;
        super(EditableBayesNet.this);
        m_nodes = new FastVector(fastvector.size());
        for (int k = 0; k < fastvector.size(); k++) {
            m_nodes.addElement(fastvector.elementAt(k));
        }

        m_dX = i;
        m_dY = j;
    }
}
