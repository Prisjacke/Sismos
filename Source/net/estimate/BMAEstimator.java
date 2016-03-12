// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net.estimate;

import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.ParentSet;
import weka.classifiers.bayes.net.search.local.K2;
import weka.core.*;

// Referenced classes of package weka.classifiers.bayes.net.estimate:
//            SimpleEstimator, DiscreteEstimatorBayes, DiscreteEstimatorFullBayes

public class BMAEstimator extends SimpleEstimator {

    static final long serialVersionUID = 0xe6619709b8dc26a3L;
    protected boolean m_bUseK2Prior;

    public BMAEstimator() {
        m_bUseK2Prior = false;
    }

    public String globalInfo() {
        return "BMAEstimator estimates conditional probability tables of a Bayes network using Bayes Model Averaging (BMA).";
    }

    public void estimateCPTs(BayesNet bayesnet) throws Exception {
        initCPTs(bayesnet);
        Instances instances = bayesnet.m_Instances;
        for (int i = 0; i < instances.numAttributes(); i++) {
            if (bayesnet.getParentSet(i).getNrOfParents() > 1) {
                throw new Exception("Cannot handle networks with nodes with more than 1 parent (yet).");
            }
        }

        BayesNet bayesnet1 = new BayesNet();
        K2 k2 = new K2();
        k2.setInitAsNaiveBayes(false);
        k2.setMaxNrOfParents(0);
        bayesnet1.setSearchAlgorithm(k2);
        bayesnet1.buildClassifier(instances);
        BayesNet bayesnet2 = new BayesNet();
        k2.setInitAsNaiveBayes(true);
        k2.setMaxNrOfParents(1);
        bayesnet2.setSearchAlgorithm(k2);
        bayesnet2.buildClassifier(instances);
        for (int j = 0; j < instances.numAttributes(); j++) {
            if (j == instances.classIndex()) {
                continue;
            }
            double d = 0.0D;
            double d1 = 0.0D;
            int l = instances.attribute(j).numValues();
            if (m_bUseK2Prior) {
                for (int i1 = 0; i1 < l; i1++) {
                    d += Statistics.lnGamma(1.0D + ((DiscreteEstimatorBayes)bayesnet1.m_Distributions[j][0]).getCount(i1)) - Statistics.lnGamma(1.0D);
                }

                d += Statistics.lnGamma(l) - Statistics.lnGamma(l + instances.numInstances());
                for (int j1 = 0; j1 < bayesnet.getParentSet(j).getCardinalityOfParents(); j1++) {
                    int j2 = 0;
                    for (int i3 = 0; i3 < l; i3++) {
                        double d2 = ((DiscreteEstimatorBayes)bayesnet2.m_Distributions[j][j1]).getCount(i3);
                        d1 += Statistics.lnGamma(1.0D + d2) - Statistics.lnGamma(1.0D);
                        j2 = (int)((double)j2 + d2);
                    }

                    d1 += Statistics.lnGamma(l) - Statistics.lnGamma(l + j2);
                }

            } else {
                for (int k1 = 0; k1 < l; k1++) {
                    d += Statistics.lnGamma(1.0D / (double)l + ((DiscreteEstimatorBayes)bayesnet1.m_Distributions[j][0]).getCount(k1)) - Statistics.lnGamma(1.0D / (double)l);
                }

                d += Statistics.lnGamma(1.0D) - Statistics.lnGamma(1 + instances.numInstances());
                int l1 = bayesnet.getParentSet(j).getCardinalityOfParents();
                for (int l2 = 0; l2 < l1; l2++) {
                    int j3 = 0;
                    for (int k3 = 0; k3 < l; k3++) {
                        double d3 = ((DiscreteEstimatorBayes)bayesnet2.m_Distributions[j][l2]).getCount(k3);
                        d1 += Statistics.lnGamma(1.0D / (double)(l * l1) + d3) - Statistics.lnGamma(1.0D / (double)(l * l1));
                        j3 = (int)((double)j3 + d3);
                    }

                    d1 += Statistics.lnGamma(1.0D) - Statistics.lnGamma(1 + j3);
                }

            }
            if (d < d1) {
                d1 -= d;
                d = 0.0D;
                d = 1.0D / (1.0D + Math.exp(d1));
                d1 = Math.exp(d1) / (1.0D + Math.exp(d1));
            } else {
                d -= d1;
                d1 = 0.0D;
                d1 = 1.0D / (1.0D + Math.exp(d));
                d = Math.exp(d) / (1.0D + Math.exp(d));
            }
            for (int i2 = 0; i2 < bayesnet.getParentSet(j).getCardinalityOfParents(); i2++) {
                bayesnet.m_Distributions[j][i2] = new DiscreteEstimatorFullBayes(instances.attribute(j).numValues(), d, d1, (DiscreteEstimatorBayes)bayesnet1.m_Distributions[j][0], (DiscreteEstimatorBayes)bayesnet2.m_Distributions[j][i2], m_fAlpha);
            }

        }

        int k = instances.classIndex();
        bayesnet.m_Distributions[k][0] = bayesnet1.m_Distributions[k][0];
    }

    public void updateClassifier(BayesNet bayesnet, Instance instance) throws Exception {
        throw new Exception("updateClassifier does not apply to BMA estimator");
    }

    public void initCPTs(BayesNet bayesnet) throws Exception {
        int i = 1;
        for (int j = 0; j < bayesnet.m_Instances.numAttributes(); j++) {
            if (bayesnet.getParentSet(j).getCardinalityOfParents() > i) {
                i = bayesnet.getParentSet(j).getCardinalityOfParents();
            }
        }

        bayesnet.m_Distributions = new weka.estimators.Estimator[bayesnet.m_Instances.numAttributes()][i];
    }

    public boolean isUseK2Prior() {
        return m_bUseK2Prior;
    }

    public void setUseK2Prior(boolean flag) {
        m_bUseK2Prior = flag;
    }

    public Enumeration listOptions() {
        Vector vector = new Vector(1);
        vector.addElement(new Option("\tWhether to use K2 prior.\n", "k2", 0, "-k2"));
        for (Enumeration enumeration = super.listOptions(); enumeration.hasMoreElements(); vector.addElement(enumeration.nextElement())) { }
        return vector.elements();
    }

    public void setOptions(String as[]) throws Exception {
        setUseK2Prior(Utils.getFlag("k2", as));
        super.setOptions(as);
    }

    public String[] getOptions() {
        String as[] = super.getOptions();
        String as1[] = new String[1 + as.length];
        int i = 0;
        if (isUseK2Prior()) {
            as1[i++] = "-k2";
        }
        for (int j = 0; j < as.length; j++) {
            as1[i++] = as[j];
        }

        while (i < as1.length)  {
            as1[i++] = "";
        }
        return as1;
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.8 $");
    }
}
