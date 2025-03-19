from sklearn.tree import DecisionTreeClassifier
from TrainModel import TrainModel

class DecisionTreeClassifierWrapper(TrainModel):
    def __init__(self, criterion='gini', max_depth=None, test_size=0.2, random_state=42):
        model = DecisionTreeClassifier(criterion=criterion, max_depth=max_depth, random_state=random_state)
        super().__init__(model = model, random_state = random_state, test_size = test_size)