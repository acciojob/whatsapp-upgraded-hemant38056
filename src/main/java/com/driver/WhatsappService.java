package com.driver;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class WhatsappService {

    int groupCount = 0;

    //users
    List<User> totalUsers = new ArrayList<>();  // list of total users
    HashMap<String, String> usersCreated = new HashMap<>();  // for validating mobile number
    HashMap<User, Group> userToGroup = new HashMap<>();  // user belongs to group


    //groups and admins
    HashMap<String, List<User>> groupsCreated = new HashMap<>();  // group name and users
    HashMap<String, User> groupAdminCreated = new HashMap<>(); // group name and admin
    List<Group> totalGroups = new ArrayList<>(); // total groups created


    //messages
    HashMap<Group, List<Message>> groupMessages = new HashMap<>(); // group and their messages
    List<Message> totalMessages = new ArrayList<>();  // total messages created
    List<Message> allGroupMessage = new ArrayList<>(); // all messages in all groups
    HashMap<User, List<Message>> userToMessage = new HashMap<>();  // user and their messages

    public String createUser(String name, String mobile) throws Exception{
        if(usersCreated.containsKey(mobile)){
//            throw new Exception("fail");
            return "fail";
        }
        usersCreated.put(mobile, name);
        User user = new User(name, mobile);
        totalUsers.add(user);

        return "SUCCESS";
    }

    public Group createGroup(List<User> users){
        if(users.size() == 2){
            String groupName = users.get(1).getName();
            groupsCreated.put(groupName, users);

//            String adminName = users.get(0).getName();
            groupAdminCreated.put(groupName, users.get(0));

            Group group = new Group();
            group.setName(groupName);
            group.setNumberOfParticipants(users.size());

            totalGroups.add(group);

            for(User user : users){
                userToGroup.put(user, group);
            }

            return group;
        }


        groupCount += 1;

        String groupName = "Group "+groupCount;
        groupsCreated.put(groupName, users);

//        String adminName = users.get(0).getName();
        groupAdminCreated.put(groupName, users.get(0));

        Group group = new Group();
        group.setName(groupName);
        group.setNumberOfParticipants(users.size());

        for(User user : users){
            userToGroup.put(user, group);
        }

        totalGroups.add(group);
        return group;

    }

    public int createMessage(String content){
        int id = totalMessages.size() + 1;
//        message.setId(id);
//        message.setContent(content);

        Message message = new Message(id, content);
        totalMessages.add(message);
        return id;
    }
    public int sendMessage(Message message, User sender, Group group) throws Exception{
        if (!totalGroups.contains(group)){
//            throw new Exception("Group does not exist");
            return -1;
        }

        String groupName = group.getName();
        List<User> groupUsers = groupsCreated.get(groupName);
        if(!groupUsers.contains(sender)){
//            throw new Exception("You are not allowed to send message");
            return -2;
        }

        if(!groupMessages.containsKey(group)){
            groupMessages.put(group, new ArrayList<>());
        }
        groupMessages.get(group).add(message);

        if(!userToMessage.containsKey(sender)){
            userToMessage.put(sender, new ArrayList<>());
        }

        userToMessage.get(sender).add(message);

        allGroupMessage.add(message);

        int count = groupMessages.get(group).size();

        return count;

    }

    public String changeAdmin(User approver, User user, Group group) throws Exception{
        if(!totalGroups.contains(group)){
//            throw new Exception("Group does not exist");
            return "G";
        }


        String groupName = group.getName();


        User admin = groupAdminCreated.get(groupName);
        if(!admin.equals(approver)){
//            throw new Exception("Approver does not have rights");
            return "A";
        }

        List<User> groupUsers = groupsCreated.get(groupName);

        if(!groupUsers.contains(user)){
//            throw new Exception("User is not a participant");
            return "U";
        }

        groupAdminCreated.put(groupName, user);

        return "SUCCESS";
    }

    public int removeUser(User user) throws Exception{
        if(!userToGroup.containsKey(user)){
//            throw new Exception("User not found");
            return -1;
        }

        Group group = userToGroup.get(user);
        String groupName = group.getName();

        User admin = groupAdminCreated.get(groupName);

        if(admin.equals(user)){
//            throw new Exception("Cannot remove admin");
            return -2;
        }

        List<Message> messagesSendByUser = userToMessage.get(user);

        List<Message> messageInGroup = groupMessages.get(group);

        for(int i = 0; i < messagesSendByUser.size(); i++){
            messageInGroup.remove(messagesSendByUser.get(i));   // update user message in group
            allGroupMessage.remove(messagesSendByUser.get(i));  // update user message in overall groups
        }

        userToGroup.remove(user);    // delete user group mapping
        userToMessage.remove(user);   // delete messages of user
        String mobileNo = user.getMobile();
        usersCreated.remove(mobileNo);  // delete mobile number
        totalUsers.remove(user);   // remove from total users

        List<User> updateGroupUser = groupsCreated.get(groupName);    //remove from group
        updateGroupUser.remove(user);
        group.setNumberOfParticipants(updateGroupUser.size());

        int numOfUsersInGroup = updateGroupUser.size();
        int numOfMessagesInGroup = messageInGroup.size();
        int numOfAllGroupMessage = allGroupMessage.size();

        return numOfAllGroupMessage + numOfMessagesInGroup + numOfUsersInGroup;







    }

    public String findMessage(Date start, Date end, int K) throws Exception{
        return "hello";
    }
}
