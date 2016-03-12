// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;
import weka.classifiers.bayes.BayesNet;
import weka.core.RevisionHandler;
import weka.core.RevisionUtils;

// Referenced classes of package weka.classifiers.bayes.net:
//            MarginCalculator

public class m_bayesNet
    implements Serializable, RevisionHandler {

    private static final long serialVersionUID = 0x5a3e8421bd26234fL;
    int m_nNodes[];
    int m_nCardinality;
    double m_fiParent[];
    double m_fiChild[];
    des m_parentNode;
    des m_childNode;
    BayesNet m_bayesNet;
    final MarginCalculator this$0;

    public void updateFromParent() {
        double ad[] = update(m_parentNode);
        if (ad == null) {
            m_fiParent = null;
        } else {
            m_fiParent = ad;
            double d = 0.0D;
            for (int i = 0; i < m_nCardinality; i++) {
                d += m_fiParent[i];
            }

            for (int j = 0; j < m_nCardinality; j++) {
                m_fiParent[j] /= d;
            }

        }
    }

    public void updateFromChild() {
        double ad[] = update(m_childNode);
        if (ad == null) {
            m_fiChild = null;
        } else {
            m_fiChild = ad;
            double d = 0.0D;
            for (int i = 0; i < m_nCardinality; i++) {
                d += m_fiChild[i];
            }

            for (int j = 0; j < m_nCardinality; j++) {
                m_fiChild[j] /= d;
            }

        }
    }

    public double[] update(m_fiChild m_fichild) {
        if (m_fichild.m_fiChild == null) {
            return null;
        }
        double ad[] = new double[m_nCardinality];
        int ai[] = new int[m_fichild.des.length];
        int ai1[] = new int[m_bayesNet.getNrOfNodes()];
        for (int i = 0; i < m_fichild.des.length; i++) {
            ai1[m_fichild.des[i]] = i;
        }

label0:
        for (int j = 0; j < m_fichild.rdinality; j++) {
            int k = getCPT(m_fichild.des, m_fichild.des.length, ai, ai1, m_bayesNet);
            int l = getCPT(m_nNodes, m_nNodes.length, ai, ai1, m_bayesNet);
            ad[l] += m_fichild.m_bayesNet[k];
            int i1 = 0;
            ai[i1]++;
            do {
                if (i1 >= m_fichild.des.length || ai[i1] != m_bayesNet.getCardinality(m_fichild.des[i1])) {
                    continue label0;
                }
                ai[i1] = 0;
                if (++i1 < m_fichild.des.length) {
                    ai[i1]++;
                }
            } while (true);
        }

        return ad;
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.2 $");
    }

    (Set set, BayesNet bayesnet,  ,  1) {
        this$0 = MarginCalculator.this;
        super();
        m_nNodes = new int[set.size()];
        int i = 0;
        m_nCardinality = 1;
        for (Iterator iterator = set.iterator(); iterator.hasNext();) {
            int j = ((Integer)iterator.next()).intValue();
            m_nNodes[i++] = j;
            m_nCardinality *= bayesnet.getCardinality(j);
        }

        m_parentNode = 1;
        m_childNode = ;
        m_bayesNet = bayesnet;
    }
}
