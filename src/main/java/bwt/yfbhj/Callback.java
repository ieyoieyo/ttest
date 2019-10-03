package bwt.yfbhj;

public interface Callback{
    public void onCheckFinish(String url);
    public void onNoQrcodeHandler();
    public void onNotFoundException();
}