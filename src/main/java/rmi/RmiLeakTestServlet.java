package rmi;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.rmi.AlreadyBoundException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "RMI Leak Test Servlet", urlPatterns = "/*", loadOnStartup = 1)
public class RmiLeakTestServlet extends HttpServlet {

    private static Registry registry;
    private static MyRemoteClass myRemoteClass;

    @Override
    public void init() throws ServletException {
        super.init();

        try {
            Logger.getLogger(RmiLeakTestServlet.class.getName()).log(Level.INFO, "Init leak test");

            registry = LocateRegistry.createRegistry(0);

            myRemoteClass = new MyRemoteClass();

            final MyRemote stub = (MyRemote) UnicastRemoteObject.exportObject(myRemoteClass, 0);

            registry.bind("MyStub", stub);

        } catch (final RemoteException | AlreadyBoundException e) {
            Logger.getLogger(RmiLeakTestServlet.class.getName()).log(Level.SEVERE, "Init Exception", e);
        }
    }

    @Override
    public void destroy() {
        super.destroy();

        try {
            Logger.getLogger(RmiLeakTestServlet.class.getName()).log(Level.INFO, "Destroy leak test");

//            UnicastRemoteObject.unexportObject(myRemoteClass, true); // Shutdown succeeds with this
            UnicastRemoteObject.unexportObject(registry, true);
        } catch (final NoSuchObjectException e) {
            Logger.getLogger(RmiLeakTestServlet.class.getName()).log(Level.SEVERE, "Destroy Exception", e);
        }
    }
}

