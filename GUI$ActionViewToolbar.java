// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.event.ActionEvent;
import javax.swing.JToolBar;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI

class this._cls0 extends this._cls0 {

    private static final long serialVersionUID = 0xffffed74ca4af13eL;
    final GUI this$0;

    public void actionPerformed(ActionEvent actionevent) {
        m_jTbTools.setVisible(!m_jTbTools.isVisible());
    }

    public () {
        this$0 = GUI.this;
        super(GUI.this, "View toolbar", "View toolbar", "toolbar", "");
    }
}
