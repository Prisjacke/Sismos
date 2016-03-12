// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import weka.classifiers.bayes.BayesNet;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.RevisionHandler;
import weka.core.RevisionUtils;
import weka.estimators.Estimator;

// Referenced classes of package weka.classifiers.bayes.net:
//            BIFReader, ParentSet

public class MarginCalculator
    implements Serializable, RevisionHandler {
    public class JunctionTreeNode
        implements Serializable, RevisionHandler {

        private static final long serialVersionUID = 0x9064069c61d69f0L;
        BayesNet m_bayesNet;
        public int m_nNodes[];
        int m_nCardinality;
        double m_fi[];
        double m_P[];
        double m_MarginalP[][];
        JunctionTreeSeparator m_parentSeparator;
        public Vector m_children;
        final MarginCalculator this$0;

        public void setParentSeparator(JunctionTreeSeparator junctiontreeseparator) {
            m_parentSeparator = junctiontreeseparator;
        }

        public void addChildClique(JunctionTreeNode junctiontreenode) {
            m_children.add(junctiontreenode);
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
                JunctionTreeNode junctiontreenode = (JunctionTreeNode)iterator.next();
                JunctionTreeSeparator junctiontreeseparator = junctiontreenode.m_parentSeparator;
                int i1 = 0;
                while (i1 < m_nCardinality)  {
                    int j1 = getCPT(junctiontreeseparator.m_nNodes, junctiontreeseparator.m_nNodes.length, ai, ai1, m_bayesNet);
                    int k1 = getCPT(m_nNodes, m_nNodes.length, ai, ai1, m_bayesNet);
                    m_P[k1] *= junctiontreeseparator.m_fiChild[j1];
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
                JunctionTreeNode junctiontreenode;
                for (Iterator iterator = m_children.iterator(); iterator.hasNext(); junctiontreenode.initializeDown(true)) {
                    junctiontreenode = (JunctionTreeNode)iterator.next();
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

            JunctionTreeNode junctiontreenode;
            for (Iterator iterator = m_children.iterator(); iterator.hasNext(); stringbuffer.append(junctiontreenode.toString())) {
                junctiontreenode = (JunctionTreeNode)iterator.next();
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

        void updateEvidence(JunctionTreeNode junctiontreenode) {
            if (junctiontreenode != this) {
                int ai[] = new int[m_nNodes.length];
                int ai1[] = new int[m_bayesNet.getNrOfNodes()];
                for (int i = 0; i < m_nNodes.length; i++) {
                    ai1[m_nNodes[i]] = i;
                }

                int ai2[] = junctiontreenode.m_parentSeparator.m_nNodes;
                int j = ai2.length;
label0:
                for (int k = 0; k < m_nCardinality; k++) {
                    int l = getCPT(m_nNodes, m_nNodes.length, ai, ai1, m_bayesNet);
                    int i1 = getCPT(ai2, j, ai, ai1, m_bayesNet);
                    if (junctiontreenode.m_parentSeparator.m_fiParent[i1] != 0.0D) {
                        m_P[l] *= junctiontreenode.m_parentSeparator.m_fiChild[i1] / junctiontreenode.m_parentSeparator.m_fiParent[i1];
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
                JunctionTreeNode junctiontreenode1 = (JunctionTreeNode)iterator.next();
                if (junctiontreenode1 != junctiontreenode) {
                    junctiontreenode1.initializeDown(true);
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

        JunctionTreeNode(Set set, BayesNet bayesnet, boolean aflag[]) {
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

    public class JunctionTreeSeparator
        implements Serializable, RevisionHandler {

        private static final long serialVersionUID = 0x5a3e8421bd26234fL;
        int m_nNodes[];
        int m_nCardinality;
        double m_fiParent[];
        double m_fiChild[];
        JunctionTreeNode m_parentNode;
        JunctionTreeNode m_childNode;
        BayesNet m_bayesNet;
        final MarginCalculator this$0;

        public void updateFromParent() {
            double ad[] = update(m_parentNode);
            if (ad == null) {
                m_fiParent = null;
            } else {
                m_fiParent = ad;
                double d = 0.0D;
                for (int i = 0; i < m_nCardinality; i++) {
                    d += m_fiParent[i];
                }

                for (int j = 0; j < m_nCardinality; j++) {
                    m_fiParent[j] /= d;
                }

            }
        }

        public void updateFromChild() {
            double ad[] = update(m_childNode);
            if (ad == null) {
                m_fiChild = null;
            } else {
                m_fiChild = ad;
                double d = 0.0D;
                for (int i = 0; i < m_nCardinality; i++) {
                    d += m_fiChild[i];
                }

                for (int j = 0; j < m_nCardinality; j++) {
                    m_fiChild[j] /= d;
                }

            }
        }

        public double[] update(JunctionTreeNode junctiontreenode) {
            if (junctiontreenode.m_P == null) {
                return null;
            }
            double ad[] = new double[m_nCardinality];
            int ai[] = new int[junctiontreenode.m_nNodes.length];
            int ai1[] = new int[m_bayesNet.getNrOfNodes()];
            for (int i = 0; i < junctiontreenode.m_nNodes.length; i++) {
                ai1[junctiontreenode.m_nNodes[i]] = i;
            }

label0:
            for (int j = 0; j < junctiontreenode.m_nCardinality; j++) {
                int k = getCPT(junctiontreenode.m_nNodes, junctiontreenode.m_nNodes.length, ai, ai1, m_bayesNet);
                int l = getCPT(m_nNodes, m_nNodes.length, ai, ai1, m_bayesNet);
                ad[l] += junctiontreenode.m_P[k];
                int i1 = 0;
                ai[i1]++;
                do {
                    if (i1 >= junctiontreenode.m_nNodes.length || ai[i1] != m_bayesNet.getCardinality(junctiontreenode.m_nNodes[i1])) {
                        continue label0;
                    }
                    ai[i1] = 0;
                    if (++i1 < junctiontreenode.m_nNodes.length) {
                        ai[i1]++;
                    }
                } while (true);
            }

            return ad;
        }

        public String getRevision() {
            return RevisionUtils.extract("$Revision: 1.2 $");
        }

        JunctionTreeSeparator(Set set, BayesNet bayesnet, JunctionTreeNode junctiontreenode, JunctionTreeNode junctiontreenode1) {
            this$0 = MarginCalculator.this;
            super();
            m_nNodes = new int[set.size()];
            int i = 0;
            m_nCardinality = 1;
            for (Iterator iterator = set.iterator(); iterator.hasNext();) {
                int j = ((Integer)iterator.next()).intValue();
                m_nNodes[i++] = j;
                m_nCardinality *= bayesnet.getCardinality(j);
            }

            m_parentNode = junctiontreenode1;
            m_childNode = junctiontreenode;
            m_bayesNet = bayesnet;
        }
    }


    private static final long serialVersionUID = 0x9064069c61d69eeL;
    boolean m_debug;
    public JunctionTreeNode m_root;
    JunctionTreeNode jtNodes[];
    double m_Margins[][];

    public MarginCalculator() {
        m_debug = false;
        m_root = null;
    }

    public int getNode(String s) {
        for (int i = 0; i < m_root.m_bayesNet.m_Instances.numAttributes(); i++) {
            if (m_root.m_bayesNet.m_Instances.attribute(i).name().equals(s)) {
                return i;
            }
        }

        return -1;
    }

    public String toXMLBIF03() {
        return m_root.m_bayesNet.toXMLBIF03();
    }

    public void calcMargins(BayesNet bayesnet) throws Exception {
        boolean aflag[][] = moralize(bayesnet);
        process(aflag, bayesnet);
    }

    public void calcFullMargins(BayesNet bayesnet) throws Exception {
        int i = bayesnet.getNrOfNodes();
        boolean aflag[][] = new boolean[i][i];
        for (int j = 0; j < i; j++) {
            for (int k = 0; k < i; k++) {
                aflag[j][k] = true;
            }

        }

        process(aflag, bayesnet);
    }

    public void process(boolean aflag[][], BayesNet bayesnet) throws Exception {
        int ai[] = getMaxCardOrder(aflag);
        aflag = fillIn(ai, aflag);
        ai = getMaxCardOrder(aflag);
        Set aset[] = getCliques(ai, aflag);
        Set aset1[] = getSeparators(ai, aset);
        int ai1[] = getCliqueTree(ai, aset, aset1);
        int i = aflag.length;
        if (m_debug) {
            for (int j = 0; j < i; j++) {
                int i1 = ai[j];
                if (aset[i1] == null) {
                    continue;
                }
                System.out.print((new StringBuilder()).append("Clique ").append(i1).append(" (").toString());
                Iterator iterator = aset[i1].iterator();
                do {
                    if (!iterator.hasNext()) {
                        break;
                    }
                    int k1 = ((Integer)iterator.next()).intValue();
                    System.out.print((new StringBuilder()).append(k1).append(" ").append(bayesnet.getNodeName(k1)).toString());
                    if (iterator.hasNext()) {
                        System.out.print(",");
                    }
                } while (true);
                System.out.print(") S(");
                iterator = aset1[i1].iterator();
                do {
                    if (!iterator.hasNext()) {
                        break;
                    }
                    int l1 = ((Integer)iterator.next()).intValue();
                    System.out.print((new StringBuilder()).append(l1).append(" ").append(bayesnet.getNodeName(l1)).toString());
                    if (iterator.hasNext()) {
                        System.out.print(",");
                    }
                } while (true);
                System.out.println((new StringBuilder()).append(") parent clique ").append(ai1[i1]).toString());
            }

        }
        jtNodes = getJunctionTree(aset, aset1, ai1, ai, bayesnet);
        m_root = null;
        int k = 0;
        do {
            if (k >= i) {
                break;
            }
            if (ai1[k] < 0 && jtNodes[k] != null) {
                m_root = jtNodes[k];
                break;
            }
            k++;
        } while (true);
        m_Margins = new double[i][];
        initialize(jtNodes, ai, aset, aset1, ai1);
        for (int l = 0; l < i; l++) {
            int j1 = ai[l];
            if (aset[j1] != null && ai1[j1] == -1 && aset1[j1].size() > 0) {
                throw new Exception("Something wrong in clique tree");
            }
        }

        if (!m_debug);
    }

    void initialize(JunctionTreeNode ajunctiontreenode[], int ai[], Set aset[], Set aset1[], int ai1[]) {
        int i = ai.length;
        for (int j = i - 1; j >= 0; j--) {
            int l = ai[j];
            if (ajunctiontreenode[l] != null) {
                ajunctiontreenode[l].initializeUp();
            }
        }

        for (int k = 0; k < i; k++) {
            int i1 = ai[k];
            if (ajunctiontreenode[i1] != null) {
                ajunctiontreenode[i1].initializeDown(false);
            }
        }

    }

    JunctionTreeNode[] getJunctionTree(Set aset[], Set aset1[], int ai[], int ai1[], BayesNet bayesnet) {
        int i = ai1.length;
        Object obj = null;
        JunctionTreeNode ajunctiontreenode[] = new JunctionTreeNode[i];
        boolean aflag[] = new boolean[i];
        for (int j = 0; j < i; j++) {
            int l = ai1[j];
            if (aset[l] != null) {
                ajunctiontreenode[l] = new JunctionTreeNode(aset[l], bayesnet, aflag);
            }
        }

        for (int k = 0; k < i; k++) {
            int i1 = ai1[k];
            if (aset[i1] == null) {
                continue;
            }
            Object obj1 = null;
            JunctionTreeNode junctiontreenode;
            if (ai[i1] > 0) {
                JunctionTreeNode junctiontreenode1 = ajunctiontreenode[ai[i1]];
                JunctionTreeSeparator junctiontreeseparator = new JunctionTreeSeparator(aset1[i1], bayesnet, ajunctiontreenode[i1], junctiontreenode1);
                ajunctiontreenode[i1].setParentSeparator(junctiontreeseparator);
                ajunctiontreenode[ai[i1]].addChildClique(ajunctiontreenode[i1]);
            } else {
                junctiontreenode = ajunctiontreenode[i1];
            }
        }

        return ajunctiontreenode;
    }

    int getCPT(int ai[], int i, int ai1[], int ai2[], BayesNet bayesnet) {
        int j = 0;
        for (int k = 0; k < i; k++) {
            int l = ai[k];
            j *= bayesnet.getCardinality(l);
            j += ai1[ai2[l]];
        }

        return j;
    }

    int[] getCliqueTree(int ai[], Set aset[], Set aset1[]) {
        int i = ai.length;
        int ai1[] = new int[i];
        for (int j = 0; j < i; j++) {
            int k = ai[j];
            ai1[k] = -1;
            if (aset[k] == null || aset1[k].size() <= 0) {
                continue;
            }
            for (int l = 0; l < i; l++) {
                int i1 = ai[l];
                if (k != i1 && aset[i1] != null && aset[i1].containsAll(aset1[k])) {
                    ai1[k] = i1;
                    l = j;
                    l = 0;
                    l = i;
                }
            }

        }

        return ai1;
    }

    Set[] getSeparators(int ai[], Set aset[]) {
        int i = ai.length;
        HashSet ahashset[] = new HashSet[i];
        HashSet hashset = new HashSet();
        for (int j = 0; j < i; j++) {
            int k = ai[j];
            if (aset[k] != null) {
                HashSet hashset1 = new HashSet();
                hashset1.addAll(aset[k]);
                hashset1.retainAll(hashset);
                ahashset[k] = hashset1;
                hashset.addAll(aset[k]);
            }
        }

        return ahashset;
    }

    Set[] getCliques(int ai[], boolean aflag[][]) throws Exception {
        int i = aflag.length;
        HashSet ahashset[] = new HashSet[i];
        for (int j = i - 1; j >= 0; j--) {
            int l = ai[j];
            if (l == 22) {
                int k1 = 3;
                k1++;
            }
            HashSet hashset = new HashSet();
            hashset.add(Integer.valueOf(l));
            for (int l1 = 0; l1 < j; l1++) {
                int j2 = ai[l1];
                if (aflag[l][j2]) {
                    hashset.add(Integer.valueOf(j2));
                }
            }

            ahashset[l] = hashset;
        }

        for (int k = 0; k < i; k++) {
            for (int i1 = 0; i1 < i; i1++) {
                if (k != i1 && ahashset[k] != null && ahashset[i1] != null && ahashset[k].containsAll(ahashset[i1])) {
                    ahashset[i1] = null;
                }
            }

        }

        if (m_debug) {
            int ai1[] = new int[i];
label0:
            for (int j1 = 0; j1 < i; j1++) {
                if (ahashset[j1] == null) {
                    continue;
                }
                Iterator iterator = ahashset[j1].iterator();
                int i2 = 0;
                while (iterator.hasNext())  {
                    ai1[i2++] = ((Integer)iterator.next()).intValue();
                }
                int k2 = 0;
                do {
                    if (k2 >= ahashset[j1].size()) {
                        continue label0;
                    }
                    for (int l2 = 0; l2 < ahashset[j1].size(); l2++) {
                        if (k2 != l2 && !aflag[ai1[k2]][ai1[l2]]) {
                            throw new Exception((new StringBuilder()).append("Non clique").append(k2).append(" ").append(l2).toString());
                        }
                    }

                    k2++;
                } while (true);
            }

        }
        return ahashset;
    }

    public boolean[][] moralize(BayesNet bayesnet) {
        int i = bayesnet.getNrOfNodes();
        boolean aflag[][] = new boolean[i][i];
        for (int j = 0; j < i; j++) {
            ParentSet parentset = bayesnet.getParentSets()[j];
            moralizeNode(parentset, j, aflag);
        }

        return aflag;
    }

    private void moralizeNode(ParentSet parentset, int i, boolean aflag[][]) {
        for (int j = 0; j < parentset.getNrOfParents(); j++) {
            int k = parentset.getParent(j);
            if (m_debug && !aflag[i][k]) {
                System.out.println((new StringBuilder()).append("Insert ").append(i).append("--").append(k).toString());
            }
            aflag[i][k] = true;
            aflag[k][i] = true;
            for (int l = j + 1; l < parentset.getNrOfParents(); l++) {
                int i1 = parentset.getParent(l);
                if (m_debug && !aflag[i1][k]) {
                    System.out.println((new StringBuilder()).append("Mary ").append(k).append("--").append(i1).toString());
                }
                aflag[i1][k] = true;
                aflag[k][i1] = true;
            }

        }

    }

    public boolean[][] fillIn(int ai[], boolean aflag[][]) {
        int i = aflag.length;
        int ai1[] = new int[i];
        for (int j = 0; j < i; j++) {
            ai1[ai[j]] = j;
        }

        for (int k = i - 1; k >= 0; k--) {
            int l = ai[k];
            for (int i1 = 0; i1 < k; i1++) {
                int j1 = ai[i1];
                if (!aflag[l][j1]) {
                    continue;
                }
                for (int k1 = i1 + 1; k1 < k; k1++) {
                    int l1 = ai[k1];
                    if (!aflag[l][l1]) {
                        continue;
                    }
                    if (m_debug && (!aflag[j1][l1] || !aflag[l1][j1])) {
                        System.out.println((new StringBuilder()).append("Fill in ").append(j1).append("--").append(l1).toString());
                    }
                    aflag[j1][l1] = true;
                    aflag[l1][j1] = true;
                }

            }

        }

        return aflag;
    }

    int[] getMaxCardOrder(boolean aflag[][]) {
        int i = aflag.length;
        int ai[] = new int[i];
        if (i == 0) {
            return ai;
        }
        boolean aflag1[] = new boolean[i];
        ai[0] = 0;
        aflag1[0] = true;
        for (int j = 1; j < i; j++) {
            int k = -1;
            int l = -1;
            for (int i1 = 0; i1 < i; i1++) {
                if (aflag1[i1]) {
                    continue;
                }
                int j1 = 0;
                for (int k1 = 0; k1 < i; k1++) {
                    if (aflag[i1][k1] && aflag1[k1]) {
                        j1++;
                    }
                }

                if (j1 > k) {
                    k = j1;
                    l = i1;
                }
            }

            ai[j] = l;
            aflag1[l] = true;
        }

        return ai;
    }

    public void setEvidence(int i, int j) throws Exception {
        if (m_root == null) {
            throw new Exception("Junction tree not initialize yet");
        }
        int k;
        for (k = 0; k < jtNodes.length && (jtNodes[k] == null || !jtNodes[k].contains(i)); k++) { }
        if (jtNodes.length == k) {
            throw new Exception((new StringBuilder()).append("Could not find node ").append(i).append(" in junction tree").toString());
        } else {
            jtNodes[k].setEvidence(i, j);
            return;
        }
    }

    public String toString() {
        return m_root.toString();
    }

    public double[] getMargin(int i) {
        return m_Margins[i];
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.2 $");
    }

    public static void main(String args[]) {
        try {
            BIFReader bifreader = new BIFReader();
            bifreader.processFile(args[0]);
            MarginCalculator margincalculator = new MarginCalculator();
            margincalculator.calcMargins(bifreader);
            byte byte0 = 2;
            int i = 0;
            byte byte1 = 4;
            int j = 0;
            margincalculator.setEvidence(byte0, i);
            margincalculator.setEvidence(byte1, j);
            System.out.print(margincalculator.toString());
            margincalculator.calcFullMargins(bifreader);
            margincalculator.setEvidence(byte0, i);
            margincalculator.setEvidence(byte1, j);
            System.out.println("==============");
            System.out.print(margincalculator.toString());
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
