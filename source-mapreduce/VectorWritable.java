

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Vector;

import org.apache.hadoop.io.Writable;

/**
 * @author Shannon Quinn
 *
 * Defines the feature vector for KMeans clustering. Contains a cluster ID
 * in addition to the feature vector itself.
 */
public class VectorWritable implements Writable {
    
    private int clusterId;
    private int numInstances;
    private Vector<Double> vector;
    
    public VectorWritable() {
        this(null, 0, 0);
    }

    public VectorWritable(Vector<Double> toWrite, int clusterId) {
        this(toWrite, clusterId, 1);
    }
    
    public VectorWritable(Vector<Double> toWrite, int clusterId, int numInstances) {
        this.clusterId = clusterId;
        this.numInstances = numInstances;
        vector = toWrite;
    }
    
    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(clusterId);
        out.writeInt(numInstances);
        out.writeInt(vector.size());
        for (Double d : vector) {
            out.writeDouble(d.doubleValue());
        }
    }
    
    @Override
    public void readFields(DataInput in) throws IOException {
        clusterId = in.readInt();
        numInstances = in.readInt();
        int length = in.readInt();
        vector = new Vector<Double>(length);
        for (int i = 0; i < length; ++i) {
            vector.add(new Double(in.readDouble()));
        }
    }
    
    public Vector<Double> get() {
        return vector;
    }
    
    public int getClusterId() {
        return clusterId;
    }
    
    public int getNumInstances() {
        return numInstances;
    }
}
