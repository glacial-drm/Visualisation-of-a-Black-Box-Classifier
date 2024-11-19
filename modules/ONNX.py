from sklearn.datasets import make_circles
from sklearn.svm import SVC
from sklearn.model_selection import train_test_split
from skl2onnx import convert_sklearn
from skl2onnx.common.data_types import FloatTensorType

# Generate the data
X, y = make_circles(n_samples=1000, factor=0.5, noise=0.1, random_state=0)

# Split the data
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

# Train the SVC model
clf = SVC(kernel='rbf', C=1, gamma='auto')
clf.fit(X_train, y_train)

# Convert the model to ONNX format
initial_type = [('float_input', FloatTensorType([None, X_train.shape[1]]))]
onnx_model = convert_sklearn(clf, initial_types=initial_type)

# Save the model to a file
with open("BC.onnx", "wb") as f:
    f.write(onnx_model.SerializeToString())

print("ONNX file has created")
