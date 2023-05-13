import torch
import torch.nn as nn
import torch.optim as optim
import torchvision
import torchvision.transforms as transforms
import torchvision.datasets as datasets
from torch.utils.data import DataLoader


class Discriminator(nn.Module):
    def __init__(self, img_dim):
        super().__init__()
        self.disc = nn.Sequential(
            nn.Linear(img_dim, 256),
            nn.ReLU(),
            nn.Linear(256, 64),
            nn.ReLU(),
            nn.Linear(64, 1),
            nn.Sigmoid()
        )

    def forward(self, x):
        return self.disc(x)


class Generator(nn.Module):
    def __init__(self, latent_dim, img_dim):
        super().__init__()
        self.generator = nn.Sequential(
            nn.Linear(latent_dim, 256),
            nn.ReLU(),
            nn.Linear(256, 128),
            nn.ReLU(),
            nn.Linear(128, img_dim)
        )

    def forward(self, x):
        return self.generator(x)


def run():
    device = "cuda" if torch.cuda.is_available() else "cpu"
    lr = 0.001
    batch_size = 64
    num_epochs = 40
    latent_dim = 64
    image_dim = 28 * 28
    noise = torch.randn()
    dataset = datasets.MNIST(root = "dataset/", transform = transforms.Compose([transforms.ToTensor()]),
                             download = True)
    loader = DataLoader(dataset, batch_size = batch_size, shuffle = True)
    discriminator = Discriminator(image_dim).to(device)
    generator = Generator(latent_dim, image_dim).to(device)
    discriminator_opt = optim.Adam(discriminator.parameters(), lr = lr)
    generator_opt = optim.Adam(generator.parameters(), lr = lr)
    loss_func = nn.BCELoss


# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    run()
