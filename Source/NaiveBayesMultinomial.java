// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes;

import java.util.Enumeration;
import weka.classifiers.Classifier;
import weka.core.*;

public class NaiveBayesMultinomial extends Classifier
    implements WeightedInstancesHandler, TechnicalInformationHandler {

    static final long serialVersionUID = 0x525353f3aa97037dL;
    protected double m_probOfWordGivenClass[][];
    protected double m_probOfClass[];
    protected int m_numAttributes;
    protected int m_numClasses;
    protected double m_lnFactorialCache[] = {
        0.0D, 0.0D
    };
    protected Instances m_headerInfo;


    public String globalInfo() {
        return (new StringBuilder()).append("Class for building and using a multinomial Naive Bayes classifier. For more information see,\n\n").append(getTechnicalInformation().toString()).append("\n\n").append("The core equation for this classifier:\n\n").append("P[Ci|D] = (P[D|Ci] x P[Ci]) / P[D] (Bayes rule)\n\n").append("where Ci is class i and D is a document.").toString();
    }

    public TechnicalInformation getTechnicalInformation() {
        TechnicalInformation technicalinformation = new TechnicalInformation(weka.core.TechnicalInformation.Type.INPROCEEDINGS);
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.AUTHOR, "Andrew Mccallum and Kamal Nigam");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.YEAR, "1998");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.TITLE, "A Comparison of Event Models for Naive Bayes Text Classification");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.BOOKTITLE, "AAAI-98 Workshop on 'Learning for Text Categorization'");
        return technicalinformation;
    }

    public Capabilities getCapabilities() {
        Capabilities capabilities = super.getCapabilities();
        capabilities.enable(weka.core.Capabilities.Capability.NUMERIC_ATTRIBUTES);
        capabilities.enable(weka.core.Capabilities.Capability.NOMINAL_CLASS);
        capabilities.enable(weka.core.Capabilities.Capability.MISSING_CLASS_VALUES);
        return capabilities;
    }

    public void buildClassifier(Instances instances) throws Exception {
        getCapabilities().testWithFail(instances);
        instances = new Instances(instances);
        instances.deleteWithMissingClass();
        m_headerInfo = new Instances(instances, 0);
        m_numClasses = instances.numClasses();
        m_numAttributes = instances.numAttributes();
        m_probOfWordGivenClass = new double[m_numClasses][];
        for (int i = 0; i < m_numClasses; i++) {
            m_probOfWordGivenClass[i] = new double[m_numAttributes];
            for (int j = 0; j < m_numAttributes; j++) {
                m_probOfWordGivenClass[i][j] = 1.0D;
            }

        }

        double ad[] = new double[m_numClasses];
        double ad1[] = new double[m_numClasses];
        for (Enumeration enumeration = instances.enumerateInstances(); enumeration.hasMoreElements();) {
            Instance instance = (Instance)enumeration.nextElement();
            int k = (int)instance.value(instance.classIndex());
            ad[k] += instance.weight();
            int l = 0;
            while (l < instance.numValues())  {
                if (instance.index(l) != instance.classIndex() && !instance.isMissing(l)) {
                    double d = instance.valueSparse(l) * instance.weight();
                    if (d < 0.0D) {
                        throw new Exception("Numeric attribute values must all be greater or equal to zero.");
                    }
                    ad1[k] += d;
                    m_probOfWordGivenClass[k][instance.index(l)] += d;
                }
                l++;
            }
        }

        for (int i1 = 0; i1 < m_numClasses; i1++) {
            for (int j1 = 0; j1 < m_numAttributes; j1++) {
                m_probOfWordGivenClass[i1][j1] = Math.log(m_probOfWordGivenClass[i1][j1] / ((ad1[i1] + (double)m_numAttributes) - 1.0D));
            }

        }

        double d1 = instances.sumOfWeights() + (double)m_numClasses;
        m_probOfClass = new double[m_numClasses];
        for (int k1 = 0; k1 < m_numClasses; k1++) {
            m_probOfClass[k1] = (ad[k1] + 1.0D) / d1;
        }

    }

    public double[] distributionForInstance(Instance instance) throws Exception {
        double ad[] = new double[m_numClasses];
        double ad1[] = new double[m_numClasses];
        for (int i = 0; i < m_numClasses; i++) {
            ad1[i] = probOfDocGivenClass(instance, i);
        }

        double d = ad1[Utils.maxIndex(ad1)];
        double d1 = 0.0D;
        for (int j = 0; j < m_numClasses; j++) {
            ad[j] = Math.exp(ad1[j] - d) * m_probOfClass[j];
            d1 += ad[j];
        }

        Utils.normalize(ad, d1);
        return ad;
    }

    private double probOfDocGivenClass(Instance instance, int i) {
        double d = 0.0D;
        for (int j = 0; j < instance.numValues(); j++) {
            if (instance.index(j) != instance.classIndex()) {
                double d1 = instance.valueSparse(j);
                d += d1 * m_probOfWordGivenClass[i][instance.index(j)];
            }
        }

        return d;
    }

    public double lnFactorial(int i) {
        if (i < 0) {
            return SpecialFunctions.lnFactorial(i);
        }
        if (m_lnFactorialCache.length <= i) {
            double ad[] = new double[i + 1];
            System.arraycopy(m_lnFactorialCache, 0, ad, 0, m_lnFactorialCache.length);
            for (int j = m_lnFactorialCache.length; j < ad.length; j++) {
                ad[j] = ad[j - 1] + Math.log(j);
            }

            m_lnFactorialCache = ad;
        }
        return m_lnFactorialCache[i];
    }

    public String toString() {
        StringBuffer stringbuffer = new StringBuffer("The independent probability of a class\n--------------------------------------\n");
        for (int i = 0; i < m_numClasses; i++) {
            stringbuffer.append(m_headerInfo.classAttribute().value(i)).append("\t").append(Double.toString(m_probOfClass[i])).append("\n");
        }

        stringbuffer.append("\nThe probability of a word given the class\n-----------------------------------------\n\t");
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
        return RevisionUtils.extract("$Revision: 1.16 $");
    }

    public static void main(String args[]) {
        runClassifier(new NaiveBayesMultinomial(), args);
    }
}
