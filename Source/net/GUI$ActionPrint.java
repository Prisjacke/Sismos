// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.event.ActionEvent;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import javax.swing.JLabel;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI

class m_bIsPrinting extends m_bIsPrinting {

    private static final long serialVersionUID = 0xffffed74d0c96ee6L;
    boolean m_bIsPrinting;
    final GUI this$0;

    public void actionPerformed(ActionEvent actionevent) {
        PrinterJob printerjob = PrinterJob.getPrinterJob();
        printerjob.setPrintable(m_GraphPanel);
        if (printerjob.printDialog()) {
            try {
                m_bIsPrinting = true;
                printerjob.print();
                m_bIsPrinting = false;
            }
            catch (PrinterException printerexception) {
                m_jStatusBar.setText((new StringBuilder()).append("Error printing: ").append(printerexception).toString());
                m_bIsPrinting = false;
            }
        }
        m_jStatusBar.setText("Print");
    }

    public boolean isPrinting() {
        return m_bIsPrinting;
    }

    public () {
        this$0 = GUI.this;
        super(GUI.this, "Print", "Print Graph", "print", "ctrl P");
        m_bIsPrinting = false;
    }
}
