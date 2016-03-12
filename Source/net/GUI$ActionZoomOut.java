// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI

class > extends > {

    private static final long serialVersionUID = 0xffff468fe6e64071L;
    final GUI this$0;

    public void actionPerformed(ActionEvent actionevent) {
        int i = 0;
        int j = (int)(GUI.access$000(GUI.this) * 100D);
        if (j < 300) {
            i = (int)Math.ceil((double)j / 25D);
        } else
        if (j < 700) {
            i = 6 + (int)Math.ceil((double)j / 50D);
        } else {
            i = 13 + (int)Math.ceil((double)j / 100D);
        }
        if (j <= 10) {
            setEnabled(false);
        } else
        if (j < 999) {
            if (i <= 1) {
                setEnabled(false);
            }
            m_jTfZoom.setText((new StringBuilder()).append(GUI.access$400(GUI.this)[i - 1]).append("%").toString());
            GUI.access$002(GUI.this, (double)GUI.access$400(GUI.this)[i - 1] / 100D);
        } else {
            if (!a_zoomin.isEnabled()) {
                a_zoomin.setEnabled(true);
            }
            m_jTfZoom.setText((new StringBuilder()).append(GUI.access$400(GUI.this)[22]).append("%").toString());
            GUI.access$002(GUI.this, (double)GUI.access$400(GUI.this)[22] / 100D);
        }
        setAppropriateSize();
        m_GraphPanel.aint();
        m_GraphPanel.alidate();
        m_jScrollPane.revalidate();
        m_jStatusBar.setText("Zooming out");
    }

    public () {
        this$0 = GUI.this;
        super(GUI.this, "Zoom out", "Zoom out", "zoomout", "-");
    }
}
