from sklearn import svm
from TrainModel import TrainModel

class SVMClassifier(TrainModel):
    def __init__(self, kernel='rbf', degree=3, C=1.0, gamma='scale', test_size=0.2, random_state=42):
        model = svm.SVC(kernel=kernel, degree=degree, C=C, gamma=gamma, probability=True)

        super().__init__("SVM.onnx", model = model, random_state = random_state, test_size = test_size)
