// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.classifiers.bayes.net.estimate.BayesNetEstimator;
import weka.classifiers.bayes.net.estimate.DiscreteEstimatorBayes;
import weka.core.*;
import weka.estimators.Estimator;

// Referenced classes of package weka.classifiers.bayes.net:
//            EditableBayesNet, BIFReader, ParentSet

public class BayesNetGenerator extends EditableBayesNet {

    int m_nSeed;
    Random random;
    static final long serialVersionUID = 0x986f9f0f098b86e8L;
    boolean m_bGenerateNet;
    int m_nNrOfNodes;
    int m_nNrOfArcs;
    int m_nNrOfInstances;
    int m_nCardinality;
    String m_sBIFFile;

    public BayesNetGenerator() {
        m_nSeed = 1;
        m_bGenerateNet = false;
        m_nNrOfNodes = 10;
        m_nNrOfArcs = 10;
        m_nNrOfInstances = 10;
        m_nCardinality = 2;
        m_sBIFFile = "";
    }

    public void generateRandomNetwork() throws Exception {
        if (m_otherBayesNet == null) {
            Init(m_nNrOfNodes, m_nCardinality);
            generateRandomNetworkStructure(m_nNrOfNodes, m_nNrOfArcs);
            generateRandomDistributions(m_nNrOfNodes, m_nCardinality);
        } else {
            m_nNrOfNodes = m_otherBayesNet.getNrOfNodes();
            m_ParentSets = m_otherBayesNet.getParentSets();
            m_Distributions = m_otherBayesNet.getDistributions();
            random = new Random(m_nSeed);
            FastVector fastvector = new FastVector(m_nNrOfNodes);
            for (int i = 0; i < m_nNrOfNodes; i++) {
                int j = m_otherBayesNet.getCardinality(i);
                FastVector fastvector1 = new FastVector(j + 1);
                for (int k = 0; k < j; k++) {
                    fastvector1.addElement(m_otherBayesNet.getNodeValue(i, k));
                }

                Attribute attribute = new Attribute(m_otherBayesNet.getNodeName(i), fastvector1);
                fastvector.addElement(attribute);
            }

            m_Instances = new Instances(m_otherBayesNet.getName(), fastvector, 100);
            m_Instances.setClassIndex(m_nNrOfNodes - 1);
        }
    }

    public void Init(int i, int j) throws Exception {
        random = new Random(m_nSeed);
        FastVector fastvector = new FastVector(i);
        FastVector fastvector1 = new FastVector(j + 1);
        for (int k = 0; k < j; k++) {
            fastvector1.addElement((new StringBuilder()).append("Value").append(k + 1).toString());
        }

        for (int l = 0; l < i; l++) {
            Attribute attribute = new Attribute((new StringBuilder()).append("Node").append(l + 1).toString(), fastvector1);
            fastvector.addElement(attribute);
        }

        m_Instances = new Instances("RandomNet", fastvector, 100);
        m_Instances.setClassIndex(i - 1);
        setUseADTree(false);
        initStructure();
        m_Distributions = new Estimator[i][1];
        for (int i1 = 0; i1 < i; i1++) {
            m_Distributions[i1][0] = new DiscreteEstimatorBayes(j, getEstimator().getAlpha());
        }

        m_nEvidence = new FastVector(i);
        for (int j1 = 0; j1 < i; j1++) {
            m_nEvidence.addElement(Integer.valueOf(-1));
        }

        m_fMarginP = new FastVector(i);
        for (int k1 = 0; k1 < i; k1++) {
            double ad[] = new double[getCardinality(k1)];
            m_fMarginP.addElement(ad);
        }

        m_nPositionX = new FastVector(i);
        m_nPositionY = new FastVector(i);
        for (int l1 = 0; l1 < i; l1++) {
            m_nPositionX.addElement(Integer.valueOf((l1 % 10) * 50));
            m_nPositionY.addElement(Integer.valueOf((l1 / 10) * 50));
        }

    }

    public void generateRandomNetworkStructure(int i, int j) throws Exception {
        if (j < i - 1) {
            throw new Exception((new StringBuilder()).append("Number of arcs should be at least (nNodes - 1) = ").append(i - 1).append(" instead of ").append(j).toString());
        }
        if (j > (i * (i - 1)) / 2) {
            throw new Exception((new StringBuilder()).append("Number of arcs should be at most nNodes * (nNodes - 1) / 2 = ").append((i * (i - 1)) / 2).append(" instead of ").append(j).toString());
        }
        if (j == 0) {
            return;
        }
        generateTree(i);
label0:
        for (int k = i - 1; k < j; k++) {
            boolean flag = false;
            do {
                if (flag) {
                    continue label0;
                }
                int l = random.nextInt(i);
                int i1 = random.nextInt(i);
                if (l == i1) {
                    i1 = (l + 1) % i;
                }
                if (i1 < l) {
                    int j1 = l;
                    l = i1;
                    i1 = j1;
                }
                if (!m_ParentSets[i1].contains(l)) {
                    m_ParentSets[i1].addParent(l, m_Instances);
                    flag = true;
                }
            } while (true);
        }

    }

