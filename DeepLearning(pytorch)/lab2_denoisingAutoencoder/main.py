import pandas as pd
import numpy as np
import torch.utils.data
import torch
import torch.nn as nn
import matplotlib.pyplot as plt


class MyMNISTLabeled(torch.utils.data.Dataset):
    def __init__(self, file_name, len_to_slice):
        df = pd.read_csv(file_name)
        sliced_df = df.drop(df.index[len_to_slice:])
        targets = sliced_df.iloc[:, 0].values
        features = sliced_df.iloc[:, 1:].values / 255.0
        self.features_tensor = torch.tensor(features, dtype = torch.float32)
        self.targets_tensor = torch.tensor(targets)

    def __len__(self):
        return len(self.features_tensor)

    def __getitem__(self, idx):
        return self.features_tensor[idx], self.targets_tensor[idx]


class MyMNISTUnlabeled(torch.utils.data.Dataset):
    def __init__(self, file_name, len_to_slice):
        df = pd.read_csv(file_name)
        unlabeled_df = df.drop(df.index[:len_to_slice])
        targets = unlabeled_df.iloc[:, 0].values
        features = unlabeled_df.iloc[:, 1:].values / 255.0
        self.features_tensor = torch.tensor(features, dtype = torch.float32)
        self.targets_tensor = torch.tensor(targets)

    def __len__(self):
        return len(self.features_tensor)

    def __getitem__(self, idx):
        return self.features_tensor[idx], self.targets_tensor[idx]


class MyMNISTTest(torch.utils.data.Dataset):
    def __init__(self, file_name):
        df = pd.read_csv(file_name)
        targets = df.iloc[:, 0].values
        features = df.iloc[:, 1:].values / 255.0
        self.features_tensor = torch.tensor(features, dtype = torch.float32)
        self.targets_tensor = torch.tensor(targets)

    def __len__(self):
        return len(self.features_tensor)

    def __getitem__(self, idx):
        return self.features_tensor[idx], self.targets_tensor[idx]


class DenoisingAutoEncoder(torch.nn.Module):
    def __init__(self, last_layer_size):
        super().__init__()
        self.encoder = nn.Sequential(
            nn.Linear(28 * 28, 128),
            nn.Tanh(),
            nn.Linear(128, last_layer_size),
            nn.Sigmoid()
        )

        self.decoder = nn.Sequential(
            nn.Linear(last_layer_size, 128),
            nn.Tanh(),
            nn.Linear(128, 28 * 28),
            nn.Sigmoid()
        )

    def forward(self, x):
        encoded = self.encoder(x)
        decoded = self.decoder(encoded)
        return decoded


class AutoEncoderClassifier(torch.nn.Module):
    def __init__(self, encoder, last_layer_size):
        super().__init__()
        self.encoder = encoder
        self.output = torch.nn.Linear(last_layer_size, 10)
        self.sigmoid = torch.nn.Sigmoid()

    def forward(self, x):
        x = self.encoder(x)
        x = self.output(x)
        x = self.sigmoid(x)
        return x


def add_noise(x, std = 0.0):
    ret = x + std * torch.randn(size = x.shape)
    ret = torch.clip(ret, 0., 1.)
    return ret


def visualize_corrupted_batch(batch, noisy_batch):
    fig, sub = plt.subplots(2, 6, figsize = (12, 4))
    for image, ax in zip(batch, sub[0]):
        ax.imshow(image.reshape(28, 28), cmap = "gray")
        ax.axis("off")
    for image, ax in zip(noisy_batch, sub[1]):
        ax.imshow(image.reshape(28, 28), cmap = 'gray')
        ax.axis('off')
    plt.show()


def train_autoencoder(autoencoder, device, loss_func, num_epochs, optimizer, std, unlabeled_loader):
    print("AUTOENCODER TRAINING")
    autoencoder.train()
    for epoch in range(num_epochs):
        running_loss = 0.0
        for data, _ in unlabeled_loader:
            noisy_data = add_noise(data, std = std).to(device)
            data = data.to(device)

            output = autoencoder(noisy_data)
            cur_loss = loss_func(output, data)

            optimizer.zero_grad()
            cur_loss.backward()
            optimizer.step()
            running_loss += cur_loss.item()
        running_loss /= len(unlabeled_loader)
        print(f"Epoch №{epoch + 1}, loss = {running_loss:.3f}")


