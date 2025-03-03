import numpy as np
from sklearn.datasets import make_circles
from sklearn import svm
from sklearn.model_selection import train_test_split
from SAO import SaveAsONNX

class SVMClassifier:
    def __init__(self, kernel='rbf', C=1.0, gamma='scale', test_size=0.2, random_state=42):
        self.model = svm.SVC(kernel=kernel, C=C, gamma=gamma, probability=True)
        self.test_size = test_size
        self.random_state = random_state
        self.is_trained = False
        self.n_features = None

    def fit(self, X, y):

        X_train, _, y_train, _ = train_test_split(
            X, y,
            test_size=self.test_size,
            random_state=self.random_state
        )

        self.n_features = X_train.shape[1]

        self.model.fit(X_train, y_train)
        self.is_trained = True
        return self
    
    def predict(self, X):
        if not self.is_trained:
            raise RuntimeError("Model not trained. Call fit() first.")
        return self.model.predict(X)
    
    def save(self, X, y):
        if not self.is_trained or self.n_features is None:
            raise RuntimeError("Model must be trained before saving")
        converter = SaveAsONNX(model=self.model, model_name="SVM.onnx", X=X, y=y)
        converter.convert(input_shape=[None, self.n_features])
        converter.save()
