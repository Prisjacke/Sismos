// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.io.Serializable;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.RevisionHandler;
import weka.core.RevisionUtils;

public class ParentSet
    implements Serializable, RevisionHandler {

    static final long serialVersionUID = 0x39a999d80b47020eL;
    private int m_nParents[];
    private int m_nNrOfParents;
    private int m_nCardinalityOfParents;

    public int getParent(int i) {
        return m_nParents[i];
    }

    public int[] getParents() {
        return m_nParents;
    }

    public void SetParent(int i, int j) {
        m_nParents[i] = j;
    }

    public int getNrOfParents() {
        return m_nNrOfParents;
    }

    public boolean contains(int i) {
        for (int j = 0; j < m_nNrOfParents; j++) {
            if (m_nParents[j] == i) {
                return true;
            }
        }

        return false;
    }

    public int getCardinalityOfParents() {
        return m_nCardinalityOfParents;
    }

    public int getFreshCardinalityOfParents(Instances instances) {
        m_nCardinalityOfParents = 1;
        for (int i = 0; i < m_nNrOfParents; i++) {
            m_nCardinalityOfParents *= instances.attribute(m_nParents[i]).numValues();
        }

        return m_nCardinalityOfParents;
    }

    public ParentSet() {
        m_nNrOfParents = 0;
        m_nCardinalityOfParents = 1;
        m_nParents = new int[10];
        m_nNrOfParents = 0;
        m_nCardinalityOfParents = 1;
    }

    public ParentSet(int i) {
        m_nNrOfParents = 0;
        m_nCardinalityOfParents = 1;
        m_nParents = new int[i];
        m_nNrOfParents = 0;
        m_nCardinalityOfParents = 1;
    }

    public ParentSet(ParentSet parentset) {
        m_nNrOfParents = 0;
        m_nCardinalityOfParents = 1;
        m_nNrOfParents = parentset.m_nNrOfParents;
        m_nCardinalityOfParents = parentset.m_nCardinalityOfParents;
        m_nParents = new int[m_nNrOfParents];
        for (int i = 0; i < m_nNrOfParents; i++) {
            m_nParents[i] = parentset.m_nParents[i];
        }

    }

    public void maxParentSetSize(int i) {
        m_nParents = new int[i];
    }

    public void addParent(int i, Instances instances) {
        if (m_nNrOfParents == 10) {
            int ai[] = new int[50];
            for (int j = 0; j < m_nNrOfParents; j++) {
                ai[j] = m_nParents[j];
            }

            m_nParents = ai;
        }
        m_nParents[m_nNrOfParents] = i;
        m_nNrOfParents++;
        m_nCardinalityOfParents *= instances.attribute(i).numValues();
    }

    public void addParent(int i, int j, Instances instances) {
        if (m_nNrOfParents == 10) {
            int ai[] = new int[50];
            for (int l = 0; l < m_nNrOfParents; l++) {
                ai[l] = m_nParents[l];
            }

            m_nParents = ai;
        }
        for (int k = m_nNrOfParents; k > j; k--) {
            m_nParents[k] = m_nParents[k - 1];
        }

        m_nParents[j] = i;
        m_nNrOfParents++;
        m_nCardinalityOfParents *= instances.attribute(i).numValues();
    }

    public int deleteParent(int i, Instances instances) {
        int j;
        for (j = 0; m_nParents[j] != i && j < m_nNrOfParents; j++) { }
        int k = -1;
        if (j < m_nNrOfParents) {
            k = j;
        }
        if (j < m_nNrOfParents) {
            for (; j < m_nNrOfParents - 1; j++) {
                m_nParents[j] = m_nParents[j + 1];
            }

            m_nNrOfParents--;
            m_nCardinalityOfParents /= instances.attribute(i).numValues();
        }
        return k;
    }

    public void deleteLastParent(Instances instances) {
        m_nNrOfParents--;
        m_nCardinalityOfParents = m_nCardinalityOfParents / instances.attribute(m_nParents[m_nNrOfParents]).numValues();
    }

    public void copy(ParentSet parentset) {
        m_nCardinalityOfParents = parentset.m_nCardinalityOfParents;
        m_nNrOfParents = parentset.m_nNrOfParents;
        for (int i = 0; i < m_nNrOfParents; i++) {
            m_nParents[i] = parentset.m_nParents[i];
        }

    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.8 $");
    }
}
