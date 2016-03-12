// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JTextField;
import weka.core.Instances;

// Referenced classes of package weka.classifiers.bayes.net:
//            BayesNetGenerator, MarginCalculator, GUI

class val.jTfFile
    implements ActionListener {

    final JTextField val$jTfNrOfInstances;
    final JTextField val$jTfFile;
    final g this$1;

    public void actionPerformed(ActionEvent actionevent) {
        try {
            String s = "tmp.bif.file.xml";
            BayesNetGenerator bayesnetgenerator = new BayesNetGenerator();
            String as[] = new String[4];
            as[0] = "-M";
            as[1] = (new StringBuilder()).append("").append(val$jTfNrOfInstances.getText()).toString();
            as[2] = "-F";
            as[3] = s;
            FileWriter filewriter = new FileWriter(s);
            StringBuffer stringbuffer = new StringBuffer();
            if (m_marginCalculator == null) {
                m_marginCalculator = new MarginCalculator();
                m_marginCalculator.calcMargins(m_BayesNet);
            }
            stringbuffer.append(m_marginCalculator.toXMLBIF03());
            filewriter.write(stringbuffer.toString());
            filewriter.close();
            bayesnetgenerator.setOptions(as);
            bayesnetgenerator.generateRandomNetwork();
            bayesnetgenerator.generateInstances();
            m_Instances = bayesnetgenerator.m_Instances;
            a_learn.setEnabled(true);
            a_learnCPT.setEnabled(true);
            sFile = val$jTfFile.getText();
            if (sFile != null && !sFile.equals("")) {
                FileWriter filewriter1 = new FileWriter(sFile);
                StringBuffer stringbuffer1 = new StringBuffer();
                stringbuffer1.append(m_Instances.toString());
                filewriter1.write(stringbuffer1.toString());
                filewriter1.close();
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        g.setVisible(false);
    }

    () {
        this$1 = final_;
        val$jTfNrOfInstances = jtextfield;
        val$jTfFile = JTextField.this;
        super();
    }
}
