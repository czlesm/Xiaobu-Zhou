# multiAgents.py
# --------------
# Licensing Information:  You are free to use or extend these projects for
# educational purposes provided that (1) you do not distribute or publish
# solutions, (2) you retain this notice, and (3) you provide clear
# attribution to UC Berkeley, including a link to http://ai.berkeley.edu.
# 
# Attribution Information: The Pacman AI projects were developed at UC Berkeley.
# The core projects and autograders were primarily created by John DeNero
# (denero@cs.berkeley.edu) and Dan Klein (klein@cs.berkeley.edu).
# Student side autograding was added by Brad Miller, Nick Hay, and
# Pieter Abbeel (pabbeel@cs.berkeley.edu).


from util import manhattanDistance
from game import Directions
import random, util

from game import Agent
from pacman import GameState

class ReflexAgent(Agent):
    """
    A reflex agent chooses an action at each choice point by examining
    its alternatives via a state evaluation function.

    The code below is provided as a guide.  You are welcome to change
    it in any way you see fit, so long as you don't touch our method
    headers.
    """


    def getAction(self, gameState: GameState):
        """
        You do not need to change this method, but you're welcome to.

        getAction chooses among the best options according to the evaluation function.

        Just like in the previous project, getAction takes a GameState and returns
        some Directions.X for some X in the set {NORTH, SOUTH, WEST, EAST, STOP}
        """
        # Collect legal moves and successor states
        legalMoves = gameState.getLegalActions()

        # Choose one of the best actions
        scores = [self.evaluationFunction(gameState, action) for action in legalMoves]
        bestScore = max(scores)
        bestIndices = [index for index in range(len(scores)) if scores[index] == bestScore]
        chosenIndex = random.choice(bestIndices) # Pick randomly among the best

        "Add more of your code here if you want to"
        """
        legalMoves = [Up, down, right]
        Scores = [1, 2, 2]
        bestScore = 2
        bestIndices = [1, 2]
        chosenIndex = 1
        """
        return legalMoves[chosenIndex]
    

    def evaluationFunction(self, currentGameState: GameState, action):
        """
        Design a better evaluation function here.

        The evaluation function takes in the current and proposed successor
        GameStates (pacman.py) and returns a number, where higher numbers are better.

        The code below extracts some useful information from the state, like the
        remaining food (newFood) and Pacman position after moving (newPos).
        newScaredTimes holds the number of moves that each ghost will remain
        scared because of Pacman having eaten a power pellet.

        Print out these variables to see what you're getting, then combine them
        to create a masterful evaluation function.
        """
        # Useful information you can extract from a GameState (pacman.py)
        successorGameState = currentGameState.generatePacmanSuccessor(action)
        newPos = successorGameState.getPacmanPosition()
        newFood = successorGameState.getFood()
        newGhostStates = successorGameState.getGhostStates()
        newScaredTimes = [ghostState.scaredTimer for ghostState in newGhostStates]

        "*** YOUR CODE HERE ***"
        if len(newFood.asList()) > 0:  
            #find the nearest food.
            foodNearest = (min([manhattanDistance(newPos, food) for food in newFood.asList()])) 
            fScore = 9 / foodNearest  

        else:
            fScore = 0
        #find the nearest ghost. 
        ghostNearest = min(
            [manhattanDistance(newPos, ghostState.configuration.pos) for ghostState in newGhostStates]) 
        
        if ghostNearest != 0: 
            checkingDistance = -10 / ghostNearest

        else:
            checkingDistance = 0

        return checkingDistance +  fScore + successorGameState.getScore() 



def scoreEvaluationFunction(currentGameState: GameState):
    """
    This default evaluation function just returns the score of the state.
    The score is the same one displayed in the Pacman GUI.

    This evaluation function is meant for use with adversarial search agents
    (not reflex agents).
    """
    return currentGameState.getScore()

class MultiAgentSearchAgent(Agent):
    """
    This class provides some common elements to all of your
    multi-agent searchers.  Any methods defined here will be available
    to the MinimaxPacmanAgent, AlphaBetaPacmanAgent & ExpectimaxPacmanAgent.

    You *do not* need to make any changes here, but you can if you want to
    add functionality to all your adversarial search agents.  Please do not
    remove anything, however.

    Note: this is an abstract class: one that should not be instantiated.  It's
    only partially specified, and designed to be extended.  Agent (game.py)
    is another abstract class.
    """

    def __init__(self, evalFn = 'scoreEvaluationFunction', depth = '2'):
        self.index = 0 # Pacman is always agent index 0
        self.evaluationFunction = util.lookup(evalFn, globals())
        self.depth = int(depth)

