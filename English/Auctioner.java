package auction;
import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.*;

import auction.AuctionerGUI;

/**
 * JADE agent representing an auctioneer of an auction.
 * It has single sequential behavior representing its lifecycle.
 */
public class Auctioner extends Agent {
	private AuctionerGUI myGui;
	private int nAgents=2;
	private int prev=0;
	private int steps=0;
	private String[] biders;
   @Override
    protected void setup() {
		System.out.println("Hello! Auctioneer: "+getAID().getLocalName()+" is ready");
		myGui = new AuctionerGUI(this);
//		Object[] args = getArguments();
//		if(args.length>0 && args[0]!=null) nAgents = Integer.parseInt((String)args[0]);
		
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("auction");
		sd.setName("auction");
		dfd.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(this, dfd); 
			System.out.println("Found the following bider agents:");
			AID[] biderAgents = new AID[result.length];
			nAgents = result.length;
			for (int i = 0; i < result.length; ++i) {
				biderAgents[i] = result[i].getName();
				bider[i] = biderAgents[i].getLocalName();
				System.out.println(biderAgents[i].getName());
			}
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		
		addBehaviour(new TickerBehaviour(this, 60000) {
			 protected void onTick() {
				 System.out.println("Starting auctions:");
				 if(!myGui.items.isEmpty() && (myGui.textField_1.getText()!="")) {
					 while(!myGui.items.isEmpty()) {//While there are items
						 System.out.println("-----------------------------------------------------");
						 boolean up=true;
						 String p = "0";
						 Random initial_price = new Random();
						 p = Integer.toString(initial_price.nextInt(100));//Initial prize
						 int max_bid=0;
						 int max_bider=0;
						 int prev_max_bider=0;
						 int prev_max_bid=0;
						 int[] bids = new int[nAgents];
						 myGui.removeItem();
						 while(up) {//while there are bids
							 prev_max_bid = max_bid;
							 prev_max_bider = max_bider;
							 System.out.println("Sending bid: "+p);
							 send_msg(p);
							 bids = recv_msg(bids);
							 
							 max_bid = find_max_bid(bids);
							 max_bider = find_max_bider(bids);
							 int num_bids = num_bid(bids);
							 
							 if( num_bids==0 ) {
								 announce_winner(prev_max_bider,prev_max_bid);
								 announce_loser(prev_max_bider,prev_max_bid);
								 up=false;
							 }else if(num_bids==1) {
								 announce_winner(max_bider,max_bid);
								 announce_loser(max_bider,max_bid);
								 up=false;
							 }else {
								 p = Integer.toString(max_bid);
							 }
							 System.out.println("Steps: "+steps);
							 System.out.println("-----------------------------------------------------");
							 steps++;
						 } 
					 }
					 send_msg("-2");
					 doDelete();
				 } 
			 }
		});
    }
	// Put agent clean-up operations here
	protected void takeDown() {
		myGui.dispose();
		System.out.println("Auctioneer "+getAID().getLocalName()+" terminating");
	}
	public void send_msg(String price) {
		 ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		 msg.setContent( price );
		 for (int i = 0; i<nAgents; i++) {
			//  msg.addReceiver( new AID( "bider" + i, AID.ISLOCALNAME) );
			msg.addReceiver( new AID( biders[i], AID.ISLOCALNAME) );

		 }
		 send(msg);	
	}
	public int[] recv_msg(int[] bids) {
		for(int i=0;i<nAgents;i++) {
			 ACLMessage reply = blockingReceive();
			 AID sender = reply.getSender();
			 int poss=0;
			 String p = sender.getLocalName();
			 poss =  Character.getNumericValue(p.charAt(5));
			 bids[poss]=Integer.parseInt(reply.getContent());
		 }
		return bids;
	}
	
	public void announce_winner(int max_bider, int max_bid) {
		myGui.sell_item(max_bider,max_bid);
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setContent( Integer.toString(-3));
		msg.addReceiver( new AID( "bider" + max_bider, AID.ISLOCALNAME) );
		send(msg);	
	}
	public void announce_loser(int max_bider, int max_bid) {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		if(myGui.items.isEmpty() && myGui.auc_item.isEmpty()) msg.setContent("-2");
		else msg.setContent( Integer.toString(-1));
		for(int i=0;i<nAgents;i++) {
			if(i!=max_bider)
				msg.addReceiver( new AID( "bider" + max_bider, AID.ISLOCALNAME) );			
		}
		send(msg);	
	}
	public int num_bid(int[] bids) {
		int total=0;
		for(int i=0;i<bids.length;i++) {
			if(bids[i]>0) total++;
		}
		return total;
	}
	public int find_max_bid(int[] bids) {
		int max=0;
		for(int i=0;i<bids.length;i++) {
			if(bids[i]>max) max=bids[i];
		}
		return max;
	}
	public int find_max_bider(int[] bids) {
		int max=0,idx=0;
		for(int i=0;i<bids.length;i++) {
			if(bids[i]>max) { 
				max=bids[i];
				idx=i;
			}
		}
		return idx;
	}
}


