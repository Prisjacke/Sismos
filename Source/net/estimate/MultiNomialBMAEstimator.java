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
import weka.estimators.Estimator;

// Referenced classes of package weka.classifiers.bayes.net.estimate:
//            BayesNetEstimator, DiscreteEstimatorBayes, DiscreteEstimatorFullBayes

public class MultiNomialBMAEstimator extends BayesNetEstimator {

    static final long serialVersionUID = 0x739c9cc32036b289L;
    protected boolean m_bUseK2Prior;

    public MultiNomialBMAEstimator() {
        m_bUseK2Prior = true;
    }

    public String globalInfo() {
        return "Multinomial BMA Estimator.";
    }

    public void estimateCPTs(BayesNet bayesnet) throws Exception {
        initCPTs(bayesnet);
        for (int i = 0; i < bayesnet.m_Instances.numAttributes(); i++) {
            if (bayesnet.getParentSet(i).getNrOfParents() > 1) {
                throw new Exception("Cannot handle networks with nodes with more than 1 parent (yet).");
            }
        }

        Instances instances;
        for (instances = new Instances(bayesnet.m_Instances); instances.numInstances() > 0; instances.delete(0)) { }
        for (int j = instances.numAttributes() - 1; j >= 0; j--) {
            if (j != instances.classIndex()) {
                FastVector fastvector = new FastVector();
                fastvector.addElement("0");
                fastvector.addElement("1");
                Attribute attribute = new Attribute(instances.attribute(j).name(), fastvector);
                instances.deleteAttributeAt(j);
                instances.insertAttributeAt(attribute, j);
            }
        }

        for (int k = 0; k < bayesnet.m_Instances.numInstances(); k++) {
            Instance instance = bayesnet.m_Instances.instance(k);
            Instance instance1 = new Instance(instances.numAttributes());
            for (int l = 0; l < instances.numAttributes(); l++) {
                if (l != instances.classIndex()) {
                    if (instance.value(l) > 0.0D) {
                        instance1.setValue(l, 1.0D);
                    }
                } else {
                    instance1.setValue(l, instance.value(l));
                }
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
        for (int i1 = 0; i1 < instances.numAttributes(); i1++) {
            if (i1 == instances.classIndex()) {
                continue;
            }
            double d = 0.0D;
            double d1 = 0.0D;
            int k1 = instances.attribute(i1).numValues();
            if (m_bUseK2Prior) {
                for (int l1 = 0; l1 < k1; l1++) {
                    d += Statistics.lnGamma(1.0D + ((DiscreteEstimatorBayes)bayesnet1.m_Distributions[i1][0]).getCount(l1)) - Statistics.lnGamma(1.0D);
                }

                d += Statistics.lnGamma(k1) - Statistics.lnGamma(k1 + instances.numInstances());
                for (int i2 = 0; i2 < bayesnet.getParentSet(i1).getCardinalityOfParents(); i2++) {
                    int j3 = 0;
                    for (int l3 = 0; l3 < k1; l3++) {
                        double d2 = ((DiscreteEstimatorBayes)bayesnet2.m_Distributions[i1][i2]).getCount(l3);
                        d1 += Statistics.lnGamma(1.0D + d2) - Statistics.lnGamma(1.0D);
                        j3 = (int)((double)j3 + d2);
                    }

                    d1 += Statistics.lnGamma(k1) - Statistics.lnGamma(k1 + j3);
                }

            } else {
                for (int j2 = 0; j2 < k1; j2++) {
                    d += Statistics.lnGamma(1.0D / (double)k1 + ((DiscreteEstimatorBayes)bayesnet1.m_Distributions[i1][0]).getCount(j2)) - Statistics.lnGamma(1.0D / (double)k1);
                }

                d += Statistics.lnGamma(1.0D) - Statistics.lnGamma(1 + instances.numInstances());
                int l2 = bayesnet.getParentSet(i1).getCardinalityOfParents();
                for (int k3 = 0; k3 < l2; k3++) {
                    int i4 = 0;
                    for (int j4 = 0; j4 < k1; j4++) {
                        double d3 = ((DiscreteEstimatorBayes)bayesnet2.m_Distributions[i1][k3]).getCount(j4);
                        d1 += Statistics.lnGamma(1.0D / (double)(k1 * l2) + d3) - Statistics.lnGamma(1.0D / (double)(k1 * l2));
                        i4 = (int)((double)i4 + d3);
                    }

                    d1 += Statistics.lnGamma(1.0D) - Statistics.lnGamma(1 + i4);
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
            for (int i3 = 0; i3 < bayesnet.getParentSet(i1).getCardinalityOfParents(); i3++) {
                bayesnet.m_Distributions[i1][i3] = new DiscreteEstimatorFullBayes(instances.attribute(i1).numValues(), d, d1, (DiscreteEstimatorBayes)bayesnet1.m_Distributions[i1][0], (DiscreteEstimatorBayes)bayesnet2.m_Distributions[i1][i3], m_fAlpha);
            }

        }

        int j1 = instances.classIndex();
        bayesnet.m_Distributions[j1][0] = bayesnet1.m_Distributions[j1][0];
    }

    public void updateClassifier(BayesNet bayesnet, Instance instance) throws Exception {
        throw new Exception("updateClassifier does not apply to BMA estimator");
    }

    public void initCPTs(BayesNet bayesnet) throws Exception {
        bayesnet.m_Distributions = new Estimator[bayesnet.m_Instances.numAttributes()][2];
    }

    public boolean isUseK2Prior() {
        return m_bUseK2Prior;
    }

    public void setUseK2Prior(boolean flag) {
        m_bUseK2Prior = flag;
    }

    public double[] distributionForInstance(BayesNet bayesnet, Instance instance) throws Exception {
        Instances instances = bayesnet.m_Instances;
        int i = instances.numClasses();
        double ad[] = new double[i];
        for (int j = 0; j < i; j++) {
            ad[j] = 1.0D;
        }

        for (int k = 0; k < i; k++) {
            double d1 = 0.0D;
            for (int j1 = 0; j1 < instances.numAttributes(); j1++) {
                double d2 = 0.0D;
                for (int k1 = 0; k1 < bayesnet.getParentSet(j1).getNrOfParents(); k1++) {
                    int l1 = bayesnet.getParentSet(j1).getParent(k1);
                    if (l1 == instances.classIndex()) {
                        d2 = d2 * (double)i + (double)k;
                    } else {
                        d2 = d2 * (double)instances.attribute(l1).numValues() + instance.value(l1);
                    }
                }

                if (j1 == instances.classIndex()) {
                    d1 += Math.log(bayesnet.m_Distributions[j1][(int)d2].getProbability(k));
                } else {
                    d1 += instance.value(j1) * Math.log(bayesnet.m_Distributions[j1][(int)d2].getProbability(instance.value(1)));
                }
            }

            ad[k] += d1;
        }

        double d = ad[0];
        for (int l = 0; l < i; l++) {
            if (ad[l] > d) {
                d = ad[l];
            }
        }

        for (int i1 = 0; i1 < i; i1++) {
            ad[i1] = Math.exp(ad[i1] - d);
        }

        Utils.normalize(ad);
        return ad;
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
