using CardsLib;

namespace ElonRoom.Storage;

public interface ICardStorage
{
    public void SetDeck(Card[] deck);
    public void SetPickedNumber(int pick);
    public CardColor GetPickedColor();
}