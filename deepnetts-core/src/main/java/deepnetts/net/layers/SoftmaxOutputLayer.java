/**
 *  DeepNetts is pure Java Deep Learning Library with support for Backpropagation
 *  based learning and image recognition.
 *
 *  Copyright (C) 2017  Zoran Sevarac <sevarac@gmail.com>
 *
 * This file is part of DeepNetts.
 *
 * DeepNetts is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <https://www.gnu.org/licenses/>.package
 * deepnetts.core;
 */
package deepnetts.net.layers;

import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.train.opt.OptimizerType;
import deepnetts.util.WeightsInit;
import deepnetts.util.Tensor;
import java.util.Arrays;

/**
 * Output layer with softmax activation function.
 *
 * @author Zoran Sevarac
 */
public class SoftmaxOutputLayer extends OutputLayer {

    public SoftmaxOutputLayer(int width) {
        super(width);
        setActivationType(ActivationType.SOFTMAX);
    }

    public SoftmaxOutputLayer(String[] labels) {
        super(labels);
        setActivationType(ActivationType.SOFTMAX);
    }

    @Override
    public void init() {
        inputs = prevLayer.outputs;
        outputs = new Tensor(width);
        outputErrors = new float[width];
        deltas = new Tensor(width);

        // height je koliko ima neurona u prethodnom FC lejeru  - pretpostavka je da moze samo FC lejer da bude iza
        int prevLayerWidth = prevLayer.getWidth();
        weights = new Tensor(prevLayerWidth, width);
        deltaWeights = new Tensor(prevLayerWidth, width);
        gradients = new Tensor(prevLayerWidth, width);
        prevDeltaWeights = new Tensor(prevLayerWidth, width);
        WeightsInit.xavier(weights.getValues(), prevLayerWidth, width);

        biases = new float[width];
        deltaBiases = new float[width];
        prevDeltaBiases = new float[width];
        WeightsInit.randomize(biases);
        
        setOptimizerType(OptimizerType.SGD);
    }

    /**
     * This method implements forward pass for the output layer. Calculates
     * layer outputs using softmax function
     */
    @Override
    public void forward() {
        // find max weightedSum
        float maxWs = Float.NEGATIVE_INFINITY;

        //  compute weighted sums (activations) and find max weighted sum
        for (int outCol = 0; outCol < outputs.getCols(); outCol++) {                    // for all neurons in this layer
            outputs.set(outCol, biases[outCol]);                                        // first add bias
            for (int inCol = 0; inCol < inputs.getCols(); inCol++) {                    // iterate all inputs
                outputs.add(outCol, inputs.get(inCol) * weights.get(inCol, outCol));    // add weighted sum of inputs
            }

            if (outputs.get(outCol) > maxWs) { // find max weighted sum
                maxWs = outputs.get(outCol);
            }
        }

        // calculate outputs and denominator sum (use max for numerical stability)
        float denSum = 0;
        for (int col = 0; col < outputs.getCols(); col++) {
            outputs.set(col, (float) Math.exp(outputs.get(col) - maxWs)); // maxWs used for numerical stability
            denSum += outputs.get(col);
        }

        outputs.div(denSum); // scale all outputs to sum to 1
    }

    /**
     *
     */
    @Override
    public void backward() {
        if (!batchMode) {
            deltaWeights.fill(0);
            Arrays.fill(deltaBiases, 0);
        }

        deltas.copyFrom(outputErrors);

        // ovo je softmax za u kombinaciji sa cross entropy loss funkcijom, nisam siguran da li je dobra u kombinaciji sa sigmoidnom? Mada tada ide BinaryCE i obican output layer
        for (int outCol = 0; outCol < outputs.getCols(); outCol++) { // iterate all output neurons / deltas
            // da li delta treba da s emnozi sa izvodom? izgleda da ne treba
            // prema ovome ne mora file:///D:/DeepNettsSredjivanje/Books%20and%20Tuts/PetersNotes%20CE%20Softmax%20Derivation/Peter's%20NotesCE.html
            // http://neuralnetworksanddeeplearning.com/chap3.html
            for (int inCol = 0; inCol < inputs.getCols(); inCol++) { // prev layer is allways Dense. iterate all inputs/weights for the current neuron
                final float grad = deltas.get(outCol) * inputs.get(inCol); 
                gradients.set(inCol, outCol, grad); // a delta weight mnozi sa inputom
                final float deltaWeight = optim.calculateDeltaWeight(grad, inCol, outCol);   
                deltaWeights.add(inCol, outCol, deltaWeight); //
            }
            
            final float deltaBias = optim.calculateDeltaBias(deltas.get(outCol), outCol); 
            deltaBiases[outCol] += deltaBias;
        }
    }

}