    void generateTree(int i) {
        boolean aflag[] = new boolean[i];
        int j = random.nextInt(i);
        int l = random.nextInt(i);
        if (j == l) {
            l = (j + 1) % i;
        }
        if (l < j) {
            int j1 = j;
            j = l;
            l = j1;
        }
        m_ParentSets[l].addParent(j, m_Instances);
        aflag[j] = true;
        aflag[l] = true;
        for (int k1 = 2; k1 < i; k1++) {
            int l1 = random.nextInt(i);
            int k = 0;
            for (; l1 >= 0; l1--) {
                for (k = (k + 1) % i; !aflag[k]; k = (k + 1) % i) { }
            }

            l1 = random.nextInt(i);
            int i1 = 0;
            for (; l1 >= 0; l1--) {
                for (i1 = (i1 + 1) % i; aflag[i1]; i1 = (i1 + 1) % i) { }
            }

            if (i1 < k) {
                int i2 = k;
                k = i1;
                i1 = i2;
            }
            m_ParentSets[i1].addParent(k, m_Instances);
            aflag[k] = true;
            aflag[i1] = true;
        }

    }

    void generateRandomDistributions(int i, int j) {
        int k = 1;
        for (int l = 0; l < i; l++) {
            if (m_ParentSets[l].getCardinalityOfParents() > k) {
                k = m_ParentSets[l].getCardinalityOfParents();
            }
        }

        m_Distributions = new Estimator[m_Instances.numAttributes()][k];
        for (int i1 = 0; i1 < i; i1++) {
            int ai[] = new int[j + 1];
            ai[0] = 0;
            ai[j] = 1000;
            for (int j1 = 0; j1 < m_ParentSets[i1].getCardinalityOfParents(); j1++) {
                for (int k1 = 1; k1 < j; k1++) {
                    ai[k1] = random.nextInt(1000);
                }

                for (int l1 = 1; l1 < j; l1++) {
                    for (int i2 = l1 + 1; i2 < j; i2++) {
                        if (ai[i2] < ai[l1]) {
                            int k2 = ai[i2];
                            ai[i2] = ai[l1];
                            ai[l1] = k2;
                        }
                    }

                }

                DiscreteEstimatorBayes discreteestimatorbayes = new DiscreteEstimatorBayes(j, getEstimator().getAlpha());
                for (int j2 = 0; j2 < j; j2++) {
                    discreteestimatorbayes.addValue(j2, ai[j2 + 1] - ai[j2]);
                }

                m_Distributions[i1][j1] = discreteestimatorbayes;
            }

        }

    }

    public void generateInstances() throws Exception {
        int ai[] = getOrder();
        for (int i = 0; i < m_nNrOfInstances; i++) {
            int j = m_Instances.numAttributes();
            Instance instance = new Instance(j);
            instance.setDataset(m_Instances);
            for (int k = 0; k < j; k++) {
                int l = ai[k];
                double d = 0.0D;
                for (int i1 = 0; i1 < m_ParentSets[l].getNrOfParents(); i1++) {
                    int j1 = m_ParentSets[l].getParent(i1);
                    d = d * (double)m_Instances.attribute(j1).numValues() + instance.value(j1);
                }

                double d1 = (float)random.nextInt(1000) / 1000F;
                int k1;
                for (k1 = 0; d1 > m_Distributions[l][(int)d].getProbability(k1); k1++) {
                    d1 -= m_Distributions[l][(int)d].getProbability(k1);
                }

                instance.setValue(l, k1);
            }

            m_Instances.add(instance);
        }

    }

    int[] getOrder() throws Exception {
        int i = m_Instances.numAttributes();
        int ai[] = new int[i];
        boolean aflag[] = new boolean[i];
        for (int j = 0; j < i; j++) {
            int k = 0;
            boolean flag;
            for (flag = false; !flag && k < i;) {
                if (!aflag[k]) {
                    flag = true;
                    int l;
                    for (l = 0; flag && l < m_ParentSets[k].getNrOfParents(); flag = aflag[m_ParentSets[j].getParent(l++)]) { }
                    if (flag && l == m_ParentSets[k].getNrOfParents()) {
                        ai[j] = k;
                        aflag[k] = true;
                    } else {
                        k++;
                    }
                } else {
                    k++;
                }
            }

            if (!flag && k == i) {
                throw new Exception("There appears to be a cycle in the graph");
            }
        }

        return ai;
    }

