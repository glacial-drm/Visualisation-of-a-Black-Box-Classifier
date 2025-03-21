from sklearn.neighbors import KNeighborsClassifier
from TrainModel import TrainModel
import numpy as np

class KNClassifier(TrainModel):
    def __init__(self, n_neighbors = 5, weights = "uniform", algorithm = "auto", leaf_size = 30,
                 p = 2, metric = "minkowski", metric_params = None, n_jobs = None, random_state=42, test_size=0.2):

        model = KNeighborsClassifier(n_neighbors = n_neighbors, weights = weights, algorithm = algorithm,
                                     leaf_size = leaf_size, p = p, metric = metric, metric_params = metric_params,
                                     n_jobs = n_jobs)

        super().__init__("KNeighbours.onnx", model = model, random_state = random_state, test_size = test_size)

    def fit(self, X, y):
        # This gets round an issue with skl2onnx
        super().fit(X, y.astype(np.int32))

