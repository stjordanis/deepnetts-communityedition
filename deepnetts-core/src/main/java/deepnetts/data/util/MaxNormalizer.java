package deepnetts.data.util;

import deepnetts.data.BasicDataSetItem;
import deepnetts.data.DataSet;
import deepnetts.data.DataSetItem;
import deepnetts.util.Tensor;
import deepnetts.util.Tensors;
import java.io.Serializable;

/**
 * Performs max normalization, rescales data to corresponding max value in each column.
 * Effectively scales all values to interval [0, 1]
 * Performs normalization on both inputs and outputs.
 * 
 * @author Zoran Sevarac
 */
public final class MaxNormalizer implements Normalizer, Serializable {
    private Tensor maxInputs;
    private Tensor maxOutputs;
                
    /**
     * Creates a new instance of max normalizer initialized to max values in given data set.
     * 
     * @param dataSet 
     */
    public MaxNormalizer(DataSet<BasicDataSetItem> dataSet) {
        // find max values for each component of input and output tensor/vector
        maxInputs = dataSet.get(0).getInput().copy();
        maxOutputs = dataSet.get(0).getTargetOutput().copy();
        
        // find max values for all components of input and output vectors
        for(BasicDataSetItem item : dataSet) {
            maxInputs = Tensors.absMax(item.getInput(), maxInputs); 
            maxOutputs = Tensors.absMax(item.getTargetOutput(), maxOutputs);
        }        
    }
        
    /**
     * Performs normalization on the given inputs.
     * 
     * @param dataSet data set to normalize
     */
    @Override
    public void normalize(DataSet<?> dataSet) {
        // todo: prevent/catch division by zero
        for(DataSetItem item : dataSet) {
            item.getInput().div(maxInputs);   // kad je absMaxInput postao 1 1 1 1 posle pre iteracije!!!
            item.getTargetOutput().div(maxOutputs); 
        }    
    }

    public Tensor getMaxInputs() {
        return maxInputs;
    }

    public void setMaxInputs(Tensor maxInputs) {
        this.maxInputs = maxInputs;
    }

    public Tensor getMaxOutputs() {
        return maxOutputs;
    }

    public void setMaxOutputs(Tensor maxOutputs) {
        this.maxOutputs = maxOutputs;
    }
           
    /**
     * De-normalize given output vector in-place.
     * Multiplies given vector with vector used for normalization, and stores these values in same memory location as input vector.
     * 
     * @param outputs 
     */
    public void deNormalizeOutputs(final Tensor outputs) {
        outputs.multiplyElementWise(maxOutputs);
    }
    
    public void deNormalizeInputs(final Tensor inputs) {
        inputs.multiplyElementWise(maxInputs);
    }    
   
}