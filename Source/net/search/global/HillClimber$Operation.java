// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net.search.global;

import java.io.Serializable;
import weka.core.RevisionHandler;
import weka.core.RevisionUtils;

// Referenced classes of package weka.classifiers.bayes.net.search.global:
//            HillClimber

class m_nOperation
    implements Serializable, RevisionHandler {

    static final long serialVersionUID = 0xd744e3eb7cd7ee89L;
    static final int OPERATION_ADD = 0;
    static final int OPERATION_DEL = 1;
    static final int OPERATION_REVERSE = 2;
    public int m_nTail;
    public int m_nHead;
    public int m_nOperation;
    public double m_fScore;
    final HillClimber this$0;

    public boolean equals(m_nOperation m_noperation) {
        if (m_noperation == null) {
            return false;
        } else {
            return m_nOperation == m_noperation.m_nOperation && m_nHead == m_noperation.m_nHead && m_nTail == m_noperation.m_nTail;
        }
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.9 $");
    }

    public () {
        this$0 = HillClimber.this;
        super();
        m_fScore = -1E+100D;
    }

    public m_fScore(int i, int j, int k) {
        this$0 = HillClimber.this;
        super();
        m_fScore = -1E+100D;
        m_nHead = j;
        m_nTail = i;
        m_nOperation = k;
    }
}
