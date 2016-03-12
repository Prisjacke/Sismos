// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI

class putValue extends AbstractAction {

    private static final long serialVersionUID = 0xfff8c19f0371c9e3L;
    final GUI this$0;

    public void actionPerformed(ActionEvent actionevent) {
    }

    public (String s, String s1, String s2, String s3) {
        this$0 = GUI.this;
        super(s);
        putValue("ShortDescription", s1);
        putValue("LongDescription", s1);
        if (s3.length() > 0) {
            KeyStroke keystroke = KeyStroke.getKeyStroke(s3);
            putValue("AcceleratorKey", keystroke);
        }
        putValue("MnemonicKey", Integer.valueOf(s.charAt(0)));
        java.net.URL url = ClassLoader.getSystemResource((new StringBuilder()).append("weka/classifiers/bayes/net/icons/").append(s2).append(".png").toString());
        if (url != null) {
            putValue("SmallIcon", new ImageIcon(url));
        } else {
            putValue("SmallIcon", new ImageIcon(new BufferedImage(20, 20, 6)));
        }
    }
}
