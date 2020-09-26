# Auction simulation with Agents

This program performs a virtual auction between agents. The auctioneer has a GUI that can
import items with their price and name for auction. Then biding agents connect to the serice
and make their bids, each agent has a different strategy that make their bides.

It uses the JADE library to implement Agents which provides protocols for message passing and agent 
communication. 

## Auctions Implemented
There are 3 kind of auctions implemented:
1. **English**: The most popular kind of auction, the highest bid wins the item
2. **Dutch**: The auctioneer provides a very high price and eventually starts to lower it, the first that
accepts wins the bid
3. **Japanese**: The auctioneer starts to increace the prize and biders have to accept each round, the biders that 
wins is the one that stays untill the end and pays the ammount of the last round

## Instuctions to run:

First you have to build using the **JADE** library using **javac** or importing it into **Eclipse** and include it
in the build path. Then you have to run first the **Auction** that promts you with the **JADE** GUI, where you specify the 
JADE agent package that you want to run with, also you have to specify the number of agents that will connect.
Lastly you run the biding agent class and add biding agents from the **JADE** GUI.
