/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012 VMware, Inc.
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

package com.zimbra.cs.account;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.Collections;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import net.spy.memcached.HashAlgorithm;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.methods.PostMethod;

import com.zimbra.common.auth.ZAuthToken;
import com.zimbra.common.localconfig.LC;
import com.zimbra.common.net.SocketFactories;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.SoapTransport;
import com.zimbra.common.soap.SoapHttpTransport.HttpDebugListener;
import com.zimbra.common.util.AccountLogger;
import com.zimbra.common.util.ByteUtil;
import com.zimbra.common.util.CliUtil;
import com.zimbra.common.util.DateUtil;
import com.zimbra.common.util.FileUtil;
import com.zimbra.common.util.Pair;
import com.zimbra.common.util.SetUtil;
import com.zimbra.common.util.StringUtil;
import com.zimbra.common.util.Version;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.common.zclient.ZClientException;
import com.zimbra.cs.account.Provisioning.AccountBy;
import com.zimbra.cs.account.Provisioning.CacheEntry;
import com.zimbra.cs.account.Provisioning.CacheEntryBy;
import com.zimbra.cs.account.Provisioning.CacheEntryType;
import com.zimbra.cs.account.Provisioning.CalendarResourceBy;
import com.zimbra.cs.account.Provisioning.CountObjectsType;
import com.zimbra.cs.account.Provisioning.CosBy;
import com.zimbra.cs.account.Provisioning.CountAccountResult;
import com.zimbra.cs.account.Provisioning.DataSourceBy;
import com.zimbra.cs.account.Provisioning.DistributionListBy;
import com.zimbra.cs.account.Provisioning.DomainBy;
import com.zimbra.cs.account.Provisioning.GranteeBy;
import com.zimbra.cs.account.Provisioning.MailMode;
import com.zimbra.cs.account.Provisioning.PublishShareInfoAction;
import com.zimbra.cs.account.Provisioning.PublishedShareInfoVisitor;
import com.zimbra.cs.account.Provisioning.RightsDoc;
import com.zimbra.cs.account.Provisioning.SearchGalResult;
import com.zimbra.cs.account.Provisioning.ServerBy;
import com.zimbra.cs.account.Provisioning.SetPasswordResult;
import com.zimbra.cs.account.Provisioning.SignatureBy;
import com.zimbra.cs.account.Provisioning.TargetBy;
import com.zimbra.cs.account.Provisioning.XMPPComponentBy;
import com.zimbra.cs.account.accesscontrol.AdminRight;
import com.zimbra.cs.account.accesscontrol.GranteeType;
import com.zimbra.cs.account.accesscontrol.AttrRight;
import com.zimbra.cs.account.accesscontrol.ComboRight;
import com.zimbra.cs.account.accesscontrol.Help;
import com.zimbra.cs.account.accesscontrol.Right;
import com.zimbra.cs.account.accesscontrol.RightClass;
import com.zimbra.cs.account.accesscontrol.RightCommand;
import com.zimbra.cs.account.accesscontrol.RightManager;
import com.zimbra.cs.account.accesscontrol.RightModifier;
import com.zimbra.cs.account.accesscontrol.TargetType;
import com.zimbra.cs.account.accesscontrol.Right.RightType;
import com.zimbra.cs.account.ldap.LdapEntrySearchFilter;
import com.zimbra.cs.account.ldap.LdapProvisioning;
import com.zimbra.cs.account.ldap.ZimbraLdapContext;
import com.zimbra.cs.account.soap.SoapProvisioning;
import com.zimbra.cs.account.soap.SoapProvisioning.MailboxInfo;
import com.zimbra.cs.account.soap.SoapProvisioning.MemcachedClientConfig;
import com.zimbra.cs.account.soap.SoapProvisioning.QuotaUsage;
import com.zimbra.cs.account.soap.SoapProvisioning.ReIndexBy;
import com.zimbra.cs.account.soap.SoapProvisioning.ReIndexInfo;
import com.zimbra.cs.extension.ExtensionDispatcherServlet;
import com.zimbra.cs.fb.FbCli;
import com.zimbra.cs.httpclient.URLUtil;
import com.zimbra.cs.util.BuildInfo;
import com.zimbra.cs.util.SoapCLI;
import com.zimbra.cs.wiki.WikiUtil;
import com.zimbra.cs.zclient.ZMailboxUtil;

/**
 * @author schemers
 */
public class ProvUtil implements HttpDebugListener {

    private static final String ERR_VIA_SOAP_ONLY = "can only be used with SOAP";
    private static final String ERR_VIA_LDAP_ONLY = "can only be used with  \"zmprov -l/--ldap\"";
    private static final String ERR_INVALID_ARG_EV = "arg -e is invalid unless -v is also specified";

    private static final PrintStream console = System.out;
    private static final PrintStream errConsole = System.err;
    
    enum SoapDebugLevel {
        none,    // no SOAP debug
        normal,  // SOAP request and response payload
        high;    // SOAP payload and http transport header
    }

    private boolean mInteractive = false;
    private boolean mVerbose = false;
    private SoapDebugLevel mDebug = SoapDebugLevel.none;
    private boolean mUseLdap = LC.zimbra_zmprov_default_to_ldap.booleanValue();
    private boolean mUseLdapMaster = false;
    private String mAccount = null;
    private String mPassword = null;
    private ZAuthToken mAuthToken = null;
    private String mServer = LC.zimbra_zmprov_default_soap_server.value();
    private int mPort = LC.zimbra_admin_service_port.intValue();
    private Command mCommand;
    private Map<String,Command> mCommandIndex;
    private Provisioning mProv;
    private BufferedReader mReader;
    private boolean mOutputBinaryToFile;

    private boolean errorOccursDuringInteraction = false; // bug 58554

    public void setDebug(SoapDebugLevel debug) { mDebug = debug; }

    public void setVerbose(boolean verbose) { mVerbose = verbose; }

    public void setUseLdap(boolean useLdap, boolean useMaster) { mUseLdap = useLdap; mUseLdapMaster = useMaster; }

    public void setAccount(String account) { mAccount = account; mUseLdap = false;}

    public void setPassword(String password) { mPassword = password; mUseLdap = false; }

    public void setAuthToken(ZAuthToken zat) { mAuthToken = zat; mUseLdap = false; }
    
    private void setOutputBinaryToFile(boolean outputBinaryToFile) {
        mOutputBinaryToFile = outputBinaryToFile;
    }
    
    private boolean outputBinaryToFile() {
        return mOutputBinaryToFile;
    }

    public void setServer(String server ) {
        int i = server.indexOf(":");
        if (i == -1) {
            mServer = server;
        } else {
            mServer = server.substring(0, i);
            mPort = Integer.parseInt(server.substring(i+1));
        }
        mUseLdap = false;
    }

    public boolean useLdap() { return mUseLdap; }

    private void deprecated() {
        console.println("This command has been deprecated.");
        System.exit(1);
    }
    
    private void usage() {
        usage(null);
    }

    private void usage(Command.Via violatedVia) {
        if (mCommand != null) {
            if (violatedVia == null) {
                console.printf("usage:  %s(%s) %s\n", mCommand.getName(), mCommand.getAlias(), mCommand.getHelp());
                CommandHelp extraHelp = mCommand.getExtraHelp();
                if (extraHelp != null)
                    extraHelp.printHelp();
            }
            else {
                if (violatedVia == Command.Via.ldap)
                    console.printf("%s %s\n", mCommand.getName(), ERR_VIA_LDAP_ONLY);
                else
                    console.printf("%s %s\n", mCommand.getName(), ERR_VIA_SOAP_ONLY);
            }
        }

        if (mInteractive)
            return;

        console.println("");
        console.println("zmprov [args] [cmd] [cmd-args ...]");
        console.println("");
        console.println("  -h/--help                             display usage");
        console.println("  -f/--file                             use file as input stream");
        console.println("  -s/--server   {host}[:{port}]         server hostname and optional port");
        console.println("  -l/--ldap                             provision via LDAP instead of SOAP");
        console.println("  -L/--logpropertyfile                  log4j property file, valid only with -l");
        console.println("  -a/--account  {name}                  account name to auth as");
        console.println("  -p/--password {pass}                  password for account");
        console.println("  -P/--passfile {file}                  read password from file");
        console.println("  -z/--zadmin                           use zimbra admin name/password from localconfig for admin/password");
        console.println("  -y/--authtoken {authtoken}            " + SoapCLI.OPT_AUTHTOKEN.getDescription());
        console.println("  -Y/--authtokenfile {authtoken file}   " + SoapCLI.OPT_AUTHTOKENFILE.getDescription());
        console.println("  -v/--verbose                          verbose mode (dumps full exception stack trace)");
        console.println("  -d/--debug                            debug mode (dumps SOAP messages)");
        console.println("  -m/--master                           use LDAP master (only valid with -l)");
        console.println("");
        doHelp(null);
        System.exit(1);
    }

    public static enum Category {
        ACCOUNT("help on account-related commands"),
        CALENDAR("help on calendar resource-related commands"),
        COMMANDS("help on all commands"),
        CONFIG("help on config-related commands"),
        COS("help on COS-related commands"),
        DOMAIN("help on domain-related commands"),
        FREEBUSY("help on free/busy-related commands"),
        LIST("help on distribution list-related commands"),
        LOG("help on logging commands"),
        MISC("help on misc commands"),
        MAILBOX("help on mailbox-related commands"),
        NOTEBOOK("help on notebook-related commands"),
        REVERSEPROXY("help on reverse proxy related commands"),
        RIGHT("help on right-related commands"),
        SEARCH("help on search-related commands"),
        SERVER("help on server-related commands"),
        SHARE("help on share related commands");


        String mDesc;

        public String getDescription() { return mDesc; }

        Category(String desc) {
            mDesc = desc;
        }

        static void help(Category cat) {
            if (cat == CALENDAR)
                helpCALENDAR();
            else if (cat == RIGHT)
                helpRIGHT();
            else if (cat == LOG)
                helpLOG();
        }

        static void helpCALENDAR() {
            console.println("");
            StringBuilder sb = new StringBuilder();
            EntrySearchFilter.Operator vals[] = EntrySearchFilter.Operator.values();
            for (int i = 0; i < vals.length; i++) {
                if (i > 0)
                    sb.append(", ");
                sb.append(vals[i].toString());
            }
            console.println("    op = " + sb.toString());
        }

        static void helpRIGHT() {
            helpRIGHTCommon();
            // helpRIGHTRights(true);
            helpRIGHTRights(false);
        }

        static void helpRIGHTCommand() {
            helpRIGHTCommon();
            helpRIGHTRights(false);
        }

        static void helpRIGHTRights(boolean printRights) {
            // rights
            console.println();
            console.println("    {right}: if right is prefixed with a '-', it means negative right, i.e., specifically deny");

            if (printRights) {
                try {
                    Map<String, AdminRight> allAdminRights = RightManager.getInstance().getAllAdminRights();
                    // print non-combo rights first
                    for (com.zimbra.cs.account.accesscontrol.Right r : allAdminRights.values()) {
                        if (RightType.combo != r.getRightType())
                        console.println("        " + r.getName() + " (" + r.getRightType().toString() + ")");
                    }
                    // then combo rights
                    for (com.zimbra.cs.account.accesscontrol.Right r : allAdminRights.values()) {
                        if (RightType.combo == r.getRightType())
                            console.println("        " + r.getName() + " (" + r.getRightType().toString() + ")");
                    }
                } catch (ServiceException e) {
                    console.println("cannot get RightManager instance: " + e.getMessage());
                }
            } else {
                console.println("             for complete list of rights, do \"zmprov [-l] gar\"");
            }

            console.println();
        }

        static void helpRIGHTCommon() {

            // target types
            console.println();
            StringBuilder tt = new StringBuilder();
            StringBuilder ttNeedsTargetIdentity = new StringBuilder();
            TargetType[] tts = TargetType.values();
            for (int i = 0; i < tts.length; i++) {
                if (i > 0)
                    tt.append(", ");
                tt.append(tts[i].getCode());

                if (tts[i].needsTargetIdentity())
                    ttNeedsTargetIdentity.append(tts[i].getCode() + " ");
            }
            console.println("    {target-type} = " + tt.toString());
            console.println();
            console.println("    {target-id|target-name} is required if target-type is: " + ttNeedsTargetIdentity + ",");
            console.println("        otherwise {target-id|target-name} should not be specified");

            // grantee types
            console.println();
            StringBuilder gt = new StringBuilder();
            StringBuilder gtNeedsGranteeIdentity = new StringBuilder();
            GranteeType[] gts = GranteeType.values();
            for (int i = 0; i < gts.length; i++) {
                if (i > 0)
                    gt.append(", ");
                gt.append(gts[i].getCode());

                if (gts[i].needsGranteeIdentity())
                    gtNeedsGranteeIdentity.append(gts[i].getCode() + " ");
            }
            console.println("    {grantee-type} = " + gt.toString());
            console.println();
            console.println("    {grantee-id|grantee-name} is required if grantee-type is: " + gtNeedsGranteeIdentity + ",");
            console.println("        otherwise {target-id|target-name} should not be specified");

        }

        static void helpLOG() {
            console.println("    Log categories:");
            int maxNameLength = 0;
            for (String name : ZimbraLog.CATEGORY_DESCRIPTIONS.keySet()) {
                if (name.length() > maxNameLength) {
                    maxNameLength = name.length();
                }
            }
            for (String name : ZimbraLog.CATEGORY_DESCRIPTIONS.keySet()) {
                console.print("        " + name);
                for (int i = 0; i < (maxNameLength - name.length()); i++) {
                    console.print(" ");
                }
                console.format(" - %s\n", ZimbraLog.CATEGORY_DESCRIPTIONS.get(name));
            }
        }
    }

    // TODO: refactor to own class
    interface CommandHelp {
        public void printHelp();
    }

    static class RightCommandHelp implements CommandHelp {
        @Override
        public void printHelp() {
            Category.helpRIGHTCommand();
        }
    }

    static class ReindexCommandHelp implements CommandHelp {
        @Override
        public void printHelp() {
            /*
             * copied from soap-admin.txt
             * Not exactly match all types in MailboxIndex
             * TODO: cleanup
             */
            console.println();
            console.println("Valid types:");
            console.println("    appointment");
            // console.println("    briefcase");
            // console.println("    chat");
            console.println("    contact");
            console.println("    conversation");
            console.println("    document");
            console.println("    message");
            console.println("    note");
            // console.println("    tag");
            console.println("    task");
            console.println("    wiki");
            console.println();
        }
    }

    static class CountObjectsHelp implements CommandHelp {
        @Override
        public void printHelp() {
            console.println();
            console.println("Valid types:");
            for (CountObjectsType type : CountObjectsType.values())
                console.println("    " + type.toString());
        }
    }

    public enum Command {
        ADD_ACCOUNT_ALIAS("addAccountAlias", "aaa", "{name@domain|id} {alias@domain}", Category.ACCOUNT, 2, 2),
        ADD_ACCOUNT_LOGGER("addAccountLogger", "aal", "[-s/--server hostname] {name@domain|id} {logging-category} {trace|debug|info|warn|error}", Category.LOG, 3, 5),
        ADD_DISTRIBUTION_LIST_ALIAS("addDistributionListAlias", "adla", "{list@domain|id} {alias@domain}", Category.LIST, 2, 2),
        ADD_DISTRIBUTION_LIST_MEMBER("addDistributionListMember", "adlm", "{list@domain|id} {member@domain}+", Category.LIST, 2, Integer.MAX_VALUE),
        AUTO_COMPLETE_GAL("autoCompleteGal", "acg", "{domain} {name}", Category.SEARCH, 2, 2),
        CHECK_PASSWORD_STRENGTH("checkPasswordStrength", "cps", "{name@domain|id} {password}", Category.ACCOUNT, 2, 2),
        CHECK_RIGHT("checkRight", "ckr", "{target-type} [{target-id|target-name}] {grantee-id|grantee-name (note:can only check internal user)} {right}", Category.RIGHT, 3, 4, null, new RightCommandHelp()),
        COPY_COS("copyCos", "cpc", "{src-cos-name|id} {dest-cos-name}", Category.COS, 2, 2),
        COUNT_ACCOUNT("countAccount", "cta", "{domain|id}", Category.DOMAIN, 1, 1),
        COUNT_OBJECTS("countObjects", "cto", "{type} [-d {domain|id}]", Category.MISC, 1, 3, Via.ldap, new CountObjectsHelp()), // add more counting types later if needed.
        CREATE_ACCOUNT("createAccount", "ca", "{name@domain} {password} [attr1 value1 [attr2 value2...]]", Category.ACCOUNT, 2, Integer.MAX_VALUE),
        CREATE_ALIAS_DOMAIN("createAliasDomain", "cad", "{alias-domain-name} {local-domain-name|id} [attr1 value1 [attr2 value2...]]", Category.DOMAIN, 2, Integer.MAX_VALUE),
        CREATE_BULK_ACCOUNTS("createBulkAccounts", "cabulk", "{domain} {namemask} {number of accounts to create}", Category.MISC, 3, 3),
        CREATE_CALENDAR_RESOURCE("createCalendarResource",  "ccr", "{name@domain} {password} [attr1 value1 [attr2 value2...]]", Category.CALENDAR, 2, Integer.MAX_VALUE),
        CREATE_COS("createCos", "cc", "{name} [attr1 value1 [attr2 value2...]]", Category.COS, 1, Integer.MAX_VALUE),
        CREATE_DATA_SOURCE("createDataSource", "cds", "{name@domain} {ds-type} {ds-name} zimbraDataSourceEnabled {TRUE|FALSE} zimbraDataSourceFolderId {folder-id} [attr1 value1 [attr2 value2...]]", Category.ACCOUNT, 3, Integer.MAX_VALUE),
        CREATE_DISTRIBUTION_LIST("createDistributionList", "cdl", "{list@domain}", Category.LIST, 1, Integer.MAX_VALUE),
        CREATE_DISTRIBUTION_LISTS_BULK("createDistributionListsBulk", "cdlbulk"),
        CREATE_DOMAIN("createDomain", "cd", "{domain} [attr1 value1 [attr2 value2...]]", Category.DOMAIN, 1, Integer.MAX_VALUE),
        CREATE_SERVER("createServer", "cs", "{name} [attr1 value1 [attr2 value2...]]", Category.SERVER, 1, Integer.MAX_VALUE),
        CREATE_IDENTITY("createIdentity", "cid", "{name@domain} {identity-name} [attr1 value1 [attr2 value2...]]", Category.ACCOUNT, 2, Integer.MAX_VALUE),
        CREATE_SIGNATURE("createSignature", "csig", "{name@domain} {signature-name} [attr1 value1 [attr2 value2...]]", Category.ACCOUNT, 2, Integer.MAX_VALUE),
        CREATE_XMPP_COMPONENT("createXMPPComponent", "cxc", "{short-name} {domain}  {server} {classname} {category} {type} [attr value1 [attr2 value2...]]", Category.CONFIG, 6, Integer.MAX_VALUE),
        DELETE_ACCOUNT("deleteAccount", "da", "{name@domain|id}", Category.ACCOUNT, 1, 1),
        DELETE_CALENDAR_RESOURCE("deleteCalendarResource",  "dcr", "{name@domain|id}", Category.CALENDAR, 1, 1),
        DELETE_COS("deleteCos", "dc", "{name|id}", Category.COS, 1, 1),
        DELETE_DATA_SOURCE("deleteDataSource", "dds", "{name@domain|id} {ds-name|ds-id}", Category.ACCOUNT, 2, 2),
        DELETE_DISTRIBUTION_LIST("deleteDistributionList", "ddl", "{list@domain|id}", Category.LIST, 1, 1),
        DELETE_DOMAIN("deleteDomain", "dd", "{domain|id}", Category.DOMAIN, 1, 1),
        DELETE_IDENTITY("deleteIdentity", "did", "{name@domain|id} {identity-name}", Category.ACCOUNT, 2, 2),
        DELETE_SIGNATURE("deleteSignature", "dsig", "{name@domain|id} {signature-name}", Category.ACCOUNT, 2, 2),
        DELETE_SERVER("deleteServer", "ds", "{name|id}", Category.SERVER, 1, 1),
        DELETE_XMPP_COMPONENT("deleteXMPPComponent", "dxc", "{xmpp-component-name}", Category.CONFIG, 1, 1),
        DESCRIBE("describe", "desc", "[[-v] [-ni] [{entry-type}]] | [-a {attribute-name}]", Category.MISC, 0, Integer.MAX_VALUE, null, null, true),
        EXIT("exit", "quit", "", Category.MISC, 0, 0),
        FLUSH_CACHE("flushCache", "fc", "[-a] {"+CacheEntryType.names()+"|<extension-cache-type>} [name1|id1 [name2|id2...]]", Category.MISC, 1, Integer.MAX_VALUE),
        GENERATE_DOMAIN_PRE_AUTH("generateDomainPreAuth", "gdpa", "{domain|id} {name|id|foreignPrincipal} {by} {timestamp|0} {expires|0}", Category.MISC, 5, 6),
        GENERATE_DOMAIN_PRE_AUTH_KEY("generateDomainPreAuthKey", "gdpak", "[-f] {domain|id}", Category.MISC, 1, 2),
        GET_ACCOUNT("getAccount", "ga", "[-e] {name@domain|id} [attr1 [attr2...]]", Category.ACCOUNT, 1, Integer.MAX_VALUE),
        GET_DATA_SOURCES("getDataSources", "gds", "{name@domain|id} [arg1 [arg2...]]", Category.ACCOUNT, 1, Integer.MAX_VALUE),
        GET_IDENTITIES("getIdentities", "gid", "{name@domain|id} [arg1 [arg...]]", Category.ACCOUNT, 1, Integer.MAX_VALUE),
        GET_SIGNATURES("getSignatures", "gsig", "{name@domain|id} [arg1 [arg...]]", Category.ACCOUNT, 1, Integer.MAX_VALUE),
        GET_ACCOUNT_MEMBERSHIP("getAccountMembership", "gam", "{name@domain|id}", Category.ACCOUNT, 1, 2),
        GET_ALL_ACCOUNTS("getAllAccounts","gaa", "[-v] [-e] [-s server] [{domain}]", Category.ACCOUNT, 0, 5),
        GET_ACCOUNT_LOGGERS("getAccountLoggers", "gal", "[-s/--server hostname] {name@domain|id}", Category.LOG, 1, 3),
        GET_ALL_ACCOUNT_LOGGERS("getAllAccountLoggers", "gaal", "[-s/--server hostname]", Category.LOG, 0, 2),
        GET_ALL_ADMIN_ACCOUNTS("getAllAdminAccounts", "gaaa", "[-v] [-e] [attr1 [attr2...]]", Category.ACCOUNT, 0, Integer.MAX_VALUE),
        GET_ALL_CALENDAR_RESOURCES("getAllCalendarResources", "gacr", "[-v] [-e] [-s server] [{domain}]", Category.CALENDAR, 0, 5),
        GET_ALL_CONFIG("getAllConfig", "gacf", "[attr1 [attr2...]]", Category.CONFIG, 0, Integer.MAX_VALUE),
        GET_ALL_COS("getAllCos", "gac", "[-v]", Category.COS, 0, 1),
        GET_ALL_DISTRIBUTION_LISTS("getAllDistributionLists", "gadl", "[-v] [{domain}]", Category.LIST, 0, 2),
        GET_ALL_DOMAINS("getAllDomains", "gad", "[-v] [-e] [attr1 [attr2...]]", Category.DOMAIN, 0, Integer.MAX_VALUE),
        GET_ALL_EFFECTIVE_RIGHTS("getAllEffectiveRights", "gaer", "{grantee-type} {grantee-id|grantee-name} [expandSetAttrs] [expandGetAttrs]", Category.RIGHT, 2, 4),
        GET_ALL_FREEBUSY_PROVIDERS("getAllFbp", "gafbp", "[-v]", Category.FREEBUSY, 0, 1),
        GET_ALL_RIGHTS("getAllRights", "gar", "[-v] [-t {target-type}] [-c " + RightClass.allValuesInString("|") + "]", Category.RIGHT, 0, 5),
        GET_ALL_SERVERS("getAllServers", "gas", "[-v] [-e] [service]", Category.SERVER, 0, 3),
        GET_ALL_XMPP_COMPONENTS("getAllXMPPComponents", "gaxcs", "", Category.CONFIG, 0, 0),
        GET_AUTH_TOKEN_INFO("getAuthTokenInfo", "gati", "{auth-token}", Category.MISC, 1, 1),
        GET_CALENDAR_RESOURCE("getCalendarResource",     "gcr", "{name@domain|id} [attr1 [attr2...]]", Category.CALENDAR, 1, Integer.MAX_VALUE),
        GET_CONFIG("getConfig", "gcf", "{name}", Category.CONFIG, 1, 1),
        GET_COS("getCos", "gc", "{name|id} [attr1 [attr2...]]", Category.COS, 1, Integer.MAX_VALUE),
        GET_DISTRIBUTION_LIST("getDistributionList", "gdl", "{list@domain|id} [attr1 [attr2...]]", Category.LIST, 1, Integer.MAX_VALUE),
        GET_DISTRIBUTION_LIST_MEMBERSHIP("getDistributionListMembership", "gdlm", "{name@domain|id}", Category.LIST, 1, 1),
        GET_DOMAIN("getDomain", "gd", "[-e] {domain|id} [attr1 [attr2...]]", Category.DOMAIN, 1, Integer.MAX_VALUE),
        GET_DOMAIN_INFO("getDomainInfo", "gdi", "name|id|virtualHostname {value} [attr1 [attr2...]]", Category.DOMAIN, 2, Integer.MAX_VALUE),
        GET_CONFIG_SMIME_CONFIG("getConfigSMIMEConfig", "gcsc", "[configName]", Category.DOMAIN, 0, 1),
        GET_DOMAIN_SMIME_CONFIG("getDomainSMIMEConfig", "gdsc", "name|id [configName]", Category.DOMAIN, 1, 2),
        GET_EFFECTIVE_RIGHTS("getEffectiveRights", "ger", "{target-type} [{target-id|target-name}] {grantee-id|grantee-name} [expandSetAttrs] [expandGetAttrs]", Category.RIGHT, 1, 5, null, new RightCommandHelp()),

