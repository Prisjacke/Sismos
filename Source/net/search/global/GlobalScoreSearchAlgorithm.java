// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net.search.global;

import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.ParentSet;
import weka.classifiers.bayes.net.search.SearchAlgorithm;
import weka.core.*;

public class GlobalScoreSearchAlgorithm extends SearchAlgorithm {

    static final long serialVersionUID = 0x65e1db30423370e5L;
    BayesNet m_BayesNet;
    boolean m_bUseProb;
    int m_nNrOfFolds;
    static final int LOOCV = 0;
    static final int KFOLDCV = 1;
    static final int CUMCV = 2;
    public static final Tag TAGS_CV_TYPE[] = {
        new Tag(0, "LOO-CV"), new Tag(1, "k-Fold-CV"), new Tag(2, "Cumulative-CV")
    };
    int m_nCVType;

    public GlobalScoreSearchAlgorithm() {
        m_bUseProb = true;
        m_nNrOfFolds = 10;
        m_nCVType = 0;
    }

    public double calcScore(BayesNet bayesnet) throws Exception {
        switch (m_nCVType) {
        case 0: // '\0'
            return leaveOneOutCV(bayesnet);

        case 2: // '\002'
            return cumulativeCV(bayesnet);

        case 1: // '\001'
            return kFoldCV(bayesnet, m_nNrOfFolds);
        }
        throw new Exception((new StringBuilder()).append("Unrecognized cross validation type encountered: ").append(m_nCVType).toString());
    }

    public double calcScoreWithExtraParent(int i, int j) throws Exception {
        ParentSet parentset = m_BayesNet.getParentSet(i);
        Instances instances = m_BayesNet.m_Instances;
        for (int k = 0; k < parentset.getNrOfParents(); k++) {
            if (parentset.getParent(k) == j) {
                return -1E+100D;
            }
        }

        parentset.addParent(j, instances);
        double d = calcScore(m_BayesNet);
        parentset.deleteLastParent(instances);
        return d;
    }

    public double calcScoreWithMissingParent(int i, int j) throws Exception {
        ParentSet parentset = m_BayesNet.getParentSet(i);
        Instances instances = m_BayesNet.m_Instances;
        if (!parentset.contains(j)) {
            return -1E+100D;
        } else {
            int k = parentset.deleteParent(j, instances);
            double d = calcScore(m_BayesNet);
            parentset.addParent(j, k, instances);
            return d;
        }
    }

    public double calcScoreWithReversedParent(int i, int j) throws Exception {
        ParentSet parentset = m_BayesNet.getParentSet(i);
        ParentSet parentset1 = m_BayesNet.getParentSet(j);
        Instances instances = m_BayesNet.m_Instances;
        if (!parentset.contains(j)) {
            return -1E+100D;
        } else {
            int k = parentset.deleteParent(j, instances);
            parentset1.addParent(i, instances);
            double d = calcScore(m_BayesNet);
            parentset1.deleteLastParent(instances);
            parentset.addParent(j, k, instances);
            return d;
        }
    }

    public double leaveOneOutCV(BayesNet bayesnet) throws Exception {
        m_BayesNet = bayesnet;
        double d = 0.0D;
        double d1 = 0.0D;
        Instances instances = bayesnet.m_Instances;
        bayesnet.estimateCPTs();
        for (int i = 0; i < instances.numInstances(); i++) {
            Instance instance = instances.instance(i);
            instance.setWeight(-instance.weight());
            bayesnet.updateClassifier(instance);
            d += accuracyIncrease(instance);
            d1 += instance.weight();
            instance.setWeight(-instance.weight());
            bayesnet.updateClassifier(instance);
        }

        return d / d1;
    }

    public double cumulativeCV(BayesNet bayesnet) throws Exception {
        m_BayesNet = bayesnet;
        double d = 0.0D;
        double d1 = 0.0D;
        Instances instances = bayesnet.m_Instances;
        bayesnet.initCPTs();
        for (int i = 0; i < instances.numInstances(); i++) {
            Instance instance = instances.instance(i);
            d += accuracyIncrease(instance);
            bayesnet.updateClassifier(instance);
            d1 += instance.weight();
        }

        return d / d1;
    }

