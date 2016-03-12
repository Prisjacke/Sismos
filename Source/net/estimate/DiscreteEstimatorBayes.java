// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net.estimate;

import java.io.PrintStream;
import weka.classifiers.bayes.net.search.local.Scoreable;
import weka.core.RevisionUtils;
import weka.core.Statistics;
import weka.core.Utils;
import weka.estimators.DiscreteEstimator;
import weka.estimators.Estimator;

public class DiscreteEstimatorBayes extends Estimator
    implements Scoreable {

    static final long serialVersionUID = 0x3a801c2d0182638cL;
    protected double m_Counts[];
    protected double m_SumOfCounts;
    protected int m_nSymbols;
    protected double m_fPrior;

    public DiscreteEstimatorBayes(int i, double d) {
        m_nSymbols = 0;
        m_fPrior = 0.0D;
        m_fPrior = d;
        m_nSymbols = i;
        m_Counts = new double[m_nSymbols];
        for (int j = 0; j < m_nSymbols; j++) {
            m_Counts[j] = m_fPrior;
        }

        m_SumOfCounts = m_fPrior * (double)m_nSymbols;
    }

    public void addValue(double d, double d1) {
        m_Counts[(int)d] += d1;
        m_SumOfCounts += d1;
    }

    public double getProbability(double d) {
        if (m_SumOfCounts == 0.0D) {
            return 0.0D;
        } else {
            return m_Counts[(int)d] / m_SumOfCounts;
        }
    }

    public double getCount(double d) {
        if (m_SumOfCounts == 0.0D) {
            return 0.0D;
        } else {
            return m_Counts[(int)d];
        }
    }

    public int getNumSymbols() {
        return m_Counts != null ? m_Counts.length : 0;
    }

    public double logScore(int i, int j) {
        double d = 0.0D;
        switch (i) {
        default:
            break;

        case 0: // '\0'
            for (int k = 0; k < m_nSymbols; k++) {
                d += Statistics.lnGamma(m_Counts[k]);
            }

            d -= Statistics.lnGamma(m_SumOfCounts);
            if (m_fPrior != 0.0D) {
                d -= (double)m_nSymbols * Statistics.lnGamma(m_fPrior);
                d += Statistics.lnGamma((double)m_nSymbols * m_fPrior);
            }
            break;

        case 1: // '\001'
            for (int l = 0; l < m_nSymbols; l++) {
                d += Statistics.lnGamma(m_Counts[l]);
            }

            d -= Statistics.lnGamma(m_SumOfCounts);
            d -= (double)m_nSymbols * Statistics.lnGamma(1.0D / (double)(m_nSymbols * j));
            d += Statistics.lnGamma(1.0D / (double)j);
            break;

        case 2: // '\002'
        case 3: // '\003'
        case 4: // '\004'
            for (int i1 = 0; i1 < m_nSymbols; i1++) {
                double d1 = getProbability(i1);
                d += m_Counts[i1] * Math.log(d1);
            }

            break;
        }
        return d;
    }

    public String toString() {
        String s = "Discrete Estimator. Counts = ";
        if (m_SumOfCounts > 1.0D) {
            for (int i = 0; i < m_Counts.length; i++) {
                s = (new StringBuilder()).append(s).append(" ").append(Utils.doubleToString(m_Counts[i], 2)).toString();
            }

            s = (new StringBuilder()).append(s).append("  (Total = ").append(Utils.doubleToString(m_SumOfCounts, 2)).append(")\n").toString();
        } else {
            for (int j = 0; j < m_Counts.length; j++) {
                s = (new StringBuilder()).append(s).append(" ").append(m_Counts[j]).toString();
            }

            s = (new StringBuilder()).append(s).append("  (Total = ").append(m_SumOfCounts).append(")\n").toString();
        }
        return s;
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.7 $");
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
