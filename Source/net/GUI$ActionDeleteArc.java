// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.event.ActionEvent;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI, EditableBayesNet

class this._cls0 extends this._cls0 {

    private static final long serialVersionUID = 0xfff8c19e522e2661L;
    final GUI this$0;

    public void actionPerformed(ActionEvent actionevent) {
        int i = 0;
        for (int j = 0; j < m_BayesNet.getNrOfNodes(); j++) {
            i += m_BayesNet.getNrOfParents(j);
        }

        String as[] = new String[i];
        int k = 0;
        for (int l = 0; l < m_BayesNet.getNrOfNodes(); l++) {
            for (int i1 = 0; i1 < m_BayesNet.getNrOfParents(l); i1++) {
                int j1 = m_BayesNet.getParent(l, i1);
                String s = m_BayesNet.getNodeName(j1);
                s = (new StringBuilder()).append(s).append(" -> ").toString();
                s = (new StringBuilder()).append(s).append(m_BayesNet.getNodeName(l)).toString();
                as[k++] = s;
            }

        }

        deleteArc(as);
    }

    public () {
        this$0 = GUI.this;
        super(GUI.this, "Delete Arc", "Delete Arc", "delarc", "");
    }
}
