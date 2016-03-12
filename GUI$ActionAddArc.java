// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI, EditableBayesNet

class t> extends t> {

    private static final long serialVersionUID = 0xfff8c19e8dc8f061L;
    final GUI this$0;

    public void actionPerformed(ActionEvent actionevent) {
        String s;
        String as[] = new String[m_BayesNet.getNrOfNodes()];
        for (int i = 0; i < as.length; i++) {
            as[i] = m_BayesNet.getNodeName(i);
        }

        s = (String)JOptionPane.showInputDialog(null, "Select child node", "Nodes", 0, null, as, as[0]);
        if (s == null || s.equals("")) {
            return;
        }
        try {
            int j = m_BayesNet.getNode(s);
            addArcInto(j);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        return;
    }

    public () {
        this$0 = GUI.this;
        super(GUI.this, "Add Arc", "Add Arc", "addarc", "");
    }
}
