// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI

class it> extends it> {

    private static final long serialVersionUID = 0xffffed74ca4a39a7L;
    final GUI this$0;

    public void actionPerformed(ActionEvent actionevent) {
        JOptionPane.showMessageDialog(null, "Bayesian Network Workbench\nPart of Weka\n2007", "About Message", -1);
    }

    public () {
        this$0 = GUI.this;
        super(GUI.this, "About", "Help about", "about", "");
    }
}