        // for testing the provisioning interface only, comment out after testing, the soap is only used by admin console
        GET_CREATE_OBJECT_ATTRS("getCreateObjectAttrs", "gcoa", "{target-type} {domain-id|domain-name} {cos-id|cos-name} {grantee-id|grantee-name}", Category.RIGHT, 3, 4),

        GET_FREEBUSY_QUEUE_INFO("getFreebusyQueueInfo", "gfbqi", "[{provider-name}]", Category.FREEBUSY, 0, 1),
        GET_GRANTS("getGrants", "gg", "[-t {target-type} [{target-id|target-name}]] [-g {grantee-type} {grantee-id|grantee-name} [{0|1 (whether to include grants granted to groups the grantee belongs)}]]", Category.RIGHT, 2, 7, null, new RightCommandHelp()),
        GET_MAILBOX_INFO("getMailboxInfo", "gmi", "{account}", Category.MAILBOX, 1, 1),
        GET_PUBLISHED_DISTRIBUTION_LIST_SHARE_INFO("getPublishedDistributionListShareInfo", "gpdlsi", "{dl-name|dl-id} [{owner-name|owner-id}]", Category.SHARE, 1, 2),
        GET_QUOTA_USAGE("getQuotaUsage", "gqu", "{server}", Category.MAILBOX, 1, 1),
        GET_RIGHT("getRight", "gr", "{right} [-e]", Category.RIGHT, 1, 2),
        GET_RIGHTS_DOC("getRightsDoc", "grd", "[java packages]", Category.RIGHT, 0, Integer.MAX_VALUE),
        GET_SERVER("getServer", "gs", "[-e] {name|id} [attr1 [attr2...]]", Category.SERVER, 1, Integer.MAX_VALUE),
        GET_SHARE_INFO("getShareInfo", "gsi", "{owner-name|owner-id}", Category.SHARE, 1, 1),
        GET_SPNEGO_DOMAIN("getSpnegoDomain", "gsd", "", Category.MISC, 0, 0),
        GET_XMPP_COMPONENT("getXMPPComponent", "gxc", "{name|id} [attr1 [attr2...]]", Category.CONFIG, 1, Integer.MAX_VALUE),
        GRANT_RIGHT("grantRight", "grr", "{target-type} [{target-id|target-name}] {grantee-type} [{grantee-id|grantee-name} [secret]] {[-]right}", Category.RIGHT, 3, 6, null, new RightCommandHelp()),
        HELP("help", "?", "commands", Category.MISC, 0, 1),
        IMPORT_NOTEBOOK("importNotebook", "impn", "{name@domain} {directory} {folder}", Category.NOTEBOOK),
        INIT_NOTEBOOK("initNotebook", "in", "[{name@domain}]", Category.NOTEBOOK),
        INIT_DOMAIN_NOTEBOOK("initDomainNotebook", "idn", "{domain} [{name@domain}]", Category.NOTEBOOK),
        LDAP(".ldap", ".l"),
        MODIFY_ACCOUNT("modifyAccount", "ma", "{name@domain|id} [attr1 value1 [attr2 value2...]]", Category.ACCOUNT, 3, Integer.MAX_VALUE),
        MODIFY_CALENDAR_RESOURCE("modifyCalendarResource",  "mcr", "{name@domain|id} [attr1 value1 [attr2 value2...]]", Category.CALENDAR, 3, Integer.MAX_VALUE),
        MODIFY_CONFIG("modifyConfig", "mcf", "attr1 value1 [attr2 value2...]", Category.CONFIG, 2, Integer.MAX_VALUE),
        MODIFY_COS("modifyCos", "mc", "{name|id} [attr1 value1 [attr2 value2...]]", Category.COS, 3, Integer.MAX_VALUE),
        MODIFY_DATA_SOURCE("modifyDataSource", "mds", "{name@domain|id} {ds-name|ds-id} [attr1 value1 [attr2 value2...]]", Category.ACCOUNT, 4, Integer.MAX_VALUE),
        MODIFY_DISTRIBUTION_LIST("modifyDistributionList", "mdl", "{list@domain|id} attr1 value1 [attr2 value2...]", Category.LIST, 3, Integer.MAX_VALUE),
        MODIFY_DOMAIN("modifyDomain", "md", "{domain|id} [attr1 value1 [attr2 value2...]]", Category.DOMAIN, 3, Integer.MAX_VALUE),
        MODIFY_CONFIG_SMIME_CONFIG("modifyConfigSMIMEConfig", "mcsc", "configName [attr2 value2...]]", Category.DOMAIN, 1, Integer.MAX_VALUE),
        MODIFY_DOMAIN_SMIME_CONFIG("modifyDomainSMIMEConfig", "mdsc", "name|id configName [attr2 value2...]]", Category.DOMAIN, 2, Integer.MAX_VALUE),
        MODIFY_IDENTITY("modifyIdentity", "mid", "{name@domain|id} {identity-name} [attr1 value1 [attr2 value2...]]", Category.ACCOUNT, 4, Integer.MAX_VALUE),
        MODIFY_SIGNATURE("modifySignature", "msig", "{name@domain|id} {signature-name|signature-id} [attr1 value1 [attr2 value2...]]", Category.ACCOUNT, 4, Integer.MAX_VALUE),
        MODIFY_SERVER("modifyServer", "ms", "{name|id} [attr1 value1 [attr2 value2...]]", Category.SERVER, 3, Integer.MAX_VALUE),
        MODIFY_XMPP_COMPONENT("modifyXMPPComponent", "mxc", "{name@domain} [attr1 value1 [attr value2...]]", Category.CONFIG, 3, Integer.MAX_VALUE),
        PUBLISH_DISTRIBUTION_LIST_SHARE_INFO("publishDistributionListShareInfo", "pdlsi", "{+|-} {dl-name@domain|id} {owner-name|owner-id} [{folder-path|folder-id}]", Category.SHARE, 3, 4),
        PUSH_FREEBUSY("pushFreebusy", "pfb", "[account-id ...]", Category.FREEBUSY, 1, Integer.MAX_VALUE),
        PUSH_FREEBUSY_DOMAIN("pushFreebusyDomain", "pfbd", "{domain}", Category.FREEBUSY, 1, 1),
        PURGE_ACCOUNT_CALENDAR_CACHE("purgeAccountCalendarCache", "pacc", "{name@domain|id} [...]", Category.CALENDAR, 1, Integer.MAX_VALUE),
        PURGE_FREEBUSY_QUEUE("purgeFreebusyQueue", "pfbq", "[{provider-name}]", Category.FREEBUSY, 0, 1),
        RECALCULATE_MAILBOX_COUNTS("recalculateMailboxCounts", "rmc", "{name@domain|id}", Category.MAILBOX, 1, 1),
        REMOVE_ACCOUNT_ALIAS("removeAccountAlias", "raa", "{name@domain|id} {alias@domain}", Category.ACCOUNT, 2, 2),
        REMOVE_ACCOUNT_LOGGER("removeAccountLogger", "ral", "[-s/--server hostname] [{name@domain|id}] [{logging-category}]", Category.LOG, 0, 4),
        REMOVE_DISTRIBUTION_LIST_ALIAS("removeDistributionListAlias", "rdla", "{list@domain|id} {alias@domain}", Category.LIST, 2, 2),
        REMOVE_DISTRIBUTION_LIST_MEMBER("removeDistributionListMember", "rdlm", "{list@domain|id} {member@domain}", Category.LIST, 2, Integer.MAX_VALUE),
        REMOVE_CONFIG_SMIME_CONFIG("removeConfigSMIMEConfig", "rcsc", "configName", Category.DOMAIN, 1, 1),
        REMOVE_DOMAIN_SMIME_CONFIG("removeDomainSMIMEConfig", "rdsc", "name|id configName", Category.DOMAIN, 2, 2),
        RENAME_ACCOUNT("renameAccount", "ra", "{name@domain|id} {newName@domain}", Category.ACCOUNT, 2, 2),
        RENAME_CALENDAR_RESOURCE("renameCalendarResource",  "rcr", "{name@domain|id} {newName@domain}", Category.CALENDAR, 2, 2),
        RENAME_COS("renameCos", "rc", "{name|id} {newName}", Category.COS, 2, 2),
        RENAME_DISTRIBUTION_LIST("renameDistributionList", "rdl", "{list@domain|id} {newName@domain}", Category.LIST, 2, 2),
        RENAME_DOMAIN("renameDomain", "rd", "{domain|id} {newDomain}", Category.DOMAIN, 2, 2, Via.ldap),
        REINDEX_MAILBOX("reIndexMailbox", "rim", "{name@domain|id} {start|status|cancel} [{types|ids} {type or id} [,type or id...]]", Category.MAILBOX, 2, Integer.MAX_VALUE, null, new ReindexCommandHelp()),
        REVOKE_RIGHT("revokeRight", "rvr", "{target-type} [{target-id|target-name}] {grantee-type} [{grantee-id|grantee-name}] {[-]right}", Category.RIGHT, 3, 5, null, new RightCommandHelp()),
        SEARCH_ACCOUNTS("searchAccounts", "sa", "[-v] {ldap-query} [limit {limit}] [offset {offset}] [sortBy {attr}] [sortAscending 0|1*] [domain {domain}]", Category.SEARCH, 1, Integer.MAX_VALUE),
        SEARCH_CALENDAR_RESOURCES("searchCalendarResources", "scr", "[-v] domain attr op value [attr op value...]", Category.SEARCH),
        SEARCH_GAL("searchGal", "sg", "{domain} {name} [limit {limit}] [offset {offset}] [sortBy {attr}]", Category.SEARCH, 2, Integer.MAX_VALUE),
        SELECT_MAILBOX("selectMailbox", "sm", "{account-name} [{zmmailbox commands}]", Category.MAILBOX, 1, Integer.MAX_VALUE),
        SET_ACCOUNT_COS("setAccountCos", "sac", "{name@domain|id} {cos-name|cos-id}", Category.ACCOUNT, 2, 2),
        SET_PASSWORD("setPassword", "sp", "{name@domain|id} {password}", Category.ACCOUNT, 2, 2),
        GET_ALL_MTA_AUTH_URLS("getAllMtaAuthURLs", "gamau", "", Category.SERVER, 0, 0),
        GET_ALL_REVERSE_PROXY_URLS("getAllReverseProxyURLs", "garpu", "", Category.REVERSEPROXY, 0, 0),
        GET_ALL_REVERSE_PROXY_BACKENDS("getAllReverseProxyBackends", "garpb", "", Category.REVERSEPROXY, 0, 0),
        GET_ALL_REVERSE_PROXY_DOMAINS("getAllReverseProxyDomains", "garpd", "", Category.REVERSEPROXY, 0, 0),
        GET_ALL_MEMCACHED_SERVERS("getAllMemcachedServers", "gamcs", "", Category.SERVER, 0, 0),
        RELOAD_MEMCACHED_CLIENT_CONFIG("reloadMemcachedClientConfig", "rmcc", "all | mailbox-server [...]", Category.MISC, 1, Integer.MAX_VALUE, Via.soap),
        GET_MEMCACHED_CLIENT_CONFIG("getMemcachedClientConfig", "gmcc", "all | mailbox-server [...]", Category.MISC, 1, Integer.MAX_VALUE, Via.soap),
        SOAP(".soap", ".s"),
        SYNC_GAL("syncGal", "syg", "{domain} [{token}]", Category.MISC, 1, 2),
        UPDATE_TEMPLATES("updateTemplates", "ut", "[-h host] {template-directory}", Category.NOTEBOOK, 1, 3);

        private String mName;
        private String mAlias;
        private String mHelp;
        private CommandHelp mExtraHelp;
        private Category mCat;
        private int mMinArgLength = 0;
        private int mMaxArgLength = Integer.MAX_VALUE;
        private Via mVia;
        private boolean mNeedsSchemaExtension = false;

        public static enum Via {
            soap, ldap;
        }

        public String getName() { return mName; }
        public String getAlias() { return mAlias; }
        public String getHelp() { return mHelp; }
        public CommandHelp getExtraHelp() { return mExtraHelp; }
        public Category getCategory() { return mCat; }
        public boolean hasHelp() { return mHelp != null; }
        public boolean checkArgsLength(String args[]) {
            int len = args == null ? 0 : args.length - 1;
            return len >= mMinArgLength && len <= mMaxArgLength;
        }
        public Via getVia() { return mVia; }
        public boolean needsSchemaExtension() {
            return mNeedsSchemaExtension || (mCat == Category.RIGHT);
        }
        public boolean isDeprecated() {
            switch (mCat) {
            case NOTEBOOK:
                return true;
            default:
                return false;
            }
        }

        private Command(String name, String alias) {
            mName = name;
            mAlias = alias;
        }

        private Command(String name, String alias, String help, Category cat)  {
            mName = name;
            mAlias = alias;
            mHelp = help;
            mCat = cat;
        }

        private Command(String name, String alias, String help, Category cat, int minArgLength, int maxArgLength)  {
            mName = name;
            mAlias = alias;
            mHelp = help;
            mCat = cat;
            mMinArgLength = minArgLength;
            mMaxArgLength = maxArgLength;
        }

        private Command(String name, String alias, String help, Category cat, int minArgLength, int maxArgLength, Via via)  {
            mName = name;
            mAlias = alias;
            mHelp = help;
            mCat = cat;
            mMinArgLength = minArgLength;
            mMaxArgLength = maxArgLength;
            mVia = via;
        }

        private Command(String name, String alias, String help, Category cat, int minArgLength, int maxArgLength,
                Via via, CommandHelp extraHelp)  {
            mName = name;
            mAlias = alias;
            mHelp = help;
            mCat = cat;
            mMinArgLength = minArgLength;
            mMaxArgLength = maxArgLength;
            mVia = via;
            mExtraHelp = extraHelp;
        }

        private Command(String name, String alias, String help, Category cat, int minArgLength, int maxArgLength,
                Via via, CommandHelp extraHelp, boolean needsSchemaExtension)  {
            this(name, alias, help, cat, minArgLength, maxArgLength, via, extraHelp);
            mNeedsSchemaExtension = needsSchemaExtension;
        }
    }

    private void addCommand(Command command) {
        String name = command.getName().toLowerCase();
        if (mCommandIndex.get(name) != null)
            throw new RuntimeException("duplicate command: "+name);

        String alias = command.getAlias().toLowerCase();
        if (mCommandIndex.get(alias) != null)
            throw new RuntimeException("duplicate command: "+alias);

        mCommandIndex.put(name, command);
        mCommandIndex.put(alias, command);
    }

    private void initCommands() {
        mCommandIndex = new HashMap<String, Command>();

        for (Command c : Command.values())
            addCommand(c);
    }

    private Command lookupCommand(String command) {
        return mCommandIndex.get(command.toLowerCase());
    }

    /*
     * Commands that should always use LdapProvisioning, but for convenience
     * don't require the -l option specified.
     *
     * Commands that must use -l (e.g. gaa) are indicated in the Via field of the command definition
     * or in the command handler.
     * TODO: clean up all the validating in individual command handlers and use the Via mecheniam.
     */
    private boolean forceLdapButDontRequireUseLdapOption(Command command) {
        if (command == Command.HELP || command == Command.DESCRIBE)
            return true;
        else
            return false;
    }

    private ProvUtil() {
        initCommands();
    }

    public void initProvisioning() throws ServiceException {
        if (mUseLdap) {
            mProv = Provisioning.getInstance();
            if (mUseLdapMaster)
                ZimbraLdapContext.forceMasterURL();
        } else {
            SoapProvisioning sp = new SoapProvisioning();
            sp.soapSetURI(LC.zimbra_admin_service_scheme.value()+mServer+":"+mPort+AdminConstants.ADMIN_SERVICE_URI);
            if (mDebug != SoapDebugLevel.none)
                sp.soapSetHttpTransportDebugListener(this);

            if (mAccount != null && mPassword != null)
                sp.soapAdminAuthenticate(mAccount, mPassword);
            else if (mAuthToken != null)
                sp.soapAdminAuthenticate(mAuthToken);
            else
                sp.soapZimbraAdminAuthenticate();

            mProv = sp;
        }
    }

    private Command.Via violateVia(Command cmd) {
        Command.Via via = cmd.getVia();
        if (via == null)
            return null;

        if (via == Command.Via.ldap && !(mProv instanceof LdapProvisioning))
            return Command.Via.ldap;

        if (via == Command.Via.soap && !(mProv instanceof SoapProvisioning))
            return Command.Via.soap;

        return null;
    }

