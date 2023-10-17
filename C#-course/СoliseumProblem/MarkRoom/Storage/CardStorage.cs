using CardsLib;

namespace MarkRoom.Storage;

public class CardStorage : ICardStorage
{
    private Card[]? _deck;
    private int? _pickedNumber;
    private bool _numberSet = false;
    private object _lockObject = new ();

    public void SetDeck(Card[] deck)
    {
        _deck = deck;
    }

    public void SetPickedNumber(int pick)
    {
        lock (_lockObject)
        {
            _pickedNumber = pick;
            _numberSet = true;
            Console.WriteLine($"Pick: {pick}");
            Monitor.Pulse(_lockObject);
        }
    }

    public CardColor GetPickedColor()
    {
        lock (_lockObject)
        {
            while (!_numberSet)
            {
                Monitor.Wait(_lockObject);
            }
            if (_deck == null || _pickedNumber == null)
            {
                throw new NullReferenceException("Deck or pick number is null");
            }

            _numberSet = false;
            var color = _deck[_pickedNumber.Value].Color;
            Console.WriteLine($"Picked color: {color}");
            return color;
        }
    }
}