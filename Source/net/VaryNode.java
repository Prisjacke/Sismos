// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.io.PrintStream;
import java.io.Serializable;
import weka.core.RevisionHandler;
import weka.core.RevisionUtils;

// Referenced classes of package weka.classifiers.bayes.net:
//            ADNode

public class VaryNode
    implements Serializable, RevisionHandler {

    private static final long serialVersionUID = 0xaa025719681cad58L;
    public int m_iNode;
    public int m_nMCV;
    public ADNode m_ADNodes[];

    public VaryNode(int i) {
        m_iNode = i;
    }

    public void getCounts(int ai[], int ai1[], int ai2[], int i, int j, ADNode adnode, boolean flag) {
        int k = ai1[i];
        for (int l = 0; l < m_ADNodes.length; l++) {
            if (l != m_nMCV) {
                if (m_ADNodes[l] != null) {
                    m_ADNodes[l].getCounts(ai, ai1, ai2, i + 1, j + ai2[i] * l, flag);
                }
                continue;
            }
            adnode.getCounts(ai, ai1, ai2, i + 1, j + ai2[i] * l, flag);
            for (int i1 = 0; i1 < m_ADNodes.length; i1++) {
                if (i1 != m_nMCV && m_ADNodes[i1] != null) {
                    m_ADNodes[i1].getCounts(ai, ai1, ai2, i + 1, j + ai2[i] * l, !flag);
                }
            }

        }

    }

    public void print(String s) {
        for (int i = 0; i < m_ADNodes.length; i++) {
            System.out.print((new StringBuilder()).append(s).append(i).append(": ").toString());
            if (m_ADNodes[i] == null) {
                if (i == m_nMCV) {
                    System.out.println("MCV");
                } else {
                    System.out.println("null");
                }
            } else {
                System.out.println();
                m_ADNodes[i].print();
            }
        }

    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.6 $");
    }
}
