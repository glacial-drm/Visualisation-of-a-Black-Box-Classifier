from sklearn import svm
from TrainModel import TrainModel

class SVMClassifier(TrainModel):
    def __init__(self, kernel='rbf', C=1.0, gamma='scale', test_size=0.2, random_state=42):
        model = svm.SVC(kernel=kernel, C=C, gamma=gamma, probability=True)

        super().__init__(model = model, random_state = random_state, test_size = test_size)
