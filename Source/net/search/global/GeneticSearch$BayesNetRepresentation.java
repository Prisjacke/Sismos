// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net.search.global;

import java.util.Random;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.ParentSet;
import weka.core.RevisionHandler;
import weka.core.RevisionUtils;

// Referenced classes of package weka.classifiers.bayes.net.search.global:
//            GeneticSearch

class m_nNodes
    implements RevisionHandler {

    int m_nNodes;
    boolean m_bits[];
    double m_fScore;
    final GeneticSearch this$0;

    public double getScore() {
        return m_fScore;
    }

    public void randomInit() {
        do {
            m_bits = new boolean[m_nNodes * m_nNodes];
            for (int i = 0; i < m_nNodes; i++) {
                int j;
                do {
                    j = m_random.nextInt(m_nNodes * m_nNodes);
                } while (isSquare(j));
                m_bits[j] = true;
            }

        } while (hasCycles());
        calcGlobalScore();
    }

    void calcGlobalScore() {
        for (int i = 0; i < m_nNodes; i++) {
            for (ParentSet parentset = m_BayesNet.getParentSet(i); parentset.getNrOfParents() > 0; parentset.deleteLastParent(m_BayesNet.m_Instances)) { }
        }

        for (int j = 0; j < m_nNodes; j++) {
            ParentSet parentset1 = m_BayesNet.getParentSet(j);
            for (int k = 0; k < m_nNodes; k++) {
                if (m_bits[k + j * m_nNodes]) {
                    parentset1.addParent(k, m_BayesNet.m_Instances);
                }
            }

        }

        try {
            m_fScore = calcScore(m_BayesNet);
        }
        catch (Exception exception) { }
    }

    public boolean hasCycles() {
        boolean aflag[] = new boolean[m_nNodes];
        for (int i = 0; i < m_nNodes; i++) {
            boolean flag = false;
            for (int j = 0; !flag && j < m_nNodes; j++) {
                if (aflag[j]) {
                    continue;
                }
                boolean flag1 = true;
                for (int k = 0; k < m_nNodes; k++) {
                    if (m_bits[k + j * m_nNodes] && !aflag[k]) {
                        flag1 = false;
                    }
                }

                if (flag1) {
                    aflag[j] = true;
                    flag = true;
                }
            }

            if (!flag) {
                return true;
            }
        }

        return false;
    }

    m_nNodes copy() {
        m_nNodes m_nnodes = new <init>(m_nNodes);
        m_nnodes.m_bits = new boolean[m_bits.length];
        for (int i = 0; i < m_nNodes * m_nNodes; i++) {
            m_nnodes.m_bits[i] = m_bits[i];
        }

        m_nnodes.m_fScore = m_fScore;
        return m_nnodes;
    }

    void mutate() {
        do {
            int i;
            do {
                i = m_random.nextInt(m_nNodes * m_nNodes);
            } while (isSquare(i));
            m_bits[i] = !m_bits[i];
        } while (hasCycles());
        calcGlobalScore();
    }

    void crossOver(calcGlobalScore calcglobalscore) {
        boolean aflag[] = new boolean[m_bits.length];
        for (int i = 0; i < m_bits.length; i++) {
            aflag[i] = m_bits[i];
        }

        int j = m_bits.length;
        do {
            for (int k = j; k < m_bits.length; k++) {
                m_bits[k] = aflag[k];
            }

            j = m_random.nextInt(m_bits.length);
            for (int l = j; l < m_bits.length; l++) {
                m_bits[l] = calcglobalscore.m_bits[l];
            }

        } while (hasCycles());
        calcGlobalScore();
    }

    boolean isSquare(int i) {
        if (GeneticSearch.g_bIsSquare == null || GeneticSearch.g_bIsSquare.length < i) {
            GeneticSearch.g_bIsSquare = new boolean[m_nNodes * m_nNodes];
            for (int j = 0; j < m_nNodes; j++) {
                GeneticSearch.g_bIsSquare[j * m_nNodes + j] = true;
            }

        }
        return GeneticSearch.g_bIsSquare[i];
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.5 $");
    }

    (int i) {
        this$0 = GeneticSearch.this;
        super();
        m_nNodes = 0;
        m_fScore = 0.0D;
        m_nNodes = i;
    }
}
