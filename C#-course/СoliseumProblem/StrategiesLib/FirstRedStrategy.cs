using CardsLib;

namespace StrategiesLib;

public class FirstRedStrategy : ICardPickStrategy
{
    public int Pick(Card[] cards)
    {
        var res = 0;
        for (var i = 0; i < cards.Length; ++i)
        {
            if (cards[i].Color == CardColor.Black)
            {
                continue;
            }
            res = i;
            break;
        }
        return res;
    }
}