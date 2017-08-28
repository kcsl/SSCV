package com.kcsl.sscv;

// import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.kcsl.sscv.helpers.Chat;
import com.kcsl.sscv.helpers.Member;
// import com.kcsl.sscv.helpers.MyException;
import com.kcsl.sscv.helpers.PublicIdentity;

/*
 * SSCV 6
 * 
 * Question: Is there a side channel in time in the given code which helps an attacker to find out whether two users were previously connected?
 * 
 * Additional Information: This code was extracted from a Chat application. The chat application allows users to connect with each other, form chat groups, and send messages to each other.
 * The variables have been renamed to make sense intuitively.
 */

public class SSCV_6 {
	
	private final Map<String,Member> nameToMember = new HashMap<String,Member>();
	
	private final Map<PublicIdentity,Member> idToMember = new HashMap<PublicIdentity,Member>();
	
	private final Chat chat;
	
	public SSCV_6(Chat chat) {
		this.chat = chat;
	}

	public void addMember(Member member, boolean shouldBeKnownMember) {
		boolean previouslyConnected = knowsMember(member.grabName());
        StringBuilder stringBuilder = new StringBuilder();
        
        if (shouldBeKnownMember && !previouslyConnected) {
            stringBuilder.append("WARNING: " + member.grabName() + " has a different identity. This may not be the same user");
        }

        if (!previouslyConnected) {
            new MemberManagerExecutor(member, stringBuilder).invoke();
        } else {
            addMemberToMemberHistoryAdviser(stringBuilder);
        }

        stringBuilder.append(member.grabName());
        if (member.hasCallbackAddress()) {
            stringBuilder.append(". callback on: " + member.obtainCallbackAddress());
        }
        chat.sendReceipt(true, 0, member);

        chat.printMemberMsg(stringBuilder.toString());
	}
	
	private void addMemberToMemberHistoryAdviser(StringBuilder stringBuilder) {
        stringBuilder.append("Reconnected to ");
    }
	
	public boolean knowsMember(String name) {
	       return nameToMember.containsKey(name);
	}

	public void storeMember(Member member) {
        String name = member.grabName();
        PublicIdentity identity = member.getIdentity();
        nameToMember.put(name, member);
        idToMember.put(identity, member);
//        ArrayList<Member> storedMembers = new ArrayList<Member>(connectionsService.addMemberToFile(member));
//        if (!storedMembers.containsAll(nameToMember.values())) {
//            storeMemberHome();
//        }
    }
	
//	public void storeMemberHome() throws MyException {
//		throw new MyException("Stored users and known users are not the same");
//	}
	
	private class MemberManagerExecutor {
        private Member member;
        private StringBuilder stringBuilder;

        public MemberManagerExecutor(Member member, StringBuilder stringBuilder) {
            this.member = member;
            this.stringBuilder = stringBuilder;
        }

        public void invoke() {
            storeMember(member);
            stringBuilder.append("Connected to new user ");
        }
    }

}
