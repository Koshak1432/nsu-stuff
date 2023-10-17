using ElonRoom;
using ElonRoom.MessageConsumers;
using ElonRoom.Storage;
using MassTransit;

var builder = WebApplication.CreateBuilder(args);
builder.Services.AddSingleton<ICardStorage, CardStorage>();
builder.Services.AddMassTransit(x =>
{
    x.AddConsumer<DeckMessageConsumer>();
    x.AddConsumer<CardMessageConsumer>();
    x.UsingRabbitMq((context, cfg) =>
    {
        cfg.Host("localhost", "/", h =>
        {
            h.Username("guest");
            h.Password("guest");
            
        });
        cfg.ReceiveEndpoint("ElonRoom-pick", e =>
        {
            e.ConfigureConsumer<CardMessageConsumer>(context);
        });
        cfg.ReceiveEndpoint("ElonRoom-deck", e =>
        {
            e.ConfigureConsumer<DeckMessageConsumer>(context);
        });
    });
});
// Add services to the container.

builder.Services.AddControllers();
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

var app = builder.Build();

var busControl = app.Services.GetRequiredService<IBusControl>();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}


app.UseHttpsRedirection();

app.UseAuthorization();

app.MapControllers();

// запуск MassTransit
busControl.Start();
app.Run($"http://localhost:{Constants.ElonRoomUrl}");