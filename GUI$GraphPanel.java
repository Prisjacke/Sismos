// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.util.Vector;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import weka.core.FastVector;
import weka.gui.visualize.PrintablePanel;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI, EditableBayesNet, MarginCalculator

private class setToolTipText extends PrintablePanel
    implements Printable {

    private static final long serialVersionUID = 0xce8e57e78a24d0ebL;
    static final int HIGHLIGHTED = 1;
    static final int NORMAL = 0;
    int m_nClique;
    final GUI this$0;

    public String getToolTipText(MouseEvent mouseevent) {
        int j;
        int i = j = 0;
        Rectangle rectangle = new Rectangle(0, 0, (int)((double)GUI.access$100(GUI.this) * GUI.access$000(GUI.this)), (int)((double)GUI.access$200(GUI.this) * GUI.access$000(GUI.this)));
        i += mouseevent.getX();
        j += mouseevent.getY();
        for (int k = 0; k < m_BayesNet.getNrOfNodes(); k++) {
            rectangle.x = (int)((double)m_BayesNet.getPositionX(k) * GUI.access$000(GUI.this));
            rectangle.y = (int)((double)m_BayesNet.getPositionY(k) * GUI.access$000(GUI.this));
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
        graphics2d.scale(GUI.access$000(GUI.this), GUI.access$000(GUI.this));
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
            m_Selection.raw(graphics2d);
        }
        if (m_nSelectedRect != null) {
            graphics2d.drawRect((int)((double)m_nSelectedRect.x / GUI.access$000(GUI.this)), (int)((double)m_nSelectedRect.y / GUI.access$000(GUI.this)), (int)((double)m_nSelectedRect.width / GUI.access$000(GUI.this)), (int)((double)m_nSelectedRect.height / GUI.access$000(GUI.this)));
        }
    }

    void viewCliques(Graphics g, r.JunctionTreeNode junctiontreenode) {
        int ai[] = junctiontreenode.m_nNodes;
        g.setColor(new Color(((m_nClique % 7) * 256) / 7, ((m_nClique % 2) * 256) / 2, ((m_nClique % 3) * 256) / 3));
        int i = GUI.access$100(GUI.this) / 2 + m_nClique;
        int j = GUI.access$200(GUI.this) / 2;
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
            viewCliques(g, (r.JunctionTreeNode)junctiontreenode.m_children.elementAt(j1));
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
        g.fillOval((k + GUI.access$100(GUI.this)) - GUI.access$300(GUI.this) - (GUI.access$100(GUI.this) - GUI.access$300(GUI.this)) / 2, l, GUI.access$300(GUI.this), GUI.access$200(GUI.this));
        g.setColor(Color.white);
        if (j == 1) {
            g.setXORMode(Color.red);
        }
        if (fontmetrics.stringWidth(m_BayesNet.getNodeName(i)) <= GUI.access$300(GUI.this)) {
            g.drawString(m_BayesNet.getNodeName(i), (k + GUI.access$100(GUI.this) / 2) - fontmetrics.stringWidth(m_BayesNet.getNodeName(i)) / 2, (l + GUI.access$200(GUI.this) / 2 + fontmetrics.getHeight() / 2) - 2);
        } else
        if (fontmetrics.stringWidth((new StringBuilder()).append("").append(i).toString()) <= GUI.access$300(GUI.this)) {
            g.drawString((new StringBuilder()).append("").append(i).toString(), (k + GUI.access$100(GUI.this) / 2) - fontmetrics.stringWidth((new StringBuilder()).append("").append(i).toString()) / 2, (l + GUI.access$200(GUI.this) / 2 + fontmetrics.getHeight() / 2) - 2);
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
                g.fillRect(k + GUI.access$100(GUI.this), l + j1 * 10 + 2, (int)(ad[j1] * 100D), 8);
                g.drawString((new StringBuilder()).append(m_BayesNet.getNodeValue(i, j1)).append(" ").append(s).toString(), k + GUI.access$100(GUI.this) + (int)(ad[j1] * 100D), l + j1 * 10 + 10);
            }

        }
        if (m_bViewCliques) {
            return;
        }
        g.setColor(Color.black);
        for (int i1 = 0; i1 < m_BayesNet.getNrOfParents(i); i1++) {
            int k1 = m_BayesNet.getParent(i, i1);
            int i2 = k + GUI.access$100(GUI.this) / 2;
            int k2 = l + GUI.access$200(GUI.this);
            int i3 = m_BayesNet.getPositionX(k1);
            int k3 = m_BayesNet.getPositionY(k1);
            int i4 = i3 + GUI.access$100(GUI.this) / 2;
            int k4 = k3;
            double d = Math.atan2(((double)(i4 - i2) + 0.0D) * (double)GUI.access$200(GUI.this), ((double)(k4 - k2) + 0.0D) * (double)GUI.access$300(GUI.this));
            i2 = (int)((double)(k + GUI.access$100(GUI.this) / 2) + (Math.sin(d) * (double)GUI.access$300(GUI.this)) / 2D);
            k2 = (int)((double)(l + GUI.access$200(GUI.this) / 2) + (Math.cos(d) * (double)GUI.access$200(GUI.this)) / 2D);
            i4 = (int)((double)(i3 + GUI.access$100(GUI.this) / 2) - (Math.sin(d) * (double)GUI.access$300(GUI.this)) / 2D);
            k4 = (int)((double)(k3 + GUI.access$200(GUI.this) / 2) - (Math.cos(d) * (double)GUI.access$200(GUI.this)) / 2D);
            drawArrow(g, i4, k4, i2, k2);
        }

        if (j == 1) {
            FastVector fastvector = m_BayesNet.getChildren(i);
            for (int l1 = 0; l1 < fastvector.size(); l1++) {
                int j2 = ((Integer)fastvector.elementAt(l1)).intValue();
                int l2 = k + GUI.access$100(GUI.this) / 2;
                int j3 = l;
                int l3 = m_BayesNet.getPositionX(j2);
                int j4 = m_BayesNet.getPositionY(j2);
                int l4 = l3 + GUI.access$100(GUI.this) / 2;
                int i5 = j4 + GUI.access$200(GUI.this);
                double d1 = Math.atan2(((double)(l4 - l2) + 0.0D) * (double)GUI.access$200(GUI.this), ((double)(i5 - j3) + 0.0D) * (double)GUI.access$300(GUI.this));
                l2 = (int)((double)(k + GUI.access$100(GUI.this) / 2) + (Math.sin(d1) * (double)GUI.access$300(GUI.this)) / 2D);
                j3 = (int)((double)(l + GUI.access$200(GUI.this) / 2) + (Math.cos(d1) * (double)GUI.access$200(GUI.this)) / 2D);
                l4 = (int)((double)(l3 + GUI.access$100(GUI.this) / 2) - (Math.sin(d1) * (double)GUI.access$300(GUI.this)) / 2D);
                i5 = (int)((double)(j4 + GUI.access$200(GUI.this) / 2) - (Math.cos(d1) * (double)GUI.access$200(GUI.this)) / 2D);
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
        graphics2d.scale(GUI.access$000(GUI.this), GUI.access$000(GUI.this));
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

        double d2 = GUI.access$000(GUI.this);
        j += GUI.access$100(GUI.this) + 100;
        if (d1 / (double)j < d / (double)k) {
            GUI.access$002(GUI.this, d1 / (double)j);
        } else {
            GUI.access$002(GUI.this, d / (double)k);
        }
        paint(graphics2d);
        GUI.access$002(GUI.this, d2);
        return 0;
    }

    public r.JunctionTreeNode() {
        this$0 = GUI.this;
        super();
        m_nClique = 1;
        addMouseListener(new izerMouseListener(GUI.this));
        addMouseMotionListener(new izerMouseMotionListener(GUI.this));
        setToolTipText("");
    }

    // Unreferenced inner class weka/classifiers/bayes/net/GUI$1

/* anonymous class */
    class GUI._cls1
        implements ActionListener {

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
                    GUI.access$002(GUI.this, (double)i / 100D);
                }
                jtextfield.setText((new StringBuilder()).append((int)(GUI.access$000(GUI.this) * 100D)).append("%").toString());
                if (GUI.access$000(GUI.this) > 0.10000000000000001D) {
                    if (!a_zoomout.isEnabled()) {
                        a_zoomout.setEnabled(true);
                    }
                } else {
                    a_zoomout.setEnabled(false);
                }
                if (GUI.access$000(GUI.this) < 9.9900000000000002D) {
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
                jtextfield.setText((new StringBuilder()).append(GUI.access$000(GUI.this) * 100D).append("%").toString());
            }
        }

             {
                this$0 = GUI.this;
                super();
            }
    }

}
