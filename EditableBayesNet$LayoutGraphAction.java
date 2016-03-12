// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import weka.core.FastVector;

// Referenced classes of package weka.classifiers.bayes.net:
//            EditableBayesNet

class m_nPosY2 extends m_nPosY2 {

    static final long serialVersionUID = 1L;
    FastVector m_nPosX;
    FastVector m_nPosY;
    FastVector m_nPosX2;
    FastVector m_nPosY2;
    final EditableBayesNet this$0;

    public void undo() {
        for (int i = 0; i < m_nPosX.size(); i++) {
            setPosition(i, ((Integer)m_nPosX.elementAt(i)).intValue(), ((Integer)m_nPosY.elementAt(i)).intValue());
        }

    }

    public void redo() {
        for (int i = 0; i < m_nPosX.size(); i++) {
            setPosition(i, ((Integer)m_nPosX2.elementAt(i)).intValue(), ((Integer)m_nPosY2.elementAt(i)).intValue());
        }

    }

    (FastVector fastvector, FastVector fastvector1) {
        this$0 = EditableBayesNet.this;
        super(EditableBayesNet.this);
        m_nPosX = new FastVector(fastvector.size());
        m_nPosY = new FastVector(fastvector.size());
        m_nPosX2 = new FastVector(fastvector.size());
        m_nPosY2 = new FastVector(fastvector.size());
        for (int i = 0; i < fastvector.size(); i++) {
            m_nPosX.addElement(m_nPositionX.elementAt(i));
            m_nPosY.addElement(m_nPositionY.elementAt(i));
            m_nPosX2.addElement(fastvector.elementAt(i));
            m_nPosY2.addElement(fastvector1.elementAt(i));
        }

    }
}
