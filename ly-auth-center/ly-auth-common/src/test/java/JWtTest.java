import org.junit.Test;
import pojo.UserInfo;
import utils.JwtUtils;

import java.security.PrivateKey;
import java.security.PublicKey;

public class JwtTest {
    private static final String pubKeyPath = "d:/test/rsa/rsa.pub";
    private static final String priKeyPath = "d:/test/rsa/rsa.pri";

    private PublicKey publicKey;
    private PrivateKey privateKey;

   /* @Test
    public void test1() throws Exception {
        File file = new File("d:/test/rsa");
        if(!file.exists()){
            file.mkdirs();
        }
        RsaUtils.generateKey(pubKeyPath,priKeyPath,"cola");
    }*/

/*   @Test
   public void test() throws Exception {
       File pubKey = new File(pubKeyPath);
       File priKey = new File(priKeyPath);
       if (!pubKey.exists() || !priKey.exists()) {
           // 生成公钥和私钥
           priKey.mkdirs();
           pubKey.mkdirs();
           RsaUtils.generateKey(pubKeyPath, priKeyPath, "cola");
       }
   }*/
/*   @Before
    public void before() throws Exception {
       publicKey = RsaUtils.getPublicKey(pubKeyPath);
       privateKey = RsaUtils.getPrivateKey(priKeyPath);
       System.out.println("公钥："+publicKey);
       System.out.println("秘钥："+privateKey);
   }*/

    @Test
    public void test1() throws Exception {
        String token = JwtUtils.generateToken(new UserInfo(777l, "cola"), privateKey, 5);
        System.out.println(token);
    }

    @Test
    public void test2() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6Nzc3LCJ1c2VybmFtZSI6ImNvbGEiLCJleHAiOjE1ODk0NTgzNDV9.S1uicX7mXogZSKfKxabZI8VS2R8s_YA0NejM8TXKXDPk0rgkS2gGhep68A6tLZPuCICkqvhAu4s09GFcOIlH7Xu1V36bK3TPS3UOjYqZmDadIEyxibbZ9EoGesy1YzlLYXrm6Q4L6O-b8WtNxx_pLQt6_Iv_jLYyyhqoJbR44Q4";
        UserInfo infoFromToken = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println(infoFromToken.getId());
        System.out.println(infoFromToken.getUsername());
    }
}
