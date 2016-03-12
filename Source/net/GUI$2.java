// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBoxMenuItem;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI

class val.viewMargins
    implements ActionListener {

    final JCheckBoxMenuItem val$viewMargins;
    final GUI this$0;

    public void actionPerformed(ActionEvent actionevent) {
        boolean flag = m_bViewMargins;
        m_bViewMargins = val$viewMargins.getState();
        if (!flag && val$viewMargins.getState()) {
            updateStatus();
        }
        repaint();
    }

    () {
        this$0 = final_gui;
        val$viewMargins = JCheckBoxMenuItem.this;
        super();
    }
}
