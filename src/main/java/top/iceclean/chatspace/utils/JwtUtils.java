package top.iceclean.chatspace.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

/**
 * @author : Ice'Clean
 * @date : 2022-01-29
 *
 * JWT 工具类
 * 提醒：明文设置有一定要求（长度、字符等）
 */
public class JwtUtils {

    /** 有效期为 1 小时 */
    private static final Long JWT_TTL = 60 * 60 * 1000L;
    /** 设置秘钥明文 */
    private static final String JWT_KEY = "ChatSpace";
    /** 签发者 */
    private static final String ISSUER = "IceClean";

    /**
     * JWT码生成
     * 超时时间使用工具类默认属性
     *
     * @param subject token中要存放的数据（json格式）
     * @return JWT码
     */
    public static String createJWT(String subject) {
        String randomId = UUID.randomUUID().toString().replaceAll("-", "");
        JwtBuilder builder = getJwtBuilder(subject, JwtUtils.JWT_TTL, randomId);
        return builder.compact();
    }

    /**
     * JWT码生成
     * 手动设置超时时间
     *
     * @param subject   token中要存放的数据（json格式）
     * @param ttlMillis token超时时间
     * @return JWT码
     */
    public static String createJWT(String subject, Long ttlMillis) {
        String randomId = UUID.randomUUID().toString().replaceAll("-", "");
        return getJwtBuilder(subject, ttlMillis, randomId).compact();
    }

    /**
     * JWT码生成
     * 手动设置超时时间，uuid外部提供
     *
     * @param subject   token中要存放的数据（json格式）
     * @param ttlMillis token超时时间
     * @param id        token中的id
     * @return JWT码
     */
    public static String createJWT(String id, String subject, Long ttlMillis) {
        return getJwtBuilder(subject, ttlMillis, id).compact();
    }

    /**
     * JWT码构造器生成
     *
     * @param subject   token中要存放的数据（json格式）
     * @param ttlMillis token超时时间
     * @param uuid      token中的uuid
     * @return JWT码构造器
     */
    private static JwtBuilder getJwtBuilder(String subject, Long ttlMillis, String uuid) {
        //JWT码构造时间和过期时间设置
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        if (ttlMillis == null) {
            ttlMillis = JwtUtils.JWT_TTL;
        }
        long expMillis = nowMillis + ttlMillis;
        Date expDate = new Date(expMillis);

        //构造JWT码构造器
        return Jwts.builder()
                .setId(uuid)
                .setSubject(subject)
                .setIssuer(ISSUER)
                .setIssuedAt(now)
                .signWith(SignatureAlgorithm.HS256, generalKey())
                .setExpiration(expDate);
    }

    /**
     * 生成加密后的秘钥 secretKey
     *
     * @return 加密后的秘钥 secretKey
     */
    public static SecretKey generalKey() {
        System.out.println();
        byte[] encodedKey = new byte[]{1, 0, 1, 0, 1, 1, 0};
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
    }

    /**
     * 解析JWT码
     *
     * @param jwt JWT码
     * @return 解析后的明文 Claims
     */
    public static Claims parseJWT(String jwt) {
        return Jwts.parser()
                .setSigningKey(generalKey())
                .parseClaimsJws(jwt)
                .getBody();
    }
}