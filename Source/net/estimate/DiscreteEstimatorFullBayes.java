// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net.estimate;

import java.io.PrintStream;
import weka.core.RevisionUtils;
import weka.estimators.DiscreteEstimator;

// Referenced classes of package weka.classifiers.bayes.net.estimate:
//            DiscreteEstimatorBayes

public class DiscreteEstimatorFullBayes extends DiscreteEstimatorBayes {

    static final long serialVersionUID = 0x5e056dd6409e1d05L;

    public DiscreteEstimatorFullBayes(int i, double d, double d1, DiscreteEstimatorBayes discreteestimatorbayes, DiscreteEstimatorBayes discreteestimatorbayes1, 
            double d2) {
        super(i, d2);
        m_SumOfCounts = 0.0D;
        for (int j = 0; j < m_nSymbols; j++) {
            double d3 = discreteestimatorbayes.getProbability(j);
            double d4 = discreteestimatorbayes1.getProbability(j);
            m_Counts[j] = d * d3 + d1 * d4;
            m_SumOfCounts += m_Counts[j];
        }

    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.3 $");
    }

    public static void main(String args[]) {
        if (args.length == 0) {
            System.out.println("Please specify a set of instances.");
            return;
        }
        try {
            int i = Integer.parseInt(args[0]);
            int l = i;
            for (int i1 = 1; i1 < args.length; i1++) {
                int j = Integer.parseInt(args[i1]);
                if (j > l) {
                    l = j;
                }
            }

            DiscreteEstimator discreteestimator = new DiscreteEstimator(l + 1, true);
            for (int j1 = 0; j1 < args.length; j1++) {
                int k = Integer.parseInt(args[j1]);
                System.out.println(discreteestimator);
                System.out.println((new StringBuilder()).append("Prediction for ").append(k).append(" = ").append(discreteestimator.getProbability(k)).toString());
                discreteestimator.addValue(k, 1.0D);
            }

        }
        catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
        return;
    }
}
