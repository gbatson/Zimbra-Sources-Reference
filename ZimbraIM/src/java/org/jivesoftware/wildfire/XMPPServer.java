/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package org.jivesoftware.wildfire;

import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.util.IMConfig;
import org.jivesoftware.util.LocaleUtils;
import org.jivesoftware.util.Log;
import org.jivesoftware.util.PropertyProvider;
import org.jivesoftware.util.Version;
import org.jivesoftware.wildfire.LocationManager.ComponentIdentifier;
import org.jivesoftware.wildfire.audit.AuditManager;
import org.jivesoftware.wildfire.audit.spi.AuditManagerImpl;
import org.jivesoftware.wildfire.commands.AdHocCommandHandler;
import org.jivesoftware.wildfire.component.InternalComponentManager;
import org.jivesoftware.wildfire.container.BasicModule;
import org.jivesoftware.wildfire.container.Module;
import org.jivesoftware.wildfire.container.PluginManager;
import org.jivesoftware.wildfire.disco.DiscoInfoProvider;
import org.jivesoftware.wildfire.disco.DiscoItemsProvider;
import org.jivesoftware.wildfire.disco.DiscoServerItem;
import org.jivesoftware.wildfire.disco.IQDiscoInfoHandler;
import org.jivesoftware.wildfire.disco.IQDiscoItemsHandler;
import org.jivesoftware.wildfire.disco.ServerFeaturesProvider;
import org.jivesoftware.wildfire.disco.ServerItemsProvider;
import org.jivesoftware.wildfire.filetransfer.proxy.FileTransferProxy;
import org.jivesoftware.wildfire.filetransfer.FileTransferManager;
import org.jivesoftware.wildfire.filetransfer.DefaultFileTransferManager;
import org.jivesoftware.wildfire.handler.*;
import org.jivesoftware.wildfire.muc.MultiUserChatServer;
import org.jivesoftware.wildfire.muc.spi.MultiUserChatServerImpl;
import org.jivesoftware.wildfire.net.SSLConfig;
import org.jivesoftware.wildfire.net.ServerTrafficCounter;
import org.jivesoftware.wildfire.roster.RosterManager;
import org.jivesoftware.wildfire.spi.*;
import org.jivesoftware.wildfire.transport.TransportHandler;
import org.jivesoftware.wildfire.user.UserManager;
import org.jivesoftware.wildfire.user.UserNotFoundException;
import org.jivesoftware.wildfire.vcard.VCardManager;
import org.xmpp.packet.JID;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.net.ssl.SSLServerSocketFactory;

/**
 * The main XMPP server that will load, initialize and start all the server's
 * modules. The server is unique in the JVM and could be obtained by using the
 * {@link #getInstance()} method.<p>
 * <p/>
 * The loaded modules will be initialized and may access through the server other
 * modules. This means that the only way for a module to locate another module is
 * through the server. The server maintains a list of loaded modules.<p>
 * <p/>
 * After starting up all the modules the server will load any available plugin.
 * For more information see: {@link org.jivesoftware.wildfire.container.PluginManager}.<p>
 * <p/>
 * A configuration file keeps the server configuration. This information is required for the
 * server to work correctly. The server assumes that the configuration file is named
 * <b>wildfire.xml</b> and is located in the <b>conf</b> folder. The folder that keeps
 * the configuration file must be located under the home folder. The server will try different
 * methods to locate the home folder.
 * <p/>
 * <ol>
 * <li><b>system property</b> - The server will use the value defined in the <i>wildfireHome</i>
 * system property.</li>
 * <li><b>working folder</b> -  The server will check if there is a <i>conf</i> folder in the
 * working directory. This is the case when running in standalone mode.</li>
 * <li><b>wildfire_init.xml file</b> - Attempt to load the value from wildfire_init.xml which
 * must be in the classpath</li>
 * </ol>
 *
 * @author Gaston Dombiak
 */
public class XMPPServer {

    private static XMPPServer instance;

    private Version version;
    private Date startDate;
    private Date stopDate;
    private boolean initialized = false;
    private List<String> mServerNames;
    
    private static class ModuleData {
        public Module module;
        public ComponentIdentifier identifier;
    }

    /**
     * All singleton modules loaded by this server
     */
    private Map<Class, Module> singletonModules = new HashMap<Class, Module>();
    
    /**
     * New-style non-singleton modules
     */
    private Map<String, ModuleData> modules = new HashMap<String, ModuleData>();

    /**
     * Listeners that will be notified when the server has started or is about to be stopped.
     */
    private List<XMPPServerListener> listeners = new CopyOnWriteArrayList<XMPPServerListener>();

