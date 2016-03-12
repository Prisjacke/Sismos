// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes;

import weka.classifiers.Classifier;
import weka.core.*;

public class HNB extends Classifier
    implements TechnicalInformationHandler {

    static final long serialVersionUID = 0xc17f060e322c9d42L;
    private double m_ClassCounts[];
    private double m_ClassAttAttCounts[][][];
    private int m_NumAttValues[];
    private int m_TotalAttValues;
    private int m_NumClasses;
    private int m_NumAttributes;
    private int m_NumInstances;
    private int m_ClassIndex;
    private int m_StartAttIndex[];
    private double m_condiMutualInfo[][];


    public String globalInfo() {
        return (new StringBuilder()).append("Contructs Hidden Naive Bayes classification model with high classification accuracy and AUC.\n\nFor more information refer to:\n\n").append(getTechnicalInformation().toString()).toString();
    }

    public TechnicalInformation getTechnicalInformation() {
        TechnicalInformation technicalinformation = new TechnicalInformation(weka.core.TechnicalInformation.Type.INPROCEEDINGS);
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.AUTHOR, "H. Zhang and L. Jiang and J. Su");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.TITLE, "Hidden Naive Bayes");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.BOOKTITLE, "Twentieth National Conference on Artificial Intelligence");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.YEAR, "2005");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.PAGES, "919-924");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.PUBLISHER, "AAAI Press");
        return technicalinformation;
    }

    public Capabilities getCapabilities() {
        Capabilities capabilities = super.getCapabilities();
        capabilities.enable(weka.core.Capabilities.Capability.NOMINAL_ATTRIBUTES);
        capabilities.enable(weka.core.Capabilities.Capability.NOMINAL_CLASS);
        capabilities.enable(weka.core.Capabilities.Capability.MISSING_CLASS_VALUES);
        return capabilities;
    }

    public void buildClassifier(Instances instances) throws Exception {
        getCapabilities().testWithFail(instances);
        instances = new Instances(instances);
        instances.deleteWithMissingClass();
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
        m_ClassAttAttCounts = new double[m_NumClasses][m_TotalAttValues][m_TotalAttValues];
        for (int j = 0; j < m_NumInstances; j++) {
            int l = (int)instances.instance(j).classValue();
            m_ClassCounts[l]++;
            int ai[] = new int[m_NumAttributes];
            for (int j1 = 0; j1 < m_NumAttributes; j1++) {
                if (j1 == m_ClassIndex) {
                    ai[j1] = -1;
                } else {
                    ai[j1] = m_StartAttIndex[j1] + (int)instances.instance(j).value(j1);
                }
            }

            for (int k1 = 0; k1 < m_NumAttributes; k1++) {
                if (ai[k1] == -1) {
                    continue;
                }
                for (int l1 = 0; l1 < m_NumAttributes; l1++) {
                    if (ai[l1] != -1) {
                        m_ClassAttAttCounts[l][ai[k1]][ai[l1]]++;
                    }
                }

            }

        }

        m_condiMutualInfo = new double[m_NumAttributes][m_NumAttributes];
        for (int k = 0; k < m_NumAttributes; k++) {
            if (k == m_ClassIndex) {
                continue;
            }
            for (int i1 = 0; i1 < m_NumAttributes; i1++) {
                if (i1 != m_ClassIndex && k != i1) {
                    m_condiMutualInfo[k][i1] = conditionalMutualInfo(k, i1);
                }
            }

        }

    }

    private double conditionalMutualInfo(int i, int j) throws Exception {
        double d = 0.0D;
        int k = m_StartAttIndex[i];
        int l = m_StartAttIndex[j];
        double ad[] = new double[m_NumClasses];
        double ad1[][] = new double[m_NumClasses][m_NumAttValues[i]];
        double ad2[][] = new double[m_NumClasses][m_NumAttValues[j]];
        double ad3[][][] = new double[m_NumClasses][m_NumAttValues[j]][m_NumAttValues[i]];
        for (int i1 = 0; i1 < m_NumClasses; i1++) {
            ad[i1] = m_ClassCounts[i1] / (double)m_NumInstances;
        }

        for (int j1 = 0; j1 < m_NumClasses; j1++) {
            for (int j2 = 0; j2 < m_NumAttValues[i]; j2++) {
                ad1[j1][j2] = m_ClassAttAttCounts[j1][k + j2][k + j2] / (double)m_NumInstances;
            }

        }

        for (int k1 = 0; k1 < m_NumClasses; k1++) {
            for (int k2 = 0; k2 < m_NumAttValues[j]; k2++) {
                ad2[k1][k2] = m_ClassAttAttCounts[k1][l + k2][l + k2] / (double)m_NumInstances;
            }

        }

        for (int l1 = 0; l1 < m_NumClasses; l1++) {
            for (int l2 = 0; l2 < m_NumAttValues[j]; l2++) {
                for (int j3 = 0; j3 < m_NumAttValues[i]; j3++) {
                    ad3[l1][l2][j3] = m_ClassAttAttCounts[l1][l + l2][k + j3] / (double)m_NumInstances;
                }

            }

        }

        for (int i2 = 0; i2 < m_NumClasses; i2++) {
            for (int i3 = 0; i3 < m_NumAttValues[j]; i3++) {
                for (int k3 = 0; k3 < m_NumAttValues[i]; k3++) {
                    d += ad3[i2][i3][k3] * log2(ad3[i2][i3][k3] * ad[i2], ad2[i2][i3] * ad1[i2][k3]);
                }

            }

        }

        return d;
    }

    private double log2(double d, double d1) {
        if (d < 9.9999999999999995E-007D || d1 < 9.9999999999999995E-007D) {
            return 0.0D;
        } else {
            return Math.log(d / d1) / Math.log(2D);
        }
    }

    public double[] distributionForInstance(Instance instance) throws Exception {
        double ad[] = new double[m_NumClasses];
        int ai[] = new int[m_NumAttributes];
        for (int j = 0; j < m_NumAttributes; j++) {
            if (j == m_ClassIndex) {
                ai[j] = -1;
            } else {
                ai[j] = m_StartAttIndex[j] + (int)instance.value(j);
            }
        }

        for (int k = 0; k < m_NumClasses; k++) {
            ad[k] = (m_ClassCounts[k] + 1.0D / (double)m_NumClasses) / ((double)m_NumInstances + 1.0D);
            for (int l = 0; l < m_NumAttributes; l++) {
                if (ai[l] == -1) {
                    continue;
                }
                int i = ai[l];
                ai[l] = -1;
                double d = 0.0D;
                double d2 = 0.0D;
                for (int i1 = 0; i1 < m_NumAttributes; i1++) {
                    if (ai[i1] != -1) {
                        d2 += m_condiMutualInfo[l][i1];
                        d += (m_condiMutualInfo[l][i1] * (m_ClassAttAttCounts[k][ai[i1]][i] + 1.0D / (double)m_NumAttValues[l])) / (m_ClassAttAttCounts[k][ai[i1]][ai[i1]] + 1.0D);
                    }
                }

                if (d2 > 0.0D) {
                    d /= d2;
                    ad[k] *= d;
                } else {
                    double d1 = (m_ClassAttAttCounts[k][i][i] + 1.0D / (double)m_NumAttValues[l]) / (m_ClassCounts[k] + 1.0D);
                    ad[k] *= d1;
                }
                ai[l] = i;
            }

        }

        Utils.normalize(ad);
        return ad;
    }

    public String toString() {
        return "HNB (Hidden Naive Bayes)";
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.9 $");
    }

    public static void main(String args[]) {
        runClassifier(new HNB(), args);
    }
}
