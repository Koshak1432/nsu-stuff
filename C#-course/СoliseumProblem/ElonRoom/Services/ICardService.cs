using CardsLib;

namespace ElonRoom.Services;

public interface ICardService
{
    public void SetDeck(Card[] deck);
    public void SetPickedNumber(int pick);
    public CardColor GetPickedColor();
}