// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes;

import java.util.Enumeration;
import weka.classifiers.Classifier;
import weka.core.*;

public class NaiveBayesSimple extends Classifier
    implements TechnicalInformationHandler {

    static final long serialVersionUID = 0xeb7c3a810b40bc62L;
    protected double m_Counts[][][];
    protected double m_Means[][];
    protected double m_Devs[][];
    protected double m_Priors[];
    protected Instances m_Instances;
    protected static double NORM_CONST = Math.sqrt(6.2831853071795862D);


    public String globalInfo() {
        return (new StringBuilder()).append("Class for building and using a simple Naive Bayes classifier.Numeric attributes are modelled by a normal distribution.\n\nFor more information, see\n\n").append(getTechnicalInformation().toString()).toString();
    }

    public TechnicalInformation getTechnicalInformation() {
        TechnicalInformation technicalinformation = new TechnicalInformation(weka.core.TechnicalInformation.Type.BOOK);
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.AUTHOR, "Richard Duda and Peter Hart");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.YEAR, "1973");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.TITLE, "Pattern Classification and Scene Analysis");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.PUBLISHER, "Wiley");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.ADDRESS, "New York");
        return technicalinformation;
    }

    public Capabilities getCapabilities() {
        Capabilities capabilities = super.getCapabilities();
        capabilities.enable(weka.core.Capabilities.Capability.NOMINAL_ATTRIBUTES);
        capabilities.enable(weka.core.Capabilities.Capability.NUMERIC_ATTRIBUTES);
        capabilities.enable(weka.core.Capabilities.Capability.DATE_ATTRIBUTES);
        capabilities.enable(weka.core.Capabilities.Capability.MISSING_VALUES);
        capabilities.enable(weka.core.Capabilities.Capability.NOMINAL_CLASS);
        capabilities.enable(weka.core.Capabilities.Capability.MISSING_CLASS_VALUES);
        return capabilities;
    }

    public void buildClassifier(Instances instances) throws Exception {
        int i = 0;
        getCapabilities().testWithFail(instances);
        instances = new Instances(instances);
        instances.deleteWithMissingClass();
        m_Instances = new Instances(instances, 0);
        m_Counts = new double[instances.numClasses()][instances.numAttributes() - 1][0];
        m_Means = new double[instances.numClasses()][instances.numAttributes() - 1];
        m_Devs = new double[instances.numClasses()][instances.numAttributes() - 1];
        m_Priors = new double[instances.numClasses()];
        for (Enumeration enumeration = instances.enumerateAttributes(); enumeration.hasMoreElements(); i++) {
            Attribute attribute = (Attribute)enumeration.nextElement();
            if (attribute.isNominal()) {
                for (int k1 = 0; k1 < instances.numClasses(); k1++) {
                    m_Counts[k1][i] = new double[attribute.numValues()];
                }

                continue;
            }
            for (int l1 = 0; l1 < instances.numClasses(); l1++) {
                m_Counts[l1][i] = new double[1];
            }

        }

        Enumeration enumeration1 = instances.enumerateInstances();
        do {
            if (!enumeration1.hasMoreElements()) {
                break;
            }
            Instance instance = (Instance)enumeration1.nextElement();
            if (!instance.classIsMissing()) {
                Enumeration enumeration3 = instances.enumerateAttributes();
                for (int j = 0; enumeration3.hasMoreElements(); j++) {
                    Attribute attribute4 = (Attribute)enumeration3.nextElement();
                    if (instance.isMissing(attribute4)) {
                        continue;
                    }
                    if (attribute4.isNominal()) {
                        m_Counts[(int)instance.classValue()][j][(int)instance.value(attribute4)]++;
                    } else {
                        m_Means[(int)instance.classValue()][j] += instance.value(attribute4);
                        m_Counts[(int)instance.classValue()][j][0]++;
                    }
                }

                m_Priors[(int)instance.classValue()]++;
            }
        } while (true);
        Enumeration enumeration2 = instances.enumerateAttributes();
label0:
        for (int k = 0; enumeration2.hasMoreElements(); k++) {
            Attribute attribute1 = (Attribute)enumeration2.nextElement();
            if (!attribute1.isNumeric()) {
                continue;
            }
            int j2 = 0;
            do {
                if (j2 >= instances.numClasses()) {
                    continue label0;
                }
                if (m_Counts[j2][k][0] < 2D) {
                    throw new Exception((new StringBuilder()).append("attribute ").append(attribute1.name()).append(": less than two values for class ").append(instances.classAttribute().value(j2)).toString());
                }
                m_Means[j2][k] /= m_Counts[j2][k][0];
                j2++;
            } while (true);
        }

        enumeration1 = instances.enumerateInstances();
        do {
            if (!enumeration1.hasMoreElements()) {
                break;
            }
            Instance instance1 = (Instance)enumeration1.nextElement();
            if (!instance1.classIsMissing()) {
                enumeration2 = instances.enumerateAttributes();
                int l = 0;
                while (enumeration2.hasMoreElements())  {
                    Attribute attribute5 = (Attribute)enumeration2.nextElement();
                    if (!instance1.isMissing(attribute5) && attribute5.isNumeric()) {
                        m_Devs[(int)instance1.classValue()][l] += (m_Means[(int)instance1.classValue()][l] - instance1.value(attribute5)) * (m_Means[(int)instance1.classValue()][l] - instance1.value(attribute5));
                    }
                    l++;
                }
            }
        } while (true);
        enumeration2 = instances.enumerateAttributes();
label1:
        for (int i1 = 0; enumeration2.hasMoreElements(); i1++) {
            Attribute attribute2 = (Attribute)enumeration2.nextElement();
            if (!attribute2.isNumeric()) {
                continue;
            }
            int k2 = 0;
            do {
                if (k2 >= instances.numClasses()) {
                    continue label1;
                }
                if (m_Devs[k2][i1] <= 0.0D) {
                    throw new Exception((new StringBuilder()).append("attribute ").append(attribute2.name()).append(": standard deviation is 0 for class ").append(instances.classAttribute().value(k2)).toString());
                }
                m_Devs[k2][i1] /= m_Counts[k2][i1][0] - 1.0D;
                m_Devs[k2][i1] = Math.sqrt(m_Devs[k2][i1]);
                k2++;
            } while (true);
        }

        enumeration2 = instances.enumerateAttributes();
label2:
        for (int j1 = 0; enumeration2.hasMoreElements(); j1++) {
            Attribute attribute3 = (Attribute)enumeration2.nextElement();
            if (!attribute3.isNominal()) {
                continue;
            }
            int l2 = 0;
            do {
                if (l2 >= instances.numClasses()) {
                    continue label2;
                }
                double d = Utils.sum(m_Counts[l2][j1]);
                for (int i3 = 0; i3 < attribute3.numValues(); i3++) {
                    m_Counts[l2][j1][i3] = (m_Counts[l2][j1][i3] + 1.0D) / (d + (double)attribute3.numValues());
                }

                l2++;
            } while (true);
        }

        double d1 = Utils.sum(m_Priors);
        for (int i2 = 0; i2 < instances.numClasses(); i2++) {
            m_Priors[i2] = (m_Priors[i2] + 1.0D) / (d1 + (double)instances.numClasses());
        }

    }

    public double[] distributionForInstance(Instance instance) throws Exception {
        double ad[] = new double[instance.numClasses()];
        for (int j = 0; j < instance.numClasses(); j++) {
            ad[j] = 1.0D;
            Enumeration enumeration = instance.enumerateAttributes();
            for (int i = 0; enumeration.hasMoreElements(); i++) {
                Attribute attribute = (Attribute)enumeration.nextElement();
                if (instance.isMissing(attribute)) {
                    continue;
                }
                if (attribute.isNominal()) {
                    ad[j] *= m_Counts[j][i][(int)instance.value(attribute)];
                } else {
                    ad[j] *= normalDens(instance.value(attribute), m_Means[j][i], m_Devs[j][i]);
                }
            }

            ad[j] *= m_Priors[j];
        }

        Utils.normalize(ad);
        return ad;
    }

    public String toString() {
        if (m_Instances == null) {
            return "Naive Bayes (simple): No model built yet.";
        }
        StringBuffer stringbuffer;
        stringbuffer = new StringBuffer("Naive Bayes (simple)");
        for (int j = 0; j < m_Instances.numClasses(); j++) {
            stringbuffer.append((new StringBuilder()).append("\n\nClass ").append(m_Instances.classAttribute().value(j)).append(": P(C) = ").append(Utils.doubleToString(m_Priors[j], 10, 8)).append("\n\n").toString());
            Enumeration enumeration = m_Instances.enumerateAttributes();
            for (int i = 0; enumeration.hasMoreElements(); i++) {
                Attribute attribute = (Attribute)enumeration.nextElement();
                stringbuffer.append((new StringBuilder()).append("Attribute ").append(attribute.name()).append("\n").toString());
                if (attribute.isNominal()) {
                    for (int k = 0; k < attribute.numValues(); k++) {
                        stringbuffer.append((new StringBuilder()).append(attribute.value(k)).append("\t").toString());
                    }

                    stringbuffer.append("\n");
                    for (int l = 0; l < attribute.numValues(); l++) {
                        stringbuffer.append((new StringBuilder()).append(Utils.doubleToString(m_Counts[j][i][l], 10, 8)).append("\t").toString());
                    }

                } else {
                    stringbuffer.append((new StringBuilder()).append("Mean: ").append(Utils.doubleToString(m_Means[j][i], 10, 8)).append("\t").toString());
                    stringbuffer.append((new StringBuilder()).append("Standard Deviation: ").append(Utils.doubleToString(m_Devs[j][i], 10, 8)).toString());
                }
                stringbuffer.append("\n\n");
            }

        }

        return stringbuffer.toString();
        Exception exception;
        exception;
        return "Can't print Naive Bayes classifier!";
    }

    protected double normalDens(double d, double d1, double d2) {
        double d3 = d - d1;
        return (1.0D / (NORM_CONST * d2)) * Math.exp(-((d3 * d3) / (2D * d2 * d2)));
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.20 $");
    }

    public static void main(String args[]) {
        runClassifier(new NaiveBayesSimple(), args);
    }

}
