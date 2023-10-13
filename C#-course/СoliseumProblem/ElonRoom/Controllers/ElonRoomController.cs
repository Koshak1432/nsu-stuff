using CardsLib;
using Microsoft.AspNetCore.Mvc;
using StrategiesLib;

namespace ElonRoom.Controllers;

[ApiController]
[Route("elon")]
public class ElonRoomController : ControllerBase
{
    [HttpPost("pick")]
    public ActionResult<int> Pick([FromBody] List<Card> deck)
    {
        ICardPickStrategy strategy = new FirstBlackStrategy();
        var res = strategy.Pick(deck.ToArray());
        return res;
    }
}