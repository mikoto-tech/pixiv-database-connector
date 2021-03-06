package net.mikoto.pixiv.database.connector;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import net.mikoto.pixiv.api.http.database.artwork.GetArtwork;
import net.mikoto.pixiv.api.http.database.artwork.GetArtworks;
import net.mikoto.pixiv.api.http.database.artwork.InsertArtworks;
import net.mikoto.pixiv.api.model.Artwork;
import net.mikoto.pixiv.database.connector.exception.GetArtworkException;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

import static net.mikoto.pixiv.api.http.database.artwork.GetArtwork.PARAM_ARTWORK_ID;
import static net.mikoto.pixiv.api.http.database.artwork.GetArtworks.*;
import static net.mikoto.pixiv.api.util.HttpApiUtil.getHttpApi;

/**
 * @author mikoto
 * @date 2022/5/21 4:25
 */
@Component("databaseConnector")
public class SimpleDatabaseConnector implements DatabaseConnector {
    private static final int SUCCESS_CODE = 200;
    private static final String SUCCESS_KEY = "success";
    private static final String BODY = "body";
    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .build();
    private static final MediaType MEDIA_TYPE = MediaType.parse("text/plain");
    @Value("${mikoto.pixiv.patcher.storage.database.address}")
    private String defaultDatabaseAddress;
    @Value("${mikoto.pixiv.patcher.storage.database.key}")
    private String defaultDatabaseKey;

    @Override
    public Artwork getArtworkById(int artworkId) throws GetArtworkException, IOException {
        return getArtwork(defaultDatabaseAddress, artworkId);
    }

    /**
     * Insert artworks.
     *
     * @param address  The address of pixiv-database.
     * @param key      The key.
     * @param artworks The artworks.
     */
    @Override
    public void insertArtworks(String address, String key, Artwork[] artworks) throws IOException {
        if (address == null) {
            address = defaultDatabaseAddress;
        }
        if (key == null) {
            key = defaultDatabaseKey;
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.fluentPut("key", key);
        jsonObject.fluentPut("body", artworks);
        RequestBody requestBody = RequestBody.create(
                new String(Base64.getEncoder().encode(jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8))),
                MEDIA_TYPE
        );
        Request insertRequest = new Request.Builder()
                .url(
                        address + getHttpApi(InsertArtworks.class)
                )
                .post(requestBody)
                .build();
        Response insertResponse = OK_HTTP_CLIENT.newCall(insertRequest).execute();
        insertResponse.close();
    }

    @Override
    public Artwork[] getArtworks(String address, String credential, Sort.Direction order, String properties, int pageCount) throws IOException, GetArtworkException {
        if (address == null) {
            address = defaultDatabaseAddress;
        }

        Artwork[] artworks;
        Request artworksRequest = new Request.Builder()
                .url(
                        address + getHttpApi(
                                GetArtworks.class,
                                PARAM_CREDENTIAL + credential,
                                PARAM_ORDER + order,
                                PARAM_PROPERTIES + properties,
                                PARAM_PAGE_COUNT + pageCount
                        )
                )
                .get()
                .build();
        Response artworkResponse = OK_HTTP_CLIENT.newCall(artworksRequest).execute();
        if (artworkResponse.code() == SUCCESS_CODE) {
            JSONObject jsonObject = JSON.parseObject(Objects.requireNonNull(artworkResponse.body()).string());
            artworkResponse.close();
            if (jsonObject != null) {
                if (jsonObject.getBoolean(SUCCESS_KEY)) {
                    artworks = jsonObject.getJSONArray(BODY).toList(Artwork.class).toArray(new Artwork[0]);
                } else {
                    throw new GetArtworkException(jsonObject.getString("message"));
                }
            } else {
                throw new GetArtworkException("The json object is null!");
            }
        } else {
            throw new GetArtworkException(String.valueOf(artworkResponse.code()));
        }
        return artworks;
    }

    @Override
    public Artwork getArtwork(String address, int artworkId) throws GetArtworkException, IOException {
        if (address == null) {
            address = defaultDatabaseAddress;
        }

        Artwork artwork;
        Request artworkRequest = new Request.Builder()
                .url(
                        address + getHttpApi(
                                GetArtwork.class,
                                PARAM_ARTWORK_ID + artworkId
                        )
                )
                .get()
                .build();
        Response artworkResponse = OK_HTTP_CLIENT.newCall(artworkRequest).execute();
        if (artworkResponse.code() == SUCCESS_CODE) {
            JSONObject jsonObject = JSON.parseObject(Objects.requireNonNull(artworkResponse.body()).string());
            artworkResponse.close();
            if (jsonObject != null) {
                if (jsonObject.getBoolean(SUCCESS_KEY)) {
                    artwork = jsonObject.getObject(BODY, Artwork.class);
                } else {
                    throw new GetArtworkException(jsonObject.getString("message"));
                }
            } else {
                throw new GetArtworkException("The json object is null!");
            }
        } else {
            throw new GetArtworkException(String.valueOf(artworkResponse.code()));
        }
        return artwork;
    }
}
