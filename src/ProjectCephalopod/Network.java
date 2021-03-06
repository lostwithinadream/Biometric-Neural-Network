/******************************************************
***  Class Name: Network
***  Class Author: Capri Sims
******************************************************
*** Purpose: Provide the methods for using a neural network
******************************************************
*** Date: June 21, 2016
******************************************************
*** TODO: Find best learning rate
*         Find best hidden layer size
*         Allow for different input formats
*******************************************************/
package ProjectCephalopod;

import static java.lang.Math.abs;
import java.util.Random;
import javax.swing.JTextArea;

public class Network {
    
    private int totalSize;
    private int inputSize;
    private int outputSize = 1;
    private int hiddenSize; 
    private int epochSize;
    private int start;

    private double[] expectedOutput = new double[outputSize];
    private double learningRate = .2; 
    
    private double[] input; 
    private double[] output = new double[outputSize];
    private double[] hidden; 
    private double[][] synapse0;
    private double[][] synapse1;
    private double bias0;
    private double bias1;
    
    double[] delta1;
    double[] delta0;
    
    private int iterations = 5000;
    
    private JTextArea text = new JTextArea("");
    
    public Network(double[] input){
        
        this.input = input;
        totalSize = input.length;
        inputSize = 30;
        normalize();
        start = 0;
        
        epochSize = totalSize / inputSize; //Breaks up total training input into chunks
        if((totalSize % inputSize) != 0){
            epochSize += 1;
        }

        hiddenSize = (inputSize + outputSize) / 2;
        hidden = new double[hiddenSize];
        
        synapse0 = new double[inputSize][hiddenSize];
        synapse1 = new double[hiddenSize][outputSize];
        
        delta1 = new double[outputSize];
        delta0 = new double[hiddenSize];

        initWeights(); //initialize the weights in synapse0 and synapse1

    }
    
    /******************************************************
    ***  Method Name: train
    ***  Method Author: Capri Sims
    ******************************************************
    *** Purpose: To train the network with training input
    *** Method Inputs: double[] expectedOutput
    *** Return value: None
    ******************************************************
    *** Date: July 21, 2016
    ******************************************************
    *** 
    *******************************************************/
    public void train(double[] expectedOutput){
        this.expectedOutput = expectedOutput;
        start = 0;
        //TRAINING
        for(int j = 0; j < epochSize; j ++){ //For each chunk
            while(abs(calcError()) > 1E-5){ //Until error becomes sufficiently low
                feedForward();
                text.setText("Training Result " + j + " : " + output[0]);
                feedBackwards();
            }
        }
        start += 30;
    }
    
    /******************************************************
    ***  Method Name: run
    ***  Method Author: Capri Sims
    ******************************************************
    *** Purpose: To use the network after training
    *** Method Inputs: None
    *** Return value: None
    ******************************************************
    *** Date: July 21, 2016
    ******************************************************
    *** 
    *******************************************************/
    public void run(){
        start = 0;
        
        epochSize = totalSize / inputSize;
        if((totalSize % inputSize) != 0){
            epochSize += 1;
        }

        for(int i = 0; i < epochSize; i++){
            feedForward();
            text.setText("Result "+ i + " : " + output[0]);
            start += 30;
        }
    }
    
    /******************************************************
    ***  Method Name: setInput
    ***  Method Author: Capri Sims
    ******************************************************
    *** Purpose: To set the input
    *** Method Inputs: double[] input
    *** Return value: None
    ******************************************************
    *** Date: July 21, 2016
    ******************************************************
    *** 
    *******************************************************/
    public void setInput(double[] input){
        this.input = input;
    }
    
    /******************************************************
    ***  Method Name: sigmoid
    ***  Method Author: Capri Sims
    ******************************************************
    *** Purpose: Provides activation function
    *** Method Inputs: double n
    *** Return value: double
    ******************************************************
    *** Date: July 21, 2016
    ******************************************************
    *** 
    *******************************************************/
    private double sigmoid(double n){
        return (1.0 /(1.0 + Math.pow(Math.E, -n)));
    }
    
    /******************************************************
    ***  Method Name: sigmoidPrime
    ***  Method Author: Capri Sims
    ******************************************************
    *** Purpose: Provides the derivative of the activation function
    ***          for back propagation 
    *** Method Inputs: double n
    *** Return value: double
    ******************************************************
    *** Date: July 21, 2016
    ******************************************************
    *** 
    *******************************************************/
    private double sigmoidPrime(double n){
        return (sigmoid(n)*(1-sigmoid(n)));
    }
    
    /******************************************************
    ***  Method Name: initWeights
    ***  Method Author: Capri Sims
    ******************************************************
    *** Purpose: Initializes the weights in synapse0 and 
    ***       synapse1 to random values between 0 and 1
    *** Method Inputs: None
    *** Return value: None
    ******************************************************
    *** Date: July 21, 2016
    ******************************************************
    *** 
    *******************************************************/
    private void initWeights(){
        
        Random random = new Random(); //random number generator 

        //Initialize synapse0 with random weights
        for(int i = 0; i < inputSize; i++){
            for(int j = 0; j < hiddenSize; j++){
                synapse0[i][j] = random.nextDouble();
                //System.out.println(synapse0[i][j]); //DEBUGGER
            }
        }
       
        //Initialize synapse1 with random weights
        for(int i = 0; i < hiddenSize; i++){
            for(int j = 0; j < outputSize; j++){
                synapse1[i][j] = random.nextDouble();
            }
        }
        bias0 = 0; //for later
        bias1 = 0;
    }
    
