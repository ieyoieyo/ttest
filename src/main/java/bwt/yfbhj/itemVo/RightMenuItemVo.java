package bwt.yfbhj.itemVo;

public class RightMenuItemVo {
//    public static final int TYPE_BACK = 0;
//    public static final int TYPE_NEXT = 1;
//    public static final int TYPE_REFRESH = 2;
//    public static final int TYPE_CLEAR = 3;
//    public static final int TYPE_ABOUT = 4;

    public static final int TYPE_REFRESH = 0;
    public static final int TYPE_CLEAR = 1;

    private int iconId;
    private String text;
    private int type;

    public RightMenuItemVo(){
    }

    public RightMenuItemVo(int type){
        this.type = type;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
