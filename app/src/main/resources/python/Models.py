from TrainModel import TrainModel
from sklearn.tree import DecisionTreeClassifier
from sklearn.neighbors import KNeighborsClassifier
from sklearn.ensemble import RandomForestClassifier
from sklearn import svm
from sklearn.linear_model import LogisticRegression
from sklearn.neural_network import MLPClassifier
import numpy as np

# File containing all models available to train data with.

class DecisionTreeClassifierWrapper(TrainModel):
    def __init__(self, criterion='gini', max_depth=None, test_size=0.2, random_state=42):
        model = DecisionTreeClassifier(criterion=criterion, max_depth=max_depth, random_state=random_state)
        super().__init__("DecisionTree.onnx", model = model, random_state = random_state, test_size = test_size)


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


class RFClassifier(TrainModel):
    def __init__(self, n_estimators=100, criterion="gini", max_depth=None, min_samples_split=2,
                 min_samples_leaf=1, max_features="sqrt", random_state=42, test_size=0.2):
        model = RandomForestClassifier(n_estimators = n_estimators, max_depth = max_depth, min_samples_split = min_samples_split,
                                       min_samples_leaf = min_samples_leaf, max_features = max_features, criterion = criterion)

        super().__init__("RandomForest.onnx", model = model, random_state = random_state, test_size = test_size)

class SVMClassifier(TrainModel):
    def __init__(self, C=1.0, kernel='rbf', degree=3, gamma='scale', coef0=0.0, shrinking=True,
                 probability=False, tol=0.001, cache_size=200, max_iter=-1, break_ties=False, test_size=0.2, random_state=42):
        model = svm.SVC(C=C, kernel=kernel, degree=degree, gamma=gamma, coef0=coef0,
                        shrinking=shrinking, probability=probability, tol=tol, cache_size=cache_size,
                        max_iter=max_iter, break_ties=break_ties, random_state=random_state)

        super().__init__("SVM.onnx", model = model, random_state = random_state, test_size = test_size)


class LRClassifier(TrainModel):
    def __init__(self, penalty = "l2", dual = False, tol = 1e-4, C = 1.0, fit_intercept = True, intercept_scaling = 1,
                 random_state = 42, test_size = 0.2, solver = "lbfgs", max_iter = 100, multi_class = "auto",
                 warm_start = False, n_jobs = None):

        model = LogisticRegression(penalty = penalty, dual = dual, tol = tol, C = C, fit_intercept = fit_intercept, intercept_scaling = intercept_scaling,
                                   random_state = random_state, solver = solver, max_iter = max_iter, multi_class = multi_class,
                                   warm_start = warm_start, n_jobs = n_jobs)

        super().__init__("LogisticRegression.onnx", model = model, random_state = random_state, test_size = test_size)

class MLPClassifierWrapper(TrainModel):
    def __init__(self, activation='relu', solver='adam', alpha=0.0001, hidden_layers=(10,5), random_state=42, test_size=0.2):        
        model = MLPClassifier(activation=activation, solver=solver, alpha=alpha, hidden_layer_sizes=hidden_layers, verbose=True)

        super().__init__("MLPClassifier.onnx", model=model, random_state=random_state, test_size=test_size)