    private boolean execute(String args[]) throws ServiceException, ArgException, IOException {
        String [] members;
        Account account;
        AccountLoggerOptions alo;

        mCommand = lookupCommand(args[0]);

        if (mCommand == null)
            return false;

        Command.Via violatedVia = violateVia(mCommand);
        if (violatedVia != null) {
            usage(violatedVia);
            return true;
        }

        if (!mCommand.checkArgsLength(args)) {
            usage();
            return true;
        }

        if (mCommand.needsSchemaExtension()) {
            loadLdapSchemaExtensionAttrs();
        }

        switch(mCommand) {
        case ADD_ACCOUNT_ALIAS:
            mProv.addAlias(lookupAccount(args[1]), args[2]);
            break;
        case ADD_ACCOUNT_LOGGER:
            alo = parseAccountLoggerOptions(args);
            if (!mCommand.checkArgsLength(alo.args)) {
                usage();
                return true;
            }
            doAddAccountLogger(alo);
            break;
        case AUTO_COMPLETE_GAL:
            doAutoCompleteGal(args);
            break;
        case COPY_COS:
            console.println(mProv.copyCos(lookupCos(args[1]).getId(), args[2]).getId());
            break;
        case COUNT_ACCOUNT:
            doCountAccount(args);
            break;
        case COUNT_OBJECTS:
            doCountObjects(args);
            break;
        case CREATE_ACCOUNT:
            console.println(mProv.createAccount(args[1], args[2].equals("")? null : args[2], getMapAndCheck(args, 3)).getId());
            break;
        case CREATE_ALIAS_DOMAIN:
            console.println(doCreateAliasDomain(args[1], args[2], getMapAndCheck(args, 3)).getId());
            break;
        case CREATE_COS:
            console.println(mProv.createCos(args[1], getMapAndCheck(args, 2)).getId());
            break;
        case CREATE_DOMAIN:
            console.println(mProv.createDomain(args[1], getMapAndCheck(args, 2)).getId());
            break;
        case CREATE_IDENTITY:
            mProv.createIdentity(lookupAccount(args[1]), args[2], getMapAndCheck(args, 3));
            break;
        case CREATE_SIGNATURE:
            console.println(mProv.createSignature(lookupAccount(args[1]), args[2], getMapAndCheck(args, 3)).getId());
            break;
        case CREATE_DATA_SOURCE:
            console.println(mProv.createDataSource(lookupAccount(args[1]), DataSource.Type.fromString(args[2]), args[3], getMapAndCheck(args, 4)).getId());
            break;
        case CREATE_SERVER:
            console.println(mProv.createServer(args[1], getMapAndCheck(args, 2)).getId());
            break;
        case CREATE_XMPP_COMPONENT:
            doCreateXMPPComponent(args);
            break;
        case DESCRIBE:
            doDescribe(args);
            break;
        case EXIT:
            System.exit(errorOccursDuringInteraction?2:0);
            break;
        case FLUSH_CACHE:
            doFlushCache(args);
            break;
        case GENERATE_DOMAIN_PRE_AUTH_KEY:
            doGenerateDomainPreAuthKey(args);
            break;
        case GENERATE_DOMAIN_PRE_AUTH:
            doGenerateDomainPreAuth(args);
            break;
        case GET_ACCOUNT:
            doGetAccount(args);
            break;
        case GET_ACCOUNT_MEMBERSHIP:
            doGetAccountMembership(args);
            break;
        case GET_IDENTITIES:
            doGetAccountIdentities(args);
            break;
        case GET_SIGNATURES:
            doGetAccountSignatures(args);
            break;
        case GET_DATA_SOURCES:
            doGetAccountDataSources(args);
            break;
        case GET_ACCOUNT_LOGGERS:
            alo = parseAccountLoggerOptions(args);
            if (!mCommand.checkArgsLength(alo.args)) {
                usage();
                return true;
            }
            doGetAccountLoggers(alo);
            break;
        case GET_ALL_ACCOUNT_LOGGERS:
            alo = parseAccountLoggerOptions(args);
            if (!mCommand.checkArgsLength(alo.args)) {
                usage();
                return true;
            }
            doGetAllAccountLoggers(alo);
            break;
        case GET_ALL_ACCOUNTS:
            doGetAllAccounts(args);
            break;
        case GET_ALL_ADMIN_ACCOUNTS:
            doGetAllAdminAccounts(args);
            break;
        case GET_ALL_CONFIG:
            dumpAttrs(mProv.getConfig().getAttrs(), getArgNameSet(args, 1));
            break;
        case GET_ALL_COS:
            doGetAllCos(args);
            break;
        case GET_ALL_DOMAINS:
            doGetAllDomains(args);
            break;
        case GET_ALL_FREEBUSY_PROVIDERS:
            doGetAllFreeBusyProviders();
            break;
        case GET_ALL_RIGHTS:
            doGetAllRights(args);
            break;
        case GET_ALL_SERVERS:
            doGetAllServers(args);
            break;
        case GET_CONFIG:
            doGetConfig(args);
            break;
        case GET_COS:
            dumpCos(lookupCos(args[1]), getArgNameSet(args, 2));
            break;
        case GET_DISTRIBUTION_LIST_MEMBERSHIP:
            doGetDistributionListMembership(args);
            break;
        case GET_DOMAIN:
            doGetDomain(args);
            break;
        case GET_DOMAIN_INFO:
            doGetDomainInfo(args);
            break;
        case GET_CONFIG_SMIME_CONFIG:
            doGetConfigSMIMEConfig(args);
            break;
        case GET_DOMAIN_SMIME_CONFIG:    
            doGetDomainSMIMEConfig(args);
            break;
        case GET_FREEBUSY_QUEUE_INFO:
            doGetFreeBusyQueueInfo(args);
            break;
        case GET_RIGHT:
            doGetRight(args);
            break;
        case GET_RIGHTS_DOC:
            doGetRightsDoc(args);
            break;
        case GET_SERVER:
            doGetServer(args);
            break;
        case GET_XMPP_COMPONENT:
            doGetXMPPComponent(args);
            break;
        case CHECK_RIGHT:
            doCheckRight(args);
            break;
        case GET_ALL_EFFECTIVE_RIGHTS:
            doGetAllEffectiveRights(args);
            break;
        case GET_EFFECTIVE_RIGHTS:
            doGetEffectiveRights(args);
            break;
        case GET_CREATE_OBJECT_ATTRS:
            doGetCreateObjectAttrs(args);
            break;
        case GET_GRANTS:
            doGetGrants(args);
            break;
        case GRANT_RIGHT:
            doGrantRight(args);
            break;
        case REVOKE_RIGHT:
            doRevokeRight(args);
            break;
        case HELP:
            doHelp(args);
            break;
        case MODIFY_ACCOUNT:
            mProv.modifyAttrs(lookupAccount(args[1]), getMapAndCheck(args,2), true);
            break;
        case MODIFY_DATA_SOURCE:
            account = lookupAccount(args[1]);
            mProv.modifyDataSource(account, lookupDataSourceId(account, args[2]), getMapAndCheck(args,3));
            break;
        case MODIFY_IDENTITY:
            account = lookupAccount(args[1]);
            mProv.modifyIdentity(account, args[2], getMapAndCheck(args,3));
            break;
        case MODIFY_SIGNATURE:
            account = lookupAccount(args[1]);
            mProv.modifySignature(account, lookupSignatureId(account, args[2]), getMapAndCheck(args,3));
            break;
        case MODIFY_COS:
            mProv.modifyAttrs(lookupCos(args[1]), getMapAndCheck(args, 2), true);
            break;
        case MODIFY_CONFIG:
            mProv.modifyAttrs(mProv.getConfig(), getMapAndCheck(args, 1), true);
            break;
        case MODIFY_DOMAIN:
            mProv.modifyAttrs(lookupDomain(args[1]), getMapAndCheck(args, 2), true);
            break;
        case MODIFY_CONFIG_SMIME_CONFIG:
            doModifyConfigSMIMEConfig(args);
            break;
        case MODIFY_DOMAIN_SMIME_CONFIG:
            doModifyDomainSMIMEConfig(args);
            break;    
        case MODIFY_SERVER:
            mProv.modifyAttrs(lookupServer(args[1]), getMapAndCheck(args, 2), true);
            break;
        case DELETE_ACCOUNT:
            doDeleteAccount(args);
            break;
        case DELETE_COS:
            mProv.deleteCos(lookupCos(args[1]).getId());
            break;
        case DELETE_DOMAIN:
            mProv.deleteDomain(lookupDomain(args[1]).getId());
            break;
        case DELETE_IDENTITY:
            mProv.deleteIdentity(lookupAccount(args[1]), args[2]);
            break;
        case DELETE_SIGNATURE:
            account = lookupAccount(args[1]);
            mProv.deleteSignature(account, lookupSignatureId(account, args[2]));
            break;
        case DELETE_DATA_SOURCE:
            account = lookupAccount(args[1]);
            mProv.deleteDataSource(account, lookupDataSourceId(account, args[2]));
            break;
        case DELETE_SERVER:
            mProv.deleteServer(lookupServer(args[1]).getId());
            break;
        case DELETE_XMPP_COMPONENT:
            mProv.deleteXMPPComponent(lookupXMPPComponent(args[1]));
            break;
        case PUSH_FREEBUSY:
            doPushFreeBusy(args);
            break;
        case PUSH_FREEBUSY_DOMAIN:
            doPushFreeBusyForDomain(args);
            break;
        case PURGE_FREEBUSY_QUEUE:
            doPurgeFreeBusyQueue(args);
            break;
        case PURGE_ACCOUNT_CALENDAR_CACHE:
            doPurgeAccountCalendarCache(args);
            break;
        case REMOVE_ACCOUNT_ALIAS:
            Account acct = lookupAccount(args[1], false);
            mProv.removeAlias(acct, args[2]);
            // even if acct is null, we still invoke removeAlias and throw an exception afterwards.
            // this is so dangling aliases can be cleaned up as much as possible
            if (acct == null)
                throw AccountServiceException.NO_SUCH_ACCOUNT(args[1]);
            break;
        case REMOVE_ACCOUNT_LOGGER:
            alo = parseAccountLoggerOptions(args);
            if (!mCommand.checkArgsLength(alo.args)) {
                usage();
                return true;
            }
            doRemoveAccountLogger(alo);
            break;
        case REMOVE_CONFIG_SMIME_CONFIG:
            doRemoveConfigSMIMEConfig(args);
            break;
        case REMOVE_DOMAIN_SMIME_CONFIG:
            doRemoveDomainSMIMEConfig(args);
            break;
        case RENAME_ACCOUNT:
            mProv.renameAccount(lookupAccount(args[1]).getId(), args[2]);
            break;
        case RENAME_COS:
            mProv.renameCos(lookupCos(args[1]).getId(), args[2]);
            break;
        case RENAME_DOMAIN:
            doRenameDomain(args);
            break;
        case SET_ACCOUNT_COS:
            mProv.setCOS(lookupAccount(args[1]),lookupCos(args[2]));
            break;
        case SEARCH_ACCOUNTS:
            doSearchAccounts(args);
            break;
        case SEARCH_GAL:
            doSearchGal(args);
            break;
        case SYNC_GAL:
            doSyncGal(args);
            break;
        case SET_PASSWORD:
            SetPasswordResult result = mProv.setPassword(lookupAccount(args[1]), args[2]);
            if (result.hasMessage()) {
                console.println(result.getMessage());
            }
            break;
        case CHECK_PASSWORD_STRENGTH:
            mProv.checkPasswordStrength(lookupAccount(args[1]), args[2]);
            console.println("Password passed strength check.");
            break;
        case CREATE_DISTRIBUTION_LIST:
            console.println(mProv.createDistributionList(args[1], getMapAndCheck(args, 2)).getId());
            break;
        case CREATE_DISTRIBUTION_LISTS_BULK:
            doCreateDistributionListsBulk(args);
            break;
        case GET_ALL_DISTRIBUTION_LISTS:
            doGetAllDistributionLists(args);
            break;
        case GET_DISTRIBUTION_LIST:
            dumpDistributionList(lookupDistributionList(args[1]), getArgNameSet(args, 2));
            break;
        case GET_ALL_XMPP_COMPONENTS:
            doGetAllXMPPComponents();
            break;
        case MODIFY_DISTRIBUTION_LIST:
            mProv.modifyAttrs(lookupDistributionList(args[1]), getMapAndCheck(args, 2), true);
            break;
        case DELETE_DISTRIBUTION_LIST:
            mProv.deleteDistributionList(lookupDistributionList(args[1]).getId());
            break;
        case ADD_DISTRIBUTION_LIST_MEMBER:
            members = new String[args.length - 2];
            System.arraycopy(args, 2, members, 0, args.length - 2);
            mProv.addMembers(lookupDistributionList(args[1]), members);
            break;
        case REMOVE_DISTRIBUTION_LIST_MEMBER:
            members = new String[args.length - 2];
            System.arraycopy(args, 2, members, 0, args.length - 2);
            mProv.removeMembers(lookupDistributionList(args[1]), members);
            break;
        case CREATE_BULK_ACCOUNTS:
            doCreateAccountsBulk(args);
            break;
        case ADD_DISTRIBUTION_LIST_ALIAS:
            mProv.addAlias(lookupDistributionList(args[1]), args[2]);
            break;
        case REMOVE_DISTRIBUTION_LIST_ALIAS:
            DistributionList dl = lookupDistributionList(args[1], false);
            // Even if dl is null, we still invoke removeAlias.
            // This is so dangling aliases can be cleaned up as much as possible.
            // If dl is null, the NO_SUCH_DISTRIBUTION_LIST thrown by SOAP will contain
            // null as the dl identity, because SoapProvisioning sends no id to the server.
            // In this case, we catch the NO_SUCH_DISTRIBUTION_LIST and throw another one
            // with the named/id entered on the comand line.
            try {
                mProv.removeAlias(dl, args[2]);
            } catch (ServiceException e) {
                if (!(dl == null && AccountServiceException.NO_SUCH_DISTRIBUTION_LIST.equals(e.getCode())))
                    throw e;
                // else eat the exception, we will throw below
            }

            if (dl == null)
                throw AccountServiceException.NO_SUCH_DISTRIBUTION_LIST(args[1]);

            break;
        case RENAME_DISTRIBUTION_LIST:
            mProv.renameDistributionList(lookupDistributionList(args[1]).getId(), args[2]);
            break;
        case CREATE_CALENDAR_RESOURCE:
            console.println(mProv.createCalendarResource(args[1], args[2].equals("")? null : args[2], getMapAndCheck(args, 3)).getId());
            break;
        case DELETE_CALENDAR_RESOURCE:
            mProv.deleteCalendarResource(lookupCalendarResource(args[1]).getId());
            break;
        case MODIFY_CALENDAR_RESOURCE:
            mProv.modifyAttrs(lookupCalendarResource(args[1]), getMapAndCheck(args, 2), true);
            break;
        case RENAME_CALENDAR_RESOURCE:
            mProv.renameCalendarResource(lookupCalendarResource(args[1]).getId(), args[2]);
            break;
        case GET_CALENDAR_RESOURCE:
            dumpCalendarResource(lookupCalendarResource(args[1]), true, getArgNameSet(args, 2));
            break;
        case GET_ALL_CALENDAR_RESOURCES:
            doGetAllCalendarResources(args);
            break;
        case SEARCH_CALENDAR_RESOURCES:
            doSearchCalendarResources(args);
            break;
        case PUBLISH_DISTRIBUTION_LIST_SHARE_INFO:
            doPublishDistributionListShareInfo(args);
            break;
        case GET_PUBLISHED_DISTRIBUTION_LIST_SHARE_INFO:
            doGetPublishedDistributionListShareInfo(args);
            break;
        case GET_SHARE_INFO:
            doGetShareInfo(args);
            break;
        case GET_SPNEGO_DOMAIN:
            doGetSpnegoDomain();
            break;
        case INIT_NOTEBOOK:
            initNotebook(args);
            break;
        case INIT_DOMAIN_NOTEBOOK:
            initDomainNotebook(args);
            break;
        case IMPORT_NOTEBOOK:
            importNotebook(args);
            break;
        case UPDATE_TEMPLATES:
            updateTemplates(args);
            break;
        case GET_QUOTA_USAGE:
            doGetQuotaUsage(args);
            break;
        case GET_MAILBOX_INFO:
            doGetMailboxInfo(args);
            break;
        case REINDEX_MAILBOX:
            doReIndexMailbox(args);
            break;
        case RECALCULATE_MAILBOX_COUNTS:
            doRecalculateMailboxCounts(args);
            break;
        case SELECT_MAILBOX:
            if (!(mProv instanceof SoapProvisioning))
                throwSoapOnly();
             ZMailboxUtil util = new ZMailboxUtil();
             util.setVerbose(mVerbose);
             util.setDebug(mDebug != SoapDebugLevel.none);
             boolean smInteractive = mInteractive && args.length < 3;
             util.setInteractive(smInteractive);
             util.selectMailbox(args[1], (SoapProvisioning) mProv);
             if (smInteractive) {
                 util.interactive(mReader);
             } else if (args.length > 2){
                 String newArgs[] = new String[args.length-2];
                 System.arraycopy(args, 2, newArgs, 0, newArgs.length);
                 util.execute(newArgs);
             } else {
                 throw ZClientException.CLIENT_ERROR("command only valid in interactive mode or with arguments", null);
             }
            break;
        case GET_ALL_MTA_AUTH_URLS:
            doGetAllMtaAuthURLs(args);
            break;
        case GET_ALL_REVERSE_PROXY_URLS:
            doGetAllReverseProxyURLs(args);
            break;
        case GET_ALL_REVERSE_PROXY_BACKENDS:
            doGetAllReverseProxyBackends(args);
            break;
        case GET_ALL_REVERSE_PROXY_DOMAINS:
            doGetAllReverseProxyDomains(args);
            break;
        case GET_ALL_MEMCACHED_SERVERS:
            doGetAllMemcachedServers(args);
            break;
        case RELOAD_MEMCACHED_CLIENT_CONFIG:
            doReloadMemcachedClientConfig(args);
            break;
        case GET_MEMCACHED_CLIENT_CONFIG:
            doGetMemcachedClientConfig(args);
            break;
        case GET_AUTH_TOKEN_INFO:
            doGetAuthTokenInfo(args);
            break;
        case SOAP:
            // HACK FOR NOW
            SoapProvisioning sp = new SoapProvisioning();
            sp.soapSetURI("https://localhost:" + mPort + AdminConstants.ADMIN_SERVICE_URI);
            sp.soapZimbraAdminAuthenticate();
            mProv = sp;
            break;
        case LDAP:
            // HACK FOR NOW
            mProv = Provisioning.getInstance();
            break;
        default:
            return false;
        }
        return true;
    }

    private void doGetDomain(String[] args) throws ServiceException {
        boolean applyDefault = true;

        int i = 1;
        while (i < args.length) {
            String arg = args[i];
            if (arg.equals("-e"))
                applyDefault = false;
            else
                break;
            i++;
        }
        if (i >= args.length) {
            usage();
            return;
        }
        dumpDomain(lookupDomain(args[i], mProv, applyDefault), applyDefault, getArgNameSet(args, i+1));
    }

    private void doGetDomainInfo(String[] args) throws ServiceException {
        if (!(mProv instanceof SoapProvisioning))
            throwSoapOnly();
        SoapProvisioning sp = (SoapProvisioning) mProv;
        DomainBy by = DomainBy.fromString(args[1]);
        String key = args[2];
        Domain domain = sp.getDomainInfo(by, key);
        if (domain == null)
            throw AccountServiceException.NO_SUCH_DOMAIN(key);
        else
            dumpDomain(domain, getArgNameSet(args, 3));
    }

    private void doRenameDomain(String[] args) throws ServiceException {
        if (!(mProv instanceof LdapProvisioning))
            throwLdapOnly();
        
        // bug 56768
        ZimbraLdapContext.forceMasterURL();
        
        // should we disable interactive mode or use a separate CLI (e.g. zmrenamedomain) 
        // for renameDomain?  After renameDomain, all subsequent LDAP accesses will go to
        // the master.
        
        LdapProvisioning lp = (LdapProvisioning) mProv;
        Domain domain = lookupDomain(args[1]);
        lp.renameDomain(domain.getId(), args[2]);
        printOutput("domain " + args[1] + " renamed to " + args[2]);
    }

    private void doGetQuotaUsage(String[] args) throws ServiceException {
        if (!(mProv instanceof SoapProvisioning))
            throwSoapOnly();
        SoapProvisioning sp = (SoapProvisioning) mProv;
        List<QuotaUsage> result = sp.getQuotaUsage(args[1]);
        for (QuotaUsage u : result) {
            console.printf("%s %d %d\n", u.getName(), u.getLimit(), u.getUsed());
        }
    }

    private void doGetMailboxInfo(String[] args) throws ServiceException {
        if (!(mProv instanceof SoapProvisioning))
            throwSoapOnly();
        SoapProvisioning sp = (SoapProvisioning) mProv;
        Account acct = lookupAccount(args[1]);
        MailboxInfo info = sp.getMailbox(acct);
        console.printf("mailboxId: %s\nquotaUsed: %d\n", info.getMailboxId(), info.getUsed());
    }

    private void doReIndexMailbox(String[] args) throws ServiceException {
        if (!(mProv instanceof SoapProvisioning))
            throwSoapOnly();
        SoapProvisioning sp = (SoapProvisioning) mProv;
        Account acct = lookupAccount(args[1]);
        ReIndexBy by = null;
        String[] values = null;
        if (args.length > 3) {
            try {
                by = ReIndexBy.valueOf(args[3]);
            } catch (IllegalArgumentException e) {
                throw ServiceException.INVALID_REQUEST("invalid reindex-by", null);
            }
            if (args.length > 4) {
                values = new String[args.length - 4];
                System.arraycopy(args, 4, values, 0, args.length - 4);
            } else
                throw ServiceException.INVALID_REQUEST("missing reindex-by values", null);
        }
        ReIndexInfo info = sp.reIndex(acct, args[2], by, values);
        ReIndexInfo.Progress progress = info.getProgress();
        console.printf("status: %s\n", info.getStatus());
        if (progress != null)
            console.printf("progress: numSucceeded=%d, numFailed=%d, numRemaining=%d\n",
                              progress.getNumSucceeded(), progress.getNumFailed(), progress.getNumRemaining());
    }

    private void doRecalculateMailboxCounts(String[] args) throws ServiceException {
        if (!(mProv instanceof SoapProvisioning))
            throwSoapOnly();
        SoapProvisioning sp = (SoapProvisioning) mProv;
        Account acct = lookupAccount(args[1]);
        long quotaUsed = sp.recalculateMailboxCounts(acct);
        console.printf("account: " + acct.getName() + "\nquotaUsed: " + quotaUsed + "\n");
    }

    private class AccountLoggerOptions {
        String server;
        String[] args;
    }

    /**
     * Handles an optional <tt>-s</tt> or <tt>--server</tt> argument that may be passed
     * to the logging commands.  Returns an <tt>AccountLogggerOptions</tt> object that
     * contains all arguments except the server option and value.
     */
    private AccountLoggerOptions parseAccountLoggerOptions(String[] args)
    throws ServiceException {
        AccountLoggerOptions alo = new AccountLoggerOptions();
        if (args.length > 1 && (args[1].equals("-s") || args[1].equals("--server"))) {
            if (args.length == 2) {
                throw ServiceException.FAILURE("Server name not specified.", null);
            }
            alo.server = args[2];

            int numArgs = args.length - 2;
            alo.args = new String[numArgs];
            alo.args[0] = args[0];
            for (int i = 1; i < numArgs; i++) {
                alo.args[i] = args[i + 2];
            }
        } else {
            alo.args = args;
        }
        return alo;
    }

    private void doAddAccountLogger(AccountLoggerOptions alo) throws ServiceException {
        if (!(mProv instanceof SoapProvisioning))
            throwSoapOnly();
        SoapProvisioning sp = (SoapProvisioning) mProv;
        Account acct = lookupAccount(alo.args[1]);
        sp.addAccountLogger(acct, alo.args[2], alo.args[3], alo.server);
    }

    private void doGetAccountLoggers(AccountLoggerOptions alo) throws ServiceException {
        if (!(mProv instanceof SoapProvisioning))
            throwSoapOnly();
        SoapProvisioning sp = (SoapProvisioning) mProv;
        Account acct = lookupAccount(alo.args[1]);
        for (AccountLogger accountLogger : sp.getAccountLoggers(acct, alo.server)) {
            console.printf("%s=%s\n", accountLogger.getCategory(), accountLogger.getLevel());
        }
    }

