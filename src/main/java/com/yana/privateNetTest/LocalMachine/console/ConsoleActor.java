package com.yana.privateNetTest.LocalMachine.console;

import com.yana.privateNetTest.Common.micromodel.ActorRef;
import com.yana.privateNetTest.Common.micromodel.Furture;
import com.yana.privateNetTest.Common.micromodel.LookUpActor;

public class ConsoleActor extends ActorRef 
{
	public static void activate() {
		new ConsoleActor();
	}

	private ConsoleActor() {
		super();
		LookUpActor.registerActorRef(ConsoleActor.class, this);
	}
	@Override
	public <T> Furture<T> ask() {
		// TODO Auto-generated method stub
		return null;
	}

}
