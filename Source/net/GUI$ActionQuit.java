// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI, EditableBayesNet

class <init> extends <init> {

    private static final long serialVersionUID = 0xfff8c19f04fe8465L;
    final GUI this$0;

    public void actionPerformed(ActionEvent actionevent) {
        if (m_BayesNet.isChanged()) {
            int i = JOptionPane.showConfirmDialog(null, "Network changed. Do you want to save it?", "Save before closing?", 1);
            if (i == 2) {
                return;
            }
            if (i == 0 && !saveAs()) {
                return;
            }
        }
        System.exit(0);
    }

    public t() {
        this$0 = GUI.this;
        super(GUI.this, "Exit", "Exit Program", "exit", "");
    }
}