    private void doGetAllAccountLoggers(AccountLoggerOptions alo) throws ServiceException {
        if (!(mProv instanceof SoapProvisioning))
            throwSoapOnly();
        SoapProvisioning sp = (SoapProvisioning) mProv;

        Map<String, List<AccountLogger>> allLoggers = sp.getAllAccountLoggers(alo.server);
        for (String accountName : allLoggers.keySet()) {
            console.printf("# name %s\n", accountName);
            for (AccountLogger logger : allLoggers.get(accountName)) {
                console.printf("%s=%s\n", logger.getCategory(), logger.getLevel());
            }
        }
    }

    private void doRemoveAccountLogger(AccountLoggerOptions alo) throws ServiceException {
        if (!(mProv instanceof SoapProvisioning))
            throwSoapOnly();
        SoapProvisioning sp = (SoapProvisioning) mProv;
        Account acct = null;
        String category = null;
        if (alo.args.length == 2) {
            // Hack: determine if it's an account or category, based on the name.
            String arg = alo.args[1];
            if (arg.startsWith("zimbra.") || arg.startsWith("com.zimbra")) {
                category = arg;
            } else {
                acct = lookupAccount(alo.args[1]);
            }
        }
        if (alo.args.length == 3) {
            acct = lookupAccount(alo.args[1]);
            category = alo.args[2];
        }
        sp.removeAccountLoggers(acct, category, alo.server);
    }

    private void doCreateAccountsBulk(String[] args) throws ServiceException {
        if (args.length < 3) {
            usage();
        } else {
            String domain = args[1];
            String password = "test123";
            String nameMask = args[2];
            int numAccounts = Integer.parseInt(args[3]);
            for(int ix=0; ix < numAccounts; ix++) {
                String name = nameMask + Integer.toString(ix) + "@" + domain;
                Map<String, Object> attrs = new HashMap<String, Object>();
                String displayName = nameMask + " N. " + Integer.toString(ix);
                StringUtil.addToMultiMap(attrs, "displayName", displayName);
                Account account = mProv.createAccount(name, password, attrs);
                console.println(account.getId());
           }
        }
    }

    private Domain doCreateAliasDomain(String aliasDomain, String localDoamin, Map<String, Object> attrs) throws ServiceException {
        Domain local = lookupDomain(localDoamin);

        String localType = local.getAttr(Provisioning.A_zimbraDomainType);
        if (!LdapProvisioning.DOMAIN_TYPE_LOCAL.equals(localType))
            throw ServiceException.INVALID_REQUEST("target domain must be a local domain", null);

        attrs.put(Provisioning.A_zimbraDomainType, LdapProvisioning.DOMAIN_TYPE_ALIAS);
        attrs.put(Provisioning.A_zimbraDomainAliasTargetId, local.getId());
        return mProv.createDomain(aliasDomain, attrs);
    }

    private void doGetAccount(String[] args) throws ServiceException {
        boolean applyDefault = true;
        int acctPos = 1;

        if (args[1].equals("-e")) {
            if (args.length > 1) {
                applyDefault = false;
                acctPos = 2;
            } else {
                usage();
                return;
            }
        }

        dumpAccount(lookupAccount(args[acctPos], true, applyDefault), applyDefault, getArgNameSet(args, acctPos+1));
    }

    private void doGetAccountMembership(String[] args) throws ServiceException {
        String key = null;
        boolean idsOnly = false;
        if (args.length > 2) {
            idsOnly = args[1].equals("-i");
            key = args[2];
        } else {
            key = args[1];
        }
        Account account = lookupAccount(key);
        if (idsOnly) {
            Set<String> lists = mProv.getDistributionLists(account);
            for (String id: lists) {
                console.println(id);
            }
        } else {
            HashMap<String,String> via = new HashMap<String, String>();
            List<DistributionList> lists = mProv.getDistributionLists(account, false, via);
            for (DistributionList dl: lists) {
                String viaDl = via.get(dl.getName());
                if (viaDl != null) console.println(dl.getName()+" (via "+viaDl+")");
                else console.println(dl.getName());
            }
        }
    }

    /*
     * + => true
     * - => false
     */
    private boolean parsePlusMinus(String s) throws ServiceException {
        if (s.equals("-"))
            return false;
        else if (s.equals("+"))
            return true;
        else
            throw ServiceException.INVALID_REQUEST("invalid arg for the add/remove", null);
    }

    private void doPublishDistributionListShareInfo(String[] args) throws ServiceException {
        if (!(mProv instanceof SoapProvisioning))
            throwSoapOnly();

        boolean isAdd = parsePlusMinus(args[1]);
        String key = args[2];
        DistributionList dl = lookupDistributionList(key);

        ShareInfoArgs siArgs = parsePublishShareInfo(isAdd, args);
        mProv.publishShareInfo(dl, siArgs.mAction, siArgs.mOwnerAcct, siArgs.mFolderPathOrId);
    }

    private void doGetPublishedDistributionListShareInfo(String[] args) throws ServiceException {
        if (!(mProv instanceof SoapProvisioning))
            throwSoapOnly();

        String key = args[1];
        DistributionList dl = lookupDistributionList(key);

        Account owner = null;
        if (args.length == 3)
            owner = lookupAccount(args[2]);

        ShareInfoVisitor.printHeadings();
        mProv.getPublishedShareInfo(dl, owner, new ShareInfoVisitor());
    }

    private static class ShareInfoArgs {

        PublishShareInfoAction mAction;
        Account mOwnerAcct;
        String mFolderPathOrId;

        ShareInfoArgs(PublishShareInfoAction action,
                Account ownerAcct, String folderPathOrId) {
            mAction = action;
            mOwnerAcct = ownerAcct;
            mFolderPathOrId = folderPathOrId;
        }
    }

    private ShareInfoArgs parsePublishShareInfo(boolean isAdd, String[] args) throws ServiceException {
        int idx = 3;
        String owner = args[idx++];
        Account ownerAcct = lookupAccount(owner);

        String folderPathOrId = null;
        if (args.length == 5)
            folderPathOrId = args[idx++];

        PublishShareInfoAction action;
        // String desc;  not supported for now

        if (isAdd) {
            action = PublishShareInfoAction.add;
            // desc = args[idx++];
        } else {
            action = PublishShareInfoAction.remove;
            // desc = null;
        }

        return new ShareInfoArgs(action, ownerAcct, folderPathOrId);
    }

    private static class ShareInfoVisitor implements PublishedShareInfoVisitor {

        private static final String mFormat =
            "%-36.36s %-15.15s %-15.15s %-5.5s %-20.20s %-10.10s %-10.10s %-5.5s %-5.5s %-36.36s %-15.15s %-15.15s\n";

        private static void printHeadings() {
            console.printf(mFormat,
                              "owner id",
                              "owner email",
                              "owner display",
                              "fid",
                              "folder path",
                              "view",
                              "rights",
                              "mid",
                              "gt",
                              "grantee id",
                              "grantee name",
                              "grantee display");

            console.printf(mFormat,
                              "------------------------------------",      // owner id
                              "---------------",                           // owner email
                              "---------------",                           // owner display
                              "-----",                                     // folder id
                              "--------------------",                      // folder path
                              "----------",                                // default view
                              "----------",                                // rights
                              "-----",                                     // mountpoint id if mounted
                              "-----",                                     // grantee type
                              "------------------------------------",      // grantee id
                              "---------------",                           // grantee name
                              "---------------");                          // grantee display
        }

        @Override
        public void visit(ShareInfoData shareInfoData) throws ServiceException {
            console.printf(mFormat,
                    shareInfoData.getOwnerAcctId(),
                    shareInfoData.getOwnerAcctEmail(),
                    shareInfoData.getOwnerAcctDisplayName(),
                    String.valueOf(shareInfoData.getFolderId()),
                    shareInfoData.getFolderPath(),
                    shareInfoData.getFolderDefaultView(),
                    shareInfoData.getRights(),
                    shareInfoData.getMountpointId_zmprov_only(),
                    shareInfoData.getGranteeType(),
                    shareInfoData.getGranteeId(),
                    shareInfoData.getGranteeName(),
                    shareInfoData.getGranteeDisplayName());
        }
    };

    private void doGetShareInfo(String[] args) throws ServiceException {
        if (!(mProv instanceof SoapProvisioning))
            throwSoapOnly();

        Account owner = lookupAccount(args[1]);

        ShareInfoVisitor.printHeadings();
        mProv.getShareInfo(owner, new ShareInfoVisitor());
    }

    private void doGetSpnegoDomain() throws ServiceException{
        Config config = mProv.getConfig();
        String spnegoAuthRealm = config.getSpnegoAuthRealm();
        if (spnegoAuthRealm != null) {
            Domain domain = mProv.get(DomainBy.krb5Realm, spnegoAuthRealm);
            if (domain != null) {
                console.println(domain.getName());
            }
        }
    }

    private void doDeleteAccount(String[] args) throws ServiceException {
        String key = args[1];
        Account acct = lookupAccount(key);
        if (key.equalsIgnoreCase(acct.getId()) ||
                key.equalsIgnoreCase(acct.getName()) ||
                acct.getName().equalsIgnoreCase(key+"@"+acct.getDomainName())) {
            mProv.deleteAccount(acct.getId());
        } else {
            throw ServiceException.INVALID_REQUEST("argument to deleteAccount must be an account id or the account's primary name", null);
        }

    }

    private void doGetAccountIdentities(String[] args) throws ServiceException {
        Account account = lookupAccount(args[1]);
        Set<String> argNameSet = getArgNameSet(args, 2);
        for (Identity identity : mProv.getAllIdentities(account)) {
            dumpIdentity(identity, argNameSet);
        }
    }

    private void doGetAccountSignatures(String[] args) throws ServiceException {
        Account account = lookupAccount(args[1]);
        Set<String> argNameSet = getArgNameSet(args, 2);
        for (Signature signature : mProv.getAllSignatures(account)) {
            dumpSignature(signature, argNameSet);
        }
    }

    private void dumpDataSource(DataSource dataSource, Set<String> argNameSet) throws ServiceException {
        console.println("# name "+dataSource.getName());
        console.println("# type "+dataSource.getType());
        Map<String, Object> attrs = dataSource.getAttrs();
        dumpAttrs(attrs, argNameSet);
        console.println();
    }

    private void doGetAccountDataSources(String[] args) throws ServiceException {
        Account account = lookupAccount(args[1]);
        Set<String> attrNameSet = getArgNameSet(args, 2);
        for (DataSource dataSource : mProv.getAllDataSources(account)) {
            dumpDataSource(dataSource, attrNameSet);
        }
    }

    private void doGetDistributionListMembership(String[] args) throws ServiceException {
        String key = args[1];
        DistributionList dist = lookupDistributionList(key);
        HashMap<String,String> via = new HashMap<String, String>();
        List<DistributionList> lists = mProv.getDistributionLists(dist, false, via);
        for (DistributionList dl: lists) {
            String viaDl = via.get(dl.getName());
            if (viaDl != null) console.println(dl.getName()+" (via "+viaDl+")");
            else console.println(dl.getName());
        }
    }

    private void doGetConfig(String[] args) throws ServiceException {
        String key = args[1];
        Set<String> needAttr = new HashSet<String>();
        needAttr.add(key);
        dumpAttrs(mProv.getConfig(key).getAttrs(), needAttr);
    }

    /*
     * prov is always LdapProvisioning here
     */
    private void doGetAllAccounts(LdapProvisioning ldapProv, Domain domain, Server server, final boolean verbose, final boolean applyDefault, final Set<String> attrNames) throws ServiceException {
        NamedEntry.Visitor visitor = new NamedEntry.Visitor() {
            @Override
            public void visit(com.zimbra.cs.account.NamedEntry entry) throws ServiceException {
                if (verbose)
                    dumpAccount((Account) entry, applyDefault, attrNames);
                else
                    console.println(entry.getName());
            }
        };

        if (verbose && applyDefault)
            ldapProv.getAllAccounts(domain, server, visitor);
        else
            ldapProv.getAllAccountsNoDefaults(domain, server, visitor);
    }

    private void doGetAllAccounts(String[] args) throws ServiceException {

        if (!(mProv instanceof LdapProvisioning))
            throwLdapOnly();

        LdapProvisioning ldapProv = (LdapProvisioning)mProv;

        boolean verbose = false;
        boolean applyDefault = true;
        String d = null;
        String s = null;

        int i = 1;
        while (i < args.length) {
            String arg = args[i];
            if (arg.equals("-v"))
                verbose = true;
            else if (arg.equals("-e"))
                applyDefault = false;
            else if (arg.equals("-s")) {
                i++;
                if (i < args.length) {
                    if (s == null)
                        s = args[i];
                    else {
                        console.println("invalid arg: " + args[i] + ", already specified -s with " + s);
                        usage();
                        return;
                    }
                } else {
                    usage();
                    return;
                }
            } else {
                if (d == null)
                    d = arg;
                else {
                    console.println("invalid arg: " + arg + ", already specified domain: " + d);
                    usage();
                    return;
                }
            }
            i++;
        }

        if (!applyDefault && !verbose) {
            console.println(ERR_INVALID_ARG_EV);
            usage();
            return;
        }

        Server server = null;
        if (s != null)
            server = lookupServer(s);

        if (d == null) {
            List domains = ldapProv.getAllDomains();
            for (Iterator dit=domains.iterator(); dit.hasNext(); ) {
                Domain domain = (Domain) dit.next();
                doGetAllAccounts(ldapProv, domain, server, verbose, applyDefault, null);
            }
        } else {
            Domain domain = lookupDomain(d, ldapProv);
            doGetAllAccounts(ldapProv, domain, server, verbose, applyDefault, null);
        }
    }

    private void doSearchAccounts(String[] args) throws ServiceException, ArgException {
        boolean verbose = false;
        int i = 1;

        if (args[i].equals("-v")) {
            verbose = true;
            i++;
            if (args.length < i-1) {
                usage();
                return;

            }
        }

        if (args.length < i+1) {
            usage();
            return;
        }

        String query = args[i];
        query = LdapEntrySearchFilter.toLdapIDNFilter(query);


        Map attrs = getMap(args, i+1);
//        int iPageNum = 0;
//        int iPerPage = 0;

        String limitStr = (String) attrs.get("limit");
        int limit = limitStr == null ? Integer.MAX_VALUE : Integer.parseInt(limitStr);

        String offsetStr = (String) attrs.get("offset");
        int offset = offsetStr == null ? 0 : Integer.parseInt(offsetStr);

        String sortBy  = (String)attrs.get("sortBy");
        String sortAscending  = (String) attrs.get("sortAscending");
        boolean isSortAscending = (sortAscending != null) ? "1".equalsIgnoreCase(sortAscending) : true;

        /*
        String attrsStr = (String)attrs.get("attrs");
        String[] attrsToGet = attrsStr == null ? null : attrsStr.split(",");
        */
        String[] attrsToGet = null;

        String typesStr = (String) attrs.get("types");
        int flags = Provisioning.SA_ACCOUNT_FLAG|Provisioning.SA_ALIAS_FLAG|Provisioning.SA_DISTRIBUTION_LIST_FLAG|Provisioning.SA_CALENDAR_RESOURCE_FLAG;

        if (typesStr != null)
            flags = Provisioning.searchAccountStringToMask(typesStr);

        String domainStr = (String)attrs.get("domain");
        List accounts;
        Provisioning prov = Provisioning.getInstance();
        if (domainStr != null) {
            Domain d = lookupDomain(domainStr, prov);
            accounts = prov.searchAccounts(d, query, attrsToGet, sortBy, isSortAscending, flags);
        } else {
            //accounts = mProvisioning.searchAccounts(query, attrsToGet, sortBy, isSortAscending, Provisioning.SA_ACCOUNT_FLAG);
            accounts = prov.searchAccounts(query, attrsToGet, sortBy, isSortAscending, flags);
        }

        //ArrayList accounts = (ArrayList) mProvisioning.searchAccounts(query);
        for (int j=offset; j < offset+limit && j < accounts.size(); j++) {
            NamedEntry account = (NamedEntry) accounts.get(j);
            if (verbose) {
                if (account instanceof Account)
                    dumpAccount((Account)account, true, null);
                else if (account instanceof Alias)
                    dumpAlias((Alias)account);
                else if (account instanceof DistributionList)
                    dumpDistributionList((DistributionList)account, null);
                else if (account instanceof Domain)
                    dumpDomain((Domain)account, null);
            } else {
                console.println(account.getName());
            }
        }
    }

    private void doSearchGal(String[] args) throws ServiceException, ArgException {
        boolean verbose = false;
        int i = 1;

        if (args.length < i+1) {
            usage();
            return;
        }

        if (args[i].equals("-v")) {
            verbose = true;
            i++;
            if (args.length < i-1) {
                usage();
                return;
            }
        }

        if (args.length < i+2) {
            usage();
            return;
        }

        String domain = args[i];
        String query = args[i+1];

        Map attrs = getMap(args, i+2);

        String limitStr = (String) attrs.get("limit");
        int limit = limitStr == null ? Integer.MAX_VALUE : Integer.parseInt(limitStr);

        String offsetStr = (String) attrs.get("offset");
        int offset = offsetStr == null ? 0 : Integer.parseInt(offsetStr);

        String sortBy  = (String)attrs.get("sortBy");

        Domain d = lookupDomain(domain);

        SearchGalResult result = (mProv instanceof SoapProvisioning) ?
                ((SoapProvisioning)mProv).searchGal(d, query, Provisioning.GalSearchType.all, null, limit, offset, sortBy) :
                mProv.searchGal(d, query, Provisioning.GalSearchType.all, null);
        for (GalContact contact : result.getMatches())
            dumpContact(contact);
    }

    private void doAutoCompleteGal(String[] args) throws ServiceException {

        String domain = args[1];
        String query = args[2];

        Domain d = lookupDomain(domain);

        SearchGalResult result = mProv.autoCompleteGal(d, query, Provisioning.GalSearchType.all, 100);
        for (GalContact contact : result.getMatches())
            dumpContact(contact);
    }

    private void doCountAccount(String[] args) throws ServiceException {
        String domain = args[1];
        Domain d = lookupDomain(domain);

        CountAccountResult result = mProv.countAccount(d);
        String formatHeading = "%-20s %-40s %s\n";
        String format = "%-20s %-40s %d\n";
        console.printf(formatHeading, "cos name", "cos id", "# of accounts");
        console.printf(formatHeading, "--------------------", "----------------------------------------", "--------------------");
        for (CountAccountResult.CountAccountByCos c : result.getCountAccountByCos()) {
            console.printf(format, c.getCosName(), c.getCosId(), c.getCount());
        }

        console.println();
    }

    private void doCountObjects(String[] args) throws ServiceException {

        // only used by installer for now.  LDAP only is good for now, add soap if needed.
        if (!(mProv instanceof LdapProvisioning))
            throwLdapOnly();

        CountObjectsType type = Provisioning.CountObjectsType.fromString(args[1]);

        Domain domain = null;
        int idx = 2;
        while (args.length > idx) {
            String arg = args[idx];

            if (arg.equals("-d")) {
                if (domain != null)
                    throw ServiceException.INVALID_REQUEST("domain is already specified as:" + domain.getName(), null);
                idx++;
                if (args.length <= idx) {
                    usage();
                    throw ServiceException.INVALID_REQUEST("expecting domain, not enough args", null);
                }

                domain = lookupDomain(args[idx]);
            } else {
                usage();
                return;
            }

            idx++;

        }

        long result = mProv.countObjects(type, domain);
        console.println(result);
    }

    private void doSyncGal(String[] args) throws ServiceException {
        String domain = args[1];
        String token = args.length  == 3 ? args[2] : "";

        Domain d = lookupDomain(domain);

        SearchGalResult result = mProv.searchGal(d, "", Provisioning.GalSearchType.all, token);
        if (result.getToken() != null)
            console.println("# token = "+result.getToken() + "\n");
        for (GalContact contact : result.getMatches())
            dumpContact(contact);
    }

    private void doGetAllAdminAccounts(String[] args) throws ServiceException {
        boolean verbose = false;
        boolean applyDefault = true;

        int i = 1;
        while (i < args.length) {
            String arg = args[i];
            if (arg.equals("-v"))
                verbose = true;
            else if (arg.equals("-e"))
                applyDefault = false;
            else
                break;
            i++;
        }

        if (!applyDefault && !verbose) {
            console.println(ERR_INVALID_ARG_EV);
            usage();
            return;
        }

        List accounts;
        if (mProv instanceof SoapProvisioning) {
            SoapProvisioning soapProv = (SoapProvisioning)mProv;
            accounts = soapProv.getAllAdminAccounts(applyDefault);
        } else
            accounts = mProv.getAllAdminAccounts();

        Set<String> attrNames = getArgNameSet(args, i);
        for (Iterator it=accounts.iterator(); it.hasNext(); ) {
            Account account = (Account) it.next();
            if (verbose)
                dumpAccount(account, applyDefault, attrNames);
            else
                console.println(account.getName());
        }
    }

    private void doGetAllCos(String[] args) throws ServiceException {
        boolean verbose = args.length > 1 && args[1].equals("-v");
        Set<String> attrNames = getArgNameSet(args, verbose ? 2 : 1);
        List allcos = mProv.getAllCos();
        for (Iterator it=allcos.iterator(); it.hasNext(); ) {
            Cos cos = (Cos) it.next();
            if (verbose)
                dumpCos(cos, attrNames);
            else
                console.println(cos.getName());
        }
    }

    private void dumpCos(Cos cos, Set<String> attrNames) throws ServiceException {
        console.println("# name "+cos.getName());
        Map<String, Object> attrs = cos.getAttrs();
        dumpAttrs(attrs, attrNames);
        console.println();
    }

    private void doGetAllDomains(String[] args) throws ServiceException {
        boolean verbose = false;
        boolean applyDefault = true;

        int i = 1;
        while (i < args.length) {
            String arg = args[i];
            if (arg.equals("-v"))
                verbose = true;
            else if (arg.equals("-e"))
                applyDefault = false;
            else
                break;
            i++;
        }

        if (!applyDefault && !verbose) {
            console.println(ERR_INVALID_ARG_EV);
            usage();
            return;
        }

        Set<String> attrNames = getArgNameSet(args, i);

        List domains;
        if (mProv instanceof SoapProvisioning) {
            SoapProvisioning soapProv = (SoapProvisioning)mProv;
            domains = soapProv.getAllDomains(applyDefault);
        } else
            domains = mProv.getAllDomains();

        for (Iterator it=domains.iterator(); it.hasNext(); ) {
            Domain domain = (Domain) it.next();
            if (verbose)
                dumpDomain(domain, attrNames);
            else
                console.println(domain.getName());
        }
    }

