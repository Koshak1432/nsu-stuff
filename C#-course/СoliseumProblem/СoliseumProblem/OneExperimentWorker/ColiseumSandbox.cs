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
    private readonly IGodAssistant _assistant;
    private readonly HttpClient _client;
    private readonly ISendEndpointProvider _provider;

    public ColiseumSandbox(IGodAssistant assistant, HttpClient client, ISendEndpointProvider provider)
    {
        _assistant = assistant;
        _client = client;
        _provider = provider;
    }

    public async Task<int> RunExperiment(string? customOrder = null)
    {
        var deck = _assistant.CreateDeck();
        _assistant.ShuffleDeck(deck, customOrder);
        var (elonCards, markCards) = _assistant.SplitDeck(deck);
        await SendDeckToQueue("ElonRoom", elonCards);
        await SendDeckToQueue("MarkRoom", markCards);
        var elonColor = await GetColor(_client, ElonRoom.Constants.ElonRoomUrl, "elon");
        var markColor = await GetColor(_client, MarkRoom.Constants.MarkRoomUrl, "mark");

        Console.WriteLine($"GOT Elon color: {elonColor} and Mark color: {markColor}");
        var decision = elonColor == markColor;
        return decision ? 1 : 0;
    }

    private async Task SendDeckToQueue(string who,Card[] deck)
    {
        var endpoint = await _provider.GetSendEndpoint(new Uri($"queue:{who}-deck"));
        await endpoint.Send(new DeckMessage { deck = deck });
    }

    private static async Task<CardColor> GetColor(HttpClient client, string port, string toWhom)
    {
        var apiURL = $"http://localhost:{port}/{toWhom}/color";
        var response = await client.GetAsync(apiURL);
        
        if (!response.IsSuccessStatusCode)
        {
            throw new Exception($"Error while sending deck to {toWhom}, cause: {response.ReasonPhrase}");

        }
        var responseContent = await response.Content.ReadAsStringAsync();
        
        if (!Enum.TryParse(responseContent, out CardColor res))
        {
            throw new Exception($"Cannot parse {responseContent}");
        }
        return res;
    }
}