// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net.search.fixed;

import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.BIFReader;
import weka.classifiers.bayes.net.ParentSet;
import weka.classifiers.bayes.net.search.SearchAlgorithm;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.Utils;

public class FromFile extends SearchAlgorithm {

    static final long serialVersionUID = 0x65c8dfe564fc62c5L;
    String m_sBIFFile;

    public FromFile() {
        m_sBIFFile = "";
    }

    public String globalInfo() {
        return "The FromFile reads the structure of a Bayes net from a file in BIFF format.";
    }

    public void buildStructure(BayesNet bayesnet, Instances instances) throws Exception {
        BIFReader bifreader = new BIFReader();
        bifreader.processFile(m_sBIFFile);
        for (int i = 0; i < instances.numAttributes(); i++) {
            int j = bifreader.getNode(bayesnet.getNodeName(i));
            ParentSet parentset = bifreader.getParentSet(j);
            for (int k = 0; k < parentset.getNrOfParents(); k++) {
                String s = bifreader.getNodeName(parentset.getParent(k));
                int l;
                for (l = 0; l < instances.numAttributes() && !bayesnet.getNodeName(l).equals(s); l++) { }
                if (l >= instances.numAttributes()) {
                    throw new Exception((new StringBuilder()).append("Could not find attribute ").append(s).append(" from BIF file in data").toString());
                }
                bayesnet.getParentSet(i).addParent(l, instances);
            }

        }

    }

    public void setBIFFile(String s) {
        m_sBIFFile = s;
    }

    public String getBIFFile() {
        return m_sBIFFile;
    }

    public Enumeration listOptions() {
        Vector vector = new Vector();
        vector.addElement(new Option("\tName of file containing network structure in BIF format\n", "B", 1, "-B <BIF File>"));
        for (Enumeration enumeration = super.listOptions(); enumeration.hasMoreElements(); vector.addElement(enumeration.nextElement())) { }
        return vector.elements();
    }

    public void setOptions(String as[]) throws Exception {
        setBIFFile(Utils.getOption('B', as));
        super.setOptions(as);
    }

    public String[] getOptions() {
        String as[] = super.getOptions();
        String as1[] = new String[2 + as.length];
        int i = 0;
        as1[i++] = "-B";
        as1[i++] = (new StringBuilder()).append("").append(getBIFFile()).toString();
        for (int j = 0; j < as.length; j++) {
            as1[i++] = as[j];
        }

        while (i < as1.length)  {
            as1[i++] = "";
        }
        return as1;
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.8 $");
    }
}
