// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.io.Serializable;

// Referenced classes of package weka.classifiers.bayes.net:
//            EditableBayesNet

class this._cls0
    implements Serializable {

    static final long serialVersionUID = 1L;
    final EditableBayesNet this$0;

    public void undo() {
    }

    public void redo() {
    }

    public String getUndoMsg() {
        return getMsg();
    }

    public String getRedoMsg() {
        return getMsg();
    }

    String getMsg() {
        String s = toString();
        int i = s.indexOf('$');
        int j = s.indexOf('@');
        StringBuffer stringbuffer = new StringBuffer();
        for (int k = i + 1; k < j; k++) {
            char c = s.charAt(k);
            if (Character.isUpperCase(c)) {
                stringbuffer.append(' ');
            }
            stringbuffer.append(s.charAt(k));
        }

        return stringbuffer.toString();
    }

    () {
        this$0 = EditableBayesNet.this;
        super();
    }
}
