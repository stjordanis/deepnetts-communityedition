package deepnetts.net.layers.activation;

import java.io.Serializable;

/**
 * Rectified Linear Activation and its Derivative.
 * 
 * y =  x for x > 0, 
 *      0.1 * x for x<0
 *        - 
 *       | 1, x > 0
 * y' = <
 *       | 0.1 , x<=0 
 *        -
 * allow a small, positive gradient when the unit is not active
 * https://ai.stanford.edu/~amaas/papers/relu_hybrid_icml2013_final.pdf
 * 
 * @author Zoran Sevarac
 */
public final class LeakyRelu implements ActivationFunction, Serializable {

    @Override
    public float getValue(final float x) {
        return ( x >= 0 ? x : 0.1f*x );  
    }

    @Override
    public float getPrime(final float y) {
         return ( y > 0 ? 1 : 0.1f);
    }
    
}