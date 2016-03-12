// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes;

import weka.classifiers.UpdateableClassifier;
import weka.core.*;

// Referenced classes of package weka.classifiers.bayes:
//            NaiveBayesMultinomial

public class NaiveBayesMultinomialUpdateable extends NaiveBayesMultinomial
    implements UpdateableClassifier {

    private static final long serialVersionUID = 0x9c04d576f618f46eL;
    protected double m_wordsPerClass[];


    public String globalInfo() {
        return (new StringBuilder()).append(super.globalInfo()).append("\n\n").append("Incremental version of the algorithm.").toString();
    }

    public void buildClassifier(Instances instances) throws Exception {
        getCapabilities().testWithFail(instances);
        instances = new Instances(instances);
        instances.deleteWithMissingClass();
        m_headerInfo = new Instances(instances, 0);
        m_numClasses = instances.numClasses();
        m_numAttributes = instances.numAttributes();
        m_probOfWordGivenClass = new double[m_numClasses][];
        m_wordsPerClass = new double[m_numClasses];
        m_probOfClass = new double[m_numClasses];
        double d = 1.0D;
        for (int i = 0; i < m_numClasses; i++) {
            m_probOfWordGivenClass[i] = new double[m_numAttributes];
            m_probOfClass[i] = d;
            m_wordsPerClass[i] = d * (double)m_numAttributes;
            for (int k = 0; k < m_numAttributes; k++) {
                m_probOfWordGivenClass[i][k] = d;
            }

        }

        for (int j = 0; j < instances.numInstances(); j++) {
            updateClassifier(instances.instance(j));
        }

    }

    public void updateClassifier(Instance instance) throws Exception {
        int i = (int)instance.value(instance.classIndex());
        m_probOfClass[i] += instance.weight();
        for (int j = 0; j < instance.numValues(); j++) {
            if (instance.index(j) == instance.classIndex() || instance.isMissing(j)) {
                continue;
            }
            double d = instance.valueSparse(j) * instance.weight();
            if (d < 0.0D) {
                throw new Exception("Numeric attribute values must all be greater or equal to zero.");
            }
            m_wordsPerClass[i] += d;
            m_probOfWordGivenClass[i][instance.index(j)] += d;
        }

    }

    public double[] distributionForInstance(Instance instance) throws Exception {
        double ad[] = new double[m_numClasses];
        double ad1[] = new double[m_numClasses];
        for (int i = 0; i < m_numClasses; i++) {
            ad1[i] += Math.log(m_probOfClass[i]);
            int j = 0;
            for (int k = 0; k < instance.numValues(); k++) {
                if (instance.index(k) != instance.classIndex()) {
                    double d1 = instance.valueSparse(k);
                    j = (int)((double)j + d1);
                    ad1[i] += d1 * Math.log(m_probOfWordGivenClass[i][instance.index(k)]);
                }
            }

            ad1[i] -= (double)j * Math.log(m_wordsPerClass[i]);
        }

        double d = ad1[Utils.maxIndex(ad1)];
        for (int l = 0; l < m_numClasses; l++) {
            ad[l] = Math.exp(ad1[l] - d);
        }

        Utils.normalize(ad);
        return ad;
    }

    public String toString() {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("The independent probability of a class\n");
        stringbuffer.append("--------------------------------------\n");
        for (int i = 0; i < m_numClasses; i++) {
            stringbuffer.append(m_headerInfo.classAttribute().value(i)).append("\t").append(Double.toString(m_probOfClass[i])).append("\n");
        }

        stringbuffer.append("\nThe probability of a word given the class\n");
        stringbuffer.append("-----------------------------------------\n\t");
        for (int j = 0; j < m_numClasses; j++) {
            stringbuffer.append(m_headerInfo.classAttribute().value(j)).append("\t");
        }

        stringbuffer.append("\n");
        for (int k = 0; k < m_numAttributes; k++) {
            stringbuffer.append(m_headerInfo.attribute(k).name()).append("\t");
            for (int l = 0; l < m_numClasses; l++) {
                stringbuffer.append(Double.toString(Math.exp(m_probOfWordGivenClass[l][k]))).append("\t");
            }

            stringbuffer.append("\n");
        }

        return stringbuffer.toString();
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.3 $");
    }

    public static void main(String args[]) {
        runClassifier(new NaiveBayesMultinomialUpdateable(), args);
    }
}
