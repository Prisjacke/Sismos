// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import weka.classifiers.bayes.BayesNet;
import weka.core.RevisionHandler;
import weka.core.RevisionUtils;
import weka.estimators.Estimator;

// Referenced classes of package weka.classifiers.bayes.net:
//            MarginCalculator, ParentSet

public class calculatePotentials
    implements Serializable, RevisionHandler {

    private static final long serialVersionUID = 0x9064069c61d69f0L;
    BayesNet m_bayesNet;
    public int m_nNodes[];
    int m_nCardinality;
    double m_fi[];
    double m_P[];
    double m_MarginalP[][];
    ator m_parentSeparator;
    public Vector m_children;
    final MarginCalculator this$0;

    public void setParentSeparator(ator ator) {
        m_parentSeparator = ator;
    }

    public void addChildClique(m_parentSeparator m_parentseparator) {
        m_children.add(m_parentseparator);
    }

    public void initializeUp() {
        m_P = new double[m_nCardinality];
        for (int i = 0; i < m_nCardinality; i++) {
            m_P[i] = m_fi[i];
        }

        int ai[] = new int[m_nNodes.length];
        int ai1[] = new int[m_bayesNet.getNrOfNodes()];
        for (int j = 0; j < m_nNodes.length; j++) {
            ai1[m_nNodes[j]] = j;
        }

        for (Iterator iterator = m_children.iterator(); iterator.hasNext();) {
            m_children m_children1 = (m_children)iterator.next();
            ator ator = m_children1.m_parentSeparator;
            int i1 = 0;
            while (i1 < m_nCardinality)  {
                int j1 = getCPT(ator.m_nNodes, ator.m_nNodes.length, ai, ai1, m_bayesNet);
                int k1 = getCPT(m_nNodes, m_nNodes.length, ai, ai1, m_bayesNet);
                m_P[k1] *= ator.m_fiChild[j1];
                int l1 = 0;
                ai[l1]++;
                do {
                    if (l1 >= m_nNodes.length || ai[l1] != m_bayesNet.getCardinality(m_nNodes[l1])) {
                        break;
                    }
                    ai[l1] = 0;
                    if (++l1 < m_nNodes.length) {
                        ai[l1]++;
                    }
                } while (true);
                i1++;
            }
        }

        double d = 0.0D;
        for (int k = 0; k < m_nCardinality; k++) {
            d += m_P[k];
        }

        for (int l = 0; l < m_nCardinality; l++) {
            m_P[l] /= d;
        }

        if (m_parentSeparator != null) {
            m_parentSeparator.updateFromChild();
        }
    }

    public void initializeDown(boolean flag) {
        if (m_parentSeparator == null) {
            calcMarginalProbabilities();
        } else {
            m_parentSeparator.updateFromParent();
            int ai[] = new int[m_nNodes.length];
            int ai1[] = new int[m_bayesNet.getNrOfNodes()];
            for (int i = 0; i < m_nNodes.length; i++) {
                ai1[m_nNodes[i]] = i;
            }

label0:
            for (int j = 0; j < m_nCardinality; j++) {
                int k = getCPT(m_parentSeparator.m_nNodes, m_parentSeparator.m_nNodes.length, ai, ai1, m_bayesNet);
                int l = getCPT(m_nNodes, m_nNodes.length, ai, ai1, m_bayesNet);
                if (m_parentSeparator.m_fiChild[k] > 0.0D) {
                    m_P[l] *= m_parentSeparator.m_fiParent[k] / m_parentSeparator.m_fiChild[k];
                } else {
                    m_P[l] = 0.0D;
                }
                int k1 = 0;
                ai[k1]++;
                do {
                    if (k1 >= m_nNodes.length || ai[k1] != m_bayesNet.getCardinality(m_nNodes[k1])) {
                        continue label0;
                    }
                    ai[k1] = 0;
                    if (++k1 < m_nNodes.length) {
                        ai[k1]++;
                    }
                } while (true);
            }

            double d = 0.0D;
            for (int i1 = 0; i1 < m_nCardinality; i1++) {
                d += m_P[i1];
            }

            for (int j1 = 0; j1 < m_nCardinality; j1++) {
                m_P[j1] /= d;
            }

            m_parentSeparator.updateFromChild();
            calcMarginalProbabilities();
        }
        if (flag) {
            ator.updateFromChild updatefromchild;
            for (Iterator iterator = m_children.iterator(); iterator.hasNext(); updatefromchild.initializeDown(true)) {
                updatefromchild = (initializeDown)iterator.next();
            }

        }
    }

    void calcMarginalProbabilities() {
        int ai[] = new int[m_nNodes.length];
        int ai1[] = new int[m_bayesNet.getNrOfNodes()];
        m_MarginalP = new double[m_nNodes.length][];
        for (int i = 0; i < m_nNodes.length; i++) {
            ai1[m_nNodes[i]] = i;
            m_MarginalP[i] = new double[m_bayesNet.getCardinality(m_nNodes[i])];
        }

label0:
        for (int j = 0; j < m_nCardinality; j++) {
            int l = getCPT(m_nNodes, m_nNodes.length, ai, ai1, m_bayesNet);
            for (int i1 = 0; i1 < m_nNodes.length; i1++) {
                m_MarginalP[i1][ai[i1]] += m_P[l];
            }

            int j1 = 0;
            ai[j1]++;
            do {
                if (j1 >= m_nNodes.length || ai[j1] != m_bayesNet.getCardinality(m_nNodes[j1])) {
                    continue label0;
                }
                ai[j1] = 0;
                if (++j1 < m_nNodes.length) {
                    ai[j1]++;
                }
            } while (true);
        }

        for (int k = 0; k < m_nNodes.length; k++) {
            m_Margins[m_nNodes[k]] = m_MarginalP[k];
        }

    }

    public String toString() {
        StringBuffer stringbuffer = new StringBuffer();
        for (int i = 0; i < m_nNodes.length; i++) {
            stringbuffer.append((new StringBuilder()).append(m_bayesNet.getNodeName(m_nNodes[i])).append(": ").toString());
            for (int j = 0; j < m_MarginalP[i].length; j++) {
                stringbuffer.append((new StringBuilder()).append(m_MarginalP[i][j]).append(" ").toString());
            }

            stringbuffer.append('\n');
        }

        m_MarginalP m_marginalp;
        for (Iterator iterator = m_children.iterator(); iterator.hasNext(); stringbuffer.append(m_marginalp.toString())) {
            m_marginalp = (toString)iterator.next();
            stringbuffer.append("----------------\n");
        }

        return stringbuffer.toString();
    }

    void calculatePotentials(BayesNet bayesnet, Set set, boolean aflag[]) {
        m_fi = new double[m_nCardinality];
        int ai[] = new int[m_nNodes.length];
        int ai1[] = new int[bayesnet.getNrOfNodes()];
        for (int i = 0; i < m_nNodes.length; i++) {
            ai1[m_nNodes[i]] = i;
        }

        boolean aflag1[] = new boolean[m_nNodes.length];
        for (int j = 0; j < m_nNodes.length; j++) {
            int l = m_nNodes[j];
            aflag1[j] = !aflag[l];
            for (int j1 = 0; j1 < bayesnet.getNrOfParents(l); j1++) {
                int i2 = bayesnet.getParent(l, j1);
                if (!set.contains(Integer.valueOf(i2))) {
                    aflag1[j] = false;
                }
            }

            if (!aflag1[j]) {
                continue;
            }
            aflag[l] = true;
            if (m_debug) {
                System.out.println((new StringBuilder()).append("adding node ").append(l).toString());
            }
        }

label0:
        for (int k = 0; k < m_nCardinality; k++) {
            int i1 = getCPT(m_nNodes, m_nNodes.length, ai, ai1, bayesnet);
            m_fi[i1] = 1.0D;
            for (int k1 = 0; k1 < m_nNodes.length; k1++) {
                if (aflag1[k1]) {
                    int j2 = m_nNodes[k1];
                    int ai2[] = bayesnet.getParentSet(j2).getParents();
                    int k2 = getCPT(ai2, bayesnet.getNrOfParents(j2), ai, ai1, bayesnet);
                    double d = bayesnet.getDistributions()[j2][k2].getProbability(ai[k1]);
                    m_fi[i1] *= d;
                }
            }

            int l1 = 0;
            ai[l1]++;
            do {
                if (l1 >= m_nNodes.length || ai[l1] != bayesnet.getCardinality(m_nNodes[l1])) {
                    continue label0;
                }
                ai[l1] = 0;
                if (++l1 < m_nNodes.length) {
                    ai[l1]++;
                }
            } while (true);
        }

    }

    boolean contains(int i) {
        for (int j = 0; j < m_nNodes.length; j++) {
            if (m_nNodes[j] == i) {
                return true;
            }
        }

        return false;
    }

    public void setEvidence(int i, int j) throws Exception {
        int ai[] = new int[m_nNodes.length];
        int ai1[] = new int[m_bayesNet.getNrOfNodes()];
        int k = -1;
        for (int l = 0; l < m_nNodes.length; l++) {
            ai1[m_nNodes[l]] = l;
            if (m_nNodes[l] == i) {
                k = l;
            }
        }

        if (k < 0) {
            throw new Exception((new StringBuilder()).append("setEvidence: Node ").append(i).append(" not found in this clique").toString());
        }
label0:
        for (int i1 = 0; i1 < m_nCardinality; i1++) {
            if (ai[k] != j) {
                int j1 = getCPT(m_nNodes, m_nNodes.length, ai, ai1, m_bayesNet);
                m_P[j1] = 0.0D;
            }
            int k1 = 0;
            ai[k1]++;
            do {
                if (k1 >= m_nNodes.length || ai[k1] != m_bayesNet.getCardinality(m_nNodes[k1])) {
                    continue label0;
                }
                ai[k1] = 0;
                if (++k1 < m_nNodes.length) {
                    ai[k1]++;
                }
            } while (true);
        }

        double d = 0.0D;
        for (int l1 = 0; l1 < m_nCardinality; l1++) {
            d += m_P[l1];
        }

        for (int i2 = 0; i2 < m_nCardinality; i2++) {
            m_P[i2] /= d;
        }

        calcMarginalProbabilities();
        updateEvidence(this);
    }

    void updateEvidence(updateEvidence updateevidence) {
        if (updateevidence != this) {
            int ai[] = new int[m_nNodes.length];
            int ai1[] = new int[m_bayesNet.getNrOfNodes()];
            for (int i = 0; i < m_nNodes.length; i++) {
                ai1[m_nNodes[i]] = i;
            }

            int ai2[] = updateevidence.m_parentSeparator.m_nNodes;
            int j = ai2.length;
label0:
            for (int k = 0; k < m_nCardinality; k++) {
                int l = getCPT(m_nNodes, m_nNodes.length, ai, ai1, m_bayesNet);
                int i1 = getCPT(ai2, j, ai, ai1, m_bayesNet);
                if (updateevidence.m_parentSeparator.m_fiParent[i1] != 0.0D) {
                    m_P[l] *= updateevidence.m_parentSeparator.m_fiChild[i1] / updateevidence.m_parentSeparator.m_fiParent[i1];
                } else {
                    m_P[l] = 0.0D;
                }
                int l1 = 0;
                ai[l1]++;
                do {
                    if (l1 >= m_nNodes.length || ai[l1] != m_bayesNet.getCardinality(m_nNodes[l1])) {
                        continue label0;
                    }
                    ai[l1] = 0;
                    if (++l1 < m_nNodes.length) {
                        ai[l1]++;
                    }
                } while (true);
            }

            double d = 0.0D;
            for (int j1 = 0; j1 < m_nCardinality; j1++) {
                d += m_P[j1];
            }

            for (int k1 = 0; k1 < m_nCardinality; k1++) {
                m_P[k1] /= d;
            }

            calcMarginalProbabilities();
        }
        Iterator iterator = m_children.iterator();
        do {
            if (!iterator.hasNext()) {
                break;
            }
            updateEvidence updateevidence1 = (m_children)iterator.next();
            if (updateevidence1 != updateevidence) {
                updateevidence1.initializeDown(true);
            }
        } while (true);
        if (m_parentSeparator != null) {
            m_parentSeparator.updateFromChild();
            m_parentSeparator.m_parentNode.updateEvidence(this);
            m_parentSeparator.updateFromParent();
        }
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.2 $");
    }

    ator(Set set, BayesNet bayesnet, boolean aflag[]) {
        this$0 = MarginCalculator.this;
        super();
        m_bayesNet = bayesnet;
        m_children = new Vector();
        m_nNodes = new int[set.size()];
        int i = 0;
        m_nCardinality = 1;
        for (Iterator iterator = set.iterator(); iterator.hasNext();) {
            int j = ((Integer)iterator.next()).intValue();
            m_nNodes[i++] = j;
            m_nCardinality *= bayesnet.getCardinality(j);
        }

        calculatePotentials(bayesnet, set, aflag);
    }
}
