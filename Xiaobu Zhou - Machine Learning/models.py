import nn

class PerceptronModel(object):
    def __init__(self, dimensions):
        """
        Initialize a new Perceptron instance.

        A perceptron classifies data points as either belonging to a particular
        class (+1) or not (-1). `dimensions` is the dimensionality of the data.
        For example, dimensions=2 would mean that the perceptron must classify
        2D points.
        """
        self.w = nn.Parameter(1, dimensions)

    def get_weights(self):
        """
        Return a Parameter instance with the current weights of the perceptron.
        """
        return self.w

    def run(self, x):
        """
        Calculates the score assigned by the perceptron to a data point x.

        Inputs:
            x: a node with shape (1 x dimensions)
        Returns: a node containing a single number (the score)
        """
        "*** YOUR CODE HERE ***"
        return nn.DotProduct(x, self.w) 

    def get_prediction(self, x):
        """
        Calculates the predicted class for a single data point `x`.

        Returns: 1 or -1
        """
        "*** YOUR CODE HERE ***"
        if nn.as_scalar(self.run(x)) >= 0:
            return 1
        
        else:
            return -1

    def train(self, dataset):
        """
        Train the perceptron until convergence.
        """
        "*** YOUR CODE HERE ***"

        stop = False

        while not stop:

            for x, y in dataset.iterate_once(batch_size=1):
                if not self.get_prediction(x) == nn.as_scalar(y): #if not the same then update. 
                    self.w.update(x, nn.as_scalar(y))
                    break
            else:
                stop = True

class RegressionModel(object):
    """
    A neural network model for approximating a function that maps from real
    numbers to real numbers. The network should be sufficiently large to be able
    to approximate sin(x) on the interval [-2pi, 2pi] to reasonable precision.
    """
    def __init__(self):
        # Initialize your model parameters here
        "*** YOUR CODE HERE ***"
        # Set the number of neurons in the hidden layers.
        self.layer_size = 20

        # Set the batch size, which is the number of data samples used in one training iteration.
        # A batch size of 1 would be inefficient, so here it is set to 200.
        self.batch_size = 200

        # Set the learning rate, which affects how much the parameters change on each update.
        self.learning_rate = 0.2

        # Set the total number of layers in the neural network, including input and output layers.
        self.layer_number = 3

        # Initialize lists to store the weight matrices (W) and bias vectors (b) for each layer.
        self.W = []

        self.b = []
        # Loop through each layer to initialize the weight matrices and bias vectors.
        for k in range(self.layer_number):
            if k == 0:
                # For the first layer, initialize the weights from the input to the first hidden layer.
                # Assuming the input size is 1 (not specified), the size of W is (1, layer_size).
                self.W.append(nn.Parameter(1, self.layer_size))
                self.b.append(nn.Parameter(1, self.layer_size))
            elif k == self.layer_number - 1:
                # For the last layer, initialize the weights from the last hidden layer to the output.
                # Assuming the output size is 1 (not specified), the size of W is (layer_size, 1).
                self.W.append(nn.Parameter(self.layer_size, 1))
                self.b.append(nn.Parameter(1, 1))
            else:
                # For any middle layers, initialize the weights between successive hidden layers.
                # Both W and b are square matrices of shape (layer_size, layer_size).
                self.W.append(nn.Parameter(self.layer_size, self.layer_size))
                self.b.append(nn.Parameter(1, self.layer_size))



    def run(self, x):
        """
        Runs the model for a batch of examples.

        Inputs:
            x: a node with shape (batch_size x 1)
        Returns:
            A node with shape (batch_size x 1) containing predicted y-values
        """
        "*** YOUR CODE HERE ***"
        input = x

        for i in range(self.layer_number):

            fx = nn.Linear(input, self.W[i])
            output = nn.AddBias(fx, self.b[i])
          
            if i == self.layer_number - 1:
                predict_y = output
            else:
            
                input = nn.ReLU(output)

        return predict_y



    def get_loss(self, x, y):
        """
        Computes the loss for a batch of examples.

        Inputs:
            x: a node with shape (batch_size x 1)
            y: a node with shape (batch_size x 1), containing the true y-values
                to be used for training
        Returns: a loss node
        """
        "*** YOUR CODE HERE ***"
        predict_y = self.run(x)
        return nn.SquareLoss(predict_y, y)

    def train(self, dataset):
        """
        Trains the model.
        """
        "*** YOUR CODE HERE ***"
        lossnumber = float('inf')

        count = 0

        while lossnumber >= 0.01:
        
            for (x, y) in dataset.iterate_once(self.batch_size):
                
                loss = self.get_loss(x, y)

                lossnumber = nn.as_scalar(loss)
                
                grad_wrt = nn.gradients(loss, self.W + self.b)
       
                for i in range(self.layer_number):

                    self.W[i].update(grad_wrt[i], -self.learning_rate)
                    self.b[i].update(grad_wrt[len(self.W) + i], -self.learning_rate)
                count += 1

        print(count)

