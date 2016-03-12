// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net.search.fixed;

import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.ParentSet;
import weka.classifiers.bayes.net.search.SearchAlgorithm;
import weka.core.Instances;
import weka.core.RevisionUtils;

public class NaiveBayes extends SearchAlgorithm {

    static final long serialVersionUID = 0xbd4484c52dfeb65dL;


    public String globalInfo() {
        return "The NaiveBayes class generates a fixed Bayes network structure with arrows from the class variable to each of the attribute variables.";
    }

    public void buildStructure(BayesNet bayesnet, Instances instances) throws Exception {
        for (int i = 0; i < instances.numAttributes(); i++) {
            if (i != instances.classIndex()) {
                bayesnet.getParentSet(i).addParent(instances.classIndex(), instances);
            }
        }

    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.6 $");
    }
}
