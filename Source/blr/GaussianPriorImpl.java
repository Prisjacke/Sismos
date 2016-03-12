// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.blr;

import weka.classifiers.bayes.BayesianLogisticRegression;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.RevisionUtils;

// Referenced classes of package weka.classifiers.bayes.blr:
//            Prior

public class GaussianPriorImpl extends Prior {

    private static final long serialVersionUID = 0xd66d3112d73e3cc9L;


    public double update(int i, Instances instances, double d, double d1, double ad[], 
            double d2) {
        double d3 = 0.0D;
        double d4 = 0.0D;
        double d5 = 0.0D;
        m_Instances = instances;
        Beta = d;
        Hyperparameter = d1;
        Delta = d2;
        R = ad;
        for (int j = 0; j < m_Instances.numInstances(); j++) {
            Instance instance = m_Instances.instance(j);
            if (instance.value(i) != 0.0D) {
                d3 += instance.value(i) * BayesianLogisticRegression.classSgn(instance.classValue()) * (0.0D - 1.0D / (1.0D + Math.exp(R[j])));
                d4 += instance.value(i) * instance.value(i) * BayesianLogisticRegression.bigF(R[j], Delta * Math.abs(instance.value(i)));
            }
        }

        d3 += (2D * Beta) / Hyperparameter;
        d4 += 2D / Hyperparameter;
        d5 = d3 / d4;
        return 0.0D - d5;
    }

    public void computeLoglikelihood(double ad[], Instances instances) {
        super.computelogLikelihood(ad, instances);
    }

    public void computePenalty(double ad[], double ad1[]) {
        penalty = 0.0D;
        for (int i = 0; i < ad.length; i++) {
            penalty += Math.log(Math.sqrt(ad1[i])) + Math.log(6.2831853071795862D) / 2D + (ad[i] * ad[i]) / (2D * ad1[i]);
        }

        penalty = 0.0D - penalty;
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.2 $");
    }
}
