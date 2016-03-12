// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net.search;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.ParentSet;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.RevisionHandler;
import weka.core.RevisionUtils;

public class SearchAlgorithm
    implements OptionHandler, Serializable, RevisionHandler {

    static final long serialVersionUID = 0x558dbde169b03a80L;
    protected int m_nMaxNrOfParents;
    protected boolean m_bInitAsNaiveBayes;
    protected boolean m_bMarkovBlanketClassifier;

    public SearchAlgorithm() {
        m_nMaxNrOfParents = 1;
        m_bInitAsNaiveBayes = true;
        m_bMarkovBlanketClassifier = false;
    }

    protected boolean addArcMakesSense(BayesNet bayesnet, Instances instances, int i, int j) {
        if (i == j) {
            return false;
        }
        if (isArc(bayesnet, i, j)) {
            return false;
        }
        int k = instances.numAttributes();
        boolean aflag[] = new boolean[k];
        for (int l = 0; l < k; l++) {
            aflag[l] = false;
        }

        bayesnet.getParentSet(i).addParent(j, instances);
        for (int i1 = 0; i1 < k; i1++) {
            boolean flag = false;
            for (int j1 = 0; !flag && j1 < k; j1++) {
                if (aflag[j1]) {
                    continue;
                }
                boolean flag1 = true;
                for (int k1 = 0; k1 < bayesnet.getParentSet(j1).getNrOfParents(); k1++) {
                    if (!aflag[bayesnet.getParentSet(j1).getParent(k1)]) {
                        flag1 = false;
                    }
                }

                if (flag1) {
                    aflag[j1] = true;
                    flag = true;
                }
            }

            if (!flag) {
                bayesnet.getParentSet(i).deleteLastParent(instances);
                return false;
            }
        }

        bayesnet.getParentSet(i).deleteLastParent(instances);
        return true;
    }

    protected boolean reverseArcMakesSense(BayesNet bayesnet, Instances instances, int i, int j) {
        if (i == j) {
            return false;
        }
        if (!isArc(bayesnet, i, j)) {
            return false;
        }
        int k = instances.numAttributes();
        boolean aflag[] = new boolean[k];
        for (int l = 0; l < k; l++) {
            aflag[l] = false;
        }

        bayesnet.getParentSet(j).addParent(i, instances);
        for (int i1 = 0; i1 < k; i1++) {
            boolean flag = false;
            for (int j1 = 0; !flag && j1 < k; j1++) {
                if (aflag[j1]) {
                    continue;
                }
                ParentSet parentset = bayesnet.getParentSet(j1);
                boolean flag1 = true;
                for (int k1 = 0; k1 < parentset.getNrOfParents(); k1++) {
                    if (!aflag[parentset.getParent(k1)] && (j1 != i || parentset.getParent(k1) != j)) {
                        flag1 = false;
                    }
                }

                if (flag1) {
                    aflag[j1] = true;
                    flag = true;
                }
            }

            if (!flag) {
                bayesnet.getParentSet(j).deleteLastParent(instances);
                return false;
            }
        }

        bayesnet.getParentSet(j).deleteLastParent(instances);
        return true;
    }

    protected boolean isArc(BayesNet bayesnet, int i, int j) {
        for (int k = 0; k < bayesnet.getParentSet(i).getNrOfParents(); k++) {
            if (bayesnet.getParentSet(i).getParent(k) == j) {
                return true;
            }
        }

        return false;
    }

    public Enumeration listOptions() {
        return (new Vector(0)).elements();
    }

    public void setOptions(String as[]) throws Exception {
    }

    public String[] getOptions() {
        return new String[0];
    }

    public String toString() {
        return "SearchAlgorithm\n";
    }

    public void buildStructure(BayesNet bayesnet, Instances instances) throws Exception {
        if (m_bInitAsNaiveBayes) {
            int i = instances.classIndex();
            for (int j = 0; j < instances.numAttributes(); j++) {
                if (j != i) {
                    bayesnet.getParentSet(j).addParent(i, instances);
                }
            }

        }
        search(bayesnet, instances);
        if (m_bMarkovBlanketClassifier) {
            doMarkovBlanketCorrection(bayesnet, instances);
        }
    }

    protected void search(BayesNet bayesnet, Instances instances) throws Exception {
    }

    protected void doMarkovBlanketCorrection(BayesNet bayesnet, Instances instances) {
        int i = instances.classIndex();
        ParentSet parentset = new ParentSet();
        int j = 0;
        parentset.addParent(i, instances);
        while (j != parentset.getNrOfParents())  {
            j = parentset.getNrOfParents();
            int k = 0;
            while (k < j)  {
                int i1 = parentset.getParent(k);
                ParentSet parentset1 = bayesnet.getParentSet(i1);
                for (int k1 = 0; k1 < parentset1.getNrOfParents(); k1++) {
                    if (!parentset.contains(parentset1.getParent(k1))) {
                        parentset.addParent(parentset1.getParent(k1), instances);
                    }
                }

                k++;
            }
        }
        for (int l = 0; l < instances.numAttributes(); l++) {
            boolean flag = l == i || bayesnet.getParentSet(l).contains(i) || bayesnet.getParentSet(i).contains(l);
            for (int j1 = 0; !flag && j1 < instances.numAttributes(); j1++) {
                flag = bayesnet.getParentSet(j1).contains(l) && bayesnet.getParentSet(j1).contains(i);
            }

            if (flag) {
                continue;
            }
            if (parentset.contains(l)) {
                if (bayesnet.getParentSet(i).getCardinalityOfParents() < 1024) {
                    bayesnet.getParentSet(i).addParent(l, instances);
                }
            } else {
                bayesnet.getParentSet(l).addParent(i, instances);
            }
        }

    }

    protected void setMarkovBlanketClassifier(boolean flag) {
        m_bMarkovBlanketClassifier = flag;
    }

    protected boolean getMarkovBlanketClassifier() {
        return m_bMarkovBlanketClassifier;
    }

    public String maxNrOfParentsTipText() {
        return "Set the maximum number of parents a node in the Bayes net can have. When initialized as Naive Bayes, setting this parameter to 1 results in a Naive Bayes classifier. When set to 2, a Tree Augmented Bayes Network (TAN) is learned, and when set >2, a Bayes Net Augmented Bayes Network (BAN) is learned. By setting it to a value much larger than the number of nodes in the network (the default of 100000 pretty much guarantees this), no restriction on the number of parents is enforced";
    }

    public String initAsNaiveBayesTipText() {
        return "When set to true (default), the initial network used for structure learning is a Naive Bayes Network, that is, a network with an arrow from the classifier node to each other node. When set to false, an empty network is used as initial network structure";
    }

    protected String markovBlanketClassifierTipText() {
        return "When set to true (default is false), after a network structure is learned a Markov Blanket correction is applied to the network structure. This ensures that all nodes in the network are part of the Markov blanket of the classifier node.";
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.9 $");
    }
}
