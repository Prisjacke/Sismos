// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net.search.ci;

import java.io.FileReader;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.ParentSet;
import weka.core.*;

// Referenced classes of package weka.classifiers.bayes.net.search.ci:
//            CISearchAlgorithm

public class ICSSearchAlgorithm extends CISearchAlgorithm {
    class SeparationSet
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

        public SeparationSet() {
            this$0 = ICSSearchAlgorithm.this;
            super();
            m_set = new int[getMaxCardinality() + 1];
        }
    }


    static final long serialVersionUID = 0xdd272f9cac3b2f90L;
    private int m_nMaxCardinality;

    public ICSSearchAlgorithm() {
        m_nMaxCardinality = 2;
    }

    String name(int i) {
        return m_instances.attribute(i).name();
    }

    int maxn() {
        return m_instances.numAttributes();
    }

    public void setMaxCardinality(int i) {
        m_nMaxCardinality = i;
    }

    public int getMaxCardinality() {
        return m_nMaxCardinality;
    }

    protected void search(BayesNet bayesnet, Instances instances) throws Exception {
        m_BayesNet = bayesnet;
        m_instances = instances;
        boolean aflag[][] = new boolean[maxn() + 1][];
        boolean aflag1[][] = new boolean[maxn() + 1][];
        SeparationSet aseparationset[][] = new SeparationSet[maxn() + 1][];
        for (int i = 0; i < maxn() + 1; i++) {
            aflag[i] = new boolean[maxn()];
            aflag1[i] = new boolean[maxn()];
            aseparationset[i] = new SeparationSet[maxn()];
        }

        calcDependencyGraph(aflag, aseparationset);
        calcVeeNodes(aflag, aflag1, aseparationset);
        calcArcDirections(aflag, aflag1);
        for (int j = 0; j < maxn(); j++) {
            ParentSet parentset;
            for (parentset = m_BayesNet.getParentSet(j); parentset.getNrOfParents() > 0; parentset.deleteLastParent(m_instances)) { }
            for (int k = 0; k < maxn(); k++) {
                if (aflag1[k][j]) {
                    parentset.addParent(k, m_instances);
                }
            }

        }

    }

    void calcDependencyGraph(boolean aflag[][], SeparationSet aseparationset[][]) {
        for (int i = 0; i < maxn(); i++) {
            for (int l = 0; l < maxn(); l++) {
                aflag[i][l] = true;
            }

        }

        for (int j = 0; j < maxn(); j++) {
            aflag[j][j] = false;
        }

        for (int k = 0; k <= getMaxCardinality(); k++) {
            for (int i1 = 0; i1 <= maxn() - 2; i1++) {
                for (int l1 = i1 + 1; l1 < maxn(); l1++) {
                    if (!aflag[i1][l1]) {
                        continue;
                    }
                    SeparationSet separationset = existsSepSet(i1, l1, k, aflag);
                    if (separationset == null) {
                        continue;
                    }
                    aflag[i1][l1] = false;
                    aflag[l1][i1] = false;
                    aseparationset[i1][l1] = separationset;
                    aseparationset[l1][i1] = separationset;
                    System.err.print((new StringBuilder()).append("I(").append(name(i1)).append(", {").toString());
                    for (int j2 = 0; j2 < k; j2++) {
                        System.err.print((new StringBuilder()).append(name(separationset.m_set[j2])).append(" ").toString());
                    }

                    System.err.print((new StringBuilder()).append("} ,").append(name(l1)).append(")\n").toString());
                }

            }

            System.err.print((new StringBuilder()).append(k).append(" ").toString());
            for (int j1 = 0; j1 < maxn(); j1++) {
                System.err.print((new StringBuilder()).append(name(j1)).append(" ").toString());
            }

            System.err.print('\n');
            for (int k1 = 0; k1 < maxn(); k1++) {
                for (int i2 = 0; i2 < maxn(); i2++) {
                    if (aflag[k1][i2]) {
                        System.err.print("X ");
                    } else {
                        System.err.print(". ");
                    }
                }

                System.err.print((new StringBuilder()).append(name(k1)).append(" ").toString());
                System.err.print('\n');
            }

        }

    }

    SeparationSet existsSepSet(int i, int j, int k, boolean aflag[][]) {
        int j1;
        SeparationSet separationset;
        separationset = new SeparationSet();
        separationset.m_set[k] = -1;
        if (k > 0) {
            separationset.m_set[0] = next(-1, i, j, aflag);
            for (int l = 1; l < k; l++) {
                separationset.m_set[l] = next(separationset.m_set[l - 1], i, j, aflag);
            }

        }
        if (k > 0) {
            j1 = maxn() - separationset.m_set[k - 1] - 1;
        } else {
            j1 = 0;
        }
_L2:
        if (j1 < 0) {
            break MISSING_BLOCK_LABEL_300;
        }
        if (isConditionalIndependent(j, i, separationset.m_set, k)) {
            return separationset;
        }
        if (k > 0) {
            separationset.m_set[k - 1] = next(separationset.m_set[k - 1], i, j, aflag);
        }
        j1 = k - 1;
_L4:
        if (j1 < 0 || separationset.m_set[j1] < maxn()) goto _L2; else goto _L1
_L1:
        for (j1 = k - 1; j1 >= 0 && separationset.m_set[j1] >= maxn(); j1--) { }
        if (j1 >= 0) goto _L3; else goto _L2
_L3:
        separationset.m_set[j1] = next(separationset.m_set[j1], i, j, aflag);
        for (int i1 = j1 + 1; i1 < k; i1++) {
            separationset.m_set[i1] = next(separationset.m_set[i1 - 1], i, j, aflag);
        }

        j1 = k - 1;
          goto _L4
        return null;
    }

    int next(int i, int j, int k, boolean aflag[][]) {
        for (i++; i < maxn() && (!aflag[j][i] || !aflag[k][i] || i == k); i++) { }
        return i;
    }

    void calcVeeNodes(boolean aflag[][], boolean aflag1[][], SeparationSet aseparationset[][]) {
        for (int i = 0; i < maxn(); i++) {
            for (int k = 0; k < maxn(); k++) {
                aflag1[i][k] = false;
            }

        }

        for (int j = 0; j < maxn() - 1; j++) {
            for (int l = j + 1; l < maxn(); l++) {
                if (aflag[j][l]) {
                    continue;
                }
                for (int i1 = 0; i1 < maxn(); i1++) {
                    if ((i1 != j && i1 != l && aflag[j][i1] && aflag[l][i1]) & (!aseparationset[j][l].contains(i1))) {
                        aflag1[j][i1] = true;
                        aflag1[l][i1] = true;
                    }
                }

            }

        }

    }

    void calcArcDirections(boolean aflag[][], boolean aflag1[][]) {
        boolean flag;
        do {
            flag = false;
            for (int i = 0; i < maxn(); i++) {
                for (int j1 = 0; j1 < maxn(); j1++) {
                    if (i == j1 || !aflag1[i][j1]) {
                        continue;
                    }
                    for (int k2 = 0; k2 < maxn(); k2++) {
                        if (i != k2 && j1 != k2 && aflag[j1][k2] && !aflag[i][k2] && !aflag1[j1][k2] && !aflag1[k2][j1]) {
                            aflag1[j1][k2] = true;
                            flag = true;
                        }
                    }

                }

            }

            for (int j = 0; j < maxn(); j++) {
                for (int k1 = 0; k1 < maxn(); k1++) {
                    if (j == k1 || !aflag1[j][k1]) {
                        continue;
                    }
                    for (int l2 = 0; l2 < maxn(); l2++) {
                        if (j != l2 && k1 != l2 && aflag[j][l2] && aflag1[k1][l2] && !aflag1[j][l2] && !aflag1[l2][j]) {
                            aflag1[j][l2] = true;
                            flag = true;
                        }
                    }

                }

            }

            for (int k = 0; k < maxn(); k++) {
                for (int l1 = 0; l1 < maxn(); l1++) {
                    if (k == l1 || !aflag1[k][l1]) {
                        continue;
                    }
                    for (int i3 = 0; i3 < maxn(); i3++) {
                        if (i3 == k || i3 == l1 || !aflag1[i3][l1] || aflag[i3][k]) {
                            continue;
                        }
                        for (int k3 = 0; k3 < maxn(); k3++) {
                            if (k3 != k && k3 != l1 && k3 != i3 && aflag[k3][k] && !aflag1[k3][k] && !aflag1[k][k3] && aflag[k3][l1] && !aflag1[k3][l1] && !aflag1[l1][k3] && aflag[k3][i3] && !aflag1[k3][i3] && !aflag1[i3][k3]) {
                                aflag1[k3][l1] = true;
                                flag = true;
                            }
                        }

                    }

                }

            }

            for (int l = 0; l < maxn(); l++) {
                for (int i2 = 0; i2 < maxn(); i2++) {
                    if (l == i2 || !aflag1[i2][l]) {
                        continue;
                    }
                    for (int j3 = 0; j3 < maxn(); j3++) {
                        if (j3 == l || j3 == i2 || !aflag[j3][i2] || aflag1[j3][i2] || aflag1[i2][j3] || !aflag[j3][l] || aflag1[j3][l] || aflag1[l][j3]) {
                            continue;
                        }
                        for (int l3 = 0; l3 < maxn(); l3++) {
                            if (l3 != l && l3 != i2 && l3 != j3 && aflag[l3][l] && !aflag1[l3][l] && !aflag1[l][l3] && aflag[l3][j3] && !aflag1[l3][j3] && !aflag1[j3][l3]) {
                                aflag1[l][l3] = true;
                                aflag1[j3][l3] = true;
                                flag = true;
                            }
                        }

                    }

                }

            }

            if (!flag) {
                for (int i1 = 0; !flag && i1 < maxn(); i1++) {
                    for (int j2 = 0; !flag && j2 < maxn(); j2++) {
                        if (aflag[i1][j2] && !aflag1[i1][j2] && !aflag1[j2][i1]) {
                            aflag1[i1][j2] = true;
                            flag = true;
                        }
                    }

                }

            }
        } while (flag);
    }

    public Enumeration listOptions() {
        Vector vector = new Vector();
        vector.addElement(new Option("\tWhen determining whether an edge exists a search is performed \n\tfor a set Z that separates the nodes. MaxCardinality determines \n\tthe maximum size of the set Z. This greatly influences the \n\tlength of the search. (default 2)", "cardinality", 1, "-cardinality <num>"));
        for (Enumeration enumeration = super.listOptions(); enumeration.hasMoreElements(); vector.addElement(enumeration.nextElement())) { }
        return vector.elements();
    }

    public void setOptions(String as[]) throws Exception {
        String s = Utils.getOption("cardinality", as);
        if (s.length() != 0) {
            setMaxCardinality(Integer.parseInt(s));
        } else {
            setMaxCardinality(2);
        }
        super.setOptions(as);
    }

    public String[] getOptions() {
        Vector vector = new Vector();
        String as[] = super.getOptions();
        for (int i = 0; i < as.length; i++) {
            vector.add(as[i]);
        }

        vector.add("-cardinality");
        vector.add((new StringBuilder()).append("").append(getMaxCardinality()).toString());
        return (String[])(String[])vector.toArray(new String[vector.size()]);
    }

    public String maxCardinalityTipText() {
        return "When determining whether an edge exists a search is performed for a set Z that separates the nodes. MaxCardinality determines the maximum size of the set Z. This greatly influences the length of the search. Default value is 2.";
    }

    public String globalInfo() {
        return "This Bayes Network learning algorithm uses conditional independence tests to find a skeleton, finds V-nodes and applies a set of rules to find the directions of the remaining arrows.";
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.8 $");
    }

    public static void main(String args[]) {
        try {
            BayesNet bayesnet = new BayesNet();
            bayesnet.setSearchAlgorithm(new ICSSearchAlgorithm());
            Instances instances = new Instances(new FileReader("C:\\eclipse\\workspace\\weka\\data\\contact-lenses.arff"));
            instances.setClassIndex(instances.numAttributes() - 1);
            bayesnet.buildClassifier(instances);
            System.out.println(bayesnet.toString());
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
