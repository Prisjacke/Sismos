// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI

class nit> extends nit> {

    private static final long serialVersionUID = 0xffffed74ca4a39a6L;
    final GUI this$0;

    public void actionPerformed(ActionEvent actionevent) {
        JOptionPane.showMessageDialog(null, "See Weka Homepage\nhttp://www.cs.waikato.ac.nz/ml", "Help Message", -1);
    }

    public () {
        this$0 = GUI.this;
        super(GUI.this, "Help", "Bayesian Network Workbench Help", "help", "");
    }
}