    public double kFoldCV(BayesNet bayesnet, int i) throws Exception {
        m_BayesNet = bayesnet;
        double d = 0.0D;
        double d1 = 0.0D;
        Instances instances = bayesnet.m_Instances;
        bayesnet.estimateCPTs();
        int j = 0;
        int k = instances.numInstances() / i;
        int l = 1;
        while (j < instances.numInstances())  {
            for (int i1 = j; i1 < k; i1++) {
                Instance instance = instances.instance(i1);
                instance.setWeight(-instance.weight());
                bayesnet.updateClassifier(instance);
            }

            for (int j1 = j; j1 < k; j1++) {
                Instance instance1 = instances.instance(j1);
                instance1.setWeight(-instance1.weight());
                d += accuracyIncrease(instance1);
                instance1.setWeight(-instance1.weight());
                d1 += instance1.weight();
            }

            for (int k1 = j; k1 < k; k1++) {
                Instance instance2 = instances.instance(k1);
                instance2.setWeight(-instance2.weight());
                bayesnet.updateClassifier(instance2);
            }

            j = k;
            k = (++l * instances.numInstances()) / i;
        }
        return d / d1;
    }

    double accuracyIncrease(Instance instance) throws Exception {
        if (m_bUseProb) {
            double ad[] = m_BayesNet.distributionForInstance(instance);
            return ad[(int)instance.classValue()] * instance.weight();
        }
        if (m_BayesNet.classifyInstance(instance) == instance.classValue()) {
            return instance.weight();
        } else {
            return 0.0D;
        }
    }

    public boolean getUseProb() {
        return m_bUseProb;
    }

    public void setUseProb(boolean flag) {
        m_bUseProb = flag;
    }

    public void setCVType(SelectedTag selectedtag) {
        if (selectedtag.getTags() == TAGS_CV_TYPE) {
            m_nCVType = selectedtag.getSelectedTag().getID();
        }
    }

    public SelectedTag getCVType() {
        return new SelectedTag(m_nCVType, TAGS_CV_TYPE);
    }

    public void setMarkovBlanketClassifier(boolean flag) {
        super.setMarkovBlanketClassifier(flag);
    }

    public boolean getMarkovBlanketClassifier() {
        return super.getMarkovBlanketClassifier();
    }

    public Enumeration listOptions() {
        Vector vector = new Vector();
        vector.addElement(new Option("\tApplies a Markov Blanket correction to the network structure, \n\tafter a network structure is learned. This ensures that all \n\tnodes in the network are part of the Markov blanket of the \n\tclassifier node.", "mbc", 0, "-mbc"));
        vector.addElement(new Option("\tScore type (LOO-CV,k-Fold-CV,Cumulative-CV)", "S", 1, "-S [LOO-CV|k-Fold-CV|Cumulative-CV]"));
        vector.addElement(new Option("\tUse probabilistic or 0/1 scoring.\n\t(default probabilistic scoring)", "Q", 0, "-Q"));
        for (Enumeration enumeration = super.listOptions(); enumeration.hasMoreElements(); vector.addElement(enumeration.nextElement())) { }
        return vector.elements();
    }

    public void setOptions(String as[]) throws Exception {
        setMarkovBlanketClassifier(Utils.getFlag("mbc", as));
        String s = Utils.getOption('S', as);
        if (s.compareTo("LOO-CV") == 0) {
            setCVType(new SelectedTag(0, TAGS_CV_TYPE));
        }
        if (s.compareTo("k-Fold-CV") == 0) {
            setCVType(new SelectedTag(1, TAGS_CV_TYPE));
        }
        if (s.compareTo("Cumulative-CV") == 0) {
            setCVType(new SelectedTag(2, TAGS_CV_TYPE));
        }
        setUseProb(!Utils.getFlag('Q', as));
        super.setOptions(as);
    }

    public String[] getOptions() {
        String as[] = super.getOptions();
        String as1[] = new String[4 + as.length];
        int i = 0;
        if (getMarkovBlanketClassifier()) {
            as1[i++] = "-mbc";
        }
        as1[i++] = "-S";
        switch (m_nCVType) {
        case 0: // '\0'
            as1[i++] = "LOO-CV";
            break;

        case 1: // '\001'
            as1[i++] = "k-Fold-CV";
            break;

        case 2: // '\002'
            as1[i++] = "Cumulative-CV";
            break;
        }
        if (!getUseProb()) {
            as1[i++] = "-Q";
        }
        for (int j = 0; j < as.length; j++) {
            as1[i++] = as[j];
        }

        while (i < as1.length)  {
            as1[i++] = "";
        }
        return as1;
    }

    public String CVTypeTipText() {
        return "Select cross validation strategy to be used in searching for networks.LOO-CV = Leave one out cross validation\nk-Fold-CV = k fold cross validation\nCumulative-CV = cumulative cross validation.";
    }

    public String useProbTipText() {
        return "If set to true, the probability of the class if returned in the estimate of the accuracy. If set to false, the accuracy estimate is only increased if the classifier returns exactly the correct class.";
    }

    public String globalInfo() {
        return "This Bayes Network learning algorithm uses cross validation to estimate classification accuracy.";
    }

    public String markovBlanketClassifierTipText() {
        return super.markovBlanketClassifierTipText();
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.10 $");
    }

}
