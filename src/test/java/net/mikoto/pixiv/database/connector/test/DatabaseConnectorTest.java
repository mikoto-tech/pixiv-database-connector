package net.mikoto.pixiv.database.connector.test;

import com.alibaba.fastjson2.JSONObject;
import net.mikoto.pixiv.api.model.Artwork;
import net.mikoto.pixiv.database.connector.DatabaseConnector;
import net.mikoto.pixiv.database.connector.SimpleDatabaseConnector;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @author mikoto
 * @date 2022/5/22 14:44
 */
public class DatabaseConnectorTest {
    @Test
    public void insertArtworksTest() throws IOException {
        String artwork1JsonString = "{\"artworkId\":79905101,\"artworkTitle\":\"中国語《王様ゲーム茶会》下着差分\",\"authorId\":50258193,\"authorName\":\"逆流茶会\",\"bookmarkCount\":52945,\"createTime\":\"2020-03-05 09:22:19\",\"description\":\"新的本子已经发布在patreon、fanbox还有爱发电了<br />patreon：<a href=\\\"/jump.php?https%3A%2F%2Fwww.patreon.com%2Fposts%2F41937517\\\" target=\\\"_blank\\\">https://www.patreon.com/posts/41937517</a><br />fanbox：<a href=\\\"https://nlch.fanbox.cc/posts/1430998\\\" target=\\\"_blank\\\">https://nlch.fanbox.cc/posts/1430998</a><br />爱发电（支持支付宝和微信支付）→<a href=\\\"/jump.php?https%3A%2F%2Fafdian.net%2F%40niliuchahui\\\" target=\\\"_blank\\\">https://afdian.net/@niliuchahui</a>\",\"grading\":0,\"hasSeries\":true,\"illustUrlMini\":\"https://i.pximg.net/c/48x48/img-master/img/2020/03/05/10/23/36/79905101_p0_square1200.jpg\",\"illustUrlOriginal\":\"https://i.pximg.net/img-original/img/2020/03/05/10/23/36/79905101_p0.jpg\",\"illustUrlRegular\":\"https://i.pximg.net/img-master/img/2020/03/05/10/23/36/79905101_p0_master1200.jpg\",\"illustUrlSmall\":\"https://i.pximg.net/c/540x540_70/img-master/img/2020/03/05/10/23/36/79905101_p0_master1200.jpg\",\"illustUrlThumb\":\"https://i.pximg.net/c/250x250_80_a2/img-master/img/2020/03/05/10/23/36/79905101_p0_square1200.jpg\",\"likeCount\":36554,\"nextArtworkId\":79865332,\"nextArtworkTitle\":\"王様ゲームの茶会１\",\"pageCount\":20,\"patchTime\":\"2022-05-22T14:46:12.716+08:00\",\"previousArtworkId\":0,\"seriesId\":101955,\"seriesOrder\":1,\"tags\":\"漫画;逆流茶会;百合;露出;すじ;魅惑のふともも;極上の乳;尻神様;オナニー\",\"updateTime\":\"2020-03-05 09:23:36\",\"viewCount\":510667}";
        String artwork2JsonString = "{\"artworkId\":91262365,\"artworkTitle\":\"初音ミク\",\"authorId\":3259336,\"authorName\":\"猫打\",\"bookmarkCount\":13382,\"createTime\":\"2021-07-15 19:02:17\",\"description\":\"4000*6000.black stockings ， White stockings  ：<br /><a href=\\\"/jump.php?http%3A%2F%2Fcbr.sh%2Fw58i1w\\\" target=\\\"_blank\\\">http://cbr.sh/w58i1w</a>\",\"grading\":0,\"hasSeries\":false,\"illustUrlMini\":\"https://i.pximg.net/c/48x48/custom-thumb/img/2021/07/16/00/48/17/91262365_p0_custom1200.jpg\",\"illustUrlOriginal\":\"https://i.pximg.net/img-original/img/2021/07/16/00/48/17/91262365_p0.jpg\",\"illustUrlRegular\":\"https://i.pximg.net/img-master/img/2021/07/16/00/48/17/91262365_p0_master1200.jpg\",\"illustUrlSmall\":\"https://i.pximg.net/c/540x540_70/img-master/img/2021/07/16/00/48/17/91262365_p0_master1200.jpg\",\"illustUrlThumb\":\"https://i.pximg.net/c/250x250_80_a2/custom-thumb/img/2021/07/16/00/48/17/91262365_p0_custom1200.jpg\",\"likeCount\":9529,\"nextArtworkId\":0,\"pageCount\":2,\"patchTime\":\"2022-05-22T14:24:56.87+08:00\",\"previousArtworkId\":0,\"seriesId\":0,\"seriesOrder\":0,\"tags\":\"初音ミク;足裏;足指;女の子;つま先;裸足;ギリシャ型;美脚;縞パン;VOCALOID10000users入り\",\"updateTime\":\"2021-07-15 23:48:17\",\"viewCount\":94266}";

        Artwork artwork1 = JSONObject.parseObject(artwork1JsonString, Artwork.class);
        Artwork artwork2 = JSONObject.parseObject(artwork2JsonString, Artwork.class);

        DatabaseConnector databaseConnector = new SimpleDatabaseConnector();

        databaseConnector.insertArtworks("http://127.0.0.1:2465", "MikotoTestAccessKey", new Artwork[]{artwork1, artwork2});
    }
}
