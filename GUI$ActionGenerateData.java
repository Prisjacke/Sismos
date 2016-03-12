// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import javax.swing.*;
import weka.core.Instances;
import weka.gui.ExtensionFileFilter;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI, BayesNetGenerator, MarginCalculator

class dlg extends dlg {

    private static final long serialVersionUID = 0xfff8c19f04fe8464L;
    int m_nNrOfInstances;
    int m_nSeed;
    String m_sFile;
    JDialog dlg;
    final GUI this$0;

    public void actionPerformed(ActionEvent actionevent) {
        if (dlg == null) {
            dlg = new JDialog();
            dlg.setTitle("Generate Random Data Options");
            JLabel jlabel = new JLabel("Nr of instances");
            final JTextField jTfNrOfInstances = new JTextField(3);
            jTfNrOfInstances.setHorizontalAlignment(0);
            jTfNrOfInstances.setText((new StringBuilder()).append("").append(m_nNrOfInstances).toString());
            JLabel jlabel1 = new JLabel("Random seed");
            JTextField jtextfield = new JTextField(3);
            jtextfield.setHorizontalAlignment(0);
            jtextfield.setText((new StringBuilder()).append("").append(m_nSeed).toString());
            JLabel jlabel2 = new JLabel("Output file (optional)");
            final JTextField jTfFile = new JTextField(12);
            jTfFile.setHorizontalAlignment(0);
            jTfFile.setText(m_sFile);
            JButton jbutton = new JButton("Generate Data");
            jbutton.addActionListener(new ActionListener() {

                final JTextField val$jTfNrOfInstances;
                final JTextField val$jTfFile;
                final GUI.ActionGenerateData this$1;

                public void actionPerformed(ActionEvent actionevent1) {
                    try {
                        String s = "tmp.bif.file.xml";
                        BayesNetGenerator bayesnetgenerator = new BayesNetGenerator();
                        String as[] = new String[4];
                        as[0] = "-M";
                        as[1] = (new StringBuilder()).append("").append(jTfNrOfInstances.getText()).toString();
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
                        m_sFile = jTfFile.getText();
                        if (m_sFile != null && !m_sFile.equals("")) {
                            FileWriter filewriter1 = new FileWriter(m_sFile);
                            StringBuffer stringbuffer1 = new StringBuffer();
                            stringbuffer1.append(m_Instances.toString());
                            filewriter1.write(stringbuffer1.toString());
                            filewriter1.close();
                        }
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    dlg.setVisible(false);
                }

             {
                this$1 = GUI.ActionGenerateData.this;
                jTfNrOfInstances = jtextfield;
                jTfFile = jtextfield1;
                super();
            }
            });
            JButton jbutton1 = new JButton("Browse");
            jbutton1.addActionListener(new ActionListener() {

                final JTextField val$jTfFile;
                final GUI.ActionGenerateData this$1;

                public void actionPerformed(ActionEvent actionevent1) {
                    JFileChooser jfilechooser = new JFileChooser(System.getProperty("user.dir"));
                    ExtensionFileFilter extensionfilefilter = new ExtensionFileFilter(".arff", "Arff files");
                    jfilechooser.addChoosableFileFilter(extensionfilefilter);
                    jfilechooser.setDialogTitle("Save Instances As");
                    int i = jfilechooser.showSaveDialog(this$0);
                    if (i == 0) {
                        String s = jfilechooser.getSelectedFile().toString();
                        jTfFile.setText(s);
                    }
                    dlg.setVisible(true);
                }

             {
                this$1 = GUI.ActionGenerateData.this;
                jTfFile = jtextfield;
                super();
            }
            });
            JButton jbutton2 = new JButton("Cancel");
            jbutton2.setMnemonic('C');
            jbutton2.addActionListener(new ActionListener() {

                final GUI.ActionGenerateData this$1;

                public void actionPerformed(ActionEvent actionevent1) {
                    dlg.setVisible(false);
                }

             {
                this$1 = GUI.ActionGenerateData.this;
                super();
            }
            });
            GridBagConstraints gridbagconstraints = new GridBagConstraints();
            dlg.setLayout(new GridBagLayout());
            Container container = new Container();
            container.setLayout(new GridBagLayout());
            gridbagconstraints.gridwidth = 2;
            gridbagconstraints.insets = new Insets(8, 0, 0, 0);
            gridbagconstraints.anchor = 18;
            gridbagconstraints.gridwidth = -1;
            gridbagconstraints.fill = 2;
            container.add(jlabel, gridbagconstraints);
            gridbagconstraints.gridwidth = 0;
            container.add(jTfNrOfInstances, gridbagconstraints);
            gridbagconstraints.gridwidth = -1;
            container.add(jlabel1, gridbagconstraints);
            gridbagconstraints.gridwidth = 0;
            container.add(jtextfield, gridbagconstraints);
            gridbagconstraints.gridwidth = -1;
            container.add(jlabel2, gridbagconstraints);
            gridbagconstraints.gridwidth = 0;
            container.add(jTfFile, gridbagconstraints);
            gridbagconstraints.gridwidth = 0;
            container.add(jbutton1, gridbagconstraints);
            gridbagconstraints.fill = 2;
            dlg.add(container, gridbagconstraints);
            dlg.add(jbutton);
            gridbagconstraints.gridwidth = 0;
            dlg.add(jbutton2);
        }
        dlg.setLocation(100, 100);
        dlg.setVisible(true);
        dlg.setSize(dlg.getPreferredSize());
        dlg.setVisible(false);
        dlg.setVisible(true);
        dlg.repaint();
    }

    public _cls3.this._cls1() {
        this$0 = GUI.this;
        super(GUI.this, "Generate Data", "Generate Random Instances from Network", "generate.data", "ctrl D");
        m_nNrOfInstances = 100;
        m_nSeed = 1234;
        m_sFile = "";
        dlg = null;
    }
}
