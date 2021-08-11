package auto.whiterhino.module.message_bridge;

public class MessageBridge {

    private static MessageBridge mInstance;

    private String userId;

    private String token;

    //构造方法声明为私有
    private MessageBridge(){
    }

    public static MessageBridge getInstance() {
        if (mInstance == null) {
            synchronized (MessageBridge.class) {
                if (mInstance == null) {
                    mInstance = new MessageBridge();
                }
            }
        }
        return mInstance;
    }




    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
