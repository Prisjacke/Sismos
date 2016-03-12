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

public class LaplacePriorImpl extends Prior {

    private static final long serialVersionUID = 0x20a99503aa93417fL;
    Instances m_Instances;
    double Beta;
    double Hyperparameter;
    double DeltaUpdate;
    double R[];
    double Delta;


    public double update(int i, Instances instances, double d, double d1, double ad[], 
            double d2) {
        double d3 = 0.0D;
        double d7 = 0.0D;
        DeltaUpdate = 0.0D;
        m_Instances = instances;
        Beta = d;
        Hyperparameter = d1;
        R = ad;
        Delta = d2;
        if (Beta == 0.0D) {
            double d4 = 1.0D;
            DeltaUpdate = laplaceUpdate(i, d4);
            if (DeltaUpdate <= 0.0D) {
                double d5 = -1D;
                DeltaUpdate = laplaceUpdate(i, d5);
                if (DeltaUpdate >= 0.0D) {
                    DeltaUpdate = 0.0D;
                }
            }
        } else {
            double d6 = Beta / Math.abs(Beta);
            DeltaUpdate = laplaceUpdate(i, d6);
            double d8 = Beta + DeltaUpdate;
            d8 /= Math.abs(d8);
            if (d8 < 0.0D) {
                DeltaUpdate = 0.0D - Beta;
            }
        }
        return DeltaUpdate;
    }

    public double laplaceUpdate(int i, double d) {
        double d1 = 0.0D;
        double d2 = 0.0D;
        double d3 = 0.0D;
        for (int j = 0; j < m_Instances.numInstances(); j++) {
            Instance instance = m_Instances.instance(j);
            if (instance.value(i) != 0.0D) {
                d2 += instance.value(i) * BayesianLogisticRegression.classSgn(instance.classValue()) * (1.0D / (1.0D + Math.exp(R[j])));
                d3 += instance.value(i) * instance.value(i) * BayesianLogisticRegression.bigF(R[j], Delta * instance.value(i));
            }
        }

        d2 -= Math.sqrt(2D / Hyperparameter) * d;
        if (d3 != 0.0D) {
            d1 = d2 / d3;
        }
        return d1;
    }

    public void computeLogLikelihood(double ad[], Instances instances) {
        super.computelogLikelihood(ad, instances);
    }

    public void computePenalty(double ad[], double ad1[]) {
        penalty = 0.0D;
        double d = 0.0D;
        for (int i = 0; i < ad.length; i++) {
            double d1 = Math.sqrt(ad1[i]);
            penalty += (Math.log(2D) - Math.log(d1)) + d1 * Math.abs(ad[i]);
        }

        penalty = 0.0D - penalty;
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.2 $");
    }
}
