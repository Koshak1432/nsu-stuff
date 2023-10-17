using ElonRoom.Services;
using MassTransit;
using Messages;
using StrategiesLib;

namespace ElonRoom.MessageConsumers;

public class DeckMessageConsumer : IConsumer<DeckMessage>
{
    private readonly ICardService _cardService;
    private readonly IPublishEndpoint _publish;
    
    public DeckMessageConsumer(ICardService cardService, IPublishEndpoint publish)
    {
        _cardService = cardService;
        _publish = publish;
    }

    public async Task Consume(ConsumeContext<DeckMessage> context)
    {
        var deck = context.Message.deck;
        _cardService.SetDeck(deck);
        ICardPickStrategy strategy = new FirstBlackStrategy();
        var pick = strategy.Pick(deck.ToArray());
        Console.WriteLine($"CONSUME DeckMessage in ELON: {deck.Length}");
        Console.WriteLine($"Elon pick: {pick}");
        await _publish.Publish(new CardMessage {CardNumber = pick, Whose = "Elon"});
    }
}