using System.Text;
using System.Text.Json;
using CardsLib;
using ColiseumProblem.GodAndAssistant;

namespace ColiseumProblem.OneExperimentWorker;

// проводит 1 эксперимент
public class ColiseumSandbox : IColiseumSandbox
{
    private readonly IGod _god;
    private readonly IGodAssistant _assistant;
    private readonly HttpClient _client;

    public ColiseumSandbox(IGod god, IGodAssistant assistant, HttpClient client)
    {
        _god = god;
        _assistant = assistant;
        _client = client;
    }

    public int RunExperiment(string? customOrder = null)
    {
        var deck = _assistant.CreateDeck();
        _assistant.ShuffleDeck(deck, customOrder);
        var (elonCards, markCards) = _assistant.SplitDeck(deck);
        _god.SetDecks(elonCards, markCards);
        var elonPick = SendDeckToRoom(_client, ElonRoom.Constants.ElonRoomUrl, "elon", elonCards);
        var markPick = SendDeckToRoom(_client, MarkRoom.Constants.MarkRoomUrl, "mark", elonCards);
        var decision = _god.MakeDecision(elonPick.Result, markPick.Result);
        return decision ? 1 : 0;
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