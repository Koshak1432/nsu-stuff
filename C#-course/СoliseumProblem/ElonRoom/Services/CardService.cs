using CardsLib;

namespace ElonRoom.Services;

public class CardService : ICardService
{
    private Card[]? _deck;
    private int? _pickedNumber;

    public void SetDeck(Card[] deck)
    {
        _deck = deck;
    }

    public void SetPickedNumber(int pick)
    {
        _pickedNumber = pick;
    }

    public CardColor GetPickedColor()
    {
        if (_deck == null || _pickedNumber == null)
        {
            throw new NullReferenceException("Deck or pick number is null");
        }

        return _deck[_pickedNumber.Value].Color;
    }
}