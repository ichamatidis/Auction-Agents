package auction;
import java.util.Random;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class Bider extends Agent{
	private int budget=5000;
	private int spent=0;
	private int init_budget=5000;
	private int prev_bid=0;
	public void setup() {
		System.out.println("Hello bider: "+getAID().getLocalName()+" is ready for auctions.");
		Object[] args = getArguments();
		if(args[0]!=null && args.length>0) {
			budget = Integer.parseInt((String)args[0]);
			init_budget=budget;
		}
		//Register bider service
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("auction");
		template.addServices(sd);
		addBehaviour(new CyclicBehaviour(this){
			public void action() {
				ACLMessage msg = blockingReceive();
				if (msg!=null) {
					int bid = Integer.parseInt(msg.getContent());

					if(bid==-1){//Lose
						budget = init_budget - spent;
					}
					else if(bid==-2) doDelete();//End of auction
					else if(bid==-3){//Win
						budget-=prev_bid;
						spent+=prev_bid;
						prev_bid=0;
					}
					else if(getLocalName().equals("bider0")){
						bider0_strategy(bid);
						System.out.println(getLocalName()+" budget: "+budget);
					}
					else if(getLocalName().equals("bider1")) {
						bider1_strategy(bid);
						System.out.println(getLocalName()+" budget: "+budget);
					}
					else if(getLocalName().equals("bider2")) {
						bider2_strategy(bid);
						System.out.println(getLocalName()+" budget: "+budget);
					}
				}
			}
			
		});
	}
	public void takeDown() {
		System.out.println("Bider: "+getAID().getLocalName()+" is terminating.");
		
	}
	public void send_msg(String price) {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setPerformative( ACLMessage.INFORM );
		msg.addReceiver( new AID( "auc", AID.ISLOCALNAME) );
		msg.setContent(price);
		send(msg);
	}
	public void bider0_strategy(int bid) {//half
		int init_bid=bid;
		if(bid > budget)
			bid=0;
		else if(bid < budget/2)
			bid=1;
		else
			bid=0;
		System.out.println("bider0 sending:"+bid);
		send_msg(Integer.toString(bid));
		if(bid > 0)prev_bid = init_bid;
	}
	public void bider1_strategy(int bid) {//third
		int init_bid=bid;
		if(bid>budget)
			bid=0;
		else if(bid < budget/3)
			bid=1;
		else
			bid=0;
		System.out.println("bider1 sending:"+bid);
		send_msg(Integer.toString(bid));
		if(bid > 0)prev_bid = init_bid;
	}
	public void bider2_strategy(int bid) {//quarter
		int init_bid=bid;
		if(bid>budget)
			bid=0;
		else if(bid < budget/4)
			bid=1;
		else
			bid=0;
		System.out.println("bider2 sending:"+bid);
		send_msg(Integer.toString(bid));
		if(bid > 0)prev_bid = init_bid;
	}
}
