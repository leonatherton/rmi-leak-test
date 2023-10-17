package rmi;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "RMI Leak Test Servlet", urlPatterns = "/*", loadOnStartup = 1)
public class RmiLeakTestServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        super.init();

        try {
            final Registry registry = LocateRegistry.createRegistry(0);

            final MyRemoteClass myRemoteClass = new MyRemoteClass();

            final MyRemote stub = (MyRemote) UnicastRemoteObject.exportObject(myRemoteClass, 0);

            registry.bind("MyStub", stub);

//            UnicastRemoteObject.unexportObject(myRemoteClass, true); // Shutdown succeeds with this
            UnicastRemoteObject.unexportObject(registry, true);

        } catch (final RemoteException | AlreadyBoundException e) {
            Logger.getLogger(RmiLeakTestServlet.class.getName()).log(Level.SEVERE, "Exception", e);
        }
    }
}

