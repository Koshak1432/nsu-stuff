import matplotlib.pyplot as plt
import torch
from numpy.random import randn
import torch.nn as nn
import torch.optim as optim
import torchvision.datasets as datasets
import torchvision.transforms as transforms
from torch.utils.data import DataLoader

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")


class Discriminator(nn.Module):
    def __init__(self, hidden_dim = 64, num_classes = 10, img_size = 28):
        super().__init__()

        # IMPORTANT: out_volume = (input_volume - kernel_size + 2*padding) / stride + 1
        self.disc = nn.Sequential(
            nn.Conv2d(1 + 1, hidden_dim, kernel_size = 4, stride = 2, padding = 1),  # 64x14x14
            nn.LeakyReLU(0.2),
            nn.Dropout(0.4),
            nn.Conv2d(hidden_dim, hidden_dim, kernel_size = 4, stride = 2, padding = 1),  # 64x7x7
            nn.LeakyReLU(0.2),
            nn.Dropout(0.4),
            nn.Flatten(),
            nn.Linear(7 * 7 * hidden_dim, 1),
            nn.Sigmoid()
        )
        self.img_size = img_size
        self.embed = nn.Embedding(num_classes, img_size * img_size)

    def forward(self, x, labels):
        embedding = self.embed(labels).view(labels.shape[0], 1, self.img_size, self.img_size)
        x = torch.cat((x, embedding), dim = 1)
        return self.disc(x)


class Generator(nn.Module):
    def __init__(self, latent_dim, hidden_dim, embed_size, num_classes = 10, img_size = 28):
        super().__init__()

        # IMPORTANT: out_volume = (input_volume - kernel_size + 2*padding) / stride + 1
        self.generator = nn.Sequential(
            nn.Linear(latent_dim + embed_size, 7 * 7 * hidden_dim),
            nn.LeakyReLU(0.3),
            nn.Unflatten(1, (hidden_dim, 7, 7)),
            nn.ConvTranspose2d(hidden_dim, hidden_dim, kernel_size = 4, stride = 2),
            nn.BatchNorm2d(hidden_dim),
            nn.LeakyReLU(0.3),
            nn.ConvTranspose2d(hidden_dim, hidden_dim, kernel_size = 4, stride = 2),
            nn.BatchNorm2d(hidden_dim),
            nn.LeakyReLU(0.3),
            nn.Conv2d(hidden_dim, 1, kernel_size = 7),
        )

        self.embed = nn.Embedding(num_classes, embed_size)

    def forward(self, x, labels):
        embedding = self.embed(labels)
        x = torch.cat((x, embedding), dim = 1)
        out = self.generator(x)
        return out


def show_imgs(images, epoch):
    fig, axes = plt.subplots(6, 6, figsize = (6, 6))
    for ax, img in zip(axes.flatten(), images):
        ax.imshow(img.to("cpu").detach().numpy().reshape(28, 28), cmap = "gray")
        ax.axis("off")
    filename = "./images/fake_images_%03d.png" % (epoch + 1)
    plt.savefig(filename)
    plt.show()


def run():
    lr = 2e-4
    batch_size = 256
    num_epochs = 110
    latent_dim = 100
    hidden_gen = 128
    hidden_disc = 128
    gen_embed = 100
    dataset = datasets.MNIST(root = "dataset/", transform = transforms.ToTensor(), train = True,
                             download = True)
    loader = DataLoader(dataset, batch_size = batch_size, shuffle = True)
    discriminator = Discriminator(hidden_disc).to(device)
    generator = Generator(latent_dim, hidden_gen, gen_embed).to(device)
    discriminator_opt = optim.Adam(discriminator.parameters(), lr = lr)
    generator_opt = optim.Adam(generator.parameters(), lr = lr)
    loss_func = nn.BCELoss()
    print(generator)
    print(discriminator)

    generator.load_state_dict(torch.load("models/generator"))
    discriminator.load_state_dict(torch.load("models/discriminator"))

    generator.train()
    discriminator.train()

    for epoch in range(num_epochs):
        for batch_idx, (real_img, labels) in enumerate(loader):
            labels = labels.to(device)
            real_img = real_img.to(device)
            b_size = real_img.shape[0]
            # discriminator train
            # max log(D(real)) + log(1 - D(G(noise)))
            noise = torch.randn(b_size, latent_dim).to(device)
            discriminator_real = discriminator(real_img, labels)
            fake_img = generator(noise, labels)
            discriminator_fake = discriminator(fake_img.detach(), labels)
            loss_disc_real = loss_func(discriminator_real, torch.ones_like(discriminator_real))
            loss_disc_fake = loss_func(discriminator_fake, torch.zeros_like(discriminator_fake))
            loss_disc = (loss_disc_fake + loss_disc_real) / 2
            discriminator.zero_grad()
            loss_disc.backward()
            discriminator_opt.step()
            # generator train
            # min log(1 - D(G(noise))) --> max log(D(G(noise)))
            out = discriminator(fake_img, labels)
            loss_gen = loss_func(out, torch.ones_like(discriminator_fake))
            generator.zero_grad()
            loss_gen.backward()
            generator_opt.step()

            if batch_idx % 400 == 0:
                print(f"Epoch [{epoch + 1} / {num_epochs}] | loss D: {loss_disc:.4f}, Loss G: {loss_gen:.4f}")
                if (epoch + 1) % 5 == 0:
                    generator.eval()
                    fake_imgs = generator(noise, labels).to("cpu")
                    show_imgs(fake_imgs, epoch)
                    generator.train()
        if epoch % 5 == 0:
            torch.save(generator.state_dict(), "models/generator")
            torch.save(discriminator.state_dict(), "models/discriminator")


if __name__ == '__main__':
    run()
