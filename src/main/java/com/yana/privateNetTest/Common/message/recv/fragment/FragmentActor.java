package com.yana.privateNetTest.Common.message.recv.fragment;

import com.yana.privateNetTest.Common.micromodel.ActorRef;
import com.yana.privateNetTest.Common.micromodel.Furture;
import com.yana.privateNetTest.Common.micromodel.LookUpActor;

public class FragmentActor extends ActorRef {

	public static void activate() {
		new FragmentActor();
	}

	private FragmentActor() {
		super();
		LookUpActor.registerActorRef(FragmentActor.class, this);
	}

	@Override
	public Furture<Object> ask() {
		return null;
	}
}
