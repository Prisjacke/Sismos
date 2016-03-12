// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI

class this._cls0
    implements ActionListener {

    final GUI this$0;

    public void actionPerformed(ActionEvent actionevent) {
        JTextField jtextfield = (JTextField)actionevent.getSource();
        try {
            int i = -1;
            i = jtextfield.getText().indexOf('%');
            if (i == -1) {
                i = Integer.parseInt(jtextfield.getText());
            } else {
                i = Integer.parseInt(jtextfield.getText().substring(0, i));
            }
            if (i <= 999) {
                GUI.access$002(GUI.this, (double)i / 100D);
            }
            jtextfield.setText((new StringBuilder()).append((int)(GUI.access$000(GUI.this) * 100D)).append("%").toString());
            if (GUI.access$000(GUI.this) > 0.10000000000000001D) {
                if (!a_zoomout.isEnabled()) {
                    a_zoomout.setEnabled(true);
                }
            } else {
                a_zoomout.setEnabled(false);
            }
            if (GUI.access$000(GUI.this) < 9.9900000000000002D) {
                if (!a_zoomin.isEnabled()) {
                    a_zoomin.setEnabled(true);
                }
            } else {
                a_zoomin.setEnabled(false);
            }
            setAppropriateSize();
            m_GraphPanel.repaint();
            m_GraphPanel.invalidate();
            m_jScrollPane.revalidate();
        }
        catch (NumberFormatException numberformatexception) {
            JOptionPane.showMessageDialog(getParent(), "Invalid integer entered for zoom.", "Error", 0);
            jtextfield.setText((new StringBuilder()).append(GUI.access$000(GUI.this) * 100D).append("%").toString());
        }
    }

    aphPanel() {
        this$0 = GUI.this;
        super();
    }
}
