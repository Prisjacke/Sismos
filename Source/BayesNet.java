// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes;

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.net.ADNode;
import weka.classifiers.bayes.net.BIFReader;
import weka.classifiers.bayes.net.ParentSet;
import weka.classifiers.bayes.net.estimate.BayesNetEstimator;
import weka.classifiers.bayes.net.estimate.DiscreteEstimatorBayes;
import weka.classifiers.bayes.net.estimate.SimpleEstimator;
import weka.classifiers.bayes.net.search.SearchAlgorithm;
import weka.classifiers.bayes.net.search.local.K2;
import weka.classifiers.bayes.net.search.local.LocalScoreSearchAlgorithm;
import weka.core.*;
import weka.estimators.Estimator;
import weka.filters.Filter;
import weka.filters.supervised.attribute.Discretize;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

public class BayesNet extends Classifier
    implements OptionHandler, WeightedInstancesHandler, Drawable, AdditionalMeasureProducer {

    static final long serialVersionUID = 0xa5a751a1a947992L;
    protected ParentSet m_ParentSets[];
    public Estimator m_Distributions[][];
    protected Discretize m_DiscretizeFilter;
    int m_nNonDiscreteAttribute;
    protected ReplaceMissingValues m_MissingValuesFilter;
    protected int m_NumClasses;
    public Instances m_Instances;
    ADNode m_ADTree;
    protected BIFReader m_otherBayesNet;
    boolean m_bUseADTree;
    SearchAlgorithm m_SearchAlgorithm;
    BayesNetEstimator m_BayesNetEstimator;

    public BayesNet() {
        m_DiscretizeFilter = null;
        m_nNonDiscreteAttribute = -1;
        m_MissingValuesFilter = null;
        m_otherBayesNet = null;
        m_bUseADTree = false;
        m_SearchAlgorithm = new K2();
        m_BayesNetEstimator = new SimpleEstimator();
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
        instances = normalizeDataSet(instances);
        m_Instances = new Instances(instances);
        m_NumClasses = instances.numClasses();
        if (m_bUseADTree) {
            m_ADTree = ADNode.makeADTree(instances);
        }
        initStructure();
        buildStructure();
        estimateCPTs();
        m_ADTree = null;
    }

    protected Instances normalizeDataSet(Instances instances) throws Exception {
        m_DiscretizeFilter = null;
        m_MissingValuesFilter = null;
        boolean flag = false;
        boolean flag1 = false;
        for (Enumeration enumeration = instances.enumerateAttributes(); enumeration.hasMoreElements();) {
            Attribute attribute = (Attribute)enumeration.nextElement();
            if (attribute.type() != 1) {
                m_nNonDiscreteAttribute = attribute.index();
                flag = true;
            }
            Enumeration enumeration1 = instances.enumerateInstances();
            while (enumeration1.hasMoreElements())  {
                if (((Instance)enumeration1.nextElement()).isMissing(attribute)) {
                    flag1 = true;
                }
            }
        }

        if (flag) {
            System.err.println("Warning: discretizing data set");
            m_DiscretizeFilter = new Discretize();
            m_DiscretizeFilter.setInputFormat(instances);
            instances = Filter.useFilter(instances, m_DiscretizeFilter);
        }
        if (flag1) {
            System.err.println("Warning: filling in missing values in data set");
            m_MissingValuesFilter = new ReplaceMissingValues();
            m_MissingValuesFilter.setInputFormat(instances);
            instances = Filter.useFilter(instances, m_MissingValuesFilter);
        }
        return instances;
    }

    protected Instance normalizeInstance(Instance instance) throws Exception {
        if (m_DiscretizeFilter != null && instance.attribute(m_nNonDiscreteAttribute).type() != 1) {
            m_DiscretizeFilter.input(instance);
            instance = m_DiscretizeFilter.output();
        }
        if (m_MissingValuesFilter != null) {
            m_MissingValuesFilter.input(instance);
            instance = m_MissingValuesFilter.output();
        } else {
            for (int i = 0; i < m_Instances.numAttributes(); i++) {
                if (i != instance.classIndex() && instance.isMissing(i)) {
                    System.err.println("Warning: Found missing value in test set, filling in values.");
                    m_MissingValuesFilter = new ReplaceMissingValues();
                    m_MissingValuesFilter.setInputFormat(m_Instances);
                    Filter.useFilter(m_Instances, m_MissingValuesFilter);
                    m_MissingValuesFilter.input(instance);
                    instance = m_MissingValuesFilter.output();
                    i = m_Instances.numAttributes();
                }
            }

        }
        return instance;
    }

    public void initStructure() throws Exception {
        int i = 0;
        for (int j = 1; j < m_Instances.numAttributes(); j++) {
            if (i == m_Instances.classIndex()) {
                i++;
            }
        }

        m_ParentSets = new ParentSet[m_Instances.numAttributes()];
        for (int k = 0; k < m_Instances.numAttributes(); k++) {
            m_ParentSets[k] = new ParentSet(m_Instances.numAttributes());
        }

    }

    public void buildStructure() throws Exception {
        m_SearchAlgorithm.buildStructure(this, m_Instances);
    }

    public void estimateCPTs() throws Exception {
        m_BayesNetEstimator.estimateCPTs(this);
    }

    public void initCPTs() throws Exception {
        m_BayesNetEstimator.initCPTs(this);
    }

    public void updateClassifier(Instance instance) throws Exception {
        instance = normalizeInstance(instance);
        m_BayesNetEstimator.updateClassifier(this, instance);
    }

    public double[] distributionForInstance(Instance instance) throws Exception {
        instance = normalizeInstance(instance);
        return m_BayesNetEstimator.distributionForInstance(this, instance);
    }

    public double[] countsForInstance(Instance instance) throws Exception {
        double ad[] = new double[m_NumClasses];
        for (int i = 0; i < m_NumClasses; i++) {
            ad[i] = 0.0D;
        }

        for (int j = 0; j < m_NumClasses; j++) {
            double d = 0.0D;
            for (int k = 0; k < m_Instances.numAttributes(); k++) {
                double d1 = 0.0D;
                for (int l = 0; l < m_ParentSets[k].getNrOfParents(); l++) {
                    int i1 = m_ParentSets[k].getParent(l);
                    if (i1 == m_Instances.classIndex()) {
                        d1 = d1 * (double)m_NumClasses + (double)j;
                    } else {
                        d1 = d1 * (double)m_Instances.attribute(i1).numValues() + instance.value(i1);
                    }
                }

                if (k == m_Instances.classIndex()) {
                    d += ((DiscreteEstimatorBayes)m_Distributions[k][(int)d1]).getCount(j);
                } else {
                    d += ((DiscreteEstimatorBayes)m_Distributions[k][(int)d1]).getCount(instance.value(k));
                }
            }

            ad[j] += d;
        }

        return ad;
    }

    public Enumeration listOptions() {
        Vector vector = new Vector(4);
        vector.addElement(new Option("\tDo not use ADTree data structure\n", "D", 0, "-D"));
        vector.addElement(new Option("\tBIF file to compare with\n", "B", 1, "-B <BIF file>"));
        vector.addElement(new Option("\tSearch algorithm\n", "Q", 1, "-Q weka.classifiers.bayes.net.search.SearchAlgorithm"));
        vector.addElement(new Option("\tEstimator algorithm\n", "E", 1, "-E weka.classifiers.bayes.net.estimate.SimpleEstimator"));
        return vector.elements();
    }

    public void setOptions(String as[]) throws Exception {
        m_bUseADTree = !Utils.getFlag('D', as);
        String s = Utils.getOption('B', as);
        if (s != null && !s.equals("")) {
            setBIFFile(s);
        }
        String s1 = Utils.getOption('Q', as);
        if (s1.length() != 0) {
            setSearchAlgorithm((SearchAlgorithm)Utils.forName(weka/classifiers/bayes/net/search/SearchAlgorithm, s1, partitionOptions(as)));
        } else {
            setSearchAlgorithm(new K2());
        }
        String s2 = Utils.getOption('E', as);
        if (s2.length() != 0) {
            setEstimator((BayesNetEstimator)Utils.forName(weka/classifiers/bayes/net/estimate/BayesNetEstimator, s2, Utils.partitionOptions(as)));
        } else {
            setEstimator(new SimpleEstimator());
        }
        Utils.checkForRemainingOptions(as);
    }

    public static String[] partitionOptions(String as[]) {
        for (int i = 0; i < as.length; i++) {
            if (as[i].equals("--")) {
                for (int j = i; j < as.length && !as[j].equals("-E"); j++) { }
                as[i++] = "";
                String as1[] = new String[as.length - i];
                int k;
                for (k = i; k < as.length && !as[k].equals("-E"); k++) {
                    as1[k - i] = as[k];
                    as[k] = "";
                }

                for (; k < as.length; k++) {
                    as1[k - i] = "";
                }

                return as1;
            }
        }

        return new String[0];
    }

    public String[] getOptions() {
        String as[] = m_SearchAlgorithm.getOptions();
        String as1[] = m_BayesNetEstimator.getOptions();
        String as2[] = new String[11 + as.length + as1.length];
        int i = 0;
        if (!m_bUseADTree) {
            as2[i++] = "-D";
        }
        if (m_otherBayesNet != null) {
            as2[i++] = "-B";
            as2[i++] = m_otherBayesNet.getFileName();
        }
        as2[i++] = "-Q";
        as2[i++] = (new StringBuilder()).append("").append(getSearchAlgorithm().getClass().getName()).toString();
        as2[i++] = "--";
        for (int j = 0; j < as.length; j++) {
            as2[i++] = as[j];
        }

        as2[i++] = "-E";
        as2[i++] = (new StringBuilder()).append("").append(getEstimator().getClass().getName()).toString();
        as2[i++] = "--";
        for (int k = 0; k < as1.length; k++) {
            as2[i++] = as1[k];
        }

        while (i < as2.length)  {
            as2[i++] = "";
        }
        return as2;
    }

    public void setSearchAlgorithm(SearchAlgorithm searchalgorithm) {
        m_SearchAlgorithm = searchalgorithm;
    }

    public SearchAlgorithm getSearchAlgorithm() {
        return m_SearchAlgorithm;
    }

    public void setEstimator(BayesNetEstimator bayesnetestimator) {
        m_BayesNetEstimator = bayesnetestimator;
    }

    public BayesNetEstimator getEstimator() {
        return m_BayesNetEstimator;
    }

    public void setUseADTree(boolean flag) {
        m_bUseADTree = flag;
    }

    public boolean getUseADTree() {
        return m_bUseADTree;
    }

    public void setBIFFile(String s) {
        try {
            m_otherBayesNet = (new BIFReader()).processFile(s);
        }
        catch (Throwable throwable) {
            m_otherBayesNet = null;
        }
    }

    public String getBIFFile() {
        if (m_otherBayesNet != null) {
            return m_otherBayesNet.getFileName();
        } else {
            return "";
        }
    }

    public String toString() {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("Bayes Network Classifier");
        stringbuffer.append((new StringBuilder()).append("\n").append(m_bUseADTree ? "Using " : "not using ").append("ADTree").toString());
        if (m_Instances == null) {
            stringbuffer.append(": No model built yet.");
        } else {
            stringbuffer.append("\n#attributes=");
            stringbuffer.append(m_Instances.numAttributes());
            stringbuffer.append(" #classindex=");
            stringbuffer.append(m_Instances.classIndex());
            stringbuffer.append("\nNetwork structure (nodes followed by parents)\n");
            for (int i = 0; i < m_Instances.numAttributes(); i++) {
                stringbuffer.append((new StringBuilder()).append(m_Instances.attribute(i).name()).append("(").append(m_Instances.attribute(i).numValues()).append("): ").toString());
                for (int j = 0; j < m_ParentSets[i].getNrOfParents(); j++) {
                    stringbuffer.append((new StringBuilder()).append(m_Instances.attribute(m_ParentSets[i].getParent(j)).name()).append(" ").toString());
                }

                stringbuffer.append("\n");
            }

            stringbuffer.append((new StringBuilder()).append("LogScore Bayes: ").append(measureBayesScore()).append("\n").toString());
            stringbuffer.append((new StringBuilder()).append("LogScore BDeu: ").append(measureBDeuScore()).append("\n").toString());
            stringbuffer.append((new StringBuilder()).append("LogScore MDL: ").append(measureMDLScore()).append("\n").toString());
            stringbuffer.append((new StringBuilder()).append("LogScore ENTROPY: ").append(measureEntropyScore()).append("\n").toString());
            stringbuffer.append((new StringBuilder()).append("LogScore AIC: ").append(measureAICScore()).append("\n").toString());
            if (m_otherBayesNet != null) {
                stringbuffer.append((new StringBuilder()).append("Missing: ").append(m_otherBayesNet.missingArcs(this)).append(" Extra: ").append(m_otherBayesNet.extraArcs(this)).append(" Reversed: ").append(m_otherBayesNet.reversedArcs(this)).append("\n").toString());
                stringbuffer.append((new StringBuilder()).append("Divergence: ").append(m_otherBayesNet.divergence(this)).append("\n").toString());
            }
        }
        return stringbuffer.toString();
    }

    public int graphType() {
        return 2;
    }

    public String graph() throws Exception {
        return toXMLBIF03();
    }

    public String getBIFHeader() {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("<?xml version=\"1.0\"?>\n");
        stringbuffer.append("<!-- DTD for the XMLBIF 0.3 format -->\n");
        stringbuffer.append("<!DOCTYPE BIF [\n");
        stringbuffer.append("\t<!ELEMENT BIF ( NETWORK )*>\n");
        stringbuffer.append("\t      <!ATTLIST BIF VERSION CDATA #REQUIRED>\n");
        stringbuffer.append("\t<!ELEMENT NETWORK ( NAME, ( PROPERTY | VARIABLE | DEFINITION )* )>\n");
        stringbuffer.append("\t<!ELEMENT NAME (#PCDATA)>\n");
        stringbuffer.append("\t<!ELEMENT VARIABLE ( NAME, ( OUTCOME |  PROPERTY )* ) >\n");
        stringbuffer.append("\t      <!ATTLIST VARIABLE TYPE (nature|decision|utility) \"nature\">\n");
        stringbuffer.append("\t<!ELEMENT OUTCOME (#PCDATA)>\n");
        stringbuffer.append("\t<!ELEMENT DEFINITION ( FOR | GIVEN | TABLE | PROPERTY )* >\n");
        stringbuffer.append("\t<!ELEMENT FOR (#PCDATA)>\n");
        stringbuffer.append("\t<!ELEMENT GIVEN (#PCDATA)>\n");
        stringbuffer.append("\t<!ELEMENT TABLE (#PCDATA)>\n");
        stringbuffer.append("\t<!ELEMENT PROPERTY (#PCDATA)>\n");
        stringbuffer.append("]>\n");
        return stringbuffer.toString();
    }

    public String toXMLBIF03() {
        if (m_Instances == null) {
            return "<!--No model built yet-->";
        }
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append(getBIFHeader());
        stringbuffer.append("\n");
        stringbuffer.append("\n");
        stringbuffer.append("<BIF VERSION=\"0.3\">\n");
        stringbuffer.append("<NETWORK>\n");
        stringbuffer.append((new StringBuilder()).append("<NAME>").append(XMLNormalize(m_Instances.relationName())).append("</NAME>\n").toString());
        for (int i = 0; i < m_Instances.numAttributes(); i++) {
            stringbuffer.append("<VARIABLE TYPE=\"nature\">\n");
            stringbuffer.append((new StringBuilder()).append("<NAME>").append(XMLNormalize(m_Instances.attribute(i).name())).append("</NAME>\n").toString());
            for (int k = 0; k < m_Instances.attribute(i).numValues(); k++) {
                stringbuffer.append((new StringBuilder()).append("<OUTCOME>").append(XMLNormalize(m_Instances.attribute(i).value(k))).append("</OUTCOME>\n").toString());
            }

            stringbuffer.append("</VARIABLE>\n");
        }

        for (int j = 0; j < m_Instances.numAttributes(); j++) {
            stringbuffer.append("<DEFINITION>\n");
            stringbuffer.append((new StringBuilder()).append("<FOR>").append(XMLNormalize(m_Instances.attribute(j).name())).append("</FOR>\n").toString());
            for (int l = 0; l < m_ParentSets[j].getNrOfParents(); l++) {
                stringbuffer.append((new StringBuilder()).append("<GIVEN>").append(XMLNormalize(m_Instances.attribute(m_ParentSets[j].getParent(l)).name())).append("</GIVEN>\n").toString());
            }

            stringbuffer.append("<TABLE>\n");
            for (int i1 = 0; i1 < m_ParentSets[j].getCardinalityOfParents(); i1++) {
                for (int j1 = 0; j1 < m_Instances.attribute(j).numValues(); j1++) {
                    stringbuffer.append(m_Distributions[j][i1].getProbability(j1));
                    stringbuffer.append(' ');
                }

                stringbuffer.append('\n');
            }

            stringbuffer.append("</TABLE>\n");
            stringbuffer.append("</DEFINITION>\n");
        }

        stringbuffer.append("</NETWORK>\n");
        stringbuffer.append("</BIF>\n");
        return stringbuffer.toString();
    }

    protected String XMLNormalize(String s) {
        StringBuffer stringbuffer = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
            case 38: // '&'
                stringbuffer.append("&amp;");
                break;

            case 39: // '\''
                stringbuffer.append("&apos;");
                break;

            case 34: // '"'
                stringbuffer.append("&quot;");
                break;

            case 60: // '<'
                stringbuffer.append("&lt;");
                break;

            case 62: // '>'
                stringbuffer.append("&gt;");
                break;

            default:
                stringbuffer.append(c);
                break;
            }
        }

        return stringbuffer.toString();
    }

    public String useADTreeTipText() {
        return "When ADTree (the data structure for increasing speed on counts, not to be confused with the classifier under the same name) is used learning time goes down typically. However, because ADTrees are memory intensive, memory problems may occur. Switching this option off makes the structure learning algorithms slower, and run with less memory. By default, ADTrees are used.";
    }

    public String searchAlgorithmTipText() {
        return "Select method used for searching network structures.";
    }

    public String estimatorTipText() {
        return "Select Estimator algorithm for finding the conditional probability tables of the Bayes Network.";
    }

    public String BIFFileTipText() {
        return "Set the name of a file in BIF XML format. A Bayes network learned from data can be compared with the Bayes network represented by the BIF file. Statistics calculated are o.a. the number of missing and extra arcs.";
    }

    public String globalInfo() {
        return "Bayes Network learning using various search algorithms and quality measures.\nBase class for a Bayes Network classifier. Provides datastructures (network structure, conditional probability distributions, etc.) and facilities common to Bayes Network learning algorithms like K2 and B.\n\nFor more information see:\n\nhttp://www.cs.waikato.ac.nz/~remco/weka.pdf";
    }

    public static void main(String args[]) {
        runClassifier(new BayesNet(), args);
    }

    public String getName() {
        return m_Instances.relationName();
    }

    public int getNrOfNodes() {
        return m_Instances.numAttributes();
    }

    public String getNodeName(int i) {
        return m_Instances.attribute(i).name();
    }

    public int getCardinality(int i) {
        return m_Instances.attribute(i).numValues();
    }

    public String getNodeValue(int i, int j) {
        return m_Instances.attribute(i).value(j);
    }

    public int getNrOfParents(int i) {
        return m_ParentSets[i].getNrOfParents();
    }

    public int getParent(int i, int j) {
        return m_ParentSets[i].getParent(j);
    }

    public ParentSet[] getParentSets() {
        return m_ParentSets;
    }

    public Estimator[][] getDistributions() {
        return m_Distributions;
    }

    public int getParentCardinality(int i) {
        return m_ParentSets[i].getCardinalityOfParents();
    }

    public double getProbability(int i, int j, int k) {
        return m_Distributions[i][j].getProbability(k);
    }

    public ParentSet getParentSet(int i) {
        return m_ParentSets[i];
    }

    public ADNode getADTree() {
        return m_ADTree;
    }

    public Enumeration enumerateMeasures() {
        Vector vector = new Vector(4);
        vector.addElement("measureExtraArcs");
        vector.addElement("measureMissingArcs");
        vector.addElement("measureReversedArcs");
        vector.addElement("measureDivergence");
        vector.addElement("measureBayesScore");
        vector.addElement("measureBDeuScore");
        vector.addElement("measureMDLScore");
        vector.addElement("measureAICScore");
        vector.addElement("measureEntropyScore");
        return vector.elements();
    }

    public double measureExtraArcs() {
        if (m_otherBayesNet != null) {
            return (double)m_otherBayesNet.extraArcs(this);
        } else {
            return 0.0D;
        }
    }

    public double measureMissingArcs() {
        if (m_otherBayesNet != null) {
            return (double)m_otherBayesNet.missingArcs(this);
        } else {
            return 0.0D;
        }
    }

    public double measureReversedArcs() {
        if (m_otherBayesNet != null) {
            return (double)m_otherBayesNet.reversedArcs(this);
        } else {
            return 0.0D;
        }
    }

    public double measureDivergence() {
        if (m_otherBayesNet != null) {
            return m_otherBayesNet.divergence(this);
        } else {
            return 0.0D;
        }
    }

    public double measureBayesScore() {
        LocalScoreSearchAlgorithm localscoresearchalgorithm = new LocalScoreSearchAlgorithm(this, m_Instances);
        return localscoresearchalgorithm.logScore(0);
    }

    public double measureBDeuScore() {
        LocalScoreSearchAlgorithm localscoresearchalgorithm = new LocalScoreSearchAlgorithm(this, m_Instances);
        return localscoresearchalgorithm.logScore(1);
    }

    public double measureMDLScore() {
        LocalScoreSearchAlgorithm localscoresearchalgorithm = new LocalScoreSearchAlgorithm(this, m_Instances);
        return localscoresearchalgorithm.logScore(2);
    }

    public double measureAICScore() {
        LocalScoreSearchAlgorithm localscoresearchalgorithm = new LocalScoreSearchAlgorithm(this, m_Instances);
        return localscoresearchalgorithm.logScore(4);
    }

    public double measureEntropyScore() {
        LocalScoreSearchAlgorithm localscoresearchalgorithm = new LocalScoreSearchAlgorithm(this, m_Instances);
        return localscoresearchalgorithm.logScore(3);
    }

    public double getMeasure(String s) {
        if (s.equals("measureExtraArcs")) {
            return measureExtraArcs();
        }
        if (s.equals("measureMissingArcs")) {
            return measureMissingArcs();
        }
        if (s.equals("measureReversedArcs")) {
            return measureReversedArcs();
        }
        if (s.equals("measureDivergence")) {
            return measureDivergence();
        }
        if (s.equals("measureBayesScore")) {
            return measureBayesScore();
        }
        if (s.equals("measureBDeuScore")) {
            return measureBDeuScore();
        }
        if (s.equals("measureMDLScore")) {
            return measureMDLScore();
        }
        if (s.equals("measureAICScore")) {
            return measureAICScore();
        }
        if (s.equals("measureEntropyScore")) {
            return measureEntropyScore();
        } else {
            return 0.0D;
        }
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.33 $");
    }
}
