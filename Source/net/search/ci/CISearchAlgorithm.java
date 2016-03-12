// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net.search.ci;

import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.ParentSet;
import weka.classifiers.bayes.net.search.local.LocalScoreSearchAlgorithm;
import weka.core.Instances;
import weka.core.RevisionUtils;

public class CISearchAlgorithm extends LocalScoreSearchAlgorithm {

    static final long serialVersionUID = 0x2bef3073224eb7f0L;
    BayesNet m_BayesNet;
    Instances m_instances;


    public String globalInfo() {
        return "The CISearchAlgorithm class supports Bayes net structure search algorithms that are based on conditional independence test (as opposed to for example score based of cross validation based search algorithms).";
    }

    protected boolean isConditionalIndependent(int i, int j, int ai[], int k) {
        ParentSet parentset;
        for (parentset = m_BayesNet.getParentSet(i); parentset.getNrOfParents() > 0; parentset.deleteLastParent(m_instances)) { }
        for (int l = 0; l < k; l++) {
            parentset.addParent(ai[l], m_instances);
        }

        double d = calcNodeScore(i);
        double d1 = calcScoreWithExtraParent(i, j);
        return d1 <= d;
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.7 $");
    }
}