    /******************************************************
    ***  Method Name: feedForward
    ***  Method Author: Capri Sims
    ******************************************************
    *** Purpose: Calculates the output based on the input
    *** Method Inputs: None
    *** Return value: None
    ******************************************************
    *** Date: July 21, 2016
    ******************************************************
    *** 
    *******************************************************/
    private void feedForward(){
        
        double num = 0;
        
        //HIDDEN LAYER 
        //hidden = input * synapse1 + bias1
        for(int i = 0; i < hiddenSize; i++){
            for(int j = 0; j < inputSize; j++){
                num += input[j + start] * synapse0[j][i]; //multiply matrices 
            }
            num += bias0; //add bias
            hidden[i] = sigmoid(num); //squashed
            num = 0;
        }
        
        //OUTPUT LAYER
        //output = hidden * synapse 2 + bias2
        for(int j = 0; j < hiddenSize; j++){
            num += hidden[j] * synapse1[j][0]; //multiply matrices
        }
        num += bias1; //add bias
        output[0] = sigmoid(num); //squashed
        
        //System.out.println(output[0] + " | Error: " + (expectedOutput[0] - output[0])); //DEBUGGER
    }
    
    /******************************************************
    ***  Method Name: feedBackwards
    ***  Method Author: Capri Sims
    ******************************************************
    *** Purpose: Back propagates; Updates the weights for 
    ***       training
    *** Method Inputs: None
    *** Return value: None
    ******************************************************
    *** Date: July 21, 2016
    ******************************************************
    *** 
    *******************************************************/
    private void feedBackwards(){
        
        double error = 0;
        double change = 0;

        //FIND DELTA FOR OUTPUT
        for(int i = 0; i < outputSize; i++){
            error = expectedOutput[i] - output[i];
            delta1[i] = sigmoidPrime(output[i]) * error; 
        }
        
        //FIND DELTA FOR HIDDEN
        for(int i = 0; i < hiddenSize; i++){
            delta0[i] = sigmoidPrime(hidden[i]) * sumError(i); 
        }
        
        //UPDATE SYNAPSE1
        for(int i = 0; i < outputSize; i++){
            for(int j = 0; j < hiddenSize; j++){
                change = learningRate * delta1[i] * hidden[j];
                //System.out.println("Synapse1 : " + change); //DEBUGGER
                synapse1[j][i] += change;
                //System.out.println("Synapse1[" + j + "][" + i + "] = " + synapse1[j][i]); //DEBUGGER
            }
        }
        
        //UPDATE SYNAPSE0
        for(int i = 0; i < hiddenSize; i++){
            for(int j = 0; j < inputSize; j++){
                change = learningRate * delta0[i] * input[j + start];
                //System.out.println("Synapse0 : " + change); //DEBUGGER
                synapse0[j][i] += change;
                //System.out.println("Synapse0[" + j + "][" + i + "] = " + synapse0[j][i]); //DEBUGGER
            }
        }
    }
    
    /******************************************************
    ***  Method Name: sumError
    ***  Method Author: Capri Sims
    ******************************************************
    *** Purpose: Calculates the error for a given node
    *** Method Inputs: int j //the row
    *** Return value: None
    ******************************************************
    *** Date: July 21, 2016
    ******************************************************
    *** 
    *******************************************************/
    private double sumError(int j){
        double sum = 0;
        
        for(int i = 0; i < outputSize; i++){
            sum += synapse1[j][i] * delta1[i]; //CHECK
        }
        
        return sum;
    }
    
    /******************************************************
    ***  Method Name: normalize
    ***  Method Author: Capri Sims
    ******************************************************
    *** Purpose: Normalizes the input to a range between 0 and 1
    *** Method Inputs: None
    *** Return value: None
    ******************************************************
    *** Date: July 21, 2016
    ******************************************************
    *** 
    *******************************************************/
    private void normalize(){
        double min = 9999, max = 0, difference;
        
        for(int i = 0; i < totalSize; i++){ 
            if(input[i] > max){ //Find max
                max = input[i];
            }
            if(input[i] < min){ //Find min
                min = input[i];
            }
        }
        
        difference = max - min;
        
        for(int i = 0; i < inputSize; i++){
            input[i] = (input[i] - min) / difference; //normalization
            //System.out.println("Normalized: " + input[i]); //DEBUGGER
        }
    }
    
    /******************************************************
    ***  Method Name: calcError
    ***  Method Author: Capri Sims
    ******************************************************
    *** Purpose: Calculates the total error of the network
    *** Method Inputs: None
    *** Return value: None
    ******************************************************
    *** Date: July 21, 2016
    ******************************************************
    *** 
    *******************************************************/
    private double calcError(){
        return Math.pow(expectedOutput[0] - output[0], 2); 
    }
    
}
