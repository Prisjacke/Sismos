// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.event.ActionEvent;
import javax.swing.JLabel;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI, EditableBayesNet

class this._cls0 extends this._cls0 {

    private static final long serialVersionUID = 0xfff8c1c8b239c261L;
    final GUI this$0;

    public void actionPerformed(ActionEvent actionevent) {
        try {
            m_BayesNet.paste(m_clipboard.t());
            updateStatus();
            m_jStatusBar.setText(m_BayesNet.lastActionMsg());
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public boolean isEnabled() {
        return m_clipboard.t();
    }

    public () {
        this$0 = GUI.this;
        super(GUI.this, "Paste", "Paste Nodes", "paste", "ctrl V");
    }
}
