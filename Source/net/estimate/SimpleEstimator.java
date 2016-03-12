// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net.estimate;

import java.util.Enumeration;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.ParentSet;
import weka.core.*;
import weka.estimators.Estimator;

// Referenced classes of package weka.classifiers.bayes.net.estimate:
//            BayesNetEstimator, DiscreteEstimatorBayes

public class SimpleEstimator extends BayesNetEstimator {

    static final long serialVersionUID = 0x5187fc450732addcL;


    public String globalInfo() {
        return "SimpleEstimator is used for estimating the conditional probability tables of a Bayes network once the structure has been learned. Estimates probabilities directly from data.";
    }

    public void estimateCPTs(BayesNet bayesnet) throws Exception {
        initCPTs(bayesnet);
        Instance instance;
        for (Enumeration enumeration = bayesnet.m_Instances.enumerateInstances(); enumeration.hasMoreElements(); updateClassifier(bayesnet, instance)) {
            instance = (Instance)enumeration.nextElement();
        }

    }

    public void updateClassifier(BayesNet bayesnet, Instance instance) throws Exception {
        for (int i = 0; i < bayesnet.m_Instances.numAttributes(); i++) {
            double d = 0.0D;
            for (int j = 0; j < bayesnet.getParentSet(i).getNrOfParents(); j++) {
                int k = bayesnet.getParentSet(i).getParent(j);
                d = d * (double)bayesnet.m_Instances.attribute(k).numValues() + instance.value(k);
            }

            bayesnet.m_Distributions[i][(int)d].addValue(instance.value(i), instance.weight());
        }

    }

    public void initCPTs(BayesNet bayesnet) throws Exception {
        Instances instances = bayesnet.m_Instances;
        int i = 1;
        for (int j = 0; j < instances.numAttributes(); j++) {
            if (bayesnet.getParentSet(j).getCardinalityOfParents() > i) {
                i = bayesnet.getParentSet(j).getCardinalityOfParents();
            }
        }

        bayesnet.m_Distributions = new Estimator[instances.numAttributes()][i];
        for (int k = 0; k < instances.numAttributes(); k++) {
            for (int l = 0; l < bayesnet.getParentSet(k).getCardinalityOfParents(); l++) {
                bayesnet.m_Distributions[k][l] = new DiscreteEstimatorBayes(instances.attribute(k).numValues(), m_fAlpha);
            }

        }

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
                    d1 += Math.log(bayesnet.m_Distributions[j1][(int)d2].getProbability(instance.value(j1)));
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

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.6 $");
    }
}
