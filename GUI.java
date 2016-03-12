// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyEditor;
import java.io.*;
import java.util.Random;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import weka.classifiers.Classifier;
import weka.core.*;
import weka.gui.ExtensionFileFilter;
import weka.gui.GenericObjectEditor;
import weka.gui.PropertyDialog;
import weka.gui.graphvisualizer.*;
import weka.gui.visualize.PrintablePanel;

// Referenced classes of package weka.classifiers.bayes.net:
//            EditableBayesNet, BIFReader, MarginCalculator, BayesNetGenerator

public class GUI extends JPanel
    implements LayoutCompleteEventListener {
    private class GraphVisualizerMouseMotionListener extends MouseMotionAdapter {

        int m_nLastNode;
        int m_nPosX;
        int m_nPosY;
        final GUI this$0;

        int getGraphNode(MouseEvent mouseevent) {
            m_nPosX = m_nPosY = 0;
            Rectangle rectangle = new Rectangle(0, 0, (int)((double)m_nPaddedNodeWidth * m_fScale), (int)((double)m_nNodeHeight * m_fScale));
            m_nPosX += mouseevent.getX();
            m_nPosY += mouseevent.getY();
            for (int i = 0; i < m_BayesNet.getNrOfNodes(); i++) {
                rectangle.x = (int)((double)m_BayesNet.getPositionX(i) * m_fScale);
                rectangle.y = (int)((double)m_BayesNet.getPositionY(i) * m_fScale);
                if (rectangle.contains(m_nPosX, m_nPosY)) {
                    return i;
                }
            }

            return -1;
        }

        public void mouseDragged(MouseEvent mouseevent) {
            if (m_nSelectedRect != null) {
                m_nSelectedRect.width = mouseevent.getPoint().x - m_nSelectedRect.x;
                m_nSelectedRect.height = mouseevent.getPoint().y - m_nSelectedRect.y;
                repaint();
                return;
            }
            int i = getGraphNode(mouseevent);
            if (i >= 0) {
                if (m_Selection.getSelected().size() > 0) {
                    if (m_Selection.getSelected().contains(Integer.valueOf(i))) {
                        m_BayesNet.setPosition(i, (int)((double)m_nPosX / m_fScale - (double)(m_nPaddedNodeWidth / 2)), (int)((double)m_nPosY / m_fScale - (double)(m_nNodeHeight / 2)), m_Selection.getSelected());
                    } else {
                        m_Selection.clear();
                        m_BayesNet.setPosition(i, (int)((double)m_nPosX / m_fScale - (double)(m_nPaddedNodeWidth / 2)), (int)((double)m_nPosY / m_fScale - (double)(m_nNodeHeight / 2)));
                    }
                    repaint();
                } else {
                    m_BayesNet.setPosition(i, (int)((double)m_nPosX / m_fScale - (double)(m_nPaddedNodeWidth / 2)), (int)((double)m_nPosY / m_fScale - (double)(m_nNodeHeight / 2)));
                }
                m_jStatusBar.setText(m_BayesNet.lastActionMsg());
                a_undo.setEnabled(true);
                a_redo.setEnabled(false);
                m_GraphPanel.highLight(i);
            }
            if (i < 0) {
                if (m_nLastNode >= 0) {
                    m_GraphPanel.repaint();
                    m_nLastNode = -1;
                } else {
                    m_nSelectedRect = new Rectangle(mouseevent.getPoint().x, mouseevent.getPoint().y, 1, 1);
                    m_GraphPanel.repaint();
                }
            }
        }

        public void mouseMoved(MouseEvent mouseevent) {
            int i = getGraphNode(mouseevent);
            if (i >= 0 && i != m_nLastNode) {
                m_GraphPanel.highLight(i);
                if (m_nLastNode >= 0) {
                    m_GraphPanel.highLight(m_nLastNode);
                }
                m_nLastNode = i;
            }
            if (i < 0 && m_nLastNode >= 0) {
                m_GraphPanel.repaint();
                m_nLastNode = -1;
            }
        }

        private GraphVisualizerMouseMotionListener() {
            this$0 = GUI.this;
            super();
            m_nLastNode = -1;
        }

    }

    private class GraphVisualizerMouseListener extends MouseAdapter {

        int m_nPosX;
        int m_nPosY;
        final GUI this$0;

        public void mouseClicked(MouseEvent mouseevent) {
            Rectangle rectangle = new Rectangle(0, 0, (int)((double)m_nPaddedNodeWidth * m_fScale), (int)((double)m_nNodeHeight * m_fScale));
            int i = mouseevent.getX();
            int j = mouseevent.getY();
            for (int k = 0; k < m_BayesNet.getNrOfNodes(); k++) {
                rectangle.x = (int)((double)m_BayesNet.getPositionX(k) * m_fScale);
                rectangle.y = (int)((double)m_BayesNet.getPositionY(k) * m_fScale);
                if (rectangle.contains(i, j)) {
                    m_nCurrentNode = k;
                    if (mouseevent.getButton() == 3) {
                        handleRightNodeClick(mouseevent);
                    }
                    if (mouseevent.getButton() == 1) {
                        if ((mouseevent.getModifiersEx() & 0x80) != 0) {
                            m_Selection.toggleSelection(m_nCurrentNode);
                        } else
                        if ((mouseevent.getModifiersEx() & 0x40) != 0) {
                            m_Selection.addToSelection(m_nCurrentNode);
                        } else {
                            m_Selection.clear();
                            m_Selection.addToSelection(m_nCurrentNode);
                        }
                        repaint();
                    }
                    return;
                }
            }

            if (mouseevent.getButton() == 3) {
                handleRightClick(mouseevent, (int)((double)i / m_fScale), (int)((double)j / m_fScale));
            }
        }

        public void mouseReleased(MouseEvent mouseevent) {
            if (m_nSelectedRect != null) {
                if ((mouseevent.getModifiersEx() & 0x80) != 0) {
                    m_Selection.toggleSelection(m_nSelectedRect);
                } else
                if ((mouseevent.getModifiersEx() & 0x40) != 0) {
                    m_Selection.addToSelection(m_nSelectedRect);
                } else {
                    m_Selection.clear();
                    m_Selection.addToSelection(m_nSelectedRect);
                }
                m_nSelectedRect = null;
                repaint();
            }
        }

        void handleRightClick(MouseEvent mouseevent, int i, int j) {
            ActionListener actionlistener = new ActionListener() {

                final GraphVisualizerMouseListener this$1;

                public void actionPerformed(ActionEvent actionevent) {
                    if (actionevent.getActionCommand().equals("Add node")) {
                        a_addnode.addNode(m_nPosX, m_nPosY);
                        return;
                    } else {
                        repaint();
                        return;
                    }
                }

                 {
                    this$1 = GraphVisualizerMouseListener.this;
                    super();
                }
            };
            JPopupMenu jpopupmenu = new JPopupMenu("Choose a value");
            JMenuItem jmenuitem = new JMenuItem("Add node");
            jmenuitem.addActionListener(actionlistener);
            jpopupmenu.add(jmenuitem);
            FastVector fastvector = m_Selection.getSelected();
            JMenu jmenu = new JMenu("Add parent");
            jpopupmenu.add(jmenu);
            if (fastvector.size() == 0) {
                jmenu.setEnabled(false);
            } else {
                int k = m_BayesNet.getNrOfNodes();
                boolean aflag[] = new boolean[k];
                for (int l = 0; l < fastvector.size(); l++) {
                    aflag[((Integer)fastvector.elementAt(l)).intValue()] = true;
                }

                for (int i1 = 0; i1 < k; i1++) {
                    for (int k1 = 0; k1 < k; k1++) {
                        for (int j2 = 0; j2 < m_BayesNet.getNrOfParents(k1); j2++) {
                            if (aflag[m_BayesNet.getParent(k1, j2)]) {
                                aflag[k1] = true;
                            }
                        }

                    }

                }

                for (int j1 = 0; j1 < fastvector.size(); j1++) {
                    int l1 = ((Integer)fastvector.elementAt(j1)).intValue();
                    for (int k2 = 0; k2 < m_BayesNet.getNrOfParents(l1); k2++) {
                        aflag[m_BayesNet.getParent(l1, k2)] = true;
                    }

                }

                ActionListener actionlistener1 = new ActionListener() {

                    final GraphVisualizerMouseListener this$1;

                    public void actionPerformed(ActionEvent actionevent) {
                        try {
                            m_BayesNet.addArc(actionevent.getActionCommand(), m_Selection.getSelected());
                            m_jStatusBar.setText(m_BayesNet.lastActionMsg());
                            updateStatus();
                        }
                        catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }

                 {
                    this$1 = GraphVisualizerMouseListener.this;
                    super();
                }
                };
                int i2 = 0;
                for (int l2 = 0; l2 < k; l2++) {
                    if (!aflag[l2]) {
                        JMenuItem jmenuitem1 = new JMenuItem(m_BayesNet.getNodeName(l2));
                        jmenuitem1.addActionListener(actionlistener1);
                        jmenu.add(jmenuitem1);
                        i2++;
                    }
                }

                if (i2 == 0) {
                    jmenu.setEnabled(false);
                }
            }
            m_nPosX = i;
            m_nPosY = j;
            jpopupmenu.setLocation(mouseevent.getX(), mouseevent.getY());
            jpopupmenu.show(m_GraphPanel, mouseevent.getX(), mouseevent.getY());
        }

        void handleRightNodeClick(MouseEvent mouseevent) {
            m_Selection.clear();
            repaint();
            ActionListener actionlistener = new ActionListener() {

                final GraphVisualizerMouseListener this$1;

                public void actionPerformed(ActionEvent actionevent) {
                    renameValue(m_nCurrentNode, actionevent.getActionCommand());
                }

                 {
                    this$1 = GraphVisualizerMouseListener.this;
                    super();
                }
            };
            ActionListener actionlistener1 = new ActionListener() {

                final GraphVisualizerMouseListener this$1;

                public void actionPerformed(ActionEvent actionevent) {
                    delValue(m_nCurrentNode, actionevent.getActionCommand());
                }

                 {
                    this$1 = GraphVisualizerMouseListener.this;
                    super();
                }
            };
            ActionListener actionlistener2 = new ActionListener() {

                final GraphVisualizerMouseListener this$1;

                public void actionPerformed(ActionEvent actionevent) {
                    try {
                        m_BayesNet.addArc(actionevent.getActionCommand(), m_BayesNet.getNodeName(m_nCurrentNode));
                        m_jStatusBar.setText(m_BayesNet.lastActionMsg());
                        updateStatus();
                    }
                    catch (Exception exception1) {
                        exception1.printStackTrace();
                    }
                }

                 {
                    this$1 = GraphVisualizerMouseListener.this;
                    super();
                }
            };
            ActionListener actionlistener3 = new ActionListener() {

                final GraphVisualizerMouseListener this$1;

                public void actionPerformed(ActionEvent actionevent) {
                    deleteArc(m_nCurrentNode, actionevent.getActionCommand());
                }

                 {
                    this$1 = GraphVisualizerMouseListener.this;
                    super();
                }
            };
            ActionListener actionlistener4 = new ActionListener() {

                final GraphVisualizerMouseListener this$1;

                public void actionPerformed(ActionEvent actionevent) {
                    deleteArc(actionevent.getActionCommand(), m_nCurrentNode);
                }

                 {
                    this$1 = GraphVisualizerMouseListener.this;
                    super();
                }
            };
            ActionListener actionlistener5 = new ActionListener() {

                final GraphVisualizerMouseListener this$1;

                public void actionPerformed(ActionEvent actionevent) {
                    try {
                        String as1[] = m_BayesNet.getValues(m_nCurrentNode);
                        int i3;
                        for (i3 = 0; i3 < as1.length && !as1[i3].equals(actionevent.getActionCommand()); i3++) { }
                        if (i3 == as1.length) {
                            i3 = -1;
                        }
                        if (i3 < as1.length) {
                            m_jStatusBar.setText((new StringBuilder()).append("Set evidence for ").append(m_BayesNet.getNodeName(m_nCurrentNode)).toString());
                            if (m_BayesNet.getEvidence(m_nCurrentNode) < 0 && i3 >= 0) {
                                m_BayesNet.setEvidence(m_nCurrentNode, i3);
                                m_marginCalculatorWithEvidence.setEvidence(m_nCurrentNode, i3);
                            } else {
                                m_BayesNet.setEvidence(m_nCurrentNode, i3);
                                SerializedObject serializedobject = new SerializedObject(m_marginCalculator);
                                m_marginCalculatorWithEvidence = (MarginCalculator)serializedobject.getObject();
                                for (int k3 = 0; k3 < m_BayesNet.getNrOfNodes(); k3++) {
                                    if (m_BayesNet.getEvidence(k3) >= 0) {
                                        m_marginCalculatorWithEvidence.setEvidence(k3, m_BayesNet.getEvidence(k3));
                                    }
                                }

                            }
                            for (int j3 = 0; j3 < m_BayesNet.getNrOfNodes(); j3++) {
                                m_BayesNet.setMargin(j3, m_marginCalculatorWithEvidence.getMargin(j3));
                            }

                        }
                    }
                    catch (Exception exception1) {
                        exception1.printStackTrace();
                    }
                    repaint();
                }

                 {
                    this$1 = GraphVisualizerMouseListener.this;
                    super();
                }
            };
            ActionListener actionlistener6 = new ActionListener() {

                final GraphVisualizerMouseListener this$1;

                public void actionPerformed(ActionEvent actionevent) {
                    if (actionevent.getActionCommand().equals("Rename")) {
                        renameNode(m_nCurrentNode);
                        return;
                    }
                    if (actionevent.getActionCommand().equals("Add parent")) {
                        addArcInto(m_nCurrentNode);
                        return;
                    }
                    if (actionevent.getActionCommand().equals("Add value")) {
                        addValue();
                        return;
                    }
                    if (actionevent.getActionCommand().equals("Delete node")) {
                        deleteNode(m_nCurrentNode);
                        return;
                    }
                    if (actionevent.getActionCommand().equals("Edit CPT")) {
                        editCPT(m_nCurrentNode);
                        return;
                    } else {
                        repaint();
                        return;
                    }
                }

                 {
                    this$1 = GraphVisualizerMouseListener.this;
                    super();
                }
            };
            try {
                JPopupMenu jpopupmenu = new JPopupMenu("Choose a value");
                JMenu jmenu = new JMenu("Set evidence");
                String as[] = m_BayesNet.getValues(m_nCurrentNode);
                for (int i = 0; i < as.length; i++) {
                    JMenuItem jmenuitem5 = new JMenuItem(as[i]);
                    jmenuitem5.addActionListener(actionlistener5);
                    jmenu.add(jmenuitem5);
                }

                jmenu.addSeparator();
                JMenuItem jmenuitem = new JMenuItem("Clear");
                jmenuitem.addActionListener(actionlistener5);
                jmenu.add(jmenuitem);
                jpopupmenu.add(jmenu);
                jmenu.setEnabled(m_bViewMargins);
                jpopupmenu.addSeparator();
                JMenuItem jmenuitem6 = new JMenuItem("Rename");
                jmenuitem6.addActionListener(actionlistener6);
                jpopupmenu.add(jmenuitem6);
                JMenuItem jmenuitem7 = new JMenuItem("Delete node");
                jmenuitem7.addActionListener(actionlistener6);
                jpopupmenu.add(jmenuitem7);
                JMenuItem jmenuitem8 = new JMenuItem("Edit CPT");
                jmenuitem8.addActionListener(actionlistener6);
                jpopupmenu.add(jmenuitem8);
                jpopupmenu.addSeparator();
                JMenu jmenu1 = new JMenu("Add parent");
                jpopupmenu.add(jmenu1);
                int j = m_BayesNet.getNrOfNodes();
                boolean aflag[] = new boolean[j];
                aflag[m_nCurrentNode] = true;
                for (int k = 0; k < j; k++) {
                    for (int j1 = 0; j1 < j; j1++) {
                        for (int l1 = 0; l1 < m_BayesNet.getNrOfParents(j1); l1++) {
                            if (aflag[m_BayesNet.getParent(j1, l1)]) {
                                aflag[j1] = true;
                            }
                        }

                    }

                }

                for (int l = 0; l < m_BayesNet.getNrOfParents(m_nCurrentNode); l++) {
                    aflag[m_BayesNet.getParent(m_nCurrentNode, l)] = true;
                }

                int i1 = 0;
                for (int k1 = 0; k1 < j; k1++) {
                    if (!aflag[k1]) {
                        JMenuItem jmenuitem1 = new JMenuItem(m_BayesNet.getNodeName(k1));
                        jmenuitem1.addActionListener(actionlistener2);
                        jmenu1.add(jmenuitem1);
                        i1++;
                    }
                }

                if (i1 == 0) {
                    jmenu1.setEnabled(false);
                }
                JMenu jmenu2 = new JMenu("Delete parent");
                jpopupmenu.add(jmenu2);
                if (m_BayesNet.getNrOfParents(m_nCurrentNode) == 0) {
                    jmenu2.setEnabled(false);
                }
                for (int i2 = 0; i2 < m_BayesNet.getNrOfParents(m_nCurrentNode); i2++) {
                    JMenuItem jmenuitem2 = new JMenuItem(m_BayesNet.getNodeName(m_BayesNet.getParent(m_nCurrentNode, i2)));
                    jmenuitem2.addActionListener(actionlistener3);
                    jmenu2.add(jmenuitem2);
                }

                JMenu jmenu3 = new JMenu("Delete child");
                jpopupmenu.add(jmenu3);
                FastVector fastvector = m_BayesNet.getChildren(m_nCurrentNode);
                if (fastvector.size() == 0) {
                    jmenu3.setEnabled(false);
                }
                for (int j2 = 0; j2 < fastvector.size(); j2++) {
                    JMenuItem jmenuitem3 = new JMenuItem(m_BayesNet.getNodeName(((Integer)fastvector.elementAt(j2)).intValue()));
                    jmenuitem3.addActionListener(actionlistener4);
                    jmenu3.add(jmenuitem3);
                }

                jpopupmenu.addSeparator();
                JMenuItem jmenuitem9 = new JMenuItem("Add value");
                jmenuitem9.addActionListener(actionlistener6);
                jpopupmenu.add(jmenuitem9);
                JMenu jmenu4 = new JMenu("Rename value");
                jpopupmenu.add(jmenu4);
                for (int k2 = 0; k2 < as.length; k2++) {
                    JMenuItem jmenuitem4 = new JMenuItem(as[k2]);
                    jmenuitem4.addActionListener(actionlistener);
                    jmenu4.add(jmenuitem4);
                }

                JMenu jmenu5 = new JMenu("Delete value");
                jpopupmenu.add(jmenu5);
                if (m_BayesNet.getCardinality(m_nCurrentNode) <= 2) {
                    jmenu5.setEnabled(false);
                }
                for (int l2 = 0; l2 < as.length; l2++) {
                    JMenuItem jmenuitem10 = new JMenuItem(as[l2]);
                    jmenuitem10.addActionListener(actionlistener1);
                    jmenu5.add(jmenuitem10);
                }

                jpopupmenu.setLocation(mouseevent.getX(), mouseevent.getY());
                jpopupmenu.show(m_GraphPanel, mouseevent.getX(), mouseevent.getY());
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        private GraphVisualizerMouseListener() {
            this$0 = GUI.this;
            super();
            m_nPosX = 0;
            m_nPosY = 0;
        }

    }

    private class GraphVisualizerTableModel extends AbstractTableModel {

        private static final long serialVersionUID = 0xbd872a01d3dddd3cL;
        final String m_sColumnNames[];
        final double m_fProbs[][];
        int m_iNode;
        final GUI this$0;

        public void randomize() {
            int i = m_fProbs[0].length;
            Random random = new Random();
            for (int j = 0; j < m_fProbs.length; j++) {
                for (int k = 0; k < i - 1; k++) {
                    m_fProbs[j][k] = random.nextDouble();
                }

                for (int l = 0; l < i - 1; l++) {
                    for (int i1 = l + 1; i1 < i - 1; i1++) {
                        if (m_fProbs[j][l] > m_fProbs[j][i1]) {
                            double d1 = m_fProbs[j][l];
                            m_fProbs[j][l] = m_fProbs[j][i1];
                            m_fProbs[j][i1] = d1;
                        }
                    }

                }

                double d = m_fProbs[j][0];
                for (int j1 = 1; j1 < i - 1; j1++) {
                    m_fProbs[j][j1] = m_fProbs[j][j1] - d;
                    d += m_fProbs[j][j1];
                }

                m_fProbs[j][i - 1] = 1.0D - d;
            }

        }

        public void setData() {
        }

        public int getColumnCount() {
            return m_sColumnNames.length;
        }

        public int getRowCount() {
            return m_fProbs.length;
        }

        public String getColumnName(int i) {
            return m_sColumnNames[i];
        }

        public Object getValueAt(int i, int j) {
            return new Double(m_fProbs[i][j]);
        }

        public void setValueAt(Object obj, int i, int j) {
            Double double1 = (Double)obj;
            if (double1.doubleValue() < 0.0D || double1.doubleValue() > 1.0D) {
                return;
            }
            m_fProbs[i][j] = double1.doubleValue();
            double d = 0.0D;
            for (int k = 0; k < m_fProbs[i].length; k++) {
                d += m_fProbs[i][k];
            }

            if (d > 1.0D) {
                for (int l = m_fProbs[i].length - 1; d > 1.0D; l--) {
                    if (l != j) {
                        if (m_fProbs[i][l] > d - 1.0D) {
                            m_fProbs[i][l] -= d - 1.0D;
                            d = 1.0D;
                        } else {
                            d -= m_fProbs[i][l];
                            m_fProbs[i][l] = 0.0D;
                        }
                    }
                }

            } else {
                for (int i1 = m_fProbs[i].length - 1; d < 1.0D; i1--) {
                    if (i1 != j) {
                        m_fProbs[i][i1] += 1.0D - d;
                        d = 1.0D;
                    }
                }

            }
            validate();
        }

        public Class getColumnClass(int i) {
            return getValueAt(0, i).getClass();
        }

        public boolean isCellEditable(int i, int j) {
            return true;
        }

        public GraphVisualizerTableModel(int i) {
            this$0 = GUI.this;
            super();
            m_iNode = i;
            double ad[][] = m_BayesNet.getDistribution(i);
            m_fProbs = new double[ad.length][ad[0].length];
            for (int j = 0; j < ad.length; j++) {
                for (int k = 0; k < ad[0].length; k++) {
                    m_fProbs[j][k] = ad[j][k];
                }

            }

            m_sColumnNames = m_BayesNet.getValues(i);
        }
    }

    private class GraphPanel extends PrintablePanel
        implements Printable {

        private static final long serialVersionUID = 0xce8e57e78a24d0ebL;
        static final int HIGHLIGHTED = 1;
        static final int NORMAL = 0;
        int m_nClique;
        final GUI this$0;

        public String getToolTipText(MouseEvent mouseevent) {
            int j;
            int i = j = 0;
            Rectangle rectangle = new Rectangle(0, 0, (int)((double)m_nPaddedNodeWidth * m_fScale), (int)((double)m_nNodeHeight * m_fScale));
            i += mouseevent.getX();
            j += mouseevent.getY();
            for (int k = 0; k < m_BayesNet.getNrOfNodes(); k++) {
                rectangle.x = (int)((double)m_BayesNet.getPositionX(k) * m_fScale);
                rectangle.y = (int)((double)m_BayesNet.getPositionY(k) * m_fScale);
                if (rectangle.contains(i, j)) {
                    return (new StringBuilder()).append(m_BayesNet.getNodeName(k)).append(" (right click to manipulate this node)").toString();
                }
            }

            return null;
        }

        public void paintComponent(Graphics g) {
            Graphics2D graphics2d = (Graphics2D)g;
            RenderingHints renderinghints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            renderinghints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
            graphics2d.setRenderingHints(renderinghints);
            graphics2d.scale(m_fScale, m_fScale);
            Rectangle rectangle = graphics2d.getClipBounds();
            graphics2d.clearRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
            if (m_bViewCliques) {
                m_nClique = 1;
                viewCliques(graphics2d, m_marginCalculator.m_root);
            }
            for (int i = 0; i < m_BayesNet.getNrOfNodes(); i++) {
                drawNode(graphics2d, i, 0);
            }

            if (!a_export.isExporting() && !a_print.isPrinting()) {
                m_Selection.draw(graphics2d);
            }
            if (m_nSelectedRect != null) {
                graphics2d.drawRect((int)((double)m_nSelectedRect.x / m_fScale), (int)((double)m_nSelectedRect.y / m_fScale), (int)((double)m_nSelectedRect.width / m_fScale), (int)((double)m_nSelectedRect.height / m_fScale));
            }
        }

        void viewCliques(Graphics g, MarginCalculator.JunctionTreeNode junctiontreenode) {
            int ai[] = junctiontreenode.m_nNodes;
            g.setColor(new Color(((m_nClique % 7) * 256) / 7, ((m_nClique % 2) * 256) / 2, ((m_nClique % 3) * 256) / 3));
            int i = m_nPaddedNodeWidth / 2 + m_nClique;
            int j = m_nNodeHeight / 2;
            int k = 0;
            int l = 0;
            String s = "";
            for (int i1 = 0; i1 < ai.length; i1++) {
                k += m_BayesNet.getPositionX(ai[i1]);
                l += m_BayesNet.getPositionY(ai[i1]);
                s = (new StringBuilder()).append(s).append(" ").append(ai[i1]).toString();
                for (int k1 = i1 + 1; k1 < ai.length; k1++) {
                    g.drawLine(m_BayesNet.getPositionX(ai[i1]) + i, m_BayesNet.getPositionY(ai[i1]) + j, m_BayesNet.getPositionX(ai[k1]) + i, m_BayesNet.getPositionY(ai[k1]) + j);
                }

            }

            m_nClique++;
            k /= ai.length;
            l /= ai.length;
            g.drawString((new StringBuilder()).append("Clique ").append(m_nClique).append("(").append(s).append(")").toString(), k, l);
            for (int j1 = 0; j1 < junctiontreenode.m_children.size(); j1++) {
                viewCliques(g, (MarginCalculator.JunctionTreeNode)junctiontreenode.m_children.elementAt(j1));
            }

        }

        protected void drawNode(Graphics g, int i, int j) {
            int k = m_BayesNet.getPositionX(i);
            int l = m_BayesNet.getPositionY(i);
            g.setColor(getBackground().darker().darker());
            FontMetrics fontmetrics = getFontMetrics(getFont());
            if (j == 1) {
                g.setXORMode(Color.green);
            }
            g.fillOval((k + m_nPaddedNodeWidth) - m_nNodeWidth - (m_nPaddedNodeWidth - m_nNodeWidth) / 2, l, m_nNodeWidth, m_nNodeHeight);
            g.setColor(Color.white);
            if (j == 1) {
                g.setXORMode(Color.red);
            }
            if (fontmetrics.stringWidth(m_BayesNet.getNodeName(i)) <= m_nNodeWidth) {
                g.drawString(m_BayesNet.getNodeName(i), (k + m_nPaddedNodeWidth / 2) - fontmetrics.stringWidth(m_BayesNet.getNodeName(i)) / 2, (l + m_nNodeHeight / 2 + fontmetrics.getHeight() / 2) - 2);
            } else
            if (fontmetrics.stringWidth((new StringBuilder()).append("").append(i).toString()) <= m_nNodeWidth) {
                g.drawString((new StringBuilder()).append("").append(i).toString(), (k + m_nPaddedNodeWidth / 2) - fontmetrics.stringWidth((new StringBuilder()).append("").append(i).toString()) / 2, (l + m_nNodeHeight / 2 + fontmetrics.getHeight() / 2) - 2);
            }
            if (j == 1) {
                g.setXORMode(Color.green);
            }
            if (m_bViewMargins) {
                if (m_BayesNet.getEvidence(i) < 0) {
                    g.setColor(new Color(0, 128, 0));
                } else {
                    g.setColor(new Color(128, 0, 0));
                }
                double ad[] = m_BayesNet.getMargin(i);
                for (int j1 = 0; j1 < ad.length; j1++) {
                    String s = (new StringBuilder()).append(ad[j1]).append("").toString();
                    if (s.charAt(0) == '0') {
                        s = s.substring(1);
                    }
                    if (s.length() > 5) {
                        s = s.substring(1, 5);
                    }
                    g.fillRect(k + m_nPaddedNodeWidth, l + j1 * 10 + 2, (int)(ad[j1] * 100D), 8);
                    g.drawString((new StringBuilder()).append(m_BayesNet.getNodeValue(i, j1)).append(" ").append(s).toString(), k + m_nPaddedNodeWidth + (int)(ad[j1] * 100D), l + j1 * 10 + 10);
                }

            }
            if (m_bViewCliques) {
                return;
            }
            g.setColor(Color.black);
            for (int i1 = 0; i1 < m_BayesNet.getNrOfParents(i); i1++) {
                int k1 = m_BayesNet.getParent(i, i1);
                int i2 = k + m_nPaddedNodeWidth / 2;
                int k2 = l + m_nNodeHeight;
                int i3 = m_BayesNet.getPositionX(k1);
                int k3 = m_BayesNet.getPositionY(k1);
                int i4 = i3 + m_nPaddedNodeWidth / 2;
                int k4 = k3;
                double d = Math.atan2(((double)(i4 - i2) + 0.0D) * (double)m_nNodeHeight, ((double)(k4 - k2) + 0.0D) * (double)m_nNodeWidth);
                i2 = (int)((double)(k + m_nPaddedNodeWidth / 2) + (Math.sin(d) * (double)m_nNodeWidth) / 2D);
                k2 = (int)((double)(l + m_nNodeHeight / 2) + (Math.cos(d) * (double)m_nNodeHeight) / 2D);
                i4 = (int)((double)(i3 + m_nPaddedNodeWidth / 2) - (Math.sin(d) * (double)m_nNodeWidth) / 2D);
                k4 = (int)((double)(k3 + m_nNodeHeight / 2) - (Math.cos(d) * (double)m_nNodeHeight) / 2D);
                drawArrow(g, i4, k4, i2, k2);
            }

            if (j == 1) {
                FastVector fastvector = m_BayesNet.getChildren(i);
                for (int l1 = 0; l1 < fastvector.size(); l1++) {
                    int j2 = ((Integer)fastvector.elementAt(l1)).intValue();
                    int l2 = k + m_nPaddedNodeWidth / 2;
                    int j3 = l;
                    int l3 = m_BayesNet.getPositionX(j2);
                    int j4 = m_BayesNet.getPositionY(j2);
                    int l4 = l3 + m_nPaddedNodeWidth / 2;
                    int i5 = j4 + m_nNodeHeight;
                    double d1 = Math.atan2(((double)(l4 - l2) + 0.0D) * (double)m_nNodeHeight, ((double)(i5 - j3) + 0.0D) * (double)m_nNodeWidth);
                    l2 = (int)((double)(k + m_nPaddedNodeWidth / 2) + (Math.sin(d1) * (double)m_nNodeWidth) / 2D);
                    j3 = (int)((double)(l + m_nNodeHeight / 2) + (Math.cos(d1) * (double)m_nNodeHeight) / 2D);
                    l4 = (int)((double)(l3 + m_nPaddedNodeWidth / 2) - (Math.sin(d1) * (double)m_nNodeWidth) / 2D);
                    i5 = (int)((double)(j4 + m_nNodeHeight / 2) - (Math.cos(d1) * (double)m_nNodeHeight) / 2D);
                    drawArrow(g, l2, j3, l4, i5);
                }

            }
        }

        protected void drawArrow(Graphics g, int i, int j, int k, int l) {
            g.drawLine(i, j, k, l);
            if (i == k) {
                if (j < l) {
                    g.drawLine(k, l, k + 4, l - 8);
                    g.drawLine(k, l, k - 4, l - 8);
                } else {
                    g.drawLine(k, l, k + 4, l + 8);
                    g.drawLine(k, l, k - 4, l + 8);
                }
            } else {
                double d = 0.0D;
                double d1 = 0.0D;
                double d2 = 0.0D;
                int i1 = 0;
                int j1 = 0;
                double d3;
                if (k < i) {
                    d1 = i - k;
                    d = Math.sqrt((k - i) * (k - i) + (l - j) * (l - j));
                    d3 = Math.acos(d1 / d);
                } else {
                    d1 = i - k;
                    d = Math.sqrt((k - i) * (k - i) + (l - j) * (l - j));
                    d3 = Math.acos(d1 / d);
                }
                double d4 = 0.52359877559829882D;
                d = 8D;
                d1 = Math.cos(d3 - d4) * d;
                d2 = Math.sin(d3 - d4) * d;
                i1 = (int)((double)k + d1);
                if (j < l) {
                    j1 = (int)((double)l - d2);
                } else {
                    j1 = (int)((double)l + d2);
                }
                g.drawLine(k, l, i1, j1);
                d1 = Math.cos(d3 + d4) * d;
                d2 = Math.sin(d3 + d4) * d;
                i1 = (int)((double)k + d1);
                if (j < l) {
                    j1 = (int)((double)l - d2);
                } else {
                    j1 = (int)((double)l + d2);
                }
                g.drawLine(k, l, i1, j1);
            }
        }

        public void highLight(int i) {
            Graphics2D graphics2d = (Graphics2D)getGraphics();
            RenderingHints renderinghints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            renderinghints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
            graphics2d.setRenderingHints(renderinghints);
            graphics2d.setPaintMode();
            graphics2d.scale(m_fScale, m_fScale);
            drawNode(graphics2d, i, 1);
        }

        public int print(Graphics g, PageFormat pageformat, int i) {
            if (i > 0) {
                return 1;
            }
            Graphics2D graphics2d = (Graphics2D)g;
            graphics2d.translate(pageformat.getImageableX(), pageformat.getImageableY());
            double d = pageformat.getImageableHeight();
            double d1 = pageformat.getImageableWidth();
            int j = 1;
            int k = 1;
            for (int l = 0; l < m_BayesNet.getNrOfNodes(); l++) {
                if (j < m_BayesNet.getPositionX(l)) {
                    j = m_BayesNet.getPositionX(l);
                }
                if (k < m_BayesNet.getPositionY(l)) {
                    k = m_BayesNet.getPositionY(l);
                }
            }

            double d2 = m_fScale;
            j += m_nPaddedNodeWidth + 100;
            if (d1 / (double)j < d / (double)k) {
                m_fScale = d1 / (double)j;
            } else {
                m_fScale = d / (double)k;
            }
            paint(graphics2d);
            m_fScale = d2;
            return 0;
        }

        public GraphPanel() {
            this$0 = GUI.this;
            super();
            m_nClique = 1;
            addMouseListener(new GraphVisualizerMouseListener());
            addMouseMotionListener(new GraphVisualizerMouseMotionListener());
            setToolTipText("");
        }
    }

    class ActionLayout extends MyAction {

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
                m_jTfNodeWidth.setHorizontalAlignment(0);
                m_jTfNodeWidth.setText((new StringBuilder()).append("").append(m_nNodeWidth).toString());
                m_jTfNodeHeight.setHorizontalAlignment(0);
                m_jTfNodeHeight.setText((new StringBuilder()).append("").append(m_nNodeHeight).toString());
                jLbNodeWidth.setEnabled(false);
                m_jTfNodeWidth.setEnabled(false);
                jLbNodeHeight.setEnabled(false);
                m_jTfNodeHeight.setEnabled(false);
                jCbCustomNodeSize.addActionListener(new ActionListener() {

                    final JLabel val$jLbNodeWidth;
                    final JLabel val$jLbNodeHeight;
                    final ActionLayout this$1;

                    public void actionPerformed(ActionEvent actionevent1) {
                        if (((JCheckBox)actionevent1.getSource()).isSelected()) {
                            jLbNodeWidth.setEnabled(true);
                            m_jTfNodeWidth.setEnabled(true);
                            jLbNodeHeight.setEnabled(true);
                            m_jTfNodeHeight.setEnabled(true);
                        } else {
                            jLbNodeWidth.setEnabled(false);
                            m_jTfNodeWidth.setEnabled(false);
                            jLbNodeHeight.setEnabled(false);
                            m_jTfNodeHeight.setEnabled(false);
                            setAppropriateSize();
                            setAppropriateNodeSize();
                        }
                    }

                 {
                    this$1 = ActionLayout.this;
                    jLbNodeWidth = jlabel;
                    jLbNodeHeight = jlabel1;
                    super();
                }
                });
                JButton jbutton = new JButton("Layout Graph");
                jbutton.setMnemonic('L');
                jbutton.addActionListener(new ActionListener() {

                    final JCheckBox val$jCbCustomNodeSize;
                    final ActionLayout this$1;

                    public void actionPerformed(ActionEvent actionevent1) {
                        if (jCbCustomNodeSize.isSelected()) {
                            int i;
                            try {
                                i = Integer.parseInt(m_jTfNodeWidth.getText());
                            }
                            catch (NumberFormatException numberformatexception) {
                                JOptionPane.showMessageDialog(getParent(), "Invalid integer entered for node width.", "Error", 0);
                                i = m_nNodeWidth;
                                m_jTfNodeWidth.setText((new StringBuilder()).append("").append(m_nNodeWidth).toString());
                            }
                            int j;
                            try {
                                j = Integer.parseInt(m_jTfNodeHeight.getText());
                            }
                            catch (NumberFormatException numberformatexception1) {
                                JOptionPane.showMessageDialog(getParent(), "Invalid integer entered for node height.", "Error", 0);
                                j = m_nNodeHeight;
                                m_jTfNodeWidth.setText((new StringBuilder()).append("").append(m_nNodeHeight).toString());
                            }
                            if (i != m_nNodeWidth || j != m_nNodeHeight) {
                                m_nNodeWidth = i;
                                m_nPaddedNodeWidth = m_nNodeWidth + 10;
                                m_nNodeHeight = j;
                            }
                        }
                        dlg.setVisible(false);
                        updateStatus();
                        layoutGraph();
                        m_jStatusBar.setText("Laying out Bayes net");
                    }

                 {
                    this$1 = ActionLayout.this;
                    jCbCustomNodeSize = jcheckbox;
                    super();
                }
                });
                JButton jbutton1 = new JButton("Cancel");
                jbutton1.setMnemonic('C');
                jbutton1.addActionListener(new ActionListener() {

                    final ActionLayout this$1;

                    public void actionPerformed(ActionEvent actionevent1) {
                        dlg.setVisible(false);
                    }

                 {
                    this$1 = ActionLayout.this;
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
                container.add(m_jTfNodeWidth, gridbagconstraints);
                gridbagconstraints.gridwidth = -1;
                container.add(jLbNodeHeight, gridbagconstraints);
                gridbagconstraints.gridwidth = 0;
                container.add(m_jTfNodeHeight, gridbagconstraints);
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

        public ActionLayout() {
            this$0 = GUI.this;
            super("Layout", "Layout Graph", "layout", "ctrl L");
            dlg = null;
        }
    }

    class ActionZoomOut extends MyAction {

        private static final long serialVersionUID = 0xffff468fe6e64071L;
        final GUI this$0;

        public void actionPerformed(ActionEvent actionevent) {
            int i = 0;
            int j = (int)(m_fScale * 100D);
            if (j < 300) {
                i = (int)Math.ceil((double)j / 25D);
            } else
            if (j < 700) {
                i = 6 + (int)Math.ceil((double)j / 50D);
            } else {
                i = 13 + (int)Math.ceil((double)j / 100D);
            }
            if (j <= 10) {
                setEnabled(false);
            } else
            if (j < 999) {
                if (i <= 1) {
                    setEnabled(false);
                }
                m_jTfZoom.setText((new StringBuilder()).append(m_nZoomPercents[i - 1]).append("%").toString());
                m_fScale = (double)m_nZoomPercents[i - 1] / 100D;
            } else {
                if (!a_zoomin.isEnabled()) {
                    a_zoomin.setEnabled(true);
                }
                m_jTfZoom.setText((new StringBuilder()).append(m_nZoomPercents[22]).append("%").toString());
                m_fScale = (double)m_nZoomPercents[22] / 100D;
            }
            setAppropriateSize();
            m_GraphPanel.repaint();
            m_GraphPanel.invalidate();
            m_jScrollPane.revalidate();
            m_jStatusBar.setText("Zooming out");
        }

        public ActionZoomOut() {
            this$0 = GUI.this;
            super("Zoom out", "Zoom out", "zoomout", "-");
        }
    }

    class ActionZoomIn extends MyAction {

        private static final long serialVersionUID = 0xfff8c19f04fe8465L;
        final GUI this$0;

        public void actionPerformed(ActionEvent actionevent) {
            int i = 0;
            int j = (int)(m_fScale * 100D);
            if (j < 300) {
                i = j / 25;
            } else
            if (j < 700) {
                i = 6 + j / 50;
            } else {
                i = 13 + j / 100;
            }
            if (j >= 999) {
                setEnabled(false);
                return;
            }
            if (j >= 10) {
                if (i >= 22) {
                    setEnabled(false);
                }
                if (j == 10 && !a_zoomout.isEnabled()) {
                    a_zoomout.setEnabled(true);
                }
                m_jTfZoom.setText((new StringBuilder()).append(m_nZoomPercents[i + 1]).append("%").toString());
                m_fScale = (double)m_nZoomPercents[i + 1] / 100D;
            } else {
                if (!a_zoomout.isEnabled()) {
                    a_zoomout.setEnabled(true);
                }
                m_jTfZoom.setText((new StringBuilder()).append(m_nZoomPercents[0]).append("%").toString());
                m_fScale = (double)m_nZoomPercents[0] / 100D;
            }
            setAppropriateSize();
            m_GraphPanel.repaint();
            m_GraphPanel.invalidate();
            m_jScrollPane.revalidate();
            m_jStatusBar.setText("Zooming in");
        }

        public ActionZoomIn() {
            this$0 = GUI.this;
            super("Zoom in", "Zoom in", "zoomin", "+");
        }
    }

    class ActionAbout extends MyAction {

        private static final long serialVersionUID = 0xffffed74ca4a39a7L;
        final GUI this$0;

        public void actionPerformed(ActionEvent actionevent) {
            JOptionPane.showMessageDialog(null, "Bayesian Network Workbench\nPart of Weka\n2007", "About Message", -1);
        }

        public ActionAbout() {
            this$0 = GUI.this;
            super("About", "Help about", "about", "");
        }
    }

    class ActionHelp extends MyAction {

        private static final long serialVersionUID = 0xffffed74ca4a39a6L;
        final GUI this$0;

        public void actionPerformed(ActionEvent actionevent) {
            JOptionPane.showMessageDialog(null, "See Weka Homepage\nhttp://www.cs.waikato.ac.nz/ml", "Help Message", -1);
        }

        public ActionHelp() {
            this$0 = GUI.this;
            super("Help", "Bayesian Network Workbench Help", "help", "");
        }
    }

    class ActionQuit extends ActionSave {

        private static final long serialVersionUID = 0xfff8c19f04fe8465L;
        final GUI this$0;

        public void actionPerformed(ActionEvent actionevent) {
            if (m_BayesNet.isChanged()) {
                int i = JOptionPane.showConfirmDialog(null, "Network changed. Do you want to save it?", "Save before closing?", 1);
                if (i == 2) {
                    return;
                }
                if (i == 0 && !saveAs()) {
                    return;
                }
            }
            System.exit(0);
        }

        public ActionQuit() {
            this$0 = GUI.this;
            super("Exit", "Exit Program", "exit", "");
        }
    }

    class ActionPrint extends ActionSave {

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

        public ActionPrint() {
            this$0 = GUI.this;
            super("Print", "Print Graph", "print", "ctrl P");
            m_bIsPrinting = false;
        }
    }

    class ActionSaveAs extends ActionSave {

        private static final long serialVersionUID = 0xffffed74ca4a39a6L;
        final GUI this$0;

        public void actionPerformed(ActionEvent actionevent) {
            saveAs();
        }

        public ActionSaveAs() {
            this$0 = GUI.this;
            super("Save As", "Save Graph As", "saveas", "");
        }
    }

    class ActionSave extends MyAction {

        private static final long serialVersionUID = 0xffb7903631f12becL;
        ExtensionFileFilter ef1;
        final GUI this$0;

        public void actionPerformed(ActionEvent actionevent) {
            if (!m_sFileName.equals("")) {
                saveFile(m_sFileName);
                m_BayesNet.isSaved();
                m_jStatusBar.setText((new StringBuilder()).append("Saved as ").append(m_sFileName).toString());
            } else
            if (saveAs()) {
                m_BayesNet.isSaved();
                m_jStatusBar.setText((new StringBuilder()).append("Saved as ").append(m_sFileName).toString());
            }
        }

        boolean saveAs() {
            JFileChooser jfilechooser = new JFileChooser(System.getProperty("user.dir"));
            jfilechooser.addChoosableFileFilter(ef1);
            jfilechooser.setDialogTitle("Save Graph As");
            if (!m_sFileName.equals("")) {
                jfilechooser.setSelectedFile(new File(m_sFileName));
            }
            int i = jfilechooser.showSaveDialog(GUI.this);
            if (i == 0) {
                String s = jfilechooser.getSelectedFile().toString();
                if (!s.endsWith(".xml")) {
                    s = s.concat(".xml");
                }
                saveFile(s);
                return true;
            } else {
                return false;
            }
        }

        protected void saveFile(String s) {
            try {
                FileWriter filewriter = new FileWriter(s);
                filewriter.write(m_BayesNet.toXMLBIF03());
                filewriter.close();
                m_sFileName = s;
                m_jStatusBar.setText((new StringBuilder()).append("Saved as ").append(m_sFileName).toString());
            }
            catch (IOException ioexception) {
                ioexception.printStackTrace();
            }
        }

        public ActionSave() {
            this$0 = GUI.this;
            super("Save", "Save Graph", "save", "ctrl S");
            ef1 = new ExtensionFileFilter(".xml", "XML BIF files");
        }

        public ActionSave(String s, String s1, String s2, String s3) {
            this$0 = GUI.this;
            super(s, s1, s2, s3);
            ef1 = new ExtensionFileFilter(".xml", "XML BIF files");
        }
    }

    class ActionViewToolbar extends MyAction {

        private static final long serialVersionUID = 0xffffed74ca4af13eL;
        final GUI this$0;

        public void actionPerformed(ActionEvent actionevent) {
            m_jTbTools.setVisible(!m_jTbTools.isVisible());
        }

        public ActionViewToolbar() {
            this$0 = GUI.this;
            super("View toolbar", "View toolbar", "toolbar", "");
        }
    }

    class ActionViewStatusbar extends MyAction {

        private static final long serialVersionUID = 0xffffed74bd2e023eL;
        final GUI this$0;

        public void actionPerformed(ActionEvent actionevent) {
            m_jStatusBar.setVisible(!m_jStatusBar.isVisible());
        }

        public ActionViewStatusbar() {
            this$0 = GUI.this;
            super("View statusbar", "View statusbar", "statusbar", "");
        }
    }

    class ActionLoad extends MyAction {

        private static final long serialVersionUID = 0xfff8c19f04fe8465L;
        final GUI this$0;

        public void actionPerformed(ActionEvent actionevent) {
            JFileChooser jfilechooser = new JFileChooser(System.getProperty("user.dir"));
            ExtensionFileFilter extensionfilefilter = new ExtensionFileFilter(".arff", "ARFF files");
            ExtensionFileFilter extensionfilefilter1 = new ExtensionFileFilter(".xml", "XML BIF files");
            jfilechooser.addChoosableFileFilter(extensionfilefilter);
            jfilechooser.addChoosableFileFilter(extensionfilefilter1);
            jfilechooser.setDialogTitle("Load Graph");
            int i = jfilechooser.showOpenDialog(GUI.this);
            if (i == 0) {
                String s = jfilechooser.getSelectedFile().toString();
                if (s.endsWith(extensionfilefilter.getExtensions()[0])) {
                    initFromArffFile(s);
                } else {
                    try {
                        readBIFFromFile(s);
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
                m_jStatusBar.setText((new StringBuilder()).append("Loaded ").append(s).toString());
                updateStatus();
            }
        }

        public ActionLoad() {
            this$0 = GUI.this;
            super("Load", "Load Graph", "open", "ctrl O");
        }
    }

    class ActionNew extends MyAction {

        private static final long serialVersionUID = 0xfff8c19f04fe8465L;
        final GUI this$0;

        public void actionPerformed(ActionEvent actionevent) {
            m_sFileName = "";
            m_BayesNet = new EditableBayesNet(true);
            updateStatus();
            layoutGraph();
            a_datagenerator.setEnabled(false);
            m_BayesNet.clearUndoStack();
            m_jStatusBar.setText("New Network");
            m_Selection = new Selection();
            repaint();
        }

        public ActionNew() {
            this$0 = GUI.this;
            super("New", "New Network", "new", "");
        }
    }

    class ActionDeleteArc extends MyAction {

        private static final long serialVersionUID = 0xfff8c19e522e2661L;
        final GUI this$0;

        public void actionPerformed(ActionEvent actionevent) {
            int i = 0;
            for (int j = 0; j < m_BayesNet.getNrOfNodes(); j++) {
                i += m_BayesNet.getNrOfParents(j);
            }

            String as[] = new String[i];
            int k = 0;
            for (int l = 0; l < m_BayesNet.getNrOfNodes(); l++) {
                for (int i1 = 0; i1 < m_BayesNet.getNrOfParents(l); i1++) {
                    int j1 = m_BayesNet.getParent(l, i1);
                    String s = m_BayesNet.getNodeName(j1);
                    s = (new StringBuilder()).append(s).append(" -> ").toString();
                    s = (new StringBuilder()).append(s).append(m_BayesNet.getNodeName(l)).toString();
                    as[k++] = s;
                }

            }

            deleteArc(as);
        }

        public ActionDeleteArc() {
            this$0 = GUI.this;
            super("Delete Arc", "Delete Arc", "delarc", "");
        }
    }

    class ActionAddArc extends MyAction {

        private static final long serialVersionUID = 0xfff8c19e8dc8f061L;
        final GUI this$0;

        public void actionPerformed(ActionEvent actionevent) {
            String s;
            String as[] = new String[m_BayesNet.getNrOfNodes()];
            for (int i = 0; i < as.length; i++) {
                as[i] = m_BayesNet.getNodeName(i);
            }

            s = (String)JOptionPane.showInputDialog(null, "Select child node", "Nodes", 0, null, as, as[0]);
            if (s == null || s.equals("")) {
                return;
            }
            try {
                int j = m_BayesNet.getNode(s);
                addArcInto(j);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
            return;
        }

        public ActionAddArc() {
            this$0 = GUI.this;
            super("Add Arc", "Add Arc", "addarc", "");
        }
    }

    class ActionSpaceVertical extends MyAction {

        private static final long serialVersionUID = 0xfffd05426c5fc661L;
        final GUI this$0;

        public void actionPerformed(ActionEvent actionevent) {
            m_BayesNet.spaceVertical(m_Selection.getSelected());
            m_jStatusBar.setText(m_BayesNet.lastActionMsg());
            a_undo.setEnabled(true);
            a_redo.setEnabled(false);
            repaint();
        }

        public ActionSpaceVertical() {
            this$0 = GUI.this;
            super("Space Vertical", "Space Vertical", "spacevertical", "");
        }
    }

    class ActionSpaceHorizontal extends MyAction {

        private static final long serialVersionUID = 0xffdd66c1b1df8661L;
        final GUI this$0;

        public void actionPerformed(ActionEvent actionevent) {
            m_BayesNet.spaceHorizontal(m_Selection.getSelected());
            m_jStatusBar.setText(m_BayesNet.lastActionMsg());
            a_undo.setEnabled(true);
            a_redo.setEnabled(false);
            repaint();
        }

        public ActionSpaceHorizontal() {
            this$0 = GUI.this;
            super("Space Horizontal", "Space Horizontal", "spacehorizontal", "");
        }
    }

    class ActionCenterVertical extends MyAction {

        private static final long serialVersionUID = 0xffe14f3367204661L;
        final GUI this$0;

        public void actionPerformed(ActionEvent actionevent) {
            m_BayesNet.centerVertical(m_Selection.getSelected());
            m_jStatusBar.setText(m_BayesNet.lastActionMsg());
            a_undo.setEnabled(true);
            a_redo.setEnabled(false);
            repaint();
        }

        public ActionCenterVertical() {
            this$0 = GUI.this;
            super("Center Vertical", "Center Vertical", "centervertical", "");
        }
    }

    class ActionCenterHorizontal extends MyAction {

        private static final long serialVersionUID = 0xffe537a51c610661L;
        final GUI this$0;

        public void actionPerformed(ActionEvent actionevent) {
            m_BayesNet.centerHorizontal(m_Selection.getSelected());
            m_jStatusBar.setText(m_BayesNet.lastActionMsg());
            a_undo.setEnabled(true);
            a_redo.setEnabled(false);
            repaint();
        }

        public ActionCenterHorizontal() {
            this$0 = GUI.this;
            super("Center Horizontal", "Center Horizontal", "centerhorizontal", "");
        }
    }

    class ActionAlignBottom extends MyAction {

        private static final long serialVersionUID = 0xffe92016d1a1c661L;
        final GUI this$0;

        public void actionPerformed(ActionEvent actionevent) {
            m_BayesNet.alignBottom(m_Selection.getSelected());
            m_jStatusBar.setText(m_BayesNet.lastActionMsg());
            a_undo.setEnabled(true);
            a_redo.setEnabled(false);
            repaint();
        }

        public ActionAlignBottom() {
            this$0 = GUI.this;
            super("Align Bottom", "Align Bottom", "alignbottom", "");
        }
    }

    class ActionAlignTop extends MyAction {

        private static final long serialVersionUID = 0xffed088886e28661L;
        final GUI this$0;

        public void actionPerformed(ActionEvent actionevent) {
            m_BayesNet.alignTop(m_Selection.getSelected());
            m_jStatusBar.setText(m_BayesNet.lastActionMsg());
            a_undo.setEnabled(true);
            a_redo.setEnabled(false);
            repaint();
        }

        public ActionAlignTop() {
            this$0 = GUI.this;
            super("Align Top", "Align Top", "aligntop", "");
        }
    }

    class ActionAlignRight extends MyAction {

        private static final long serialVersionUID = 0xfff0f0fa3c234661L;
        final GUI this$0;

        public void actionPerformed(ActionEvent actionevent) {
            m_BayesNet.alignRight(m_Selection.getSelected());
            m_jStatusBar.setText(m_BayesNet.lastActionMsg());
            a_undo.setEnabled(true);
            a_redo.setEnabled(false);
            repaint();
        }

        public ActionAlignRight() {
            this$0 = GUI.this;
            super("Align Right", "Align Right", "alignright", "");
        }
    }

    class ActionAlignLeft extends MyAction {

        private static final long serialVersionUID = 0xfff4d96bf1640661L;
        final GUI this$0;

        public void actionPerformed(ActionEvent actionevent) {
            m_BayesNet.alignLeft(m_Selection.getSelected());
            m_jStatusBar.setText(m_BayesNet.lastActionMsg());
            a_undo.setEnabled(true);
            a_redo.setEnabled(false);
            repaint();
        }

        public ActionAlignLeft() {
            this$0 = GUI.this;
            super("Align Left", "Align Left", "alignleft", "");
        }
    }

    class ActionExport extends MyAction {

        boolean m_bIsExporting;
        private static final long serialVersionUID = 0xfff53e6024f5f661L;
        final GUI this$0;

        public void actionPerformed(ActionEvent actionevent) {
            m_bIsExporting = true;
            m_GraphPanel.saveComponent();
            m_bIsExporting = false;
            repaint();
        }

        public boolean isExporting() {
            return m_bIsExporting;
        }

        public ActionExport() {
            this$0 = GUI.this;
            super("Export", "Export to graphics file", "export", "");
            m_bIsExporting = false;
        }
    }

    class ActionSelectAll extends MyAction {

        private static final long serialVersionUID = 0xfff8c1dda6a4c661L;
        final GUI this$0;

        public void actionPerformed(ActionEvent actionevent) {
            m_Selection.selectAll();
            repaint();
        }

        public ActionSelectAll() {
            this$0 = GUI.this;
            super("Select All", "Select All Nodes", "selectall", "ctrl A");
        }
    }

    class ActionPasteNode extends MyAction {

        private static final long serialVersionUID = 0xfff8c1c8b239c261L;
        final GUI this$0;

        public void actionPerformed(ActionEvent actionevent) {
            try {
                m_BayesNet.paste(m_clipboard.getText());
                updateStatus();
                m_jStatusBar.setText(m_BayesNet.lastActionMsg());
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        public boolean isEnabled() {
            return m_clipboard.hasText();
        }

        public ActionPasteNode() {
            this$0 = GUI.this;
            super("Paste", "Paste Nodes", "paste", "ctrl V");
        }
    }

    class ActionCutNode extends ActionCopyNode {

        private static final long serialVersionUID = 0xfff8c1b3bdcebe61L;
        final GUI this$0;

        public void actionPerformed(ActionEvent actionevent) {
            copy();
            m_BayesNet.deleteSelection(m_Selection.getSelected());
            m_jStatusBar.setText(m_BayesNet.lastActionMsg());
            m_Selection.clear();
            a_undo.setEnabled(true);
            a_redo.setEnabled(false);
            repaint();
        }

        public ActionCutNode() {
            this$0 = GUI.this;
            super("Cut", "Cut Nodes", "cut", "ctrl X");
        }
    }

    class ActionCopyNode extends MyAction {

        private static final long serialVersionUID = 0xfff8c1c8b239c261L;
        final GUI this$0;

        public void actionPerformed(ActionEvent actionevent) {
            copy();
        }

        public void copy() {
            String s = m_BayesNet.toXMLBIF03(m_Selection.getSelected());
            m_clipboard.setText(s);
        }

        public ActionCopyNode() {
            this$0 = GUI.this;
            super("Copy", "Copy Nodes", "copy", "ctrl C");
        }

        public ActionCopyNode(String s, String s1, String s2, String s3) {
            this$0 = GUI.this;
            super(s, s1, s2, s3);
        }
    }

    class ActionDeleteNode extends MyAction {

        private static final long serialVersionUID = 0xfff8c19ec963ba61L;
        final GUI this$0;

        public void actionPerformed(ActionEvent actionevent) {
            if (m_Selection.getSelected().size() > 0) {
                m_BayesNet.deleteSelection(m_Selection.getSelected());
                m_jStatusBar.setText(m_BayesNet.lastActionMsg());
                m_Selection.clear();
                updateStatus();
                repaint();
            } else {
                String as[] = new String[m_BayesNet.getNrOfNodes()];
                for (int i = 0; i < as.length; i++) {
                    as[i] = m_BayesNet.getNodeName(i);
                }

                String s = (String)JOptionPane.showInputDialog(null, "Select node to delete", "Nodes", 0, null, as, as[0]);
                if (s != null && !s.equals("")) {
                    int j = m_BayesNet.getNode2(s);
                    deleteNode(j);
                }
            }
        }

        public ActionDeleteNode() {
            this$0 = GUI.this;
            super("Delete Node", "Delete Node", "delnode", "DELETE");
        }
    }

    class ActionAddNode extends MyAction {

        private static final long serialVersionUID = 0xfff8c19f40994e61L;
        JDialog dlg;
        JTextField jTfName;
        JTextField jTfCard;
        int m_X;
        int m_Y;
        final GUI this$0;

        public void addNode(int i, int j) {
            m_X = i;
            m_Y = j;
            addNode();
        }

        void addNode() {
            if (dlg == null) {
                dlg = new JDialog();
                dlg.setTitle("Add node");
                JLabel jlabel = new JLabel("Name");
                jTfName.setHorizontalAlignment(0);
                JLabel jlabel1 = new JLabel("Cardinality");
                jTfCard.setHorizontalAlignment(0);
                jTfCard.setText("2");
                JButton jbutton = new JButton("Cancel");
                jbutton.setMnemonic('C');
                jbutton.addActionListener(new ActionListener() {

                    final ActionAddNode this$1;

                    public void actionPerformed(ActionEvent actionevent) {
                        dlg.setVisible(false);
                    }

                 {
                    this$1 = ActionAddNode.this;
                    super();
                }
                });
                JButton jbutton1 = new JButton("Ok");
                jbutton1.setMnemonic('O');
                jbutton1.addActionListener(new ActionListener() {

                    final ActionAddNode this$1;

                    public void actionPerformed(ActionEvent actionevent) {
                        String s = jTfName.getText();
                        if (s.length() <= 0) {
                            JOptionPane.showMessageDialog(null, "Name should have at least one character");
                            return;
                        }
                        int i = (new Integer(jTfCard.getText())).intValue();
                        if (i <= 1) {
                            JOptionPane.showMessageDialog(null, "Cardinality should be larger than 1");
                            return;
                        }
                        try {
                            if (m_X < 0x7fffffff) {
                                m_BayesNet.addNode(s, i, m_X, m_Y);
                            } else {
                                m_BayesNet.addNode(s, i);
                            }
                            m_jStatusBar.setText(m_BayesNet.lastActionMsg());
                            a_undo.setEnabled(true);
                            a_redo.setEnabled(false);
                        }
                        catch (Exception exception) {
                            exception.printStackTrace();
                        }
                        repaint();
                        dlg.setVisible(false);
                    }

                 {
                    this$1 = ActionAddNode.this;
                    super();
                }
                });
                dlg.setLayout(new GridLayout(3, 2, 10, 10));
                dlg.add(jlabel);
                dlg.add(jTfName);
                dlg.add(jlabel1);
                dlg.add(jTfCard);
                dlg.add(jbutton1);
                dlg.add(jbutton);
                dlg.setSize(dlg.getPreferredSize());
            }
            jTfName.setText((new StringBuilder()).append("Node").append(m_BayesNet.getNrOfNodes() + 1).toString());
            dlg.setVisible(true);
        }

        public void actionPerformed(ActionEvent actionevent) {
            m_X = 0x7fffffff;
            addNode();
        }

        public ActionAddNode() {
            this$0 = GUI.this;
            super("Add Node", "Add Node", "addnode", "");
            dlg = null;
            jTfName = new JTextField(20);
            jTfCard = new JTextField(3);
            m_X = 0x7fffffff;
        }
    }

    class ActionRedo extends MyAction {

        private static final long serialVersionUID = 0xfff1a6a1f70c4e61L;
        final GUI this$0;

        public boolean isEnabled() {
            return m_BayesNet.canRedo();
        }

        public void actionPerformed(ActionEvent actionevent) {
            String s = m_BayesNet.redo();
            m_jStatusBar.setText((new StringBuilder()).append("Redo action performed: ").append(s).toString());
            m_Selection.clear();
            updateStatus();
            repaint();
        }

        public ActionRedo() {
            this$0 = GUI.this;
            super("Redo", "Redo", "redo", "ctrl Y");
            setEnabled(false);
        }
    }

    class ActionUndo extends MyAction {

        private static final long serialVersionUID = 0xfff534209bd2ce61L;
        final GUI this$0;

        public boolean isEnabled() {
            return m_BayesNet.canUndo();
        }

        public void actionPerformed(ActionEvent actionevent) {
            String s = m_BayesNet.undo();
            m_jStatusBar.setText((new StringBuilder()).append("Undo action performed: ").append(s).toString());
            a_redo.setEnabled(m_BayesNet.canRedo());
            a_undo.setEnabled(m_BayesNet.canUndo());
            m_Selection.clear();
            updateStatus();
            repaint();
        }

        public ActionUndo() {
            this$0 = GUI.this;
            super("Undo", "Undo", "undo", "ctrl Z");
            setEnabled(false);
        }
    }

    class ActionSetData extends MyAction {

        private static final long serialVersionUID = 0xfff8c19f04fe8461L;
        final GUI this$0;

        public void actionPerformed(ActionEvent actionevent) {
            JFileChooser jfilechooser = new JFileChooser(System.getProperty("user.dir"));
            ExtensionFileFilter extensionfilefilter = new ExtensionFileFilter(".arff", "ARFF files");
            jfilechooser.addChoosableFileFilter(extensionfilefilter);
            jfilechooser.setDialogTitle("Set Data File");
            int i = jfilechooser.showOpenDialog(GUI.this);
            if (i == 0) {
                String s = jfilechooser.getSelectedFile().toString();
                try {
                    m_Instances = new Instances(new FileReader(s));
                    m_Instances.setClassIndex(m_Instances.numAttributes() - 1);
                    a_learn.setEnabled(true);
                    a_learnCPT.setEnabled(true);
                    repaint();
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }

        public ActionSetData() {
            this$0 = GUI.this;
            super("Set Data", "Set Data File", "setdata", "ctrl A");
        }
    }

    class ActionLearnCPT extends MyAction {

        private static final long serialVersionUID = 0xfff8d0cf4a8fdc64L;
        final GUI this$0;

        public void actionPerformed(ActionEvent actionevent) {
            if (m_Instances == null) {
                JOptionPane.showMessageDialog(null, "Select instances to learn from first (menu Tools/Set Data)");
                return;
            }
            try {
                m_BayesNet.setData(m_Instances);
            }
            catch (Exception exception) {
                JOptionPane.showMessageDialog(null, (new StringBuilder()).append("Data set is not compatible with network.\n").append(exception.getMessage()).append("\nChoose other instances (menu Tools/Set Data)").toString());
                return;
            }
            try {
                m_BayesNet.estimateCPTs();
                m_BayesNet.clearUndoStack();
            }
            catch (Exception exception1) {
                exception1.printStackTrace();
            }
            updateStatus();
        }

        public ActionLearnCPT() {
            this$0 = GUI.this;
            super("Learn CPT", "Learn conditional probability tables", "learncpt", "");
            setEnabled(false);
        }
    }

    class ActionLearn extends MyAction {

        private static final long serialVersionUID = 0xfff8c19f04fe8464L;
        JDialog dlg;
        final GUI this$0;

        public void actionPerformed(ActionEvent actionevent) {
            if (dlg == null) {
                dlg = new JDialog();
                dlg.setTitle("Learn Bayesian Network");
                JButton jbutton = new JButton("Options");
                jbutton.addActionListener(new ActionListener() {

                    final ActionLearn this$1;

                    public void actionPerformed(ActionEvent actionevent1) {
                        try {
                            GenericObjectEditor.registerEditors();
                            GenericObjectEditor genericobjecteditor = new GenericObjectEditor(true);
                            genericobjecteditor.setClassType(weka/classifiers/Classifier);
                            genericobjecteditor.setValue(m_BayesNet);
                            PropertyDialog propertydialog;
                            if (PropertyDialog.getParentDialog(_fld0) != null) {
                                propertydialog = new PropertyDialog(PropertyDialog.getParentDialog(_fld0), genericobjecteditor, 100, 100);
                            } else {
                                propertydialog = new PropertyDialog(PropertyDialog.getParentFrame(_fld0), genericobjecteditor, 100, 100);
                            }
                            propertydialog.addWindowListener(new WindowAdapter() {

                                final _cls1 this$2;

                                public void windowClosing(WindowEvent windowevent) {
                                    PropertyEditor propertyeditor = ((PropertyDialog)windowevent.getSource()).getEditor();
                                    Object obj = propertyeditor.getValue();
                                    String s = "";
                                    if (obj instanceof OptionHandler) {
                                        s = Utils.joinOptions(((OptionHandler)obj).getOptions());
                                        try {
                                            m_BayesNet.setOptions(((OptionHandler)obj).getOptions());
                                        }
                                        catch (Exception exception1) {
                                            exception1.printStackTrace();
                                        }
                                    }
                                    System.out.println((new StringBuilder()).append(obj.getClass().getName()).append(" ").append(s).toString());
                                    System.exit(0);
                                }

                         {
                            this$2 = _cls1.this;
                            super();
                        }
                            });
                            propertydialog.setVisible(true);
                        }
                        catch (Exception exception) {
                            exception.printStackTrace();
                            System.err.println(exception.getMessage());
                        }
                        m_BayesNet.clearUndoStack();
                        a_undo.setEnabled(false);
                        a_redo.setEnabled(false);
                    }

                 {
                    this$1 = ActionLearn.this;
                    super();
                }
                });
                JTextField jtextfield = new JTextField(40);
                jtextfield.setHorizontalAlignment(0);
                jtextfield.setText((new StringBuilder()).append("").append(Utils.joinOptions(m_BayesNet.getOptions())).toString());
                JButton jbutton1 = new JButton("Learn");
                jbutton1.addActionListener(new ActionListener() {

                    final ActionLearn this$1;

                    public void actionPerformed(ActionEvent actionevent1) {
                        try {
                            m_BayesNet.buildClassifier(m_Instances);
                            layoutGraph();
                            updateStatus();
                            m_BayesNet.clearUndoStack();
                            dlg.setVisible(false);
                        }
                        catch (Exception exception) {
                            exception.printStackTrace();
                        }
                        dlg.setVisible(false);
                    }

                 {
                    this$1 = ActionLearn.this;
                    super();
                }
                });
                JButton jbutton2 = new JButton("Cancel");
                jbutton2.setMnemonic('C');
                jbutton2.addActionListener(new ActionListener() {

                    final ActionLearn this$1;

                    public void actionPerformed(ActionEvent actionevent1) {
                        dlg.setVisible(false);
                    }

                 {
                    this$1 = ActionLearn.this;
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
                container.add(jbutton, gridbagconstraints);
                gridbagconstraints.gridwidth = 0;
                container.add(jtextfield, gridbagconstraints);
                gridbagconstraints.fill = 2;
                dlg.add(container, gridbagconstraints);
                dlg.add(jbutton1);
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

        public ActionLearn() {
            this$0 = GUI.this;
            super("Learn Network", "Learn Bayesian Network", "learn", "ctrl L");
            dlg = null;
            setEnabled(false);
        }
    }

    class ActionGenerateData extends MyAction {

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
                    final ActionGenerateData this$1;

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
                    this$1 = ActionGenerateData.this;
                    jTfNrOfInstances = jtextfield;
                    jTfFile = jtextfield1;
                    super();
                }
                });
                JButton jbutton1 = new JButton("Browse");
                jbutton1.addActionListener(new ActionListener() {

                    final JTextField val$jTfFile;
                    final ActionGenerateData this$1;

                    public void actionPerformed(ActionEvent actionevent1) {
                        JFileChooser jfilechooser = new JFileChooser(System.getProperty("user.dir"));
                        ExtensionFileFilter extensionfilefilter = new ExtensionFileFilter(".arff", "Arff files");
                        jfilechooser.addChoosableFileFilter(extensionfilefilter);
                        jfilechooser.setDialogTitle("Save Instances As");
                        int i = jfilechooser.showSaveDialog(_fld0);
                        if (i == 0) {
                            String s = jfilechooser.getSelectedFile().toString();
                            jTfFile.setText(s);
                        }
                        dlg.setVisible(true);
                    }

                 {
                    this$1 = ActionGenerateData.this;
                    jTfFile = jtextfield;
                    super();
                }
                });
                JButton jbutton2 = new JButton("Cancel");
                jbutton2.setMnemonic('C');
                jbutton2.addActionListener(new ActionListener() {

                    final ActionGenerateData this$1;

                    public void actionPerformed(ActionEvent actionevent1) {
                        dlg.setVisible(false);
                    }

                 {
                    this$1 = ActionGenerateData.this;
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

        public ActionGenerateData() {
            this$0 = GUI.this;
            super("Generate Data", "Generate Random Instances from Network", "generate.data", "ctrl D");
            m_nNrOfInstances = 100;
            m_nSeed = 1234;
            m_sFile = "";
            dlg = null;
        }
    }

    class ActionGenerateNetwork extends MyAction {

        private static final long serialVersionUID = 0xfff8c19f04fe8463L;
        int m_nNrOfNodes;
        int m_nNrOfArcs;
        int m_nCardinality;
        int m_nSeed;
        JDialog dlg;
        final GUI this$0;

        public void actionPerformed(ActionEvent actionevent) {
            if (dlg == null) {
                dlg = new JDialog();
                dlg.setTitle("Generate Random Bayesian Network Options");
                JLabel jlabel = new JLabel("Nr of nodes");
                final JTextField jTfNrOfNodes = new JTextField(3);
                jTfNrOfNodes.setHorizontalAlignment(0);
                jTfNrOfNodes.setText((new StringBuilder()).append("").append(m_nNrOfNodes).toString());
                JLabel jlabel1 = new JLabel("Nr of arcs");
                final JTextField jTfNrOfArcs = new JTextField(3);
                jTfNrOfArcs.setHorizontalAlignment(0);
                jTfNrOfArcs.setText((new StringBuilder()).append("").append(m_nNrOfArcs).toString());
                JLabel jlabel2 = new JLabel("Cardinality");
                final JTextField jTfCardinality = new JTextField(3);
                jTfCardinality.setHorizontalAlignment(0);
                jTfCardinality.setText((new StringBuilder()).append("").append(m_nCardinality).toString());
                JLabel jlabel3 = new JLabel("Random seed");
                final JTextField jTfSeed = new JTextField(3);
                jTfSeed.setHorizontalAlignment(0);
                jTfSeed.setText((new StringBuilder()).append("").append(m_nSeed).toString());
                JButton jbutton = new JButton("Generate Network");
                jbutton.addActionListener(new ActionListener() {

                    final JTextField val$jTfNrOfNodes;
                    final JTextField val$jTfNrOfArcs;
                    final JTextField val$jTfCardinality;
                    final JTextField val$jTfSeed;
                    final ActionGenerateNetwork this$1;

                    public void actionPerformed(ActionEvent actionevent1) {
                        try {
                            BayesNetGenerator bayesnetgenerator = new BayesNetGenerator();
                            m_BayesNet = bayesnetgenerator;
                            m_BayesNet.clearUndoStack();
                            String as[] = new String[8];
                            as[0] = "-N";
                            as[1] = (new StringBuilder()).append("").append(jTfNrOfNodes.getText()).toString();
                            as[2] = "-A";
                            as[3] = (new StringBuilder()).append("").append(jTfNrOfArcs.getText()).toString();
                            as[4] = "-C";
                            as[5] = (new StringBuilder()).append("").append(jTfCardinality.getText()).toString();
                            as[6] = "-S";
                            as[7] = (new StringBuilder()).append("").append(jTfSeed.getText()).toString();
                            bayesnetgenerator.setOptions(as);
                            bayesnetgenerator.generateRandomNetwork();
                            BIFReader bifreader = new BIFReader();
                            bifreader.processString(m_BayesNet.toXMLBIF03());
                            m_BayesNet = new EditableBayesNet(bifreader);
                            updateStatus();
                            layoutGraph();
                            a_datagenerator.setEnabled(true);
                            m_Instances = null;
                            a_learn.setEnabled(false);
                            a_learnCPT.setEnabled(false);
                            dlg.setVisible(false);
                        }
                        catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }

                 {
                    this$1 = ActionGenerateNetwork.this;
                    jTfNrOfNodes = jtextfield;
                    jTfNrOfArcs = jtextfield1;
                    jTfCardinality = jtextfield2;
                    jTfSeed = jtextfield3;
                    super();
                }
                });
                JButton jbutton1 = new JButton("Cancel");
                jbutton1.setMnemonic('C');
                jbutton1.addActionListener(new ActionListener() {

                    final ActionGenerateNetwork this$1;

                    public void actionPerformed(ActionEvent actionevent1) {
                        dlg.setVisible(false);
                    }

                 {
                    this$1 = ActionGenerateNetwork.this;
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
                container.add(jTfNrOfNodes, gridbagconstraints);
                gridbagconstraints.gridwidth = -1;
                container.add(jlabel1, gridbagconstraints);
                gridbagconstraints.gridwidth = 0;
                container.add(jTfNrOfArcs, gridbagconstraints);
                gridbagconstraints.gridwidth = -1;
                container.add(jlabel2, gridbagconstraints);
                gridbagconstraints.gridwidth = 0;
                container.add(jTfCardinality, gridbagconstraints);
                gridbagconstraints.gridwidth = -1;
                container.add(jlabel3, gridbagconstraints);
                gridbagconstraints.gridwidth = 0;
                container.add(jTfSeed, gridbagconstraints);
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

        public ActionGenerateNetwork() {
            this$0 = GUI.this;
            super("Generate Network", "Generate Random Bayesian Network", "generate.network", "ctrl N");
            m_nNrOfNodes = 10;
            m_nNrOfArcs = 15;
            m_nCardinality = 2;
            m_nSeed = 123;
            dlg = null;
        }
    }

    class MyAction extends AbstractAction {

        private static final long serialVersionUID = 0xfff8c19f0371c9e3L;
        final GUI this$0;

        public void actionPerformed(ActionEvent actionevent) {
        }

        public MyAction(String s, String s1, String s2, String s3) {
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

    class ClipBoard {

        String m_sText;
        final GUI this$0;

        public boolean hasText() {
            return m_sText != null;
        }

        public String getText() {
            return m_sText;
        }

        public void setText(String s) {
            m_sText = s;
            a_pastenode.setEnabled(true);
        }

        public ClipBoard() {
            this$0 = GUI.this;
            super();
            m_sText = null;
            if (a_pastenode != null) {
                a_pastenode.setEnabled(false);
            }
        }
    }

    class Selection {

        FastVector m_selected;
        final GUI this$0;

        public FastVector getSelected() {
            return m_selected;
        }

        void updateGUI() {
            if (m_selected.size() > 0) {
                a_cutnode.setEnabled(true);
                a_copynode.setEnabled(true);
            } else {
                a_cutnode.setEnabled(false);
                a_copynode.setEnabled(false);
            }
            if (m_selected.size() > 1) {
                a_alignleft.setEnabled(true);
                a_alignright.setEnabled(true);
                a_aligntop.setEnabled(true);
                a_alignbottom.setEnabled(true);
                a_centerhorizontal.setEnabled(true);
                a_centervertical.setEnabled(true);
                a_spacehorizontal.setEnabled(true);
                a_spacevertical.setEnabled(true);
            } else {
                a_alignleft.setEnabled(false);
                a_alignright.setEnabled(false);
                a_aligntop.setEnabled(false);
                a_alignbottom.setEnabled(false);
                a_centerhorizontal.setEnabled(false);
                a_centervertical.setEnabled(false);
                a_spacehorizontal.setEnabled(false);
                a_spacevertical.setEnabled(false);
            }
        }

        public void addToSelection(int i) {
            for (int j = 0; j < m_selected.size(); j++) {
                if (i == ((Integer)m_selected.elementAt(j)).intValue()) {
                    return;
                }
            }

            m_selected.addElement(Integer.valueOf(i));
            updateGUI();
        }

        public void addToSelection(int ai[]) {
            for (int i = 0; i < ai.length; i++) {
                addToSelection(ai[i]);
            }

            updateGUI();
        }

        public void addToSelection(Rectangle rectangle) {
            for (int i = 0; i < m_BayesNet.getNrOfNodes(); i++) {
                if (contains(rectangle, i)) {
                    addToSelection(i);
                }
            }

        }

        public void selectAll() {
            m_selected.removeAllElements();
            for (int i = 0; i < m_BayesNet.getNrOfNodes(); i++) {
                m_selected.addElement(Integer.valueOf(i));
            }

            updateGUI();
        }

        boolean contains(Rectangle rectangle, int i) {
            return rectangle.intersects((double)m_BayesNet.getPositionX(i) * m_fScale, (double)m_BayesNet.getPositionY(i) * m_fScale, (double)m_nPaddedNodeWidth * m_fScale, (double)m_nNodeHeight * m_fScale);
        }

        public void removeFromSelection(int i) {
            for (int j = 0; j < m_selected.size(); j++) {
                if (i == ((Integer)m_selected.elementAt(j)).intValue()) {
                    m_selected.removeElementAt(j);
                }
            }

            updateGUI();
        }

        public void toggleSelection(int i) {
            for (int j = 0; j < m_selected.size(); j++) {
                if (i == ((Integer)m_selected.elementAt(j)).intValue()) {
                    m_selected.removeElementAt(j);
                    updateGUI();
                    return;
                }
            }

            addToSelection(i);
        }

        public void toggleSelection(Rectangle rectangle) {
            for (int i = 0; i < m_BayesNet.getNrOfNodes(); i++) {
                if (contains(rectangle, i)) {
                    toggleSelection(i);
                }
            }

        }

        public void clear() {
            m_selected.removeAllElements();
            updateGUI();
        }

        public void draw(Graphics g) {
            if (m_selected.size() == 0) {
                return;
            }
            for (int i = 0; i < m_selected.size(); i++) {
                int j = ((Integer)m_selected.elementAt(i)).intValue();
                int k = m_BayesNet.getPositionX(j);
                int l = m_BayesNet.getPositionY(j);
                g.setColor(Color.BLACK);
                int i1 = (k + m_nPaddedNodeWidth) - m_nNodeWidth - (m_nPaddedNodeWidth - m_nNodeWidth) / 2;
                int j1 = l;
                byte byte0 = 5;
                g.fillRect(i1, j1, byte0, byte0);
                g.fillRect(i1, j1 + m_nNodeHeight, byte0, byte0);
                g.fillRect(i1 + m_nNodeWidth, j1, byte0, byte0);
                g.fillRect(i1 + m_nNodeWidth, j1 + m_nNodeHeight, byte0, byte0);
            }

        }

        public Selection() {
            this$0 = GUI.this;
            super();
            m_selected = new FastVector();
        }
    }


    private static final long serialVersionUID = 0xe3b4552b82352818L;
    protected LayoutEngine m_layoutEngine;
    protected GraphPanel m_GraphPanel;
    EditableBayesNet m_BayesNet;
    protected String m_sFileName;
    MarginCalculator m_marginCalculator;
    MarginCalculator m_marginCalculatorWithEvidence;
    boolean m_bViewMargins;
    boolean m_bViewCliques;
    private JMenuBar m_menuBar;
    Instances m_Instances;
    final JTextField m_jTfZoom = new JTextField("100%");
    final JToolBar m_jTbTools = new JToolBar();
    final JLabel m_jStatusBar = new JLabel("Status bar");
    private final JTextField m_jTfNodeWidth = new JTextField(3);
    private final JTextField m_jTfNodeHeight = new JTextField(3);
    JScrollPane m_jScrollPane;
    private final String ICONPATH = "weka/classifiers/bayes/net/icons/";
    private double m_fScale;
    private int m_nNodeHeight;
    static final int DEFAULT_NODE_WIDTH = 50;
    private int m_nNodeWidth;
    static final int PADDING = 10;
    private int m_nPaddedNodeWidth;
    private int m_nZoomPercents[] = {
        10, 25, 50, 75, 100, 125, 150, 175, 200, 225, 
        250, 275, 300, 350, 400, 450, 500, 550, 600, 650, 
        700, 800, 900, 999
    };
    Action a_new;
    Action a_quit;
    Action a_save;
    ActionExport a_export;
    ActionPrint a_print;
    Action a_load;
    Action a_zoomin;
    Action a_zoomout;
    Action a_layout;
    Action a_saveas;
    Action a_viewtoolbar;
    Action a_viewstatusbar;
    Action a_networkgenerator;
    Action a_datagenerator;
    Action a_datasetter;
    Action a_learn;
    Action a_learnCPT;
    Action a_help;
    Action a_about;
    ActionAddNode a_addnode;
    Action a_delnode;
    Action a_cutnode;
    Action a_copynode;
    Action a_pastenode;
    Action a_selectall;
    Action a_addarc;
    Action a_delarc;
    Action a_undo;
    Action a_redo;
    Action a_alignleft;
    Action a_alignright;
    Action a_aligntop;
    Action a_alignbottom;
    Action a_centerhorizontal;
    Action a_centervertical;
    Action a_spacehorizontal;
    Action a_spacevertical;
    int m_nCurrentNode;
    Selection m_Selection;
    Rectangle m_nSelectedRect;
    ClipBoard m_clipboard;

    public GUI() {
        m_BayesNet = new EditableBayesNet(true);
        m_sFileName = "";
        m_marginCalculator = null;
        m_marginCalculatorWithEvidence = null;
        m_bViewMargins = false;
        m_bViewCliques = false;
        m_Instances = null;
        m_fScale = 1.0D;
        m_nNodeHeight = 2 * getFontMetrics(getFont()).getHeight();
        m_nNodeWidth = 50;
        m_nPaddedNodeWidth = 60;
        a_new = new ActionNew();
        a_quit = new ActionQuit();
        a_save = new ActionSave();
        a_export = new ActionExport();
        a_print = new ActionPrint();
        a_load = new ActionLoad();
        a_zoomin = new ActionZoomIn();
        a_zoomout = new ActionZoomOut();
        a_layout = new ActionLayout();
        a_saveas = new ActionSaveAs();
        a_viewtoolbar = new ActionViewToolbar();
        a_viewstatusbar = new ActionViewStatusbar();
        a_networkgenerator = new ActionGenerateNetwork();
        a_datagenerator = new ActionGenerateData();
        a_datasetter = new ActionSetData();
        a_learn = new ActionLearn();
        a_learnCPT = new ActionLearnCPT();
        a_help = new ActionHelp();
        a_about = new ActionAbout();
        a_addnode = new ActionAddNode();
        a_delnode = new ActionDeleteNode();
        a_cutnode = new ActionCutNode();
        a_copynode = new ActionCopyNode();
        a_pastenode = new ActionPasteNode();
        a_selectall = new ActionSelectAll();
        a_addarc = new ActionAddArc();
        a_delarc = new ActionDeleteArc();
        a_undo = new ActionUndo();
        a_redo = new ActionRedo();
        a_alignleft = new ActionAlignLeft();
        a_alignright = new ActionAlignRight();
        a_aligntop = new ActionAlignTop();
        a_alignbottom = new ActionAlignBottom();
        a_centerhorizontal = new ActionCenterHorizontal();
        a_centervertical = new ActionCenterVertical();
        a_spacehorizontal = new ActionSpaceHorizontal();
        a_spacevertical = new ActionSpaceVertical();
        m_nCurrentNode = -1;
        m_Selection = new Selection();
        m_nSelectedRect = null;
        m_clipboard = new ClipBoard();
        m_GraphPanel = new GraphPanel();
        m_jScrollPane = new JScrollPane(m_GraphPanel);
        m_jTfZoom.setMinimumSize(m_jTfZoom.getPreferredSize());
        m_jTfZoom.setHorizontalAlignment(0);
        m_jTfZoom.setToolTipText("Zoom");
        m_jTfZoom.addActionListener(new ActionListener() {

            final GUI this$0;

            public void actionPerformed(ActionEvent actionevent) {
                JTextField jtextfield = (JTextField)actionevent.getSource();
                try {
                    int i = -1;
                    i = jtextfield.getText().indexOf('%');
                    if (i == -1) {
                        i = Integer.parseInt(jtextfield.getText());
                    } else {
                        i = Integer.parseInt(jtextfield.getText().substring(0, i));
                    }
                    if (i <= 999) {
                        m_fScale = (double)i / 100D;
                    }
                    jtextfield.setText((new StringBuilder()).append((int)(m_fScale * 100D)).append("%").toString());
                    if (m_fScale > 0.10000000000000001D) {
                        if (!a_zoomout.isEnabled()) {
                            a_zoomout.setEnabled(true);
                        }
                    } else {
                        a_zoomout.setEnabled(false);
                    }
                    if (m_fScale < 9.9900000000000002D) {
                        if (!a_zoomin.isEnabled()) {
                            a_zoomin.setEnabled(true);
                        }
                    } else {
                        a_zoomin.setEnabled(false);
                    }
                    setAppropriateSize();
                    m_GraphPanel.repaint();
                    m_GraphPanel.invalidate();
                    m_jScrollPane.revalidate();
                }
                catch (NumberFormatException numberformatexception) {
                    JOptionPane.showMessageDialog(getParent(), "Invalid integer entered for zoom.", "Error", 0);
                    jtextfield.setText((new StringBuilder()).append(m_fScale * 100D).append("%").toString());
                }
            }

             {
                this$0 = GUI.this;
                super();
            }
        });
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        JPanel jpanel = new JPanel(new GridBagLayout());
        jpanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("ExtraControls"), BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        jpanel.setPreferredSize(new Dimension(0, 0));
        m_jTbTools.setFloatable(false);
        m_jTbTools.setLayout(new GridBagLayout());
        gridbagconstraints.anchor = 18;
        gridbagconstraints.gridwidth = 0;
        gridbagconstraints.insets = new Insets(0, 0, 0, 0);
        m_jTbTools.add(jpanel, gridbagconstraints);
        gridbagconstraints.gridwidth = 1;
        m_jTbTools.add(a_new);
        m_jTbTools.add(a_save);
        m_jTbTools.add(a_load);
        m_jTbTools.addSeparator(new Dimension(2, 2));
        m_jTbTools.add(a_cutnode);
        m_jTbTools.add(a_copynode);
        m_jTbTools.add(a_pastenode);
        m_jTbTools.addSeparator(new Dimension(2, 2));
        m_jTbTools.add(a_undo);
        m_jTbTools.add(a_redo);
        m_jTbTools.addSeparator(new Dimension(2, 2));
        m_jTbTools.add(a_alignleft);
        m_jTbTools.add(a_alignright);
        m_jTbTools.add(a_aligntop);
        m_jTbTools.add(a_alignbottom);
        m_jTbTools.add(a_centerhorizontal);
        m_jTbTools.add(a_centervertical);
        m_jTbTools.add(a_spacehorizontal);
        m_jTbTools.add(a_spacevertical);
        m_jTbTools.addSeparator(new Dimension(2, 2));
        m_jTbTools.add(a_zoomin);
        gridbagconstraints.fill = 3;
        gridbagconstraints.weighty = 1.0D;
        JPanel jpanel1 = new JPanel(new BorderLayout());
        jpanel1.setPreferredSize(m_jTfZoom.getPreferredSize());
        jpanel1.setMinimumSize(m_jTfZoom.getPreferredSize());
        jpanel1.add(m_jTfZoom, "Center");
        m_jTbTools.add(jpanel1, gridbagconstraints);
        gridbagconstraints.weighty = 0.0D;
        gridbagconstraints.fill = 0;
        m_jTbTools.add(a_zoomout);
        m_jTbTools.addSeparator(new Dimension(2, 2));
        m_jTbTools.add(a_layout);
        m_jTbTools.addSeparator(new Dimension(4, 2));
        gridbagconstraints.weightx = 1.0D;
        gridbagconstraints.fill = 1;
        setLayout(new BorderLayout());
        add(m_jTbTools, "North");
        add(m_jScrollPane, "Center");
        add(m_jStatusBar, "South");
        updateStatus();
        a_datagenerator.setEnabled(false);
        makeMenuBar();
    }

    public JMenuBar getMenuBar() {
        return m_menuBar;
    }

    private void makeMenuBar() {
        m_menuBar = new JMenuBar();
        JMenu jmenu = new JMenu("File");
        jmenu.setMnemonic('F');
        m_menuBar.add(jmenu);
        jmenu.add(a_new);
        jmenu.add(a_load);
        jmenu.add(a_save);
        jmenu.add(a_saveas);
        jmenu.addSeparator();
        jmenu.add(a_print);
        jmenu.add(a_export);
        jmenu.addSeparator();
        jmenu.add(a_quit);
        JMenu jmenu1 = new JMenu("Edit");
        jmenu1.setMnemonic('E');
        m_menuBar.add(jmenu1);
        jmenu1.add(a_undo);
        jmenu1.add(a_redo);
        jmenu1.addSeparator();
        jmenu1.add(a_selectall);
        jmenu1.add(a_delnode);
        jmenu1.add(a_cutnode);
        jmenu1.add(a_copynode);
        jmenu1.add(a_pastenode);
        jmenu1.addSeparator();
        jmenu1.add(a_addnode);
        jmenu1.add(a_addarc);
        jmenu1.add(a_delarc);
        jmenu1.addSeparator();
        jmenu1.add(a_alignleft);
        jmenu1.add(a_alignright);
        jmenu1.add(a_aligntop);
        jmenu1.add(a_alignbottom);
        jmenu1.add(a_centerhorizontal);
        jmenu1.add(a_centervertical);
        jmenu1.add(a_spacehorizontal);
        jmenu1.add(a_spacevertical);
        JMenu jmenu2 = new JMenu("Tools");
        jmenu2.setMnemonic('T');
        jmenu2.add(a_networkgenerator);
        jmenu2.add(a_datagenerator);
        jmenu2.add(a_datasetter);
        jmenu2.add(a_learn);
        jmenu2.add(a_learnCPT);
        jmenu2.addSeparator();
        jmenu2.add(a_layout);
        jmenu2.addSeparator();
        final JCheckBoxMenuItem viewMargins = new JCheckBoxMenuItem("Show Margins", false);
        viewMargins.addActionListener(new ActionListener() {

            final JCheckBoxMenuItem val$viewMargins;
            final GUI this$0;

            public void actionPerformed(ActionEvent actionevent) {
                boolean flag = m_bViewMargins;
                m_bViewMargins = viewMargins.getState();
                if (!flag && viewMargins.getState()) {
                    updateStatus();
                }
                repaint();
            }

             {
                this$0 = GUI.this;
                viewMargins = jcheckboxmenuitem;
                super();
            }
        });
        jmenu2.add(viewMargins);
        final JCheckBoxMenuItem viewCliques = new JCheckBoxMenuItem("Show Cliques", false);
        viewCliques.addActionListener(new ActionListener() {

            final JCheckBoxMenuItem val$viewCliques;
            final GUI this$0;

            public void actionPerformed(ActionEvent actionevent) {
                boolean flag = m_bViewCliques;
                m_bViewCliques = viewCliques.getState();
                if (!flag && viewCliques.getState()) {
                    updateStatus();
                }
                repaint();
            }

             {
                this$0 = GUI.this;
                viewCliques = jcheckboxmenuitem;
                super();
            }
        });
        jmenu2.add(viewCliques);
        m_menuBar.add(jmenu2);
        JMenu jmenu3 = new JMenu("View");
        jmenu3.setMnemonic('V');
        m_menuBar.add(jmenu3);
        jmenu3.add(a_zoomin);
        jmenu3.add(a_zoomout);
        jmenu3.addSeparator();
        jmenu3.add(a_viewtoolbar);
        jmenu3.add(a_viewstatusbar);
        JMenu jmenu4 = new JMenu("Help");
        jmenu4.setMnemonic('H');
        m_menuBar.add(jmenu4);
        jmenu4.add(a_help);
        jmenu4.add(a_about);
    }

    protected void setAppropriateNodeSize() {
        FontMetrics fontmetrics = getFontMetrics(getFont());
        int j = 50;
        if (j == 0) {
            for (int k = 0; k < m_BayesNet.getNrOfNodes(); k++) {
                int i = fontmetrics.stringWidth(m_BayesNet.getNodeName(k));
                if (i > j) {
                    j = i;
                }
            }

        }
        m_nNodeWidth = j + 4;
        m_nPaddedNodeWidth = m_nNodeWidth + 10;
        m_jTfNodeWidth.setText((new StringBuilder()).append("").append(m_nNodeWidth).toString());
        m_nNodeHeight = 2 * fontmetrics.getHeight();
        m_jTfNodeHeight.setText((new StringBuilder()).append("").append(m_nNodeHeight).toString());
    }

    public void setAppropriateSize() {
        int i = 0;
        int j = 0;
        m_GraphPanel.setScale(m_fScale, m_fScale);
        for (int k = 0; k < m_BayesNet.getNrOfNodes(); k++) {
            int l = m_BayesNet.getPositionX(k);
            int i1 = m_BayesNet.getPositionY(k);
            if (i < l) {
                i = l + 100;
            }
            if (j < i1) {
                j = i1;
            }
        }

        m_GraphPanel.setPreferredSize(new Dimension((int)((double)(i + m_nPaddedNodeWidth + 2) * m_fScale), (int)((double)(j + m_nNodeHeight + 2) * m_fScale)));
        m_GraphPanel.revalidate();
    }

    public void layoutCompleted(LayoutCompleteEvent layoutcompleteevent) {
        LayoutEngine layoutengine = m_layoutEngine;
        FastVector fastvector = new FastVector(m_BayesNet.getNrOfNodes());
        FastVector fastvector1 = new FastVector(m_BayesNet.getNrOfNodes());
        for (int i = 0; i < layoutengine.getNodes().size(); i++) {
            GraphNode graphnode = (GraphNode)layoutengine.getNodes().elementAt(i);
            if (graphnode.nodeType == 3) {
                fastvector.addElement(Integer.valueOf(graphnode.x));
                fastvector1.addElement(Integer.valueOf(graphnode.y));
            }
        }

        m_BayesNet.layoutGraph(fastvector, fastvector1);
        m_jStatusBar.setText("Graph layed out");
        a_undo.setEnabled(true);
        a_redo.setEnabled(false);
        setAppropriateSize();
        m_GraphPanel.invalidate();
        m_jScrollPane.revalidate();
        m_GraphPanel.repaint();
    }

    public void readBIFFromFile(String s) throws BIFFormatException, IOException {
        m_sFileName = s;
        try {
            BIFReader bifreader = new BIFReader();
            bifreader.processFile(s);
            m_BayesNet = new EditableBayesNet(bifreader);
            updateStatus();
            a_datagenerator.setEnabled(m_BayesNet.getNrOfNodes() > 0);
            m_BayesNet.clearUndoStack();
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return;
        }
        setAppropriateNodeSize();
        setAppropriateSize();
    }

    void initFromArffFile(String s) {
        try {
            Instances instances = new Instances(new FileReader(s));
            m_BayesNet = new EditableBayesNet(instances);
            m_Instances = instances;
            a_learn.setEnabled(true);
            a_learnCPT.setEnabled(true);
            setAppropriateNodeSize();
            setAppropriateSize();
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return;
        }
    }

    void layoutGraph() {
        if (m_BayesNet.getNrOfNodes() == 0) {
            return;
        }
        try {
            FastVector fastvector = new FastVector();
            FastVector fastvector1 = new FastVector();
            BIFParser bifparser = new BIFParser(m_BayesNet.toXMLBIF03(), fastvector, fastvector1);
            bifparser.parse();
            updateStatus();
            m_layoutEngine = new HierarchicalBCEngine(fastvector, fastvector1, m_nPaddedNodeWidth, m_nNodeHeight);
            m_layoutEngine.addLayoutCompleteEventListener(this);
            m_layoutEngine.layoutGraph();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    void updateStatus() {
        a_undo.setEnabled(m_BayesNet.canUndo());
        a_redo.setEnabled(m_BayesNet.canRedo());
        a_datagenerator.setEnabled(m_BayesNet.getNrOfNodes() > 0);
        if (!m_bViewMargins && !m_bViewCliques) {
            repaint();
            return;
        }
        try {
            m_marginCalculator = new MarginCalculator();
            m_marginCalculator.calcMargins(m_BayesNet);
            SerializedObject serializedobject = new SerializedObject(m_marginCalculator);
            m_marginCalculatorWithEvidence = (MarginCalculator)serializedobject.getObject();
            for (int i = 0; i < m_BayesNet.getNrOfNodes(); i++) {
                if (m_BayesNet.getEvidence(i) >= 0) {
                    m_marginCalculatorWithEvidence.setEvidence(i, m_BayesNet.getEvidence(i));
                }
            }

            for (int j = 0; j < m_BayesNet.getNrOfNodes(); j++) {
                m_BayesNet.setMargin(j, m_marginCalculatorWithEvidence.getMargin(j));
            }

        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        repaint();
    }

    void addArcInto(int i) {
        String s = m_BayesNet.getNodeName(i);
        int j;
        boolean aflag[];
        int i1;
        j = m_BayesNet.getNrOfNodes();
        aflag = new boolean[j];
        aflag[i] = true;
        for (int k = 0; k < j; k++) {
            for (int j1 = 0; j1 < j; j1++) {
                for (int l1 = 0; l1 < m_BayesNet.getNrOfParents(j1); l1++) {
                    if (aflag[m_BayesNet.getParent(j1, l1)]) {
                        aflag[j1] = true;
                    }
                }

            }

        }

        for (int l = 0; l < m_BayesNet.getNrOfParents(i); l++) {
            aflag[m_BayesNet.getParent(i, l)] = true;
        }

        i1 = 0;
        for (int k1 = 0; k1 < j; k1++) {
            if (!aflag[k1]) {
                i1++;
            }
        }

        if (i1 == 0) {
            JOptionPane.showMessageDialog(null, (new StringBuilder()).append("No potential parents available for this node (").append(s).append("). Choose another node as child node.").toString());
            return;
        }
        String s1;
        String as[] = new String[i1];
        int i2 = 0;
        for (int j2 = 0; j2 < j; j2++) {
            if (!aflag[j2]) {
                as[i2++] = m_BayesNet.getNodeName(j2);
            }
        }

        s1 = (String)JOptionPane.showInputDialog(null, (new StringBuilder()).append("Select parent node for ").append(s).toString(), "Nodes", 0, null, as, as[0]);
        if (s1 == null || s1.equals("")) {
            return;
        }
        try {
            m_BayesNet.addArc(s1, s);
            m_jStatusBar.setText(m_BayesNet.lastActionMsg());
            updateStatus();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        return;
    }

    void deleteArc(int i, String s) {
        try {
            m_BayesNet.deleteArc(m_BayesNet.getNode(s), i);
            m_jStatusBar.setText(m_BayesNet.lastActionMsg());
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        updateStatus();
    }

    void deleteArc(String s, int i) {
        try {
            m_BayesNet.deleteArc(i, m_BayesNet.getNode(s));
            m_jStatusBar.setText(m_BayesNet.lastActionMsg());
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        updateStatus();
    }

    void deleteArc(String as[]) {
        String s = (String)JOptionPane.showInputDialog(null, "Select arc to delete", "Arcs", 0, null, as, as[0]);
        if (s != null && !s.equals("")) {
            int i = s.indexOf(" -> ");
            String s1 = s.substring(0, i);
            String s2 = s.substring(i + 4);
            try {
                m_BayesNet.deleteArc(s1, s2);
                m_jStatusBar.setText(m_BayesNet.lastActionMsg());
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
            updateStatus();
        }
    }

    void renameNode(int i) {
        String s;
        s = JOptionPane.showInputDialog(null, m_BayesNet.getNodeName(i), "New name for node", 2);
        if (s == null || s.equals("")) {
            return;
        }
        while (m_BayesNet.getNode2(s) >= 0)  {
            s = JOptionPane.showInputDialog(null, (new StringBuilder()).append("Cannot rename to ").append(s).append(".\nNode with that name already exists.").toString());
            if (s == null || s.equals("")) {
                return;
            }
        }
        try {
            m_BayesNet.setNodeName(i, s);
            m_jStatusBar.setText(m_BayesNet.lastActionMsg());
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        repaint();
        return;
    }

    void renameValue(int i, String s) {
        String s1 = JOptionPane.showInputDialog(null, (new StringBuilder()).append("New name for value ").append(s).toString(), (new StringBuilder()).append("Node ").append(m_BayesNet.getNodeName(i)).toString(), 2);
        if (s1 == null || s1.equals("")) {
            return;
        } else {
            m_BayesNet.renameNodeValue(i, s, s1);
            m_jStatusBar.setText(m_BayesNet.lastActionMsg());
            a_undo.setEnabled(true);
            a_redo.setEnabled(false);
            repaint();
            return;
        }
    }

    void deleteNode(int i) {
        try {
            m_BayesNet.deleteNode(i);
            m_jStatusBar.setText(m_BayesNet.lastActionMsg());
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        updateStatus();
    }

    void addValue() {
        String s = (new StringBuilder()).append("Value").append(m_BayesNet.getCardinality(m_nCurrentNode) + 1).toString();
        String s1 = JOptionPane.showInputDialog(null, (new StringBuilder()).append("New value ").append(s).toString(), (new StringBuilder()).append("Node ").append(m_BayesNet.getNodeName(m_nCurrentNode)).toString(), 2);
        if (s1 == null || s1.equals("")) {
            return;
        }
        try {
            m_BayesNet.addNodeValue(m_nCurrentNode, s1);
            m_jStatusBar.setText(m_BayesNet.lastActionMsg());
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        updateStatus();
    }

    void delValue(int i, String s) {
        try {
            m_BayesNet.delNodeValue(i, s);
            m_jStatusBar.setText(m_BayesNet.lastActionMsg());
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        updateStatus();
    }

    void editCPT(int i) {
        m_nCurrentNode = i;
        final GraphVisualizerTableModel tm = new GraphVisualizerTableModel(i);
        JTable jtable = new JTable(tm);
        JScrollPane jscrollpane = new JScrollPane(jtable);
        int j = m_BayesNet.getNrOfParents(i);
        if (j > 0) {
            GridBagConstraints gridbagconstraints = new GridBagConstraints();
            JPanel jpanel = new JPanel(new GridBagLayout());
            int ai[] = new int[j];
            int ai1[] = new int[j];
            gridbagconstraints.anchor = 18;
            gridbagconstraints.fill = 2;
            gridbagconstraints.insets = new Insets(0, 1, 0, 0);
            int k = 0;
            boolean flag = false;
            boolean flag1 = false;
            int j1;
            do {
                gridbagconstraints.gridwidth = 1;
                for (int i1 = 0; i1 < j; i1++) {
                    int k1 = m_BayesNet.getParent(i, i1);
                    JLabel jlabel1 = new JLabel(m_BayesNet.getValueName(k1, ai[i1]));
                    jlabel1.setFont(new Font("Dialog", 0, 12));
                    jlabel1.setOpaque(true);
                    jlabel1.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 1));
                    jlabel1.setHorizontalAlignment(0);
                    if (flag1) {
                        jlabel1.setBackground(jlabel1.getBackground().darker());
                        jlabel1.setForeground(Color.white);
                    } else {
                        jlabel1.setForeground(Color.black);
                    }
                    int l = jlabel1.getPreferredSize().width;
                    jlabel1.setPreferredSize(new Dimension(l, jtable.getRowHeight()));
                    if (ai1[i1] < l) {
                        ai1[i1] = l;
                    }
                    l = 0;
                    if (i1 == j - 1) {
                        gridbagconstraints.gridwidth = 0;
                        flag1 = !flag1;
                    }
                    jpanel.add(jlabel1, gridbagconstraints);
                    k++;
                }

                j1 = j - 1;
                do {
                    if (j1 < 0) {
                        break;
                    }
                    int l1 = m_BayesNet.getParent(m_nCurrentNode, j1);
                    if (ai[j1] == m_BayesNet.getCardinality(l1) - 1 && j1 != 0) {
                        ai[j1] = 0;
                    } else {
                        ai[j1]++;
                        break;
                    }
                    j1--;
                } while (true);
                j1 = m_BayesNet.getParent(m_nCurrentNode, 0);
            } while (ai[0] != m_BayesNet.getCardinality(j1));
            JLabel jlabel = (JLabel)jpanel.getComponent(k - 1);
            jpanel.remove(k - 1);
            jlabel.setPreferredSize(new Dimension(jlabel.getPreferredSize().width, jtable.getRowHeight()));
            gridbagconstraints.gridwidth = 0;
            gridbagconstraints.weighty = 1.0D;
            jpanel.add(jlabel, gridbagconstraints);
            gridbagconstraints.weighty = 0.0D;
            gridbagconstraints.gridwidth = 1;
            JPanel jpanel1 = new JPanel(new GridBagLayout());
            for (int i2 = 0; i2 < j; i2++) {
                JLabel jlabel3 = new JLabel(m_BayesNet.getNodeName(m_BayesNet.getParent(i, i2)));
                jlabel3.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 1));
                Dimension dimension = jlabel3.getPreferredSize();
                if (dimension.width < ai1[i2]) {
                    jlabel3.setPreferredSize(new Dimension(ai1[i2], dimension.height));
                    jlabel3.setHorizontalAlignment(0);
                    jlabel3.setMinimumSize(new Dimension(ai1[i2], dimension.height));
                } else
                if (dimension.width > ai1[i2]) {
                    JLabel jlabel2 = (JLabel)jpanel.getComponent(i2);
                    jlabel2.setPreferredSize(new Dimension(dimension.width, jlabel2.getPreferredSize().height));
                }
                jpanel1.add(jlabel3, gridbagconstraints);
            }

            jscrollpane.setRowHeaderView(jpanel);
            jscrollpane.setCorner("UPPER_LEFT_CORNER", jpanel1);
        }
        final JDialog dlg = new JDialog((Frame)getTopLevelAncestor(), (new StringBuilder()).append("Probability Distribution Table For ").append(m_BayesNet.getNodeName(i)).toString(), true);
        dlg.setSize(500, 400);
        dlg.setLocation((getLocation().x + getWidth() / 2) - 250, (getLocation().y + getHeight() / 2) - 200);
        dlg.getContentPane().setLayout(new BorderLayout());
        dlg.getContentPane().add(jscrollpane, "Center");
        JButton jbutton = new JButton("Randomize");
        jbutton.setMnemonic('R');
        jbutton.addActionListener(new ActionListener() {

            final GraphVisualizerTableModel val$tm;
            final JDialog val$dlg;
            final GUI this$0;

            public void actionPerformed(ActionEvent actionevent) {
                tm.randomize();
                dlg.repaint();
            }

             {
                this$0 = GUI.this;
                tm = graphvisualizertablemodel;
                dlg = jdialog;
                super();
            }
        });
        JButton jbutton1 = new JButton("Ok");
        jbutton1.setMnemonic('O');
        jbutton1.addActionListener(new ActionListener() {

            final GraphVisualizerTableModel val$tm;
            final JDialog val$dlg;
            final GUI this$0;

            public void actionPerformed(ActionEvent actionevent) {
                tm.setData();
                try {
                    m_BayesNet.setDistribution(m_nCurrentNode, tm.m_fProbs);
                    m_jStatusBar.setText(m_BayesNet.lastActionMsg());
                    updateStatus();
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                dlg.setVisible(false);
            }

             {
                this$0 = GUI.this;
                tm = graphvisualizertablemodel;
                dlg = jdialog;
                super();
            }
        });
        JButton jbutton2 = new JButton("Cancel");
        jbutton2.setMnemonic('C');
        jbutton2.addActionListener(new ActionListener() {

            final JDialog val$dlg;
            final GUI this$0;

            public void actionPerformed(ActionEvent actionevent) {
                dlg.setVisible(false);
            }

             {
                this$0 = GUI.this;
                dlg = jdialog;
                super();
            }
        });
        Container container = new Container();
        container.setLayout(new GridBagLayout());
        container.add(jbutton);
        container.add(jbutton1);
        container.add(jbutton2);
        dlg.getContentPane().add(container, "South");
        dlg.setVisible(true);
    }

    public static void main(String args[]) {
        JFrame jframe = new JFrame("Bayes Network Editor");
        GUI gui = new GUI();
        JMenuBar jmenubar = gui.getMenuBar();
        if (args.length > 0) {
            try {
                gui.readBIFFromFile(args[0]);
            }
            catch (IOException ioexception) {
                ioexception.printStackTrace();
            }
            catch (BIFFormatException bifformatexception) {
                bifformatexception.printStackTrace();
                System.exit(-1);
            }
        }
        jframe.setJMenuBar(jmenubar);
        jframe.getContentPane().add(gui);
        jframe.setDefaultCloseOperation(3);
        jframe.setSize(800, 600);
        jframe.setVisible(true);
        gui.m_Selection.updateGUI();
        GenericObjectEditor.registerEditors();
    }











}
