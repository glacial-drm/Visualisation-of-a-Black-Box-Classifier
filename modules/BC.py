import numpy as np


class LogisticRegression:
    def __init__(self, learning_rate = 0.003, iterations = 100):
        self.learning_rate = learning_rate
        self.iterations = iterations

    def sigmoid(self,z):
        return 1 / (1 + np.exp(-z))

    def fit(self, X, y):
        self.weights = np.random.randn(X.shape[1])
        self.bias = 0

        for i in range(self.iterations):
            y_hat = self.sigmoid(np.dot(X, self.weights) + self.bias)

            loss = (-1/len(X))*np.sum(y*np.log(y_hat)+(1-y)*np.log(1-y_hat))

            dw = (1/len(X))*np.dot(X.T, (y_hat-y))
            db = (1/len(X))*np.sum(y_hat-y)

            self.weights -= self.learning_rate * dw
            self.bias -= self.learning_rate * db

            if i % 10 == 0:
                print(f"Loss after iteration {i}: {loss}")

    def predict(self, X):
        y_hat = self.sigmoid(np.dot(X, self.weights) + self.bias)
        y_hat[y_hat >= 0.5] = 1
        y_hat[y_hat < 0.5] = 0
        return y_hat
    
    def score(self, y_pred, y):
        accuracy = (y_pred == y).sum() / len(y)
        return accuracy
