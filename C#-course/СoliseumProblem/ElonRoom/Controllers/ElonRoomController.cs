using CardsLib;
using ElonRoom.Services;
using Microsoft.AspNetCore.Mvc;
using StrategiesLib;

namespace ElonRoom.Controllers;

[ApiController]
[Route("elon")]
public class ElonRoomController : ControllerBase
{
    private ICardService _cardService;

    public ElonRoomController(ICardService cardService)
    {
        _cardService = cardService;
    }

    [HttpPost("pick")]
    public ActionResult<int> Pick([FromBody] List<Card> deck)
    {
        ICardPickStrategy strategy = new FirstBlackStrategy();
        var res = strategy.Pick(deck.ToArray());
        return res;
    }

    [HttpGet("color")]
    public ActionResult<CardColor> getColor()
    {
        throw new NotImplementedException();
    }
}