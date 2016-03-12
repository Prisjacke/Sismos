// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net.search.local;

import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.ADNode;
import weka.classifiers.bayes.net.ParentSet;
import weka.classifiers.bayes.net.search.SearchAlgorithm;
import weka.core.*;

// Referenced classes of package weka.classifiers.bayes.net.search.local:
//            Scoreable

public class LocalScoreSearchAlgorithm extends SearchAlgorithm {

    static final long serialVersionUID = 0x2e284f5545c0d226L;
    BayesNet m_BayesNet;
    double m_fAlpha;
    public static final Tag TAGS_SCORE_TYPE[] = {
        new Tag(0, "BAYES"), new Tag(1, "BDeu"), new Tag(2, "MDL"), new Tag(3, "ENTROPY"), new Tag(4, "AIC")
    };
    int m_nScoreType;

    public LocalScoreSearchAlgorithm() {
        m_fAlpha = 0.5D;
        m_nScoreType = 0;
    }

    public LocalScoreSearchAlgorithm(BayesNet bayesnet, Instances instances) {
        m_fAlpha = 0.5D;
        m_nScoreType = 0;
        m_BayesNet = bayesnet;
    }

    public double logScore(int i) {
        if (m_BayesNet.m_Distributions == null) {
            return 0.0D;
        }
        if (i < 0) {
            i = m_nScoreType;
        }
        double d = 0.0D;
        Instances instances = m_BayesNet.m_Instances;
        for (int j = 0; j < instances.numAttributes(); j++) {
            int k = m_BayesNet.getParentSet(j).getCardinalityOfParents();
            for (int l = 0; l < k; l++) {
                d += ((Scoreable)m_BayesNet.m_Distributions[j][l]).logScore(i, k);
            }

            switch (i) {
            case 2: // '\002'
                d -= 0.5D * (double)m_BayesNet.getParentSet(j).getCardinalityOfParents() * (double)(instances.attribute(j).numValues() - 1) * Math.log(instances.numInstances());
                break;

            case 4: // '\004'
                d -= m_BayesNet.getParentSet(j).getCardinalityOfParents() * (instances.attribute(j).numValues() - 1);
                break;
            }
        }

        return d;
    }

    public void buildStructure(BayesNet bayesnet, Instances instances) throws Exception {
        m_BayesNet = bayesnet;
        super.buildStructure(bayesnet, instances);
    }

    public double calcNodeScore(int i) {
        if (m_BayesNet.getUseADTree() && m_BayesNet.getADTree() != null) {
            return calcNodeScoreADTree(i);
        } else {
            return calcNodeScorePlain(i);
        }
    }

    private double calcNodeScoreADTree(int i) {
        Instances instances = m_BayesNet.m_Instances;
        ParentSet parentset = m_BayesNet.getParentSet(i);
        int j = parentset.getNrOfParents();
        int ai[] = new int[j + 1];
        for (int k = 0; k < j; k++) {
            ai[k] = parentset.getParent(k);
        }

        ai[j] = i;
        int ai1[] = new int[j + 1];
        int l = 1;
        ai1[j] = 1;
        l *= instances.attribute(i).numValues();
        for (int i1 = j - 1; i1 >= 0; i1--) {
            ai1[i1] = l;
            l *= instances.attribute(ai[i1]).numValues();
        }

        for (int j1 = 1; j1 < ai.length; j1++) {
            for (int l1 = j1; l1 > 0 && ai[l1] < ai[l1 - 1]; l1--) {
                int j2 = ai[l1];
                ai[l1] = ai[l1 - 1];
                ai[l1 - 1] = j2;
                j2 = ai1[l1];
                ai1[l1] = ai1[l1 - 1];
                ai1[l1 - 1] = j2;
            }

        }

        int k1 = parentset.getCardinalityOfParents();
        int i2 = instances.attribute(i).numValues();
        int ai2[] = new int[k1 * i2];
        m_BayesNet.getADTree().getCounts(ai2, ai, ai1, 0, 0, false);
        return calcScoreOfCounts(ai2, k1, i2, instances);
    }

