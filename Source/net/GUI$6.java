// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI

class val.dlg
    implements ActionListener {

    final JDialog val$dlg;
    final GUI this$0;

    public void actionPerformed(ActionEvent actionevent) {
        val$dlg.setVisible(false);
    }

    () {
        this$0 = final_gui;
        val$dlg = JDialog.this;
        super();
    }
}
