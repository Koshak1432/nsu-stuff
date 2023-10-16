using ElonRoom.Services;
using MassTransit;
using Messages;

namespace ElonRoom.MessageConsumers;

public class CardMessageConsumer : IConsumer<CardMessage>
{
    // private readonly ICardService _cardService;
    //
    // public CardMessageConsumer(ICardService cardService)
    // {
    //     _cardService = cardService;
    // }
    
    public Task Consume(ConsumeContext<CardMessage> context)
    {
        var cardNumber = context.Message.CardNumber;

        Console.WriteLine($"Received card: {cardNumber}");
        // _cardService.SetPickedNumber(cardNumber);
        return Task.CompletedTask;
    }
}