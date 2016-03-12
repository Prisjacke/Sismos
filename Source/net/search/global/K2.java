// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net.search.global;

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.ParentSet;
import weka.core.*;

// Referenced classes of package weka.classifiers.bayes.net.search.global:
//            GlobalScoreSearchAlgorithm

public class K2 extends GlobalScoreSearchAlgorithm
    implements TechnicalInformationHandler {

    static final long serialVersionUID = 0xa4089fdff35af030L;
    boolean m_bRandomOrder;

    public K2() {
        m_bRandomOrder = false;
    }

    public TechnicalInformation getTechnicalInformation() {
        TechnicalInformation technicalinformation = new TechnicalInformation(weka.core.TechnicalInformation.Type.PROCEEDINGS);
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.AUTHOR, "G.F. Cooper and E. Herskovits");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.YEAR, "1990");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.TITLE, "A Bayesian method for constructing Bayesian belief networks from databases");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.BOOKTITLE, "Proceedings of the Conference on Uncertainty in AI");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.PAGES, "86-94");
        TechnicalInformation technicalinformation1 = technicalinformation.add(weka.core.TechnicalInformation.Type.ARTICLE);
        technicalinformation1.setValue(weka.core.TechnicalInformation.Field.AUTHOR, "G. Cooper and E. Herskovits");
        technicalinformation1.setValue(weka.core.TechnicalInformation.Field.YEAR, "1992");
        technicalinformation1.setValue(weka.core.TechnicalInformation.Field.TITLE, "A Bayesian method for the induction of probabilistic networks from data");
        technicalinformation1.setValue(weka.core.TechnicalInformation.Field.JOURNAL, "Machine Learning");
        technicalinformation1.setValue(weka.core.TechnicalInformation.Field.VOLUME, "9");
        technicalinformation1.setValue(weka.core.TechnicalInformation.Field.NUMBER, "4");
        technicalinformation1.setValue(weka.core.TechnicalInformation.Field.PAGES, "309-347");
        return technicalinformation;
    }

    public void search(BayesNet bayesnet, Instances instances) throws Exception {
        int ai[] = new int[instances.numAttributes()];
        ai[0] = instances.classIndex();
        int i = 0;
        for (int j = 1; j < instances.numAttributes(); j++) {
            if (i == instances.classIndex()) {
                i++;
            }
            ai[j] = i++;
        }

        if (m_bRandomOrder) {
            Random random = new Random();
            byte byte0;
            if (getInitAsNaiveBayes()) {
                byte0 = 0;
            } else {
                byte0 = -1;
            }
            for (int k = 0; k < instances.numAttributes(); k++) {
                int i1 = Math.abs(random.nextInt()) % instances.numAttributes();
                if (k != byte0 && i1 != byte0) {
                    int k1 = ai[k];
                    ai[k] = ai[i1];
                    ai[i1] = k1;
                }
            }

        }
        double d = calcScore(bayesnet);
        for (int l = 1; l < instances.numAttributes(); l++) {
            int j1 = ai[l];
            double d1 = d;
            for (boolean flag = bayesnet.getParentSet(j1).getNrOfParents() < getMaxNrOfParents(); flag && bayesnet.getParentSet(j1).getNrOfParents() < getMaxNrOfParents();) {
                int l1 = -1;
                for (int i2 = 0; i2 < l; i2++) {
                    int j2 = ai[i2];
                    double d2 = calcScoreWithExtraParent(j1, j2);
                    if (d2 > d1) {
                        d1 = d2;
                        l1 = j2;
                    }
                }

                if (l1 != -1) {
                    bayesnet.getParentSet(j1).addParent(l1, instances);
                    d = d1;
                    flag = true;
                } else {
                    flag = false;
                }
            }

        }

    }

    public void setMaxNrOfParents(int i) {
        m_nMaxNrOfParents = i;
    }

    public int getMaxNrOfParents() {
        return m_nMaxNrOfParents;
    }

    public void setInitAsNaiveBayes(boolean flag) {
        m_bInitAsNaiveBayes = flag;
    }

    public boolean getInitAsNaiveBayes() {
        return m_bInitAsNaiveBayes;
    }

    public void setRandomOrder(boolean flag) {
        m_bRandomOrder = flag;
    }

    public boolean getRandomOrder() {
        return m_bRandomOrder;
    }

    public Enumeration listOptions() {
        Vector vector = new Vector(0);
        vector.addElement(new Option("\tInitial structure is empty (instead of Naive Bayes)", "N", 0, "-N"));
        vector.addElement(new Option("\tMaximum number of parents", "P", 1, "-P <nr of parents>"));
        vector.addElement(new Option("\tRandom order.\n\t(default false)", "R", 0, "-R"));
        for (Enumeration enumeration = super.listOptions(); enumeration.hasMoreElements(); vector.addElement(enumeration.nextElement())) { }
        return vector.elements();
    }

    public void setOptions(String as[]) throws Exception {
        setRandomOrder(Utils.getFlag('R', as));
        m_bInitAsNaiveBayes = !Utils.getFlag('N', as);
        String s = Utils.getOption('P', as);
        if (s.length() != 0) {
            setMaxNrOfParents(Integer.parseInt(s));
        } else {
            setMaxNrOfParents(0x186a0);
        }
        super.setOptions(as);
    }

    public String[] getOptions() {
        String as[] = super.getOptions();
        String as1[] = new String[4 + as.length];
        int i = 0;
        as1[i++] = "-P";
        as1[i++] = (new StringBuilder()).append("").append(m_nMaxNrOfParents).toString();
        if (!m_bInitAsNaiveBayes) {
            as1[i++] = "-N";
        }
        if (getRandomOrder()) {
            as1[i++] = "-R";
        }
        for (int j = 0; j < as.length; j++) {
            as1[i++] = as[j];
        }

        while (i < as1.length)  {
            as1[i++] = "";
        }
        return as1;
    }

    public String randomOrderTipText() {
        return "When set to true, the order of the nodes in the network is random. Default random order is false and the order of the nodes in the dataset is used. In any case, when the network was initialized as Naive Bayes Network, the class variable is first in the ordering though.";
    }

    public String globalInfo() {
        return (new StringBuilder()).append("This Bayes Network learning algorithm uses a hill climbing algorithm restricted by an order on the variables.\n\nFor more information see:\n\n").append(getTechnicalInformation().toString()).append("\n\n").append("Works with nominal variables and no missing values only.").toString();
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.8 $");
    }
}
