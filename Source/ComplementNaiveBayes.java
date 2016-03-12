// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes;

import java.util.Enumeration;
import weka.classifiers.Classifier;
import weka.core.*;

public class ComplementNaiveBayes extends Classifier
    implements OptionHandler, WeightedInstancesHandler, TechnicalInformationHandler {

    static final long serialVersionUID = 0x64900a1e5953c33dL;
    private double wordWeights[][];
    private double smoothingParameter;
    private boolean m_normalizeWordWeights;
    private int numClasses;
    private Instances header;

    public ComplementNaiveBayes() {
        smoothingParameter = 1.0D;
        m_normalizeWordWeights = false;
    }

    public Enumeration listOptions() {
        FastVector fastvector = new FastVector(2);
        fastvector.addElement(new Option("\tNormalize the word weights for each class\n", "N", 0, "-N"));
        fastvector.addElement(new Option("\tSmoothing value to avoid zero WordGivenClass probabilities (default=1.0).\n", "S", 1, "-S"));
        return fastvector.elements();
    }

    public String[] getOptions() {
        String as[] = new String[4];
        int i = 0;
        if (getNormalizeWordWeights()) {
            as[i++] = "-N";
        }
        as[i++] = "-S";
        for (as[i++] = Double.toString(smoothingParameter); i < as.length; as[i++] = "") { }
        return as;
    }

    public void setOptions(String as[]) throws Exception {
        setNormalizeWordWeights(Utils.getFlag('N', as));
        String s = Utils.getOption('S', as);
        if (s.length() != 0) {
            setSmoothingParameter(Double.parseDouble(s));
        } else {
            setSmoothingParameter(1.0D);
        }
    }

    public boolean getNormalizeWordWeights() {
        return m_normalizeWordWeights;
    }

    public void setNormalizeWordWeights(boolean flag) {
        m_normalizeWordWeights = flag;
    }

    public String normalizeWordWeightsTipText() {
        return "Normalizes the word weights for each class.";
    }

    public double getSmoothingParameter() {
        return smoothingParameter;
    }

    public void setSmoothingParameter(double d) {
        smoothingParameter = d;
    }

    public String smoothingParameterTipText() {
        return "Sets the smoothing parameter to avoid zero WordGivenClass probabilities (default=1.0).";
    }

    public String globalInfo() {
        return (new StringBuilder()).append("Class for building and using a Complement class Naive Bayes classifier.\n\nFor more information see, \n\n").append(getTechnicalInformation().toString()).append("\n\n").append("P.S.: TF, IDF and length normalization transforms, as ").append("described in the paper, can be performed through ").append("weka.filters.unsupervised.StringToWordVector.").toString();
    }

    public TechnicalInformation getTechnicalInformation() {
        TechnicalInformation technicalinformation = new TechnicalInformation(weka.core.TechnicalInformation.Type.INPROCEEDINGS);
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.AUTHOR, "Jason D. Rennie and Lawrence Shih and Jaime Teevan and David R. Karger");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.TITLE, "Tackling the Poor Assumptions of Naive Bayes Text Classifiers");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.BOOKTITLE, "ICML");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.YEAR, "2003");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.PAGES, "616-623");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.PUBLISHER, "AAAI Press");
        return technicalinformation;
    }

    public Capabilities getCapabilities() {
        Capabilities capabilities = super.getCapabilities();
        capabilities.enable(weka.core.Capabilities.Capability.NUMERIC_ATTRIBUTES);
        capabilities.enable(weka.core.Capabilities.Capability.MISSING_VALUES);
        capabilities.enable(weka.core.Capabilities.Capability.NOMINAL_CLASS);
        capabilities.enable(weka.core.Capabilities.Capability.MISSING_CLASS_VALUES);
        return capabilities;
    }

    public void buildClassifier(Instances instances) throws Exception {
        getCapabilities().testWithFail(instances);
        instances = new Instances(instances);
        instances.deleteWithMissingClass();
        numClasses = instances.numClasses();
        int i = instances.numAttributes();
        header = new Instances(instances, 0);
        double ad[][] = new double[numClasses][i];
        wordWeights = new double[numClasses][i];
        double ad1[] = new double[numClasses];
        double d = 0.0D;
        double d1 = (double)(i - 1) * smoothingParameter;
        int j = instances.instance(0).classIndex();
        for (Enumeration enumeration = instances.enumerateInstances(); enumeration.hasMoreElements();) {
            Instance instance = (Instance)enumeration.nextElement();
            int k = (int)instance.value(j);
            int l = 0;
            while (l < instance.numValues())  {
                if (instance.index(l) != instance.classIndex() && !instance.isMissing(l)) {
                    double d2 = instance.valueSparse(l) * instance.weight();
                    if (d2 < 0.0D) {
                        throw new Exception("Numeric attribute values must all be greater or equal to zero.");
                    }
                    d += d2;
                    ad1[k] += d2;
                    ad[k][instance.index(l)] += d2;
                    wordWeights[0][instance.index(l)] += d2;
                }
                l++;
            }
        }

        for (int i1 = 1; i1 < numClasses; i1++) {
            double d3 = d - ad1[i1];
            for (int l1 = 0; l1 < i; l1++) {
                if (l1 != j) {
                    double d7 = wordWeights[0][l1] - ad[i1][l1];
                    wordWeights[i1][l1] = Math.log((d7 + smoothingParameter) / (d3 + d1));
                }
            }

        }

        for (int j1 = 0; j1 < i; j1++) {
            if (j1 != j) {
                double d4 = wordWeights[0][j1] - ad[0][j1];
                double d6 = d - ad1[0];
                wordWeights[0][j1] = Math.log((d4 + smoothingParameter) / (d6 + d1));
            }
        }

        if (m_normalizeWordWeights) {
            for (int k1 = 0; k1 < numClasses; k1++) {
                double d5 = 0.0D;
                for (int i2 = 0; i2 < i; i2++) {
                    if (i2 != j) {
                        d5 += Math.abs(wordWeights[k1][i2]);
                    }
                }

                for (int j2 = 0; j2 < i; j2++) {
                    if (j2 != j) {
                        wordWeights[k1][j2] = wordWeights[k1][j2] / d5;
                    }
                }

            }

        }
    }

    public double classifyInstance(Instance instance) throws Exception {
        if (wordWeights == null) {
            throw new Exception("Error. The classifier has not been built properly.");
        }
        double ad[] = new double[numClasses];
        double d = 0.0D;
        for (int i = 0; i < numClasses; i++) {
            double d1 = 0.0D;
            for (int l = 0; l < instance.numValues(); l++) {
                if (instance.index(l) != instance.classIndex()) {
                    double d2 = instance.valueSparse(l);
                    d1 += d2 * wordWeights[i][instance.index(l)];
                }
            }

            ad[i] = d1;
            d += ad[i];
        }

        int j = 0;
        for (int k = 0; k < numClasses; k++) {
            if (ad[k] < ad[j]) {
                j = k;
            }
        }

        return (double)j;
    }

    public String toString() {
        if (wordWeights == null) {
            return "The classifier hasn't been built yet.";
        }
        int i = header.numAttributes();
        StringBuffer stringbuffer = new StringBuffer("The word weights for each class are: \n------------------------------------\n\t");
        for (int j = 0; j < numClasses; j++) {
            stringbuffer.append(header.classAttribute().value(j)).append("\t");
        }

        stringbuffer.append("\n");
        for (int k = 0; k < i; k++) {
            stringbuffer.append(header.attribute(k).name()).append("\t");
            for (int l = 0; l < numClasses; l++) {
                stringbuffer.append(Double.toString(wordWeights[l][k])).append("\t");
            }

            stringbuffer.append("\n");
        }

        return stringbuffer.toString();
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.9 $");
    }

    public static void main(String args[]) {
        runClassifier(new ComplementNaiveBayes(), args);
    }
}
