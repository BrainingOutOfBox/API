package config;

import play.core.ApplicationProvider;
import play.server.api.SSLEngineProvider;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SslEngineProvider implements SSLEngineProvider {
    private ApplicationProvider applicationProvider;
    private final String KEYSTORE_PATH = "/distelli/envs/.keystore/keyStore.jks";
    private ArrayList<String> priorityCipherSuites = new ArrayList<>(Arrays.asList(
            "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA",
            "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA",
            "TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA"));

    public SslEngineProvider(ApplicationProvider applicationProvider) {
        this.applicationProvider = applicationProvider;
    }

    @Override
    public SSLEngine createSSLEngine() {
        SSLContext context = createSSLContext();
        SSLEngine engine = context.createSSLEngine();
        List<String> cipherSuites = Arrays.asList(engine.getEnabledCipherSuites());
        priorityCipherSuites.retainAll(cipherSuites);

        engine.setEnabledCipherSuites(priorityCipherSuites.toArray(new String[0]));
        SSLParameters params = engine.getSSLParameters();
        params.setUseCipherSuitesOrder(true);
        engine.setSSLParameters(params);
        return engine;
    }

    private SSLContext createSSLContext(){
        try{
            KeyManager[] keyManagers = readKeyManagers();
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(keyManagers, new TrustManager[]{},null);
            return context;
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String readConfigPassword(){
        return System.getProperty("play.server.https.keyStore.password");
    }
    private InputStream readKeyStoreInputStream() throws IOException {
        String path = System.getProperty("play.server.https.keyStore.path");
        if(path == null || path.isEmpty()){
            path = KEYSTORE_PATH;
        }
        Path keystorePath = FileSystems.getDefault().getPath(path);
        if(keystorePath!=null){
            return Files.newInputStream(keystorePath);
        }
        throw new IOException("Keystore Path not found: "+ keystorePath.toString());
    }
    private KeyManager[] readKeyManagers() throws IOException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, CertificateException {
        char[] password = readConfigPassword().toCharArray();
        InputStream keystoreStream = readKeyStoreInputStream();
        try{
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(keystoreStream,password);
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keystore,password);
            return kmf.getKeyManagers();
        }finally{
            keystoreStream.close();
        }
    }
}
