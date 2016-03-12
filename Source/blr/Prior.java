// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.blr;

import java.io.Serializable;
import weka.classifiers.bayes.BayesianLogisticRegression;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.RevisionHandler;

public abstract class Prior
    implements Serializable, RevisionHandler {

    protected Instances m_Instances;
    protected double Beta;
    protected double Hyperparameter;
    protected double DeltaUpdate;
    protected double R[];
    protected double Delta;
    protected double log_posterior;
    protected double log_likelihood;
    protected double penalty;

    public Prior() {
        Beta = 0.0D;
        Hyperparameter = 0.0D;
        Delta = 0.0D;
        log_posterior = 0.0D;
        log_likelihood = 0.0D;
        penalty = 0.0D;
    }

    public double update(int i, Instances instances, double d, double d1, double ad[], 
            double d2) {
        return 0.0D;
    }

    public void computelogLikelihood(double ad[], Instances instances) {
        log_likelihood = 0.0D;
        for (int i = 0; i < instances.numInstances(); i++) {
            Instance instance = instances.instance(i);
            double d = 0.0D;
            for (int j = 0; j < instance.numAttributes(); j++) {
                if (instance.value(j) != 0.0D) {
                    d += ad[j] * instance.value(j) * instance.value(j);
                }
            }

            d *= BayesianLogisticRegression.classSgn(instance.classValue());
            log_likelihood += Math.log(1.0D + Math.exp(0.0D - d));
        }

        log_likelihood = 0.0D - log_likelihood;
    }

    public void computePenalty(double ad[], double ad1[]) {
    }

    public double getLoglikelihood() {
        return log_likelihood;
    }

    public double getLogPosterior() {
        log_posterior = log_likelihood + penalty;
        return log_posterior;
    }

    public double getPenalty() {
        return penalty;
    }
}
