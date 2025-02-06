from skl2onnx import convert_sklearn
from skl2onnx.common.data_types import FloatTensorType
from onnx import StringStringEntryProto

class SaveAsONNX:
    def __init__(self, model, model_name="model.onnx", X=None, y=None):
        self.model = model
        self.model_name = model_name
        self.onnx_model = None
        self.X = X
        self.y = y

    def convert(self, input_shape):
        if not self.model:
            raise ValueError("No trained model provided for conversion.")
        # convert data 2 ONNX model
        initial_type = [('float_input', FloatTensorType(input_shape))]
        
        self.onnx_model = convert_sklearn(self.model, initial_types=initial_type)

        if self.X is not None and self.y is not None:
            self.onnx_model.metadata_props.append(StringStringEntryProto(key="training_data_X", value="test X"))
            self.onnx_model.metadata_props.append(StringStringEntryProto(key="training_data_y", value="test y"))

        print("Model converted to ONNX format.")

    def save(self):
        if not self.onnx_model:
            raise ValueError("No ONNX model available to save. Convert the model first.")
        
        # save ONNX model
        with open(self.model_name, "wb") as f:
            f.write(self.onnx_model.SerializeToString())
        print(f"ONNX model saved as {self.model_name}")