class MinimaxAgent(MultiAgentSearchAgent):
    """
    Your minimax agent (question 2)
    """

    def getAction(self, gameState: GameState):
        """
        Returns the minimax action from the current gameState using self.depth
        and self.evaluationFunction.

        Here are some method calls that might be useful when implementing minimax.

        gameState.getLegalActions(agentIndex):
        Returns a list of legal actions for an agent
        agentIndex=0 means Pacman, ghosts are >= 1

        gameState.generateSuccessor(agentIndex, action):
        Returns the successor game state after an agent takes an action

        gameState.getNumAgents():
        Returns the total number of agents in the game

        gameState.isWin():
        Returns whether or not the game state is a winning state

        gameState.isLose():
        Returns whether or not the game state is a losing state
        """
        "*** YOUR CODE HERE ***"
        bestScore = -float('inf')
        bestAction = None
        for action in gameState.getLegalActions(0):
            # Generate successor for each action and evaluate its value
            successor = gameState.generateSuccessor(0, action)
            score = self.evaluate(successor, 1, 0)  # Start evaluation with the first ghost (agentIndex = 1)
            # Update best score and action if current score is better
            if score > bestScore:
                bestScore = score
                bestAction = action
        
        return bestAction

    def evaluate(self, gameState, agentIndex, currentDepth):
        # evaluate the gamestate value
        if currentDepth == self.depth or gameState.isWin() or gameState.isLose():
            return self.evaluationFunction(gameState)
        
        # Pacman's move (maximizer)
        if agentIndex == 0:
            return max(self.evaluate(gameState.generateSuccessor(agentIndex, action),
                                     (agentIndex + 1) % gameState.getNumAgents(), currentDepth + 1 if agentIndex + 1 == gameState.getNumAgents() else currentDepth)
                       for action in gameState.getLegalActions(agentIndex))
        
        # Ghosts' move (minimizer)
        else:
            return min(self.evaluate(gameState.generateSuccessor(agentIndex, action),
                                     (agentIndex + 1) % gameState.getNumAgents(), currentDepth + 1 if agentIndex + 1 == gameState.getNumAgents() else currentDepth)
                       for action in gameState.getLegalActions(agentIndex))
        


class AlphaBetaAgent(MultiAgentSearchAgent):
    """
    Your minimax agent with alpha-beta pruning (question 3)
    """

    def getAction(self, gameState: GameState):
        """
        Returns the minimax action using self.depth and self.evaluationFunction
        """
        "*** YOUR CODE HERE ***"
        
    #     legalActions = gameState.getLegalActions(0)
    #     a = float('-inf')
    #     b = float('inf')
    #     scores = []

    #     for action in legalActions:
    #         score = self.minimax(gameState.generateSuccessor(0, action), 0, 1, a, b)
    #         if score > b:
    #             return action
    #         scores.append(score)
    #         a = max(a, score)

    #     bestScore = max(scores)
    #     bestIndices = [index for index in range(len(scores)) if scores[index] == bestScore]
    #     chosenIndex = random.choice(bestIndices)
    #     return legalActions[chosenIndex]
    
    # def minimax(self, gameState: GameState, depth: int, agentIndex: int, a: float, b: float):
    #     if depth == self.depth or gameState.isWin() or gameState.isLose():
    #         return self.evaluationFunction(gameState)
    #     if agentIndex == 0:
    #         return self.maxValue(gameState, depth, agentIndex, a, b)
    #     else:
    #         return self.minValue(gameState, depth, agentIndex, a, b)
        

    # def maxValue(self, gameState: GameState, depth: int, agentIndex: int, a: float, b: float):

    #     value = float('-inf')

    #     for action in gameState.getLegalActions(agentIndex):

    #         value = max(value, self.minimax(gameState.generateSuccessor(agentIndex, action), depth, agentIndex + 1, a, b))
    #         if value > b:
    #             return value
    #         a = max(a, value)
            
    #     return value
    

    # def minValue(self, gameState: GameState, depth: int, agentIndex: int, a: float, b: float):
    #     value = float('inf')

    #     for action in gameState.getLegalActions(agentIndex):
    #         if agentIndex == gameState.getNumAgents() - 1:
    #             value= min(value, self.minimax(gameState.generateSuccessor(agentIndex, action), depth + 1, 0, a, b))
    #         else:
    #             value = min(value, self.minimax(gameState.generateSuccessor(agentIndex, action), depth, agentIndex + 1, a, b))

    #         if value < a:
    #             return value
    #         b = min(b, value)
            
    #     return value 

        legalActions = gameState.getLegalActions(0)
        alpha = float('-inf')
        beta = float('inf')
        scores = []
        
        for action in legalActions:

            score = self.minimax(gameState.generateSuccessor(0, action), 0, 1, alpha, beta)

            if score > beta:
                return action
            
            scores.append(score)
            alpha = max(alpha, score)
        
        bestScore = max(scores)
        bestIndices = [index for index in range(len(scores)) if scores[index] == bestScore]
        chosenIndex = random.choice(bestIndices)
        return legalActions[chosenIndex]
    

    def minimax(self, gameState: GameState, depth: int, agentIndex: int, alpha: float, beta: float):

        if depth == self.depth or gameState.isWin() or gameState.isLose():
            return self.evaluationFunction(gameState)
        
        if agentIndex == 0:
            return self.maxValue(gameState, depth, agentIndex, alpha, beta)
        
        else:
            return self.minValue(gameState, depth, agentIndex, alpha, beta)
    
    def minValue(self, gameState: GameState, depth: int, agentIndex: int, alpha: float, beta: float):
        val = float('inf')

        for action in gameState.getLegalActions(agentIndex):

            if agentIndex == gameState.getNumAgents() - 1:
                val = min(val, self.minimax(gameState.generateSuccessor(agentIndex, action), depth + 1, 0, alpha, beta))

            else:
                val = min(val, self.minimax(gameState.generateSuccessor(agentIndex, action), depth, agentIndex + 1, alpha, beta))

            if val < alpha:
                return val
            
            beta = min(beta, val)
            
        return val 
    

    def maxValue(self, gameState: GameState, depth: int, agentIndex: int, alpha: float, beta: float):

        val = float('-inf')

        for action in gameState.getLegalActions(agentIndex):
            val = max(val, self.minimax(gameState.generateSuccessor(agentIndex, action), depth, agentIndex + 1, alpha, beta))

            if val > beta:
                return val
            
            alpha = max(alpha, val)
            
        return val

    

