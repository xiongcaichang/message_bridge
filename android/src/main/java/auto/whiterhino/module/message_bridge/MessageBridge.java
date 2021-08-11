package auto.whiterhino.module.message_bridge;

public class MessageBridge {

    private static MessageBridge mInstance;

    private static String baseUrl = "http://192.168.10.141:3085";

    private boolean reportLocation;

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

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static void setBaseUrl(String baseUrl) {
        MessageBridge.baseUrl = baseUrl;
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

    public boolean isReportLocation() {
        return reportLocation;
    }

    public void setReportLocation(boolean isReport) {
        this.reportLocation = isReport;
    }
}
