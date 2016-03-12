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

class t> extends t> {

    private static final long serialVersionUID = 0xfff8c19f04fe8465L;
    final GUI this$0;

    public void actionPerformed(ActionEvent actionevent) {
        int i = 0;
        int j = (int)(GUI.access$000(GUI.this) * 100D);
        if (j < 300) {
            i = j / 25;
        } else
        if (j < 700) {
            i = 6 + j / 50;
        } else {
            i = 13 + j / 100;
        }
        if (j >= 999) {
            setEnabled(false);
            return;
        }
        if (j >= 10) {
            if (i >= 22) {
                setEnabled(false);
            }
            if (j == 10 && !a_zoomout.isEnabled()) {
                a_zoomout.setEnabled(true);
            }
            m_jTfZoom.setText((new StringBuilder()).append(GUI.access$400(GUI.this)[i + 1]).append("%").toString());
            GUI.access$002(GUI.this, (double)GUI.access$400(GUI.this)[i + 1] / 100D);
        } else {
            if (!a_zoomout.isEnabled()) {
                a_zoomout.setEnabled(true);
            }
            m_jTfZoom.setText((new StringBuilder()).append(GUI.access$400(GUI.this)[0]).append("%").toString());
            GUI.access$002(GUI.this, (double)GUI.access$400(GUI.this)[0] / 100D);
        }
        setAppropriateSize();
        m_GraphPanel.paint();
        m_GraphPanel.validate();
        m_jScrollPane.revalidate();
        m_jStatusBar.setText("Zooming in");
    }

    public () {
        this$0 = GUI.this;
        super(GUI.this, "Zoom in", "Zoom in", "zoomin", "+");
    }
}
