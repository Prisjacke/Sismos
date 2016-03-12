// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes;

import weka.classifiers.UpdateableClassifier;
import weka.core.RevisionUtils;
import weka.core.TechnicalInformation;

// Referenced classes of package weka.classifiers.bayes:
//            NaiveBayes

public class NaiveBayesUpdateable extends NaiveBayes
    implements UpdateableClassifier {

    static final long serialVersionUID = 0xb5b2b6f4f40f1b63L;


    public String globalInfo() {
        return (new StringBuilder()).append("Class for a Naive Bayes classifier using estimator classes. This is the updateable version of NaiveBayes.\nThis classifier will use a default precision of 0.1 for numeric attributes when buildClassifier is called with zero training instances.\n\nFor more information on Naive Bayes classifiers, see\n\n").append(getTechnicalInformation().toString()).toString();
    }

    public TechnicalInformation getTechnicalInformation() {
        return super.getTechnicalInformation();
    }

    public void setUseSupervisedDiscretization(boolean flag) {
        if (flag) {
            throw new IllegalArgumentException("Can't use discretization in NaiveBayesUpdateable!");
        } else {
            m_UseDiscretization = false;
            return;
        }
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.11 $");
    }

    public static void main(String args[]) {
        runClassifier(new NaiveBayesUpdateable(), args);
    }
}