class DigitClassificationModel(object):
    """
    A model for handwritten digit classification using the MNIST dataset.

    Each handwritten digit is a 28x28 pixel grayscale image, which is flattened
    into a 784-dimensional vector for the purposes of this model. Each entry in
    the vector is a floating point number between 0 and 1.

    The goal is to sort each digit into one of 10 classes (number 0 through 9).

    (See RegressionModel for more information about the APIs of different
    methods here. We recommend that you implement the RegressionModel before
    working on this part of the project.)
    """
    def __init__(self):
        # Initialize your model parameters here
        "*** YOUR CODE HERE ***"

        # Initialize a list to store the number of neurons in each layer of the neural network
        self.layers = [300, 100, 10]
        self.batch_size = 500
        self.learning_rate = 0.2
        # Initialize sets for parameters W and b
        self.W = []
        self.b = []
        # Initialize W and b according to the structure of the neural network
        for i in range(len(self.layers)):
            if i == 0:
                # The input data for the first layer of the neural network is x, with size 784 in this case
                self.W.append(nn.Parameter(784, self.layers[i]))
                self.b.append(nn.Parameter(1, self.layers[i]))
            elif i == len(self.layers) - 1:
                # The output data for the last layer of the neural network is y, with size 10 in this case
                self.W.append(nn.Parameter(self.layers[i - 1], 10))
                self.b.append(nn.Parameter(1, 10))
            else:
                # For layers that are neither the first nor the last, their input and output parameters depend on adjacent layers
                self.W.append(nn.Parameter(self.layers[i - 1], self.layers[i]))
                self.b.append(nn.Parameter(1, self.layers[i]))

    def run(self, x):
        """
        Runs the model for a batch of examples.

        Your model should predict a node with shape (batch_size x 10),
        containing scores. Higher scores correspond to greater probability of
        the image belonging to a particular class.

        Inputs:
            x: a node with shape (batch_size x 784)
        Output:
            A node with shape (batch_size x 10) containing predicted scores
                (also called logits)
        """
        "*** YOUR CODE HERE ***"
        input = x
        # Construct a loop based on the structure of the neural network
        for i in range(len(self.layers)):
            # Construct loop body similar to linear regression
            fx = nn.Linear(input, self.W[i])
            output = nn.AddBias(fx, self.b[i])
            # According to the prompt, no need to call ReLU activation function for the last layer of the neural network
            if i == len(self.layers) - 1:
                predict_y = output
            else:
                # If it's not the last layer of the neural network, call the activation function and calculate the input data for the next layer
                input = nn.ReLU(output)
        return predict_y

    def get_loss(self, x, y):
        """
        Computes the loss for a batch of examples.

        The correct labels `y` are represented as a node with shape
        (batch_size x 10). Each row is a one-hot vector encoding the correct
        digit class (0-9).

        Inputs:
            x: a node with shape (batch_size x 784)
            y: a node with shape (batch_size x 10)
        Returns: a loss node
        """
        "*** YOUR CODE HERE ***"
        # Note that the loss function in this problem is different from the previous one!
        predicted_y = self.run(x)
        # Softmax function, also known as the normalized exponential function, is a generalization of the logistic function.
        # It compresses a K-dimensional vector A containing arbitrary real numbers to another K-dimensional real vector A',
        # such that each element of A' is in the range (0,1), and the sum of all elements is 1.
        return nn.SoftmaxLoss(predicted_y, y)

    def train(self, dataset):
        """
        Trains the model.
        """
        "*** YOUR CODE HERE ***"
        accuracy = 0
        while accuracy < 0.98:
            # Get (x, y) pairs from the dataset as training data
            for (x, y) in dataset.iterate_once(self.batch_size):
            # Calculate the loss value
                loss = self.get_loss(x, y)
                # Use nn.gradients for gradient descent algorithm
                grad_wrt = nn.gradients(loss, self.W + self.b)
                # Update parameters by iterating through grad_wrt

                for i in range(len(self.layers)):
                    self.W[i].update(grad_wrt[i], -self.learning_rate)
                    self.b[i].update(grad_wrt[len(self.W) + i], -self.learning_rate)

            # Update accuracy after looping through the dataset once
            accuracy = dataset.get_validation_accuracy()

            print("Accuracy:", accuracy)

