from sklearn.datasets import make_circles
from sklearn.svm import SVC
from sklearn.model_selection import train_test_split
from SAO import SaveAsONNX

X, y = make_circles(n_samples=1000, factor=0.5, noise=0.1, random_state=0)
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
clf = SVC(kernel='rbf', C=1, gamma='auto')
clf.fit(X_train, y_train)

converter = SaveAsONNX(model=clf, model_name="Hello.onnx")

converter.convert(input_shape=[None, X_train.shape[1]])
    
converter.save()

