using ElonRoom.Services;
using MassTransit;
using Messages;

namespace ElonRoom.MessageConsumers;

public class CardMessageConsumer : IConsumer<CardMessage>
{
    private readonly ICardService _cardService;
    
    public CardMessageConsumer(ICardService cardService)
    {
        _cardService = cardService;
    }
    
    public Task Consume(ConsumeContext<CardMessage> context)
    {
        var (cardNumber, from) = (context.Message.CardNumber, context.Message.Whose);

        if (from == "Elon")
        {
            Console.WriteLine("Got own message");
            return Task.CompletedTask;
        }
        Console.WriteLine($"Received card: {cardNumber} from {from}");
        _cardService.SetPickedNumber(cardNumber);
        return Task.CompletedTask;
    }
}