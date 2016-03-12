// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI

class val.jLbNodeHeight
    implements ActionListener {

    final JLabel val$jLbNodeWidth;
    final JLabel val$jLbNodeHeight;
    final odeSize this$1;

    public void actionPerformed(ActionEvent actionevent) {
        if (((JCheckBox)actionevent.getSource()).isSelected()) {
            val$jLbNodeWidth.setEnabled(true);
            GUI.access$500(_fld0).setEnabled(true);
            val$jLbNodeHeight.setEnabled(true);
            GUI.access$600(_fld0).setEnabled(true);
        } else {
            val$jLbNodeWidth.setEnabled(false);
            GUI.access$500(_fld0).setEnabled(false);
            val$jLbNodeHeight.setEnabled(false);
            GUI.access$600(_fld0).setEnabled(false);
            setAppropriateSize();
            setAppropriateNodeSize();
        }
    }

    () {
        this$1 = final_;
        val$jLbNodeWidth = jlabel;
        val$jLbNodeHeight = JLabel.this;
        super();
    }
}
