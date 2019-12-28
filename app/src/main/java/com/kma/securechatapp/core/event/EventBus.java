package com.kma.securechatapp.core.event;

import com.kma.securechatapp.core.api.model.MessagePlaneText;
import com.kma.securechatapp.core.api.model.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class EventBus {
    public abstract static class EvenBusAction{
        public  void onRefreshContact(){}
        public  void onRefreshConversation(){}
        public  void onNewMessage(MessagePlaneText msg){}
        public  void onNetworkStateChange(int State){}
        public  void onLogin(UserInfo u){}
        public  void onLogout(UserInfo u){}
        public  void toatMessage(String msg){}
        public  void noticShow(String message,String title){}
        public  void onChangeProfile(){}
    }
    private List<EvenBusAction> evenBusActions;

    private static EventBus _instance = null;

    public static EventBus getInstance(){

        if (_instance == null)
            _instance = new EventBus();
        return _instance;
    }
    private EventBus(){

        evenBusActions = new ArrayList<EvenBusAction>();
    }

    public void addEvent(EvenBusAction action){
            this.evenBusActions.add(action);
    }
    public void removeEvent(EvenBusAction action){
        this.evenBusActions.remove(action);
    }
    public void pushOnRefreshContact(){
        for (EvenBusAction eventBus:evenBusActions){
            eventBus.onRefreshConversation();
        }
    }
    public void pushOnRefreshConversation(){
        for (EvenBusAction eventBus:evenBusActions){
            eventBus.onRefreshContact();
        }
    }

    public void pushOnNewMessage(MessagePlaneText msg){
        for (EvenBusAction eventBus:evenBusActions){
            eventBus.onNewMessage(msg);
        }
    }

    public void pushOnNetworkStateChange(int state){
        for (EvenBusAction eventBus:evenBusActions){
            eventBus.onNetworkStateChange(state);
        }
    }

    public void pushOnLogin(UserInfo u){
        for (EvenBusAction eventBus:evenBusActions){
            eventBus.onLogin(u);
        }
    }

    public void pushOnLogout(UserInfo u){
        for (EvenBusAction eventBus:evenBusActions){
            eventBus.onLogout(u);
        }
    }

    public void pushToastMsg(String message){
        for (EvenBusAction eventBus:evenBusActions){
            eventBus.toatMessage(message);
        }
    }
    public void noticShow(String message,String title){
        for (EvenBusAction eventBus:evenBusActions){
            eventBus.noticShow(message,title);
        }
    }
    public void pushOnChangeProfile(){
        for (EvenBusAction eventBus:evenBusActions){
            eventBus.onChangeProfile();
        }
    }

}
