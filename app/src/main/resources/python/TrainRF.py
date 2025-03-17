from sklearn.ensemble import RandomForestClassifier
from SAO import SaveAsONNX
from sklearn.model_selection import train_test_split

class RFClassifier:
    def __init__(self, n_estimators = 100, criterion = "gini", max_depth = None, min_samples_split = 2, min_samples_leaf = 1,
                 max_features = "sqrt",random_state = 42, test_size=0.2):
        self.model = RandomForestClassifier(n_estimators = n_estimators, max_depth = max_depth, min_samples_split = min_samples_split,
                                            min_samples_leaf = min_samples_leaf, max_features = max_features, criterion = criterion)
        self.random_state = random_state
        self.is_trained = False
        self.n_features = None
        self.test_size = test_size


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
        converter = SaveAsONNX(model=self.model, model_name="RandomForest.onnx", X=X, y=y)
        converter.convert(input_shape=[None, self.n_features])
        converter.save()