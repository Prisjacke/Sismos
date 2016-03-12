// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes;

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.rules.ZeroR;
import weka.core.*;

public class WAODE extends Classifier
    implements TechnicalInformationHandler {

    private static final long serialVersionUID = 0x1e20ddbd0462751aL;
    private double m_ClassCounts[];
    private double m_AttCounts[];
    private double m_AttAttCounts[][];
    private double m_ClassAttAttCounts[][][];
    private int m_NumAttValues[];
    private int m_TotalAttValues;
    private int m_NumClasses;
    private int m_NumAttributes;
    private int m_NumInstances;
    private int m_ClassIndex;
    private int m_StartAttIndex[];
    private double m_mutualInformation[];
    private Instances m_Header;
    private boolean m_Internals;
    private Classifier m_ZeroR;

    public WAODE() {
        m_Header = null;
        m_Internals = false;
    }

    public String globalInfo() {
        return (new StringBuilder()).append("WAODE contructs the model called Weightily Averaged One-Dependence Estimators.\n\nFor more information, see\n\n").append(getTechnicalInformation().toString()).toString();
    }

    public Enumeration listOptions() {
        Vector vector = new Vector();
        for (Enumeration enumeration = super.listOptions(); enumeration.hasMoreElements(); vector.add(enumeration.nextElement())) { }
        vector.addElement(new Option("\tWhether to print some more internals.\n\t(default: no)", "I", 0, "-I"));
        return vector.elements();
    }

    public void setOptions(String as[]) throws Exception {
        super.setOptions(as);
        setInternals(Utils.getFlag('I', as));
    }

    public String[] getOptions() {
        Vector vector = new Vector();
        String as[] = super.getOptions();
        for (int i = 0; i < as.length; i++) {
            vector.add(as[i]);
        }

        if (getInternals()) {
            vector.add("-I");
        }
        return (String[])(String[])vector.toArray(new String[vector.size()]);
    }

    public String internalsTipText() {
        return "Prints more internals of the classifier.";
    }

    public void setInternals(boolean flag) {
        m_Internals = flag;
    }

    public boolean getInternals() {
        return m_Internals;
    }

    public TechnicalInformation getTechnicalInformation() {
        TechnicalInformation technicalinformation = new TechnicalInformation(weka.core.TechnicalInformation.Type.INPROCEEDINGS);
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.AUTHOR, "L. Jiang and H. Zhang");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.TITLE, "Weightily Averaged One-Dependence Estimators");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.BOOKTITLE, "Proceedings of the 9th Biennial Pacific Rim International Conference on Artificial Intelligence, PRICAI 2006");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.YEAR, "2006");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.PAGES, "970-974");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.SERIES, "LNAI");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.VOLUME, "4099");
        return technicalinformation;
    }

    public Capabilities getCapabilities() {
        Capabilities capabilities = super.getCapabilities();
        capabilities.enable(weka.core.Capabilities.Capability.NOMINAL_ATTRIBUTES);
        capabilities.enable(weka.core.Capabilities.Capability.NOMINAL_CLASS);
        return capabilities;
    }

    public void buildClassifier(Instances instances) throws Exception {
        getCapabilities().testWithFail(instances);
        if (instances.numAttributes() == 1) {
            System.err.println("Cannot build model (only class attribute present in data!), using ZeroR model instead!");
            m_ZeroR = new ZeroR();
            m_ZeroR.buildClassifier(instances);
            return;
        }
        m_ZeroR = null;
        m_NumClasses = instances.numClasses();
        m_ClassIndex = instances.classIndex();
        m_NumAttributes = instances.numAttributes();
        m_NumInstances = instances.numInstances();
        m_TotalAttValues = 0;
        m_StartAttIndex = new int[m_NumAttributes];
        m_NumAttValues = new int[m_NumAttributes];
        for (int i = 0; i < m_NumAttributes; i++) {
            if (i != m_ClassIndex) {
                m_StartAttIndex[i] = m_TotalAttValues;
                m_NumAttValues[i] = instances.attribute(i).numValues();
                m_TotalAttValues += m_NumAttValues[i];
            } else {
                m_StartAttIndex[i] = -1;
                m_NumAttValues[i] = m_NumClasses;
            }
        }

        m_ClassCounts = new double[m_NumClasses];
        m_AttCounts = new double[m_TotalAttValues];
        m_AttAttCounts = new double[m_TotalAttValues][m_TotalAttValues];
        m_ClassAttAttCounts = new double[m_NumClasses][m_TotalAttValues][m_TotalAttValues];
        m_Header = new Instances(instances, 0);
        for (int j = 0; j < m_NumInstances; j++) {
            int l = (int)instances.instance(j).classValue();
            m_ClassCounts[l]++;
            int ai[] = new int[m_NumAttributes];
            for (int i1 = 0; i1 < m_NumAttributes; i1++) {
                if (i1 == m_ClassIndex) {
                    ai[i1] = -1;
                } else {
                    ai[i1] = m_StartAttIndex[i1] + (int)instances.instance(j).value(i1);
                    m_AttCounts[ai[i1]]++;
                }
            }

            for (int j1 = 0; j1 < m_NumAttributes; j1++) {
                if (ai[j1] == -1) {
                    continue;
                }
                for (int k1 = 0; k1 < m_NumAttributes; k1++) {
                    if (ai[k1] != -1) {
                        m_AttAttCounts[ai[j1]][ai[k1]]++;
                        m_ClassAttAttCounts[l][ai[j1]][ai[k1]]++;
                    }
                }

            }

        }

        m_mutualInformation = new double[m_NumAttributes];
        for (int k = 0; k < m_NumAttributes; k++) {
            if (k != m_ClassIndex) {
                m_mutualInformation[k] = mutualInfo(k);
            }
        }

    }

    private double mutualInfo(int i) {
        double d = 0.0D;
        int j = m_StartAttIndex[i];
        double ad[] = new double[m_NumClasses];
        double ad1[] = new double[m_NumAttValues[i]];
        double ad2[][] = new double[m_NumClasses][m_NumAttValues[i]];
        for (int k = 0; k < m_NumClasses; k++) {
            ad[k] = m_ClassCounts[k] / (double)m_NumInstances;
        }

        for (int l = 0; l < m_NumAttValues[i]; l++) {
            ad1[l] = m_AttCounts[j + l] / (double)m_NumInstances;
        }

        for (int i1 = 0; i1 < m_NumClasses; i1++) {
            for (int k1 = 0; k1 < m_NumAttValues[i]; k1++) {
                ad2[i1][k1] = m_ClassAttAttCounts[i1][j + k1][j + k1] / (double)m_NumInstances;
            }

        }

        for (int j1 = 0; j1 < m_NumClasses; j1++) {
            for (int l1 = 0; l1 < m_NumAttValues[i]; l1++) {
                d += ad2[j1][l1] * log2(ad2[j1][l1], ad[j1] * ad1[l1]);
            }

        }

        return d;
    }

    private double log2(double d, double d1) {
        if (d < Utils.SMALL || d1 < Utils.SMALL) {
            return 0.0D;
        } else {
            return Math.log(d / d1) / Math.log(2D);
        }
    }

    public double[] distributionForInstance(Instance instance) throws Exception {
        if (m_ZeroR != null) {
            return m_ZeroR.distributionForInstance(instance);
        }
        double ad[] = new double[m_NumClasses];
        int ai[] = new int[m_NumAttributes];
        for (int i = 0; i < m_NumAttributes; i++) {
            if (i == m_ClassIndex) {
                ai[i] = -1;
            } else {
                ai[i] = m_StartAttIndex[i] + (int)instance.value(i);
            }
        }

        for (int j = 0; j < m_NumClasses; j++) {
            ad[j] = 0.0D;
            double d = 1.0D;
            double d2 = 0.0D;
            for (int k = 0; k < m_NumAttributes; k++) {
                if (ai[k] == -1) {
                    continue;
                }
                double d1 = (m_ClassAttAttCounts[j][ai[k]][ai[k]] + 1.0D / (double)(m_NumClasses * m_NumAttValues[k])) / ((double)m_NumInstances + 1.0D);
                for (int l = 0; l < m_NumAttributes; l++) {
                    if (ai[l] != -1 && l != k) {
                        d1 *= (m_ClassAttAttCounts[j][ai[k]][ai[l]] + 1.0D / (double)m_NumAttValues[l]) / (m_ClassAttAttCounts[j][ai[k]][ai[k]] + 1.0D);
                    }
                }

                d2 += m_mutualInformation[k];
                ad[j] += m_mutualInformation[k] * d1;
            }

            ad[j] /= d2;
        }

        if (!Double.isNaN(Utils.sum(ad))) {
            Utils.normalize(ad);
        }
        return ad;
    }

    public String toString() {
        StringBuffer stringbuffer;
        if (m_ZeroR != null) {
            stringbuffer = new StringBuffer();
            stringbuffer.append((new StringBuilder()).append(getClass().getName().replaceAll(".*\\.", "")).append("\n").toString());
            stringbuffer.append((new StringBuilder()).append(getClass().getName().replaceAll(".*\\.", "").replaceAll(".", "=")).append("\n\n").toString());
            stringbuffer.append("Warning: No model could be built, hence ZeroR model is used:\n\n");
            stringbuffer.append(m_ZeroR.toString());
        } else {
            String s = getClass().getName().replaceAll(".*\\.", "");
            stringbuffer = new StringBuffer();
            stringbuffer.append((new StringBuilder()).append(s).append("\n").toString());
            stringbuffer.append((new StringBuilder()).append(s.replaceAll(".", "=")).append("\n\n").toString());
            if (m_Header == null) {
                stringbuffer.append("No Model built yet.\n");
            } else
            if (getInternals()) {
                stringbuffer.append("Mutual information of attributes with class attribute:\n");
                for (int i = 0; i < m_Header.numAttributes(); i++) {
                    if (i != m_Header.classIndex()) {
                        stringbuffer.append((new StringBuilder()).append(i + 1).append(". ").append(m_Header.attribute(i).name()).append(": ").append(Utils.doubleToString(m_mutualInformation[i], 6)).append("\n").toString());
                    }
                }

            } else {
                stringbuffer.append("Model built successfully.\n");
            }
        }
        return stringbuffer.toString();
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.3 $");
    }

    public static void main(String args[]) {
        runClassifier(new WAODE(), args);
    }
}