    /**
     * Location of the home directory. All configuration files should be
     * located here.
     */
    private File wildfireHome;
    private ClassLoader loader;

    private PluginManager pluginManager;
    private InternalComponentManager componentManager;
    private LocationManager mLocationManager = null;

    /**
     * True if in setup mode
     */
    private boolean setupMode = true;

    private RoutingTable mRoutingTable;

    /**
     * Returns a singleton instance of XMPPServer.
     *
     * @return an instance.
     */
    public static XMPPServer getInstance() {
        return instance;
    }
    
    /**
     * TEMPORARY API.  
     * 
     * @return
     */
    public InternalComponentManager getInternalComponentManager() {
        return componentManager;
    }
    
    /**
     * Creates a server and starts it.
     */
    public XMPPServer(String[] sslExcludedCiphers, 
                      LocationManager locMgr, List<String> domainNames /*, PropertyProvider localConfig, PropertyProvider globalProperties*/) {
        // We may only have one instance of the server running on the JVM
        if (instance != null) {
            throw new IllegalStateException("A server is already running");
        }
        mLocationManager = locMgr;
        
        instance = this;
        
        SSLConfig.init(sslExcludedCiphers);
        
        start(domainNames);
    }

    /**
     * Returns a snapshot of the server's status.
     *
     * @return the server information current at the time of the method call.
     */
    public XMPPServerInfo getServerInfo() {
        if (!initialized) {
            throw new IllegalStateException("Not initialized yet");
        }
        return new XMPPServerInfoImpl(mServerNames, version, startDate, stopDate, getConnectionManager());
    }
    
    public boolean isShuttingDown() {
        return false;
    }
    
    /**
     * @return a list of the domains that are local to this cloud (ie don't use XMPP S2S to connect)
     */
    public Collection<String> getLocalDomains() {
        return mServerNames;
    }
    
    /**
     * @param domain
     * @return TRUE if the specified domain is local to this cloud (ie do't use XMPP S2S to connect)
     */
    public boolean isLocalDomain(String domain) {
        return mServerNames.contains(domain);
    }

    /**
     * Returns true if the given address is local to the server (managed by this
     * server domain). Return false even if the jid's domain matches a local component's
     * service JID.
     *
     * @param jid the JID to check.
     * @return true if the address is a local address to this server.
     */
    public boolean isLocal(JID jid) {
            if (jid != null && isLocalDomain(jid.getDomain())) {
                try {
                    return mLocationManager.isLocal(jid);
                } catch (UserNotFoundException e) {
                    return false;
                }
            }
            return false;
    }

