using CardsLib;
using ElonRoom.Storage;
using Microsoft.AspNetCore.Mvc;

namespace ElonRoom.Controllers;

[ApiController]
[Route("elon")]
public class ElonRoomController : ControllerBase
{
    private readonly ICardStorage _cardStorage;

    public ElonRoomController(ICardStorage cardStorage)
    {
        _cardStorage = cardStorage;
    }

    [HttpGet("color")]
    public ActionResult<CardColor> GetColor()
    {
        // тут ждать, пока лок на стораге откроется?
        // тогда раньше в консумерах надо залокать
        return _cardStorage.GetPickedColor();
    }
}