class LanguageIDModel(object):
    """
    A model for language identification at a single-word granularity.

    (See RegressionModel for more information about the APIs of different
    methods here. We recommend that you implement the RegressionModel before
    working on this part of the project.)
    """
    def __init__(self):
        # Our dataset contains words from five different languages, and the
        # combined alphabets of the five languages contain a total of 47 unique
        # characters.
        # You can refer to self.num_chars or len(self.languages) in your code
        self.num_chars = 47
        self.languages = ["English", "Spanish", "Finnish", "Dutch", "Polish"]

        # Initialize your model parameters here
        "*** YOUR CODE HERE ***"
        self.learning_rate = 0.2
        # Set the learning rate for the model to 0.2

        self.batch_size = 10
        # Set the batch size for training to 10

        self.threshold = 0.85
        # Set the threshold for decision-making to 0.85

        self.hidden_size = 800
        # Set the size of the hidden layer in the neural network to 800

        # Parameters for f_initial
        self.W = nn.Parameter(self.num_chars, self.hidden_size)
        # Initialize the weight parameter matrix for the initial function f_initial,
        # with a shape determined by the number of input characters and the size of the hidden layer
        self.b = nn.Parameter(1, self.hidden_size)
        # Initialize the bias parameter vector for the initial function f_initial,
        # with a shape of (1, hidden_size)

        # Parameters for f
        self.W_hidden = nn.Parameter(self.hidden_size, self.hidden_size)
        # Initialize the weight parameter matrix for the function f,
        # with a shape determined by the size of the hidden layer (input and output sizes are the same)
        self.b_hidden = nn.Parameter(1, self.hidden_size)
        # Initialize the bias parameter vector for the function f,
        # with a shape of (1, hidden_size)

        # Parameters for the output layer
        self.W_output = nn.Parameter(self.hidden_size, len(self.languages))
        # Initialize the weight parameter matrix for the output layer,
        # with a shape determined by the size of the hidden layer and the number of output languages
        self.b_output = nn.Parameter(1, len(self.languages))
        # Initialize the bias parameter vector for the output layer,
        # with a shape of (1, number of output languages)


    def run(self, xs):
        """
        Runs the model for a batch of examples.

        Although words have different lengths, our data processing guarantees
        that within a single batch, all words will be of the same length (L).

        Here `xs` will be a list of length L. Each element of `xs` will be a
        node with shape (batch_size x self.num_chars), where every row in the
        array is a one-hot vector encoding of a character. For example, if we
        have a batch of 8 three-letter words where the last word is "cat", then
        xs[1] will be a node that contains a 1 at position (7, 0). Here the
        index 7 reflects the fact that "cat" is the last word in the batch, and
        the index 0 reflects the fact that the letter "a" is the inital (0th)
        letter of our combined alphabet for this task.

        Your model should use a Recurrent Neural Network to summarize the list
        `xs` into a single node of shape (batch_size x hidden_size), for your
        choice of hidden_size. It should then calculate a node of shape
        (batch_size x 5) containing scores, where higher scores correspond to
        greater probability of the word originating from a particular language.

        Inputs:
            xs: a list with L elements (one per character), where each element
                is a node with shape (batch_size x self.num_chars)
        Returns:
            A node with shape (batch_size x 5) containing predicted scores
                (also called logits)
        """
        "*** YOUR CODE HERE ***"
        hidden_state = nn.Linear(xs[0], self.W)
        # Calculate the hidden state based on the first input data, using linear transformation with weights self.W.
        # This initializes the hidden state for the recurrent neural network (RNN).

        # Calculate the subsequent nodes' f values based on the number of input data, completing the computation of the recurrent neural network.
        for x in xs[1:]:
            # Calculate the first part of the hidden state update
            part1 = nn.AddBias(nn.Linear(x, self.W), self.b)
            # Calculate the second part of the hidden state update
            part2 = nn.AddBias(nn.Linear(hidden_state, self.W_hidden), self.b_hidden)
            # Update the hidden state using ReLU activation function
            hidden_state = nn.ReLU(nn.Add(part1, part2))

        # Calculate the predictions of the output layer.
        y_predictions = nn.AddBias(nn.Linear(hidden_state, self.W_output), self.b_output)
        # Apply bias and linear transformation to the hidden state to get the predictions.
        # These predictions represent the output of the neural network.
        return y_predictions


    def get_loss(self, xs, y):
        """
        Computes the loss for a batch of examples.

        The correct labels `y` are represented as a node with shape
        (batch_size x 5). Each row is a one-hot vector encoding the correct
        language.

        Inputs:
            xs: a list with L elements (one per character), where each element
                is a node with shape (batch_size x self.num_chars)
            y: a node with shape (batch_size x 5)
        Returns: a loss node
        """
        "*** YOUR CODE HERE ***"
        predict_y = self.run(xs)
        return nn.SoftmaxLoss(predict_y,y)

    def train(self, dataset):
        """
        Trains the model.
        """
        "*** YOUR CODE HERE ***"
        accuracy = 0
        # Initialize accuracy to 0.
        while accuracy < self.threshold:
        # Continue training while accuracy is less than the defined threshold.
        
            # Get (x, y) pairs from the dataset as training data.
            for x, y in dataset.iterate_once(self.batch_size):
                
                # Calculate the loss value.
                loss = self.get_loss(x, y)
                
                # Combine all parameters and call nn.gradients for gradient descent algorithm.
                params = [self.W, self.b, self.W_hidden, self.b_hidden, self.W_output, self.b_output]
                gradients = nn.gradients(loss, params)
                
                # Iterate through the parameters and update them.
                for i in range(len(params)):
                    param = params[i]
                    param.update(gradients[i], -self.learning_rate)
                    
            # Update accuracy after looping through the dataset once.
            accuracy = dataset.get_validation_accuracy()
            
            # Print the current accuracy.
            print("Accuracy:", accuracy)