    /**
     * Returns true if the given address does not match the local server hostname and does not
     * match a component service JID.
     *
     * @param jid the JID to check.
     * @return true if the given address does not match the local server hostname and does not
     *         match a component service JID.
     */
    public boolean isRemote(JID jid) {
        if (jid != null) {
            if (!isLocal(jid) && !componentManager.isCloudComponent(jid)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the given address matches a component service JID.
     *
     * @param jid the JID to check.
     * @return true if the given address matches a component service JID.
     */
    public boolean matchesComponent(JID jid) {
        if (jid != null) {
            return !isLocal(jid) && 
            ( mLocationManager.isCloudComponent(jid.toBareJID()) || 
                            this.componentManager.getComponent(jid)!=null); 
                            
        }
        return false;
    }
    
    /**
     * Return TRUE if the specified domain is in this cloud
     * 
     * @param domain
     * @return
     */
    public boolean isCloudComponent(String domain) {
        return mLocationManager.isCloudComponent(domain);
    }
    
    public String getServerForComponent(String domain) {
        return mLocationManager.getServerForComponent(domain);
    }
    
    /**
     * Return a list of domains for componentType that are running ON THIS SERVER  
     * 
     * @param componentType type of component, e.g. "muc" or "filetransfer"
     * @return
     */
    public List<LocationManager.ComponentIdentifier> getThisServerComponents(String componentType) throws ServiceException {
        return mLocationManager.getThisServerComponents(componentType);
    }
    
    public boolean isAdmin(String jid) {
        if (jid != null && jid.length() > 0) {
            for (JID admin : getAdmins()) {
                if (jid.equals(admin.toBareJID()))
                    return true;
            }
        }
        return false;
    }
    
    /**
     * Returns a collection with the JIDs of the server's admins. The collection may include
     * JIDs of local users and users of remote servers.
     *
     * @return a collection with the JIDs of the server's admins.
     */
    public Collection<JID> getAdmins() {
        Collection<JID> admins = new ArrayList<JID>();
        // Add the JIDs of the local users that are admins
        String usernames = IMConfig.ADMIN_USERNAMES.getString();
        usernames = (usernames == null || usernames.trim().length() == 0) ? "admin" : usernames;
        StringTokenizer tokenizer = new StringTokenizer(usernames, ",");
        while (tokenizer.hasMoreTokens()) {
            String username = tokenizer.nextToken();
            try {
                admins.add(new JID(username.toLowerCase().trim()));
            }
            catch (IllegalArgumentException e) {
                // Ignore usernames that when appended @server.com result in an invalid JID
                Log.warn("Invalid username found in authorizedUsernames at wildfire.xml: " +
                         username, e);
            }
        }

        return admins;
    }

    /**
     * Adds a new server listener that will be notified when the server has been started
     * or is about to be stopped.
     *
     * @param listener the new server listener to add.
     */
    public void addServerListener(XMPPServerListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a server listener that was being notified when the server was being started
     * or was about to be stopped.
     *
     * @param listener the server listener to remove.
     */
    public void removeServerListener(XMPPServerListener listener) {
        listeners.remove(listener);
    }
    
    private void initialize(List<String> domainNames) throws FileNotFoundException {
        mServerNames = new CopyOnWriteArrayList<String>(domainNames);
        
        version = new Version(3, 1, 0, Version.ReleaseStatus.Release, 0);
        setupMode = false;

//        if (isStandAlone()) {
//            Runtime.getRuntime().addShutdownHook(new ShutdownHookThread());
//        }

        loader = Thread.currentThread().getContextClassLoader();
        componentManager = InternalComponentManager.getInstance();

        initialized = true;
    }
    
    private List<ServerItemsProvider> mServerItemsProviders = new ArrayList<ServerItemsProvider>();
    
    private class MyServerItemsProvider implements ServerItemsProvider {
        MyServerItemsProvider(ComponentIdentifier comp) {
            c = comp;
        }
        ComponentIdentifier c;
        public Iterator<DiscoServerItem> getItems() {
            ArrayList<DiscoServerItem> items = new ArrayList<DiscoServerItem>();
            items.add(new DiscoServerItem() {
                public String getJID() {
                    return c.serviceDomain;
                }

                public String getName() {
                    return c.serviceName;
                }

                public String getAction() {
                    return null;
                }

                public String getNode() {
                    return ""; // FIXME
                }

                public DiscoInfoProvider getDiscoInfoProvider() {
                    return null;
                }

                public DiscoItemsProvider getDiscoItemsProvider() {
                    return null;
                }
            });
            return items.iterator();
        }
    }
    
    private void updateRemoteComponentDiscoveryItems() {
        for (ServerItemsProvider sip : mServerItemsProviders) {
            getIQDiscoItemsHandler().removeServerItemsProvider(sip);
        }
        
        try {
            for (ComponentIdentifier c : mLocationManager.getRemoteServerComponents()) {
//            for (ComponentIdentifier c : mLocationManager.getAllServerComponents()) {
                ServerItemsProvider sip = new MyServerItemsProvider(c);
                mServerItemsProviders.add(sip);
            }
        } catch (ServiceException e) {
            ZimbraLog.im.warn("Caught ServiceExcepton trying to setup remote Disco handlers", e);
        }
        
        for (ServerItemsProvider sip : mServerItemsProviders) {
            getIQDiscoItemsHandler().addServerItemsProvider(sip);
        }
    }
    

    public void start(List<String> domainNames) {
        try {
            initialize(domainNames);

            // If the server has already been setup then we can start all the server's modules
            if (!setupMode) {
                verifyDataSource();
                // First load all the modules so that modules may access other modules while
                // being initialized
                loadModules();
                // Initize all the modules
                initModules();
                // Start all the modules
                startModules();
                // Initialize component manager (initialize before plugins get loaded)
                InternalComponentManager.getInstance().start();
                
                // Setup Discovery handler for remote components
                updateRemoteComponentDiscoveryItems();
                
//                Interop.getInstance().start(this, componentManager);
                
            }
            // Initialize statistics
            ServerTrafficCounter.initStatistics();

            // Load plugins (when in setup mode only the admin console will be loaded)
            File pluginDir = new File(wildfireHome, "im"+File.separator+"plugins");
            pluginManager = new PluginManager(pluginDir);
            pluginManager.start();

            // Log that the server has been started
            List<String> params = new ArrayList<String>();
            params.add(version.getVersionString());
            params.add(IMConfig.formatDateTime(new Date()));
            String startupBanner = LocaleUtils.getLocalizedString("startup.name", params);
            Log.info(startupBanner);

            startDate = new Date();
            stopDate = null;
            // Notify server listeners that the server has been started
            for (XMPPServerListener listener : listeners) {
                listener.serverStarted();
            }
        }
        catch (Exception e) {
            ZimbraLog.im.error("Exception during IM server startup.  Aborting", e);
            shutdownServer();
        }
    }

    private void loadModules() {
        // Load boot modules
        mRoutingTable = (RoutingTable)loadSingletonModule(IMConfig.ROUTING_TABLE_CLASSNAME.getString());
        loadSingletonModule(AuditManagerImpl.class.getName());
        loadSingletonModule(RosterManager.class.getName());
        loadSingletonModule(PrivateStorage.class.getName());
        // Load core modules
        loadSingletonModule(PresenceManagerImpl.class.getName());
        loadSingletonModule(SessionManager.class.getName());
        loadSingletonModule(PacketRouterImpl.class.getName());
        loadSingletonModule(IQRouter.class.getName());
        loadSingletonModule(MessageRouter.class.getName());
        loadSingletonModule(PresenceRouter.class.getName());
        loadSingletonModule(MulticastRouter.class.getName());
//        loadModule(PacketTransporterImpl.class.getName());
        loadSingletonModule(PacketDelivererImpl.class.getName());
        loadSingletonModule(TransportHandler.class.getName());
        loadSingletonModule(OfflineMessageStrategy.class.getName());
        loadSingletonModule(OfflineMessageStore.class.getName());
        loadSingletonModule(VCardManager.class.getName());
        // Load standard modules
        loadSingletonModule(IQBindHandler.class.getName());
        loadSingletonModule(IQSessionEstablishmentHandler.class.getName());
        loadSingletonModule(IQAuthHandler.class.getName());
        loadSingletonModule(IQPrivateHandler.class.getName());
        loadSingletonModule(IQRegisterHandler.class.getName());
        loadSingletonModule(IQRosterHandler.class.getName());
        loadSingletonModule(IQTimeHandler.class.getName());
        loadSingletonModule(IQvCardHandler.class.getName());
        loadSingletonModule(IQVersionHandler.class.getName());
        loadSingletonModule(IQLastActivityHandler.class.getName());
        loadSingletonModule(PresenceSubscribeHandler.class.getName());
        loadSingletonModule(PresenceUpdateHandler.class.getName());
        loadSingletonModule(IQDiscoInfoHandler.class.getName());
        loadSingletonModule(IQDiscoItemsHandler.class.getName());
        loadSingletonModule(IQOfflineMessagesHandler.class.getName());
        loadSingletonModule(IQSharedGroupHandler.class.getName());
        loadSingletonModule(AdHocCommandHandler.class.getName());
        loadSingletonModule(IQPrivacyHandler.class.getName());
        loadSingletonModule(DefaultFileTransferManager.class.getName());
        loadSingletonModule(FileTransferProxy.class.getName());
        
        // Load this module always last since we don't want to start listening for clients
        // before the rest of the modules have been started
        loadSingletonModule(ConnectionManagerImpl.class.getName());
        
        List<ComponentIdentifier> componentIds = null;         
        try {
            componentIds = XMPPServer.getInstance().getThisServerComponents(null);
            for (ComponentIdentifier id : componentIds) {
                loadModule(id);
            }
        } catch(ServiceException ex) { // FIXME real error handling!
            ZimbraLog.im.warn("Caught service exception getting local component list", ex);
        }
    }

    /**
     * Loads a module.
     *
     * @param module the name of the class that implements the Module interface.
     */
    private Module loadModule(ComponentIdentifier modId) {
        if (modId.className == null) {
            ZimbraLog.im.error("Unable to load XMPP component: "+modId);
            return null; // ignore
        }
        
        try {
            Class modClass = loader.loadClass(modId.className);
            Module mod = (Module) modClass.newInstance();

            if (mod instanceof BasicModule) {
                BasicModule bm = (BasicModule)mod;
                bm.setStartupParameters(modId, new HashMap<String, String>());
            }
            ModuleData md = new ModuleData();
            md.module = mod;
            md.identifier = modId;
            this.modules.put(modId.serviceDomain, md);
            return mod;
        }
        catch (Exception e) {
//            e.printStackTrace();
//            Log.error(LocaleUtils.getLocalizedString("admin.error"), e);
            ZimbraLog.im.error("Unable to load XMPP component: "+modId, e);
            return null;
        }
    }
    
    /**
     * Loads a module.
     *
     * @param module the name of the class that implements the Module interface.
     */
    private Module loadSingletonModule(String module) {
        try {
            Class modClass = loader.loadClass(module);
            Module mod = (Module) modClass.newInstance();
            this.singletonModules.put(modClass, mod);
            return mod;
        }
        catch (Exception e) {
            ZimbraLog.im.error(LocaleUtils.getLocalizedString("admin.error"), e);
            return null;
        }
    }
    
    private void initModules() {
        for (Iterator<Module> iter = singletonModules.values().iterator(); iter.hasNext();) {
            Module module = iter.next();
            if (!doModuleInit(module))
                iter.remove();
        }
        for (Iterator<ModuleData> iter = modules.values().iterator(); iter.hasNext();) {
            ModuleData md = iter.next();
            if (!doModuleInit(md.module)) 
                iter.remove();
        }
    }
    
    private boolean doModuleInit(Module module) {
        boolean isInitialized = false;
        try {
            module.initialize(this);
            isInitialized = true;
        }
        catch (Exception e) {
            ZimbraLog.im.error("IM Error in doModuleInit", e);
            if (isInitialized) {
                module.stop();
                module.destroy();
            }
            Log.error(LocaleUtils.getLocalizedString("admin.error"), e);
            return false;
        }
        return true;
    }

    /**
     * <p>Following the loading and initialization of all the modules
     * this method is called to iterate through the known modules and
     * start them.</p>
     */
    private void startModules() {
        for (Module module : singletonModules.values()) {
            doStartModule(module);
        }
        for (ModuleData md : modules.values()) {
            doStartModule(md.module);
        }
    }
    
    private void doStartModule(Module module) {
        boolean started = false;
        try {
            module.start();
        }
        catch (Exception e) {
            if (started && module != null) {
                module.stop();
                module.destroy();
            }
            Log.error(LocaleUtils.getLocalizedString("admin.error"), e);
        }
    }

    /**
     * Restarts the server and all it's modules only if the server is restartable. Otherwise do
     * nothing.
     */
    public void restart() {
//        if (isStandAlone() && isRestartable()) {
//            try {
//                Class wrapperClass = Class.forName(WRAPPER_CLASSNAME);
//                Method restartMethod = wrapperClass.getMethod("restart", (Class []) null);
//                restartMethod.invoke(null, (Object []) null);
//            }
//            catch (Exception e) {
//                Log.error("Could not restart container", e);
//            }
//        }
    }

    /**
     * Stops the server only if running in standalone mode. Do nothing if the server is running
     * inside of another server.
     */
    public void stop() {
        // Only do a system exit if we're running standalone
//        if (isStandAlone()) {
//            // if we're in a wrapper, we have to tell the wrapper to shut us down
//            if (isRestartable()) {
//                try {
//                    Class wrapperClass = Class.forName(WRAPPER_CLASSNAME);
//                    Method stopMethod = wrapperClass.getMethod("stop", Integer.TYPE);
//                    stopMethod.invoke(null, 0);
//                }
//                catch (Exception e) {
//                    Log.error("Could not stop container", e);
//                }
//            }
//            else {
//                shutdownServer();
//                stopDate = new Date();
//                Thread shutdownThread = new ShutdownThread();
//                shutdownThread.setDaemon(true);
//                shutdownThread.start();
//            }
//        }
//        else {
            // Close listening socket no matter what the condition is in order to be able
            // to be restartable inside a container.
            shutdownServer();
            stopDate = new Date();
//        }
    }

    public boolean isSetupMode() {
        return setupMode;
    }

    public boolean isRestartable() {
//        boolean restartable;
//        try {
//            restartable = Class.forName(WRAPPER_CLASSNAME) != null;
//        }
//        catch (ClassNotFoundException e) {
//            restartable = false;
//        }
//        return restartable;
        return false;
    }

    /**
     * Returns if the server is running in standalone mode. We consider that it's running in
     * standalone if the "org.jivesoftware.wildfire.starter.ServerStarter" class is present in the
     * system.
     *
     * @return true if the server is running in standalone mode.
     */
    public boolean isStandAlone() {
//        if (false) {
//            boolean standalone;
//            try {
//                standalone = Class.forName(STARTER_CLASSNAME) != null;
//            }
//            catch (ClassNotFoundException e) {
//                standalone = false;
//            }
//            return standalone;
//        } else {
//            return false;
//        }
        return false;
    }

    /**
     * Verify that the database is accessible.
     */
    private void verifyDataSource() {
        java.sql.Connection conn = null;
        try {
            conn = DbConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT count(*) FROM jiveID");
            ResultSet rs = stmt.executeQuery();
            rs.next();
            rs.close();
            stmt.close();
        }
        catch (Exception e) {
            ZimbraLog.im.error("Database could not be accessed", e);
            throw new IllegalArgumentException(e);
        }
        finally {
            if (conn != null) {
                try {
                    conn.close();
                }
                catch (SQLException e) {
                    Log.error(e);
                }
            }
        }
    }

//    /**
//     * Verifies that the given home guess is a real Wildfire home directory.
//     * We do the verification by checking for the Wildfire config file in
//     * the config dir of jiveHome.
//     *
//     * @param homeGuess a guess at the path to the home directory.
//     * @param jiveConfigName the name of the config file to check.
//     * @return a file pointing to the home directory or null if the
//     *         home directory guess was wrong.
//     * @throws java.io.FileNotFoundException if there was a problem with the home
//     *                                       directory provided
//     */
//    private File verifyHome(String homeGuess, String jiveConfigName) throws FileNotFoundException {
//        File wildfireHome = new File(homeGuess);
//        File configFile = new File(wildfireHome, jiveConfigName);
//        if (!configFile.exists()) {
//            throw new FileNotFoundException();
//        }
//        else {
//            try {
//                return new File(wildfireHome.getCanonicalPath());
//            }
//            catch (Exception ex) {
//                throw new FileNotFoundException();
//            }
//        }
//    }

//    /**
//     * <p>A thread to ensure the server shuts down no matter what.</p>
//     * <p>Spawned when stop() is called in standalone mode, we wait a few
//     * seconds then call system exit().</p>
//     *
//     * @author Iain Shigeoka
//     */
//    private class ShutdownHookThread extends Thread {
//
//        /**
//         * <p>Logs the server shutdown.</p>
//         */
//        public void run() {
//            shutdownServer();
//            Log.info("Server halted");
//            System.err.println("Server halted");
//        }
//    }

//    /**
//     * <p>A thread to ensure the server shuts down no matter what.</p>
//     * <p>Spawned when stop() is called in standalone mode, we wait a few
//     * seconds then call system exit().</p>
//     *
//     * @author Iain Shigeoka
//     */
//    private class ShutdownThread extends Thread {
//
//        /**
//         * <p>Shuts down the JVM after a 5 second delay.</p>
//         */
//        public void run() {
//            try {
//                Thread.sleep(5000);
//              // No matter what, we make sure it's dead
//                System.exit(0);
//            }
//            catch (InterruptedException e) {
//              // Ignore.
//            }
//
//        }
//    }

    /**
     * Makes a best effort attempt to shutdown the server
     */
    private void shutdownServer() {
        // Notify server listeners that the server is about to be stopped
        for (XMPPServerListener listener : listeners) {
            listener.serverStopping();
        }
        
//        Interop.getInstance().stop();
        
        // If we don't have modules then the server has already been shutdown
        if (singletonModules.isEmpty()) {
            return;
        }
        
        for (ModuleData md : modules.values()) {
            md.module.stop();
            md.module.destroy();
        }
        modules.clear();
        
        // Get all modules and stop and destroy them
        for (Module module : singletonModules.values()) {
            module.stop();
            module.destroy();
        }
        singletonModules.clear();
        // Stop all plugins
        if (pluginManager != null) {
            pluginManager.shutdown();
        }
        // Stop the Db connection manager.
        DbConnectionManager.destroyConnectionProvider();
        // hack to allow safe stopping
        Log.info("Wildfire stopped");
    }

    /**
     * Returns the <code>ConnectionManager</code> registered with this server. The
     * <code>ConnectionManager</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>ConnectionManager</code> registered with this server.
     */
    public ConnectionManager getConnectionManager() {
        return (ConnectionManager) singletonModules.get(ConnectionManagerImpl.class);
    }

    /**
     * Returns the <code>RoutingTable</code> registered with this server. The
     * <code>RoutingTable</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>RoutingTable</code> registered with this server.
     */
    public RoutingTable getRoutingTable() {
        return mRoutingTable;
    }

    /**
     * Returns the <code>PacketDeliverer</code> registered with this server. The
     * <code>PacketDeliverer</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>PacketDeliverer</code> registered with this server.
     */
    public PacketDeliverer getPacketDeliverer() {
        return (PacketDeliverer) singletonModules.get(PacketDelivererImpl.class);
    }

    /**
     * Returns the <code>RosterManager</code> registered with this server. The
     * <code>RosterManager</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>RosterManager</code> registered with this server.
     */
    public RosterManager getRosterManager() {
        return (RosterManager) singletonModules.get(RosterManager.class);
    }

    /**
     * Returns the <code>PresenceManager</code> registered with this server. The
     * <code>PresenceManager</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>PresenceManager</code> registered with this server.
     */
    public PresenceManager getPresenceManager() {
        return (PresenceManager) singletonModules.get(PresenceManagerImpl.class);
    }

    /**
     * Returns the <code>OfflineMessageStore</code> registered with this server. The
     * <code>OfflineMessageStore</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>OfflineMessageStore</code> registered with this server.
     */
    public OfflineMessageStore getOfflineMessageStore() {
        return (OfflineMessageStore) singletonModules.get(OfflineMessageStore.class);
    }

    /**
     * Returns the <code>OfflineMessageStrategy</code> registered with this server. The
     * <code>OfflineMessageStrategy</code> was registered with the server as a module while starting
     * up the server.
     *
     * @return the <code>OfflineMessageStrategy</code> registered with this server.
     */
    public OfflineMessageStrategy getOfflineMessageStrategy() {
        return (OfflineMessageStrategy) singletonModules.get(OfflineMessageStrategy.class);
    }

    /**
     * Returns the <code>PacketRouter</code> registered with this server. The
     * <code>PacketRouter</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>PacketRouter</code> registered with this server.
     */
    public PacketRouter getPacketRouter() {
        return (PacketRouter) singletonModules.get(PacketRouterImpl.class);
    }

    /**
     * Returns the <code>IQRegisterHandler</code> registered with this server. The
     * <code>IQRegisterHandler</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>IQRegisterHandler</code> registered with this server.
     */
    public IQRegisterHandler getIQRegisterHandler() {
        return (IQRegisterHandler) singletonModules.get(IQRegisterHandler.class);
    }

    /**
     * Returns the <code>IQAuthHandler</code> registered with this server. The
     * <code>IQAuthHandler</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>IQAuthHandler</code> registered with this server.
     */
    public IQAuthHandler getIQAuthHandler() {
        return (IQAuthHandler) singletonModules.get(IQAuthHandler.class);
    }

    /**
     * Returns the <code>PluginManager</code> instance registered with this server.
     *
     * @return the PluginManager instance.
     */
    public PluginManager getPluginManager() {
        return pluginManager;
    }

    /**
     * Returns a list with all the modules registered with the server that inherit from IQHandler.
     *
     * @return a list with all the modules registered with the server that inherit from IQHandler.
     */
    public List<IQHandler> getIQHandlers() {
        List<IQHandler> answer = new ArrayList<IQHandler>();
        for (Module module : singletonModules.values()) {
            if (module instanceof IQHandler) {
                answer.add((IQHandler) module);
            }
        }
        return answer;
    }

    /**
     * Returns the <code>SessionManager</code> registered with this server. The
     * <code>SessionManager</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>SessionManager</code> registered with this server.
     */
    public SessionManager getSessionManager() {
        return (SessionManager) singletonModules.get(SessionManager.class);
    }

    /**
     * Returns the <code>TransportHandler</code> registered with this server. The
     * <code>TransportHandler</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>TransportHandler</code> registered with this server.
     */
    public TransportHandler getTransportHandler() {
        return (TransportHandler) singletonModules.get(TransportHandler.class);
    }

    /**
     * Returns the <code>PresenceUpdateHandler</code> registered with this server. The
     * <code>PresenceUpdateHandler</code> was registered with the server as a module while starting
     * up the server.
     *
     * @return the <code>PresenceUpdateHandler</code> registered with this server.
     */
    public PresenceUpdateHandler getPresenceUpdateHandler() {
        return (PresenceUpdateHandler) singletonModules.get(PresenceUpdateHandler.class);
    }

    /**
     * Returns the <code>PresenceSubscribeHandler</code> registered with this server. The
     * <code>PresenceSubscribeHandler</code> was registered with the server as a module while
     * starting up the server.
     *
     * @return the <code>PresenceSubscribeHandler</code> registered with this server.
     */
    public PresenceSubscribeHandler getPresenceSubscribeHandler() {
        return (PresenceSubscribeHandler) singletonModules.get(PresenceSubscribeHandler.class);
    }

    /**
     * Returns the <code>IQRouter</code> registered with this server. The
     * <code>IQRouter</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>IQRouter</code> registered with this server.
     */
    public IQRouter getIQRouter() {
        return (IQRouter) singletonModules.get(IQRouter.class);
    }

    /**
     * Returns the <code>MessageRouter</code> registered with this server. The
     * <code>MessageRouter</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>MessageRouter</code> registered with this server.
     */
    public MessageRouter getMessageRouter() {
        return (MessageRouter) singletonModules.get(MessageRouter.class);
    }

    /**
     * Returns the <code>PresenceRouter</code> registered with this server. The
     * <code>PresenceRouter</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>PresenceRouter</code> registered with this server.
     */
    public PresenceRouter getPresenceRouter() {
        return (PresenceRouter) singletonModules.get(PresenceRouter.class);
    }

    /**
     * Returns the <code>MulticastRouter</code> registered with this server. The
     * <code>MulticastRouter</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>MulticastRouter</code> registered with this server.
     */
    public MulticastRouter getMulticastRouter() {
        return (MulticastRouter) singletonModules.get(MulticastRouter.class);
    }

    /**
     * Returns the <code>UserManager</code> registered with this server. The
     * <code>UserManager</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>UserManager</code> registered with this server.
     */
    public UserManager getUserManager() {
        return UserManager.getInstance();
    }

//    /**
//     * Returns the <code>UpdateManager</code> registered with this server. The
//     * <code>UpdateManager</code> was registered with the server as a module while starting up
//     * the server.
//     *
//     * @return the <code>UpdateManager</code> registered with this server.
//     */
//    public UpdateManager getUpdateManager() {
//        return (UpdateManager) modules.get(UpdateManager.class);
//    }

    /**
     * Returns the <code>AuditManager</code> registered with this server. The
     * <code>AuditManager</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>AuditManager</code> registered with this server.
     */
    public AuditManager getAuditManager() {
        return (AuditManager) singletonModules.get(AuditManagerImpl.class);
    }

    /**
     * Returns a list with all the modules that provide "discoverable" features.
     *
     * @return a list with all the modules that provide "discoverable" features.
     */
    public List<ServerFeaturesProvider> getServerFeaturesProviders() {
        List<ServerFeaturesProvider> answer = new ArrayList<ServerFeaturesProvider>();
        for (Module module : singletonModules.values()) {
            if (module instanceof ServerFeaturesProvider) {
                answer.add((ServerFeaturesProvider) module);
            }
        }
        return answer;
    }

    /**
     * Returns a list with all the modules that provide "discoverable" items associated with
     * the server.
     *
     * @return a list with all the modules that provide "discoverable" items associated with
     *         the server.
     */
    public List<ServerItemsProvider> getServerItemsProviders() {
        List<ServerItemsProvider> answer = new ArrayList<ServerItemsProvider>();
        for (Module module : singletonModules.values()) {
            if (module instanceof ServerItemsProvider) {
                answer.add((ServerItemsProvider) module);
            }
        }
        return answer;
    }

    /**
     * Returns the <code>IQDiscoInfoHandler</code> registered with this server. The
     * <code>IQDiscoInfoHandler</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>IQDiscoInfoHandler</code> registered with this server.
     */
    public IQDiscoInfoHandler getIQDiscoInfoHandler() {
        return (IQDiscoInfoHandler) singletonModules.get(IQDiscoInfoHandler.class);
    }

    /**
     * Returns the <code>IQDiscoItemsHandler</code> registered with this server. The
     * <code>IQDiscoItemsHandler</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>IQDiscoItemsHandler</code> registered with this server.
     */
    public IQDiscoItemsHandler getIQDiscoItemsHandler() {
        return (IQDiscoItemsHandler) singletonModules.get(IQDiscoItemsHandler.class);
    }

    /**
     * Returns the <code>PrivateStorage</code> registered with this server. The
     * <code>PrivateStorage</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>PrivateStorage</code> registered with this server.
     */
    public PrivateStorage getPrivateStorage() {
        return (PrivateStorage) singletonModules.get(PrivateStorage.class);
    }

    /**
     * Returns the <code>AdHocCommandHandler</code> registered with this server. The
     * <code>AdHocCommandHandler</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>AdHocCommandHandler</code> registered with this server.
     */
    public AdHocCommandHandler getAdHocCommandHandler() {
        return (AdHocCommandHandler) singletonModules.get(AdHocCommandHandler.class);
    }

    /**
     * Returns the <code>FileTransferProxy</code> registered with this server. The
     * <code>FileTransferProxy</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>FileTransferProxy</code> registered with this server.
     */
    public FileTransferProxy getFileTransferProxy() {
        return (FileTransferProxy) singletonModules.get(FileTransferProxy.class);
    }

    /**
     * Returns the <code>FileTransferManager</code> registered with this server. The
     * <code>FileTransferManager</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>FileTransferProxy</code> registered with this server.
     */
    public FileTransferManager getFileTransferManager() {
        return (FileTransferManager) singletonModules.get(DefaultFileTransferManager.class);
    }
}
