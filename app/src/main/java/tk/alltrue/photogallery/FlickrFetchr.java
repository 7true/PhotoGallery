package tk.alltrue.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FlickrFetchr {

    private static final String TAG = "FlickrFetchr";
    private static final String API_KEY = "e665ea0daf73e8da198ed8c8be1c1aab";
//    private static final String FETCH_RECENTS_METHOD = "flickr.photos.getRecent";
//    private static final String SEARCH_METHOD = "flickr.photos.search";
//
//    private static final Uri ENDPOINT = Uri
//            .parse("https://api.flickr.com/services/rest")
//            .buildUpon()
//            .appendQueryParameter("api_key", API_KEY)
//            .appendQueryParameter("format", "json")
//            .appendQueryParameter("nojsoncallback", "1")
//            .appendQueryParameter("extras", "url_s")
//            //.appendQueryParameter("page", page + "")
//            .build();

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<GalleryItem> fetchItems(int page) {

        List<GalleryItem> items = new ArrayList<>();

        try {
            String url = Uri.parse("https://api.flickr.com/services/rest")
                    .buildUpon()
                    .appendQueryParameter("method", "flickr.photos.getRecent")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .appendQueryParameter("page", page + "")
                    .build().toString();

            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            //JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(items, jsonString);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }
        return items;
    }

    public Bitmap getUrlBitmap(String urlSpec) throws IOException {
        byte[] bytes = getUrlBytes(urlSpec);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void parseItems(List<GalleryItem> items, String jsonString) {

        Gson gson = new Gson();

        Recent recent = gson.fromJson(jsonString, Recent.class);

        List<Photo> photos = recent.getPhotos().getPhoto();

        for (int i = 0; i < photos.size(); i++) {
            Photo photo = photos.get(i);

            GalleryItem item = new GalleryItem();
            item.setId(photo.getId());
            item.setCaption(photo.getTitle());

            if (photo.getUrlS() != null) {
                item.setUrl(photo.getUrlS());
                items.add(item);
            }

        }
    }

    private class Recent {

        @SerializedName("photos")
        private Photos mPhotos;

        public Photos getPhotos() {
            return mPhotos;
        }

        public void setPhotos(Photos photos) {
            mPhotos = photos;
        }

    }

    private class Photos {

        @SerializedName("photo")
        private List<Photo> mPhoto;

        @SerializedName("page")
        private int mPage;

        public List<Photo> getPhoto() {
            return mPhoto;
        }

        public void setPhoto(List<Photo> photo) {
            mPhoto = photo;
        }

        public int getPage() {
            return mPage;
        }

        public void setPage(int page) {
            mPage = page;
        }

    }

    private class Photo {

        @SerializedName("id")
        private String mId;

        @SerializedName("title")
        private String mTitle;

        @SerializedName("url_s")
        private String mUrlS;

        public String getId() {
            return mId;
        }

        public void setId(String id) {
            mId = id;
        }

        public String getTitle() {
            return mTitle;
        }

        public void setTitle(String title) {
            mTitle = title;
        }

        public String getUrlS() {
            return mUrlS;
        }

        public void setUrlS(String urlS) {
            mUrlS = urlS;
        }

    }
}
