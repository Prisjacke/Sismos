// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI

class val.jCbCustomNodeSize
    implements ActionListener {

    final JCheckBox val$jCbCustomNodeSize;
    final g this$1;

    public void actionPerformed(ActionEvent actionevent) {
        if (val$jCbCustomNodeSize.isSelected()) {
            int i;
            try {
                i = Integer.parseInt(GUI.access$500(_fld0).getText());
            }
            catch (NumberFormatException numberformatexception) {
                JOptionPane.showMessageDialog(getParent(), "Invalid integer entered for node width.", "Error", 0);
                i = GUI.access$300(_fld0);
                GUI.access$500(_fld0).setText((new StringBuilder()).append("").append(GUI.access$300(_fld0)).toString());
            }
            int j;
            try {
                j = Integer.parseInt(GUI.access$600(_fld0).getText());
            }
            catch (NumberFormatException numberformatexception1) {
                JOptionPane.showMessageDialog(getParent(), "Invalid integer entered for node height.", "Error", 0);
                j = GUI.access$200(_fld0);
                GUI.access$500(_fld0).setText((new StringBuilder()).append("").append(GUI.access$200(_fld0)).toString());
            }
            if (i != GUI.access$300(_fld0) || j != GUI.access$200(_fld0)) {
                GUI.access$302(_fld0, i);
                GUI.access$102(_fld0, GUI.access$300(_fld0) + 10);
                GUI.access$202(_fld0, j);
            }
        }
        g.setVisible(false);
        updateStatus();
        layoutGraph();
        m_jStatusBar.setText("Laying out Bayes net");
    }

    () {
        this$1 = final_;
        val$jCbCustomNodeSize = JCheckBox.this;
        super();
    }
}
