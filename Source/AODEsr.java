// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes;

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.UpdateableClassifier;
import weka.core.*;

public class AODEsr extends Classifier
    implements OptionHandler, WeightedInstancesHandler, UpdateableClassifier, TechnicalInformationHandler {

    static final long serialVersionUID = 0x4dbecf64ff5756b0L;
    private double m_CondiCounts[][][];
    private double m_CondiCountsNoClass[][];
    private double m_ClassCounts[];
    private double m_SumForCounts[][];
    private int m_NumClasses;
    private int m_NumAttributes;
    private int m_NumInstances;
    private int m_ClassIndex;
    private Instances m_Instances;
    private int m_TotalAttValues;
    private int m_StartAttIndex[];
    private int m_NumAttValues[];
    private double m_Frequencies[];
    private double m_SumInstances;
    private int m_Limit;
    private boolean m_Debug;
    protected double m_MWeight;
    private boolean m_Laplace;
    private int m_Critical;

    public AODEsr() {
        m_Limit = 1;
        m_Debug = false;
        m_MWeight = 1.0D;
        m_Laplace = false;
        m_Critical = 50;
    }

    public String globalInfo() {
        return (new StringBuilder()).append("AODEsr augments AODE with Subsumption Resolution.AODEsr detects specializations between two attribute values at classification time and deletes the generalization attribute value.\nFor more information, see:\n").append(getTechnicalInformation().toString()).toString();
    }

    public TechnicalInformation getTechnicalInformation() {
        TechnicalInformation technicalinformation = new TechnicalInformation(weka.core.TechnicalInformation.Type.INPROCEEDINGS);
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.AUTHOR, "Fei Zheng and Geoffrey I. Webb");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.YEAR, "2006");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.TITLE, "Efficient Lazy Elimination for Averaged-One Dependence Estimators");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.PAGES, "1113-1120");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.BOOKTITLE, "Proceedings of the Twenty-third International Conference on Machine  Learning (ICML 2006)");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.PUBLISHER, "ACM Press");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.ISBN, "1-59593-383-2");
        return technicalinformation;
    }

    public Capabilities getCapabilities() {
        Capabilities capabilities = super.getCapabilities();
        capabilities.enable(weka.core.Capabilities.Capability.NOMINAL_ATTRIBUTES);
        capabilities.enable(weka.core.Capabilities.Capability.MISSING_VALUES);
        capabilities.enable(weka.core.Capabilities.Capability.NOMINAL_CLASS);
        capabilities.enable(weka.core.Capabilities.Capability.MISSING_CLASS_VALUES);
        capabilities.setMinimumNumberInstances(0);
        return capabilities;
    }

    public void buildClassifier(Instances instances) throws Exception {
        getCapabilities().testWithFail(instances);
        m_Instances = new Instances(instances);
        m_Instances.deleteWithMissingClass();
        m_SumInstances = 0.0D;
        m_ClassIndex = instances.classIndex();
        m_NumInstances = m_Instances.numInstances();
        m_NumAttributes = instances.numAttributes();
        m_NumClasses = instances.numClasses();
        m_StartAttIndex = new int[m_NumAttributes];
        m_NumAttValues = new int[m_NumAttributes];
        m_TotalAttValues = 0;
        for (int i = 0; i < m_NumAttributes; i++) {
            if (i != m_ClassIndex) {
                m_StartAttIndex[i] = m_TotalAttValues;
                m_NumAttValues[i] = m_Instances.attribute(i).numValues();
                m_TotalAttValues += m_NumAttValues[i] + 1;
            } else {
                m_NumAttValues[i] = m_NumClasses;
            }
        }

        m_CondiCounts = new double[m_NumClasses][m_TotalAttValues][m_TotalAttValues];
        m_ClassCounts = new double[m_NumClasses];
        m_SumForCounts = new double[m_NumClasses][m_NumAttributes];
        m_Frequencies = new double[m_TotalAttValues];
        m_CondiCountsNoClass = new double[m_TotalAttValues][m_TotalAttValues];
        for (int j = 0; j < m_NumInstances; j++) {
            addToCounts(m_Instances.instance(j));
        }

        m_Instances = new Instances(m_Instances, 0);
    }

    public void updateClassifier(Instance instance) {
        addToCounts(instance);
    }

    private void addToCounts(Instance instance) {
        if (instance.classIsMissing()) {
            return;
        }
        int i = (int)instance.classValue();
        double d = instance.weight();
        m_ClassCounts[i] += d;
        m_SumInstances += d;
        int ai[] = new int[m_NumAttributes];
        for (int j = 0; j < m_NumAttributes; j++) {
            if (j == m_ClassIndex) {
                ai[j] = -1;
                continue;
            }
            if (instance.isMissing(j)) {
                ai[j] = m_StartAttIndex[j] + m_NumAttValues[j];
            } else {
                ai[j] = m_StartAttIndex[j] + (int)instance.value(j);
            }
        }

        for (int k = 0; k < m_NumAttributes; k++) {
            if (ai[k] == -1) {
                continue;
            }
            m_Frequencies[ai[k]] += d;
            if (!instance.isMissing(k)) {
                m_SumForCounts[i][k] += d;
            }
            double ad[] = m_CondiCounts[i][ai[k]];
            double ad1[] = m_CondiCountsNoClass[ai[k]];
            for (int l = 0; l < m_NumAttributes; l++) {
                if (ai[l] != -1) {
                    ad[ai[l]] += d;
                    ad1[ai[l]] += d;
                }
            }

        }

    }

    public double[] distributionForInstance(Instance instance) throws Exception {
        double ad[] = new double[m_NumClasses];
        int ai[] = new int[m_NumAttributes];
        int ai1[] = new int[m_NumAttributes];
        for (int k = 0; k < m_NumAttributes; k++) {
            if (instance.isMissing(k) || k == m_ClassIndex) {
                ai1[k] = -1;
            } else {
                ai1[k] = m_StartAttIndex[k] + (int)instance.value(k);
            }
        }

        for (int l = 0; l < m_NumAttributes; l++) {
            ai[l] = -1;
        }

        for (int i1 = 0; i1 < m_NumAttributes; i1++) {
            if (ai1[i1] == -1) {
                continue;
            }
            double ad3[] = m_CondiCountsNoClass[ai1[i1]];
            for (int k1 = 0; k1 < m_NumAttributes; k1++) {
                if (ai1[k1] == -1 || i1 == k1 || ai[k1] == i1) {
                    continue;
                }
                double ad4[] = m_CondiCountsNoClass[ai1[k1]];
                if (ad4[ai1[k1]] <= (double)m_Critical || ad4[ai1[k1]] != ad3[ai1[k1]] || ad4[ai1[k1]] == ad3[ai1[i1]] && i1 < k1) {
                    continue;
                }
                ai[i1] = k1;
                break;
            }

        }

        for (int j1 = 0; j1 < m_NumClasses; j1++) {
            ad[j1] = 0.0D;
            double d = 0.0D;
            int j = 0;
            double ad1[][] = m_CondiCounts[j1];
            for (int l1 = 0; l1 < m_NumAttributes; l1++) {
                if (ai1[l1] == -1) {
                    continue;
                }
                int i = ai1[l1];
                if (m_Frequencies[i] < (double)m_Limit || ai[l1] != -1) {
                    continue;
                }
                double ad2[] = ad1[i];
                ai1[l1] = -1;
                j++;
                double d2 = ad2[i];
                double d3 = m_Frequencies[m_StartAttIndex[l1] + m_NumAttValues[l1]];
                double d1;
                if (m_Laplace) {
                    d1 = LaplaceEstimate(d2, m_SumInstances - d3, m_NumClasses * m_NumAttValues[l1]);
                } else {
                    d1 = MEstimate(d2, m_SumInstances - d3, m_NumClasses * m_NumAttValues[l1]);
                }
                for (int i2 = 0; i2 < m_NumAttributes; i2++) {
                    if (ai1[i2] == -1 || ai[i2] != -1) {
                        continue;
                    }
                    double d4 = ad2[m_StartAttIndex[i2] + m_NumAttValues[i2]];
                    if (m_Laplace) {
                        d1 *= LaplaceEstimate(ad2[ai1[i2]], d2 - d4, m_NumAttValues[i2]);
                    } else {
                        d1 *= MEstimate(ad2[ai1[i2]], d2 - d4, m_NumAttValues[i2]);
                    }
                }

                ad[j1] += d1;
                ai1[l1] = i;
            }

            if (j < 1) {
                ad[j1] = NBconditionalProb(instance, j1);
            } else {
                ad[j1] /= j;
            }
        }

        Utils.normalize(ad);
        return ad;
    }

    public double NBconditionalProb(Instance instance, int i) throws Exception {
        double d;
        if (m_Laplace) {
            d = LaplaceEstimate(m_ClassCounts[i], m_SumInstances, m_NumClasses);
        } else {
            d = MEstimate(m_ClassCounts[i], m_SumInstances, m_NumClasses);
        }
        double ad[][] = m_CondiCounts[i];
        for (int k = 0; k < m_NumAttributes; k++) {
            if (k == m_ClassIndex || instance.isMissing(k)) {
                continue;
            }
            int j = m_StartAttIndex[k] + (int)instance.value(k);
            if (m_Laplace) {
                d *= LaplaceEstimate(ad[j][j], m_SumForCounts[i][k], m_NumAttValues[k]);
            } else {
                d *= MEstimate(ad[j][j], m_SumForCounts[i][k], m_NumAttValues[k]);
            }
        }

        return d;
    }

    public double MEstimate(double d, double d1, double d2) {
        return (d + m_MWeight / d2) / (d1 + m_MWeight);
    }

    public double LaplaceEstimate(double d, double d1, double d2) {
        return (d + 1.0D) / (d1 + d2);
    }

    public Enumeration listOptions() {
        Vector vector = new Vector(5);
        vector.addElement(new Option("\tOutput debugging information\n", "D", 0, "-D"));
        vector.addElement(new Option("\tImpose a critcal value for specialization-generalization relationship\n\t(default is 50)", "C", 1, "-C"));
        vector.addElement(new Option("\tImpose a frequency limit for superParents\n\t(default is 1)", "F", 2, "-F"));
        vector.addElement(new Option("\tUsing Laplace estimation\n\t(default is m-esimation (m=1))", "L", 3, "-L"));
        vector.addElement(new Option("\tWeight value for m-estimation\n\t(default is 1.0)", "M", 4, "-M"));
        return vector.elements();
    }

    public void setOptions(String as[]) throws Exception {
        m_Debug = Utils.getFlag('D', as);
        String s = Utils.getOption('C', as);
        if (s.length() != 0) {
            m_Critical = Integer.parseInt(s);
        } else {
            m_Critical = 50;
        }
        String s1 = Utils.getOption('F', as);
        if (s1.length() != 0) {
            m_Limit = Integer.parseInt(s1);
        } else {
            m_Limit = 1;
        }
        m_Laplace = Utils.getFlag('L', as);
        String s2 = Utils.getOption('M', as);
        if (s2.length() != 0) {
            if (m_Laplace) {
                throw new Exception("weight for m-estimate is pointless if using laplace estimation!");
            }
            m_MWeight = Double.parseDouble(s2);
        } else {
            m_MWeight = 1.0D;
        }
        Utils.checkForRemainingOptions(as);
    }

    public String[] getOptions() {
        Vector vector = new Vector();
        if (m_Debug) {
            vector.add("-D");
        }
        vector.add("-F");
        vector.add((new StringBuilder()).append("").append(m_Limit).toString());
        if (m_Laplace) {
            vector.add("-L");
        } else {
            vector.add("-M");
            vector.add((new StringBuilder()).append("").append(m_MWeight).toString());
        }
        vector.add("-C");
        vector.add((new StringBuilder()).append("").append(m_Critical).toString());
        return (String[])(String[])vector.toArray(new String[vector.size()]);
    }

    public String mestWeightTipText() {
        return "Set the weight for m-estimate.";
    }

    public void setMestWeight(double d) {
        if (getUseLaplace()) {
            System.out.println("Weight is only used in conjunction with m-estimate - ignored!");
        } else
        if (d > 0.0D) {
            m_MWeight = d;
        } else {
            System.out.println("M-Estimate Weight must be greater than 0!");
        }
    }

    public double getMestWeight() {
        return m_MWeight;
    }

    public String useLaplaceTipText() {
        return "Use Laplace correction instead of m-estimation.";
    }

    public boolean getUseLaplace() {
        return m_Laplace;
    }

    public void setUseLaplace(boolean flag) {
        m_Laplace = flag;
    }

    public String frequencyLimitTipText() {
        return "Attributes with a frequency in the train set below this value aren't used as parents.";
    }

    public void setFrequencyLimit(int i) {
        m_Limit = i;
    }

    public int getFrequencyLimit() {
        return m_Limit;
    }

    public String criticalValueTipText() {
        return "Specify critical value for specialization-generalization relationship (default 50).";
    }

    public void setCriticalValue(int i) {
        m_Critical = i;
    }

    public int getCriticalValue() {
        return m_Critical;
    }

    public String toString() {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("The AODEsr Classifier");
        if (m_Instances == null) {
            stringbuffer.append(": No model built yet.");
        } else {
            try {
                for (int i = 0; i < m_NumClasses; i++) {
                    stringbuffer.append((new StringBuilder()).append("\nClass ").append(m_Instances.classAttribute().value(i)).append(": Prior probability = ").append(Utils.doubleToString((m_ClassCounts[i] + 1.0D) / (m_SumInstances + (double)m_NumClasses), 4, 2)).append("\n\n").toString());
                }

                stringbuffer.append((new StringBuilder()).append("Dataset: ").append(m_Instances.relationName()).append("\n").append("Instances: ").append(m_NumInstances).append("\n").append("Attributes: ").append(m_NumAttributes).append("\n").append("Frequency limit for superParents: ").append(m_Limit).append("\n").append("Critical value for the specializtion-generalization ").append("relationship: ").append(m_Critical).append("\n").toString());
                if (m_Laplace) {
                    stringbuffer.append("Using LapLace estimation.");
                } else {
                    stringbuffer.append((new StringBuilder()).append("Using m-estimation, m = ").append(m_MWeight).toString());
                }
            }
            catch (Exception exception) {
                stringbuffer.append(exception.getMessage());
            }
        }
        return stringbuffer.toString();
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.4 $");
    }

    public static void main(String args[]) {
        runClassifier(new AODEsr(), args);
    }
}
