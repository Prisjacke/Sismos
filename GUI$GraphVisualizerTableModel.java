// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.util.Random;
import javax.swing.table.AbstractTableModel;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI, EditableBayesNet

private class m_sColumnNames extends AbstractTableModel {

    private static final long serialVersionUID = 0xbd872a01d3dddd3cL;
    final String m_sColumnNames[];
    final double m_fProbs[][];
    int m_iNode;
    final GUI this$0;

    public void randomize() {
        int i = m_fProbs[0].length;
        Random random = new Random();
        for (int j = 0; j < m_fProbs.length; j++) {
            for (int k = 0; k < i - 1; k++) {
                m_fProbs[j][k] = random.nextDouble();
            }

            for (int l = 0; l < i - 1; l++) {
                for (int i1 = l + 1; i1 < i - 1; i1++) {
                    if (m_fProbs[j][l] > m_fProbs[j][i1]) {
                        double d1 = m_fProbs[j][l];
                        m_fProbs[j][l] = m_fProbs[j][i1];
                        m_fProbs[j][i1] = d1;
                    }
                }

            }

            double d = m_fProbs[j][0];
            for (int j1 = 1; j1 < i - 1; j1++) {
                m_fProbs[j][j1] = m_fProbs[j][j1] - d;
                d += m_fProbs[j][j1];
            }

            m_fProbs[j][i - 1] = 1.0D - d;
        }

    }

    public void setData() {
    }

    public int getColumnCount() {
        return m_sColumnNames.length;
    }

    public int getRowCount() {
        return m_fProbs.length;
    }

    public String getColumnName(int i) {
        return m_sColumnNames[i];
    }

    public Object getValueAt(int i, int j) {
        return new Double(m_fProbs[i][j]);
    }

    public void setValueAt(Object obj, int i, int j) {
        Double double1 = (Double)obj;
        if (double1.doubleValue() < 0.0D || double1.doubleValue() > 1.0D) {
            return;
        }
        m_fProbs[i][j] = double1.doubleValue();
        double d = 0.0D;
        for (int k = 0; k < m_fProbs[i].length; k++) {
            d += m_fProbs[i][k];
        }

        if (d > 1.0D) {
            for (int l = m_fProbs[i].length - 1; d > 1.0D; l--) {
                if (l != j) {
                    if (m_fProbs[i][l] > d - 1.0D) {
                        m_fProbs[i][l] -= d - 1.0D;
                        d = 1.0D;
                    } else {
                        d -= m_fProbs[i][l];
                        m_fProbs[i][l] = 0.0D;
                    }
                }
            }

        } else {
            for (int i1 = m_fProbs[i].length - 1; d < 1.0D; i1--) {
                if (i1 != j) {
                    m_fProbs[i][i1] += 1.0D - d;
                    d = 1.0D;
                }
            }

        }
        validate();
    }

    public Class getColumnClass(int i) {
        return getValueAt(0, i).getClass();
    }

    public boolean isCellEditable(int i, int j) {
        return true;
    }

    public (int i) {
        this$0 = GUI.this;
        super();
        m_iNode = i;
        double ad[][] = m_BayesNet.getDistribution(i);
        m_fProbs = new double[ad.length][ad[0].length];
        for (int j = 0; j < ad.length; j++) {
            for (int k = 0; k < ad[0].length; k++) {
                m_fProbs[j][k] = ad[j][k];
            }

        }

        m_sColumnNames = m_BayesNet.getValues(i);
    }
}
