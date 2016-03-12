// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.event.ActionEvent;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI, EditableBayesNet

class  extends  {

    private static final long serialVersionUID = 0xfff8c1c8b239c261L;
    final GUI this$0;

    public void actionPerformed(ActionEvent actionevent) {
        copy();
    }

    public void copy() {
        String s = m_BayesNet.toXMLBIF03(m_Selection.lected());
        m_clipboard.xt(s);
    }

    public () {
        this$0 = GUI.this;
        super(GUI.this, "Copy", "Copy Nodes", "copy", "ctrl C");
    }

    public (String s, String s1, String s2, String s3) {
        this$0 = GUI.this;
        super(GUI.this, s, s1, s2, s3);
    }
}