    private void dumpDomain(Domain domain, Set<String> attrNames) throws ServiceException {
        dumpDomain(domain, true, attrNames);
    }

    private void dumpDomain(Domain domain, boolean expandConfig, Set<String> attrNames) throws ServiceException {
        console.println("# name "+domain.getName());
        Map<String, Object> attrs = domain.getAttrs(expandConfig);
        dumpAttrs(attrs, attrNames);
        console.println();
    }

    private void dumpDistributionList(DistributionList dl, Set<String> attrNames) throws ServiceException {
        String[] members = dl.getAllMembers();
        int count = members == null ? 0 : members.length;
        console.println("# distributionList " + dl.getName() + " memberCount=" + count);
        Map<String, Object> attrs = dl.getAttrs();
        dumpAttrs(attrs, attrNames);
        console.println();
    }

    private void dumpAlias(Alias alias) throws ServiceException {
        console.println("# alias " + alias.getName());
        Map<String, Object> attrs = alias.getAttrs();
        dumpAttrs(attrs, null);
    }

    private void doGetRight(String[] args) throws ServiceException, ArgException  {
        boolean expandComboRight = false;
        String right = args[1];
        if (args.length > 2) {
            if (args[2].equals("-e")) {
                expandComboRight = true;
            } else {
                throw new ArgException("invalid arguments");
            }
        }
        dumpRight(lookupRight(right), expandComboRight);
    }
    
    private void doGetAllRights(String[] args) throws ServiceException, ArgException  {
        boolean verbose = false;
        String targetType = null;
        String rightClass = null;    
        
        int i = 1;
        while (i < args.length) {
            String arg = args[i];
            if (arg.equals("-v"))
                verbose = true;
            else if (arg.equals("-t")) {
                i++;
                if (i == args.length)
                    throw new ArgException("not enough arguments");
                else
                    targetType = args[i];
            } else if (arg.equals("-c")) {
                i++;
                if (i == args.length)
                    throw new ArgException("not enough arguments");
                else
                    rightClass = args[i];
            } else {
                throw new ArgException("invalid arg: " + arg);
            }
            i++;
        }

        List<Right> allRights = mProv.getAllRights(targetType, false, rightClass);
        for (Right right : allRights) {
            if (verbose)
                dumpRight(right);
            else
                console.println(right.getName());
        }
    }
    
    private void dumpRight(Right right) {
        dumpRight(right, true);
    }
    
    private void dumpRight(Right right, boolean expandComboRight) {
        String tab = "    ";
        String indent = tab;
        String indent2 = indent + indent;

        console.println();
        console.println("------------------------------");

        console.println(right.getName());
        console.println(indent + "   description: " + right.getDesc());
        console.println(indent + "    right type: " + right.getRightType().name());

        String targetType = right.getTargetTypeStr();
        console.println(indent + "target type(s): " + (targetType==null?"":targetType));

        console.println(indent + "   right class: " + right.getRightClass().name());
        
        if (right.isAttrRight()) {
            AttrRight attrRight = (AttrRight)right;
            console.println();
            console.println(indent + "attributes:");
            if (attrRight.allAttrs()) {
                console.println(indent2 + "all attributes");
            } else {
                for (String attrName : attrRight.getAttrs())
                    console.println(indent2 + attrName);
            }
        } else if (right.isComboRight()) {
            ComboRight comboRight = (ComboRight)right;
            console.println();
            console.println(indent + "rights:");
            dumpComboRight(comboRight, expandComboRight, indent, new HashSet<String>());
        }
        console.println();
        
        Help help = right.getHelp();
        if (help != null) {
            console.println(help.getDesc());
            List<String> helpItems = help.getItems();
            for (String helpItem : helpItems) {
                // console.println(FileGenUtil.wrapComments(helpItem, 70, prefix) + "\n");
                console.println("- " + helpItem.trim());
                console.println();
            }
        }
        console.println();
    }
    
    private void dumpComboRight(ComboRight comboRight, boolean expandComboRight, String indent, Set<String> seen) {
        // safety check, should not happen, 
        // detect circular combo rights
        if (seen.contains(comboRight.getName())) {
            console.println("Circular combo right: " + comboRight.getName() + " !!");
            return;
        }
        
        String indent2 = indent + indent;
        
        for (Right r : comboRight.getRights()) {
            String tt = r.getTargetTypeStr();
            tt = tt==null?"": " (" + tt + ")";
            // console.format("%s%10.10s: %s %s\n", indent2, r.getRightType().name(), r.getName(), tt);
            console.format("%s %s: %s %s\n", indent2, r.getRightType().name(), r.getName(), tt);
            
            seen.add(comboRight.getName());
            
            if (r.isComboRight() && expandComboRight) {
                dumpComboRight((ComboRight)r, expandComboRight, indent2, seen);
            }
            
            seen.clear();
        }
    }

    private void doGetRightsDoc(String[] args) throws ServiceException {
        if (!(mProv instanceof SoapProvisioning))
            throwSoapOnly();

        String[] packages;

        StringBuilder argsDump = new StringBuilder();
        if (args.length > 1) {
            // args[0] is "grd", starting from args[1]
            packages = new String[args.length - 1];
            for (int i=1; i<args.length; i++) {
                packages[i-1] = args[i];
                argsDump.append(" " + args[i]);
            }

        } else {
            packages = new String[] {
                    "com.zimbra.cs.service.admin",
                    "com.zimbra.bp",
                    "com.zimbra.cert",
                    "com.zimbra.cs.network",
                    "com.zimbra.cs.network.license.service",
                    "com.zimbra.cs.service.backup",
                    "com.zimbra.cs.service.hsm",
                    "com.zimbra.xmbxsearch"};
        }

        console.println("#");
        console.println("#  Generated by: zmprov grd" + argsDump);
        console.println("#");
        console.println("#  Date: " + DateFormat.getDateInstance(DateFormat.LONG).format(new Date()));
        console.println("# ");
        console.println("#  Pacakges:");
        for (String pkg : packages)
            console.println("#       " + pkg);
        console.println("# ");
        console.println("\n");

        Map<String, List<RightsDoc>> allDocs = mProv.getRightsDoc(packages);
        for (Map.Entry<String, List<RightsDoc>> docs : allDocs.entrySet()) {
            console.println("========================================");
            console.println("Package: " + docs.getKey());
            console.println("========================================");
            console.println();

            for (RightsDoc doc : docs.getValue()) {
                console.println("------------------------------");
                console.println(doc.getCmd() + "\n");

                console.println("    Related rights:");
                for (String r : doc.getRights())
                    console.println("        " + r);

                console.println();
                console.println("    Notes:");
                for (String n : doc.getNotes())
                    console.println(FileGenUtil.wrapComments(StringUtil.escapeHtml(n), 70, "        ") + "\n");
                console.println();
            }
        }
    }

    private void doGetAllServers(String[] args) throws ServiceException {
        boolean verbose = false;
        boolean applyDefault = true;
        String service = null;

        int i = 1;
        while (i < args.length) {
            String arg = args[i];
            if (arg.equals("-v"))
                verbose = true;
            else if (arg.equals("-e"))
                applyDefault = false;
            else {
                if (service == null)
                    service = arg;
                else {
                    console.println("invalid arg: " + arg + ", already specified service: " + service);
                    usage();
                    return;
                }
            }
            i++;
        }

        if (!applyDefault && !verbose) {
            console.println(ERR_INVALID_ARG_EV);
            usage();
            return;
        }

        List servers;
        if (mProv instanceof SoapProvisioning) {
            SoapProvisioning soapProv = (SoapProvisioning)mProv;
            servers = soapProv.getAllServers(service, applyDefault);
        } else
            servers = mProv.getAllServers(service);

        for (Iterator it=servers.iterator(); it.hasNext(); ) {
            Server server = (Server) it.next();
            if (verbose)
                dumpServer(server, applyDefault, null);
            else
                console.println(server.getName());
        }
    }

    private void dumpServer(Server server, boolean expandConfig, Set<String> attrNames) throws ServiceException {
        console.println("# name "+server.getName());
        Map<String, Object> attrs = server.getAttrs(expandConfig);
        dumpAttrs(attrs, attrNames);
        console.println();
    }

    private void dumpXMPPComponent(XMPPComponent comp, Set<String> attrNames) throws ServiceException {
        console.println("# name "+comp.getName());
        Map<String, Object> attrs = comp.getAttrs();
        dumpAttrs(attrs, attrNames);
        console.println();
    }

    private void doGetAllXMPPComponents() throws ServiceException {
        List<XMPPComponent> components = mProv.getAllXMPPComponents();
        for (XMPPComponent comp : components) {
            dumpXMPPComponent(comp, null);
        }
    }

    private void dumpAccount(Account account, boolean expandCos, Set<String> attrNames) throws ServiceException {
        console.println("# name "+account.getName());
        Map<String, Object> attrs = account.getAttrs(expandCos);
        dumpAttrs(attrs, attrNames);
        console.println();
    }

    private void dumpCalendarResource(CalendarResource resource, boolean expandCos, Set<String> attrNames) throws ServiceException {
        console.println("# name "+resource.getName());
        Map<String, Object> attrs = resource.getAttrs(expandCos);
        dumpAttrs(attrs, attrNames);
        console.println();
    }

    private void dumpContact(GalContact contact) throws ServiceException {
        console.println("# name "+contact.getId());
        Map<String, Object> attrs = contact.getAttrs();
        dumpAttrs(attrs, null);
        console.println();
    }

    private void dumpIdentity(Identity identity, Set<String> attrNameSet) throws ServiceException {
        console.println("# name "+identity.getName());
        Map<String, Object> attrs = identity.getAttrs();
        dumpAttrs(attrs, attrNameSet);
        console.println();
    }

    private void dumpSignature(Signature signature, Set<String> attrNameSet) throws ServiceException {
        console.println("# name "+signature.getName());
        Map<String, Object> attrs = signature.getAttrs();
        dumpAttrs(attrs, attrNameSet);
        console.println();
    }

    private void dumpAttrs(Map<String, Object> attrsIn, Set<String> specificAttrs) throws ServiceException {
        TreeMap<String, Object> attrs = new TreeMap<String, Object>(attrsIn);

        Map<String, Set<String>> specificAttrValues = null;

        if (specificAttrs != null) {
            specificAttrValues = new HashMap<String, Set<String>>();
            for (String specificAttr : specificAttrs) {
                int colonAt = specificAttr.indexOf("=");
                String attrName = null;
                String attrValue = null;
                if (colonAt == -1) {
                    attrName = specificAttr;
                } else {
                    attrName = specificAttr.substring(0, colonAt);
                    attrValue = specificAttr.substring(colonAt+1);
                    if (attrValue.length() < 1)
                        throw ServiceException.INVALID_REQUEST("missing value for " + specificAttr, null);
                }

                attrName = attrName.toLowerCase();
                Set<String> values = specificAttrValues.get(attrName);
                if (values == null) // haven't seen the attr yet
                    values = new HashSet<String>();

                if (attrValue != null)
                    values.add(attrValue);

                specificAttrValues.put(attrName, values);
            }
        }

        AttributeManager attrMgr = AttributeManager.getInstance();
        
        SimpleDateFormat dateFmt = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = dateFmt.format(new Date());
        
        for (Map.Entry<String, Object> entry : attrs.entrySet()) {
            String name = entry.getKey();

            boolean isBinary = needsBinaryIO(attrMgr, name);
                
            Set<String> specificValues = null;
            if (specificAttrValues != null)
                specificValues = specificAttrValues.get(name.toLowerCase());

            if (specificAttrValues == null || specificAttrValues.keySet().contains(name.toLowerCase())) {

                Object value = entry.getValue();
                
                if (value instanceof String[]) {
                    String sv[] = (String[]) value;
                    for (int i = 0; i < sv.length; i++) {
                        String aSv = sv[i];
                        // don't print permission denied attr
                        if (aSv.length() > 0 && (specificValues == null || specificValues.isEmpty() || specificValues.contains(aSv))) {
                            printAttr(name, aSv, i, isBinary, timestamp);
                        }
                    }
                } else if (value instanceof String) {
                    // don't print permission denied attr
                    if (((String)value).length() > 0 && (specificValues == null || specificValues.isEmpty() || specificValues.contains(value))) {
                        printAttr(name, (String)value, null, isBinary, timestamp);
                    }
                }
            }
        }
    }

    private void doCreateDistributionListsBulk(String[] args) throws ServiceException {
        if (args.length < 3) {
            usage();
        } else {
            String domain = args[1];
            String nameMask = args[2];
            int numAccounts = Integer.parseInt(args[3]);
            for(int ix=0; ix < numAccounts; ix++) {
                String name = nameMask + Integer.toString(ix) + "@" + domain;
                Map<String, Object> attrs = new HashMap<String, Object>();
                String displayName = nameMask + " N. " + Integer.toString(ix);
                StringUtil.addToMultiMap(attrs, "displayName", displayName);
                DistributionList dl  = mProv.createDistributionList(name, attrs);
                console.println(dl.getId());
           }
        }
    }

    private void doGetAllDistributionLists(String[] args) throws ServiceException {
        String d = null;
        boolean verbose = false;
        int i = 1;
        while (i < args.length) {
            String arg = args[i];
            if (arg.equals("-v")) {
                verbose = true;
            } else {
                if (d == null)
                    d = arg;
                else {
                    console.println("invalid arg: " + arg + ", already specified domain: " + d);
                    usage();
                    return;
                }
            }
            i++;
        }

        if (d == null) {
            List domains = mProv.getAllDomains();
            for (Iterator dit=domains.iterator(); dit.hasNext(); ) {
                Domain domain = (Domain) dit.next();
                Collection dls = mProv.getAllDistributionLists(domain);
                for (Iterator it = dls.iterator(); it.hasNext();) {
                    DistributionList dl = (DistributionList)it.next();
                    if (verbose)
                        dumpDistributionList(dl, null);
                    else
                        console.println(dl.getName());
                }
            }
        } else {
            Domain domain = lookupDomain(d);
            Collection dls = mProv.getAllDistributionLists(domain);
            for (Iterator it = dls.iterator(); it.hasNext();) {
                DistributionList dl = (DistributionList) it.next();
                if (verbose)
                    dumpDistributionList(dl, null);
                else
                    console.println(dl.getName());
            }
        }
    }

    private void doGetAllCalendarResources(String[] args)
    throws ServiceException {
        boolean verbose = false;
        boolean applyDefault = true;
        String d = null;
        String s = null;

        int i = 1;
        while (i < args.length) {
            String arg = args[i];
            if (arg.equals("-v"))
                verbose = true;
            else if (arg.equals("-e"))
                applyDefault = false;
            else if (arg.equals("-s")) {
                i++;
                if (i < args.length) {
                    if (s == null)
                        s = args[i];
                    else {
                        console.println("invalid arg: " + args[i] + ", already specified -s with " + s);
                        usage();
                        return;
                    }
                } else {
                    usage();
                    return;
                }
            } else {
                if (d == null)
                    d = arg;
                else {
                    console.println("invalid arg: " + arg + ", already specified domain: " + d);
                    usage();
                    return;
                }
            }
            i++;
        }

        if (!applyDefault && !verbose) {
            console.println(ERR_INVALID_ARG_EV);
            usage();
            return;
        }

        // always use LDAP
        Provisioning prov = Provisioning.getInstance();

        Server server = null;
        if (s != null)
            server = lookupServer(s);

        if (d == null) {
            List<Domain> domains = prov.getAllDomains();
            for (Domain domain : domains) {
                doGetAllCalendarResources(prov, domain, server, verbose, applyDefault);
            }
        } else {
            Domain domain = lookupDomain(d, prov);
            doGetAllCalendarResources(prov, domain, server, verbose, applyDefault);
        }
    }

    private void doGetAllCalendarResources(Provisioning prov, Domain domain, Server server,
                                           final boolean verbose, final boolean applyDefault)
    throws ServiceException {
        NamedEntry.Visitor visitor = new NamedEntry.Visitor() {
            @Override
            public void visit(com.zimbra.cs.account.NamedEntry entry)
            throws ServiceException {
                if (verbose) {
                    dumpCalendarResource((CalendarResource) entry, applyDefault, null);
                } else {
                    console.println(entry.getName());
                }
            }
        };
        prov.getAllCalendarResources(domain, server, visitor);
    }

    private void doSearchCalendarResources(String[] args) throws ServiceException {
        if (!(mProv instanceof LdapProvisioning))
            throwLdapOnly();

        boolean verbose = false;
        int i = 1;

        if (args.length < i + 1) { usage(); return; }
        if (args[i].equals("-v")) { verbose = true; i++; }

        if (args.length < i + 1) { usage(); return; }
        Domain d = lookupDomain(args[i++]);

        if ((args.length - i) % 3 != 0) { usage(); return; }

        EntrySearchFilter.Multi multi =
            new EntrySearchFilter.Multi(false, EntrySearchFilter.AndOr.and);
        for (; i < args.length; ) {
            String attr = args[i++];
            String op = args[i++];
            String value = args[i++];
            try {
                EntrySearchFilter.Single single =
                    new EntrySearchFilter.Single(false, attr, op, value);
                multi.add(single);
            } catch (IllegalArgumentException e) {
                printError("Bad search op in: " + attr + " " + op + " '" + value + "'");
                e.printStackTrace();
                usage();
                return;
            }
        }
        EntrySearchFilter filter = new EntrySearchFilter(multi);

        List resources = mProv.searchCalendarResources(d, filter, null, null, true);
        for (Iterator iter = resources.iterator(); iter.hasNext(); ) {
            CalendarResource resource = (CalendarResource) iter.next();
            if (verbose)
                dumpCalendarResource(resource, true, null);
            else
                console.println(resource.getName());
        }
    }

    private void initNotebook(String[] args) throws ServiceException {
        if (args.length > 2) {usage(); return; }
        String username = null;

        if (args.length == 2)
            username = args[1];

        WikiUtil wu = WikiUtil.getInstance(mProv);
        wu.initDefaultWiki(username);
    }
    private void initDomainNotebook(String[] args) throws ServiceException {
        if (args.length < 2 || args.length > 3) {usage(); return; }

        String domain = null;
        String username = null;

        domain = args[1];
        if (args.length == 3)
            username = args[2];

        if (mProv.get(AccountBy.name, username) == null)
            throw AccountServiceException.NO_SUCH_ACCOUNT(username);

        WikiUtil wu = WikiUtil.getInstance(mProv);
        wu.initDomainWiki(domain, username);
    }
    private void importNotebook(String[] args) throws ServiceException, IOException {
        if (args.length != 4) {usage(); return; }

        WikiUtil wu = WikiUtil.getInstance(mProv);
        wu.startImport(args[1], args[3], new java.io.File(args[2]));
    }
    private void updateTemplates(String[] args) throws ServiceException, IOException {
        if (args.length != 2 && args.length != 4) {usage(); return; }

        String dir = args[1];
        Server server = null;

        if (args.length == 4 && args[1].equals("-h")) {
            server = mProv.get(ServerBy.name, args[2]);
            if (server == null)
                throw AccountServiceException.NO_SUCH_SERVER(args[2]);
            dir = args[3];
        }
        WikiUtil wu = WikiUtil.getInstance(mProv);
        wu.updateTemplates(server, new java.io.File(dir));
    }

    private Account lookupAccount(String key, boolean mustFind, boolean applyDefault) throws ServiceException {

        Account a;
        if (applyDefault==true || (mProv instanceof LdapProvisioning))
            a = mProv.getAccount(key);
        else {
            /*
             * oops, do not apply default, and we are SoapProvisioning
             *
             * This a bit awkward because the applyDefault is controlled at the Entry.getAttrs, not at
             * the provisioning interface.   But for SOAP, this needs to be passed to the get(AccountBy)
             * method so it can set the flag in SOAP.   We do not want to add a provisioning method for
             * this.  Instead, we make it a SOAPProvisioning only method.
             *
             */
            SoapProvisioning soapProv = (SoapProvisioning)mProv;
            a = soapProv.getAccount(key, applyDefault);
        }

        if (mustFind && a == null)
            throw AccountServiceException.NO_SUCH_ACCOUNT(key);
        else
            return a;
    }

    private Account lookupAccount(String key) throws ServiceException {
        return lookupAccount(key, true, true);
    }

    private Account lookupAccount(String key, boolean mustFind) throws ServiceException {
        return lookupAccount(key, mustFind, true);
    }

    private CalendarResource lookupCalendarResource(String key) throws ServiceException {
        CalendarResource res = mProv.get(guessCalendarResourceBy(key), key);
        if (res == null)
            throw AccountServiceException.NO_SUCH_CALENDAR_RESOURCE(key);
        else
            return res;
    }

    private Domain lookupDomain(String key) throws ServiceException {
        return lookupDomain(key, mProv);
    }

    private Domain lookupDomain(String key, Provisioning prov) throws ServiceException {
        return lookupDomain(key, prov, true);
    }

    private Domain lookupDomain(String key, Provisioning prov, boolean applyDefault) throws ServiceException {

        Domain d;

        if (prov instanceof SoapProvisioning) {
            SoapProvisioning soapProv = (SoapProvisioning)prov;
            d = soapProv.get(guessDomainBy(key), key, applyDefault);
        } else
            d = prov.get(guessDomainBy(key), key);

        if (d == null)
            throw AccountServiceException.NO_SUCH_DOMAIN(key);
        else
            return d;
    }

    private Cos lookupCos(String key) throws ServiceException {
        Cos c = mProv.get(guessCosBy(key), key);
        if (c == null)
            throw AccountServiceException.NO_SUCH_COS(key);
        else
            return c;
    }

    private Right lookupRight(String rightName) throws ServiceException {
        return mProv.getRight(rightName, false);
    }

    private Server lookupServer(String key) throws ServiceException {
        return lookupServer(key, true);
    }

    private Server lookupServer(String key, boolean applyDefault) throws ServiceException {
        Server s;

        if (mProv instanceof SoapProvisioning) {
            SoapProvisioning soapProv = (SoapProvisioning)mProv;
            s = soapProv.get(guessServerBy(key), key, applyDefault);
        } else
            s = mProv.get(guessServerBy(key), key);

        if (s == null)
            throw AccountServiceException.NO_SUCH_SERVER(key);
        else
            return s;
    }

    private String lookupDataSourceId(Account account, String key) throws ServiceException {
        if (Provisioning.isUUID(key)) {
            return key;
        }
        DataSource ds = mProv.get(account, DataSourceBy.name, key);
        if (ds == null)
            throw AccountServiceException.NO_SUCH_DATA_SOURCE(key);
        else
            return ds.getId();
    }