    public String toString() {
        if (m_bGenerateNet) {
            return toXMLBIF03();
        } else {
            return m_Instances.toString();
        }
    }

    void setNrOfNodes(int i) {
        m_nNrOfNodes = i;
    }

    void setNrOfArcs(int i) {
        m_nNrOfArcs = i;
    }

    void setNrOfInstances(int i) {
        m_nNrOfInstances = i;
    }

    void setCardinality(int i) {
        m_nCardinality = i;
    }

    void setSeed(int i) {
        m_nSeed = i;
    }

    public Enumeration listOptions() {
        Vector vector = new Vector(6);
        vector.addElement(new Option("\tGenerate network (instead of instances)\n", "B", 0, "-B"));
        vector.addElement(new Option("\tNr of nodes\n", "N", 1, "-N <integer>"));
        vector.addElement(new Option("\tNr of arcs\n", "A", 1, "-A <integer>"));
        vector.addElement(new Option("\tNr of instances\n", "M", 1, "-M <integer>"));
        vector.addElement(new Option("\tCardinality of the variables\n", "C", 1, "-C <integer>"));
        vector.addElement(new Option("\tSeed for random number generator\n", "S", 1, "-S <integer>"));
        vector.addElement(new Option("\tThe BIF file to obtain the structure from.\n", "F", 1, "-F <file>"));
        return vector.elements();
    }

    public void setOptions(String as[]) throws Exception {
        m_bGenerateNet = Utils.getFlag('B', as);
        String s = Utils.getOption('N', as);
        if (s.length() != 0) {
            setNrOfNodes(Integer.parseInt(s));
        } else {
            setNrOfNodes(10);
        }
        String s1 = Utils.getOption('A', as);
        if (s1.length() != 0) {
            setNrOfArcs(Integer.parseInt(s1));
        } else {
            setNrOfArcs(10);
        }
        String s2 = Utils.getOption('M', as);
        if (s2.length() != 0) {
            setNrOfInstances(Integer.parseInt(s2));
        } else {
            setNrOfInstances(10);
        }
        String s3 = Utils.getOption('C', as);
        if (s3.length() != 0) {
            setCardinality(Integer.parseInt(s3));
        } else {
            setCardinality(2);
        }
        String s4 = Utils.getOption('S', as);
        if (s4.length() != 0) {
            setSeed(Integer.parseInt(s4));
        } else {
            setSeed(1);
        }
        String s5 = Utils.getOption('F', as);
        if (s5 != null && s5 != "") {
            setBIFFile(s5);
        }
    }

    public String[] getOptions() {
        String as[] = new String[13];
        int i = 0;
        if (m_bGenerateNet) {
            as[i++] = "-B";
        }
        as[i++] = "-N";
        as[i++] = (new StringBuilder()).append("").append(m_nNrOfNodes).toString();
        as[i++] = "-A";
        as[i++] = (new StringBuilder()).append("").append(m_nNrOfArcs).toString();
        as[i++] = "-M";
        as[i++] = (new StringBuilder()).append("").append(m_nNrOfInstances).toString();
        as[i++] = "-C";
        as[i++] = (new StringBuilder()).append("").append(m_nCardinality).toString();
        as[i++] = "-S";
        as[i++] = (new StringBuilder()).append("").append(m_nSeed).toString();
        if (m_sBIFFile.length() != 0) {
            as[i++] = "-F";
            as[i++] = (new StringBuilder()).append("").append(m_sBIFFile).toString();
        }
        while (i < as.length)  {
            as[i++] = "";
        }
        return as;
    }

    protected static void printOptions(OptionHandler optionhandler) {
        Enumeration enumeration = optionhandler.listOptions();
        System.out.println((new StringBuilder()).append("Options for ").append(optionhandler.getClass().getName()).append(":\n").toString());
        Option option;
        for (; enumeration.hasMoreElements(); System.out.println(option.description())) {
            option = (Option)enumeration.nextElement();
            System.out.println(option.synopsis());
        }

    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.14 $");
    }

    public static void main(String args[]) {
        BayesNetGenerator bayesnetgenerator = new BayesNetGenerator();
        if (args.length == 0 || Utils.getFlag('h', args)) {
            printOptions(bayesnetgenerator);
            return;
        }
        try {
            bayesnetgenerator.setOptions(args);
            bayesnetgenerator.generateRandomNetwork();
            if (!bayesnetgenerator.m_bGenerateNet) {
                bayesnetgenerator.generateInstances();
            }
            System.out.println(bayesnetgenerator.toString());
        }
        catch (Exception exception) {
            exception.printStackTrace();
            printOptions(bayesnetgenerator);
        }
        return;
    }
}
