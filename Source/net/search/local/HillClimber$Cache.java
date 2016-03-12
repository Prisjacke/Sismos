// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net.search.local;

import weka.core.RevisionHandler;
import weka.core.RevisionUtils;

// Referenced classes of package weka.classifiers.bayes.net.search.local:
//            HillClimber

class m_fDeltaScoreDel
    implements RevisionHandler {

    double m_fDeltaScoreAdd[][];
    double m_fDeltaScoreDel[][];
    final HillClimber this$0;

    public void put(ion ion, double d) {
        if (ion.m_nOperation == 0) {
            m_fDeltaScoreAdd[ion.m_nTail][ion.m_nHead] = d;
        } else {
            m_fDeltaScoreDel[ion.m_nTail][ion.m_nHead] = d;
        }
    }

    public double get(ion ion) {
        switch (ion.m_nOperation) {
        case 0: // '\0'
            return m_fDeltaScoreAdd[ion.m_nTail][ion.m_nHead];

        case 1: // '\001'
            return m_fDeltaScoreDel[ion.m_nTail][ion.m_nHead];

        case 2: // '\002'
            return m_fDeltaScoreDel[ion.m_nTail][ion.m_nHead] + m_fDeltaScoreAdd[ion.m_nHead][ion.m_nTail];
        }
        return 0.0D;
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.9 $");
    }

    ion(int i) {
        this$0 = HillClimber.this;
        super();
        m_fDeltaScoreAdd = new double[i][i];
        m_fDeltaScoreDel = new double[i][i];
    }
}
