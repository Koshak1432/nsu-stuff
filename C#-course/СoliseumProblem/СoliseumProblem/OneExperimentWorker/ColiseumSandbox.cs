using System.Text;
using System.Text.Json;
using CardsLib;
using ColiseumProblem.GodAndAssistant;
using MassTransit;
using Messages;

namespace ColiseumProblem.OneExperimentWorker;

// проводит 1 эксперимент
public class ColiseumSandbox : IColiseumSandbox
{
    private readonly IGod _god;
    private readonly IGodAssistant _assistant;
    private readonly HttpClient _client;
    private readonly ISendEndpointProvider _provider;

    public ColiseumSandbox(IGod god, IGodAssistant assistant, HttpClient client, ISendEndpointProvider provider)
    {
        _god = god;
        _assistant = assistant;
        _client = client;
        _provider = provider;
    }

    public async Task<int> RunExperiment(string? customOrder = null)
    {
        var deck = _assistant.CreateDeck();
        _assistant.ShuffleDeck(deck, customOrder);
        var (elonCards, markCards) = _assistant.SplitDeck(deck);
        _god.SetDecks(elonCards, markCards);
        await sendDeckTo("ElonRoom", elonCards);
        await sendDeckTo("MarkRoom", markCards);
        // todo
        // var elonPick = SendDeckToRoom(_client, ElonRoom.Constants.ElonRoomUrl, "elon", elonCards);
        // var markPick = SendDeckToRoom(_client, MarkRoom.Constants.MarkRoomUrl, "mark", elonCards);
        // var elonPick = 
        // var decision = _god.MakeDecision(elonPick.Result, markPick.Result);
        var decision = true;
        return decision ? 1 : 0;
    }

    private async Task sendDeckTo(string who,Card[] deck)
    {
        var endpoint = await _provider.GetSendEndpoint(new Uri($"queue:{who}-deck"));
        await endpoint.Send(new DeckMessage { deck = deck });
    }

    private static async Task<int> SendDeckToRoom(HttpClient client, string port, string toWhom, Card[] deck)
    {
        var apiURL = $"http://localhost:{port}/{toWhom}/pick";
        var content = new StringContent(JsonSerializer.Serialize(deck.ToList()), Encoding.UTF8, "application/json");
        var response = await client.PostAsync(apiURL, content);
        
        if (!response.IsSuccessStatusCode)
        {
            throw new Exception($"Error while sending deck to {toWhom}, cause: {response.ReasonPhrase}");

        }
        var responseContent = await response.Content.ReadAsStringAsync();
        
        if (!int.TryParse(responseContent, out var res))
        {
            throw new Exception($"Cannot parse {responseContent}");
        }
        return res;
    }

    private static void PrintCards(Card[] cards)
    {
        foreach (var card in cards)
        {
            Console.Write(card + " ");
        }

        Console.Write("\n");
    }
}