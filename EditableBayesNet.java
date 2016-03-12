// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.io.PrintStream;
import java.io.Serializable;
import java.io.StringReader;
import java.util.StringTokenizer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.estimate.DiscreteEstimatorBayes;
import weka.core.*;
import weka.estimators.Estimator;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Reorder;

// Referenced classes of package weka.classifiers.bayes.net:
//            ParentSet, BIFReader

public class EditableBayesNet extends BayesNet {
    class PasteAction extends UndoAction {

        static final long serialVersionUID = 1L;
        int m_nBase;
        String m_sXML;
        final EditableBayesNet this$0;

        public void undo() {
            try {
                for (int i = getNrOfNodes() - 1; i >= m_nBase; i--) {
                    deleteNode(i);
                }

            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        public void redo() {
            try {
                paste(m_sXML, 1);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        PasteAction(String s, int i) {
            this$0 = EditableBayesNet.this;
            super();
            m_sXML = s;
            m_nBase = i;
        }
    }

    class LayoutGraphAction extends UndoAction {

        static final long serialVersionUID = 1L;
        FastVector m_nPosX;
        FastVector m_nPosY;
        FastVector m_nPosX2;
        FastVector m_nPosY2;
        final EditableBayesNet this$0;

        public void undo() {
            for (int i = 0; i < m_nPosX.size(); i++) {
                setPosition(i, ((Integer)m_nPosX.elementAt(i)).intValue(), ((Integer)m_nPosY.elementAt(i)).intValue());
            }

        }

        public void redo() {
            for (int i = 0; i < m_nPosX.size(); i++) {
                setPosition(i, ((Integer)m_nPosX2.elementAt(i)).intValue(), ((Integer)m_nPosY2.elementAt(i)).intValue());
            }

        }

        LayoutGraphAction(FastVector fastvector, FastVector fastvector1) {
            this$0 = EditableBayesNet.this;
            super();
            m_nPosX = new FastVector(fastvector.size());
            m_nPosY = new FastVector(fastvector.size());
            m_nPosX2 = new FastVector(fastvector.size());
            m_nPosY2 = new FastVector(fastvector.size());
            for (int i = 0; i < fastvector.size(); i++) {
                m_nPosX.addElement(m_nPositionX.elementAt(i));
                m_nPosY.addElement(m_nPositionY.elementAt(i));
                m_nPosX2.addElement(fastvector.elementAt(i));
                m_nPosY2.addElement(fastvector1.elementAt(i));
            }

        }
    }

    class SetGroupPositionAction extends UndoAction {

        static final long serialVersionUID = 1L;
        FastVector m_nodes;
        int m_dX;
        int m_dY;
        final EditableBayesNet this$0;

        public void undo() {
            for (int i = 0; i < m_nodes.size(); i++) {
                int j = ((Integer)m_nodes.elementAt(i)).intValue();
                setPosition(j, getPositionX(j) - m_dX, getPositionY(j) - m_dY);
            }

        }

        public void redo() {
            for (int i = 0; i < m_nodes.size(); i++) {
                int j = ((Integer)m_nodes.elementAt(i)).intValue();
                setPosition(j, getPositionX(j) + m_dX, getPositionY(j) + m_dY);
            }

        }

        public void setUndoPosition(int i, int j) {
            m_dX += i;
            m_dY += j;
        }

        SetGroupPositionAction(FastVector fastvector, int i, int j) {
            this$0 = EditableBayesNet.this;
            super();
            m_nodes = new FastVector(fastvector.size());
            for (int k = 0; k < fastvector.size(); k++) {
                m_nodes.addElement(fastvector.elementAt(k));
            }

            m_dX = i;
            m_dY = j;
        }
    }

    class SetPositionAction extends UndoAction {

        static final long serialVersionUID = 1L;
        int m_nTargetNode;
        int m_nX;
        int m_nY;
        int m_nX2;
        int m_nY2;
        final EditableBayesNet this$0;

        public void undo() {
            setPosition(m_nTargetNode, m_nX, m_nY);
        }

        public void redo() {
            setPosition(m_nTargetNode, m_nX2, m_nY2);
        }

        public void setUndoPosition(int i, int j) {
            m_nX2 = i;
            m_nY2 = j;
        }

        SetPositionAction(int i, int j, int k) {
            this$0 = EditableBayesNet.this;
            super();
            m_nTargetNode = i;
            m_nX2 = j;
            m_nY2 = k;
            m_nX = getPositionX(i);
            m_nY = getPositionY(i);
        }
    }

    class spaceVerticalAction extends alignAction {

        static final long serialVersionUID = 1L;
        final EditableBayesNet this$0;

        public void redo() {
            try {
                spaceVertical(m_nodes);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        public String getUndoMsg() {
            return (new StringBuilder()).append("Returning ").append(m_nodes.size()).append(" from spaceng vertically.").toString();
        }

        public String getRedoMsg() {
            return (new StringBuilder()).append("Spaceng ").append(m_nodes.size()).append(" nodes vertically.").toString();
        }

        public spaceVerticalAction(FastVector fastvector) {
            this$0 = EditableBayesNet.this;
            super(fastvector);
        }
    }

    class spaceHorizontalAction extends alignAction {

        static final long serialVersionUID = 1L;
        final EditableBayesNet this$0;

        public void redo() {
            try {
                spaceHorizontal(m_nodes);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        public String getUndoMsg() {
            return (new StringBuilder()).append("Returning ").append(m_nodes.size()).append(" from spaceing horizontally.").toString();
        }

        public String getRedoMsg() {
            return (new StringBuilder()).append("spaceing ").append(m_nodes.size()).append(" nodes horizontally.").toString();
        }

        public spaceHorizontalAction(FastVector fastvector) {
            this$0 = EditableBayesNet.this;
            super(fastvector);
        }
    }

    class centerVerticalAction extends alignAction {

        static final long serialVersionUID = 1L;
        final EditableBayesNet this$0;

        public void redo() {
            try {
                centerVertical(m_nodes);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        public String getUndoMsg() {
            return (new StringBuilder()).append("Returning ").append(m_nodes.size()).append(" from centering vertically.").toString();
        }

        public String getRedoMsg() {
            return (new StringBuilder()).append("Centering ").append(m_nodes.size()).append(" nodes vertically.").toString();
        }

        public centerVerticalAction(FastVector fastvector) {
            this$0 = EditableBayesNet.this;
            super(fastvector);
        }
    }

    class centerHorizontalAction extends alignAction {

        static final long serialVersionUID = 1L;
        final EditableBayesNet this$0;

        public void redo() {
            try {
                centerHorizontal(m_nodes);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        public String getUndoMsg() {
            return (new StringBuilder()).append("Returning ").append(m_nodes.size()).append(" from centering horizontally.").toString();
        }

        public String getRedoMsg() {
            return (new StringBuilder()).append("Centering ").append(m_nodes.size()).append(" nodes horizontally.").toString();
        }

        public centerHorizontalAction(FastVector fastvector) {
            this$0 = EditableBayesNet.this;
            super(fastvector);
        }
    }

    class alignBottomAction extends alignAction {

        static final long serialVersionUID = 1L;
        final EditableBayesNet this$0;

        public void redo() {
            try {
                alignBottom(m_nodes);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        public String getUndoMsg() {
            return (new StringBuilder()).append("Returning ").append(m_nodes.size()).append(" from aliging nodes to the bottom.").toString();
        }

        public String getRedoMsg() {
            return (new StringBuilder()).append("Aligning ").append(m_nodes.size()).append(" nodes to the bottom.").toString();
        }

        public alignBottomAction(FastVector fastvector) {
            this$0 = EditableBayesNet.this;
            super(fastvector);
        }
    }

    class alignTopAction extends alignAction {

        static final long serialVersionUID = 1L;
        final EditableBayesNet this$0;

        public void redo() {
            try {
                alignTop(m_nodes);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        public String getUndoMsg() {
            return (new StringBuilder()).append("Returning ").append(m_nodes.size()).append(" from aliging nodes to the top.").toString();
        }

        public String getRedoMsg() {
            return (new StringBuilder()).append("Aligning ").append(m_nodes.size()).append(" nodes to the top.").toString();
        }

        public alignTopAction(FastVector fastvector) {
            this$0 = EditableBayesNet.this;
            super(fastvector);
        }
    }

    class alignRightAction extends alignAction {

        static final long serialVersionUID = 1L;
        final EditableBayesNet this$0;

        public void redo() {
            try {
                alignRight(m_nodes);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        public String getUndoMsg() {
            return (new StringBuilder()).append("Returning ").append(m_nodes.size()).append(" from aliging nodes to the right.").toString();
        }

        public String getRedoMsg() {
            return (new StringBuilder()).append("Aligning ").append(m_nodes.size()).append(" nodes to the right.").toString();
        }

        public alignRightAction(FastVector fastvector) {
            this$0 = EditableBayesNet.this;
            super(fastvector);
        }
    }

    class alignLeftAction extends alignAction {

        static final long serialVersionUID = 1L;
        final EditableBayesNet this$0;

        public void redo() {
            try {
                alignLeft(m_nodes);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        public String getUndoMsg() {
            return (new StringBuilder()).append("Returning ").append(m_nodes.size()).append(" from aliging nodes to the left.").toString();
        }

        public String getRedoMsg() {
            return (new StringBuilder()).append("Aligning ").append(m_nodes.size()).append(" nodes to the left.").toString();
        }

        public alignLeftAction(FastVector fastvector) {
            this$0 = EditableBayesNet.this;
            super(fastvector);
        }
    }

    class alignAction extends UndoAction {

        static final long serialVersionUID = 1L;
        FastVector m_nodes;
        FastVector m_posX;
        FastVector m_posY;
        final EditableBayesNet this$0;

        public void undo() {
            try {
                for (int i = 0; i < m_nodes.size(); i++) {
                    int j = ((Integer)m_nodes.elementAt(i)).intValue();
                    setPosition(j, ((Integer)m_posX.elementAt(i)).intValue(), ((Integer)m_posY.elementAt(i)).intValue());
                }

            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        alignAction(FastVector fastvector) {
            this$0 = EditableBayesNet.this;
            super();
            m_nodes = new FastVector(fastvector.size());
            m_posX = new FastVector(fastvector.size());
            m_posY = new FastVector(fastvector.size());
            for (int i = 0; i < fastvector.size(); i++) {
                int j = ((Integer)fastvector.elementAt(i)).intValue();
                m_nodes.addElement(Integer.valueOf(j));
                m_posX.addElement(Integer.valueOf(getPositionX(j)));
                m_posY.addElement(Integer.valueOf(getPositionY(j)));
            }

        }
    }

    class DelValueAction extends UndoAction {

        static final long serialVersionUID = 1L;
        int m_nTargetNode;
        String m_sValue;
        Estimator m_CPT[];
        FastVector m_children;
        Estimator m_childAtts[][];
        Attribute m_att;
        final EditableBayesNet this$0;

        public void undo() {
            try {
                m_Instances.insertAttributeAt(m_att, m_nTargetNode);
                SerializedObject serializedobject = new SerializedObject(m_CPT);
                m_Distributions[m_nTargetNode] = (Estimator[])(Estimator[])serializedobject.getObject();
                for (int i = 0; i < m_children.size(); i++) {
                    int j = ((Integer)m_children.elementAt(i)).intValue();
                    m_Instances.insertAttributeAt(m_att, m_nTargetNode);
                    SerializedObject serializedobject1 = new SerializedObject(m_childAtts[i]);
                    m_Distributions[j] = (Estimator[])(Estimator[])serializedobject1.getObject();
                }

            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        public void redo() {
            try {
                delNodeValue(m_nTargetNode, m_sValue);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        public String getUndoMsg() {
            return (new StringBuilder()).append("Value ").append(m_sValue).append(" added to node ").append(getNodeName(m_nTargetNode)).toString();
        }

        public String getRedoMsg() {
            return (new StringBuilder()).append("Value ").append(m_sValue).append(" removed from node ").append(getNodeName(m_nTargetNode)).toString();
        }

        DelValueAction(int i, String s) {
            this$0 = EditableBayesNet.this;
            super();
            try {
                m_nTargetNode = i;
                m_sValue = s;
                m_att = m_Instances.attribute(i);
                SerializedObject serializedobject = new SerializedObject(m_Distributions[i]);
                m_CPT = (Estimator[])(Estimator[])serializedobject.getObject();
                m_children = new FastVector();
                for (int j = 0; j < getNrOfNodes(); j++) {
                    if (EditableBayesNet.this.this$0[j].contains(i)) {
                        m_children.addElement(Integer.valueOf(j));
                    }
                }

                m_childAtts = new Estimator[m_children.size()][];
                for (int k = 0; k < m_children.size(); k++) {
                    int l = ((Integer)m_children.elementAt(k)).intValue();
                    m_childAtts[k] = m_Distributions[l];
                }

            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    class AddValueAction extends UndoAction {

        static final long serialVersionUID = 1L;
        int m_nTargetNode;
        String m_sValue;
        final EditableBayesNet this$0;

        public void undo() {
            try {
                delNodeValue(m_nTargetNode, m_sValue);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        public void redo() {
            addNodeValue(m_nTargetNode, m_sValue);
        }

        public String getUndoMsg() {
            return (new StringBuilder()).append("Value ").append(m_sValue).append(" removed from node ").append(getNodeName(m_nTargetNode)).toString();
        }

        public String getRedoMsg() {
            return (new StringBuilder()).append("Value ").append(m_sValue).append(" added to node ").append(getNodeName(m_nTargetNode)).toString();
        }

        AddValueAction(int i, String s) {
            this$0 = EditableBayesNet.this;
            super();
            m_nTargetNode = i;
            m_sValue = s;
        }
    }

    class RenameValueAction extends RenameAction {

        static final long serialVersionUID = 1L;
        final EditableBayesNet this$0;

        public void undo() {
            renameNodeValue(m_nTargetNode, m_sNewName, m_sOldName);
        }

        public void redo() {
            renameNodeValue(m_nTargetNode, m_sOldName, m_sNewName);
        }

        public String getUndoMsg() {
            return (new StringBuilder()).append("Value of node ").append(getNodeName(m_nTargetNode)).append(" changed from ").append(m_sNewName).append(" to ").append(m_sOldName).toString();
        }

        public String getRedoMsg() {
            return (new StringBuilder()).append("Value of node ").append(getNodeName(m_nTargetNode)).append(" changed from ").append(m_sOldName).append(" to ").append(m_sNewName).toString();
        }

        RenameValueAction(int i, String s, String s1) {
            this$0 = EditableBayesNet.this;
            super(i, s, s1);
        }
    }

    class RenameAction extends UndoAction {

        static final long serialVersionUID = 1L;
        int m_nTargetNode;
        String m_sNewName;
        String m_sOldName;
        final EditableBayesNet this$0;

        public void undo() {
            setNodeName(m_nTargetNode, m_sOldName);
        }

        public void redo() {
            setNodeName(m_nTargetNode, m_sNewName);
        }

        RenameAction(int i, String s, String s1) {
            this$0 = EditableBayesNet.this;
            super();
            m_nTargetNode = i;
            m_sNewName = s1;
            m_sOldName = s;
        }
    }

    class SetDistributionAction extends UndoAction {

        static final long serialVersionUID = 1L;
        int m_nTargetNode;
        Estimator m_CPT[];
        double m_P[][];
        final EditableBayesNet this$0;

        public void undo() {
            try {
                SerializedObject serializedobject = new SerializedObject(m_CPT);
                m_Distributions[m_nTargetNode] = (Estimator[])(Estimator[])serializedobject.getObject();
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        public void redo() {
            try {
                setDistribution(m_nTargetNode, m_P);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        public String getUndoMsg() {
            return (new StringBuilder()).append("Distribution of node ").append(getNodeName(m_nTargetNode)).append(" changed").toString();
        }

        public String getRedoMsg() {
            return (new StringBuilder()).append("Distribution of node ").append(getNodeName(m_nTargetNode)).append(" changed").toString();
        }

        SetDistributionAction(int i, double ad[][]) {
            this$0 = EditableBayesNet.this;
            super();
            try {
                m_nTargetNode = i;
                SerializedObject serializedobject = new SerializedObject(m_Distributions[i]);
                m_CPT = (Estimator[])(Estimator[])serializedobject.getObject();
                m_P = ad;
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    class DeleteArcAction extends UndoAction {

        static final long serialVersionUID = 1L;
        int m_nParents[];
        int m_nChild;
        int m_nParent;
        Estimator m_CPT[];
        final EditableBayesNet this$0;

        public void undo() {
            try {
                SerializedObject serializedobject = new SerializedObject(m_CPT);
                m_Distributions[m_nChild] = (Estimator[])(Estimator[])serializedobject.getObject();
                ParentSet parentset = new ParentSet();
                for (int i = 0; i < m_nParents.length; i++) {
                    parentset.addParent(m_nParents[i], m_Instances);
                }

                EditableBayesNet.this.this$0[m_nChild] = parentset;
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        public void redo() {
            try {
                deleteArc(m_nParent, m_nChild);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        DeleteArcAction(int i, int j) {
            this$0 = EditableBayesNet.this;
            super();
            try {
                m_nChild = j;
                m_nParent = i;
                m_nParents = new int[getNrOfParents(j)];
                for (int k = 0; k < m_nParents.length; k++) {
                    m_nParents[k] = getParent(j, k);
                }

                SerializedObject serializedobject = new SerializedObject(m_Distributions[j]);
                m_CPT = (Estimator[])(Estimator[])serializedobject.getObject();
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    class AddArcAction extends UndoAction {

        static final long serialVersionUID = 1L;
        FastVector m_children;
        int m_nParent;
        Estimator m_CPT[][];
        final EditableBayesNet this$0;

        public void undo() {
            try {
                for (int i = 0; i < m_children.size(); i++) {
                    int j = ((Integer)m_children.elementAt(i)).intValue();
                    deleteArc(m_nParent, j);
                    SerializedObject serializedobject = new SerializedObject(m_CPT[i]);
                    m_Distributions[j] = (Estimator[])(Estimator[])serializedobject.getObject();
                }

            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        public void redo() {
            try {
                for (int i = 0; i < m_children.size(); i++) {
                    int j = ((Integer)m_children.elementAt(i)).intValue();
                    addArc(m_nParent, j);
                }

            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        AddArcAction(int i, int j) {
            this$0 = EditableBayesNet.this;
            super();
            try {
                m_nParent = i;
                m_children = new FastVector();
                m_children.addElement(Integer.valueOf(j));
                SerializedObject serializedobject = new SerializedObject(m_Distributions[j]);
                m_CPT = new Estimator[1][];
                m_CPT[0] = (Estimator[])(Estimator[])serializedobject.getObject();
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        AddArcAction(int i, FastVector fastvector) {
            this$0 = EditableBayesNet.this;
            super();
            try {
                m_nParent = i;
                m_children = new FastVector();
                m_CPT = new Estimator[fastvector.size()][];
                for (int j = 0; j < fastvector.size(); j++) {
                    int k = ((Integer)fastvector.elementAt(j)).intValue();
                    m_children.addElement(Integer.valueOf(k));
                    SerializedObject serializedobject = new SerializedObject(m_Distributions[k]);
                    m_CPT[j] = (Estimator[])(Estimator[])serializedobject.getObject();
                }

            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    class DeleteSelectionAction extends UndoAction {

        static final long serialVersionUID = 1L;
        FastVector m_nodes;
        Attribute m_att[];
        Estimator m_CPT[][];
        ParentSet m_ParentSet[];
        FastVector m_deleteArcActions;
        int m_nPosX[];
        int m_nPosY[];
        final EditableBayesNet this$0;

        public void undo() {
            try {
                for (int i = 0; i < m_nodes.size(); i++) {
                    int k = ((Integer)m_nodes.elementAt(i)).intValue();
                    m_Instances.insertAttributeAt(m_att[i], k);
                }

                int j = m_Instances.numAttributes();
                ParentSet aparentset[] = new ParentSet[j];
                int ai[] = new int[j];
                for (int l = 0; l < j; l++) {
                    ai[l] = l;
                }

                for (int i1 = m_nodes.size() - 1; i1 >= 0; i1--) {
                    int k1 = ((Integer)m_nodes.elementAt(i1)).intValue();
                    for (int i2 = k1; i2 < j - 1; i2++) {
                        ai[i2] = ai[i2 + 1];
                    }

                }

                int j1 = 0;
                for (int l1 = 0; l1 < j; l1++) {
                    if (j1 < m_nodes.size() && (Integer)m_nodes.elementAt(j1) == Integer.valueOf(l1)) {
                        SerializedObject serializedobject = new SerializedObject(m_ParentSet[j1]);
                        aparentset[l1] = (ParentSet)serializedobject.getObject();
                        j1++;
                        continue;
                    }
                    aparentset[l1] = EditableBayesNet.this.this$0[l1 - j1];
                    for (int j2 = 0; j2 < aparentset[l1].getNrOfParents(); j2++) {
                        int j3 = aparentset[l1].getParent(j2);
                        aparentset[l1].SetParent(j2, ai[j3]);
                    }

                }

                aparentset.this$0 = i;
                Estimator aestimator[][] = new Estimator[j][];
                j1 = 0;
                for (int k2 = 0; k2 < j; k2++) {
                    if (j1 < m_nodes.size() && (Integer)m_nodes.elementAt(j1) == Integer.valueOf(k2)) {
                        SerializedObject serializedobject1 = new SerializedObject(m_CPT[j1]);
                        aestimator[k2] = (Estimator[])(Estimator[])serializedobject1.getObject();
                        j1++;
                    } else {
                        aestimator[k2] = m_Distributions[k2 - j1];
                    }
                }

                m_Distributions = aestimator;
                for (int l2 = 0; l2 < m_nodes.size(); l2++) {
                    int k3 = ((Integer)m_nodes.elementAt(l2)).intValue();
                    m_nPositionX.insertElementAt(Integer.valueOf(m_nPosX[l2]), k3);
                    m_nPositionY.insertElementAt(Integer.valueOf(m_nPosY[l2]), k3);
                    m_nEvidence.insertElementAt(Integer.valueOf(-1), k3);
                    m_fMarginP.insertElementAt(new double[getCardinality(k3)], k3);
                }

                for (int i3 = 0; i3 < m_deleteArcActions.size(); i3++) {
                    DeleteArcAction deletearcaction = (DeleteArcAction)m_deleteArcActions.elementAt(i3);
                    deletearcaction.undo();
                }

            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        public void redo() {
            try {
                for (int i = m_nodes.size() - 1; i >= 0; i--) {
                    int j = ((Integer)m_nodes.elementAt(i)).intValue();
                    deleteNode(j);
                }

            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        public DeleteSelectionAction(FastVector fastvector) {
            this$0 = EditableBayesNet.this;
            super();
            m_nodes = new FastVector();
            int i = fastvector.size();
            m_att = new Attribute[i];
            m_CPT = new Estimator[i][];
            m_ParentSet = new ParentSet[i];
            m_nPosX = new int[i];
            m_nPosY = new int[i];
            m_deleteArcActions = new FastVector();
            for (int j = 0; j < fastvector.size(); j++) {
                int k = ((Integer)fastvector.elementAt(j)).intValue();
                m_nodes.addElement(Integer.valueOf(k));
                m_att[j] = m_Instances.attribute(k);
                try {
                    SerializedObject serializedobject = new SerializedObject(m_Distributions[k]);
                    m_CPT[j] = (Estimator[])(Estimator[])serializedobject.getObject();
                    serializedobject = new SerializedObject(EditableBayesNet.this.this$0[k]);
                    m_ParentSet[j] = (ParentSet)serializedobject.getObject();
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                m_nPosX[j] = getPositionX(k);
                m_nPosY[j] = getPositionY(k);
                for (int l = 0; l < getNrOfNodes(); l++) {
                    if (!fastvector.contains(Integer.valueOf(l)) && EditableBayesNet.this.this$0[l].contains(k)) {
                        m_deleteArcActions.addElement(new DeleteArcAction(k, l));
                    }
                }

            }

        }
    }

    class DeleteNodeAction extends UndoAction {

        static final long serialVersionUID = 1L;
        int m_nTargetNode;
        Attribute m_att;
        Estimator m_CPT[];
        ParentSet m_ParentSet;
        FastVector m_deleteArcActions;
        int m_nPosX;
        int m_nPosY;
        final EditableBayesNet this$0;

        public void undo() {
            try {
                m_Instances.insertAttributeAt(m_att, m_nTargetNode);
                int i = m_Instances.numAttributes();
                ParentSet aparentset[] = new ParentSet[i];
                int j = 0;
                for (int k = 0; k < i; k++) {
                    if (k == m_nTargetNode) {
                        SerializedObject serializedobject = new SerializedObject(m_ParentSet);
                        aparentset[k] = (ParentSet)serializedobject.getObject();
                        j = 1;
                        continue;
                    }
                    aparentset[k] = EditableBayesNet.this.this$0[k - j];
                    for (int l = 0; l < aparentset[k].getNrOfParents(); l++) {
                        int k1 = aparentset[k].getParent(l);
                        if (k1 >= m_nTargetNode) {
                            aparentset[k].SetParent(l, k1 + 1);
                        }
                    }

                }

                aparentset.this$0 = <no variable>;
                Estimator aestimator[][] = new Estimator[i][];
                j = 0;
                for (int i1 = 0; i1 < i; i1++) {
                    if (i1 == m_nTargetNode) {
                        SerializedObject serializedobject1 = new SerializedObject(m_CPT);
                        aestimator[i1] = (Estimator[])(Estimator[])serializedobject1.getObject();
                        j = 1;
                    } else {
                        aestimator[i1] = m_Distributions[i1 - j];
                    }
                }

                m_Distributions = aestimator;
                for (int j1 = 0; j1 < m_deleteArcActions.size(); j1++) {
                    DeleteArcAction deletearcaction = (DeleteArcAction)m_deleteArcActions.elementAt(j1);
                    deletearcaction.undo();
                }

                m_nPositionX.insertElementAt(Integer.valueOf(m_nPosX), m_nTargetNode);
                m_nPositionY.insertElementAt(Integer.valueOf(m_nPosY), m_nTargetNode);
                m_nEvidence.insertElementAt(Integer.valueOf(-1), m_nTargetNode);
                m_fMarginP.insertElementAt(new double[getCardinality(m_nTargetNode)], m_nTargetNode);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        public void redo() {
            try {
                deleteNode(m_nTargetNode);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        DeleteNodeAction(int i) {
            this$0 = EditableBayesNet.this;
            super();
            m_nTargetNode = i;
            m_att = m_Instances.attribute(i);
            try {
                SerializedObject serializedobject = new SerializedObject(m_Distributions[i]);
                m_CPT = (Estimator[])(Estimator[])serializedobject.getObject();
                serializedobject = new SerializedObject(EditableBayesNet.this.this$0[i]);
                m_ParentSet = (ParentSet)serializedobject.getObject();
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
            m_deleteArcActions = new FastVector();
            for (int j = 0; j < getNrOfNodes(); j++) {
                if (EditableBayesNet.this.this$0[j].contains(i)) {
                    m_deleteArcActions.addElement(new DeleteArcAction(i, j));
                }
            }

            m_nPosX = getPositionX(m_nTargetNode);
            m_nPosY = getPositionY(m_nTargetNode);
        }
    }

    class AddNodeAction extends UndoAction {

        static final long serialVersionUID = 1L;
        String m_sName;
        int m_nPosX;
        int m_nPosY;
        int m_nCardinality;
        final EditableBayesNet this$0;

        public void undo() {
            try {
                deleteNode(getNrOfNodes() - 1);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        public void redo() {
            try {
                addNode(m_sName, m_nCardinality, m_nPosX, m_nPosY);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        AddNodeAction(String s, int i, int j, int k) {
            this$0 = EditableBayesNet.this;
            super();
            m_sName = s;
            m_nCardinality = i;
            m_nPosX = j;
            m_nPosY = k;
        }
    }

    class UndoAction
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

        UndoAction() {
            this$0 = EditableBayesNet.this;
            super();
        }
    }


    static final long serialVersionUID = 0xa5a751a1a93dd52L;
    protected FastVector m_nPositionX;
    protected FastVector m_nPositionY;
    protected FastVector m_fMarginP;
    protected FastVector m_nEvidence;
    static final int TEST = 0;
    static final int EXECUTE = 1;
    FastVector m_undoStack;
    int m_nCurrentEditAction;
    int m_nSavedPointer;
    boolean m_bNeedsUndoAction;

    public EditableBayesNet() {
        m_undoStack = new FastVector();
        m_nCurrentEditAction = -1;
        m_nSavedPointer = -1;
        m_bNeedsUndoAction = true;
        m_nEvidence = new FastVector(0);
        m_fMarginP = new FastVector(0);
        m_nPositionX = new FastVector();
        m_nPositionY = new FastVector();
        clearUndoStack();
    }

    public EditableBayesNet(Instances instances) {
        m_undoStack = new FastVector();
        m_nCurrentEditAction = -1;
        m_nSavedPointer = -1;
        m_bNeedsUndoAction = true;
        try {
            if (instances.classIndex() < 0) {
                instances.setClassIndex(instances.numAttributes() - 1);
            }
            m_Instances = normalizeDataSet(instances);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        int i = getNrOfNodes();
        m_ParentSets = new ParentSet[i];
        for (int j = 0; j < i; j++) {
            m_ParentSets[j] = new ParentSet();
        }

        m_Distributions = new Estimator[i][];
        for (int k = 0; k < i; k++) {
            m_Distributions[k] = new Estimator[1];
            m_Distributions[k][0] = new DiscreteEstimatorBayes(getCardinality(k), 0.5D);
        }

        m_nEvidence = new FastVector(i);
        for (int l = 0; l < i; l++) {
            m_nEvidence.addElement(Integer.valueOf(-1));
        }

        m_fMarginP = new FastVector(i);
        for (int i1 = 0; i1 < i; i1++) {
            double ad[] = new double[getCardinality(i1)];
            m_fMarginP.addElement(ad);
        }

        m_nPositionX = new FastVector(i);
        m_nPositionY = new FastVector(i);
        for (int j1 = 0; j1 < i; j1++) {
            m_nPositionX.addElement(Integer.valueOf((j1 % 10) * 50));
            m_nPositionY.addElement(Integer.valueOf((j1 / 10) * 50));
        }

    }

    public EditableBayesNet(BIFReader bifreader) {
        m_undoStack = new FastVector();
        m_nCurrentEditAction = -1;
        m_nSavedPointer = -1;
        m_bNeedsUndoAction = true;
        m_Instances = bifreader.m_Instances;
        m_ParentSets = bifreader.getParentSets();
        m_Distributions = bifreader.getDistributions();
        int i = getNrOfNodes();
        m_nPositionX = new FastVector(i);
        m_nPositionY = new FastVector(i);
        for (int j = 0; j < i; j++) {
            m_nPositionX.addElement(Integer.valueOf(bifreader.m_nPositionX[j]));
            m_nPositionY.addElement(Integer.valueOf(bifreader.m_nPositionY[j]));
        }

        m_nEvidence = new FastVector(i);
        for (int k = 0; k < i; k++) {
            m_nEvidence.addElement(Integer.valueOf(-1));
        }

        m_fMarginP = new FastVector(i);
        for (int l = 0; l < i; l++) {
            double ad[] = new double[getCardinality(l)];
            m_fMarginP.addElement(ad);
        }

        clearUndoStack();
    }

    public EditableBayesNet(boolean flag) {
        m_undoStack = new FastVector();
        m_nCurrentEditAction = -1;
        m_nSavedPointer = -1;
        m_bNeedsUndoAction = true;
        m_nEvidence = new FastVector(0);
        m_fMarginP = new FastVector(0);
        m_nPositionX = new FastVector();
        m_nPositionY = new FastVector();
        clearUndoStack();
        if (flag) {
            m_Instances = new Instances("New Network", new FastVector(0), 0);
        }
    }

    public void setData(Instances instances) throws Exception {
        int ai[] = new int[getNrOfNodes()];
        for (int i = 0; i < getNrOfNodes(); i++) {
            String s = getNodeName(i);
            int j;
            for (j = 0; j < getNrOfNodes() && !s.equals(instances.attribute(j).name()); j++) { }
            if (j >= getNrOfNodes()) {
                throw new Exception((new StringBuilder()).append("Cannot find node named [[[").append(s).append("]]] in the data").toString());
            }
            ai[i] = j;
        }

        Reorder reorder = new Reorder();
        reorder.setAttributeIndicesArray(ai);
        reorder.setInputFormat(instances);
        instances = Filter.useFilter(instances, reorder);
        Instances instances1 = new Instances(m_Instances, 0);
        if (m_DiscretizeFilter == null && m_MissingValuesFilter == null) {
            instances1 = normalizeDataSet(instances);
        } else {
            for (int k = 0; k < instances.numInstances(); k++) {
                instances1.add(normalizeInstance(instances.instance(k)));
            }

        }
        for (int l = 0; l < getNrOfNodes(); l++) {
            if (instances1.attribute(l).numValues() != getCardinality(l)) {
                throw new Exception((new StringBuilder()).append("Number of values of node [[[").append(getNodeName(l)).append("]]] differs in (discretized) dataset.").toString());
            }
        }

        m_Instances = instances1;
    }

    public int getNode2(String s) {
        for (int i = 0; i < m_Instances.numAttributes(); i++) {
            if (m_Instances.attribute(i).name().equals(s)) {
                return i;
            }
        }

        return -1;
    }

    public int getNode(String s) throws Exception {
        int i = getNode2(s);
        if (i < 0) {
            throw new Exception((new StringBuilder()).append("Could not find node [[").append(s).append("]]").toString());
        } else {
            return i;
        }
    }

    public void addNode(String s, int i) throws Exception {
        addNode(s, i, 100 + getNrOfNodes() * 10, 100 + getNrOfNodes() * 10);
    }

    public void addNode(String s, int i, int j, int k) throws Exception {
        if (getNode2(s) >= 0) {
            addNode((new StringBuilder()).append(s).append("x").toString(), i);
            return;
        }
        FastVector fastvector = new FastVector(i);
        for (int l = 0; l < i; l++) {
            fastvector.addElement((new StringBuilder()).append("Value").append(l + 1).toString());
        }

        Attribute attribute = new Attribute(s, fastvector);
        m_Instances.insertAttributeAt(attribute, m_Instances.numAttributes());
        int i1 = m_Instances.numAttributes();
        ParentSet aparentset[] = new ParentSet[i1];
        for (int j1 = 0; j1 < i1 - 1; j1++) {
            aparentset[j1] = m_ParentSets[j1];
        }

        aparentset[i1 - 1] = new ParentSet();
        m_ParentSets = aparentset;
        Estimator aestimator[][] = new Estimator[i1][];
        for (int k1 = 0; k1 < i1 - 1; k1++) {
            aestimator[k1] = m_Distributions[k1];
        }

        aestimator[i1 - 1] = new Estimator[1];
        aestimator[i1 - 1][0] = new DiscreteEstimatorBayes(i, 0.5D);
        m_Distributions = aestimator;
        m_nPositionX.addElement(Integer.valueOf(j));
        m_nPositionY.addElement(Integer.valueOf(k));
        m_nEvidence.addElement(Integer.valueOf(-1));
        double ad[] = new double[i];
        for (int l1 = 0; l1 < i; l1++) {
            ad[l1] = 1.0D / (double)i;
        }

        m_fMarginP.addElement(ad);
        if (m_bNeedsUndoAction) {
            addUndoAction(new AddNodeAction(s, i, j, k));
        }
    }

    public void deleteNode(String s) throws Exception {
        int i = getNode(s);
        deleteNode(i);
    }

    public void deleteNode(int i) throws Exception {
        if (m_bNeedsUndoAction) {
            addUndoAction(new DeleteNodeAction(i));
        }
        int j = m_Instances.numAttributes() - 1;
        int k = m_Instances.attribute(i).numValues();
        Estimator aestimator[][] = new Estimator[j][];
        for (int l = 0; l < j; l++) {
            int i1 = l;
            if (l >= i) {
                i1++;
            }
            Estimator aestimator1[] = m_Distributions[i1];
            if (m_ParentSets[i1].contains(i)) {
                int l1 = m_ParentSets[i1].getCardinalityOfParents();
                l1 /= k;
                Estimator aestimator2[] = new Estimator[l1];
                for (int j2 = 0; j2 < l1; j2++) {
                    aestimator2[j2] = aestimator1[j2];
                }

                aestimator1 = aestimator2;
            }
            aestimator[l] = aestimator1;
        }

        m_Distributions = aestimator;
        ParentSet aparentset[] = new ParentSet[j];
        for (int j1 = 0; j1 < j; j1++) {
            int k1 = j1;
            if (j1 >= i) {
                k1++;
            }
            ParentSet parentset = m_ParentSets[k1];
            parentset.deleteParent(i, m_Instances);
            for (int i2 = 0; i2 < parentset.getNrOfParents(); i2++) {
                int k2 = parentset.getParent(i2);
                if (k2 > i) {
                    parentset.SetParent(i2, k2 - 1);
                }
            }

            aparentset[j1] = parentset;
        }

        m_ParentSets = aparentset;
        m_Instances.setClassIndex(-1);
        m_Instances.deleteAttributeAt(i);
        m_Instances.setClassIndex(j - 1);
        m_nPositionX.removeElementAt(i);
        m_nPositionY.removeElementAt(i);
        m_nEvidence.removeElementAt(i);
        m_fMarginP.removeElementAt(i);
    }

    public void deleteSelection(FastVector fastvector) {
        for (int i = 0; i < fastvector.size(); i++) {
            for (int j = i + 1; j < fastvector.size(); j++) {
                if (((Integer)fastvector.elementAt(i)).intValue() > ((Integer)fastvector.elementAt(j)).intValue()) {
                    int l = ((Integer)fastvector.elementAt(i)).intValue();
                    fastvector.setElementAt(fastvector.elementAt(j), i);
                    fastvector.setElementAt(Integer.valueOf(l), j);
                }
            }

        }

        if (m_bNeedsUndoAction) {
            addUndoAction(new DeleteSelectionAction(fastvector));
        }
        boolean flag = m_bNeedsUndoAction;
        m_bNeedsUndoAction = false;
        try {
            for (int k = fastvector.size() - 1; k >= 0; k--) {
                deleteNode(((Integer)fastvector.elementAt(k)).intValue());
            }

        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        m_bNeedsUndoAction = flag;
    }

    FastVector selectElements(Node node, String s) throws Exception {
        NodeList nodelist = node.getChildNodes();
        FastVector fastvector = new FastVector();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node node1 = nodelist.item(i);
            if (node1.getNodeType() == 1 && node1.getNodeName().equals(s)) {
                fastvector.addElement(node1);
            }
        }

        return fastvector;
    }

    public String getContent(Element element) {
        String s = "";
        NodeList nodelist = element.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node node = nodelist.item(i);
            if (node.getNodeType() == 3) {
                s = (new StringBuilder()).append(s).append("\n").append(node.getNodeValue()).toString();
            }
        }

        return s;
    }

    Element getDefinition(Document document, String s) throws Exception {
        NodeList nodelist = document.getElementsByTagName("DEFINITION");
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node node = nodelist.item(i);
            FastVector fastvector = selectElements(node, "FOR");
            if (fastvector.size() <= 0) {
                continue;
            }
            Node node1 = (Node)fastvector.elementAt(0);
            if (getContent((Element)node1).trim().equals(s)) {
                return (Element)node;
            }
        }

        throw new Exception((new StringBuilder()).append("Could not find definition for ((").append(s).append("))").toString());
    }

    public void paste(String s) throws Exception {
        try {
            paste(s, 0);
        }
        catch (Exception exception) {
            throw exception;
        }
        paste(s, 1);
    }

    void paste(String s, int i) throws Exception {
        DocumentBuilderFactory documentbuilderfactory = DocumentBuilderFactory.newInstance();
        documentbuilderfactory.setValidating(true);
        Document document = documentbuilderfactory.newDocumentBuilder().parse(new InputSource(new StringReader(s)));
        document.normalize();
        NodeList nodelist = document.getElementsByTagName("VARIABLE");
        FastVector fastvector = new FastVector();
        Instances instances = new Instances(m_Instances, 0);
        int j = instances.numAttributes();
        for (int k = 0; k < nodelist.getLength(); k++) {
            FastVector fastvector2 = selectElements(nodelist.item(k), "OUTCOME");
            int l = fastvector2.size();
            FastVector fastvector3 = new FastVector(l + 1);
            for (int k1 = 0; k1 < l; k1++) {
                Node node = ((Node)fastvector2.elementAt(k1)).getFirstChild();
                String s3 = ((CharacterData)(CharacterData)node).getData();
                if (s3 == null) {
                    s3 = (new StringBuilder()).append("Value").append(k1 + 1).toString();
                }
                fastvector3.addElement(s3);
            }

            FastVector fastvector4 = selectElements(nodelist.item(k), "NAME");
            if (fastvector4.size() == 0) {
                throw new Exception("No name specified for variable");
            }
            String s2 = ((CharacterData)(CharacterData)((Node)fastvector4.elementAt(0)).getFirstChild()).getData();
            fastvector.addElement(s2);
            String s4 = s2;
            if (getNode2(s4) >= 0) {
                s4 = (new StringBuilder()).append("Copy of ").append(s2).toString();
            }
            int j2;
            for (j2 = 2; getNode2(s4) >= 0; j2++) {
                s4 = (new StringBuilder()).append("Copy (").append(j2).append(") of ").append(s2).toString();
            }

            Attribute attribute = new Attribute(s4, fastvector3);
            instances.insertAttributeAt(attribute, instances.numAttributes());
            fastvector2 = selectElements(nodelist.item(k), "PROPERTY");
            l = fastvector2.size();
            int i3 = j2 * 10;
            int k3 = j2 * 10;
            for (int j4 = 0; j4 < l; j4++) {
                Node node2 = ((Node)fastvector2.elementAt(j4)).getFirstChild();
                String s7 = ((CharacterData)(CharacterData)node2).getData();
                if (!s7.startsWith("position")) {
                    continue;
                }
                int l4 = s7.indexOf('(');
                int i5 = s7.indexOf(',');
                int j5 = s7.indexOf(')');
                String s9 = s7.substring(l4 + 1, i5).trim();
                String s10 = s7.substring(i5 + 1, j5).trim();
                try {
                    i3 = Integer.parseInt(s9) + j2 * 10;
                    k3 = Integer.parseInt(s10) + j2 * 10;
                }
                catch (NumberFormatException numberformatexception) {
                    System.err.println((new StringBuilder()).append("Wrong number format in position :(").append(s9).append(",").append(s10).append(")").toString());
                }
            }

            if (i == 1) {
                m_nPositionX.addElement(Integer.valueOf(i3));
                m_nPositionY.addElement(Integer.valueOf(k3));
            }
        }

        Estimator aestimator[][] = new Estimator[j + fastvector.size()][];
        ParentSet aparentset[] = new ParentSet[j + fastvector.size()];
        for (int i1 = 0; i1 < j; i1++) {
            aestimator[i1] = m_Distributions[i1];
            aparentset[i1] = m_ParentSets[i1];
        }

        if (i == 1) {
            m_Instances = instances;
        }
        for (int j1 = 0; j1 < fastvector.size(); j1++) {
            String s1 = (String)fastvector.elementAt(j1);
            Element element = getDefinition(document, s1);
            aparentset[j + j1] = new ParentSet();
            FastVector fastvector1 = selectElements(element, "GIVEN");
            for (int l1 = 0; l1 < fastvector1.size(); l1++) {
                Node node1 = ((Node)fastvector1.elementAt(l1)).getFirstChild();
                String s5 = ((CharacterData)(CharacterData)node1).getData();
                int j3 = -1;
                for (int l3 = 0; l3 < fastvector.size(); l3++) {
                    if (s5.equals((String)fastvector.elementAt(l3))) {
                        j3 = j + l3;
                    }
                }

                if (j3 < 0) {
                    j3 = getNode(s5);
                }
                aparentset[j + j1].addParent(j3, instances);
            }

            int i2 = aparentset[j + j1].getCardinalityOfParents();
            int k2 = instances.attribute(j + j1).numValues();
            aestimator[j + j1] = new Estimator[i2];
            for (int l2 = 0; l2 < i2; l2++) {
                aestimator[j + j1][l2] = new DiscreteEstimatorBayes(k2, 0.0D);
            }

            String s6 = getContent((Element)selectElements(element, "TABLE").elementAt(0));
            s6 = s6.replaceAll("\\n", " ");
            StringTokenizer stringtokenizer = new StringTokenizer(s6.toString());
            for (int i4 = 0; i4 < i2; i4++) {
                DiscreteEstimatorBayes discreteestimatorbayes = (DiscreteEstimatorBayes)aestimator[j + j1][i4];
                for (int k4 = 0; k4 < k2; k4++) {
                    String s8 = stringtokenizer.nextToken();
                    discreteestimatorbayes.addValue(k4, (new Double(s8)).doubleValue());
                }

            }

            if (i == 1) {
                m_nEvidence.insertElementAt(Integer.valueOf(-1), j + j1);
                m_fMarginP.insertElementAt(new double[getCardinality(j + j1)], j + j1);
            }
        }

        if (i == 1) {
            m_Distributions = aestimator;
            m_ParentSets = aparentset;
        }
        if (i == 1 && m_bNeedsUndoAction) {
            addUndoAction(new PasteAction(s, j));
        }
    }

    public void addArc(String s, String s1) throws Exception {
        int i = getNode(s);
        int j = getNode(s1);
        addArc(i, j);
    }

    public void addArc(int i, int j) throws Exception {
        if (m_bNeedsUndoAction) {
            addUndoAction(new AddArcAction(i, j));
        }
        int k = m_ParentSets[j].getCardinalityOfParents();
        m_ParentSets[j].addParent(i, m_Instances);
        int l = m_ParentSets[j].getCardinalityOfParents();
        Estimator aestimator[] = new Estimator[l];
        for (int i1 = 0; i1 < l; i1++) {
            aestimator[i1] = Estimator.clone(m_Distributions[j][i1 % k]);
        }

        m_Distributions[j] = aestimator;
    }

    public void addArc(String s, FastVector fastvector) throws Exception {
        int i = getNode(s);
        if (m_bNeedsUndoAction) {
            addUndoAction(new AddArcAction(i, fastvector));
        }
        boolean flag = m_bNeedsUndoAction;
        m_bNeedsUndoAction = false;
        for (int j = 0; j < fastvector.size(); j++) {
            int k = ((Integer)fastvector.elementAt(j)).intValue();
            addArc(i, k);
        }

        m_bNeedsUndoAction = flag;
    }

    public void deleteArc(String s, String s1) throws Exception {
        int i = getNode(s);
        int j = getNode(s1);
        deleteArc(i, j);
    }

    public void deleteArc(int i, int j) throws Exception {
        if (m_bNeedsUndoAction) {
            addUndoAction(new DeleteArcAction(i, j));
        }
        int k = m_ParentSets[j].getCardinalityOfParents();
        int l = m_Instances.attribute(j).numValues();
        k /= l;
        Estimator aestimator[] = new Estimator[k];
        for (int i1 = 0; i1 < k; i1++) {
            aestimator[i1] = m_Distributions[j][i1];
        }

        m_Distributions[j] = aestimator;
        m_ParentSets[j].deleteParent(i, m_Instances);
    }

    public void setDistribution(String s, double ad[][]) throws Exception {
        int i = getNode(s);
        setDistribution(i, ad);
    }

    public void setDistribution(int i, double ad[][]) throws Exception {
        if (m_bNeedsUndoAction) {
            addUndoAction(new SetDistributionAction(i, ad));
        }
        Estimator aestimator[] = m_Distributions[i];
        for (int j = 0; j < aestimator.length; j++) {
            DiscreteEstimatorBayes discreteestimatorbayes = new DiscreteEstimatorBayes(ad[0].length, 0.0D);
            for (int k = 0; k < discreteestimatorbayes.getNumSymbols(); k++) {
                discreteestimatorbayes.addValue(k, ad[j][k]);
            }

            aestimator[j] = discreteestimatorbayes;
        }

    }

    public double[][] getDistribution(String s) {
        int i = getNode2(s);
        return getDistribution(i);
    }

    public double[][] getDistribution(int i) {
        int j = m_ParentSets[i].getCardinalityOfParents();
        int k = m_Instances.attribute(i).numValues();
        double ad[][] = new double[j][k];
        for (int l = 0; l < j; l++) {
            for (int i1 = 0; i1 < k; i1++) {
                ad[l][i1] = m_Distributions[i][l].getProbability(i1);
            }

        }

        return ad;
    }

    public String[] getValues(String s) {
        int i = getNode2(s);
        return getValues(i);
    }

    public String[] getValues(int i) {
        String as[] = new String[getCardinality(i)];
        for (int j = 0; j < as.length; j++) {
            as[j] = m_Instances.attribute(i).value(j);
        }

        return as;
    }

    public String getValueName(int i, int j) {
        return m_Instances.attribute(i).value(j);
    }

    public void setNodeName(int i, String s) {
        if (m_bNeedsUndoAction) {
            addUndoAction(new RenameAction(i, getNodeName(i), s));
        }
        Attribute attribute = m_Instances.attribute(i);
        int j = attribute.numValues();
        FastVector fastvector = new FastVector(j);
        for (int k = 0; k < j; k++) {
            fastvector.addElement(attribute.value(k));
        }

        replaceAtt(i, s, fastvector);
    }

    public void renameNodeValue(int i, String s, String s1) {
        if (m_bNeedsUndoAction) {
            addUndoAction(new RenameValueAction(i, s, s1));
        }
        Attribute attribute = m_Instances.attribute(i);
        int j = attribute.numValues();
        FastVector fastvector = new FastVector(j);
        for (int k = 0; k < j; k++) {
            if (attribute.value(k).equals(s)) {
                fastvector.addElement(s1);
            } else {
                fastvector.addElement(attribute.value(k));
            }
        }

        replaceAtt(i, attribute.name(), fastvector);
    }

    public void addNodeValue(int i, String s) {
        if (m_bNeedsUndoAction) {
            addUndoAction(new AddValueAction(i, s));
        }
        Attribute attribute = m_Instances.attribute(i);
        int j = attribute.numValues();
        FastVector fastvector = new FastVector(j);
        for (int k = 0; k < j; k++) {
            fastvector.addElement(attribute.value(k));
        }

        fastvector.addElement(s);
        replaceAtt(i, attribute.name(), fastvector);
        Estimator aestimator[] = m_Distributions[i];
        int l = fastvector.size();
        for (int i1 = 0; i1 < aestimator.length; i1++) {
            DiscreteEstimatorBayes discreteestimatorbayes = new DiscreteEstimatorBayes(l, 0.0D);
            for (int k1 = 0; k1 < l - 1; k1++) {
                discreteestimatorbayes.addValue(k1, aestimator[i1].getProbability(k1));
            }

            aestimator[i1] = discreteestimatorbayes;
        }

        for (int j1 = 0; j1 < getNrOfNodes(); j1++) {
            if (!m_ParentSets[j1].contains(i)) {
                continue;
            }
            Estimator aestimator1[] = m_Distributions[j1];
            ParentSet parentset = m_ParentSets[j1];
            int l1 = parentset.getFreshCardinalityOfParents(m_Instances);
            Estimator aestimator2[] = new Estimator[l1];
            int i2 = getCardinality(j1);
            int j2 = parentset.getNrOfParents();
            int ai[] = new int[j2];
            int k2 = 0;
            int l2;
            for (l2 = 0; parentset.getParent(l2) != i; l2++) { }
            for (int i3 = 0; i3 < l1; i3++) {
                DiscreteEstimatorBayes discreteestimatorbayes1 = new DiscreteEstimatorBayes(i2, 0.0D);
                for (int j3 = 0; j3 < i2; j3++) {
                    discreteestimatorbayes1.addValue(j3, aestimator1[k2].getProbability(j3));
                }

                aestimator2[i3] = discreteestimatorbayes1;
                int k3 = 0;
                ai[k3]++;
                do {
                    if (k3 >= j2 || ai[k3] != getCardinality(parentset.getParent(k3))) {
                        break;
                    }
                    ai[k3] = 0;
                    if (++k3 < j2) {
                        ai[k3]++;
                    }
                } while (true);
                if (ai[l2] != l - 1) {
                    k2++;
                }
            }

            m_Distributions[j1] = aestimator2;
        }

    }

    public void delNodeValue(int i, String s) throws Exception {
        if (m_bNeedsUndoAction) {
            addUndoAction(new DelValueAction(i, s));
        }
        Attribute attribute = m_Instances.attribute(i);
        int j = attribute.numValues();
        FastVector fastvector = new FastVector(j);
        int k = -1;
        for (int l = 0; l < j; l++) {
            if (attribute.value(l).equals(s)) {
                k = l;
            } else {
                fastvector.addElement(attribute.value(l));
            }
        }

        if (k < 0) {
            throw new Exception((new StringBuilder()).append("Node ").append(i).append(" does not have value (").append(s).append(")").toString());
        }
        replaceAtt(i, attribute.name(), fastvector);
        Estimator aestimator[] = m_Distributions[i];
        int i1 = fastvector.size();
        for (int j1 = 0; j1 < aestimator.length; j1++) {
            DiscreteEstimatorBayes discreteestimatorbayes = new DiscreteEstimatorBayes(i1, 0.0D);
            double d = 0.0D;
            for (int i2 = 0; i2 < i1; i2++) {
                d += aestimator[j1].getProbability(i2);
            }

            if (d > 0.0D) {
                for (int j2 = 0; j2 < i1; j2++) {
                    discreteestimatorbayes.addValue(j2, aestimator[j1].getProbability(j2) / d);
                }

            } else {
                for (int k2 = 0; k2 < i1; k2++) {
                    discreteestimatorbayes.addValue(k2, 1.0D / (double)i1);
                }

            }
            aestimator[j1] = discreteestimatorbayes;
        }

        for (int k1 = 0; k1 < getNrOfNodes(); k1++) {
            if (!m_ParentSets[k1].contains(i)) {
                continue;
            }
            ParentSet parentset = m_ParentSets[k1];
            Estimator aestimator1[] = m_Distributions[k1];
            Estimator aestimator2[] = new Estimator[(aestimator1.length * i1) / (i1 + 1)];
            int l1 = 0;
            int l2 = parentset.getNrOfParents();
            int ai[] = new int[l2];
            int i3 = (parentset.getFreshCardinalityOfParents(m_Instances) * (i1 + 1)) / i1;
            int j3;
            for (j3 = 0; parentset.getParent(j3) != i; j3++) { }
            int ai1[] = new int[l2];
            for (int k3 = 0; k3 < l2; k3++) {
                ai1[k3] = getCardinality(parentset.getParent(k3));
            }

            ai1[j3]++;
label0:
            for (int l3 = 0; l3 < i3; l3++) {
                if (ai[j3] != k) {
                    aestimator2[l1++] = aestimator1[l3];
                }
                int i4 = 0;
                ai[i4]++;
                do {
                    if (i4 >= l2 || ai[i4] != ai1[i4]) {
                        continue label0;
                    }
                    ai[i4] = 0;
                    if (++i4 < l2) {
                        ai[i4]++;
                    }
                } while (true);
            }

            m_Distributions[k1] = aestimator2;
        }

        if (getEvidence(i) > k) {
            setEvidence(i, getEvidence(i) - 1);
        }
    }

    public void setPosition(int i, int j, int k) {
        if (m_bNeedsUndoAction) {
            boolean flag = false;
            Object obj = null;
            try {
                if (m_undoStack.size() > 0) {
                    UndoAction undoaction = (UndoAction)m_undoStack.elementAt(m_undoStack.size() - 1);
                    SetPositionAction setpositionaction = (SetPositionAction)undoaction;
                    if (setpositionaction.m_nTargetNode == i) {
                        flag = true;
                        setpositionaction.setUndoPosition(j, k);
                    }
                }
            }
            catch (Exception exception) { }
            if (!flag) {
                addUndoAction(new SetPositionAction(i, j, k));
            }
        }
        m_nPositionX.setElementAt(Integer.valueOf(j), i);
        m_nPositionY.setElementAt(Integer.valueOf(k), i);
    }

    public void setPosition(int i, int j, int k, FastVector fastvector) {
        int l = j - getPositionX(i);
        int i1 = k - getPositionY(i);
        if (m_bNeedsUndoAction) {
            boolean flag = false;
            try {
                Object obj = null;
                if (m_undoStack.size() > 0) {
                    UndoAction undoaction = (UndoAction)m_undoStack.elementAt(m_undoStack.size() - 1);
                    SetGroupPositionAction setgrouppositionaction = (SetGroupPositionAction)undoaction;
                    flag = true;
                    for (int k1 = 0; flag && k1 < setgrouppositionaction.m_nodes.size(); k1++) {
                        if ((Integer)setgrouppositionaction.m_nodes.elementAt(k1) != (Integer)fastvector.elementAt(k1)) {
                            flag = false;
                        }
                    }

                    if (flag) {
                        setgrouppositionaction.setUndoPosition(l, i1);
                    }
                }
            }
            catch (Exception exception) { }
            if (!flag) {
                addUndoAction(new SetGroupPositionAction(fastvector, l, i1));
            }
        }
        for (int j1 = 0; j1 < fastvector.size(); j1++) {
            i = ((Integer)fastvector.elementAt(j1)).intValue();
            m_nPositionX.setElementAt(Integer.valueOf(getPositionX(i) + l), i);
            m_nPositionY.setElementAt(Integer.valueOf(getPositionY(i) + i1), i);
        }

    }

    public void layoutGraph(FastVector fastvector, FastVector fastvector1) {
        if (m_bNeedsUndoAction) {
            addUndoAction(new LayoutGraphAction(fastvector, fastvector1));
        }
        m_nPositionX = fastvector;
        m_nPositionY = fastvector1;
    }

    public int getPositionX(int i) {
        return ((Integer)(Integer)m_nPositionX.elementAt(i)).intValue();
    }

    public int getPositionY(int i) {
        return ((Integer)(Integer)m_nPositionY.elementAt(i)).intValue();
    }

    public void alignLeft(FastVector fastvector) {
        if (m_bNeedsUndoAction) {
            addUndoAction(new alignLeftAction(fastvector));
        }
        int i = -1;
        for (int j = 0; j < fastvector.size(); j++) {
            int l = getPositionX(((Integer)fastvector.elementAt(j)).intValue());
            if (l < i || j == 0) {
                i = l;
            }
        }

        for (int k = 0; k < fastvector.size(); k++) {
            int i1 = ((Integer)fastvector.elementAt(k)).intValue();
            m_nPositionX.setElementAt(Integer.valueOf(i), i1);
        }

    }

    public void alignRight(FastVector fastvector) {
        if (m_bNeedsUndoAction) {
            addUndoAction(new alignRightAction(fastvector));
        }
        int i = -1;
        for (int j = 0; j < fastvector.size(); j++) {
            int l = getPositionX(((Integer)fastvector.elementAt(j)).intValue());
            if (l > i || j == 0) {
                i = l;
            }
        }

        for (int k = 0; k < fastvector.size(); k++) {
            int i1 = ((Integer)fastvector.elementAt(k)).intValue();
            m_nPositionX.setElementAt(Integer.valueOf(i), i1);
        }

    }

    public void alignTop(FastVector fastvector) {
        if (m_bNeedsUndoAction) {
            addUndoAction(new alignTopAction(fastvector));
        }
        int i = -1;
        for (int j = 0; j < fastvector.size(); j++) {
            int l = getPositionY(((Integer)fastvector.elementAt(j)).intValue());
            if (l < i || j == 0) {
                i = l;
            }
        }

        for (int k = 0; k < fastvector.size(); k++) {
            int i1 = ((Integer)fastvector.elementAt(k)).intValue();
            m_nPositionY.setElementAt(Integer.valueOf(i), i1);
        }

    }

    public void alignBottom(FastVector fastvector) {
        if (m_bNeedsUndoAction) {
            addUndoAction(new alignBottomAction(fastvector));
        }
        int i = -1;
        for (int j = 0; j < fastvector.size(); j++) {
            int l = getPositionY(((Integer)fastvector.elementAt(j)).intValue());
            if (l > i || j == 0) {
                i = l;
            }
        }

        for (int k = 0; k < fastvector.size(); k++) {
            int i1 = ((Integer)fastvector.elementAt(k)).intValue();
            m_nPositionY.setElementAt(Integer.valueOf(i), i1);
        }

    }

    public void centerHorizontal(FastVector fastvector) {
        if (m_bNeedsUndoAction) {
            addUndoAction(new centerHorizontalAction(fastvector));
        }
        int i = -1;
        int j = -1;
        for (int k = 0; k < fastvector.size(); k++) {
            int i1 = getPositionY(((Integer)fastvector.elementAt(k)).intValue());
            if (i1 < i || k == 0) {
                i = i1;
            }
            if (i1 > j || k == 0) {
                j = i1;
            }
        }

        for (int l = 0; l < fastvector.size(); l++) {
            int j1 = ((Integer)fastvector.elementAt(l)).intValue();
            m_nPositionY.setElementAt(Integer.valueOf((i + j) / 2), j1);
        }

    }

    public void centerVertical(FastVector fastvector) {
        if (m_bNeedsUndoAction) {
            addUndoAction(new centerVerticalAction(fastvector));
        }
        int i = -1;
        int j = -1;
        for (int k = 0; k < fastvector.size(); k++) {
            int i1 = getPositionX(((Integer)fastvector.elementAt(k)).intValue());
            if (i1 < i || k == 0) {
                i = i1;
            }
            if (i1 > j || k == 0) {
                j = i1;
            }
        }

        for (int l = 0; l < fastvector.size(); l++) {
            int j1 = ((Integer)fastvector.elementAt(l)).intValue();
            m_nPositionX.setElementAt(Integer.valueOf((i + j) / 2), j1);
        }

    }

    public void spaceHorizontal(FastVector fastvector) {
        if (m_bNeedsUndoAction) {
            addUndoAction(new spaceHorizontalAction(fastvector));
        }
        int i = -1;
        int j = -1;
        for (int k = 0; k < fastvector.size(); k++) {
            int i1 = getPositionX(((Integer)fastvector.elementAt(k)).intValue());
            if (i1 < i || k == 0) {
                i = i1;
            }
            if (i1 > j || k == 0) {
                j = i1;
            }
        }

        for (int l = 0; l < fastvector.size(); l++) {
            int j1 = ((Integer)fastvector.elementAt(l)).intValue();
            m_nPositionX.setElementAt(Integer.valueOf((int)((double)i + (double)(l * (j - i)) / ((double)fastvector.size() - 1.0D))), j1);
        }

    }

    public void spaceVertical(FastVector fastvector) {
        if (m_bNeedsUndoAction) {
            addUndoAction(new spaceVerticalAction(fastvector));
        }
        int i = -1;
        int j = -1;
        for (int k = 0; k < fastvector.size(); k++) {
            int i1 = getPositionY(((Integer)fastvector.elementAt(k)).intValue());
            if (i1 < i || k == 0) {
                i = i1;
            }
            if (i1 > j || k == 0) {
                j = i1;
            }
        }

        for (int l = 0; l < fastvector.size(); l++) {
            int j1 = ((Integer)fastvector.elementAt(l)).intValue();
            m_nPositionY.setElementAt(Integer.valueOf((int)((double)i + (double)(l * (j - i)) / ((double)fastvector.size() - 1.0D))), j1);
        }

    }

    void replaceAtt(int i, String s, FastVector fastvector) {
        Attribute attribute = new Attribute(s, fastvector);
        if (m_Instances.classIndex() == i) {
            m_Instances.setClassIndex(-1);
            m_Instances.insertAttributeAt(attribute, i);
            m_Instances.deleteAttributeAt(i + 1);
            m_Instances.setClassIndex(i);
        } else {
            m_Instances.insertAttributeAt(attribute, i);
            m_Instances.deleteAttributeAt(i + 1);
        }
    }

    public double[] getMargin(int i) {
        return (double[])(double[])m_fMarginP.elementAt(i);
    }

    public void setMargin(int i, double ad[]) {
        m_fMarginP.setElementAt(ad, i);
    }

    public int getEvidence(int i) {
        return ((Integer)m_nEvidence.elementAt(i)).intValue();
    }

    public void setEvidence(int i, int j) {
        m_nEvidence.setElementAt(Integer.valueOf(j), i);
    }

    public FastVector getChildren(int i) {
        FastVector fastvector = new FastVector();
        for (int j = 0; j < getNrOfNodes(); j++) {
            if (m_ParentSets[j].contains(i)) {
                fastvector.addElement(Integer.valueOf(j));
            }
        }

        return fastvector;
    }

    public String toXMLBIF03() {
        if (m_Instances == null) {
            return "<!--No model built yet-->";
        }
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append(getBIFHeader());
        stringbuffer.append("\n");
        stringbuffer.append("\n");
        stringbuffer.append("<BIF VERSION=\"0.3\">\n");
        stringbuffer.append("<NETWORK>\n");
        stringbuffer.append((new StringBuilder()).append("<NAME>").append(XMLNormalize(m_Instances.relationName())).append("</NAME>\n").toString());
        for (int i = 0; i < m_Instances.numAttributes(); i++) {
            stringbuffer.append("<VARIABLE TYPE=\"nature\">\n");
            stringbuffer.append((new StringBuilder()).append("<NAME>").append(XMLNormalize(m_Instances.attribute(i).name())).append("</NAME>\n").toString());
            for (int k = 0; k < m_Instances.attribute(i).numValues(); k++) {
                stringbuffer.append((new StringBuilder()).append("<OUTCOME>").append(XMLNormalize(m_Instances.attribute(i).value(k))).append("</OUTCOME>\n").toString());
            }

            stringbuffer.append((new StringBuilder()).append("<PROPERTY>position = (").append(getPositionX(i)).append(",").append(getPositionY(i)).append(")</PROPERTY>\n").toString());
            stringbuffer.append("</VARIABLE>\n");
        }

        for (int j = 0; j < m_Instances.numAttributes(); j++) {
            stringbuffer.append("<DEFINITION>\n");
            stringbuffer.append((new StringBuilder()).append("<FOR>").append(XMLNormalize(m_Instances.attribute(j).name())).append("</FOR>\n").toString());
            for (int l = 0; l < m_ParentSets[j].getNrOfParents(); l++) {
                stringbuffer.append((new StringBuilder()).append("<GIVEN>").append(XMLNormalize(m_Instances.attribute(m_ParentSets[j].getParent(l)).name())).append("</GIVEN>\n").toString());
            }

            stringbuffer.append("<TABLE>\n");
            for (int i1 = 0; i1 < m_ParentSets[j].getCardinalityOfParents(); i1++) {
                for (int j1 = 0; j1 < m_Instances.attribute(j).numValues(); j1++) {
                    stringbuffer.append(m_Distributions[j][i1].getProbability(j1));
                    stringbuffer.append(' ');
                }

                stringbuffer.append('\n');
            }

            stringbuffer.append("</TABLE>\n");
            stringbuffer.append("</DEFINITION>\n");
        }

        stringbuffer.append("</NETWORK>\n");
        stringbuffer.append("</BIF>\n");
        return stringbuffer.toString();
    }

    public String toXMLBIF03(FastVector fastvector) {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append(getBIFHeader());
        stringbuffer.append("\n");
        stringbuffer.append("\n");
        stringbuffer.append("<BIF VERSION=\"0.3\">\n");
        stringbuffer.append("<NETWORK>\n");
        stringbuffer.append((new StringBuilder()).append("<NAME>").append(XMLNormalize(m_Instances.relationName())).append("</NAME>\n").toString());
        for (int i = 0; i < fastvector.size(); i++) {
            int k = ((Integer)fastvector.elementAt(i)).intValue();
            stringbuffer.append("<VARIABLE TYPE=\"nature\">\n");
            stringbuffer.append((new StringBuilder()).append("<NAME>").append(XMLNormalize(m_Instances.attribute(k).name())).append("</NAME>\n").toString());
            for (int i1 = 0; i1 < m_Instances.attribute(k).numValues(); i1++) {
                stringbuffer.append((new StringBuilder()).append("<OUTCOME>").append(XMLNormalize(m_Instances.attribute(k).value(i1))).append("</OUTCOME>\n").toString());
            }

            stringbuffer.append((new StringBuilder()).append("<PROPERTY>position = (").append(getPositionX(k)).append(",").append(getPositionY(k)).append(")</PROPERTY>\n").toString());
            stringbuffer.append("</VARIABLE>\n");
        }

        for (int j = 0; j < fastvector.size(); j++) {
            int l = ((Integer)fastvector.elementAt(j)).intValue();
            stringbuffer.append("<DEFINITION>\n");
            stringbuffer.append((new StringBuilder()).append("<FOR>").append(XMLNormalize(m_Instances.attribute(l).name())).append("</FOR>\n").toString());
            for (int j1 = 0; j1 < m_ParentSets[l].getNrOfParents(); j1++) {
                stringbuffer.append((new StringBuilder()).append("<GIVEN>").append(XMLNormalize(m_Instances.attribute(m_ParentSets[l].getParent(j1)).name())).append("</GIVEN>\n").toString());
            }

            stringbuffer.append("<TABLE>\n");
            for (int k1 = 0; k1 < m_ParentSets[l].getCardinalityOfParents(); k1++) {
                for (int l1 = 0; l1 < m_Instances.attribute(l).numValues(); l1++) {
                    stringbuffer.append(m_Distributions[l][k1].getProbability(l1));
                    stringbuffer.append(' ');
                }

                stringbuffer.append('\n');
            }

            stringbuffer.append("</TABLE>\n");
            stringbuffer.append("</DEFINITION>\n");
        }

        stringbuffer.append("</NETWORK>\n");
        stringbuffer.append("</BIF>\n");
        return stringbuffer.toString();
    }

    public boolean canUndo() {
        return m_nCurrentEditAction >= 0;
    }

    public boolean canRedo() {
        return m_nCurrentEditAction < m_undoStack.size() - 1;
    }

    public boolean isChanged() {
        return m_nCurrentEditAction != m_nSavedPointer;
    }

    public void isSaved() {
        m_nSavedPointer = m_nCurrentEditAction;
    }

    public String lastActionMsg() {
        if (m_undoStack.size() == 0) {
            return "";
        } else {
            return ((UndoAction)m_undoStack.lastElement()).getRedoMsg();
        }
    }

    public String undo() {
        if (!canUndo()) {
            return "";
        } else {
            UndoAction undoaction = (UndoAction)m_undoStack.elementAt(m_nCurrentEditAction);
            m_bNeedsUndoAction = false;
            undoaction.undo();
            m_bNeedsUndoAction = true;
            m_nCurrentEditAction--;
            return undoaction.getUndoMsg();
        }
    }

    public String redo() {
        if (!canRedo()) {
            return "";
        } else {
            m_nCurrentEditAction++;
            UndoAction undoaction = (UndoAction)m_undoStack.elementAt(m_nCurrentEditAction);
            m_bNeedsUndoAction = false;
            undoaction.redo();
            m_bNeedsUndoAction = true;
            return undoaction.getRedoMsg();
        }
    }

    void addUndoAction(UndoAction undoaction) {
        for (int i = m_undoStack.size() - 1; i > m_nCurrentEditAction;) {
            m_undoStack.removeElementAt(i--);
        }

        if (m_nSavedPointer > m_nCurrentEditAction) {
            m_nSavedPointer = -2;
        }
        m_undoStack.addElement(undoaction);
        m_nCurrentEditAction++;
    }

    public void clearUndoStack() {
        m_undoStack = new FastVector();
        m_nCurrentEditAction = -1;
        m_nSavedPointer = -1;
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.4 $");
    }

    public static void main(String args[]) {
    }










}
