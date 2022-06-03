package net.mikoto.pixiv.database.connector;

import net.mikoto.pixiv.api.connector.ArtworkDataSource;
import net.mikoto.pixiv.api.connector.Connector;
import net.mikoto.pixiv.api.model.Artwork;
import net.mikoto.pixiv.database.connector.exception.GetArtworkException;
import net.mikoto.pixiv.database.connector.exception.WrongSignException;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

/**
 * @author mikoto
 * @date 2022/5/21 4:24
 */
public interface DatabaseConnector extends ArtworkDataSource, Connector {
    /**
     * Insert artworks.
     *
     * @param key      The key.
     * @param address  The address of pixiv-database.
     * @param artworks The artworks.
     * @throws IOException An exception.
     */
    void insertArtworks(String key, String address, Artwork[] artworks) throws IOException;


    /**
     * Get artworks.
     *
     * @param credential The credential that the artworks have.
     * @param order      The order.
     * @param properties The properties that need to sort.
     * @param pageCount  The page num.
     * @return The artwork in this page.
     */
    Artwork[] getArtworks(String address, String credential, Sort.Direction order, String properties, int pageCount) throws IOException, GetArtworkException, WrongSignException, InvalidKeySpecException, NoSuchAlgorithmException, SignatureException, InvalidKeyException;

    /**
     * Get the artwork.
     *
     * @param address   The address.
     * @param artworkId The artwork id.
     * @return The artwork.
     */
    Artwork getArtwork(String address, int artworkId) throws GetArtworkException, IOException, InvalidKeySpecException, NoSuchAlgorithmException, WrongSignException, SignatureException, InvalidKeyException;
}
