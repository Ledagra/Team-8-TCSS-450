package edu.uw.tcss450.group8project.ui.messages;

import java.util.ArrayList;

public final class MessageInfo {



    private MessageInfo(){
        /* Empty private Constructor */
    }

    public static ArrayList<Message> getMessageData(){
        //Create some examples for the message list

        ArrayList<Message> messageList = new ArrayList<>();

        Message exp1 = new Message("Adam", "How are you?", "1 min ago");
        Message exp2 = new Message("Bob", "How are you?", "yesterday");
        Message exp3 = new Message("Carl", "How are you?", "2 mins ago");
        Message exp4 = new Message("David", "How are you?", "1 hour ago");
        Message exp5 = new Message("Fred", "How are you?", "2 hours ago");
        Message exp6 = new Message("George", "How are you?", "38 mins ago");
        Message exp7 = new Message("Harry", "How are you?", "40 mins ago");
        Message exp8 = new Message("Ian", "How are you?", "3 mins ago");
        Message exp9 = new Message("Jake", "How are you?", "2 days ago");
        Message exp10 = new Message("Kyle", "How are you?", "1 week ago");

        messageList.add(exp1);
        messageList.add(exp2);
        messageList.add(exp3);
        messageList.add(exp4);
        messageList.add(exp5);
        messageList.add(exp6);
        messageList.add(exp7);
        messageList.add(exp8);
        messageList.add(exp9);
        messageList.add(exp10);

        return messageList;

    }
}