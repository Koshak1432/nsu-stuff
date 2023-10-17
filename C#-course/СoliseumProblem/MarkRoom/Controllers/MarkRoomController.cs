using CardsLib;
using MarkRoom.Storage;
using Microsoft.AspNetCore.Mvc;

namespace MarkRoom.Controllers;

[ApiController]
[Route("mark")]
public class MarkRoomController : ControllerBase
{
    private readonly ICardStorage _cardStorage;

    public MarkRoomController(ICardStorage cardStorage)
    {
        _cardStorage = cardStorage;
    }
    
    [HttpGet("color")]
    public ActionResult<CardColor> GetColor()
    {
        return _cardStorage.GetPickedColor();
    }
}