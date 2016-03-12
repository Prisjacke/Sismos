// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net.search.local;

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.ParentSet;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.Utils;

// Referenced classes of package weka.classifiers.bayes.net.search.local:
//            HillClimber

public class RepeatedHillClimber extends HillClimber {

    static final long serialVersionUID = 0xa4c428eb7ab3f7eaL;
    int m_nRuns;
    int m_nSeed;
    Random m_random;

    public RepeatedHillClimber() {
        m_nRuns = 10;
        m_nSeed = 1;
    }

    protected void search(BayesNet bayesnet, Instances instances) throws Exception {
        m_random = new Random(getSeed());
        double d1 = 0.0D;
        for (int i = 0; i < instances.numAttributes(); i++) {
            d1 += calcNodeScore(i);
        }

        double d = d1;
        BayesNet bayesnet1 = new BayesNet();
        bayesnet1.m_Instances = instances;
        bayesnet1.initStructure();
        copyParentSets(bayesnet1, bayesnet);
        for (int j = 0; j < m_nRuns; j++) {
            generateRandomNet(bayesnet, instances);
            super.search(bayesnet, instances);
            double d2 = 0.0D;
            for (int k = 0; k < instances.numAttributes(); k++) {
                d2 += calcNodeScore(k);
            }

            if (d2 > d) {
                d = d2;
                copyParentSets(bayesnet1, bayesnet);
            }
        }

        copyParentSets(bayesnet, bayesnet1);
        bayesnet1 = null;
        m_Cache = null;
    }

    void generateRandomNet(BayesNet bayesnet, Instances instances) {
        int i = instances.numAttributes();
        for (int j = 0; j < i; j++) {
            for (ParentSet parentset = bayesnet.getParentSet(j); parentset.getNrOfParents() > 0; parentset.deleteLastParent(instances)) { }
        }

        if (getInitAsNaiveBayes()) {
            int k = instances.classIndex();
            for (int i1 = 0; i1 < i; i1++) {
                if (i1 != k) {
                    bayesnet.getParentSet(i1).addParent(k, instances);
                }
            }

        }
        int l = m_random.nextInt(i * i);
        for (int j1 = 0; j1 < l; j1++) {
            int k1 = m_random.nextInt(i);
            int l1 = m_random.nextInt(i);
            if (bayesnet.getParentSet(l1).getNrOfParents() < getMaxNrOfParents() && addArcMakesSense(bayesnet, instances, l1, k1)) {
                bayesnet.getParentSet(l1).addParent(k1, instances);
            }
        }

    }

    void copyParentSets(BayesNet bayesnet, BayesNet bayesnet1) {
        int i = bayesnet1.getNrOfNodes();
        for (int j = 0; j < i; j++) {
            bayesnet.getParentSet(j).copy(bayesnet1.getParentSet(j));
        }

    }

    public int getRuns() {
        return m_nRuns;
    }

    public void setRuns(int i) {
        m_nRuns = i;
    }

    public int getSeed() {
        return m_nSeed;
    }

    public void setSeed(int i) {
        m_nSeed = i;
    }

    public Enumeration listOptions() {
        Vector vector = new Vector(4);
        vector.addElement(new Option("\tNumber of runs", "U", 1, "-U <integer>"));
        vector.addElement(new Option("\tRandom number seed", "A", 1, "-A <seed>"));
        for (Enumeration enumeration = super.listOptions(); enumeration.hasMoreElements(); vector.addElement(enumeration.nextElement())) { }
        return vector.elements();
    }

    public void setOptions(String as[]) throws Exception {
        String s = Utils.getOption('U', as);
        if (s.length() != 0) {
            setRuns(Integer.parseInt(s));
        }
        String s1 = Utils.getOption('A', as);
        if (s1.length() != 0) {
            setSeed(Integer.parseInt(s1));
        }
        super.setOptions(as);
    }

    public String[] getOptions() {
        String as[] = super.getOptions();
        String as1[] = new String[7 + as.length];
        int i = 0;
        as1[i++] = "-U";
        as1[i++] = (new StringBuilder()).append("").append(getRuns()).toString();
        as1[i++] = "-A";
        as1[i++] = (new StringBuilder()).append("").append(getSeed()).toString();
        for (int j = 0; j < as.length; j++) {
            as1[i++] = as[j];
        }

        while (i < as1.length)  {
            as1[i++] = "";
        }
        return as1;
    }

    public String globalInfo() {
        return "This Bayes Network learning algorithm repeatedly uses hill climbing starting with a randomly generated network structure and return the best structure of the various runs.";
    }

    public String runsTipText() {
        return "Sets the number of times hill climbing is performed.";
    }

    public String seedTipText() {
        return "Initialization value for random number generator. Setting the seed allows replicability of experiments.";
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.6 $");
    }
}
