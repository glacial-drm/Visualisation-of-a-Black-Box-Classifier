from sklearn.ensemble import RandomForestClassifier
from TrainModel import TrainModel


class RFClassifier(TrainModel):
    def __init__(self, n_estimators=100, criterion="gini", max_depth=None, min_samples_split=2,
                 min_samples_leaf=1, max_features="sqrt", random_state=42, test_size=0.2):
        model = RandomForestClassifier(n_estimators = n_estimators, max_depth = max_depth, min_samples_split = min_samples_split,
                                            min_samples_leaf = min_samples_leaf, max_features = max_features, criterion = criterion)

        super().__init__("RandomForest.onnx", model = model, random_state = random_state, test_size = test_size)
