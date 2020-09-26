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

//Reads arguments for number of agents, removed and replaces with proposal offer
//		Object[] args = getArguments();
//		if(args.length>0 && args[0]!=null) nAgents = Integer.parseInt((String)args[0]);
		
		// Register the auction service in the yellow pages
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
				biders[i] = biderAgents[i].getLocalName();
				System.out.println(biderAgents[i].getLocalName());
				
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
						 String p = "10000";//Initial prize 0
						 int max_bid=0;
						 int max_bider=0;
						 int prev_max_bider=0;
						 int prev_max_bid=0;
						 int[] bids = new int[nAgents];
						 myGui.removeItem();
						 while(up) {//while there are bids
							 prev_max_bid = Integer.parseInt(p);
							 prev_max_bider = max_bider;
							 System.out.println("Sending bid: "+p);
							 send_msg(p);
							 bids = recv_msg(bids);
							 
							 max_bid = prev_max_bid;
							 max_bider = find_max_bider(bids);
							 int num_bids = num_bid(bids);
							 
							 if( num_bids==0 || num_bids!=1) {
								 if(Integer.parseInt(p)>0)
									 p = Integer.toString(Integer.parseInt(p)-500);
								 else {
									 p=Integer.toString(0);
									 send_msg("-1");
									 String sold = myGui.auc_item.remove();
									 myGui.auc_price.remove();
									 myGui.textArea.append(sold+" not sold"+"\n");
									 up=false;
								 } 									 
							 }else if(num_bids==1) {
								 announce_winner(max_bider,max_bid);
								 announce_loser(max_bider,max_bid);
								 up=false;
							 }else if(Integer.parseInt(p)==0) {
								 send_msg("-1");
								 String sold = myGui.auc_item.remove();
								 myGui.auc_price.remove();
								 myGui.textArea.append(sold+" not sold"+"\n");
								 up=false;
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
		// Deregister from the yellow pages
		try {
			DFService.deregister(this);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		myGui.dispose();
		System.out.println("Auctioneer "+getAID().getLocalName()+" terminating");
	}
	public void send_msg(String price) {
		 ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		 msg.setContent( price );
		 for (int i = 0; i<nAgents; i++) {
//			 msg.addReceiver( new AID( "bider" + i, AID.ISLOCALNAME) );
			 //Using the names that Auctioner got from service
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


