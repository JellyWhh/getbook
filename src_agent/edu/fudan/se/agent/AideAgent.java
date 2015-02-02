/**
 * 
 */
package edu.fudan.se.agent;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import edu.fudan.se.getbook.GetBookRunnable;
import edu.fudan.se.getbook.data.userTask.UserConfirmTask;
import edu.fudan.se.getbook.data.userTask.UserInputTask;
import edu.fudan.se.getbook.data.userTask.UserShowContentTask;
import edu.fudan.se.getbook.data.userTask.UserTask;
import edu.fudan.se.getbook.initial.GBApplication;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

/**
 * @author whh
 * 
 */
public class AideAgent extends Agent implements AideAgentInterface {

	private static final long serialVersionUID = 7573641846783926724L;

	private GBApplication application;
	private Context context;

	private ArrayList<UserTask> userUnreadTaskList;

	@Override
	protected void setup() {
		super.setup();
		Object[] args = getArguments();
		if (args != null && args.length > 0) {
			if (args[0] instanceof Context) {
				context = (Context) args[0];
			}
			if (args[1] instanceof GBApplication) {
				application = (GBApplication) args[1];
			}

			userUnreadTaskList = application.getUserUnreadTaskList();
		}

		registerO2AInterface(AideAgentInterface.class, this);

		/* 在黄页服务中注册服务 */
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("GETBOOK");
		sd.setName("GETBOOK");
		dfd.addServices(sd);

		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}

		// 循环接收并处理来自外部agent的消息
		addBehaviour(new HandleMesFromExternalAgent());

	}

	@Override
	protected void takeDown() {
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.fudan.se.agent.AideAgentInterface#sendMesToExternalAgent(edu.fudan
	 * .se.agent.AgentMessage)
	 */
	@Override
	public void sendNewTaskMesToExternalAgent(AgentMessage message) {
		this.addBehaviour(new SendNewTaskMesToExternalAgent(this, message));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.fudan.se.agent.AideAgentInterface#sendNewTaskMesToSelfAgent(edu.fudan
	 * .se.agent.AgentMessage)
	 */
	@Override
	public void sendNewTaskMesToSelfAgent(AgentMessage message) {
		this.addBehaviour(new SendNewTaskMesToSelfAgent(this, message));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.fudan.se.agent.AideAgentInterface#sendTaskRetToExternalAgent(edu.
	 * fudan.se.agent.AgentMessage)
	 */
	@Override
	public void sendTaskRetToExternalAgent(AgentMessage message) {
		this.addBehaviour(new SendTaskRetToExternalAgent(this, message));
	}

	/**
	 * 处理来自外部agent的消息
	 * 
	 * @author whh
	 * 
	 */
	private class HandleMesFromExternalAgent extends CyclicBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = -4159835298981404030L;

		/*
		 * (non-Javadoc)
		 * 
		 * @see jade.core.behaviours.Behaviour#action()
		 */
		@Override
		public void action() {
			ACLMessage msg = receive();
			if (msg != null) {
				Log.i("MY_LOG", "Handle mes from external agent...");
				try {
					AgentMessage message = (AgentMessage) msg
							.getContentObject();
					if (message.getHeader().equals(
							AgentMessage.AMHeader.NewTask)) {
						String time = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss").format(new Date());
						UserTask userTask = null;
						switch (message.getBody()) {
						case ToConfirm:
							userTask = new UserConfirmTask(time,
									message.getFromAgent(),
									message.getDescription());
							break;

						case ToDo:
							userTask = new UserTask(time,
									message.getFromAgent(),
									message.getDescription());
							break;
						case ToInput:
							userTask = new UserInputTask(time,
									message.getFromAgent(),
									message.getDescription());
							break;
						case ToShow:
							userTask = new UserShowContentTask(time,
									message.getFromAgent(),
									message.getDescription());
							((UserShowContentTask) userTask).setContent(message
									.getContent());
							break;
						}
						// 添加一个新任务
						userUnreadTaskList.add(0, userTask);
						// 新任务广播
						Intent broadcast_nda = new Intent();
						broadcast_nda.setAction("jade.task.NOTIFICATION");
						broadcast_nda.putExtra("Content",
								userTask.getDescription());
						context.sendBroadcast(broadcast_nda);

					} else if (message.getHeader().equals(
							AgentMessage.AMHeader.TaskRet)) {
						// 直接将消息发给GetBookRunnable
						GetBookRunnable.msgPool.offer(message);

					}
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * 发送新的newTask消息给外部agent
	 * 
	 * @author whh
	 * 
	 */
	private class SendNewTaskMesToExternalAgent extends OneShotBehaviour {

		private static final long serialVersionUID = -8599919799918935939L;
		private Agent agent;
		private AgentMessage message;

		public SendNewTaskMesToExternalAgent(Agent agent, AgentMessage message) {
			super(agent);
			this.agent = agent;
			this.message = message;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see jade.core.behaviours.Behaviour#action()
		 */
		@Override
		public void action() {
			// 先从platform上搜索agent
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName(getAID());
			ServiceDescription sd = new ServiceDescription();
			sd.setType("GETBOOK");
			sd.setName("GETBOOK");
			dfd.addServices(sd);

			try {
				DFAgentDescription[] result = DFService.search(agent, dfd);
				if (result == null || result.length == 0) {
					// 没有可委托对象
					AgentMessage am = new AgentMessage(null,
							AgentMessage.AMBody.NoDelegateTo);
					GetBookRunnable.msgPool.offer(am);
				} else {
					message.setFromAgent(agent.getLocalName());
					String toAgent = result[0].getName().getLocalName();

					ACLMessage aclMessage = new ACLMessage(ACLMessage.INFORM);
					aclMessage.addReceiver(new AID(toAgent, AID.ISLOCALNAME));
					aclMessage.setContentObject(message);
					send(aclMessage);

				}
			} catch (FIPAException | IOException e) {
				e.printStackTrace();
			}

		}

	}

	/**
	 * 发送新的task消息给自己
	 * 
	 * @author whh
	 * 
	 */
	private class SendNewTaskMesToSelfAgent extends OneShotBehaviour {
		private static final long serialVersionUID = -4855912616215748262L;
		private Agent agent;
		private AgentMessage message;

		public SendNewTaskMesToSelfAgent(Agent agent, AgentMessage message) {
			super(agent);
			this.agent = agent;
			this.message = message;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see jade.core.behaviours.Behaviour#action()
		 */
		@Override
		public void action() {
			message.setFromAgent(agent.getLocalName());

			ACLMessage aclMessage = new ACLMessage(ACLMessage.INFORM);
			aclMessage.addReceiver(new AID(agent.getLocalName(),
					AID.ISLOCALNAME));
			try {
				aclMessage.setContentObject(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
			send(aclMessage);
		}

	}

	private class SendTaskRetToExternalAgent extends OneShotBehaviour {

		private static final long serialVersionUID = 7756321452401736976L;
		private Agent agent;
		private AgentMessage message;

		public SendTaskRetToExternalAgent(Agent agent, AgentMessage message) {
			super(agent);
			this.agent = agent;
			this.message = message;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see jade.core.behaviours.Behaviour#action()
		 */
		@Override
		public void action() {
			
			String toAgent = message.getToAgent();
			message.setFromAgent(agent.getLocalName());

			ACLMessage aclMessage = new ACLMessage(ACLMessage.INFORM);
			aclMessage.addReceiver(new AID(toAgent, AID.ISLOCALNAME));
			try {
				aclMessage.setContentObject(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
			send(aclMessage);

		}
	}

}
