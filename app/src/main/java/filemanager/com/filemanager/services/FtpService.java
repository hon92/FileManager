package filemanager.com.filemanager.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.apache.ftpserver.ConnectionConfig;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PasswordEncryptor;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;

/**
 * Created by Honza on 08.12.2017.
 */

public class FtpService extends Service {
    private final int DEFAULT_FTP_PORT = 2000;
    private FtpServer ftpServer;
    private int ftpPort = DEFAULT_FTP_PORT;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ftpPort = intent.getIntExtra("ftpPort", DEFAULT_FTP_PORT);
        startFTPServer();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startFTPServer() {
        FtpServerFactory serverFactory = new FtpServerFactory();
        serverFactory.setConnectionConfig(new ConnectionConfig() {
            @Override
            public int getMaxLoginFailures() {
                return 3;
            }

            @Override
            public int getLoginFailureDelay() {
                return 500;
            }

            @Override
            public int getMaxAnonymousLogins() {
                return 1;
            }

            @Override
            public int getMaxLogins() {
                return 1;
            }

            @Override
            public boolean isAnonymousLoginEnabled() {
                return true;
            }

            @Override
            public int getMaxThreads() {
                return 4;
            }
        });
        ListenerFactory factory = new ListenerFactory();
        factory.setPort(ftpPort);
        serverFactory.addListener("default", factory.createListener());
        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();

        userManagerFactory.setPasswordEncryptor(new PasswordEncryptor()
        {

            @Override
            public String encrypt(String password) {
                return password;
            }

            @Override
            public boolean matches(String passwordToCheck, String storedPassword) {
                return passwordToCheck.equals(storedPassword);
            }
        });

        //BaseUser rootUser = createUser("root", "root", "/");
        BaseUser anonymousUser = createUser("anonymous", "", "/");
        UserManager um = userManagerFactory.createUserManager();
        try
        {
            //um.save(rootUser);
            um.save(anonymousUser);
        }
        catch (FtpException e1)
        {
            e1.printStackTrace();
        }

        serverFactory.setUserManager(um);
        ftpServer = serverFactory.createServer();

        try {
            ftpServer.start();
        } catch (FtpException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ftpServer.stop();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public BaseUser createUser(String name, String password, String rootDirectory) {
        BaseUser user = new BaseUser();
        user.setName(name);
        user.setPassword(password);
        user.setHomeDirectory(rootDirectory);
        return user;
    }
}
