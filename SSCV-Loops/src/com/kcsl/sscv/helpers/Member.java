package com.kcsl.sscv.helpers;

public class Member {
	
	private final String name;
	private final PublicIdentity identity;
    private Connection connection;
	
	private Member(String name, PublicIdentity identity, Connection connection) {
        this.name = name;
        this.identity = identity;
        this.connection = connection;
    }
	
	public Member(String name, Connection connection) {
        this(name, connection.getTheirIdentity(), connection);
    }

    public Member(String name, PublicIdentity identity) {
        this(name, identity, null);
    }

    public String grabName() {
        return name;
    }

	public boolean hasCallbackAddress() {
		return identity.hasCallbackAddress();
	}
	
	public PublicIdentity getIdentity() {
		return this.identity;
	}

	public NetworkAddress obtainCallbackAddress() {
        return identity.fetchCallbackAddress();
    }
}
