// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net.search.ci;

import weka.core.RevisionHandler;
import weka.core.RevisionUtils;

// Referenced classes of package weka.classifiers.bayes.net.search.ci:
//            ICSSearchAlgorithm

class ity
    implements RevisionHandler {

    public int m_set[];
    final ICSSearchAlgorithm this$0;

    public boolean contains(int i) {
        for (int j = 0; j < getMaxCardinality() && m_set[j] != -1; j++) {
            if (m_set[j] == i) {
                return true;
            }
        }

        return false;
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.8 $");
    }

    public () {
        this$0 = ICSSearchAlgorithm.this;
        super();
        m_set = new int[getMaxCardinality() + 1];
    }
}
