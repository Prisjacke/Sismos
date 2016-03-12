// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net.search.local;

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.ParentSet;
import weka.core.*;

// Referenced classes of package weka.classifiers.bayes.net.search.local:
//            LocalScoreSearchAlgorithm

public class SimulatedAnnealing extends LocalScoreSearchAlgorithm
    implements TechnicalInformationHandler {

    static final long serialVersionUID = 0x607a4eca6985b7a7L;
    double m_fTStart;
    double m_fDelta;
    int m_nRuns;
    boolean m_bUseArcReversal;
    int m_nSeed;
    Random m_random;

    public SimulatedAnnealing() {
        m_fTStart = 10D;
        m_fDelta = 0.999D;
        m_nRuns = 10000;
        m_bUseArcReversal = false;
        m_nSeed = 1;
    }

    public TechnicalInformation getTechnicalInformation() {
        TechnicalInformation technicalinformation = new TechnicalInformation(weka.core.TechnicalInformation.Type.PHDTHESIS);
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.AUTHOR, "R.R. Bouckaert");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.YEAR, "1995");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.TITLE, "Bayesian Belief Networks: from Construction to Inference");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.INSTITUTION, "University of Utrecht");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.ADDRESS, "Utrecht, Netherlands");
        return technicalinformation;
    }

    public void search(BayesNet bayesnet, Instances instances) throws Exception {
        m_random = new Random(m_nSeed);
        double ad[] = new double[instances.numAttributes()];
        double d = 0.0D;
        for (int i = 0; i < instances.numAttributes(); i++) {
            ad[i] = calcNodeScore(i);
            d += ad[i];
        }

        double d1 = d;
        BayesNet bayesnet1 = new BayesNet();
        bayesnet1.m_Instances = instances;
        bayesnet1.initStructure();
        copyParentSets(bayesnet1, bayesnet);
        double d2 = m_fTStart;
        for (int j = 0; j < m_nRuns; j++) {
            boolean flag = false;
            double d3 = 0.0D;
            do {
                if (flag) {
                    break;
                }
                int k = Math.abs(m_random.nextInt()) % instances.numAttributes();
                int l;
                for (l = Math.abs(m_random.nextInt()) % instances.numAttributes(); k == l; l = Math.abs(m_random.nextInt()) % instances.numAttributes()) { }
                if (isArc(bayesnet, l, k)) {
                    flag = true;
                    bayesnet.getParentSet(l).deleteParent(k, instances);
                    double d6 = calcNodeScore(l);
                    double d4 = d6 - ad[l];
                    if (d2 * Math.log((double)(Math.abs(m_random.nextInt()) % 10000) / 10000D + 1E-100D) < d4) {
                        d += d4;
                        ad[l] = d6;
                    } else {
                        bayesnet.getParentSet(l).addParent(k, instances);
                    }
                } else
                if (addArcMakesSense(bayesnet, instances, l, k)) {
                    flag = true;
                    double d7 = calcScoreWithExtraParent(l, k);
                    double d5 = d7 - ad[l];
                    if (d2 * Math.log((double)(Math.abs(m_random.nextInt()) % 10000) / 10000D + 1E-100D) < d5) {
                        bayesnet.getParentSet(l).addParent(k, instances);
                        ad[l] = d7;
                        d += d5;
                    }
                }
            } while (true);
            if (d > d1) {
                copyParentSets(bayesnet1, bayesnet);
            }
            d2 *= m_fDelta;
        }

        copyParentSets(bayesnet, bayesnet1);
    }

    void copyParentSets(BayesNet bayesnet, BayesNet bayesnet1) {
        int i = bayesnet1.getNrOfNodes();
        for (int j = 0; j < i; j++) {
            bayesnet.getParentSet(j).copy(bayesnet1.getParentSet(j));
        }

    }

    public double getDelta() {
        return m_fDelta;
    }

    public double getTStart() {
        return m_fTStart;
    }

    public int getRuns() {
        return m_nRuns;
    }

    public void setDelta(double d) {
        m_fDelta = d;
    }

    public void setTStart(double d) {
        m_fTStart = d;
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
        Vector vector = new Vector(3);
        vector.addElement(new Option("\tStart temperature", "A", 1, "-A <float>"));
        vector.addElement(new Option("\tNumber of runs", "U", 1, "-U <integer>"));
        vector.addElement(new Option("\tDelta temperature", "D", 1, "-D <float>"));
        vector.addElement(new Option("\tRandom number seed", "R", 1, "-R <seed>"));
        for (Enumeration enumeration = super.listOptions(); enumeration.hasMoreElements(); vector.addElement(enumeration.nextElement())) { }
        return vector.elements();
    }

    public void setOptions(String as[]) throws Exception {
        String s = Utils.getOption('A', as);
        if (s.length() != 0) {
            setTStart(Double.parseDouble(s));
        }
        String s1 = Utils.getOption('U', as);
        if (s1.length() != 0) {
            setRuns(Integer.parseInt(s1));
        }
        String s2 = Utils.getOption('D', as);
        if (s2.length() != 0) {
            setDelta(Double.parseDouble(s2));
        }
        String s3 = Utils.getOption('R', as);
        if (s3.length() != 0) {
            setSeed(Integer.parseInt(s3));
        }
        super.setOptions(as);
    }

    public String[] getOptions() {
        String as[] = super.getOptions();
        String as1[] = new String[8 + as.length];
        int i = 0;
        as1[i++] = "-A";
        as1[i++] = (new StringBuilder()).append("").append(getTStart()).toString();
        as1[i++] = "-U";
        as1[i++] = (new StringBuilder()).append("").append(getRuns()).toString();
        as1[i++] = "-D";
        as1[i++] = (new StringBuilder()).append("").append(getDelta()).toString();
        as1[i++] = "-R";
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
        return (new StringBuilder()).append("This Bayes Network learning algorithm uses the general purpose search method of simulated annealing to find a well scoring network structure.\n\nFor more information see:\n\n").append(getTechnicalInformation().toString()).toString();
    }

    public String TStartTipText() {
        return "Sets the start temperature of the simulated annealing search. The start temperature determines the probability that a step in the 'wrong' direction in the search space is accepted. The higher the temperature, the higher the probability of acceptance.";
    }

    public String runsTipText() {
        return "Sets the number of iterations to be performed by the simulated annealing search.";
    }

    public String deltaTipText() {
        return "Sets the factor with which the temperature (and thus the acceptance probability of steps in the wrong direction in the search space) is decreased in each iteration.";
    }

    public String seedTipText() {
        return "Initialization value for random number generator. Setting the seed allows replicability of experiments.";
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.6 $");
    }
}