    private double calcNodeScorePlain(int i) {
        Instances instances = m_BayesNet.m_Instances;
        ParentSet parentset = m_BayesNet.getParentSet(i);
        int j = parentset.getCardinalityOfParents();
        int k = instances.attribute(i).numValues();
        int ai[] = new int[j * k];
        for (int l = 0; l < j * k; l++) {
            ai[l] = 0;
        }

        for (Enumeration enumeration = instances.enumerateInstances(); enumeration.hasMoreElements();) {
            Instance instance = (Instance)enumeration.nextElement();
            double d = 0.0D;
            for (int i1 = 0; i1 < parentset.getNrOfParents(); i1++) {
                int j1 = parentset.getParent(i1);
                d = d * (double)instances.attribute(j1).numValues() + instance.value(j1);
            }

            ai[k * (int)d + (int)instance.value(i)]++;
        }

        return calcScoreOfCounts(ai, j, k, instances);
    }

    protected double calcScoreOfCounts(int ai[], int i, int j, Instances instances) {
        double d = 0.0D;
        for (int k = 0; k < i; k++) {
            switch (m_nScoreType) {
            default:
                break;

            case 0: // '\0'
                double d1 = 0.0D;
                for (int l = 0; l < j; l++) {
                    if (m_fAlpha + (double)ai[k * j + l] != 0.0D) {
                        d += Statistics.lnGamma(m_fAlpha + (double)ai[k * j + l]);
                        d1 += m_fAlpha + (double)ai[k * j + l];
                    }
                }

                if (d1 != 0.0D) {
                    d -= Statistics.lnGamma(d1);
                }
                if (m_fAlpha != 0.0D) {
                    d -= (double)j * Statistics.lnGamma(m_fAlpha);
                    d += Statistics.lnGamma((double)j * m_fAlpha);
                }
                break;

            case 1: // '\001'
                double d2 = 0.0D;
                for (int i1 = 0; i1 < j; i1++) {
                    if (m_fAlpha + (double)ai[k * j + i1] != 0.0D) {
                        d += Statistics.lnGamma(1.0D / (double)(j * i) + (double)ai[k * j + i1]);
                        d2 += 1.0D / (double)(j * i) + (double)ai[k * j + i1];
                    }
                }

                d -= Statistics.lnGamma(d2);
                d -= (double)j * Statistics.lnGamma(1.0D / (double)(j * i));
                d += Statistics.lnGamma(1.0D / (double)i);
                break;

            case 2: // '\002'
            case 3: // '\003'
            case 4: // '\004'
                double d3 = 0.0D;
                for (int j1 = 0; j1 < j; j1++) {
                    d3 += ai[k * j + j1];
                }

                for (int k1 = 0; k1 < j; k1++) {
                    if (ai[k * j + k1] > 0) {
                        d += (double)ai[k * j + k1] * Math.log((double)ai[k * j + k1] / d3);
                    }
                }

                break;
            }
        }

        switch (m_nScoreType) {
        case 2: // '\002'
            d -= 0.5D * (double)i * (double)(j - 1) * Math.log(instances.numInstances());
            break;

        case 4: // '\004'
            d -= i * (j - 1);
            break;
        }
        return d;
    }

    protected double calcScoreOfCounts2(int ai[][], int i, int j, Instances instances) {
        double d = 0.0D;
        for (int k = 0; k < i; k++) {
            switch (m_nScoreType) {
            default:
                break;

            case 0: // '\0'
                double d1 = 0.0D;
                for (int l = 0; l < j; l++) {
                    if (m_fAlpha + (double)ai[k][l] != 0.0D) {
                        d += Statistics.lnGamma(m_fAlpha + (double)ai[k][l]);
                        d1 += m_fAlpha + (double)ai[k][l];
                    }
                }

                if (d1 != 0.0D) {
                    d -= Statistics.lnGamma(d1);
                }
                if (m_fAlpha != 0.0D) {
                    d -= (double)j * Statistics.lnGamma(m_fAlpha);
                    d += Statistics.lnGamma((double)j * m_fAlpha);
                }
                break;

            case 1: // '\001'
                double d2 = 0.0D;
                for (int i1 = 0; i1 < j; i1++) {
                    if (m_fAlpha + (double)ai[k * j][i1] != 0.0D) {
                        d += Statistics.lnGamma(1.0D / (double)(j * i) + (double)ai[k * j][i1]);
                        d2 += 1.0D / (double)(j * i) + (double)ai[k * j][i1];
                    }
                }

                d -= Statistics.lnGamma(d2);
                d -= (double)j * Statistics.lnGamma(1.0D / (double)(i * j));
                d += Statistics.lnGamma(1.0D / (double)i);
                break;

            case 2: // '\002'
            case 3: // '\003'
            case 4: // '\004'
                double d3 = 0.0D;
                for (int j1 = 0; j1 < j; j1++) {
                    d3 += ai[k][j1];
                }

                for (int k1 = 0; k1 < j; k1++) {
                    if (ai[k][k1] > 0) {
                        d += (double)ai[k][k1] * Math.log((double)ai[k][k1] / d3);
                    }
                }

                break;
            }
        }

        switch (m_nScoreType) {
        case 2: // '\002'
            d -= 0.5D * (double)i * (double)(j - 1) * Math.log(instances.numInstances());
            break;

        case 4: // '\004'
            d -= i * (j - 1);
            break;
        }
        return d;
    }

