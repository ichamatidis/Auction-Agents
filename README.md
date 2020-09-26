# Auction simulation with Agents

This program performs a virtual auction between agents. The auctioneer has a GUI that can
import items with their price and name for auction. Then biding agents connect to the serice
and make their bids, each agent has a different strategy that make their bides.

It uses the JADE library to implement Agents which provides protocols for message passing and agent 
communication. 

## Auctions Implemented
There are 3 kind of auctions implemented:
1. **English**: The most popular kind of auction, the highest bid wins item
2. **Dutch**: The auctioneer provides a very high price and eventually starts to lower it, the first that
accepts wins the bid
3. **Japanese**: The auctioneer starts to increace the prize and biders have to accept each round, the biders that 
wins is the one that stays untill the end and pays the ammount of the last round.
