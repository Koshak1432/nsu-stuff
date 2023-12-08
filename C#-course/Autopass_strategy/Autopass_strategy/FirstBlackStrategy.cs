using CardsLib;

namespace Autopass_strategy;

public class FirstBlackStrategy : ICardPickStrategy
{
    public int Pick(Card[] cards)
    {
        var res = 0;
        for (var i = 0; i < cards.Length; ++i)
        {
            if (cards[i].Color == CardColor.Red)
            {
                continue;
            }
            res = i;
            break;
        }
        return res;
    }
}