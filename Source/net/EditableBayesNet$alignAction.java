// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import weka.core.FastVector;

// Referenced classes of package weka.classifiers.bayes.net:
//            EditableBayesNet

class  extends  {

    static final long serialVersionUID = 1L;
    FastVector m_nodes;
    FastVector m_posX;
    FastVector m_posY;
    final EditableBayesNet this$0;

    public void undo() {
        try {
            for (int i = 0; i < m_nodes.size(); i++) {
                int j = ((Integer)m_nodes.elementAt(i)).intValue();
                setPosition(j, ((Integer)m_posX.elementAt(i)).intValue(), ((Integer)m_posY.elementAt(i)).intValue());
            }

        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    (FastVector fastvector) {
        this$0 = EditableBayesNet.this;
        super(EditableBayesNet.this);
        m_nodes = new FastVector(fastvector.size());
        m_posX = new FastVector(fastvector.size());
        m_posY = new FastVector(fastvector.size());
        for (int i = 0; i < fastvector.size(); i++) {
            int j = ((Integer)fastvector.elementAt(i)).intValue();
            m_nodes.addElement(Integer.valueOf(j));
            m_posX.addElement(Integer.valueOf(getPositionX(j)));
            m_posY.addElement(Integer.valueOf(getPositionY(j)));
        }

    }
}
