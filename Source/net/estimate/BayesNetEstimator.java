// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net.estimate;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.bayes.BayesNet;
import weka.core.*;

public class BayesNetEstimator
    implements OptionHandler, Serializable, RevisionHandler {

    static final long serialVersionUID = 0x1e504cbde00e4c3cL;
    protected double m_fAlpha;

    public BayesNetEstimator() {
        m_fAlpha = 0.5D;
    }

    public void estimateCPTs(BayesNet bayesnet) throws Exception {
        throw new Exception("Incorrect BayesNetEstimator: use subclass instead.");
    }

    public void updateClassifier(BayesNet bayesnet, Instance instance) throws Exception {
        throw new Exception("Incorrect BayesNetEstimator: use subclass instead.");
    }

    public double[] distributionForInstance(BayesNet bayesnet, Instance instance) throws Exception {
        throw new Exception("Incorrect BayesNetEstimator: use subclass instead.");
    }

    public void initCPTs(BayesNet bayesnet) throws Exception {
        throw new Exception("Incorrect BayesNetEstimator: use subclass instead.");
    }

    public Enumeration listOptions() {
        Vector vector = new Vector(1);
        vector.addElement(new Option("\tInitial count (alpha)\n", "A", 1, "-A <alpha>"));
        return vector.elements();
    }

    public void setOptions(String as[]) throws Exception {
        String s = Utils.getOption('A', as);
        if (s.length() != 0) {
            m_fAlpha = (new Float(s)).floatValue();
        } else {
            m_fAlpha = 0.5D;
        }
        Utils.checkForRemainingOptions(as);
    }

    public String[] getOptions() {
        String as[] = new String[2];
        int i = 0;
        as[i++] = "-A";
        as[i++] = (new StringBuilder()).append("").append(m_fAlpha).toString();
        return as;
    }

    public void setAlpha(double d) {
        m_fAlpha = d;
    }

    public double getAlpha() {
        return m_fAlpha;
    }

    public String alphaTipText() {
        return "Alpha is used for estimating the probability tables and can be interpreted as the initial count on each value.";
    }

    public String globalInfo() {
        return "BayesNetEstimator is the base class for estimating the conditional probability tables of a Bayes network once the structure has been learned.";
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.4 $");
    }
}
