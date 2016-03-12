// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

// Referenced classes of package weka.classifiers.bayes:
//            DMNBtext

public class m_WordLaplace
    implements Serializable {

    private double m_perWordPerClass[][];
    private double m_wordsPerClass[];
    int m_classIndex;
    private double m_classDistribution[];
    private int m_numAttributes;
    private int m_targetClass;
    private double m_WordLaplace;
    private double m_coefficient[];
    private double m_classRatio;
    private double m_wordRatio;
    final DMNBtext this$0;

    public void initClassifier(Instances instances) throws Exception {
        m_numAttributes = instances.numAttributes();
        m_perWordPerClass = new double[2][m_numAttributes];
        m_coefficient = new double[m_numAttributes];
        m_wordsPerClass = new double[2];
        m_classDistribution = new double[2];
        m_WordLaplace = Math.log(m_numAttributes);
        m_classIndex = instances.classIndex();
        for (int i = 0; i < 2; i++) {
            m_classDistribution[i] = 1.0D;
            m_wordsPerClass[i] = m_WordLaplace * (double)m_numAttributes;
            Arrays.fill(m_perWordPerClass[i], m_WordLaplace);
        }

    }

    public void updateClassifier(Instance instance) throws Exception {
        int i = 0;
        if (instance.value(instance.classIndex()) != (double)m_targetClass) {
            i = 1;
        }
        double d = 1.0D - distributionForInstance(instance)[i];
        double d1 = d * instance.weight();
        for (int j = 0; j < instance.numValues(); j++) {
            if (instance.index(j) == m_classIndex) {
                continue;
            }
            if (m_BinaryWord) {
                if (instance.valueSparse(j) > 0.0D) {
                    m_wordsPerClass[i] += d1;
                    m_perWordPerClass[i][instance.index(j)] += d1;
                }
            } else {
                double d2 = instance.valueSparse(j) * d1;
                m_wordsPerClass[i] += d2;
                m_perWordPerClass[i][instance.index(j)] += d2;
            }
            m_coefficient[instance.index(j)] = Math.log(m_perWordPerClass[0][instance.index(j)] / m_perWordPerClass[1][instance.index(j)]);
        }

        m_wordRatio = Math.log(m_wordsPerClass[0] / m_wordsPerClass[1]);
        m_classDistribution[i] += d1;
        m_classRatio = Math.log(m_classDistribution[0] / m_classDistribution[1]);
    }

    public double getLogProbForTargetClass(Instance instance) throws Exception {
        double d = m_classRatio;
        for (int i = 0; i < instance.numValues(); i++) {
            if (instance.index(i) == m_classIndex) {
                continue;
            }
            if (m_BinaryWord) {
                if (instance.valueSparse(i) > 0.0D) {
                    d += m_coefficient[instance.index(i)] - m_wordRatio;
                }
            } else {
                d += instance.valueSparse(i) * (m_coefficient[instance.index(i)] - m_wordRatio);
            }
        }

        return d;
    }

    public double[] distributionForInstance(Instance instance) throws Exception {
        double ad[] = new double[2];
        double d = getLogProbForTargetClass(instance);
        if (d > 709D) {
            ad[0] = 1.0D;
        } else {
            d = Math.exp(d);
            ad[0] = d / (1.0D + d);
        }
        ad[1] = 1.0D - ad[0];
        return ad;
    }

    public String toString() {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("\n");
        TreeMap treemap = new TreeMap();
        double ad[] = new double[m_numAttributes];
        for (int i = 0; i < m_numAttributes; i++) {
            if (i != m_headerInfo.classIndex()) {
                String s = (new StringBuilder()).append(m_headerInfo.attribute(i).name()).append(": ").append(m_coefficient[i]).toString();
                treemap.put(Double.valueOf(-1D * Math.abs(m_coefficient[i])), s);
            }
        }

        for (Iterator iterator = treemap.values().iterator(); iterator.hasNext(); stringbuffer.append("\n")) {
            stringbuffer.append((String)iterator.next());
        }

        return stringbuffer.toString();
    }

    public void setTargetClass(int i) {
        m_targetClass = i;
    }

    public int getTargetClass() {
        return m_targetClass;
    }

    public () {
        this$0 = DMNBtext.this;
        super();
        m_classIndex = -1;
        m_targetClass = -1;
        m_WordLaplace = 1.0D;
    }
}