    public double calcScoreWithExtraParent(int i, int j) {
        ParentSet parentset = m_BayesNet.getParentSet(i);
        if (parentset.contains(j)) {
            return -1E+100D;
        } else {
            parentset.addParent(j, m_BayesNet.m_Instances);
            double d = calcNodeScore(i);
            parentset.deleteLastParent(m_BayesNet.m_Instances);
            return d;
        }
    }

    public double calcScoreWithMissingParent(int i, int j) {
        ParentSet parentset = m_BayesNet.getParentSet(i);
        if (!parentset.contains(j)) {
            return -1E+100D;
        } else {
            int k = parentset.deleteParent(j, m_BayesNet.m_Instances);
            double d = calcNodeScore(i);
            parentset.addParent(j, k, m_BayesNet.m_Instances);
            return d;
        }
    }

    public void setScoreType(SelectedTag selectedtag) {
        if (selectedtag.getTags() == TAGS_SCORE_TYPE) {
            m_nScoreType = selectedtag.getSelectedTag().getID();
        }
    }

    public SelectedTag getScoreType() {
        return new SelectedTag(m_nScoreType, TAGS_SCORE_TYPE);
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
        vector.addElement(new Option("\tScore type (BAYES, BDeu, MDL, ENTROPY and AIC)", "S", 1, "-S [BAYES|MDL|ENTROPY|AIC|CROSS_CLASSIC|CROSS_BAYES]"));
        return vector.elements();
    }

    public void setOptions(String as[]) throws Exception {
        setMarkovBlanketClassifier(Utils.getFlag("mbc", as));
        String s = Utils.getOption('S', as);
        if (s.compareTo("BAYES") == 0) {
            setScoreType(new SelectedTag(0, TAGS_SCORE_TYPE));
        }
        if (s.compareTo("BDeu") == 0) {
            setScoreType(new SelectedTag(1, TAGS_SCORE_TYPE));
        }
        if (s.compareTo("MDL") == 0) {
            setScoreType(new SelectedTag(2, TAGS_SCORE_TYPE));
        }
        if (s.compareTo("ENTROPY") == 0) {
            setScoreType(new SelectedTag(3, TAGS_SCORE_TYPE));
        }
        if (s.compareTo("AIC") == 0) {
            setScoreType(new SelectedTag(4, TAGS_SCORE_TYPE));
        }
    }

    public String[] getOptions() {
        String as[] = super.getOptions();
        String as1[] = new String[3 + as.length];
        int i = 0;
        if (getMarkovBlanketClassifier()) {
            as1[i++] = "-mbc";
        }
        as1[i++] = "-S";
        switch (m_nScoreType) {
        case 0: // '\0'
            as1[i++] = "BAYES";
            break;

        case 1: // '\001'
            as1[i++] = "BDeu";
            break;

        case 2: // '\002'
            as1[i++] = "MDL";
            break;

        case 3: // '\003'
            as1[i++] = "ENTROPY";
            break;

        case 4: // '\004'
            as1[i++] = "AIC";
            break;
        }
        for (int j = 0; j < as.length; j++) {
            as1[i++] = as[j];
        }

        while (i < as1.length)  {
            as1[i++] = "";
        }
        return as1;
    }

    public String scoreTypeTipText() {
        return "The score type determines the measure used to judge the quality of a network structure. It can be one of Bayes, BDeu, Minimum Description Length (MDL), Akaike Information Criterion (AIC), and Entropy.";
    }

    public String markovBlanketClassifierTipText() {
        return super.markovBlanketClassifierTipText();
    }

    public String globalInfo() {
        return "The ScoreBasedSearchAlgorithm class supports Bayes net structure search algorithms that are based on maximizing scores (as opposed to for example conditional independence based search algorithms).";
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.8 $");
    }

}
