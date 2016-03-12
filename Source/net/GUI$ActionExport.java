// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.event.ActionEvent;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI

class m_bIsExporting extends m_bIsExporting {

    boolean m_bIsExporting;
    private static final long serialVersionUID = 0xfff53e6024f5f661L;
    final GUI this$0;

    public void actionPerformed(ActionEvent actionevent) {
        m_bIsExporting = true;
        m_GraphPanel.veComponent();
        m_bIsExporting = false;
        repaint();
    }

    public boolean isExporting() {
        return m_bIsExporting;
    }

    public () {
        this$0 = GUI.this;
        super(GUI.this, "Export", "Export to graphics file", "export", "");
        m_bIsExporting = false;
    }
}
