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
import javax.swing.*;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI

class dlg extends dlg {

    private static final long serialVersionUID = 0xffff468fe6e64071L;
    JDialog dlg;
    final GUI this$0;

    public void actionPerformed(ActionEvent actionevent) {
        if (dlg == null) {
            dlg = new JDialog();
            dlg.setTitle("Graph Layout Options");
            final JCheckBox jCbCustomNodeSize = new JCheckBox("Custom Node Size");
            final JLabel jLbNodeWidth = new JLabel("Width");
            final JLabel jLbNodeHeight = new JLabel("Height");
            GUI.access$500(GUI.this).setHorizontalAlignment(0);
            GUI.access$500(GUI.this).setText((new StringBuilder()).append("").append(GUI.access$300(GUI.this)).toString());
            GUI.access$600(GUI.this).setHorizontalAlignment(0);
            GUI.access$600(GUI.this).setText((new StringBuilder()).append("").append(GUI.access$200(GUI.this)).toString());
            jLbNodeWidth.setEnabled(false);
            GUI.access$500(GUI.this).setEnabled(false);
            jLbNodeHeight.setEnabled(false);
            GUI.access$600(GUI.this).setEnabled(false);
            jCbCustomNodeSize.addActionListener(new ActionListener() {

                final JLabel val$jLbNodeWidth;
                final JLabel val$jLbNodeHeight;
                final GUI.ActionLayout this$1;

                public void actionPerformed(ActionEvent actionevent1) {
                    if (((JCheckBox)actionevent1.getSource()).isSelected()) {
                        jLbNodeWidth.setEnabled(true);
                        GUI.access$500(this$0).setEnabled(true);
                        jLbNodeHeight.setEnabled(true);
                        GUI.access$600(this$0).setEnabled(true);
                    } else {
                        jLbNodeWidth.setEnabled(false);
                        GUI.access$500(this$0).setEnabled(false);
                        jLbNodeHeight.setEnabled(false);
                        GUI.access$600(this$0).setEnabled(false);
                        setAppropriateSize();
                        setAppropriateNodeSize();
                    }
                }

             {
                this$1 = GUI.ActionLayout.this;
                jLbNodeWidth = jlabel;
                jLbNodeHeight = jlabel1;
                super();
            }
            });
            JButton jbutton = new JButton("Layout Graph");
            jbutton.setMnemonic('L');
            jbutton.addActionListener(new ActionListener() {

                final JCheckBox val$jCbCustomNodeSize;
                final GUI.ActionLayout this$1;

                public void actionPerformed(ActionEvent actionevent1) {
                    if (jCbCustomNodeSize.isSelected()) {
                        int i;
                        try {
                            i = Integer.parseInt(GUI.access$500(this$0).getText());
                        }
                        catch (NumberFormatException numberformatexception) {
                            JOptionPane.showMessageDialog(getParent(), "Invalid integer entered for node width.", "Error", 0);
                            i = GUI.access$300(this$0);
                            GUI.access$500(this$0).setText((new StringBuilder()).append("").append(GUI.access$300(this$0)).toString());
                        }
                        int j;
                        try {
                            j = Integer.parseInt(GUI.access$600(this$0).getText());
                        }
                        catch (NumberFormatException numberformatexception1) {
                            JOptionPane.showMessageDialog(getParent(), "Invalid integer entered for node height.", "Error", 0);
                            j = GUI.access$200(this$0);
                            GUI.access$500(this$0).setText((new StringBuilder()).append("").append(GUI.access$200(this$0)).toString());
                        }
                        if (i != GUI.access$300(this$0) || j != GUI.access$200(this$0)) {
                            GUI.access$302(this$0, i);
                            GUI.access$102(this$0, GUI.access$300(this$0) + 10);
                            GUI.access$202(this$0, j);
                        }
                    }
                    dlg.setVisible(false);
                    updateStatus();
                    layoutGraph();
                    m_jStatusBar.setText("Laying out Bayes net");
                }

             {
                this$1 = GUI.ActionLayout.this;
                jCbCustomNodeSize = jcheckbox;
                super();
            }
            });
            JButton jbutton1 = new JButton("Cancel");
            jbutton1.setMnemonic('C');
            jbutton1.addActionListener(new ActionListener() {

                final GUI.ActionLayout this$1;

                public void actionPerformed(ActionEvent actionevent1) {
                    dlg.setVisible(false);
                }

             {
                this$1 = GUI.ActionLayout.this;
                super();
            }
            });
            GridBagConstraints gridbagconstraints = new GridBagConstraints();
            dlg.setLayout(new GridBagLayout());
            Container container = new Container();
            container.setLayout(new GridBagLayout());
            gridbagconstraints.gridwidth = 1;
            gridbagconstraints.insets = new Insets(8, 0, 0, 0);
            gridbagconstraints.anchor = 18;
            gridbagconstraints.gridwidth = 0;
            container.add(jCbCustomNodeSize, gridbagconstraints);
            gridbagconstraints.gridwidth = -1;
            container.add(jLbNodeWidth, gridbagconstraints);
            gridbagconstraints.gridwidth = 0;
            container.add(GUI.access$500(GUI.this), gridbagconstraints);
            gridbagconstraints.gridwidth = -1;
            container.add(jLbNodeHeight, gridbagconstraints);
            gridbagconstraints.gridwidth = 0;
            container.add(GUI.access$600(GUI.this), gridbagconstraints);
            gridbagconstraints.fill = 2;
            dlg.add(container, gridbagconstraints);
            dlg.add(jbutton);
            gridbagconstraints.gridwidth = 0;
            dlg.add(jbutton1);
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
        super(GUI.this, "Layout", "Layout Graph", "layout", "ctrl L");
        dlg = null;
    }
}
