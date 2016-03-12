// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes;

import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.core.*;
import weka.estimators.DiscreteEstimator;
import weka.estimators.Estimator;
import weka.estimators.KernelEstimator;
import weka.estimators.NormalEstimator;
import weka.filters.Filter;
import weka.filters.supervised.attribute.Discretize;

public class NaiveBayes extends Classifier
    implements OptionHandler, WeightedInstancesHandler, TechnicalInformationHandler {

    static final long serialVersionUID = 0x5333570390c65577L;
    protected Estimator m_Distributions[][];
    protected Estimator m_ClassDistribution;
    protected boolean m_UseKernelEstimator;
    protected boolean m_UseDiscretization;
    protected int m_NumClasses;
    protected Instances m_Instances;
    protected static final double DEFAULT_NUM_PRECISION = 0.01D;
    protected Discretize m_Disc;
    protected boolean m_displayModelInOldFormat;

    public NaiveBayes() {
        m_UseKernelEstimator = false;
        m_UseDiscretization = false;
        m_Disc = null;
        m_displayModelInOldFormat = false;
    }

    public String globalInfo() {
        return (new StringBuilder()).append("Class for a Naive Bayes classifier using estimator classes. Numeric estimator precision values are chosen based on analysis of the  training data. For this reason, the classifier is not an UpdateableClassifier (which in typical usage are initialized with zero training instances) -- if you need the UpdateableClassifier functionality, use the NaiveBayesUpdateable classifier. The NaiveBayesUpdateable classifier will  use a default precision of 0.1 for numeric attributes when buildClassifier is called with zero training instances.\n\nFor more information on Naive Bayes classifiers, see\n\n").append(getTechnicalInformation().toString()).toString();
    }

    public TechnicalInformation getTechnicalInformation() {
        TechnicalInformation technicalinformation = new TechnicalInformation(weka.core.TechnicalInformation.Type.INPROCEEDINGS);
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.AUTHOR, "George H. John and Pat Langley");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.TITLE, "Estimating Continuous Distributions in Bayesian Classifiers");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.BOOKTITLE, "Eleventh Conference on Uncertainty in Artificial Intelligence");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.YEAR, "1995");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.PAGES, "338-345");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.PUBLISHER, "Morgan Kaufmann");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.ADDRESS, "San Mateo");
        return technicalinformation;
    }

    public Capabilities getCapabilities() {
        Capabilities capabilities = super.getCapabilities();
        capabilities.enable(weka.core.Capabilities.Capability.NOMINAL_ATTRIBUTES);
        capabilities.enable(weka.core.Capabilities.Capability.NUMERIC_ATTRIBUTES);
        capabilities.enable(weka.core.Capabilities.Capability.MISSING_VALUES);
        capabilities.enable(weka.core.Capabilities.Capability.NOMINAL_CLASS);
        capabilities.enable(weka.core.Capabilities.Capability.MISSING_CLASS_VALUES);
        capabilities.setMinimumNumberInstances(0);
        return capabilities;
    }

    public void buildClassifier(Instances instances) throws Exception {
        getCapabilities().testWithFail(instances);
        instances = new Instances(instances);
        instances.deleteWithMissingClass();
        m_NumClasses = instances.numClasses();
        m_Instances = new Instances(instances);
        if (m_UseDiscretization) {
            m_Disc = new Discretize();
            m_Disc.setInputFormat(m_Instances);
            m_Instances = Filter.useFilter(m_Instances, m_Disc);
        } else {
            m_Disc = null;
        }
        m_Distributions = new Estimator[m_Instances.numAttributes() - 1][m_Instances.numClasses()];
        m_ClassDistribution = new DiscreteEstimator(m_Instances.numClasses(), true);
        int i = 0;
        for (Enumeration enumeration = m_Instances.enumerateAttributes(); enumeration.hasMoreElements();) {
            Attribute attribute = (Attribute)enumeration.nextElement();
            double d = 0.01D;
            if (attribute.type() == 0) {
                m_Instances.sort(attribute);
                if (m_Instances.numInstances() > 0 && !m_Instances.instance(0).isMissing(attribute)) {
                    double d1 = m_Instances.instance(0).value(attribute);
                    double d3 = 0.0D;
                    int k = 0;
                    for (int l = 1; l < m_Instances.numInstances(); l++) {
                        Instance instance1 = m_Instances.instance(l);
                        if (instance1.isMissing(attribute)) {
                            break;
                        }
                        double d2 = instance1.value(attribute);
                        if (d2 != d1) {
                            d3 += d2 - d1;
                            d1 = d2;
                            k++;
                        }
                    }

                    if (k > 0) {
                        d = d3 / (double)k;
                    }
                }
            }
            for (int j = 0; j < m_Instances.numClasses(); j++) {
                switch (attribute.type()) {
                case 0: // '\0'
                    if (m_UseKernelEstimator) {
                        m_Distributions[i][j] = new KernelEstimator(d);
                    } else {
                        m_Distributions[i][j] = new NormalEstimator(d);
                    }
                    break;

                case 1: // '\001'
                    m_Distributions[i][j] = new DiscreteEstimator(attribute.numValues(), true);
                    break;

                default:
                    throw new Exception("Attribute type unknown to NaiveBayes");
                }
            }

            i++;
        }

        Instance instance;
        for (Enumeration enumeration1 = m_Instances.enumerateInstances(); enumeration1.hasMoreElements(); updateClassifier(instance)) {
            instance = (Instance)enumeration1.nextElement();
        }

        m_Instances = new Instances(m_Instances, 0);
    }

    public void updateClassifier(Instance instance) throws Exception {
        if (!instance.classIsMissing()) {
            Enumeration enumeration = m_Instances.enumerateAttributes();
            for (int i = 0; enumeration.hasMoreElements(); i++) {
                Attribute attribute = (Attribute)enumeration.nextElement();
                if (!instance.isMissing(attribute)) {
                    m_Distributions[i][(int)instance.classValue()].addValue(instance.value(attribute), instance.weight());
                }
            }

            m_ClassDistribution.addValue(instance.classValue(), instance.weight());
        }
    }

    public double[] distributionForInstance(Instance instance) throws Exception {
        if (m_UseDiscretization) {
            m_Disc.input(instance);
            instance = m_Disc.output();
        }
        double ad[] = new double[m_NumClasses];
        for (int i = 0; i < m_NumClasses; i++) {
            ad[i] = m_ClassDistribution.getProbability(i);
        }

        Enumeration enumeration = instance.enumerateAttributes();
        for (int j = 0; enumeration.hasMoreElements(); j++) {
            Attribute attribute = (Attribute)enumeration.nextElement();
            if (instance.isMissing(attribute)) {
                continue;
            }
            double d1 = 0.0D;
            for (int k = 0; k < m_NumClasses; k++) {
                double d = Math.max(9.9999999999999996E-076D, Math.pow(m_Distributions[j][k].getProbability(instance.value(attribute)), m_Instances.attribute(j).weight()));
                ad[k] *= d;
                if (ad[k] > d1) {
                    d1 = ad[k];
                }
                if (Double.isNaN(ad[k])) {
                    throw new Exception((new StringBuilder()).append("NaN returned from estimator for attribute ").append(attribute.name()).append(":\n").append(m_Distributions[j][k].toString()).toString());
                }
            }

            if (d1 <= 0.0D || d1 >= 9.9999999999999996E-076D) {
                continue;
            }
            for (int l = 0; l < m_NumClasses; l++) {
                ad[l] *= 9.9999999999999993E+074D;
            }

        }

        Utils.normalize(ad);
        return ad;
    }

    public Enumeration listOptions() {
        Vector vector = new Vector(3);
        vector.addElement(new Option("\tUse kernel density estimator rather than normal\n\tdistribution for numeric attributes", "K", 0, "-K"));
        vector.addElement(new Option("\tUse supervised discretization to process numeric attributes\n", "D", 0, "-D"));
        vector.addElement(new Option("\tDisplay model in old format (good when there are many classes)\n", "O", 0, "-O"));
        return vector.elements();
    }

    public void setOptions(String as[]) throws Exception {
        boolean flag = Utils.getFlag('K', as);
        boolean flag1 = Utils.getFlag('D', as);
        if (flag && flag1) {
            throw new IllegalArgumentException("Can't use both kernel density estimation and discretization!");
        } else {
            setUseSupervisedDiscretization(flag1);
            setUseKernelEstimator(flag);
            setDisplayModelInOldFormat(Utils.getFlag('O', as));
            Utils.checkForRemainingOptions(as);
            return;
        }
    }

    public String[] getOptions() {
        String as[] = new String[3];
        int i = 0;
        if (m_UseKernelEstimator) {
            as[i++] = "-K";
        }
        if (m_UseDiscretization) {
            as[i++] = "-D";
        }
        if (m_displayModelInOldFormat) {
            as[i++] = "-O";
        }
        while (i < as.length)  {
            as[i++] = "";
        }
        return as;
    }

    public String toString() {
        if (m_displayModelInOldFormat) {
            return toStringOriginal();
        }
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("Naive Bayes Classifier");
        if (m_Instances == null) {
            stringbuffer.append(": No model built yet.");
        } else {
            int i = 0;
            int j = 0;
            boolean flag = false;
            for (int k = 0; k < m_Instances.numClasses(); k++) {
                if (m_Instances.classAttribute().value(k).length() > i) {
                    i = m_Instances.classAttribute().value(k).length();
                }
            }

            for (int l = 0; l < m_Instances.numAttributes(); l++) {
                if (l == m_Instances.classIndex()) {
                    continue;
                }
                Attribute attribute = m_Instances.attribute(l);
                if (attribute.name().length() > j) {
                    j = m_Instances.attribute(l).name().length();
                }
                if (!attribute.isNominal()) {
                    continue;
                }
                for (int i3 = 0; i3 < attribute.numValues(); i3++) {
                    String s5 = (new StringBuilder()).append(attribute.value(i3)).append("  ").toString();
                    if (s5.length() > j) {
                        j = s5.length();
                    }
                }

            }

            for (int i1 = 0; i1 < m_Distributions.length; i1++) {
label0:
                for (int k2 = 0; k2 < m_Instances.numClasses(); k2++) {
                    if (m_Distributions[i1][0] instanceof NormalEstimator) {
                        NormalEstimator normalestimator = (NormalEstimator)m_Distributions[i1][k2];
                        double d = Math.log(Math.abs(normalestimator.getMean())) / Math.log(10D);
                        double d1 = Math.log(Math.abs(normalestimator.getPrecision())) / Math.log(10D);
                        double d2 = d <= d1 ? d1 : d;
                        if (d2 < 0.0D) {
                            d2 = 1.0D;
                        }
                        d2 += 6D;
                        if ((int)d2 > i) {
                            i = (int)d2;
                        }
                        continue;
                    }
                    if (m_Distributions[i1][0] instanceof KernelEstimator) {
                        flag = true;
                        KernelEstimator kernelestimator = (KernelEstimator)m_Distributions[i1][k2];
                        int j3 = kernelestimator.getNumKernels();
                        String s8 = (new StringBuilder()).append("K").append(j3).append(": mean (weight)").toString();
                        if (j < s8.length()) {
                            j = s8.length();
                        }
                        if (kernelestimator.getNumKernels() <= 0) {
                            continue;
                        }
                        double ad[] = kernelestimator.getMeans();
                        double ad1[] = kernelestimator.getWeights();
                        int k6 = 0;
                        do {
                            if (k6 >= kernelestimator.getNumKernels()) {
                                continue label0;
                            }
                            String s22 = Utils.doubleToString(ad[k6], i, 4).trim();
                            s22 = (new StringBuilder()).append(s22).append(" (").append(Utils.doubleToString(ad1[k6], i, 1).trim()).append(")").toString();
                            if (i < s22.length()) {
                                i = s22.length();
                            }
                            k6++;
                        } while (true);
                    }
                    if (!(m_Distributions[i1][0] instanceof DiscreteEstimator)) {
                        continue;
                    }
                    DiscreteEstimator discreteestimator = (DiscreteEstimator)m_Distributions[i1][k2];
                    for (int k3 = 0; k3 < discreteestimator.getNumSymbols(); k3++) {
                        String s9 = (new StringBuilder()).append("").append(discreteestimator.getCount(k3)).toString();
                        if (s9.length() > i) {
                            i = s9.length();
                        }
                    }

                    int l3 = (new StringBuilder()).append("").append(discreteestimator.getSumOfCounts()).toString().length();
                    if (l3 > i) {
                        i = l3;
                    }
                }

            }

            for (int j1 = 0; j1 < m_Instances.numClasses(); j1++) {
                String s = m_Instances.classAttribute().value(j1);
                if (s.length() > i) {
                    i = s.length();
                }
            }

            for (int k1 = 0; k1 < m_Instances.numClasses(); k1++) {
                String s1 = Utils.doubleToString(((DiscreteEstimator)m_ClassDistribution).getProbability(k1), i, 2).trim();
                s1 = (new StringBuilder()).append("(").append(s1).append(")").toString();
                if (s1.length() > i) {
                    i = s1.length();
                }
            }

            if (j < "Attribute".length()) {
                j = "Attribute".length();
            }
            if (j < "  weight sum".length()) {
                j = "  weight sum".length();
            }
            if (flag && j < "  [precision]".length()) {
                j = "  [precision]".length();
            }
            j += 2;
            stringbuffer.append("\n\n");
            stringbuffer.append(pad("Class", " ", (j + i + 1) - "Class".length(), true));
            stringbuffer.append("\n");
            stringbuffer.append(pad("Attribute", " ", j - "Attribute".length(), false));
            for (int l1 = 0; l1 < m_Instances.numClasses(); l1++) {
                String s2 = m_Instances.classAttribute().value(l1);
                stringbuffer.append(pad(s2, " ", (i + 1) - s2.length(), true));
            }

            stringbuffer.append("\n");
            stringbuffer.append(pad("", " ", j, true));
            for (int i2 = 0; i2 < m_Instances.numClasses(); i2++) {
                String s3 = Utils.doubleToString(((DiscreteEstimator)m_ClassDistribution).getProbability(i2), i, 2).trim();
                s3 = (new StringBuilder()).append("(").append(s3).append(")").toString();
                stringbuffer.append(pad(s3, " ", (i + 1) - s3.length(), true));
            }

            stringbuffer.append("\n");
            stringbuffer.append(pad("", "=", j + i * m_Instances.numClasses() + m_Instances.numClasses() + 1, true));
            stringbuffer.append("\n");
            int j2 = 0;
            for (int l2 = 0; l2 < m_Instances.numAttributes(); l2++) {
                if (l2 == m_Instances.classIndex()) {
                    continue;
                }
                String s4 = m_Instances.attribute(l2).name();
                stringbuffer.append((new StringBuilder()).append(s4).append("\n").toString());
                if (m_Distributions[j2][0] instanceof NormalEstimator) {
                    String s6 = "  mean";
                    stringbuffer.append(pad(s6, " ", (j + 1) - s6.length(), false));
                    for (int i4 = 0; i4 < m_Instances.numClasses(); i4++) {
                        NormalEstimator normalestimator1 = (NormalEstimator)m_Distributions[j2][i4];
                        String s16 = Utils.doubleToString(normalestimator1.getMean(), i, 4).trim();
                        stringbuffer.append(pad(s16, " ", (i + 1) - s16.length(), true));
                    }

                    stringbuffer.append("\n");
                    String s10 = "  std. dev.";
                    stringbuffer.append(pad(s10, " ", (j + 1) - s10.length(), false));
                    for (int l4 = 0; l4 < m_Instances.numClasses(); l4++) {
                        NormalEstimator normalestimator2 = (NormalEstimator)m_Distributions[j2][l4];
                        String s19 = Utils.doubleToString(normalestimator2.getStdDev(), i, 4).trim();
                        stringbuffer.append(pad(s19, " ", (i + 1) - s19.length(), true));
                    }

                    stringbuffer.append("\n");
                    String s13 = "  weight sum";
                    stringbuffer.append(pad(s13, " ", (j + 1) - s13.length(), false));
                    for (int k5 = 0; k5 < m_Instances.numClasses(); k5++) {
                        NormalEstimator normalestimator3 = (NormalEstimator)m_Distributions[j2][k5];
                        String s23 = Utils.doubleToString(normalestimator3.getSumOfWeights(), i, 4).trim();
                        stringbuffer.append(pad(s23, " ", (i + 1) - s23.length(), true));
                    }

                    stringbuffer.append("\n");
                    String s17 = "  precision";
                    stringbuffer.append(pad(s17, " ", (j + 1) - s17.length(), false));
                    for (int l6 = 0; l6 < m_Instances.numClasses(); l6++) {
                        NormalEstimator normalestimator4 = (NormalEstimator)m_Distributions[j2][l6];
                        String s27 = Utils.doubleToString(normalestimator4.getPrecision(), i, 4).trim();
                        stringbuffer.append(pad(s27, " ", (i + 1) - s27.length(), true));
                    }

                    stringbuffer.append("\n\n");
                } else
                if (m_Distributions[j2][0] instanceof DiscreteEstimator) {
                    Attribute attribute1 = m_Instances.attribute(l2);
                    for (int j4 = 0; j4 < attribute1.numValues(); j4++) {
                        String s14 = (new StringBuilder()).append("  ").append(attribute1.value(j4)).toString();
                        stringbuffer.append(pad(s14, " ", (j + 1) - s14.length(), false));
                        for (int l5 = 0; l5 < m_Instances.numClasses(); l5++) {
                            DiscreteEstimator discreteestimator2 = (DiscreteEstimator)m_Distributions[j2][l5];
                            String s24 = (new StringBuilder()).append("").append(discreteestimator2.getCount(j4)).toString();
                            stringbuffer.append(pad(s24, " ", (i + 1) - s24.length(), true));
                        }

                        stringbuffer.append("\n");
                    }

                    String s11 = "  [total]";
                    stringbuffer.append(pad(s11, " ", (j + 1) - s11.length(), false));
                    for (int i5 = 0; i5 < m_Instances.numClasses(); i5++) {
                        DiscreteEstimator discreteestimator1 = (DiscreteEstimator)m_Distributions[j2][i5];
                        String s20 = (new StringBuilder()).append("").append(discreteestimator1.getSumOfCounts()).toString();
                        stringbuffer.append(pad(s20, " ", (i + 1) - s20.length(), true));
                    }

                    stringbuffer.append("\n\n");
                } else
                if (m_Distributions[j2][0] instanceof KernelEstimator) {
                    String s7 = "  [# kernels]";
                    stringbuffer.append(pad(s7, " ", (j + 1) - s7.length(), false));
                    for (int k4 = 0; k4 < m_Instances.numClasses(); k4++) {
                        KernelEstimator kernelestimator1 = (KernelEstimator)m_Distributions[j2][k4];
                        String s18 = (new StringBuilder()).append("").append(kernelestimator1.getNumKernels()).toString();
                        stringbuffer.append(pad(s18, " ", (i + 1) - s18.length(), true));
                    }

                    stringbuffer.append("\n");
                    String s12 = "  [std. dev]";
                    stringbuffer.append(pad(s12, " ", (j + 1) - s12.length(), false));
                    for (int j5 = 0; j5 < m_Instances.numClasses(); j5++) {
                        KernelEstimator kernelestimator2 = (KernelEstimator)m_Distributions[j2][j5];
                        String s21 = Utils.doubleToString(kernelestimator2.getStdDev(), i, 4).trim();
                        stringbuffer.append(pad(s21, " ", (i + 1) - s21.length(), true));
                    }

                    stringbuffer.append("\n");
                    String s15 = "  [precision]";
                    stringbuffer.append(pad(s15, " ", (j + 1) - s15.length(), false));
                    for (int i6 = 0; i6 < m_Instances.numClasses(); i6++) {
                        KernelEstimator kernelestimator3 = (KernelEstimator)m_Distributions[j2][i6];
                        String s25 = Utils.doubleToString(kernelestimator3.getPrecision(), i, 4).trim();
                        stringbuffer.append(pad(s25, " ", (i + 1) - s25.length(), true));
                    }

                    stringbuffer.append("\n");
                    int j6 = 0;
                    for (int i7 = 0; i7 < m_Instances.numClasses(); i7++) {
                        KernelEstimator kernelestimator4 = (KernelEstimator)m_Distributions[j2][i7];
                        if (kernelestimator4.getNumKernels() > j6) {
                            j6 = kernelestimator4.getNumKernels();
                        }
                    }

                    for (int j7 = 0; j7 < j6; j7++) {
                        String s26 = (new StringBuilder()).append("  K").append(j7 + 1).append(": mean (weight)").toString();
                        stringbuffer.append(pad(s26, " ", (j + 1) - s26.length(), false));
                        for (int k7 = 0; k7 < m_Instances.numClasses(); k7++) {
                            KernelEstimator kernelestimator5 = (KernelEstimator)m_Distributions[j2][k7];
                            double ad2[] = kernelestimator5.getMeans();
                            double ad3[] = kernelestimator5.getWeights();
                            String s28 = "--";
                            if (kernelestimator5.getNumKernels() == 0) {
                                s28 = "0";
                            } else
                            if (j7 < kernelestimator5.getNumKernels()) {
                                s28 = Utils.doubleToString(ad2[j7], i, 4).trim();
                                s28 = (new StringBuilder()).append(s28).append(" (").append(Utils.doubleToString(ad3[j7], i, 1).trim()).append(")").toString();
                            }
                            stringbuffer.append(pad(s28, " ", (i + 1) - s28.length(), true));
                        }

                        stringbuffer.append("\n");
                    }

                    stringbuffer.append("\n");
                }
                j2++;
            }

        }
        return stringbuffer.toString();
    }

    protected String toStringOriginal() {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("Naive Bayes Classifier");
        if (m_Instances == null) {
            stringbuffer.append(": No model built yet.");
        } else {
            try {
                for (int i = 0; i < m_Distributions[0].length; i++) {
                    stringbuffer.append((new StringBuilder()).append("\n\nClass ").append(m_Instances.classAttribute().value(i)).append(": Prior probability = ").append(Utils.doubleToString(m_ClassDistribution.getProbability(i), 4, 2)).append("\n\n").toString());
                    Enumeration enumeration = m_Instances.enumerateAttributes();
                    for (int j = 0; enumeration.hasMoreElements(); j++) {
                        Attribute attribute = (Attribute)enumeration.nextElement();
                        if (attribute.weight() > 0.0D) {
                            stringbuffer.append((new StringBuilder()).append(attribute.name()).append(":  ").append(m_Distributions[j][i]).toString());
                        }
                    }

                }

            }
            catch (Exception exception) {
                stringbuffer.append(exception.getMessage());
            }
        }
        return stringbuffer.toString();
    }

    private String pad(String s, String s1, int i, boolean flag) {
        StringBuffer stringbuffer = new StringBuffer();
        if (flag) {
            for (int j = 0; j < i; j++) {
                stringbuffer.append(s1);
            }

            stringbuffer.append(s);
        } else {
            stringbuffer.append(s);
            for (int k = 0; k < i; k++) {
                stringbuffer.append(s1);
            }

        }
        return stringbuffer.toString();
    }

    public String useKernelEstimatorTipText() {
        return "Use a kernel estimator for numeric attributes rather than a normal distribution.";
    }

    public boolean getUseKernelEstimator() {
        return m_UseKernelEstimator;
    }

    public void setUseKernelEstimator(boolean flag) {
        m_UseKernelEstimator = flag;
        if (flag) {
            setUseSupervisedDiscretization(false);
        }
    }

    public String useSupervisedDiscretizationTipText() {
        return "Use supervised discretization to convert numeric attributes to nominal ones.";
    }

    public boolean getUseSupervisedDiscretization() {
        return m_UseDiscretization;
    }

    public void setUseSupervisedDiscretization(boolean flag) {
        m_UseDiscretization = flag;
        if (flag) {
            setUseKernelEstimator(false);
        }
    }

    public String displayModelInOldFormatTipText() {
        return "Use old format for model output. The old format is better when there are many class values. The new format is better when there are fewer classes and many attributes.";
    }

    public void setDisplayModelInOldFormat(boolean flag) {
        m_displayModelInOldFormat = flag;
    }

    public boolean getDisplayModelInOldFormat() {
        return m_displayModelInOldFormat;
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.27 $");
    }

    public static void main(String args[]) {
        runClassifier(new NaiveBayes(), args);
    }
}