    private String lookupSignatureId(Account account, String key) throws ServiceException {
        Signature sig = mProv.get(account, guessSignatureBy(key), key);
        if (sig == null)
            throw AccountServiceException.NO_SUCH_SIGNATURE(key);
        else
            return sig.getId();
    }

    private DistributionList lookupDistributionList(String key, boolean mustFind) throws ServiceException {
        DistributionList dl = mProv.get(guessDistributionListBy(key), key);
        if (mustFind && dl == null)
            throw AccountServiceException.NO_SUCH_DISTRIBUTION_LIST(key);
        else
            return dl;
    }

    private DistributionList lookupDistributionList(String key) throws ServiceException {
        return lookupDistributionList(key, true);
    }

    private XMPPComponent lookupXMPPComponent(String value) throws ServiceException {
        if (Provisioning.isUUID(value)) {
            return mProv.get(XMPPComponentBy.id, value);
        } else {
            return mProv.get(XMPPComponentBy.name, value);
        }
    }

    public static AccountBy guessAccountBy(String value) {
        if (Provisioning.isUUID(value))
            return AccountBy.id;
        return AccountBy.name;
    }


    public static CosBy guessCosBy(String value) {
        if (Provisioning.isUUID(value))
            return CosBy.id;
        return CosBy.name;
    }

    public static DomainBy guessDomainBy(String value) {
        if (Provisioning.isUUID(value))
            return DomainBy.id;
        return DomainBy.name;
    }

    public static ServerBy guessServerBy(String value) {
        if (Provisioning.isUUID(value))
            return ServerBy.id;
        return ServerBy.name;
    }

    public static CalendarResourceBy guessCalendarResourceBy(String value) {
        if (Provisioning.isUUID(value))
            return CalendarResourceBy.id;
        return CalendarResourceBy.name;
    }

    public static DistributionListBy guessDistributionListBy(String value) {
        if (Provisioning.isUUID(value))
            return DistributionListBy.id;
        return DistributionListBy.name;
    }

    public static SignatureBy guessSignatureBy(String value) {
        if (Provisioning.isUUID(value))
            return SignatureBy.id;
        return SignatureBy.name;
    }

    public static TargetBy guessTargetBy(String value) {
        if (Provisioning.isUUID(value))
            return TargetBy.id;
        return TargetBy.name;
    }

    public static GranteeBy guessGranteeBy(String value) {
        if (Provisioning.isUUID(value))
            return GranteeBy.id;
        return GranteeBy.name;
    }

    private void checkDeprecatedAttrs(Map<String, ? extends Object> attrs) throws ServiceException {
        AttributeManager am = AttributeManager.getInstance();
        boolean hadWarnings = false;
        for (String attr : attrs.keySet()) {
            AttributeInfo ai = am.getAttributeInfo(attr);
            if (ai == null) {
                continue;
            }
            
            if (ai.isDeprecated()) {
                hadWarnings = true;
                console.println("Warn: attribute " + attr + " has been deprecated since " + 
                        ai.getDeprecatedSince().toString());
            }
        }
        
        if (hadWarnings) {
            console.println();
        }
    }

	private static boolean needsBinaryIO(AttributeManager attrMgr, String attr) {
	    return attrMgr.containsBinaryData(attr);
	}

	/**
	 * get map and check/warn deprecated attrs.
	 */
    private Map<String, Object> getMapAndCheck(String[] args, int offset) throws ArgException, ServiceException {
        Map<String, Object> attrs = getAttrMap(args, offset);
        checkDeprecatedAttrs(attrs);
        return attrs;
    }
    

    /**
     * Convert an array of the form:
     *
     *    a1 v1 a2 v2 a2 v3
     *
     * to a map of the form:
     *
     *    a1 -> v1
     *    a2 -> [v2, v3]
     *    
     * For binary attribute, the argument following an attribute name will be treated as a 
     * file path and value for the attribute will be the base64 encoded string of the content 
     * of the file.   
     */
    private static Map<String, Object> keyValueArrayToMultiMap(String[] args, int offset) 
    throws IOException, ServiceException  {
        AttributeManager attrMgr = AttributeManager.getInstance();
        
        Map<String, Object> attrs = new HashMap<String, Object>();
        for (int i = offset; i < args.length; i += 2) {
            String n = args[i];
            if (i + 1 >= args.length) {
                throw new IllegalArgumentException("not enough arguments");
            }
            String v = args[i + 1];
            String attrName = n;
            if (n.charAt(0) == '+' || n.charAt(0) == '-') {
                attrName = attrName.substring(1);
            }
            if (needsBinaryIO(attrMgr, attrName) && v.length() > 0) {
                File file = new File(v);
                byte[] bytes = ByteUtil.getContent(file);
                v = ByteUtil.encodeLDAPBase64(bytes);
            }
            StringUtil.addToMultiMap(attrs, n, v);
        }
        return attrs;
    }
    
    private Map<String, Object> getAttrMap(String[] args, int offset) throws ArgException, ServiceException {
        try {
            return keyValueArrayToMultiMap(args, offset);
        } catch (IllegalArgumentException iae) {
            throw new ArgException("not enough arguments");
        } catch (IOException ioe) {
            throw ServiceException.INVALID_REQUEST("unable to process arguments", ioe);
        }
    }
    
    private Map<String, Object> getMap(String[] args, int offset) throws ArgException {
        try {
            return StringUtil.keyValueArrayToMultiMap(args, offset);
        } catch (IllegalArgumentException iae) {
            throw new ArgException("not enough arguments");
        }
    }

    private Set<String> getArgNameSet(String[] args, int offset) {
        if (offset >= args.length) return null;

        Set<String> result = new HashSet<String>();
        for (int i=offset; i < args.length; i++)
            result.add(args[i].toLowerCase());

        return result;
    }

    private void interactive(BufferedReader in) throws IOException {
        mReader = in;
        mInteractive = true;
        while (true) {
            console.print("prov> ");
            String line = StringUtil.readLine(in);
            if (line == null)
                break;
            if (mVerbose) {
                console.println(line);
            }
            String args[] = StringUtil.parseLine(line);
            if (args.length == 0)
                continue;
            try {
                if (!execute(args)) {
                    console.println("Unknown command. Type: 'help commands' for a list");
                }
            } catch (ServiceException e) {
                errorOccursDuringInteraction = true;
                Throwable cause = e.getCause();
                String errText = "ERROR: " + e.getCode() + " (" + e.getMessage() + ")" +
                        (cause == null ? "" : " (cause: " + cause.getClass().getName() + " " + cause.getMessage() + ")");

                printError(errText);

                if (mVerbose) e.printStackTrace(errConsole);
            } catch (ArgException e) {
                    usage();
            }
        }
    }


    /**
     * Output binary attribute to file
     * 
     * value is written to:
     * {LC.zmprov_tmp_directory}/{attr-name}[_{index-if-multi-valued}]{timestamp}
     * 
     * e.g.
     * /opt/zimbra/data/tmp/zmprov/zimbraFoo_20110202161621
     * 
     * /opt/zimbra/data/tmp/zmprov/zimbraBar_0_20110202161507
     * /opt/zimbra/data/tmp/zmprov/zimbraBar_1_20110202161507
     * 
     * @param attrName
     * @param idx
     * @param value
     * @param timestamp
     * @throws ServiceException
     */
    private void outputBinaryAttrToFile(String attrName, Integer idx, byte[] value, String timestamp) 
    throws ServiceException {
        StringBuilder sb = new StringBuilder(LC.zmprov_tmp_directory.value());
        sb.append(File.separator).append(attrName);
        if (idx != null) {
            sb.append("_" + idx);
        }
        sb.append("_" + timestamp);
        
        File file = new File(sb.toString());
        if (file.exists()) {
            file.delete();
        }
        
        try {
            FileUtil.ensureDirExists(file.getParentFile());
        } catch (IOException e) {
            throw ServiceException.FAILURE(
                    "Unable to create directory " + file.getParentFile().getAbsolutePath(), e);
        }

        try {
            ByteUtil.putContent(file.getAbsolutePath(), value);
        } catch (IOException e) {
            throw ServiceException.FAILURE(
                    "Unable to write to file " + file.getAbsolutePath(), e);
        }
    }
    
    private void printAttr(String attrName, String value, Integer idx, boolean isBinary, String timestamp) 
    throws ServiceException {
        if (isBinary) {
            byte[] binary = ByteUtil.decodeLDAPBase64(value);
            if (outputBinaryToFile()) {
                outputBinaryAttrToFile(attrName, idx, binary, timestamp);
            } else {
                // print base64 encoded content
                // follow ldapsearch notion of using two colons when printing base64 encoded data
                // re-encode into 76 character blocks
                String based64Chunked = new String(Base64.encodeBase64Chunked(binary));
                // strip off the \n at the end
                if (based64Chunked.charAt(based64Chunked.length() -1) == '\n') {
                    based64Chunked = based64Chunked.substring(0, based64Chunked.length()-1);
                }
                printOutput(attrName + ":: " + based64Chunked);
            }
        } else {
            printOutput(attrName + ": " + value);
        }
    }

