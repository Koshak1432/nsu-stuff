using CardsLib;
using ElonRoom.Storage;
using MassTransit;
using Messages;
using StrategiesLib;

namespace ElonRoom.MessageConsumers;

public class DeckMessageConsumer : IConsumer<DeckMessage>
{
    private readonly ICardStorage _cardStorage;
    private readonly IPublishEndpoint _publish;
    
    public DeckMessageConsumer(ICardStorage cardStorage, IPublishEndpoint publish)
    {
        _cardStorage = cardStorage;
        _publish = publish;
    }

    public async Task Consume(ConsumeContext<DeckMessage> context)
    {
        var deck = context.Message.deck;
        PrintCards(deck);
        _cardStorage.SetDeck(deck);
        ICardPickStrategy strategy = new FirstBlackStrategy();
        var pick = strategy.Pick(deck.ToArray());
        await _publish.Publish(new CardMessage {CardNumber = pick, Whose = "Elon"});
    }

    private static void PrintCards(Card[] deck)
    {
        foreach (var card in deck)
        {
            Console.Write(card.ToString());
        }

        Console.WriteLine("");
    }
}