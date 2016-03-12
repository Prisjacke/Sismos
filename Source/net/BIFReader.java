// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.io.File;
import java.io.PrintStream;
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

// Referenced classes of package weka.classifiers.bayes.net:
//            ParentSet

public class BIFReader extends BayesNet
    implements TechnicalInformationHandler {

    protected int m_nPositionX[];
    protected int m_nPositionY[];
    private int m_order[];
    static final long serialVersionUID = 0x8bff58dc4065e82bL;
    String m_sFile;

    public String globalInfo() {
        return (new StringBuilder()).append("Builds a description of a Bayes Net classifier stored in XML BIF 0.3 format.\n\nFor more details on XML BIF see:\n\n").append(getTechnicalInformation().toString()).toString();
    }

    public BIFReader processFile(String s) throws Exception {
        m_sFile = s;
        DocumentBuilderFactory documentbuilderfactory = DocumentBuilderFactory.newInstance();
        documentbuilderfactory.setValidating(true);
        Document document = documentbuilderfactory.newDocumentBuilder().parse(new File(s));
        document.normalize();
        buildInstances(document, s);
        buildStructure(document);
        return this;
    }

    public BIFReader processString(String s) throws Exception {
        DocumentBuilderFactory documentbuilderfactory = DocumentBuilderFactory.newInstance();
        documentbuilderfactory.setValidating(true);
        Document document = documentbuilderfactory.newDocumentBuilder().parse(new InputSource(new StringReader(s)));
        document.normalize();
        buildInstances(document, "from-string");
        buildStructure(document);
        return this;
    }

    public String getFileName() {
        return m_sFile;
    }

    public TechnicalInformation getTechnicalInformation() {
        TechnicalInformation technicalinformation = new TechnicalInformation(weka.core.TechnicalInformation.Type.MISC);
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.AUTHOR, "Fabio Cozman and Marek Druzdzel and Daniel Garcia");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.YEAR, "1998");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.TITLE, "XML BIF version 0.3");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.URL, "http://www-2.cs.cmu.edu/~fgcozman/Research/InterchangeFormat/");
        return technicalinformation;
    }

    void buildStructure(Document document) throws Exception {
        m_Distributions = new Estimator[m_Instances.numAttributes()][];
        for (int i = 0; i < m_Instances.numAttributes(); i++) {
            String s = m_Instances.attribute(i).name();
            Element element = getDefinition(document, s);
            FastVector fastvector = getParentNodes(element);
            for (int j = 0; j < fastvector.size(); j++) {
                Node node = ((Node)fastvector.elementAt(j)).getFirstChild();
                String s1 = ((CharacterData)(CharacterData)node).getData();
                int j1 = getNode(s1);
                m_ParentSets[i].addParent(j1, m_Instances);
            }

            int k = m_ParentSets[i].getCardinalityOfParents();
            int l = m_Instances.attribute(i).numValues();
            m_Distributions[i] = new Estimator[k];
            for (int i1 = 0; i1 < k; i1++) {
                m_Distributions[i][i1] = new DiscreteEstimatorBayes(l, 0.0D);
            }

            String s2 = getTable(element);
            StringTokenizer stringtokenizer = new StringTokenizer(s2.toString());
            for (int k1 = 0; k1 < k; k1++) {
                DiscreteEstimatorBayes discreteestimatorbayes = (DiscreteEstimatorBayes)m_Distributions[i][k1];
                for (int l1 = 0; l1 < l; l1++) {
                    String s3 = stringtokenizer.nextToken();
                    discreteestimatorbayes.addValue(l1, (new Double(s3)).doubleValue());
                }

            }

        }

    }

    public void Sync(BayesNet bayesnet) throws Exception {
        int i = m_Instances.numAttributes();
        if (i != bayesnet.m_Instances.numAttributes()) {
            throw new Exception("Cannot synchronize networks: different number of attributes.");
        }
        m_order = new int[i];
        for (int j = 0; j < i; j++) {
            String s = bayesnet.getNodeName(j);
            m_order[getNode(s)] = j;
        }

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

    void buildInstances(Document document, String s) throws Exception {
        NodeList nodelist = selectAllNames(document);
        if (nodelist.getLength() > 0) {
            s = ((CharacterData)(CharacterData)nodelist.item(0).getFirstChild()).getData();
        }
        nodelist = selectAllVariables(document);
        int i = nodelist.getLength();
        FastVector fastvector = new FastVector(i);
        m_nPositionX = new int[nodelist.getLength()];
        m_nPositionY = new int[nodelist.getLength()];
        for (int j = 0; j < nodelist.getLength(); j++) {
            FastVector fastvector1 = selectOutCome(nodelist.item(j));
            int k = fastvector1.size();
            FastVector fastvector2 = new FastVector(k + 1);
            for (int l = 0; l < k; l++) {
                Node node = ((Node)fastvector1.elementAt(l)).getFirstChild();
                String s2 = ((CharacterData)(CharacterData)node).getData();
                if (s2 == null) {
                    s2 = (new StringBuilder()).append("Value").append(l + 1).toString();
                }
                fastvector2.addElement(s2);
            }

            FastVector fastvector3 = selectName(nodelist.item(j));
            if (fastvector3.size() == 0) {
                throw new Exception("No name specified for variable");
            }
            String s1 = ((CharacterData)(CharacterData)((Node)fastvector3.elementAt(0)).getFirstChild()).getData();
            Attribute attribute = new Attribute(s1, fastvector2);
            fastvector.addElement(attribute);
            fastvector1 = selectProperty(nodelist.item(j));
            k = fastvector1.size();
            for (int i1 = 0; i1 < k; i1++) {
                Node node1 = ((Node)fastvector1.elementAt(i1)).getFirstChild();
                String s3 = ((CharacterData)(CharacterData)node1).getData();
                if (!s3.startsWith("position")) {
                    continue;
                }
                int j1 = s3.indexOf('(');
                int k1 = s3.indexOf(',');
                int l1 = s3.indexOf(')');
                String s4 = s3.substring(j1 + 1, k1).trim();
                String s5 = s3.substring(k1 + 1, l1).trim();
                try {
                    m_nPositionX[j] = Integer.parseInt(s4);
                    m_nPositionY[j] = Integer.parseInt(s5);
                    continue;
                }
                catch (NumberFormatException numberformatexception) {
                    System.err.println((new StringBuilder()).append("Wrong number format in position :(").append(s4).append(",").append(s5).append(")").toString());
                }
                m_nPositionX[j] = 0;
                m_nPositionY[j] = 0;
            }

        }

        m_Instances = new Instances(s, fastvector, 100);
        m_Instances.setClassIndex(i - 1);
        setUseADTree(false);
        initStructure();
    }

    NodeList selectAllNames(Document document) throws Exception {
        NodeList nodelist = document.getElementsByTagName("NAME");
        return nodelist;
    }

    NodeList selectAllVariables(Document document) throws Exception {
        NodeList nodelist = document.getElementsByTagName("VARIABLE");
        return nodelist;
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

    FastVector getParentNodes(Node node) throws Exception {
        FastVector fastvector = selectElements(node, "GIVEN");
        return fastvector;
    }

    String getTable(Node node) throws Exception {
        FastVector fastvector = selectElements(node, "TABLE");
        String s = getContent((Element)fastvector.elementAt(0));
        s = s.replaceAll("\\n", " ");
        return s;
    }

    FastVector selectOutCome(Node node) throws Exception {
        FastVector fastvector = selectElements(node, "OUTCOME");
        return fastvector;
    }

    FastVector selectName(Node node) throws Exception {
        FastVector fastvector = selectElements(node, "NAME");
        return fastvector;
    }

    FastVector selectProperty(Node node) throws Exception {
        FastVector fastvector = selectElements(node, "PROPERTY");
        return fastvector;
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

    public int missingArcs(BayesNet bayesnet) {
        int i;
        Sync(bayesnet);
        i = 0;
        for (int j = 0; j < m_Instances.numAttributes(); j++) {
            for (int k = 0; k < m_ParentSets[j].getNrOfParents(); k++) {
                int l = m_ParentSets[j].getParent(k);
                if (!bayesnet.getParentSet(m_order[j]).contains(m_order[l]) && !bayesnet.getParentSet(m_order[l]).contains(m_order[j])) {
                    i++;
                }
            }

        }

        return i;
        Exception exception;
        exception;
        System.err.println(exception.getMessage());
        return 0;
    }

    public int extraArcs(BayesNet bayesnet) {
        int i;
        Sync(bayesnet);
        i = 0;
        for (int j = 0; j < m_Instances.numAttributes(); j++) {
            for (int k = 0; k < bayesnet.getParentSet(m_order[j]).getNrOfParents(); k++) {
                int l = m_order[bayesnet.getParentSet(m_order[j]).getParent(k)];
                if (!m_ParentSets[j].contains(l) && !m_ParentSets[l].contains(j)) {
                    i++;
                }
            }

        }

        return i;
        Exception exception;
        exception;
        System.err.println(exception.getMessage());
        return 0;
    }

    public double divergence(BayesNet bayesnet) {
        double d;
        Sync(bayesnet);
        d = 0.0D;
        int i = m_Instances.numAttributes();
        int ai[] = new int[i];
        for (int j = 0; j < i; j++) {
            ai[j] = m_Instances.attribute(j).numValues();
        }

        int ai1[] = new int[i];
        int k = 0;
        do {
            if (k >= i) {
                break;
            }
            ai1[k]++;
            do {
                if (k >= i || ai1[k] != m_Instances.attribute(k).numValues()) {
                    break;
                }
                ai1[k] = 0;
                if (++k < i) {
                    ai1[k]++;
                }
            } while (true);
            if (k < i) {
                k = 0;
                double d1 = 1.0D;
                for (int l = 0; l < i; l++) {
                    int i1 = 0;
                    for (int j1 = 0; j1 < m_ParentSets[l].getNrOfParents(); j1++) {
                        int l1 = m_ParentSets[l].getParent(j1);
                        i1 = i1 * ai[l1] + ai1[l1];
                    }

                    d1 *= m_Distributions[l][i1].getProbability(ai1[l]);
                }

                double d2 = 1.0D;
                for (int k1 = 0; k1 < i; k1++) {
                    int i2 = 0;
                    for (int j2 = 0; j2 < bayesnet.getParentSet(m_order[k1]).getNrOfParents(); j2++) {
                        int k2 = m_order[bayesnet.getParentSet(m_order[k1]).getParent(j2)];
                        i2 = i2 * ai[k2] + ai1[k2];
                    }

                    d2 *= bayesnet.m_Distributions[m_order[k1]][i2].getProbability(ai1[k1]);
                }

                if (d1 > 0.0D && d2 > 0.0D) {
                    d += d1 * Math.log(d2 / d1);
                }
            }
        } while (true);
        return d;
        Exception exception;
        exception;
        System.err.println(exception.getMessage());
        return 0.0D;
    }

    public int reversedArcs(BayesNet bayesnet) {
        int i;
        Sync(bayesnet);
        i = 0;
        for (int j = 0; j < m_Instances.numAttributes(); j++) {
            for (int k = 0; k < m_ParentSets[j].getNrOfParents(); k++) {
                int l = m_ParentSets[j].getParent(k);
                if (!bayesnet.getParentSet(m_order[j]).contains(m_order[l]) && bayesnet.getParentSet(m_order[l]).contains(m_order[j])) {
                    i++;
                }
            }

        }

        return i;
        Exception exception;
        exception;
        System.err.println(exception.getMessage());
        return 0;
    }

    public int getNode(String s) throws Exception {
        for (int i = 0; i < m_Instances.numAttributes(); i++) {
            if (m_Instances.attribute(i).name().equals(s)) {
                return i;
            }
        }

        throw new Exception((new StringBuilder()).append("Could not find node [[").append(s).append("]]").toString());
    }


    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.15 $");
    }

    public static void main(String args[]) {
        try {
            BIFReader bifreader = new BIFReader();
            bifreader.processFile(args[0]);
            System.out.println(bifreader.toString());
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
