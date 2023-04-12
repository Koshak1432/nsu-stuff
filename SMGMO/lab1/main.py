import torch
import torch.utils.data
import torch.nn as nn
import torchvision.datasets as dset
import pandas as pd
import numpy as np
import torchvision.transforms
from sklearn.model_selection import train_test_split


def generate_result(model, loader, device):
    predictions = []
    model.eval()

    for images in loader:
        images = images[0].reshape(-1, 28 * 28) / 255
        images = images.to(device)

        outputs = model(images)
        _, predicted = torch.max(outputs, 1)
        predicted_np = (predicted.cpu().detach().numpy()).flatten()
        predictions.extend(predicted_np.tolist())

    return np.array(predictions).astype(str)


def train_model(model, train_loader, test_loader, loss, optimizer, num_epochs, device):
    train_history = []
    loss_history = []
    for epoch in range(num_epochs):
        model.train()
        correct = 0
        total = 0
        loss_val = 0
        for images, labels in train_loader:
            images = images.reshape(-1, 28 * 28) / 255
            images = images.to(device)
            labels = labels.to(device)

            outputs = model(images)
            loss_val = loss(outputs, labels)
            optim.zero_grad()
            loss_val.backward()
            optimizer.step()

            _, predicted = torch.max(outputs, 1)
            total += labels.shape[0]
            correct += predicted.eq(labels).sum().item()

        train_acc = 100 * float(correct) / total
        train_history.append(train_acc)
        loss_history.append(loss_val.item())
        print("Train accuracy: %.4f, loss: %.4f" % (train_acc, loss_val.item()))

    return generate_result(model, test_loader, device)


n_input = 1 * 28 * 28
n_hidden = 1000
n_out = 10
batch_size = 100
learn_rate = 0.001
num_epochs = 30

train_dset = pd.read_csv("./input/train.csv")
test_dset = pd.read_csv("./input/test.csv")

targets_train_np = train_dset.label.values
features_train_np = train_dset.loc[:, train_dset.columns != "label"].values

features_test_np = test_dset.values # вроде норм, дальше с этим как до этого со мнистом работал, а тренировочный оставить как есть

features_train, features_test_null, targets_train, targets_test_null = train_test_split(features_train_np,
                                                                                        targets_train_np,
                                                                                        test_size = 0.1)

featuresTrain = torch.from_numpy(features_train)
targetsTrain = torch.from_numpy(targets_train).type(torch.LongTensor)
featuresTest = torch.from_numpy(features_test_np)

# featuresTest = torch.from_numpy(features_test)
# targetsTest = torch.from_numpy(targets_test).type(torch.LongTensor)

train = torch.utils.data.TensorDataset(featuresTrain, targetsTrain)
test = torch.utils.data.TensorDataset(featuresTest)

train_loader = torch.utils.data.DataLoader(dataset = train, batch_size = batch_size, shuffle = True)
test_loader = torch.utils.data.DataLoader(dataset = test, batch_size = batch_size, shuffle = False)


device = torch.device("cuda")
my_nn = nn.Sequential(nn.Flatten(),
                      nn.Linear(n_input, n_hidden),
                      nn.ReLU(),
                      nn.Linear(n_hidden, n_out))
my_nn.to(device)
loss = nn.CrossEntropyLoss()
optim = torch.optim.Adam(my_nn.parameters(), lr = learn_rate)
sm = nn.Softmax(dim = 1)

image_indices_int = np.linspace(1, test_dset.shape[0], test_dset.shape[0], dtype = 'int')

# image_indices_str = np.concatenate((['ImageId'], image_indices_int.astype(str)))
image_indices_str = image_indices_int.astype(str)
print(image_indices_str[:20])
print(len(image_indices_str))
print(type(image_indices_str))
print(type(image_indices_str[2]))

test_res = train_model(my_nn, train_loader, test_loader, loss, optim, num_epochs, device)

print(len(image_indices_str))
print(len(test_res))
print(test_res[:10])
print(image_indices_str[:10])
res_data = {
    'ImageId': image_indices_str,
    'Label': test_res
}
df = pd.DataFrame(res_data)
df.to_csv("C:/pythonProject/lab1/out.csv", index = False)
torch.save(my_nn, "C:/pythonProject/lab1/model.pt")