    private static void printError(String text) {
        PrintStream ps = errConsole;
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ps, "UTF-8"));
            writer.write(text+"\n");
            writer.flush();
        } catch (UnsupportedEncodingException e) {
            ps.println(text);
        } catch (IOException e) {
            ps.println(text);
        }
    }
    
    private static void printOutput(String text) {
        PrintStream ps = console;
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ps, "UTF-8"));
            writer.write(text+"\n");
            writer.flush();
        } catch (UnsupportedEncodingException e) {
            ps.println(text);
        } catch (IOException e) {
            ps.println(text);
        }
    }

    public static void main(String args[]) throws IOException, ServiceException {
        CliUtil.setCliSoapHttpTransportTimeout();
        ZimbraLog.toolSetupLog4jConsole("INFO", true, false); // send all logs to stderr
        SocketFactories.registerProtocols();

        SoapTransport.setDefaultUserAgent("zmprov", BuildInfo.VERSION);

        ProvUtil pu = new ProvUtil();
        CommandLineParser parser = new PosixParser();
        Options options = new Options();
        
        options.addOption("h", "help", false, "display usage");
        options.addOption("f", "file", true, "use file as input stream");
        options.addOption("s", "server", true, "host[:port] of server to connect to");
        options.addOption("l", "ldap", false, "provision via LDAP");
        options.addOption("L", "logpropertyfile", true, "log4j property file");
        options.addOption("a", "account", true, "account name (not used with --ldap)");
        options.addOption("p", "password", true, "password for account");
        options.addOption("P", "passfile", true, "filename with password in it");
        options.addOption("z", "zadmin", false, "use zimbra admin name/password from localconfig for account/password");
        options.addOption("v", "verbose", false, "verbose mode");
        options.addOption("d", "debug", false, "debug mode (SOAP request and response payload)");
        options.addOption("D", "debughigh", false, "debug mode (SOAP req/resp payload and http headers)");
        options.addOption("m", "master", false, "use LDAP master (has to be used with --ldap)");
        options.addOption("t", "temp", false, "write binary values to files in temporary directory specified in localconfig key zmprov_tmp_directory");
        options.addOption(SoapCLI.OPT_AUTHTOKEN);
        options.addOption(SoapCLI.OPT_AUTHTOKENFILE);

        CommandLine cl = null;
        boolean err = false;

        try {
            cl = parser.parse(options, args, true);
        } catch (ParseException pe) {
            printError("error: " + pe.getMessage());
            err = true;
        }

        if (err || cl.hasOption('h')) {
            pu.usage();
        }

        if (cl.hasOption('l') && cl.hasOption('s')) {
            printError("error: cannot specify both -l and -s at the same time");
            System.exit(2);
        }

        pu.setVerbose(cl.hasOption('v'));
        if (cl.hasOption('l'))
            pu.setUseLdap(true, cl.hasOption('m'));

        if (cl.hasOption('L')) {
            if (cl.hasOption('l'))
                ZimbraLog.toolSetupLog4j("INFO", cl.getOptionValue('L'));
            else {
                printError("error: cannot specify -L when -l is not specified");
                System.exit(2);
            }
        }

        if (cl.hasOption('z')) {
            pu.setAccount(LC.zimbra_ldap_user.value());
            pu.setPassword(LC.zimbra_ldap_password.value());
        }

        if (cl.hasOption(SoapCLI.O_AUTHTOKEN) && cl.hasOption(SoapCLI.O_AUTHTOKENFILE)) {
            printError("error: cannot specify " + SoapCLI.O_AUTHTOKEN + " when " + SoapCLI.O_AUTHTOKENFILE + " is specified");
            System.exit(2);
        }
        if (cl.hasOption(SoapCLI.O_AUTHTOKEN)) {
            ZAuthToken zat = ZAuthToken.fromJSONString(cl.getOptionValue(SoapCLI.O_AUTHTOKEN));
            pu.setAuthToken(zat);
        }
        if (cl.hasOption(SoapCLI.O_AUTHTOKENFILE)) {
            String authToken = StringUtil.readSingleLineFromFile(cl.getOptionValue(SoapCLI.O_AUTHTOKENFILE));
            ZAuthToken zat = ZAuthToken.fromJSONString(authToken);
            pu.setAuthToken(zat);
        }

        if (cl.hasOption('s')) pu.setServer(cl.getOptionValue('s'));
        if (cl.hasOption('a')) pu.setAccount(cl.getOptionValue('a'));
        if (cl.hasOption('p')) pu.setPassword(cl.getOptionValue('p'));
        if (cl.hasOption('P')) {
            pu.setPassword(StringUtil.readSingleLineFromFile(cl.getOptionValue('P')));
        }

        if (cl.hasOption('d') && cl.hasOption('D')) {
            printError("error: cannot specify both -d and -D at the same time");
            System.exit(2);
        }
        if (cl.hasOption('D'))
            pu.setDebug(SoapDebugLevel.high);
        else if (cl.hasOption('d'))
            pu.setDebug(SoapDebugLevel.normal);


        if (!pu.useLdap() && cl.hasOption('m')) {
            printError("error: cannot specify -m when -l is not specified");
            System.exit(2);
        }

        if (cl.hasOption('t')) {
            pu.setOutputBinaryToFile(true);
        }
        
        args = cl.getArgs();

        try {
            if (args.length < 1) {
                pu.initProvisioning();
                InputStream is = null;
                if (cl.hasOption('f')) {
                    is = new FileInputStream(cl.getOptionValue('f'));
                } else {
                    if (LC.command_line_editing_enabled.booleanValue()) {
                        try {
                            CliUtil.enableCommandLineEditing(LC.zimbra_home.value() + "/.zmprov_history");
                        } catch (IOException e) {
                            errConsole.println("Command line editing will be disabled: " + e);
                            if (pu.mVerbose) {
                                e.printStackTrace(errConsole);
                            }
                        }
                    }
                    
                    // This has to happen last because JLine modifies System.in.
                    is = System.in;
                }
                pu.interactive(new BufferedReader(new InputStreamReader(is, "UTF-8")));
            } else {
                Command cmd = pu.lookupCommand(args[0]);
                if (cmd == null)
                    pu.usage();
                if (cmd.isDeprecated())
                    pu.deprecated();

                if (pu.forceLdapButDontRequireUseLdapOption(cmd))
                    pu.setUseLdap(true, false);
                pu.initProvisioning();

                try {
                    if (!pu.execute(args))
                        pu.usage();
                } catch (ArgException e) {
                    pu.usage();
                }
            }
        } catch (ServiceException e) {
            Throwable cause = e.getCause();
            String errText = "ERROR: " + e.getCode() + " (" + e.getMessage() + ")" +
                    (cause == null ? "" : " (cause: " + cause.getClass().getName() + " " + cause.getMessage() + ")");

            printError(errText);

            if (pu.mVerbose) e.printStackTrace(errConsole);
            System.exit(2);
        }
    }

    class ArgException extends Exception {
        ArgException(String msg) {
            super(msg);
        }
    }

    private static class DescribeArgs {

        enum Field {
            type("attribute type"),
            value("value for enum or regex attributes"),
            callback("class name of AttributeCallback object to invoke on changes to attribute."),
            immutable("whether this attribute can be modified directly"),
            cardinality("single or multi"),
            requiredIn("comma-seperated list containing classes in which this attribute is required"),
            optionalIn("comma-seperated list containing classes in which this attribute can appear"),
            flags("attribute flags"),
            defaults("default value on global config or default COS(for new install) and all upgraded COS's"),
            min("min value for integers and durations. defaults to Integer.MIN_VALUE"),
            max("max value for integers and durations, max length for strings/email, defaults to Integer.MAX_VALUE"),
            id("leaf OID of the attribute"),
            requiresRestart("server(s) need be to restarted after changing this attribute"),
            since("version since which the attribute had been introduced"),
            deprecatedSince("version since which the attribute had been deprecaed");
            // desc("description");

            String mDesc;

            Field(String desc) {
                mDesc = desc;
            }

            String getDesc() {
                return mDesc;
            }

            static String formatDefaults(AttributeInfo ai) {
                StringBuilder sb = new StringBuilder();
                for (String d : ai.getDefaultCosValues())
                    sb.append(d + ",");
                for (String d : ai.getGlobalConfigValues())
                    sb.append(d + ",");

                return sb.length()==0 ? "" : sb.substring(0, sb.length()-1); // trim the ending ,
            }

            static String formatRequiredIn(AttributeInfo ai) {
                Set<AttributeClass> requiredIn = ai.getRequiredIn();
                if (requiredIn == null)
                    return "";

                StringBuilder sb = new StringBuilder();

                for (AttributeClass ac : requiredIn)
                    sb.append(ac.name() + ",");
                return sb.substring(0, sb.length()-1); // trim the ending ,
            }

            static String formatOptionalIn(AttributeInfo ai) {
                Set<AttributeClass> optionalIn = ai.getOptionalIn();
                if (optionalIn == null)
                    return "";

                StringBuilder sb = new StringBuilder();
                for (AttributeClass ac : optionalIn)
                    sb.append(ac.name() + ",");
                return sb.substring(0, sb.length()-1); // trim the ending ,
            }

            static String formatFlags(AttributeInfo ai) {
                StringBuilder sb = new StringBuilder();
                for (AttributeFlag f : AttributeFlag.values()) {
                    if (ai.hasFlag(f))
                        sb.append(f.name() + ",");
                }
                return sb.length() == 0 ? "" : sb.substring(0, sb.length()-1); // trim the ending ,
            }

            static String formatRequiresRestart(AttributeInfo ai) {
                StringBuilder sb = new StringBuilder();
                List<AttributeServerType> requiresRetstart = ai.getRequiresRestart();
                if (requiresRetstart != null) {
                    for (AttributeServerType ast : requiresRetstart) {
                        sb.append(ast.name() + ",");
                    }
                }
                return sb.length() == 0 ? "" : sb.substring(0, sb.length()-1); // trim the ending ,
            }

            static String print(Field field, AttributeInfo ai) {
                String out = null;

                switch (field) {
                case type:
                    out = ai.getType().getName();
                    break;
                case value:
                    out = ai.getValue();
                    break;
                case callback:
                    AttributeCallback acb = ai.getCallback();
                    if (acb != null)
                        out = acb.getClass().getSimpleName();
                    break;
                case immutable:
                    out = Boolean.toString(ai.isImmutable());
                    break;
                case cardinality:
                    AttributeCardinality card = ai.getCardinality();
                    if (card != null)
                        out = card.name();
                    break;
                case requiredIn:
                    out = formatRequiredIn(ai);
                    break;
                case optionalIn:
                    out = formatOptionalIn(ai);
                    break;
                case flags:
                    out = formatFlags(ai);
                    break;
                case defaults:
                    out = formatDefaults(ai);
                    break;
                case min:
                    long min = ai.getMin();
                    if (min != Long.MIN_VALUE && min != Integer.MIN_VALUE)
                        out = Long.toString(min);
                    break;
                case max:
                    long max = ai.getMax();
                    if (max != Long.MAX_VALUE && max != Integer.MAX_VALUE)
                        out = Long.toString(max);
                    break;
                case id:
                    int id = ai.getId();
                    if (id != -1)
                        out = Integer.toString(ai.getId());
                    break;
                case requiresRestart:
                    out = formatRequiresRestart(ai);
                    /*
                    if (out.length() == 0)
                        out= Bug26161.removeMeAfterBug26161IsFixed(ai);
                    */
                    break;
                case since:
                    Version since = ai.getSince();
                    if (since != null)
                        out = since.toString();
                    break;
                case deprecatedSince:
                    Version depreSince = ai.getDeprecatedSince();
                    if (depreSince != null)
                        out = depreSince.toString();
                    break;
                }

                if (out == null)
                    out = "";

                return out;
            }

        }

        /*
         * args when an object class is specified
         */
        boolean mNonInheritedOnly;
        boolean mOnThisObjectTypeOnly;
        AttributeClass mAttrClass;
        boolean mVerbose;

        /*
         * args when a specific attribute is specified
         */
        String mAttr;
    }

    static String formatLine(int width) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i< width; i++)
            sb.append("-");
        return sb.toString();
    }

    private String formatAllEntryTypes() {
        StringBuilder sb = new StringBuilder();
        for (AttributeClass ac : AttributeClass.values()) {
            if (ac.isProvisionable())
                sb.append(ac.name() + ",");
        }
        return sb.substring(0, sb.length()-1); // trim the ending ,
    }

    private void descAttrsUsage(Exception e) {
        console.println(e.getMessage() + "\n");

        console.printf("usage:  %s(%s) %s\n", mCommand.getName(), mCommand.getAlias(), mCommand.getHelp());

        console.println();
        console.println("Valid entry types: " + formatAllEntryTypes() + "\n");

        console.println("Examples:");

        console.println("zmprov desc");
        console.println("    print attribute name of all attributes" + "\n");

        console.println("zmprov desc -v");
        console.println("    print attribute name and description of all attributes" + "\n");

        console.println("zmprov desc account");
        console.println("    print attribute name of all account attributes" + "\n");

        console.println("zmprov desc -ni -v account");
        console.println("    print attribute name and description of all non-inherited account attributes, ");
        console.println("    that is, attributes that are on account but not on cos"+ "\n");

        console.println("zmprov desc -ni domain");
        console.println("    print attribute name of all non-inherited domain attributes, ");
        console.println("    that is, attributes that are on domain but not on global config"+ "\n");

        /*  -only is *not* a documented option, we could expose it if we want,
         *  handy for engineering tasks, not as useful for users
         *
        console.println("zmprov desc -only globalConfig");
        console.println("    print attribute name of all attributes that are on global config only" + "\n");
        */

        console.println("zmprov desc -a zimbraId");
        console.println("    print attribute name, description, and all properties of attribute zimbraId\n");

        console.println("zmprov desc account -a zimbraId");
        console.println("    error: can only specify either an entry type or a specific attribute\n");

        usage();
    }


    private DescribeArgs parseDescribeArgs(String[] args) throws ServiceException {
        DescribeArgs descArgs = new DescribeArgs();

        int i = 1;
        while (i < args.length) {
            if ("-v".equals(args[i])) {
                if (descArgs.mAttr != null)
                    throw ServiceException.INVALID_REQUEST("cannot specify -v when -a is specified", null);
                descArgs.mVerbose = true;

            } else if (args[i].startsWith("-ni")) {
                if (descArgs.mAttr != null)
                    throw ServiceException.INVALID_REQUEST("cannot specify -ni when -a is specified", null);
                descArgs.mNonInheritedOnly = true;
            } else if (args[i].startsWith("-only")) {
                if (descArgs.mAttr != null)
                    throw ServiceException.INVALID_REQUEST("cannot specify -only when -a is specified", null);
                descArgs.mOnThisObjectTypeOnly = true;

            } else if (args[i].startsWith("-a")) {
                if (descArgs.mAttrClass != null)
                    throw ServiceException.INVALID_REQUEST("cannot specify -a when entry type is specified", null);
                if (descArgs.mAttr != null)
                    throw ServiceException.INVALID_REQUEST("attribute is already specified as " + descArgs.mAttr, null);
                if (args.length <= i+1)
                    throw ServiceException.INVALID_REQUEST("not enough number of args", null);
                i++;
                descArgs.mAttr = args[i];

            } else {
                if (descArgs.mAttr != null)
                    throw ServiceException.INVALID_REQUEST("too many args", null);

                if (descArgs.mAttrClass != null)
                    throw ServiceException.INVALID_REQUEST("entry type is already specified as " + descArgs.mAttrClass, null);
                AttributeClass ac = AttributeClass.fromString(args[i]);
                if (ac == null || !ac.isProvisionable())
                    throw ServiceException.INVALID_REQUEST("invalid entry type " + ac.name(), null);
                descArgs.mAttrClass = ac;
            }
            i++;
        }

        if ((descArgs.mNonInheritedOnly == true || descArgs.mOnThisObjectTypeOnly == true) && descArgs.mAttrClass == null)
            throw ServiceException.INVALID_REQUEST("-ni -only must be specified with an entry type", null);

        return descArgs;
    }

    private void doDescribe(String[] args) throws ServiceException {
        // never use SOAP
        /*
        if (!(mProv instanceof LdapProvisioning))
            throwLdapOnly();
        */

        DescribeArgs descArgs = null;
        try {
            descArgs = parseDescribeArgs(args);
        } catch (ServiceException e) {
            descAttrsUsage(e);
            return;
        } catch (NumberFormatException e) {
            descAttrsUsage(e);
            return;
        }

        SortedSet<String> attrs  = null;
        String specificAttr = null;

        AttributeManager am = AttributeManager.getInstance();

        if (descArgs.mAttr != null) {
            //
            // specific attr
            //
            specificAttr = descArgs.mAttr;
        } else if (descArgs.mAttrClass != null) {
            //
            // attrs in a class
            //
            attrs = new TreeSet<String>(am.getAllAttrsInClass(descArgs.mAttrClass));
            if (descArgs.mNonInheritedOnly) {
                Set<String> inheritFrom = null;
                Set<String> netAttrs = null;
                switch (descArgs.mAttrClass) {
                case account:
                    netAttrs = new HashSet<String>(attrs);
                    inheritFrom = new HashSet<String>(am.getAllAttrsInClass(AttributeClass.cos));
                    netAttrs = SetUtil.subtract(netAttrs, inheritFrom);
                    inheritFrom = new HashSet<String>(am.getAllAttrsInClass(AttributeClass.domain)); // for accountCosDomainInherited
                    netAttrs = SetUtil.subtract(netAttrs, inheritFrom);
                    break;
                case domain:
                case server:
                    netAttrs = new HashSet<String>(attrs);
                    inheritFrom = new HashSet<String>(am.getAllAttrsInClass(AttributeClass.globalConfig));
                    netAttrs = SetUtil.subtract(netAttrs, inheritFrom);
                    break;
                }

                if (netAttrs != null)
                    attrs = new TreeSet<String>(netAttrs);
            }

            if (descArgs.mOnThisObjectTypeOnly) {
                TreeSet<String> netAttrs = new TreeSet<String>();
                for (String attr : attrs) {
                    AttributeInfo ai = am.getAttributeInfo(attr);
                    if (ai == null)
                        continue;

                    Set<AttributeClass> requiredIn = ai.getRequiredIn();
                    Set<AttributeClass> optionalIn = ai.getOptionalIn();
                    if ((requiredIn == null || requiredIn.size() == 1) &&
                        (optionalIn == null || optionalIn.size() == 1))
                    netAttrs.add(attr);
                }
                attrs = netAttrs;
            }

        } else {
            //
            // all attrs
            //

            // am.getAllAttrs() only contains attrs with AttributeInfo
            // not extension attrs
            // attrs = new TreeSet<String>(am.getAllAttrs());

            // attr sets for each AttributeClass contain attrs in the extensions, use them
            attrs = new TreeSet<String>();
            for (AttributeClass ac : AttributeClass.values()) {
                attrs.addAll(am.getAllAttrsInClass(ac));
            }
        }

        if (specificAttr != null) {
            AttributeInfo ai = am.getAttributeInfo(specificAttr);
            if (ai == null) {
                console.println("no attribute info for " + specificAttr);
                // throw ServiceException.INVALID_REQUEST("no such attribute: " + specificAttr, null);
            } else {

                console.println(ai.getName());

                // description
                String desc = ai.getDescription();
                console.println(FileGenUtil.wrapComments((desc==null?"":desc), 70, "    "));
                console.println();

                for (DescribeArgs.Field f : DescribeArgs.Field.values()) {
                    console.format("    %15s : %s\n", f.name(), DescribeArgs.Field.print(f, ai));
                }
            }
            console.println();

        } else {
            String indent = "    ";
            for (String attr : attrs) {
                AttributeInfo ai = am.getAttributeInfo(attr);
                if (ai == null) {
                    console.println(attr + " (no attribute info)");
                    continue;
                }

                String attrName = ai.getName();  // camel case name
                console.println(attrName);
                /* for tracking progress of bug 26161
                console.format("%-48s %-25s %s\n", attr,
                        DescribeArgs.Field.print(DescribeArgs.Field.requiresRestart, ai),
                        DescribeArgs.Field.print(DescribeArgs.Field.deprecatedSince, ai));
                */

                if (descArgs.mVerbose) {
                    String desc = ai.getDescription();
                    console.println(FileGenUtil.wrapComments((desc==null?"":desc), 70, "    ")  + "\n");
                }
            }
        }
    }

    private void doFlushCache(String[] args) throws ServiceException {
        if (!(mProv instanceof SoapProvisioning))
            throwSoapOnly();

        boolean allServers = false;

        int argIdx = 1;
        if (args[argIdx].equals("-a")) {
            allServers = true;
            argIdx++;
        }
        String type = args[argIdx++];

        CacheEntry[] entries = null;

        if (args.length > argIdx) {
            entries = new CacheEntry[args.length - argIdx];
            for (int i=argIdx; i<args.length; i++) {
                CacheEntryBy entryBy;
                if (Provisioning.isUUID(args[i]))
                    entryBy = CacheEntryBy.id;
                else
                    entryBy = CacheEntryBy.name;

                entries[i-argIdx] = new CacheEntry(entryBy, args[i]);
            }
        }

        SoapProvisioning sp = (SoapProvisioning)mProv;
        sp.flushCache(type, entries, allServers);

    }

    private void doGenerateDomainPreAuthKey(String[] args) throws ServiceException {
        String key = null;
        boolean force = false;
        if (args.length == 3) {
            if (args[1].equals("-f"))
                force = true;
            else  {
                usage();
                return;
            }
            key = args[2];
        } else {
            key = args[1];
        }

        Domain domain = lookupDomain(key);
        String curPreAuthKey = domain.getAttr(Provisioning.A_zimbraPreAuthKey);
        if (curPreAuthKey != null && !force)
            throw ServiceException.INVALID_REQUEST("pre auth key exists for domain " + key + ", use command -f option to force overwriting the existing key", null);

        String preAuthKey = PreAuthKey.generateRandomPreAuthKey();
        HashMap<String,String> attrs = new HashMap<String,String>();
        attrs.put(Provisioning.A_zimbraPreAuthKey, preAuthKey);
        mProv.modifyAttrs(domain, attrs);
        console.printf("preAuthKey: %s\n", preAuthKey);
        if (curPreAuthKey != null)
            console.printf("previous preAuthKey: %s\n", curPreAuthKey);
    }

    private void doGenerateDomainPreAuth(String[] args) throws ServiceException {
        String key = args[1];
        Domain domain = lookupDomain(key);
        String preAuthKey = domain.getAttr(Provisioning.A_zimbraPreAuthKey, null);
        if (preAuthKey == null)
            throw ServiceException.INVALID_REQUEST("domain not configured for preauth", null);

        String name = args[2];
        String by = args[3];
        long timestamp = Long.parseLong(args[4]);
        if (timestamp == 0) timestamp = System.currentTimeMillis();
        long expires = Long.parseLong(args[5]);
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("account", name);
        params.put("by", by);
        params.put("timestamp", timestamp+"");
        params.put("expires", expires+"");
        if (args.length == 7) params.put("admin", args[6]);
        console.printf("account: %s\nby: %s\ntimestamp: %s\nexpires: %s\npreauth: %s\n", name, by, timestamp, expires,PreAuthKey.computePreAuth(params, preAuthKey));
    }

    private void doGetAllMtaAuthURLs(String[] args) throws ServiceException {
        List<Server> servers = mProv.getAllServers();
        for (Server server : servers ) {
            boolean isTarget = server.getBooleanAttr(Provisioning.A_zimbraMtaAuthTarget, false);
            if (isTarget) {
                console.print(URLUtil.getAdminURL(server) + " ");
            }
        }
        console.println();
    }

    private void doGetAllReverseProxyURLs(String[] args) throws ServiceException {
        // String REVERSE_PROXY_PROTO = "http://";
        String REVERSE_PROXY_PROTO = "";  // don't need proto for nginx.conf
        int REVERSE_PROXY_PORT = 7072;
        // String REVERSE_PROXY_PATH = "/service/extension/nginx-lookup";
        String REVERSE_PROXY_PATH = ExtensionDispatcherServlet.EXTENSION_PATH + "/nginx-lookup";

        List<Server> servers = mProv.getAllServers();
        for (Server server : servers ) {
            boolean isTarget = server.getBooleanAttr(Provisioning.A_zimbraReverseProxyLookupTarget, false);
            if (isTarget) {
                String serviceName = server.getAttr(Provisioning.A_zimbraServiceHostname, "");
                console.print(REVERSE_PROXY_PROTO + serviceName + ":" + REVERSE_PROXY_PORT + REVERSE_PROXY_PATH + " ");
            }
        }
        console.println();
    }

    private void doGetAllReverseProxyBackends(String[] args) throws ServiceException {
        List<Server> servers = mProv.getAllServers();
        boolean atLeastOne = false;
        for (Server server : servers) {
            boolean isTarget = server.getBooleanAttr(Provisioning.A_zimbraReverseProxyLookupTarget, false);
            if (!isTarget) {
                continue;
            }

            // (For now) assume HTTP can be load balanced to...
            String mode = server.getAttr(Provisioning.A_zimbraMailMode, null);
            if (mode == null) {
                continue;
            }
            MailMode mailMode = Provisioning.MailMode.fromString(mode);

            boolean isPlain = (mailMode == Provisioning.MailMode.http ||
                               mailMode == Provisioning.MailMode.mixed ||
                               mailMode == Provisioning.MailMode.both);
            if (!isPlain) {
                continue;
            }

            int backendPort = server.getIntAttr(Provisioning.A_zimbraMailPort, 0);
            String serviceName = server.getAttr(Provisioning.A_zimbraServiceHostname, "");
            console.println("    server " + serviceName + ":" + backendPort + ";");
            atLeastOne = true;
        }

        if (!atLeastOne) {
            // workaround zmmtaconfig not being able to deal with empty output
            console.println("    server localhost:8080;");
        }
    }

    private void doGetAllReverseProxyDomains(String[] args) throws ServiceException {

        if (!(mProv instanceof LdapProvisioning))
            throwLdapOnly();

        NamedEntry.Visitor visitor = new NamedEntry.Visitor() {
            public void visit(NamedEntry entry) throws ServiceException {
                if (entry.getAttr(Provisioning.A_zimbraVirtualHostname) != null &&
                    entry.getAttr(Provisioning.A_zimbraSSLPrivateKey) != null &&
                    entry.getAttr(Provisioning.A_zimbraSSLCertificate) != null) {
                    StringBuilder virtualHosts = new StringBuilder();
                    for (String vh : entry.getMultiAttr(Provisioning.A_zimbraVirtualHostname))
                        virtualHosts.append(vh + " ");
                    console.println(entry.getName() + " " + virtualHosts);
                }
            }
        };

        mProv.getAllDomains(visitor, new String[]{Provisioning.A_zimbraVirtualHostname,
                                                  Provisioning.A_zimbraSSLPrivateKey,
                                                  Provisioning.A_zimbraSSLCertificate});
    }

    private void doGetAllMemcachedServers(String[] args) throws ServiceException {
        List<Server> servers = mProv.getAllServers(Provisioning.SERVICE_MEMCACHED);
        for (Server server : servers ) {
            console.print(server.getAttr(Provisioning.A_zimbraServiceHostname, "") + ":" +
                             server.getAttr(Provisioning.A_zimbraMemcachedBindPort, "") + " ");
        }
        console.println();
    }

    private List<Pair<String /* hostname */, Integer /* port */>> getMailboxServersFromArgs(String[] args)
    throws ServiceException {
        List<Pair<String, Integer>> entries = new ArrayList<Pair<String, Integer>>();
        if (args.length == 2 && "all".equalsIgnoreCase(args[1])) {
            // Get all mailbox servers.
            List<Server> servers = mProv.getAllServers(Provisioning.SERVICE_MAILBOX);
            for (Server svr : servers) {
                String host = svr.getAttr(Provisioning.A_zimbraServiceHostname);
                int port = (int) svr.getLongAttr(Provisioning.A_zimbraAdminPort, (long) mPort);
                Pair<String, Integer> entry = new Pair<String, Integer>(host, port);
                entries.add(entry);
            }
        } else {
            // Only named servers.
            for (int i = 1; i < args.length; ++i) {
                String arg = args[i];
                if (mServer.equalsIgnoreCase(arg)) {
                    entries.add(new Pair<String, Integer>(mServer, mPort));
                } else {
                    Server svr = mProv.getServerByServiceHostname(arg);
                    if (svr == null)
                        throw AccountServiceException.NO_SUCH_SERVER(arg);
                    // TODO: Verify svr has mailbox service enabled.
                    int port = (int) svr.getLongAttr(Provisioning.A_zimbraAdminPort, (long) mPort);
                    entries.add(new Pair<String, Integer>(arg, port));
                }
            }
        }
        return entries;
    }

    private void doReloadMemcachedClientConfig(String[] args) throws ServiceException {
        List<Pair<String, Integer>> servers = getMailboxServersFromArgs(args);
        // Send command to each server.
        for (Pair<String, Integer> server : servers) {
            String hostname = server.getFirst();
            int port = server.getSecond();
            if (mVerbose)
                console.print("Updating " + hostname + " ... ");
            boolean success = false;
            try {
                SoapProvisioning sp = new SoapProvisioning();
                sp.soapSetURI(LC.zimbra_admin_service_scheme.value() + hostname + ":" + port + AdminConstants.ADMIN_SERVICE_URI);
                if (mDebug != SoapDebugLevel.none)
                    sp.soapSetHttpTransportDebugListener(this);
                if (mAccount != null && mPassword != null)
                    sp.soapAdminAuthenticate(mAccount, mPassword);
                else if (mAuthToken != null)
                    sp.soapAdminAuthenticate(mAuthToken);
                else
                    sp.soapZimbraAdminAuthenticate();
                sp.reloadMemcachedClientConfig();
                success = true;
            } catch (ServiceException e) {
                if (mVerbose) {
                    console.println("fail");
                    e.printStackTrace(console);
                } else {
                    console.println("Error updating " + hostname + ": " + e.getMessage());
                }
            } finally {
                if (mVerbose && success)
                    console.println("ok");
            }
        }
    }

    private void doGetMemcachedClientConfig(String[] args) throws ServiceException {
        List<Pair<String, Integer>> servers = getMailboxServersFromArgs(args);
        // Send command to each server.
        int longestHostname = 0;
        for (Pair<String, Integer> server : servers) {
            String hostname = server.getFirst();
            longestHostname = Math.max(longestHostname, hostname.length());
        }
        String hostnameFormat = String.format("%%-%ds", longestHostname);
        boolean consistent = true;
        String prevConf = null;
        for (Pair<String, Integer> server : servers) {
            String hostname = server.getFirst();
            int port = server.getSecond();
            try {
                SoapProvisioning sp = new SoapProvisioning();
                sp.soapSetURI(LC.zimbra_admin_service_scheme.value() + hostname + ":" + port + AdminConstants.ADMIN_SERVICE_URI);
                if (mDebug != SoapDebugLevel.none)
                    sp.soapSetHttpTransportDebugListener(this);
                if (mAccount != null && mPassword != null)
                    sp.soapAdminAuthenticate(mAccount, mPassword);
                else if (mAuthToken != null)
                    sp.soapAdminAuthenticate(mAuthToken);
                else
                    sp.soapZimbraAdminAuthenticate();
                MemcachedClientConfig config = sp.getMemcachedClientConfig();
                String serverList = config.serverList != null ? config.serverList : "none";
                if (mVerbose) {
                    console.printf(hostnameFormat + " => serverList=[%s], hashAlgo=%s, binaryProto=%s, expiry=%ds, timeout=%dms\n",
                                      hostname, serverList, config.hashAlgorithm,
                                      config.binaryProtocol, config.defaultExpirySeconds, config.defaultTimeoutMillis);
                } else if (config.serverList != null) {
                    if (HashAlgorithm.KETAMA_HASH.toString().equals(config.hashAlgorithm)) {
                        // Don't print the default hash algorithm to keep the output clutter-free.
                        console.printf(hostnameFormat + " => %s\n", hostname, serverList);
                    } else {
                        console.printf(hostnameFormat + " => %s (%S)\n", hostname, serverList, config.hashAlgorithm);
                    }
                } else {
                    console.printf(hostnameFormat + " => none\n", hostname);
                }

                String listAndAlgo = serverList + "/" + config.hashAlgorithm;
                if (prevConf == null) {
                    prevConf = listAndAlgo;
                } else if (!prevConf.equals(listAndAlgo)) {
                    consistent = false;
                }
            } catch (ServiceException e) {
                console.printf(hostnameFormat + " => ERROR: unable to get configuration\n", hostname);
                if (mVerbose)
                    e.printStackTrace(console);
            }
        }
        if (!consistent) {
            console.println("Inconsistency detected!");
        }
    }

    private void doGetServer(String[] args) throws ServiceException {
        boolean applyDefault = true;

        int i = 1;
        while (i < args.length) {
            String arg = args[i];
            if (arg.equals("-e"))
                applyDefault = false;
            else
                break;
            i++;
        }
        if (i >= args.length) {
            usage();
            return;
        }
        dumpServer(lookupServer(args[i], applyDefault), applyDefault, getArgNameSet(args, i+1));
    }

    private void doPurgeAccountCalendarCache(String[] args) throws ServiceException {
        if (!(mProv instanceof SoapProvisioning))
            throwSoapOnly();
        if (args.length > 1) {
            for (int i = 1; i < args.length; i++) {
                Account acct = lookupAccount(args[i], true);
                mProv.purgeAccountCalendarCache(acct.getId());
            }
        }
    }

    private void doCreateXMPPComponent(String[] args) throws ServiceException, ArgException {
        //4 = class
        //5 = category
        //6 = type
        Map<String,Object> map = getMapAndCheck(args, 7);
        map.put(Provisioning.A_zimbraXMPPComponentClassName, args[4]);
        map.put(Provisioning.A_zimbraXMPPComponentCategory, args[5]);
        map.put(Provisioning.A_zimbraXMPPComponentType, args[6]);
        Domain d = lookupDomain(args[2]);
        String routableName = args[1]+"."+d.getName();
        console.println(mProv.createXMPPComponent(routableName, lookupDomain(args[2]), lookupServer(args[3]), map));
    }

    private void doGetXMPPComponent(String[] args) throws ServiceException {
        dumpXMPPComponent(lookupXMPPComponent(args[1]), getArgNameSet(args, 2));
    }

    static private class RightArgs {
        String mTargetType;
        String mTargetIdOrName;
        String mGranteeType;
        String mGranteeIdOrName;
        String mSecret;
        String mRight;
        RightModifier mRightModifier;

        String[] mArgs;
        int mCurPos = 1;

        RightArgs(String[] args) {
            mArgs = args;
            mCurPos = 1;
        }

        String getNextArg() throws ServiceException {
            if (hasNext())
                return mArgs[mCurPos++];
            else
                throw ServiceException.INVALID_REQUEST("not enough number of arguments", null);
        }

        boolean hasNext() {
            return (mCurPos < mArgs.length);
        }
    }

    private void getRightArgsTarget(RightArgs ra) throws ServiceException, ArgException {
        if (ra.mCurPos >= ra.mArgs.length) throw new ArgException("not enough number of arguments");
        ra.mTargetType = ra.mArgs[ra.mCurPos++];
        TargetType tt = TargetType.fromCode(ra.mTargetType);
        if (tt.needsTargetIdentity()) {
            if (ra.mCurPos >= ra.mArgs.length) throw new ArgException("not enough number of arguments");
            ra.mTargetIdOrName = ra.mArgs[ra.mCurPos++];
        } else
            ra.mTargetIdOrName = null;
    }

    private void getRightArgsGrantee(RightArgs ra, boolean needGranteeType, boolean needSecret) throws ServiceException, ArgException {
        if (ra.mCurPos >= ra.mArgs.length) throw new ArgException("not enough number of arguments");

        GranteeType gt = null;
        if (needGranteeType) {
            ra.mGranteeType = ra.mArgs[ra.mCurPos++];
            gt = GranteeType.fromCode(ra.mGranteeType);
        } else
            ra.mGranteeType = null;

        if (gt == GranteeType.GT_AUTHUSER || gt == GranteeType.GT_PUBLIC)
            return;

        if (ra.mCurPos >= ra.mArgs.length) throw new ArgException("not enough number of arguments");
        ra.mGranteeIdOrName = ra.mArgs[ra.mCurPos++];

        if (needSecret && gt != null) {
            if (gt.allowSecret()) {
                if (ra.mCurPos >= ra.mArgs.length) throw new ArgException("not enough number of arguments");
                ra.mSecret = ra.mArgs[ra.mCurPos++];
            }
        }
    }

    private void getRightArgsRight(RightArgs ra) throws ServiceException, ArgException {
        if (ra.mCurPos >= ra.mArgs.length) throw new ArgException("not enough number of arguments");

        ra.mRight = ra.mArgs[ra.mCurPos++];
        ra.mRightModifier = RightModifier.fromChar(ra.mRight.charAt(0));
        if (ra.mRightModifier != null)
            ra.mRight = ra.mRight.substring(1);
    }

    private void getRightArgs(RightArgs ra, boolean needGranteeType, boolean needSecret) throws ServiceException, ArgException {
        getRightArgsTarget(ra);
        getRightArgsGrantee(ra, needGranteeType, needSecret);
        getRightArgsRight(ra);
    }

    private void doCheckRight(String[] args) throws ServiceException, ArgException {
        RightArgs ra = new RightArgs(args);
        getRightArgs(ra, false, false); // todo, handle secret

        Map<String, Object> attrs = getMap(args, ra.mCurPos);

        TargetBy targetBy = (ra.mTargetIdOrName == null) ? null : guessTargetBy(ra.mTargetIdOrName);
        GranteeBy granteeBy = guessGranteeBy(ra.mGranteeIdOrName);

        AccessManager.ViaGrant via = new AccessManager.ViaGrant();
        boolean allow = mProv.checkRight(ra.mTargetType, targetBy, ra.mTargetIdOrName,
                                         granteeBy, ra.mGranteeIdOrName,
                                         ra.mRight, attrs, via);

        console.println(allow? "ALLOWED" : "DENIED");
        if (via.available()) {
            console.println("Via:");
            console.println("    target type  : " + via.getTargetType());
            console.println("    target       : " + via.getTargetName());
            console.println("    grantee type : " + via.getGranteeType());
            console.println("    grantee      : " + via.getGranteeName());
            console.println("    right        : " + (via.isNegativeGrant()?"DENY ":"") + via.getRight());
            console.println();
        }
    }

    private void doGetAllEffectiveRights(String[] args) throws ServiceException, ArgException {
        RightArgs ra = new RightArgs(args);

        if (mProv instanceof LdapProvisioning) {
            // must provide grantee info
            getRightArgsGrantee(ra, true, false);
        } else {
            // has more args, use it for the requested grantee
            if (ra.mCurPos < args.length)
                getRightArgsGrantee(ra, true, false);
        }

        boolean expandSetAttrs = false;
        boolean expandGetAttrs = false;

        // if there are more args, see if they are expandSetAttrs/expandGetAttrs
        for (int i= ra.mCurPos; i < args.length; i++) {
            if ("expandSetAttrs".equals(args[i]))
                expandSetAttrs = true;
            else if ("expandGetAttrs".equals(args[i]))
                expandGetAttrs = true;
            else
                throw new ArgException("unrecognized arg: " + args[i]);
        }

        GranteeBy granteeBy = (ra.mGranteeIdOrName == null)? null: guessGranteeBy(ra.mGranteeIdOrName);

        RightCommand.AllEffectiveRights allEffRights = mProv.getAllEffectiveRights(
                ra.mGranteeType, granteeBy, ra.mGranteeIdOrName, expandSetAttrs, expandGetAttrs);

        console.println(allEffRights.granteeType() + " " +
                allEffRights.granteeName() + "(" + allEffRights.granteeId() + ")" +
                " has the following rights:");

        for (Map.Entry<TargetType, RightCommand.RightsByTargetType> rightsByTargetType : allEffRights.rightsByTargetType().entrySet()) {
            RightCommand.RightsByTargetType rbtt = rightsByTargetType.getValue();
            if (!rbtt.hasNoRight())
                dumpRightsByTargetType(rightsByTargetType.getKey(), rbtt, expandSetAttrs, expandGetAttrs);
        }
    }

    private void dumpRightsByTargetType(TargetType targetType, RightCommand.RightsByTargetType rbtt,
            boolean expandSetAttrs, boolean expandGetAttrs) {
        console.println("------------------------------------------------------------------");
        console.println("Target type: " + targetType.getCode());
        console.println("------------------------------------------------------------------");

        RightCommand.EffectiveRights er = rbtt.all();
        if (er != null) {
            console.println("On all " + targetType.getPrettyName() + " entries");
            dumpEffectiveRight(er, expandSetAttrs, expandGetAttrs);
        }

        if (rbtt instanceof RightCommand.DomainedRightsByTargetType) {
            RightCommand.DomainedRightsByTargetType domainedRights = (RightCommand.DomainedRightsByTargetType)rbtt;

            for (RightCommand.RightAggregation rightsByDomains : domainedRights.domains()) {
                dumpRightAggregation(targetType, rightsByDomains, true, expandSetAttrs, expandGetAttrs);
            }
        }

        for (RightCommand.RightAggregation rightsByEntries : rbtt.entries()) {
            dumpRightAggregation(targetType, rightsByEntries, false, expandSetAttrs, expandGetAttrs);
        }
    }

    private void dumpRightAggregation(TargetType targetType,
            RightCommand.RightAggregation rightAggr, boolean domainScope,
            boolean expandSetAttrs, boolean expandGetAttrs) {
        Set<String> entries = rightAggr.entries();
        RightCommand.EffectiveRights er = rightAggr.effectiveRights();

        for (String entry : entries) {
            if (domainScope)
                console.println("On " + targetType.getCode() + " entries in domain " + entry);
            else
                console.println("On " + targetType.getCode() + " " + entry);
        }
        dumpEffectiveRight(er, expandSetAttrs, expandGetAttrs);
    }

    private void doGetEffectiveRights(String[] args) throws ServiceException, ArgException {
        RightArgs ra = new RightArgs(args);
        getRightArgsTarget(ra);

        if (mProv instanceof LdapProvisioning) {
            // must provide grantee info
            getRightArgsGrantee(ra, false, false);
        } else {
            // has more args, use it for the requested grantee
            if (ra.mCurPos < args.length)
                getRightArgsGrantee(ra, false, false);
        }

        boolean expandSetAttrs = false;
        boolean expandGetAttrs = false;

        // if there are more args, see if they are expandSetAttrs/expandGetAttrs
        for (int i= ra.mCurPos; i < args.length; i++) {
            if ("expandSetAttrs".equals(args[i]))
                expandSetAttrs = true;
            else if ("expandGetAttrs".equals(args[i]))
                expandGetAttrs = true;
            else
                throw new ArgException("unrecognized arg: " + args[i]);
        }

        TargetBy targetBy = (ra.mTargetIdOrName == null) ? null : guessTargetBy(ra.mTargetIdOrName);
        GranteeBy granteeBy = (ra.mGranteeIdOrName == null)? null: guessGranteeBy(ra.mGranteeIdOrName);

        RightCommand.EffectiveRights effRights = mProv.getEffectiveRights(ra.mTargetType, targetBy, ra.mTargetIdOrName,
                                                                          granteeBy, ra.mGranteeIdOrName, expandSetAttrs, expandGetAttrs);

        console.println("Account " + effRights.granteeName() + " has the following rights on target " + effRights.targetType() + " " + effRights.targetName());
        dumpEffectiveRight(effRights, expandSetAttrs, expandGetAttrs);
    }

    private void dumpEffectiveRight(RightCommand.EffectiveRights effRights, boolean expandSetAttrs, boolean expandGetAttrs) {

        List<String> presetRights = effRights.presetRights();
        if (presetRights != null && presetRights.size() > 0) {
            console.println("================");
            console.println("Preset rights");
            console.println("================");
            for (String r : presetRights)
                console.println("    " + r);
        }

        displayAttrs("set", expandSetAttrs, effRights.canSetAllAttrs(), effRights.canSetAttrs());
        displayAttrs("get", expandGetAttrs, effRights.canGetAllAttrs(), effRights.canGetAttrs());

        console.println();
        console.println();
    }

    private void displayAttrs(String op, boolean expandAll, boolean allAttrs, SortedMap<String, RightCommand.EffectiveAttr> attrs) {
        if (!allAttrs && attrs.size()==0)
            return;

        String format = "    %-50s %-30s\n";
        console.println();
        console.println("=========================");
        console.println(op + " attributes rights");
        console.println("=========================");
        if (allAttrs)
            console.println("Can " + op + " all attributes");

        if (!allAttrs || expandAll) {
            console.println("Can " + op + " the following attributes");
            console.println("--------------------------------");
            console.printf(format, "attribute", "default");
            console.printf(format, "----------------------------------------", "--------------------");
            for (RightCommand.EffectiveAttr ea : attrs.values()) {
                boolean first = true;
                if (ea.getDefault().isEmpty()) {
                    console.printf(format, ea.getAttrName(), "");
                } else {
                    for (String v: ea.getDefault()) {
                        if (first) {
                            console.printf(format, ea.getAttrName(), v);
                            first = false;
                        } else
                            console.printf(format, "", v);
                    }
                }
            }
        }
    }

    /*
     * for testing only, not used in production
     */
    private void doGetCreateObjectAttrs(String[] args) throws ServiceException {
        String targetType = args[1];

        DomainBy domainBy = null;
        String domain = null;
        if (!args[2].equals("null")) {
            domainBy = guessDomainBy(args[2]);
            domain = args[2];
        }

        CosBy cosBy = null;
        String cos = null;
        if (!args[3].equals("null")) {
            cosBy = guessCosBy(args[3]);
            cos = args[3];
        }

        GranteeBy granteeBy = null;
        String grantee = null;

        /*
         * take grantee arg only if LdapProvisioning
         * for SoapProvisioning, -a {admin account} -p {password} is required with zmprov
         */
        if (mProv instanceof LdapProvisioning) {
            granteeBy = guessGranteeBy(args[4]);
            grantee = args[4];
        }

        console.println("Domain:  " + domain);
        console.println("Cos:     " + cos);
        console.println("Grantee: " + grantee);
        console.println();

        RightCommand.EffectiveRights effRights = mProv.getCreateObjectAttrs(targetType,
                                                                            domainBy, domain,
                                                                            cosBy, cos,
                                                                            granteeBy, grantee);

        displayAttrs("set", true, effRights.canSetAllAttrs(), effRights.canSetAttrs());
    }

    private void doGetGrants(String[] args) throws ServiceException, ArgException {
        RightArgs ra = new RightArgs(args);

        boolean granteeIncludeGroupsGranteeBelongs = true;

        while (ra.hasNext()) {
            String arg = ra.getNextArg();
            if ("-t".equals(arg))
                getRightArgsTarget(ra);
            else if ("-g".equals(arg)) {
                getRightArgsGrantee(ra, true, false);
                if (ra.hasNext()) {
                    String includeGroups = ra.getNextArg();
                    if ("1".equals(includeGroups))
                        granteeIncludeGroupsGranteeBelongs = true;
                    else if ("0".equals(includeGroups))
                        granteeIncludeGroupsGranteeBelongs = false;
                    else
                        throw ServiceException.INVALID_REQUEST("invalid value for the include group flag, must be 0 or 1", null);
                }
            }
        }

        TargetBy targetBy = (ra.mTargetIdOrName == null) ? null : guessTargetBy(ra.mTargetIdOrName);
        GranteeBy granteeBy = (ra.mGranteeIdOrName == null) ? null : guessGranteeBy(ra.mGranteeIdOrName);

        RightCommand.Grants grants = mProv.getGrants(ra.mTargetType, targetBy, ra.mTargetIdOrName,
                ra.mGranteeType, granteeBy, ra.mGranteeIdOrName,
                granteeIncludeGroupsGranteeBelongs);

        String format = "%-12.12s %-36.36s %-30.30s %-12.12s %-36.36s %-30.30s %s\n";
        console.printf(format, "target type", "target id", "target name", "grantee type", "grantee id", "grantee name", "right");
        console.printf(format,
                "------------",
                "------------------------------------",
                "------------------------------",
                "------------",
                "------------------------------------",
                "------------------------------",
                "--------------------");

        for (RightCommand.ACE ace : grants.getACEs()) {
            // String deny = ace.deny()?"-":"";
            RightModifier rightModifier = ace.rightModifier();
            String rm = (rightModifier==null)?"":String.valueOf(rightModifier.getModifier());
            console.printf(format,
                              ace.targetType(),
                              ace.targetId(),
                              ace.targetName(),
                              ace.granteeType(),
                              ace.granteeId(),
                              ace.granteeName(),
                              rm + ace.right());
        }
        console.println();
    }

    private void doGrantRight(String[] args) throws ServiceException, ArgException {
        RightArgs ra = new RightArgs(args);
        getRightArgs(ra, true, true);

        TargetBy targetBy = (ra.mTargetIdOrName == null) ? null : guessTargetBy(ra.mTargetIdOrName);
        GranteeBy granteeBy = (ra.mGranteeIdOrName == null)? null : guessGranteeBy(ra.mGranteeIdOrName);

        mProv.grantRight(ra.mTargetType, targetBy, ra.mTargetIdOrName,
                         ra.mGranteeType, granteeBy, ra.mGranteeIdOrName, ra.mSecret,
                         ra.mRight, ra.mRightModifier);
    }

    private void doRevokeRight(String[] args) throws ServiceException, ArgException {
        RightArgs ra = new RightArgs(args);
        getRightArgs(ra, true, false);

        TargetBy targetBy = (ra.mTargetIdOrName == null) ? null : guessTargetBy(ra.mTargetIdOrName);
        GranteeBy granteeBy = (ra.mGranteeIdOrName == null)? null : guessGranteeBy(ra.mGranteeIdOrName);

        mProv.revokeRight(ra.mTargetType, targetBy, ra.mTargetIdOrName,
                          ra.mGranteeType, granteeBy, ra.mGranteeIdOrName,
                          ra.mRight, ra.mRightModifier);
    }

    private void doGetAuthTokenInfo(String[] args) {
        String authToken = args[1];

        try {
            Map attrs = AuthToken.getInfo(authToken);
            List keys = new ArrayList(attrs.keySet());
            Collections.sort(keys);

            for (Object k : keys) {
                String key = k.toString();
                String value = attrs.get(k).toString();

                if ("exp".equals(key)) {
                    long exp = Long.parseLong(value);
                    console.format("%s: %s (%s)\n", key, value, DateUtil.toRFC822Date(new Date(exp)));
                } else
                    console.format("%s: %s\n", key, value);
            }
        } catch (AuthTokenException e) {
            console.println("Unable to parse auth token: " + e.getMessage());
        }

        console.println();

    }

    private void doGetAllFreeBusyProviders() throws ServiceException, IOException {
        FbCli fbcli = new FbCli();
        for (FbCli.FbProvider fbprov : fbcli.getAllFreeBusyProviders())
            console.println(fbprov.toString());
    }
    
    private void doGetFreeBusyQueueInfo(String[] args) throws ServiceException, IOException {
        FbCli fbcli = new FbCli();
        String name = null;
        if (args.length > 1)
            name = args[1];
        for (FbCli.FbQueue fbqueue : fbcli.getFreeBusyQueueInfo(name))
            console.println(fbqueue.toString());
    }
    
    private void doPushFreeBusy(String[] args) throws ServiceException, IOException {
        FbCli fbcli = new FbCli();
        HashMap<String,HashSet<String>> accountMap = new HashMap<String,HashSet<String>>();
        for (int i = 1; i < args.length; i++) {
            String acct = args[i];
            Account account = mProv.getAccountById(acct);
            if (account == null)
                throw AccountServiceException.NO_SUCH_ACCOUNT(acct);
            String host = account.getMailHost();
            HashSet<String> accountSet = accountMap.get(host);
            if (accountSet == null) {
                accountSet = new HashSet<String>();
                accountMap.put(host, accountSet);
            }
            accountSet.add(acct);
        }
        for (String host : accountMap.keySet()) {
            console.println("pushing to server " + host);
            fbcli.setServer(host);
            fbcli.pushFreeBusyForAccounts(accountMap.get(host));
        }
    }
    
    private void doPushFreeBusyForDomain(String[] args) throws ServiceException, IOException {
        lookupDomain(args[1]);
        FbCli fbcli = new FbCli();
        for (Server server : mProv.getAllServers(Provisioning.SERVICE_MAILBOX)) {
            console.println("pushing to server " + server.getName());
            fbcli.setServer(server.getName());
            fbcli.pushFreeBusyForDomain(args[1]);
        }
    }

    private void doPurgeFreeBusyQueue(String[] args) throws ServiceException, IOException {
        String provider = null;
        if (args.length > 1)
            provider = args[1];
        FbCli fbcli = new FbCli();
        fbcli.purgeFreeBusyQueue(provider);
    }
    
    private void dumpSMIMEConfigs(Map<String, Map<String, Object>> smimeConfigs) throws ServiceException {
        for (Map.Entry<String, Map<String, Object>> smimeConfig : smimeConfigs.entrySet()) {
            String configName = smimeConfig.getKey();
            Map<String, Object> configAttrs = smimeConfig.getValue();
            
            console.println("# name "+ configName);
            dumpAttrs(configAttrs, null);
            console.println();
        }
    }
    
    private void doGetConfigSMIMEConfig(String[] args) throws ServiceException {
        String configName = null;
        if (args.length > 1) {
            configName = args[1];
        }
        
        Map<String, Map<String, Object>> smimeConfigs = mProv.getConfigSMIMEConfig(configName);
        dumpSMIMEConfigs(smimeConfigs);
    }
    
    private void doGetDomainSMIMEConfig(String[] args) throws ServiceException {
        String domainName = args[1];
        Domain domain = lookupDomain(domainName);
        
        String configName = null;
        if (args.length > 2) {
            configName = args[2];
        }
        
        Map<String, Map<String, Object>> smimeConfigs = mProv.getDomainSMIMEConfig(domain, configName);
        dumpSMIMEConfigs(smimeConfigs);
    }
    
    private void doModifyConfigSMIMEConfig(String[] args) throws ServiceException, ArgException {
        String configName = args[1];
        mProv.modifyConfigSMIMEConfig(configName, getMapAndCheck(args, 2));
    }
    
    private void doModifyDomainSMIMEConfig(String[] args) throws ServiceException, ArgException {
        String domainName = args[1];
        Domain domain = lookupDomain(domainName);
        
        String configName = args[2];
        mProv.modifyDomainSMIMEConfig(domain, configName, getMapAndCheck(args, 3));
    }
    
    private void doRemoveConfigSMIMEConfig(String[] args) throws ServiceException {
        String configName = null;
        if (args.length > 1) {
            configName = args[1];
        }
        
        mProv.removeConfigSMIMEConfig(configName);
    }
    
    private void doRemoveDomainSMIMEConfig(String[] args) throws ServiceException {
        String domainName = args[1];
        Domain domain = lookupDomain(domainName);
        
        String configName = null;
        if (args.length > 2) {
            configName = args[2];
        }
        
        mProv.removeDomainSMIMEConfig(domain, configName);
    }

    private void doHelp(String[] args) {
        Category cat = null;
        if (args != null && args.length >= 2) {
            String s = args[1].toUpperCase();
            try {
                cat = Category.valueOf(s);
            } catch (IllegalArgumentException e) {
                for (Category c : Category.values()) {
                    if (c.name().startsWith(s)) {
                        cat = c;
                        break;
                    }
                }
            }
        }

        if (args == null || args.length == 1 || cat == null) {
            console.println(" zmprov is used for provisioning. Try:");
            console.println("");
            for (Category c: Category.values()) {
                console.printf("     zmprov help %-15s %s\n", c.name().toLowerCase(), c.getDescription());
            }

        }

        if (cat != null) {
            console.println("");
            for (Command c : Command.values()) {
                if (!c.hasHelp()) continue;
                if (cat == Category.COMMANDS || cat == c.getCategory()) {
                    Command.Via via = c.getVia();
                    /*
                    if (via == null ||
                        (via == Command.Via.ldap && mUseLdap) ||
                        (via == Command.Via.soap && !mUseLdap))
                        console.printf("  %s(%s) %s\n\n", c.getName(), c.getAlias(), c.getHelp());
                    */
                    console.printf("  %s(%s) %s\n", c.getName(), c.getAlias(), c.getHelp());
                    if (via == Command.Via.ldap)
                        console.printf("    -- NOTE: %s can only be used with \"zmprov -l/--ldap\"\n", c.getName());
                    console.printf("\n");
                }
            }

            Category.help(cat);
        }
        console.println();
    }

    private long mSendStart;

    @Override
    public void receiveSoapMessage(PostMethod postMethod, Element envelope) {
        console.printf("======== SOAP RECEIVE =========\n");

        if (mDebug == SoapDebugLevel.high) {
            Header[] headers = postMethod.getResponseHeaders();
            for (Header header : headers) {
                console.println(header.toString().trim()); // trim the ending crlf
            }
            console.println();
        }

        long end = System.currentTimeMillis();
        console.println(envelope.prettyPrint());
        console.printf("=============================== (%d msecs)\n", end-mSendStart);
    }

    @Override
    public void sendSoapMessage(PostMethod postMethod, Element envelope) {
        console.println("========== SOAP SEND ==========");

        if (mDebug == SoapDebugLevel.high) {
            Header[] headers = postMethod.getRequestHeaders();
            for (Header header : headers) {
                console.println(header.toString().trim()); // trim the ending crlf
            }
            console.println();
        }

        mSendStart = System.currentTimeMillis();

        console.println(envelope.prettyPrint());
        console.println("===============================");
    }

    void throwSoapOnly() throws ServiceException {
        throw ServiceException.INVALID_REQUEST(ERR_VIA_SOAP_ONLY, null);
    }

    void throwLdapOnly() throws ServiceException {
        throw ServiceException.INVALID_REQUEST(ERR_VIA_LDAP_ONLY, null);
    }

    private void loadLdapSchemaExtensionAttrs() {
        if (mProv instanceof LdapProvisioning)
            AttributeManager.loadLdapSchemaExtensionAttrs((LdapProvisioning)mProv);
    }

}

