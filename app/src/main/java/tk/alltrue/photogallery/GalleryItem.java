package tk.alltrue.photogallery;

public class GalleryItem {
    public String getCaption() {
        return mCaption;
    }

    private String mCaption;
    private String mId;
    private String mUrl;


    public String getId() {
        return mId;
    }

    @Override
    public String toString() {
        return mCaption;
    }
    public void setId(String id) {
        mId = id;
    }

    public void setCaption(String caption) {
        mCaption = caption;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

}
