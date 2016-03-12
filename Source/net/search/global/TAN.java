// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net.search.global;

import java.util.Enumeration;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.ParentSet;
import weka.core.Instances;
import weka.core.RevisionUtils;
import weka.core.TechnicalInformation;
import weka.core.TechnicalInformationHandler;

// Referenced classes of package weka.classifiers.bayes.net.search.global:
//            GlobalScoreSearchAlgorithm

public class TAN extends GlobalScoreSearchAlgorithm
    implements TechnicalInformationHandler {

    static final long serialVersionUID = 0x17cde36485e8e842L;


    public TechnicalInformation getTechnicalInformation() {
        TechnicalInformation technicalinformation = new TechnicalInformation(weka.core.TechnicalInformation.Type.ARTICLE);
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.AUTHOR, "N. Friedman and D. Geiger and M. Goldszmidt");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.YEAR, "1997");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.TITLE, "Bayesian network classifiers");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.JOURNAL, "Machine Learning");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.VOLUME, "29");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.NUMBER, "2-3");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.PAGES, "131-163");
        return technicalinformation;
    }

    public void buildStructure(BayesNet bayesnet, Instances instances) throws Exception {
        m_BayesNet = bayesnet;
        m_bInitAsNaiveBayes = true;
        m_nMaxNrOfParents = 2;
        super.buildStructure(bayesnet, instances);
        int i = instances.numAttributes();
        int j = instances.classIndex();
        int ai[] = new int[i - 1];
        int ai1[] = new int[i - 1];
        boolean aflag[] = new boolean[i];
        int k = -1;
        int i1 = -1;
        double d = 0.0D;
        for (int j1 = 0; j1 < i; j1++) {
            if (j1 == j) {
                continue;
            }
            for (int l1 = 0; l1 < i; l1++) {
                if (j1 == l1 || l1 == j) {
                    continue;
                }
                double d1 = calcScoreWithExtraParent(j1, l1);
                if (k == -1 || d1 > d) {
                    d = d1;
                    k = l1;
                    i1 = j1;
                }
            }

        }

        ai[0] = k;
        ai1[0] = i1;
        aflag[k] = true;
        aflag[i1] = true;
        for (int i2 = 1; i2 < i - 2; i2++) {
            int l = -1;
            for (int k1 = 0; k1 < i; k1++) {
                if (k1 == j) {
                    continue;
                }
                for (int j2 = 0; j2 < i; j2++) {
                    if (k1 == j2 || j2 == j || !aflag[k1] && !aflag[j2] || aflag[k1] && aflag[j2]) {
                        continue;
                    }
                    double d2 = calcScoreWithExtraParent(k1, j2);
                    if (l == -1 || d2 > d) {
                        d = d2;
                        l = j2;
                        i1 = k1;
                    }
                }

            }

            ai[i2] = l;
            ai1[i2] = i1;
            aflag[l] = true;
            aflag[i1] = true;
        }

        boolean aflag1[] = new boolean[i];
        for (int k2 = 0; k2 < i - 2; k2++) {
            if (!aflag1[ai[k2]]) {
                bayesnet.getParentSet(ai[k2]).addParent(ai1[k2], instances);
                aflag1[ai[k2]] = true;
                continue;
            }
            if (aflag1[ai1[k2]]) {
                throw new Exception("Bug condition found: too many arrows");
            }
            bayesnet.getParentSet(ai1[k2]).addParent(ai[k2], instances);
            aflag1[ai1[k2]] = true;
        }

    }

    public Enumeration listOptions() {
        return super.listOptions();
    }

    public void setOptions(String as[]) throws Exception {
        super.setOptions(as);
    }

    public String[] getOptions() {
        return super.getOptions();
    }

    public String globalInfo() {
        return (new StringBuilder()).append("This Bayes Network learning algorithm determines the maximum weight spanning tree and returns a Naive Bayes network augmented with a tree.\n\nFor more information see:\n\n").append(getTechnicalInformation().toString()).toString();
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.7 $");
    }
}
