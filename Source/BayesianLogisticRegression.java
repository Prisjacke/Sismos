// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes;

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.blr.GaussianPriorImpl;
import weka.classifiers.bayes.blr.LaplacePriorImpl;
import weka.classifiers.bayes.blr.Prior;
import weka.core.*;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

public class BayesianLogisticRegression extends Classifier
    implements OptionHandler, TechnicalInformationHandler {

    static final long serialVersionUID = 0x90ca67664ae184c1L;
    public static double LogLikelihood[];
    public static double InputHyperparameterValues[];
    boolean debug;
    public boolean NormalizeData;
    public double Tolerance;
    public double Threshold;
    public static final int GAUSSIAN = 1;
    public static final int LAPLACIAN = 2;
    public static final Tag TAGS_PRIOR[] = {
        new Tag(1, "Gaussian"), new Tag(2, "Laplacian")
    };
    public int PriorClass;
    public int NumFolds;
    public static final int NORM_BASED = 1;
    public static final int CV_BASED = 2;
    public static final int SPECIFIC_VALUE = 3;
    public static final Tag TAGS_HYPER_METHOD[] = {
        new Tag(1, "Norm-based"), new Tag(2, "CV-based"), new Tag(3, "Specific value")
    };
    public int HyperparameterSelection;
    public int ClassIndex;
    public double HyperparameterValue;
    public String HyperparameterRange;
    public int maxIterations;
    public int iterationCounter;
    public double BetaVector[];
    public double DeltaBeta[];
    public double DeltaUpdate[];
    public double Delta[];
    public double Hyperparameters[];
    public double R[];
    public double DeltaR[];
    public double Change;
    public Filter m_Filter;
    protected Instances m_Instances;
    protected Prior m_PriorUpdate;

    public BayesianLogisticRegression() {
        debug = false;
        NormalizeData = false;
        Tolerance = 0.00050000000000000001D;
        Threshold = 0.5D;
        PriorClass = 1;
        NumFolds = 2;
        HyperparameterSelection = 1;
        ClassIndex = -1;
        HyperparameterValue = 0.27000000000000002D;
        HyperparameterRange = "R:0.01-316,3.16";
        maxIterations = 100;
        iterationCounter = 0;
    }

    public String globalInfo() {
        return (new StringBuilder()).append("Implements Bayesian Logistic Regression for both Gaussian and Laplace Priors.\n\nFor more information, see\n\n").append(getTechnicalInformation()).toString();
    }

    public void initialize() throws Exception {
        Change = 0.0D;
        if (NormalizeData) {
            m_Filter = new Normalize();
            m_Filter.setInputFormat(m_Instances);
            m_Instances = Filter.useFilter(m_Instances, m_Filter);
        }
        Attribute attribute = new Attribute("(intercept)");
        m_Instances.insertAttributeAt(attribute, 0);
        for (int k = 0; k < m_Instances.numInstances(); k++) {
            Instance instance = m_Instances.instance(k);
            instance.setValue(0, 1.0D);
        }

        int i = m_Instances.numAttributes();
        int j = m_Instances.numInstances();
        ClassIndex = m_Instances.classIndex();
        iterationCounter = 0;
        switch (HyperparameterSelection) {
        default:
            break;

        case 1: // '\001'
            HyperparameterValue = normBasedHyperParameter();
            if (debug) {
                System.out.println((new StringBuilder()).append("Norm-based Hyperparameter: ").append(HyperparameterValue).toString());
            }
            break;

        case 2: // '\002'
            HyperparameterValue = CVBasedHyperparameter();
            if (debug) {
                System.out.println((new StringBuilder()).append("CV-based Hyperparameter: ").append(HyperparameterValue).toString());
            }
            break;
        }
        BetaVector = new double[i];
        Delta = new double[i];
        DeltaBeta = new double[i];
        Hyperparameters = new double[i];
        DeltaUpdate = new double[i];
        for (int i1 = 0; i1 < i; i1++) {
            BetaVector[i1] = 0.0D;
            Delta[i1] = 1.0D;
            DeltaBeta[i1] = 0.0D;
            DeltaUpdate[i1] = 0.0D;
            Hyperparameters[i1] = HyperparameterValue;
        }

        DeltaR = new double[j];
        R = new double[j];
        for (int l = 0; l < j; l++) {
            DeltaR[l] = 0.0D;
            R[l] = 0.0D;
        }

        if (PriorClass == 1) {
            m_PriorUpdate = new GaussianPriorImpl();
        } else {
            m_PriorUpdate = new LaplacePriorImpl();
        }
    }

    public Capabilities getCapabilities() {
        Capabilities capabilities = super.getCapabilities();
        capabilities.enable(weka.core.Capabilities.Capability.NUMERIC_ATTRIBUTES);
        capabilities.enable(weka.core.Capabilities.Capability.BINARY_ATTRIBUTES);
        capabilities.enable(weka.core.Capabilities.Capability.BINARY_CLASS);
        capabilities.setMinimumNumberInstances(0);
        return capabilities;
    }

    public void buildClassifier(Instances instances) throws Exception {
        getCapabilities().testWithFail(instances);
        m_Instances = new Instances(instances);
        initialize();
        do {
            for (int j = 0; j < m_Instances.numAttributes(); j++) {
                if (j == ClassIndex) {
                    continue;
                }
                DeltaUpdate[j] = m_PriorUpdate.update(j, m_Instances, BetaVector[j], Hyperparameters[j], R, Delta[j]);
                DeltaBeta[j] = Math.min(Math.max(DeltaUpdate[j], 0.0D - Delta[j]), Delta[j]);
                for (int i = 0; i < m_Instances.numInstances(); i++) {
                    Instance instance = m_Instances.instance(i);
                    if (instance.value(j) != 0.0D) {
                        DeltaR[i] = DeltaBeta[j] * instance.value(j) * classSgn(instance.classValue());
                        R[i] += DeltaR[i];
                    }
                }

                BetaVector[j] += DeltaBeta[j];
                Delta[j] = Math.max(2D * Math.abs(DeltaBeta[j]), Delta[j] / 2D);
            }

        } while (!stoppingCriterion());
        m_PriorUpdate.computelogLikelihood(BetaVector, m_Instances);
        m_PriorUpdate.computePenalty(BetaVector, Hyperparameters);
    }

    public static double classSgn(double d) {
        return d != 0.0D ? 1.0D : -1D;
    }

    public TechnicalInformation getTechnicalInformation() {
        TechnicalInformation technicalinformation = null;
        technicalinformation = new TechnicalInformation(weka.core.TechnicalInformation.Type.TECHREPORT);
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.AUTHOR, "Alexander Genkin and David D. Lewis and David Madigan");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.YEAR, "2004");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.TITLE, "Large-scale bayesian logistic regression for text categorization");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.INSTITUTION, "DIMACS");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.URL, "http://www.stat.rutgers.edu/~madigan/PAPERS/shortFat-v3a.pdf");
        return technicalinformation;
    }

    public static double bigF(double d, double d1) {
        double d2 = 0.25D;
        double d3 = Math.abs(d);
        if (d3 > d1) {
            d2 = 1.0D / (2D + Math.exp(d3 - d1) + Math.exp(d1 - d3));
        }
        return d2;
    }

    public boolean stoppingCriterion() {
        double d = 0.0D;
        double d1 = 1.0D;
        double d2 = 0.0D;
        for (int i = 0; i < m_Instances.numInstances(); i++) {
            d += Math.abs(DeltaR[i]);
            d1 += Math.abs(R[i]);
        }

        double d3 = Math.abs(d - Change);
        Change = d3 / d1;
        if (debug) {
            System.out.println((new StringBuilder()).append(Change).append(" <= ").append(Tolerance).toString());
        }
        boolean flag = Change <= Tolerance || iterationCounter >= maxIterations;
        iterationCounter++;
        Change = d;
        return flag;
    }

    public static double logisticLinkFunction(double d) {
        return Math.exp(d) / (1.0D + Math.exp(d));
    }

    public static double sgn(double d) {
        double d1 = 0.0D;
        if (d > 0.0D) {
            d1 = 1.0D;
        } else
        if (d < 0.0D) {
            d1 = -1D;
        }
        return d1;
    }

    public double normBasedHyperParameter() {
        double d = 0.0D;
        for (int i = 0; i < m_Instances.numInstances(); i++) {
            Instance instance = m_Instances.instance(i);
            double d1 = 0.0D;
            for (int j = 0; j < m_Instances.numAttributes(); j++) {
                if (j != ClassIndex) {
                    d1 += instance.value(j) * instance.value(j);
                }
            }

            d += d1;
        }

        d /= m_Instances.numInstances();
        return (double)m_Instances.numAttributes() / d;
    }

    public double classifyInstance(Instance instance) throws Exception {
        double d = 0.0D;
        double d1 = 0.0D;
        d = BetaVector[0];
        for (int i = 0; i < instance.numAttributes(); i++) {
            if (i != ClassIndex - 1) {
                d += BetaVector[i + 1] * instance.value(i);
            }
        }

        d = logisticLinkFunction(d);
        if (d > Threshold) {
            d1 = 1.0D;
        } else {
            d1 = 0.0D;
        }
        return d1;
    }

    public String toString() {
        if (m_Instances == null) {
            return "Bayesian logistic regression: No model built yet.";
        }
        StringBuffer stringbuffer = new StringBuffer();
        String s = "";
        switch (HyperparameterSelection) {
        case 1: // '\001'
            s = "Norm-Based Hyperparameter Selection: ";
            break;

        case 2: // '\002'
            s = "Cross-Validation Based Hyperparameter Selection: ";
            break;

        case 3: // '\003'
            s = "Specified Hyperparameter: ";
            break;
        }
        stringbuffer.append(s).append(HyperparameterValue).append("\n\n");
        stringbuffer.append("Regression Coefficients\n");
        stringbuffer.append("=========================\n\n");
        for (int i = 0; i < m_Instances.numAttributes(); i++) {
            if (i != ClassIndex && BetaVector[i] != 0.0D) {
                stringbuffer.append(m_Instances.attribute(i).name()).append(" : ").append(BetaVector[i]).append("\n");
            }
        }

        stringbuffer.append("===========================\n\n");
        stringbuffer.append((new StringBuilder()).append("Likelihood: ").append(m_PriorUpdate.getLoglikelihood()).append("\n\n").toString());
        stringbuffer.append((new StringBuilder()).append("Penalty: ").append(m_PriorUpdate.getPenalty()).append("\n\n").toString());
        stringbuffer.append((new StringBuilder()).append("Regularized Log Posterior: ").append(m_PriorUpdate.getLogPosterior()).append("\n").toString());
        stringbuffer.append("===========================\n\n");
        return stringbuffer.toString();
    }

    public double CVBasedHyperparameter() throws Exception {
        boolean flag = false;
        double ad[] = null;
        double d3 = 0.0D;
        double d4 = 0.0D;
        StringTokenizer stringtokenizer = new StringTokenizer(HyperparameterRange);
        String s = stringtokenizer.nextToken(":");
        if (s.equals("R")) {
            String s1 = stringtokenizer.nextToken();
            stringtokenizer = new StringTokenizer(s1);
            double d = Double.parseDouble(stringtokenizer.nextToken("-"));
            stringtokenizer = new StringTokenizer(stringtokenizer.nextToken());
            double d1 = Double.parseDouble(stringtokenizer.nextToken(","));
            double d2 = Double.parseDouble(stringtokenizer.nextToken());
            int j = (int)((Math.log10(d1) - Math.log10(d)) / Math.log10(d2) + 1.0D);
            ad = new double[j];
            int l = 0;
            for (double d5 = d; d5 <= d1; d5 *= d2) {
                ad[l++] = d5;
            }

        } else
        if (s.equals("L")) {
            Vector vector = new Vector();
            for (; stringtokenizer.hasMoreTokens(); vector.add(stringtokenizer.nextToken(","))) { }
            ad = new double[vector.size()];
            for (int k = 0; k < vector.size(); k++) {
                ad[k] = Double.parseDouble((String)vector.get(k));
            }

        }
        if (ad != null) {
            int i = NumFolds;
            Random random = new Random();
            m_Instances.randomize(random);
            m_Instances.stratify(i);
            for (int i1 = 0; i1 < ad.length; i1++) {
                for (int j1 = 0; j1 < i; j1++) {
                    Instances instances = m_Instances.trainCV(i, j1, random);
                    SerializedObject serializedobject = new SerializedObject(this);
                    BayesianLogisticRegression bayesianlogisticregression = (BayesianLogisticRegression)serializedobject.getObject();
                    bayesianlogisticregression.setHyperparameterSelection(new SelectedTag(3, TAGS_HYPER_METHOD));
                    bayesianlogisticregression.setHyperparameterValue(ad[i1]);
                    bayesianlogisticregression.setPriorClass(new SelectedTag(PriorClass, TAGS_PRIOR));
                    bayesianlogisticregression.setThreshold(Threshold);
                    bayesianlogisticregression.setTolerance(Tolerance);
                    bayesianlogisticregression.buildClassifier(instances);
                    Instances instances1 = m_Instances.testCV(i, j1);
                    double d6 = bayesianlogisticregression.getLoglikeliHood(bayesianlogisticregression.BetaVector, instances1);
                    if (debug) {
                        System.out.println((new StringBuilder()).append("Fold ").append(j1).append("Hyperparameter: ").append(ad[i1]).toString());
                        System.out.println("===================================");
                        System.out.println((new StringBuilder()).append(" Likelihood: ").append(d6).toString());
                    }
                    if ((i1 == 0) | (d6 > d4)) {
                        d4 = d6;
                        d3 = ad[i1];
                    }
                }

            }

        } else {
            return HyperparameterValue;
        }
        return d3;
    }

    public double getLoglikeliHood(double ad[], Instances instances) {
        m_PriorUpdate.computelogLikelihood(ad, instances);
        return m_PriorUpdate.getLoglikelihood();
    }

    public Enumeration listOptions() {
        Vector vector = new Vector();
        vector.addElement(new Option("\tShow Debugging Output\n", "D", 0, "-D"));
        vector.addElement(new Option("\tDistribution of the Prior (1=Gaussian, 2=Laplacian)\n\t(default: 1=Gaussian)", "P", 1, "-P <integer>"));
        vector.addElement(new Option("\tHyperparameter Selection Method (1=Norm-based, 2=CV-based, 3=specific value)\n\t(default: 1=Norm-based)", "H", 1, "-H <integer>"));
        vector.addElement(new Option("\tSpecified Hyperparameter Value (use in conjunction with -H 3)\n\t(default: 0.27)", "V", 1, "-V <double>"));
        vector.addElement(new Option("\tHyperparameter Range (use in conjunction with -H 2)\n\t(format: R:start-end,multiplier OR L:val(1), val(2), ..., val(n))\n\t(default: R:0.01-316,3.16)", "R", 1, "-R <string>"));
        vector.addElement(new Option("\tTolerance Value\n\t(default: 0.0005)", "Tl", 1, "-Tl <double>"));
        vector.addElement(new Option("\tThreshold Value\n\t(default: 0.5)", "S", 1, "-S <double>"));
        vector.addElement(new Option("\tNumber Of Folds (use in conjuction with -H 2)\n\t(default: 2)", "F", 1, "-F <integer>"));
        vector.addElement(new Option("\tMax Number of Iterations\n\t(default: 100)", "I", 1, "-I <integer>"));
        vector.addElement(new Option("\tNormalize the data", "N", 0, "-N"));
        return vector.elements();
    }

    public void setOptions(String as[]) throws Exception {
        debug = Utils.getFlag('D', as);
        String s = Utils.getOption("Tl", as);
        if (s.length() != 0) {
            Tolerance = Double.parseDouble(s);
        }
        String s1 = Utils.getOption('S', as);
        if (s1.length() != 0) {
            Threshold = Double.parseDouble(s1);
        }
        String s2 = Utils.getOption('H', as);
        if (s2.length() != 0) {
            HyperparameterSelection = Integer.parseInt(s2);
        }
        String s3 = Utils.getOption('V', as);
        if (s3.length() != 0) {
            HyperparameterValue = Double.parseDouble(s3);
        }
        String s4 = Utils.getOption("R", as);
        String s5 = Utils.getOption('P', as);
        if (s5.length() != 0) {
            PriorClass = Integer.parseInt(s5);
        }
        String s6 = Utils.getOption('F', as);
        if (s6.length() != 0) {
            NumFolds = Integer.parseInt(s6);
        }
        String s7 = Utils.getOption('I', as);
        if (s7.length() != 0) {
            maxIterations = Integer.parseInt(s7);
        }
        NormalizeData = Utils.getFlag('N', as);
        Utils.checkForRemainingOptions(as);
    }

    public String[] getOptions() {
        Vector vector = new Vector();
        vector.add("-D");
        vector.add("-Tl");
        vector.add((new StringBuilder()).append("").append(Tolerance).toString());
        vector.add("-S");
        vector.add((new StringBuilder()).append("").append(Threshold).toString());
        vector.add("-H");
        vector.add((new StringBuilder()).append("").append(HyperparameterSelection).toString());
        vector.add("-V");
        vector.add((new StringBuilder()).append("").append(HyperparameterValue).toString());
        vector.add("-R");
        vector.add((new StringBuilder()).append("").append(HyperparameterRange).toString());
        vector.add("-P");
        vector.add((new StringBuilder()).append("").append(PriorClass).toString());
        vector.add("-F");
        vector.add((new StringBuilder()).append("").append(NumFolds).toString());
        vector.add("-I");
        vector.add((new StringBuilder()).append("").append(maxIterations).toString());
        vector.add("-N");
        return (String[])(String[])vector.toArray(new String[vector.size()]);
    }

    public static void main(String args[]) {
        runClassifier(new BayesianLogisticRegression(), args);
    }

    public String debugTipText() {
        return "Turns on debugging mode.";
    }

    public void setDebug(boolean flag) {
        debug = flag;
    }

    public String hyperparameterSelectionTipText() {
        return "Select the type of Hyperparameter to be used.";
    }

    public SelectedTag getHyperparameterSelection() {
        return new SelectedTag(HyperparameterSelection, TAGS_HYPER_METHOD);
    }

    public void setHyperparameterSelection(SelectedTag selectedtag) {
        if (selectedtag.getTags() == TAGS_HYPER_METHOD) {
            int i = selectedtag.getSelectedTag().getID();
            if (i >= 1 && i <= 3) {
                HyperparameterSelection = i;
            } else {
                throw new IllegalArgumentException("Wrong selection type, -H value should be: 1 for norm-based, 2 for CV-based and 3 for specific value");
            }
        }
    }

    public String priorClassTipText() {
        return "The type of prior to be used.";
    }

    public void setPriorClass(SelectedTag selectedtag) {
        if (selectedtag.getTags() == TAGS_PRIOR) {
            int i = selectedtag.getSelectedTag().getID();
            if (i == 1 || i == 2) {
                PriorClass = i;
            } else {
                throw new IllegalArgumentException("Wrong selection type, -P value should be: 1 for Gaussian or 2 for Laplacian");
            }
        }
    }

    public SelectedTag getPriorClass() {
        return new SelectedTag(PriorClass, TAGS_PRIOR);
    }

    public String thresholdTipText() {
        return "Set the threshold for classifiction. The logistic function doesn't return a class label but an estimate of p(y=+1|B,x(i)). These estimates need to be converted to binary class label predictions. values above the threshold are assigned class +1.";
    }

    public double getThreshold() {
        return Threshold;
    }

    public void setThreshold(double d) {
        Threshold = d;
    }

    public String toleranceTipText() {
        return "This value decides the stopping criterion.";
    }

    public double getTolerance() {
        return Tolerance;
    }

    public void setTolerance(double d) {
        Tolerance = d;
    }

    public String hyperparameterValueTipText() {
        return "Specific hyperparameter value. Used when the hyperparameter selection method is set to specific value";
    }

    public double getHyperparameterValue() {
        return HyperparameterValue;
    }

    public void setHyperparameterValue(double d) {
        HyperparameterValue = d;
    }

    public String numFoldsTipText() {
        return "The number of folds to use for CV-based hyperparameter selection.";
    }

    public int getNumFolds() {
        return NumFolds;
    }

    public void setNumFolds(int i) {
        NumFolds = i;
    }

    public String maxIterationsTipText() {
        return "The maximum number of iterations to perform.";
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public void setMaxIterations(int i) {
        maxIterations = i;
    }

    public String normalizeDataTipText() {
        return "Normalize the data.";
    }

    public boolean isNormalizeData() {
        return NormalizeData;
    }

    public void setNormalizeData(boolean flag) {
        NormalizeData = flag;
    }

    public String hyperparameterRangeTipText() {
        return "Hyperparameter value range. In case of CV-based Hyperparameters, you can specify the range in two ways: \nComma-Separated: L: 3,5,6 (This will be a list of possible values.)\nRange: R:0.01-316,3.16 (This will take values from 0.01-316 (inclusive) in multiplications of 3.16";
    }

    public String getHyperparameterRange() {
        return HyperparameterRange;
    }

    public void setHyperparameterRange(String s) {
        HyperparameterRange = s;
    }

    public boolean isDebug() {
        return debug;
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.3 $");
    }

}
