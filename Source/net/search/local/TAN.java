// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net.search.local;

import java.util.Enumeration;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.ParentSet;
import weka.core.Instances;
import weka.core.RevisionUtils;
import weka.core.TechnicalInformation;
import weka.core.TechnicalInformationHandler;

// Referenced classes of package weka.classifiers.bayes.net.search.local:
//            LocalScoreSearchAlgorithm

public class TAN extends LocalScoreSearchAlgorithm
    implements TechnicalInformationHandler {

    static final long serialVersionUID = 0xd65040819a36992L;


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
        m_bInitAsNaiveBayes = true;
        m_nMaxNrOfParents = 2;
        super.buildStructure(bayesnet, instances);
        int i = instances.numAttributes();
        double ad[] = new double[instances.numAttributes()];
        for (int j = 0; j < i; j++) {
            ad[j] = calcNodeScore(j);
        }

        double ad1[][] = new double[i][i];
        for (int k = 0; k < i; k++) {
            for (int i1 = 0; i1 < i; i1++) {
                if (k != i1) {
                    ad1[k][i1] = calcScoreWithExtraParent(k, i1);
                }
            }

        }

        int l = instances.classIndex();
        int ai[] = new int[i - 1];
        int ai1[] = new int[i - 1];
        boolean aflag[] = new boolean[i];
        int j1 = -1;
        int l1 = -1;
        double d = 0.0D;
        for (int i2 = 0; i2 < i; i2++) {
            if (i2 == l) {
                continue;
            }
            for (int k2 = 0; k2 < i; k2++) {
                if (i2 != k2 && k2 != l && (j1 == -1 || ad1[i2][k2] - ad[i2] > d)) {
                    d = ad1[i2][k2] - ad[i2];
                    j1 = k2;
                    l1 = i2;
                }
            }

        }

        ai[0] = j1;
        ai1[0] = l1;
        aflag[j1] = true;
        aflag[l1] = true;
        for (int l2 = 1; l2 < i - 2; l2++) {
            int k1 = -1;
            for (int j2 = 0; j2 < i; j2++) {
                if (j2 == l) {
                    continue;
                }
                for (int i3 = 0; i3 < i; i3++) {
                    if (j2 != i3 && i3 != l && (aflag[j2] || aflag[i3]) && (!aflag[j2] || !aflag[i3]) && (k1 == -1 || ad1[j2][i3] - ad[j2] > d)) {
                        d = ad1[j2][i3] - ad[j2];
                        k1 = i3;
                        l1 = j2;
                    }
                }

            }

            ai[l2] = k1;
            ai1[l2] = l1;
            aflag[k1] = true;
            aflag[l1] = true;
        }

        boolean aflag1[] = new boolean[i];
        for (int j3 = 0; j3 < i - 2; j3++) {
            if (!aflag1[ai[j3]]) {
                bayesnet.getParentSet(ai[j3]).addParent(ai1[j3], instances);
                aflag1[ai[j3]] = true;
                continue;
            }
            if (aflag1[ai1[j3]]) {
                throw new Exception("Bug condition found: too many arrows");
            }
            bayesnet.getParentSet(ai1[j3]).addParent(ai[j3], instances);
            aflag1[ai1[j3]] = true;
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
        return (new StringBuilder()).append("This Bayes Network learning algorithm determines the maximum weight spanning tree  and returns a Naive Bayes network augmented with a tree.\n\nFor more information see:\n\n").append(getTechnicalInformation().toString()).toString();
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.7 $");
    }
}
