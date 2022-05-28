package net.mikoto.pixiv.database.connector;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import net.mikoto.pixiv.api.http.database.artwork.GetArtwork;
import net.mikoto.pixiv.api.http.database.artwork.InsertArtworks;
import net.mikoto.pixiv.api.model.Artwork;
import net.mikoto.pixiv.database.connector.exception.GetArtworkException;
import net.mikoto.pixiv.database.connector.exception.WrongSignException;
import okhttp3.*;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Objects;

import static net.mikoto.pixiv.api.http.database.artwork.GetArtwork.PARAM_ARTWORK_ID;
import static net.mikoto.pixiv.api.util.HttpApiUtil.getHttpApi;
import static net.mikoto.pixiv.api.util.RsaUtil.getPublicKey;
import static net.mikoto.pixiv.api.util.RsaUtil.verify;
import static net.mikoto.pixiv.api.util.Sha256Util.getSha256;

/**
 * @author mikoto
 * @date 2022/5/21 4:25
 */
@Component("databaseConnector")
public class SimpleDatabaseConnector implements DatabaseConnector {
    private static final int SUCCESS_CODE = 400;
    private static final String SUCCESS_KEY = "success";
    private static final String BODY = "body";
    private static final String SIGN = "sign";
    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .build();
    private static final MediaType MEDIA_TYPE = MediaType.parse("text/plain");
    private final String DEFAULT_DATABASE_ADDRESS;
    private final String DEFAULT_DATABASE_PUBLIC_KEY;

    public SimpleDatabaseConnector(String defaultDatabaseAddress, String defaultDatabasePublicKey) {
        DEFAULT_DATABASE_ADDRESS = defaultDatabaseAddress;
        DEFAULT_DATABASE_PUBLIC_KEY = defaultDatabasePublicKey;
    }

    public SimpleDatabaseConnector() {
        DEFAULT_DATABASE_ADDRESS = "";
        DEFAULT_DATABASE_PUBLIC_KEY = "";
    }

    @Override
    public Artwork getArtworkById(int artworkId) throws GetArtworkException, IOException, InvalidKeySpecException, NoSuchAlgorithmException, SignatureException, InvalidKeyException, WrongSignException {
        return getArtwork(DEFAULT_DATABASE_ADDRESS, DEFAULT_DATABASE_PUBLIC_KEY, artworkId);
    }

    /**
     * Insert artworks.
     *
     * @param key      The key.
     * @param address  The address of pixiv-database.
     * @param artworks The artworks.
     */
    @Override
    public void insertArtworks(String key, String address, Artwork[] artworks) throws IOException {
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
    public Artwork[] getArtworks(String credential, Sort.Direction order, String properties, int pageCount) {
        return new Artwork[0];
    }

    @Override
    public Artwork getArtwork(String address, String publicKey, int artworkId) throws GetArtworkException, IOException, InvalidKeySpecException, NoSuchAlgorithmException, WrongSignException, SignatureException, InvalidKeyException {
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
                    if (verify(getSha256(jsonObject.getJSONObject(BODY).toJSONString()), getPublicKey(publicKey), jsonObject.getString(SIGN))) {
                        artwork = jsonObject.getObject(BODY, Artwork.class);
                    } else {
                        throw new WrongSignException(getSha256(jsonObject.getJSONObject(BODY).toJSONString()), publicKey, jsonObject.getString(SIGN));
                    }
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