// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.io.FileReader;
import java.io.PrintStream;
import java.io.Serializable;
import weka.core.*;

// Referenced classes of package weka.classifiers.bayes.net:
//            VaryNode

public class ADNode
    implements Serializable, TechnicalInformationHandler, RevisionHandler {

    static final long serialVersionUID = 0x583e20ad9981afcL;
    static final int MIN_RECORD_SIZE = 0;
    public VaryNode m_VaryNodes[];
    public Instance m_Instances[];
    public int m_nCount;
    public int m_nStartNode;


    public TechnicalInformation getTechnicalInformation() {
        TechnicalInformation technicalinformation = new TechnicalInformation(weka.core.TechnicalInformation.Type.ARTICLE);
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.AUTHOR, "Andrew W. Moore and Mary S. Lee");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.YEAR, "1998");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.TITLE, "Cached Sufficient Statistics for Efficient Machine Learning with Large Datasets");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.JOURNAL, "Journal of Artificial Intelligence Research");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.VOLUME, "8");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.PAGES, "67-91");
        return technicalinformation;
    }

    public static VaryNode makeVaryNode(int i, FastVector fastvector, Instances instances) {
        VaryNode varynode = new VaryNode(i);
        int j = instances.attribute(i).numValues();
        FastVector afastvector[] = new FastVector[j];
        for (int k = 0; k < j; k++) {
            afastvector[k] = new FastVector();
        }

        for (int l = 0; l < fastvector.size(); l++) {
            int j1 = ((Integer)fastvector.elementAt(l)).intValue();
            afastvector[(int)instances.instance(j1).value(i)].addElement(new Integer(j1));
        }

        int i1 = afastvector[0].size();
        int k1 = 0;
        for (int l1 = 1; l1 < j; l1++) {
            if (afastvector[l1].size() > i1) {
                i1 = afastvector[l1].size();
                k1 = l1;
            }
        }

        varynode.m_nMCV = k1;
        varynode.m_ADNodes = new ADNode[j];
        for (int i2 = 0; i2 < j; i2++) {
            if (i2 == k1 || afastvector[i2].size() == 0) {
                varynode.m_ADNodes[i2] = null;
            } else {
                varynode.m_ADNodes[i2] = makeADTree(i + 1, afastvector[i2], instances);
            }
        }

        return varynode;
    }

    public static ADNode makeADTree(int i, FastVector fastvector, Instances instances) {
        ADNode adnode = new ADNode();
        adnode.m_nCount = fastvector.size();
        adnode.m_nStartNode = i;
        if (fastvector.size() < 0) {
            adnode.m_Instances = new Instance[fastvector.size()];
            for (int j = 0; j < fastvector.size(); j++) {
                adnode.m_Instances[j] = instances.instance(((Integer)fastvector.elementAt(j)).intValue());
            }

        } else {
            adnode.m_VaryNodes = new VaryNode[instances.numAttributes() - i];
            for (int k = i; k < instances.numAttributes(); k++) {
                adnode.m_VaryNodes[k - i] = makeVaryNode(k, fastvector, instances);
            }

        }
        return adnode;
    }

    public static ADNode makeADTree(Instances instances) {
        FastVector fastvector = new FastVector(instances.numInstances());
        for (int i = 0; i < instances.numInstances(); i++) {
            fastvector.addElement(new Integer(i));
        }

        return makeADTree(0, fastvector, instances);
    }

    public void getCounts(int ai[], int ai1[], int ai2[], int i, int j, boolean flag) {
        if (i >= ai1.length) {
            if (flag) {
                ai[j] -= m_nCount;
            } else {
                ai[j] += m_nCount;
            }
            return;
        }
        if (m_VaryNodes != null) {
            m_VaryNodes[ai1[i] - m_nStartNode].getCounts(ai, ai1, ai2, i, j, this, flag);
        } else {
            for (int k = 0; k < m_Instances.length; k++) {
                int l = j;
                Instance instance = m_Instances[k];
                for (int i1 = i; i1 < ai1.length; i1++) {
                    l += ai2[i1] * (int)instance.value(ai1[i1]);
                }

                if (flag) {
                    ai[l]--;
                } else {
                    ai[l]++;
                }
            }

        }
    }

    public void print() {
        String s = new String();
        for (int i = 0; i < m_nStartNode; i++) {
            s = (new StringBuilder()).append(s).append("  ").toString();
        }

        System.out.println((new StringBuilder()).append(s).append("Count = ").append(m_nCount).toString());
        if (m_VaryNodes != null) {
            for (int j = 0; j < m_VaryNodes.length; j++) {
                System.out.println((new StringBuilder()).append(s).append("Node ").append(j + m_nStartNode).toString());
                m_VaryNodes[j].print(s);
            }

        } else {
            System.out.println(m_Instances);
        }
    }

    public static void main(String args[]) {
        try {
            Instances instances = new Instances(new FileReader("\\iris.2.arff"));
            ADNode adnode = makeADTree(instances);
            int ai[] = new int[12];
            int ai1[] = new int[3];
            int ai2[] = new int[3];
            ai1[0] = 0;
            ai1[1] = 3;
            ai1[2] = 4;
            ai2[0] = 2;
            ai2[1] = 1;
            ai2[2] = 4;
            adnode.print();
            adnode.getCounts(ai, ai1, ai2, 0, 0, false);
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.7 $");
    }
}
