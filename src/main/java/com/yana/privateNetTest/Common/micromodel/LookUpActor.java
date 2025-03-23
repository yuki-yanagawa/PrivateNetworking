package com.yana.privateNetTest.Common.micromodel;

import java.util.HashMap;
import java.util.Map;

public class LookUpActor {
	@SuppressWarnings("rawtypes")
	private static Map<Class, ActorRef> routingMap = new HashMap<>();

	@SuppressWarnings("rawtypes")
	public synchronized static ActorRef getActorRef(Class actorKeyWordClass) {
		return routingMap.get(actorKeyWordClass);
	}

	@SuppressWarnings("rawtypes")
	public synchronized static void registerActorRef(Class actorKeyWordClass, ActorRef actorRef) {
		routingMap.put(actorKeyWordClass, actorRef);
	}
}
