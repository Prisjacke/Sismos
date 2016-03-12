// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes;

import java.io.Serializable;
import java.util.*;
import weka.classifiers.Classifier;
import weka.classifiers.UpdateableClassifier;
import weka.core.*;

public class DMNBtext extends Classifier
    implements OptionHandler, WeightedInstancesHandler, TechnicalInformationHandler, UpdateableClassifier {
    public class DNBBinary
        implements Serializable {

        private double m_perWordPerClass[][];
        private double m_wordsPerClass[];
        int m_classIndex;
        private double m_classDistribution[];
        private int m_numAttributes;
        private int m_targetClass;
        private double m_WordLaplace;
        private double m_coefficient[];
        private double m_classRatio;
        private double m_wordRatio;
        final DMNBtext this$0;

        public void initClassifier(Instances instances) throws Exception {
            m_numAttributes = instances.numAttributes();
            m_perWordPerClass = new double[2][m_numAttributes];
            m_coefficient = new double[m_numAttributes];
            m_wordsPerClass = new double[2];
            m_classDistribution = new double[2];
            m_WordLaplace = Math.log(m_numAttributes);
            m_classIndex = instances.classIndex();
            for (int i = 0; i < 2; i++) {
                m_classDistribution[i] = 1.0D;
                m_wordsPerClass[i] = m_WordLaplace * (double)m_numAttributes;
                Arrays.fill(m_perWordPerClass[i], m_WordLaplace);
            }

        }

        public void updateClassifier(Instance instance) throws Exception {
            int i = 0;
            if (instance.value(instance.classIndex()) != (double)m_targetClass) {
                i = 1;
            }
            double d = 1.0D - distributionForInstance(instance)[i];
            double d1 = d * instance.weight();
            for (int j = 0; j < instance.numValues(); j++) {
                if (instance.index(j) == m_classIndex) {
                    continue;
                }
                if (m_BinaryWord) {
                    if (instance.valueSparse(j) > 0.0D) {
                        m_wordsPerClass[i] += d1;
                        m_perWordPerClass[i][instance.index(j)] += d1;
                    }
                } else {
                    double d2 = instance.valueSparse(j) * d1;
                    m_wordsPerClass[i] += d2;
                    m_perWordPerClass[i][instance.index(j)] += d2;
                }
                m_coefficient[instance.index(j)] = Math.log(m_perWordPerClass[0][instance.index(j)] / m_perWordPerClass[1][instance.index(j)]);
            }

            m_wordRatio = Math.log(m_wordsPerClass[0] / m_wordsPerClass[1]);
            m_classDistribution[i] += d1;
            m_classRatio = Math.log(m_classDistribution[0] / m_classDistribution[1]);
        }

        public double getLogProbForTargetClass(Instance instance) throws Exception {
            double d = m_classRatio;
            for (int i = 0; i < instance.numValues(); i++) {
                if (instance.index(i) == m_classIndex) {
                    continue;
                }
                if (m_BinaryWord) {
                    if (instance.valueSparse(i) > 0.0D) {
                        d += m_coefficient[instance.index(i)] - m_wordRatio;
                    }
                } else {
                    d += instance.valueSparse(i) * (m_coefficient[instance.index(i)] - m_wordRatio);
                }
            }

            return d;
        }

        public double[] distributionForInstance(Instance instance) throws Exception {
            double ad[] = new double[2];
            double d = getLogProbForTargetClass(instance);
            if (d > 709D) {
                ad[0] = 1.0D;
            } else {
                d = Math.exp(d);
                ad[0] = d / (1.0D + d);
            }
            ad[1] = 1.0D - ad[0];
            return ad;
        }

        public String toString() {
            StringBuffer stringbuffer = new StringBuffer();
            stringbuffer.append("\n");
            TreeMap treemap = new TreeMap();
            double ad[] = new double[m_numAttributes];
            for (int i = 0; i < m_numAttributes; i++) {
                if (i != m_headerInfo.classIndex()) {
                    String s = (new StringBuilder()).append(m_headerInfo.attribute(i).name()).append(": ").append(m_coefficient[i]).toString();
                    treemap.put(Double.valueOf(-1D * Math.abs(m_coefficient[i])), s);
                }
            }

            for (Iterator iterator = treemap.values().iterator(); iterator.hasNext(); stringbuffer.append("\n")) {
                stringbuffer.append((String)iterator.next());
            }

            return stringbuffer.toString();
        }

        public void setTargetClass(int i) {
            m_targetClass = i;
        }

        public int getTargetClass() {
            return m_targetClass;
        }

        public DNBBinary() {
            this$0 = DMNBtext.this;
            super();
            m_classIndex = -1;
            m_targetClass = -1;
            m_WordLaplace = 1.0D;
        }
    }


    static final long serialVersionUID = 0x525353f5fec4793dL;
    protected int m_NumIterations;
    protected boolean m_BinaryWord;
    int m_numClasses;
    protected Instances m_headerInfo;
    DNBBinary m_binaryClassifiers[];

    public DMNBtext() {
        m_NumIterations = 1;
        m_BinaryWord = true;
        m_numClasses = -1;
        m_binaryClassifiers = null;
    }

    public String globalInfo() {
        return (new StringBuilder()).append("Class for building and using a Discriminative Multinomial Naive Bayes classifier. For more information see,\n\n").append(getTechnicalInformation().toString()).append("\n\n").append("The core equation for this classifier:\n\n").append("P[Ci|D] = (P[D|Ci] x P[Ci]) / P[D] (Bayes rule)\n\n").append("where Ci is class i and D is a document.").toString();
    }

    public TechnicalInformation getTechnicalInformation() {
        TechnicalInformation technicalinformation = new TechnicalInformation(weka.core.TechnicalInformation.Type.INPROCEEDINGS);
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.AUTHOR, "Jiang Su,Harry Zhang,Charles X. Ling,Stan Matwin");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.YEAR, "2008");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.TITLE, "Discriminative Parameter Learning for Bayesian Networks");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.BOOKTITLE, "ICML 2008'");
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
        Instances instances1 = new Instances(instances);
        instances1.deleteWithMissingClass();
        m_binaryClassifiers = new DNBBinary[instances1.numClasses()];
        m_numClasses = instances1.numClasses();
        m_headerInfo = new Instances(instances1, 0);
        for (int i = 0; i < instances1.numClasses(); i++) {
            m_binaryClassifiers[i] = new DNBBinary();
            m_binaryClassifiers[i].setTargetClass(i);
            m_binaryClassifiers[i].initClassifier(instances1);
        }

        if (instances1.numInstances() == 0) {
            return;
        }
        Random random = new Random();
        for (int j = 0; j < m_NumIterations; j++) {
            for (int k = 0; k < instances1.numInstances(); k++) {
                updateClassifier(instances1.instance(k));
            }

        }

    }

    public void updateClassifier(Instance instance) throws Exception {
        if (m_numClasses == 2) {
            m_binaryClassifiers[0].updateClassifier(instance);
        } else {
            for (int i = 0; i < instance.numClasses(); i++) {
                m_binaryClassifiers[i].updateClassifier(instance);
            }

        }
    }

    public double[] distributionForInstance(Instance instance) throws Exception {
        if (m_numClasses == 2) {
            return m_binaryClassifiers[0].distributionForInstance(instance);
        }
        double ad[] = new double[instance.numClasses()];
        for (int i = 0; i < m_numClasses; i++) {
            ad[i] = m_binaryClassifiers[i].getLogProbForTargetClass(instance);
        }

        double d = ad[Utils.maxIndex(ad)];
        for (int j = 0; j < m_numClasses; j++) {
            ad[j] = Math.exp(ad[j] - d);
        }

        try {
            Utils.normalize(ad);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        return ad;
    }

    public String toString() {
        StringBuffer stringbuffer = new StringBuffer("");
        stringbuffer.append("The log ratio of two conditional probabilities of a word w_i: log(p(w_i)|+)/p(w_i)|-)) in decent order based on their absolute values\n");
        stringbuffer.append("Can be used to measure the discriminative power of each word.\n");
        if (m_numClasses == 2) {
            return stringbuffer.append(m_binaryClassifiers[0].toString()).toString();
        }
        for (int i = 0; i < m_numClasses; i++) {
            stringbuffer.append((new StringBuilder()).append(i).append(" against the rest classes\n").toString());
            stringbuffer.append((new StringBuilder()).append(m_binaryClassifiers[i].toString()).append("\n").toString());
        }

        return stringbuffer.toString();
    }

    public void setOptions(String as[]) throws Exception {
        String s = Utils.getOption('I', as);
        if (s.length() != 0) {
            setNumIterations(Integer.parseInt(s));
        } else {
            setNumIterations(m_NumIterations);
        }
        s = Utils.getOption('B', as);
        if (s.length() != 0) {
            setBinaryWord(Boolean.parseBoolean(s));
        } else {
            setBinaryWord(m_BinaryWord);
        }
    }

    public String[] getOptions() {
        String as[] = new String[4];
        int i = 0;
        as[i++] = "-I";
        as[i++] = (new StringBuilder()).append("").append(getNumIterations()).toString();
        as[i++] = "-B";
        as[i++] = (new StringBuilder()).append("").append(getBinaryWord()).toString();
        return as;
    }

    public String numIterationsTipText() {
        return "The number of iterations that the classifier will scan the training data";
    }

    public void setNumIterations(int i) {
        m_NumIterations = i;
    }

    public int getNumIterations() {
        return m_NumIterations;
    }

    public String binaryWordTipText() {
        return " whether ingore the frequency information in data";
    }

    public void setBinaryWord(boolean flag) {
        m_BinaryWord = flag;
    }

    public boolean getBinaryWord() {
        return m_BinaryWord;
    }

    public String getRevision() {
        return "$Revision: 1.0";
    }

    public static void main(String args[]) {
        DMNBtext dmnbtext = new DMNBtext();
        runClassifier(dmnbtext, args);
    }
}
