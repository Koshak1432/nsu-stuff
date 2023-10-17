using ElonRoom.Storage;
using MassTransit;
using Messages;

namespace ElonRoom.MessageConsumers;

public class CardMessageConsumer : IConsumer<CardMessage>
{
    private readonly ICardStorage _cardStorage;
    
    public CardMessageConsumer(ICardStorage cardStorage)
    {
        _cardStorage = cardStorage;
    }
    
    public Task Consume(ConsumeContext<CardMessage> context)
    {
        var (cardNumber, from) = (context.Message.CardNumber, context.Message.Whose);

        if (from == "Elon")
        {
            return Task.CompletedTask;
        }
        _cardStorage.SetPickedNumber(cardNumber);
        return Task.CompletedTask;
    }
}