def train_supervised(classifier, device, labeled_loader, loss_func, num_epochs, optimizer):
    print("SUPERVISED TRAINING")
    classifier.train()
    for epoch in range(num_epochs):
        running_loss = 0.0
        for data, labels in labeled_loader:
            data, labels = data.to(device), labels.to(device)
            cur_loss = loss_func(classifier(data), labels)
            optimizer.zero_grad()
            cur_loss.backward()
            optimizer.step()
            running_loss += cur_loss.item()
        running_loss /= len(labeled_loader)
        print(f"Epoch №{epoch}, loss = {running_loss:.3f}")


def test_run(classifier, device, test_loader):
    print("TEST RUNNING")
    classifier.eval()
    total = 0
    correct = 0
    with torch.no_grad():
        for data, labels in test_loader:
            data, labels = data.to(device), labels.to(device)
            output = classifier(data)
            predicted = torch.max(output, 1)[1]
            total += labels.shape[0]
            correct += predicted.eq(labels).sum().item()
    print(f"Test accuracy: {100 * float(correct) / total}")


def visualize_decoded(dae, std, test_loader):
    dae.eval()
    batch = next(iter(test_loader))[0][:6]
    batch_noisy = add_noise(batch, std = std)
    batch_denoised = dae(batch_noisy)
    fig, sub = plt.subplots(2, 6, figsize = (12, 4))
    for image, ax in zip(batch, sub[0]):
        ax.imshow(image.to("cpu").reshape(28, 28), cmap = "gray")
        ax.axis("off")
    for image, ax in zip(batch_denoised, sub[1]):
        ax.imshow(image.to("cpu").detach().numpy().reshape(28, 28), cmap = 'gray')
        ax.axis('off')
    plt.show()


def main():
    batch_size = 1024
    labeled_len = 3000
    num_epochs = 30
    lr = 0.01
    std = 0.5
    last_layer_size = 64
    if torch.cuda.is_available():
        device = torch.device("cuda:0")
    else:
        device = torch.device("cpu")

    labeled_dset = MyMNISTLabeled("./mnist/mnist_train.csv", labeled_len)
    unlabeled_dset = MyMNISTUnlabeled("./mnist/mnist_train.csv", labeled_len)
    test_dset = MyMNISTTest("./mnist/mnist_test.csv")

    labeled_loader = torch.utils.data.DataLoader(labeled_dset, batch_size = batch_size, shuffle = True)
    unlabeled_loader = torch.utils.data.DataLoader(unlabeled_dset, batch_size = batch_size, shuffle = True)
    test_loader = torch.utils.data.DataLoader(test_dset, batch_size = batch_size, shuffle = True)

    dae = DenoisingAutoEncoder(last_layer_size).to(device)
    loss_func = torch.nn.MSELoss()
    optimizer = torch.optim.Adam(dae.parameters(), lr = lr)

    # batch = next(iter(unlabeled_loader))[0][:6]
    # noisy_batch = add_noise(batch, std)
    # visualize_corrupted_batch(batch, noisy_batch)

    # train_autoencoder(dae, device, loss_func, num_epochs, optimizer, std, unlabeled_loader)
    # torch.save(dae.state_dict(), "./models/dae_weights")

    dae.load_state_dict(torch.load("./models/dae_weights"))

    # visualize decoded images

    # dae = dae.to("cpu")
    # visualize_decoded(dae, std, test_loader)

    classifier = AutoEncoderClassifier(dae.encoder, last_layer_size).to(device)
    loss_func = nn.CrossEntropyLoss()
    optimizer_class = torch.optim.Adam(classifier.parameters(), lr = lr)

    train_supervised(classifier, device, labeled_loader, loss_func, num_epochs, optimizer_class)
    test_run(classifier, device, test_loader)


if __name__ == '__main__':
    main()