class ExpectimaxAgent(MultiAgentSearchAgent):
    """
      Your expectimax agent (question 4)
    """

    def getAction(self, gameState: GameState):
        """
        Returns the expectimax action using self.depth and self.evaluationFunction

        All ghosts should be modeled as choosing uniformly at random from their
        legal moves.
        """
        "*** YOUR CODE HERE ***"

        legalActions = gameState.getLegalActions(0)
        scores = [self.minimax(gameState.generateSuccessor(0, action), 0, 1) for action in legalActions]
        bestScore = max(scores)
        bestIndices = [index for index in range(len(scores)) if scores[index] == bestScore]
        chosenIndex = random.choice(bestIndices)

        return legalActions[chosenIndex]
    
    def minimax(self, gameState: GameState, depth: int, agentIndex: int):

        if depth == self.depth or gameState.isWin() or gameState.isLose():
            return self.evaluationFunction(gameState)
        
        if agentIndex == 0:
            return self.maxValue(gameState, depth, agentIndex)
        
        else:
            return self.minValue(gameState, depth, agentIndex)
    
    def minValue(self, gameState: GameState, depth: int, agentIndex: int):
        val = float(0)
        for action in gameState.getLegalActions(agentIndex):

            if agentIndex == gameState.getNumAgents() - 1:
                val = val + self.minimax(gameState.generateSuccessor(agentIndex, action), depth + 1, 0)

            else:
                val = val + self.minimax(gameState.generateSuccessor(agentIndex, action), depth, agentIndex + 1)

        val = val / len(gameState.getLegalActions(agentIndex))

        return val  

    def maxValue(self, gameState: GameState, depth: int, agentIndex: int):
        val = float('-inf')

        for action in gameState.getLegalActions(agentIndex):
            val = max(val, self.minimax(gameState.generateSuccessor(agentIndex, action), depth, agentIndex + 1))

        return val
    
def betterEvaluationFunction(currentGameState: GameState):
    """
    Your extreme ghost-hunting, pellet-nabbing, food-gobbling, unstoppable
    evaluation function (question 5).

    DESCRIPTION: <write something here so we know what you did>
    """
    "*** YOUR CODE HERE ***"
    newPos = currentGameState.getPacmanPosition()
    newFood = currentGameState.getFood()
    newGhostStates = currentGameState.getGhostStates()
    newScaredTimes = [ghostState.scaredTimer for ghostState in newGhostStates]

    if len(newFood.asList()) > 0:  #find the nearest food

        nearestFood = (min([manhattanDistance(newPos, f) for f in newFood.asList()]))  
        fScore = 9 / nearestFood  
    else:
        fScore = 0

    #find the nearest ghost
    ghostNearest = min([manhattanDistance(newPos, ghostState.configuration.pos) for ghostState in newGhostStates])  
    if ghostNearest != 0: 
        checkingDistance = -10 / ghostNearest
    else: 
        checkingDistance = 0

    if any(newScaredTimes):
        currentGameState.data.score += 100.0
        
    total = sum(newScaredTimes)  

    return total + currentGameState.getScore() + fScore + checkingDistance  

# Abbreviation
better = betterEvaluationFunction
