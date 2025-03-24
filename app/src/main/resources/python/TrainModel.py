from sklearn.model_selection import train_test_split
from SAO import SaveAsONNX

class TrainModel:
    def __init__(self, output_name, model, random_state = 42, test_size = 0.2):
        self.output_name = output_name
        self.model = model
        self.random_state = random_state
        self.test_size = test_size
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
        converter = SaveAsONNX(model=self.model, model_name=self.output_name, X=X, y=y)
        converter.convert(input_shape=[None, self.n_features])
        converter.save()
