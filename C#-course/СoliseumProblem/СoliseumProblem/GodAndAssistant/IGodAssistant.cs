﻿using CardsLib;

namespace ColiseumProblem.GodAndAssistant;

public interface IGodAssistant
{
    public void ShuffleDeck(Card[] cards, string? customOrder = null);

    public Card[] CreateDeck();

    public Card[] GetDeck();
    
    public (Card[] elon, Card[] mark) SplitDeck(Card[] deck);

    public string GetDeckOrder();
}