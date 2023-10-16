using MarkRoom.Services;
using MassTransit;
using Messages;
using StrategiesLib;

namespace MarkRoom.MessageConsumers;

public class DeckMessageConsumer : IConsumer<DeckMessage>
{
    private readonly ICardService _cardService;

    public DeckMessageConsumer(ICardService cardService)
    {
        _cardService = cardService;
    }

    public Task Consume(ConsumeContext<DeckMessage> context)
    {
        var deck = context.Message.deck;
        _cardService.SetDeck(deck);
        ICardPickStrategy strategy = new FirstBlackStrategy();
        var pick = strategy.Pick(deck.ToArray());
        Console.WriteLine($"CONSUME DeckMessage in MARK: {deck.Length}");
        Console.WriteLine($"Mark pick: {pick}");
        // отправить пик марку
        return Task.CompletedTask;
    }
}