using CssLib;
using MVVM.Model;
using Misc;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Collections;
using System.ComponentModel;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Windows.Input;
using System.Windows;
using System;

namespace MVVM.ViewModel
{
public class PublicfoldersViewModel: BaseViewModel
{
    readonly Users m_users = new Users("", "", -1);
    readonly Folder m_folders = new Folder();

    public PublicfoldersViewModel(string username, string mappedname)
    {
        this.ObjectPickerCommand = new ActionCommand(this.ObjectPicker, () => true);
        this.PubFoldersCommand = new ActionCommand(this.PublicFolders, () => true);
        this.UserMapCommand = new ActionCommand(this.UserMap, () => true);
        this.AddCommand = new ActionCommand(this.Add, () => true);
        this.RemoveCommand = new ActionCommand(this.Remove, () => true);
        this.SaveCSVCommand = new ActionCommand(this.SaveCSV, () => true);
        this.BackCommand = new ActionCommand(this.Back, () => true);
        this.NextCommand = new ActionCommand(this.Next, () => true);
        this.Username = username;
        this.MappedName = mappedname;
        this.OPInfo = new ObjectPickerInfo("", "", "", "");
        this.IsProvisioned = false;
        this.MustChangePassword = false;
        this.EnablePopButtons = true;
        this.DomainsFilledIn = false;
        this.CsvDelimiter = ",";
        this.m_publicFolderName = username;
        this.PlusEnabled = true;

        
    }

    // a bit of a hack, but with the LDAP Browser now being controlled by the UsersView,
    // we need a way for the view to get to the ScheduleViewModel to set EnableMigrate
    public ScheduleViewModel svm;

    public string ZimbraAccountName
    {
        get { return m_config.ZimbraServer.UserAccount; }
        set
        {
            if (value == m_config.ZimbraServer.UserAccount)
                return;
            m_config.ZimbraServer.UserAccount = value;

            OnPropertyChanged(new PropertyChangedEventArgs("ZimbraAccountName"));
        }
    }        

    // Commands
    public ICommand ObjectPickerCommand {
        get;
        private set;
    }

    // Commands
    public ICommand PubFoldersCommand
    {
        get;
        private set;
    }

    private void PublicFolders()
    {
        EnablePopButtons = false;

        CSMigrationWrapper mw = ((IntroViewModel)ViewModelPtrs[(int)ViewType.INTRO]).mw;
        MigrationAccount Acct = new MigrationAccount();

       /* string userName = (UsersList[0].MappedName.Length > 0) ? UsersList[0].MappedName :
               UsersList[0].Username;
        string acctName = userName + '@' + this.ZimbraDomain;
        Acct.AccountName = acctName;
        Acct.ProfileName = acctName;*/
        ConfigViewModelS eparams =
                   ((ConfigViewModelS)ViewModelPtrs[(int)ViewType.SVRSRC]);
        Acct.ProfileName = eparams.OutlookProfile;

        if (ZimbraAccountName == null)
        {
            MessageBox.Show("Please specify a zimbra account name", "Zimbra Migration", MessageBoxButton.OK,
                            MessageBoxImage.Warning);
            EnableNext = false;
            return;
        }
       

        string acctName = ZimbraAccountName + '@' + this.ZimbraDomain;
        Acct.AccountName = acctName;
        //Acct.ProfileName = acctName;
       

        
        string[] folders = mw.GetPublicFolders( Acct, LogLevel.Trace);




        if (folders != null)
        {
            // FBS rewrite -- bug 71646 -- 3/26/12
            UsersViewModel userm;
            userm = new UsersViewModel(ZimbraAccountName, ZimbraAccountName);

            for (int i = 0; i < folders.Length; i++)
            {
                /*string[] tokens = users[i].Split('~');
                if (tokens.Length < 5)
                {
                    MessageBox.Show("Object picker returned insufficient data", "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Error);
                    EnablePopButtons = true;
                    return;
                }*/
                string uname = "", displayname = "", givenname = "", sn = "", zfp = "";
               /* for (int j = 0; j < tokens.Length; j += 5)
                {
                    uname = tokens.GetValue(j).ToString();
                    displayname = tokens.GetValue(j + 1).ToString();
                    givenname = tokens.GetValue(j + 2).ToString();
                    sn = tokens.GetValue(j + 3).ToString();
                    zfp = tokens.GetValue(j + 4).ToString();
                }*/
                uname = folders.GetValue(i).ToString();
                 displayname = folders.GetValue(i).ToString();
                    givenname = folders.GetValue(i).ToString();
                    sn =folders.GetValue(i).ToString();
                    zfp = folders.GetValue(i).ToString();

                /*if (uname.IndexOf("@") != -1)
                {
                    uname = uname.Substring(0, uname.IndexOf("@"));
                }*/

                PublicfoldersViewModel uvm;
               

                if (uname.CompareTo(displayname) == 0)
                {
                    uvm = new PublicfoldersViewModel(displayname, uname);
                                   }
                else
                {
                    uvm = new PublicfoldersViewModel(uname, uname);
                }

               
               
                //uvm.AddOPInfo(new ObjectPickerInfo(displayname, givenname, sn, zfp));
                uvm.ZimbraAccountName = ZimbraAccountName;
                UsersBKList.Add(uvm);

                UsersList.Add(userm);


                ScheduleViewModel scheduleViewModel =
                    ((ScheduleViewModel)ViewModelPtrs[(int)ViewType.SCHED]);


                PlusEnabled = false;
                //Username = ZimbraAccountName;
                scheduleViewModel.SchedList.Add(new SchedUser(ZimbraAccountName, false));
                scheduleViewModel.SchedfolderList.Add(new SchedUser(ZimbraAccountName, false));
                scheduleViewModel.EnableMigrate = (scheduleViewModel.SchedfolderList.Count > 0);
                scheduleViewModel.EnablePreview = scheduleViewModel.EnableMigrate;
                EnableNext = (UsersList.Count > 0);
            }
            UsersViewModel usersViewModel =
              ((UsersViewModel)ViewModelPtrs[(int)ViewType.USERS]);

            usersViewModel.ZimbraDomain = this.ZimbraDomain;
            
            usersViewModel.UsersList.Add(userm);

        }
        EnablePopButtons = true;
    }
    private void ObjectPicker()
    {
        EnablePopButtons = false;

        CSMigrationWrapper mw = ((IntroViewModel)ViewModelPtrs[(int)ViewType.INTRO]).mw;


        string[] users = mw.GetListFromObjectPicker();
        if(users != null)
        {
        // FBS rewrite -- bug 71646 -- 3/26/12
        for (int i = 0; i < users.Length; i++)
        {
            string[] tokens = users[i].Split('~');
            if (tokens.Length < 5)
            {
                MessageBox.Show("Object picker returned insufficient data", "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Error);
                EnablePopButtons = true;
                return;
            }
            string uname = "", displayname = "", givenname = "", sn = "", zfp = "";
            for (int j = 0; j < tokens.Length; j += 5)
            {
                uname = tokens.GetValue(j).ToString();
                displayname = tokens.GetValue(j + 1).ToString();
                givenname = tokens.GetValue(j + 2).ToString();
                sn = tokens.GetValue(j + 3).ToString();
                zfp = tokens.GetValue(j + 4).ToString();
            }

            if (uname.IndexOf("@") != -1)
            {
                uname = uname.Substring(0, uname.IndexOf("@"));
            }

            PublicfoldersViewModel uvm;

            if (uname.CompareTo(displayname) == 0)
            {
                uvm = new PublicfoldersViewModel(displayname, uname);
            }
            else
            {
                uvm = new PublicfoldersViewModel(uname, uname);
            }
            //uvm.AddOPInfo(new ObjectPickerInfo(displayname, givenname, sn, zfp));
            UsersBKList.Add(uvm);

            ScheduleViewModel scheduleViewModel =
                ((ScheduleViewModel)ViewModelPtrs[(int)ViewType.SCHED]);

            scheduleViewModel.SchedList.Add(new SchedUser(Username, false));
            scheduleViewModel.EnableMigrate = (scheduleViewModel.SchedList.Count > 0);
            scheduleViewModel.EnablePreview = scheduleViewModel.EnableMigrate;
            EnableNext = (UsersList.Count > 0);
        }
    }
        EnablePopButtons = true;
    }
    public ICommand UserMapCommand {
        get;
        private set;
    }
    private void UserMap()
    {
        ScheduleViewModel scheduleViewModel =
            ((ScheduleViewModel)ViewModelPtrs[(int)ViewType.SCHED]);
        bool bCSV = false;

        Microsoft.Win32.OpenFileDialog fDialog = new Microsoft.Win32.OpenFileDialog();
        fDialog.Filter = "User Map Files|*.xml;*.csv";
        fDialog.CheckFileExists = true;
        fDialog.Multiselect = false;

       // string delimiter = ",";
        // /
        // Domain information is stored in the xml and not in  the usermap.
        // will have to revisit
        
        System.Xml.Serialization.XmlSerializer reader =
            new System.Xml.Serialization.XmlSerializer(typeof(Config));
        if (File.Exists(scheduleViewModel.GetConfigFile()))
        {
            System.IO.StreamReader fileRead = new System.IO.StreamReader(
                scheduleViewModel.GetConfigFile());

            Config Z11 = new Config();

            Z11 = (Config)reader.Deserialize(fileRead);
            fileRead.Close();

            CSVDelimiter = Z11.AdvancedImportOptions.CSVDelimiter;
            if (CSVDelimiter == null)
            {
                CSVDelimiter = ",";
            }

            ZimbraDomain = Z11.UserProvision.DestinationDomain;
            if (DomainList.Count > 0)
                CurrentDomainSelection = (ZimbraDomain == null) ? 0 :
                    DomainList.IndexOf(ZimbraDomain);

            else
                DomainList.Add(ZimbraDomain);

        }
        if (fDialog.ShowDialog() == true)
        {
            int lastDot = fDialog.FileName.LastIndexOf(".");

            if (lastDot != -1)
            {
                string substr = fDialog.FileName.Substring(lastDot, 4);

                if (substr == ".csv")
                {
                    bCSV = true;

                    /*try
                     * {
                     *  string names = File.ReadAllText(fDialog.FileName);
                     *  string[] nameTokens = names.Split(',');
                     *  foreach (string name in nameTokens)
                     *  {
                     *      UsersList.Add(name);
                     *      scheduleViewModel.SchedList.Add(name);
                     *  }
                     * }
                     * catch (IOException ex)
                     * {
                     *  MessageBox.Show(ex.Message, "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Exclamation);
                     * }*/
                    List<string[]> parsedData = new List<string[]>();
                    try
                    {
                        if (File.Exists(fDialog.FileName))
                        {
                            using (StreamReader readFile = new StreamReader(fDialog.FileName)) {
                                string line;

                                string[] row;
                                while ((line = readFile.ReadLine()) != null)
                                {
                                    row = line.Split(CSVDelimiter.ToCharArray());
                                    //row = line.Split(',');
                                    parsedData.Add(row);
                                }
                                readFile.Close();
                            }
                        }
                        else
                        {
                            MessageBox.Show(
                                "There is no user information stored.Please enter some user info",
                                "Zimbra Migration", MessageBoxButton.OK,
                                MessageBoxImage.Error);
                        }
                    }
                    catch (Exception e)
                    {
                        string message = e.Message;
                    }
                    // for (int i = 1; i < parsedData.Count; i++)
                    {
                        string[] strres = new string[parsedData.Count];

                        Users tempuser = new Users();

                        try
                        {
                            for (int j = 0; j < parsedData.Count; j++)
                            {
                                bool bFoundSharp = false;
                                strres = parsedData[j];
                                int num = strres.Count();
                                for (int k = 0; k < num; k++)
                                {
                                    if (strres[k].Contains("#"))
                                    {
                                        bFoundSharp = true;
                                        break;
                                    }
                                }
                                if (!bFoundSharp)   // FBS bug 71933 -- 3/21/12
                                {
                                    tempuser.UserName = strres[0];
                                    tempuser.MappedName = strres[1];

                                    tempuser.ChangePWD = Convert.ToBoolean(strres[2]);
                                    // tempuser.PWDdefault = strres[3];
                                    // string result = tempuser.UserName + "," + tempuser.MappedName +"," + tempuser.ChangePWD + "," + tempuser.PWDdefault;
                                    string result = tempuser.Username + CSVDelimiter /*","*/  + tempuser.MappedName;

                                    Username = strres[0];
                                    MappedName = strres[1];
                                    PublicfoldersViewModel uvm = new PublicfoldersViewModel(Username, MappedName);
                                    uvm.MustChangePassword = tempuser.ChangePWD;
                                    UsersBKList.Add(uvm);
                                    scheduleViewModel.SchedList.Add(new SchedUser(Username, false));
                                }
                            }
                        }
                        catch (Exception)
                        {
                            MessageBox.Show("Incorrect .csv file format", "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Error);
                            return;
                        }

                        EnableNext = (UsersList.Count > 0);
                    }
                    scheduleViewModel.EnableMigrate = (scheduleViewModel.SchedList.Count > 0);
                    scheduleViewModel.EnablePreview = scheduleViewModel.EnableMigrate;

                    // /
                    // Domain information is stored in the xml and not in  the usermap.
                    // will have to revisit

                   /* System.Xml.Serialization.XmlSerializer reader =
                        new System.Xml.Serialization.XmlSerializer(typeof (Config));
                    if (File.Exists(scheduleViewModel.GetConfigFile()))
                    {
                        System.IO.StreamReader fileRead = new System.IO.StreamReader(
                            scheduleViewModel.GetConfigFile());

                        Config Z11 = new Config();

                        Z11 = (Config)reader.Deserialize(fileRead);
                        fileRead.Close();
                    }*/
                   /* {
                        ZimbraDomain = Z11.UserProvision.DestinationDomain;
                        if (DomainList.Count > 0)
                            CurrentDomainSelection = (ZimbraDomain == null) ? 0 :
                                DomainList.IndexOf(ZimbraDomain);

                        else
                            DomainList.Add(ZimbraDomain);
                    }*/
                    scheduleViewModel.SetUsermapFile(fDialog.FileName);
                }
            }
            if (!bCSV)
                MessageBox.Show("Only CSV files are supported", "Zimbra Migration",
                    MessageBoxButton.OK, MessageBoxImage.Exclamation);
        }
    }
    public ICommand AddCommand {
        get;
        private set;
    }
    private void Add(object value)
    {
        var name = value as string;

        UsersBKList.Add(new PublicfoldersViewModel("", ""));
        EnableNext = (UsersList.Count > 0);

        ScheduleViewModel scheduleViewModel =
            ((ScheduleViewModel)ViewModelPtrs[(int)ViewType.SCHED]);

        scheduleViewModel.SchedList.Add(new SchedUser(name, false));
        scheduleViewModel.EnableMigrate = (scheduleViewModel.SchedList.Count > 0);
        scheduleViewModel.EnablePreview = scheduleViewModel.EnableMigrate;
    }

    private void AddUB(object value)
    {
        var name = value as string;

        UsersList.Add(new UsersViewModel("", ""));
        EnableNext = (UsersBKList.Count > 0);

        ScheduleViewModel scheduleViewModel =
            ((ScheduleViewModel)ViewModelPtrs[(int)ViewType.SCHED]);

        scheduleViewModel.SchedList.Add(new SchedUser(name, false));
        scheduleViewModel.EnableMigrate = (scheduleViewModel.SchedList.Count > 0);
        scheduleViewModel.EnablePreview = scheduleViewModel.EnableMigrate;
    }
    public ICommand RemoveCommand {
        get;
        private set;
    }
    private void Remove()
    {
        OptionsViewModel ovm = ((OptionsViewModel)ViewModelPtrs[(int)ViewType.OPTIONS]);
        ovm.IsSkipFolders = true;
           
        folderstoskip += UsersBKList[CurrentFolderSelection].MappedName.ToString();
        folderstoskip += ",";
        ovm.FoldersToSkip = folderstoskip;
        UsersBKList.RemoveAt(CurrentFolderSelection);
        EnableNext = (UsersBKList.Count > 0);

        ScheduleViewModel scheduleViewModel =
            ((ScheduleViewModel)ViewModelPtrs[(int)ViewType.SCHED]);

        scheduleViewModel.EnableMigrate = (scheduleViewModel.SchedList.Count > 0);
        scheduleViewModel.EnablePreview = scheduleViewModel.EnableMigrate;
    }
    public ICommand SaveCSVCommand {
        get;
        private set;
    }
    private void SaveCSV()
    {
        if (!ValidateUsersList(true))
            return;

        ScheduleViewModel scheduleViewModel =
            ((ScheduleViewModel)ViewModelPtrs[(int)ViewType.SCHED]);

        System.Xml.Serialization.XmlSerializer reader =
            new System.Xml.Serialization.XmlSerializer(typeof(Config));
        if (File.Exists(scheduleViewModel.GetConfigFile()))
        {
            System.IO.StreamReader fileRead = new System.IO.StreamReader(
                scheduleViewModel.GetConfigFile());

            Config Z11 = new Config();

            Z11 = (Config)reader.Deserialize(fileRead);
            fileRead.Close();

            CSVDelimiter = Z11.AdvancedImportOptions.CSVDelimiter;
        }
        List<Users> ListofUsers = new List<Users>();
        for (int i = 0; i < UsersList.Count; i++)
        {
            if (CSVDelimiter == null)
            {
                CSVDelimiter = ",";
            }
            string users = UsersList[i].Username + CSVDelimiter/*','*/ + UsersList[i].MappedName;

//            string[] nameTokens = users.Split(',');
            string[] nameTokens = users.Split(CSVDelimiter.ToCharArray());

            Users tempUser = new Users();

            tempUser.UserName = nameTokens.GetValue(0).ToString();
            tempUser.MappedName = nameTokens.GetValue(1).ToString();
            if (nameTokens.Length > 2)
            {
                tempUser.ChangePWD = Convert.ToBoolean(nameTokens.GetValue(2).ToString());
            }
            else
            {
                tempUser.ChangePWD = false;
            }
            
            // tempUser.PWDdefault = nameTokens.GetValue(3).ToString();

            ListofUsers.Add(tempUser);
        }

        //string resultcsv = Users.ToCsv<Users>(",", ListofUsers);
        string resultcsv = Users.ToCsv<Users>(CSVDelimiter, ListofUsers);

        Microsoft.Win32.SaveFileDialog fDialog = new Microsoft.Win32.SaveFileDialog();
        fDialog.Filter = "User Map Files|*.csv";

        // fDialog.CheckFileExists = true;
        // fDialog.Multiselect = false;

        /*ScheduleViewModel scheduleViewModel =
            ((ScheduleViewModel)ViewModelPtrs[(int)ViewType.SCHED]);*/

        if (fDialog.ShowDialog() == true)
        {
            string filename = fDialog.FileName;

            // +".csv";
            System.IO.File.WriteAllText(filename, resultcsv);
            scheduleViewModel.SetUsermapFile(filename);
        }
        // /Domain information gets stored in the xml
        // ///will have to revisit.
        SaveDomain();
    }
    public ICommand BackCommand {
        get;
        private set;
    }
    private void Back()
    {
        lb.SelectedIndex = 3;
    }
    public ICommand NextCommand {
        get;
        private set;
    }
    private void Next()
    {

       
        if (!ValidateUsersList(true))
            return;
        ZimbraAPI zimbraAPI = new ZimbraAPI(isServer);
        if (ZimbraValues.zimbraValues.AuthToken.Length == 0)
        {
            MessageBox.Show("You must log on to the Zimbra server", "Zimbra Migration",
                MessageBoxButton.OK, MessageBoxImage.Error);
            return;
        }
        SaveDomain();

        PublicfoldersViewModel publicfoldersViewModel =
                    ((PublicfoldersViewModel)ViewModelPtrs[(int)ViewType.PUBFLDS]);
        ScheduleViewModel scheduleViewModel =
            ((ScheduleViewModel)ViewModelPtrs[(int)ViewType.SCHED]);

        scheduleViewModel.EnableProvGB = false;

        

       /* for (int i = 0; i < UsersBKList.Count; i++)
        {
            OptionsViewModel ovm = ((OptionsViewModel)ViewModelPtrs[(int)ViewType.OPTIONS]);
            ovm.IsSkipFolders = true;
            m_config.AdvancedImportOptions.FoldersToSkip = new Folder[UsersBKList.Count];
            string folderstoskip = "";
            for (i = 0; i < UsersBKList.Count; i++)
            {
                Folder tempUser = new Folder();
                tempUser.FolderName = UsersBKList[i].MappedName.ToString();
                folderstoskip += UsersBKList[i].MappedName.ToString();
                folderstoskip += ",";
                m_config.AdvancedImportOptions.FoldersToSkip.SetValue(tempUser, i);
             
                m_config.AdvancedImportOptions.IsSkipFolders = true;
            }
            ovm.FoldersToSkip = folderstoskip;
        }*/
        
       /* for (int i = 0; i < UsersList.Count; i++)
        {
            string userName = (UsersList[i].MappedName.Length > 0) ? UsersList[i].MappedName :
                UsersList[i].Username;
            string acctName = userName + '@' + ZimbraDomain;

            if (zimbraAPI.GetAccount(acctName) == 0)
            {
                UsersList[i].IsProvisioned = true;
                scheduleViewModel.SchedList[i].isProvisioned = true;    // get (SchedList) in schedule view model will set again
            }
            else if (zimbraAPI.LastError.IndexOf("no such account") != -1)
            {
                UsersList[i].IsProvisioned = false;     // get (SchedList) in schedule view model will set again
                scheduleViewModel.SchedList[i].isProvisioned = false;
                if (!scheduleViewModel.EnableProvGB)
                {
                    scheduleViewModel.EnableProvGB = true;
                }
            }
            else
            {
                MessageBox.Show(string.Format("Error accessing account {0}: {1}", acctName,
                    zimbraAPI.LastError), "Zimbra Migration", MessageBoxButton.OK,
                    MessageBoxImage.Error);
            }
        }*/
        string acctName = ZimbraAccountName + '@' + ZimbraDomain;

        if (zimbraAPI.GetAccount(acctName) == 0)
        {
            UsersList[0].IsProvisioned = true;
            {
                SchedUser schd = new SchedUser(ZimbraAccountName, true);
                scheduleViewModel.SchedList.Add(schd);
                //scheduleViewModel.SchedList[0].isProvisioned = true;
            }
            scheduleViewModel.SchedfolderList[0].isProvisioned = true;    // get (SchedList) in schedule view model will set again
        }
        else if (zimbraAPI.LastError.IndexOf("no such account") != -1)
        {
            UsersList[0].IsProvisioned = false;     // get (SchedList) in schedule view model will set again
            scheduleViewModel.SchedList[0].isProvisioned = false;
            scheduleViewModel.SchedfolderList[0].isProvisioned = false;
            if (!scheduleViewModel.EnableProvGB)
            {
                scheduleViewModel.EnableProvGB = true;
            }
        }
        else
        {
            MessageBox.Show(string.Format("Error accessing account {0}: {1}", acctName,
                zimbraAPI.LastError), "Zimbra Migration", MessageBoxButton.OK,
                MessageBoxImage.Error);
        }
        //Logic to get the index of defaulf COS from CosList.
        for (int i = 0; i < scheduleViewModel.CosList.Count; i++)
        {
            if (scheduleViewModel.CosList[i].CosName == "default")
            {
                ZimbraValues.GetZimbraValues().DefaultCosIndex = i;
                break;
            }
        }

        foreach (DomainInfo domaininfo in ZimbraValues.GetZimbraValues().ZimbraDomains)
        {
            if (domaininfo.DomainName == publicfoldersViewModel.DomainList[publicfoldersViewModel.CurrentDomainSelection])
            {
                if (domaininfo.zimbraDomainDefaultCOSId != "")
                {
                    for (int i = 0; i < scheduleViewModel.CosList.Count; i++)
                    {
                        if (domaininfo.zimbraDomainDefaultCOSId == scheduleViewModel.CosList[i].CosID)
                        {
                            scheduleViewModel.CurrentCOSSelection = i;
                            break;
                        }
                    }
                }
                else
                    scheduleViewModel.CurrentCOSSelection = ZimbraValues.GetZimbraValues().DefaultCosIndex;
            break;
            }
        }
        lb.SelectedIndex = 6;
    }

    public const int TYPE_USERNAME = 1;
    public const int TYPE_MAPNAME = 2;
    private bool isDuplicate(string nam, int type)
    {
        bool bRetval = false;
        int iHitCount = 0;

        for (int i = 0; i < UsersList.Count; i++)
        {
            string nam2 = (type == TYPE_USERNAME) ? UsersList[i].Username :
                UsersList[i].MappedName;

            if (nam == nam2)
            {
                iHitCount++;
                if (iHitCount == 2)
                {
                    bRetval = true;
                    break;
                }
            }
        }
        return bRetval;
    }

    public bool ValidateUsersList(bool bShowWarning)
    {
        // Make sure there are no blanks or duplicates in the list; remove them if there are.
        // If we get down to no items, disable the Next button.
         OptionsViewModel ovm = ((OptionsViewModel)ViewModelPtrs[(int)ViewType.OPTIONS]);
         if (ovm.IsPublicFolders)
         {
             for (int i = UsersList.Count - 1; i >= 0; i--)
             {
                if (UsersList[i].MappedName.Length > 0)
                 {
                     if (isDuplicate(UsersList[i].MappedName, TYPE_MAPNAME))
                         UsersList.RemoveAt(i);
                 }
             }
             if (UsersList.Count == 0)
             {
                 if (bShowWarning)
                 {
                     MessageBox.Show("Please specify a source name", "Zimbra Migration", MessageBoxButton.OK,
                                     MessageBoxImage.Warning);
                 }
                 EnableNext = false;
                 return false;
             }
             return true;


         }
         else
         {

             for (int i = UsersList.Count - 1; i >= 0; i--)
             {
                 if (UsersList[i].Username.Length == 0)
                 {
                     UsersList.RemoveAt(i);
                 }
                 else if (isDuplicate(UsersList[i].Username, TYPE_USERNAME))
                 {
                     UsersList.RemoveAt(i);
                 }
                 else if (UsersList[i].MappedName.Length > 0)
                 {
                     if (isDuplicate(UsersList[i].MappedName, TYPE_MAPNAME))
                         UsersList.RemoveAt(i);
                 }
             }
             if (UsersList.Count == 0)
             {
                 if (bShowWarning)
                 {
                     MessageBox.Show("Please specify a source name", "Zimbra Migration", MessageBoxButton.OK,
                                     MessageBoxImage.Warning);
                 }
                 EnableNext = false;
                 return false;
             }
             return true;
         }
    }

    public void LoadDomain(Config config)
    {
        CurrentDomainSelection = 0;

        string d = config.UserProvision.DestinationDomain;

        if (DomainList.Count > 0)
        {
            for (int i = 0; i < DomainList.Count; i++)
            {
                if (d == DomainList[i])
                {
                    CurrentDomainSelection = i;
                    DomainsFilledIn = true;
                    break;
                }
            }
        }
        else
        {
            DomainList.Add(d);
        }
    }

    private void SaveDomain()
    {
        try
        {
            ScheduleViewModel scheduleViewModel =
                ((ScheduleViewModel)ViewModelPtrs[(int)ViewType.SCHED]);

            ZimbraDomain = DomainList[CurrentDomainSelection];
            if (scheduleViewModel.GetConfigFile().Length > 0)
            {
                if (CurrentDomainSelection > -1)
                {
                    if (File.Exists(scheduleViewModel.GetConfigFile()))
                    {
                        UpdateXmlElement(scheduleViewModel.GetConfigFile(), "UserProvision");
                    }
                    else
                    {
                        System.Xml.Serialization.XmlSerializer writer =
                            new System.Xml.Serialization.XmlSerializer(typeof(Config));

                        System.IO.StreamWriter file = new System.IO.StreamWriter(
                            scheduleViewModel.GetConfigFile());
                        writer.Serialize(file, m_config);
                        file.Close();
                    }
                }
            }
        }
        catch (ArgumentOutOfRangeException)
        {
             MessageBox.Show("Please specify a domain", "Zimbra Migration", MessageBoxButton.OK,
                             MessageBoxImage.Warning);
        }
    }

    public ObjectPickerInfo GetOPInfo()
    {
        return OPInfo;
    }

    private void AddOPInfo(ObjectPickerInfo info)
    {
        OPInfo.DisplayName = info.DisplayName;
        OPInfo.GivenName = info.GivenName;
        OPInfo.Sn = info.Sn;
        OPInfo.Zfp = info.Zfp;
    }

    // //////////////////////

    private ObservableCollection<PublicfoldersViewModel> usersbklist =
        new ObservableCollection<PublicfoldersViewModel>();
    public ObservableCollection<PublicfoldersViewModel> UsersBKList {
        get { return usersbklist; }
    }

    private ObservableCollection<UsersViewModel> userslist =
       new ObservableCollection<UsersViewModel>();
    public ObservableCollection<UsersViewModel> UsersList
    {
        get { return userslist; }
    }
    private ObservableCollection<string> domainlist = new ObservableCollection<string>();
    public ObservableCollection<string> DomainList {
        get { return domainlist; }
        set
        {
            domainlist = value;
        }
    }

    private ObservableCollection<DomainInfo> domaininfolist = new ObservableCollection<DomainInfo>();
    public ObservableCollection<DomainInfo> DomainInfoList {
        get { return domaininfolist; }
        set
        {
            domaininfolist = value;
        }
    }

    ObjectPickerInfo OPInfo;
    public string Username {
        get { return m_users.Username; }
        set
        {
            if (value == m_users.Username)
                return;
            m_users.Username = value;
            OnPropertyChanged(new PropertyChangedEventArgs("Username"));
        }
    }
    public string MappedName {
        get { return m_users.MappedName; }
        set
        {
            if (value == m_users.MappedName)
                return;
            m_users.MappedName = value;
            OnPropertyChanged(new PropertyChangedEventArgs("MappedName"));
        }
    }

    public string ZimbraDomain {
        get { return m_config.UserProvision.DestinationDomain; }
        set
        {
            if (value == m_config.UserProvision.DestinationDomain)
                return;
            m_config.UserProvision.DestinationDomain = value;

            OnPropertyChanged(new PropertyChangedEventArgs("ZimbraDomain"));
        }
    }
   
    public int CurrentUserSelection {
        get { return m_users.CurrentUserSelection; }
        set
        {
            if (value == m_users.CurrentUserSelection)
                return;
            m_users.CurrentUserSelection = value;
            MinusEnabled = (value != -1);
            OnPropertyChanged(new PropertyChangedEventArgs("CurrentUserSelection"));
        }
    }

    private ObservableCollection<string> folderinfolist = new ObservableCollection<string>();
    public ObservableCollection<string> FolderInfoList
    {
        get { return folderinfolist; }
        set
        {
            folderinfolist = value;
        }
    }
    public int CurrentFolderSelection
    {
        get { return folderselection; }
        set
        {
            folderselection = value;
            MinusEnabled = (value != -1);
            OnPropertyChanged(new PropertyChangedEventArgs("CurrentFolderSelection"));
        }
    }
    public int CurrentDomainSelection {
        get { return domainselection; }
        set
        {
            domainselection = value;

            OnPropertyChanged(new PropertyChangedEventArgs("CurrentDomainSelection"));
        }
    }
    private int domainselection;
    private int folderselection;
    private bool minusEnabled;
    private bool plusEnabled;
    private string folderstoskip;
    public bool MinusEnabled {
        get { return minusEnabled; }
        set
        {
            minusEnabled = value;
            OnPropertyChanged(new PropertyChangedEventArgs("MinusEnabled"));
        }
    }
    public bool PlusEnabled
    {
        get { return plusEnabled; }
        set
        {
            plusEnabled = value;
            OnPropertyChanged(new PropertyChangedEventArgs("PlusEnabled"));
        }
    }

    private string CsvDelimiter;

    private string m_publicFolderName;
    public string PublicFolderName
    {
        get { return m_publicFolderName; }
        set { m_publicFolderName = value; }

    }

    public string CSVDelimiter
    {
        get { return CsvDelimiter; }
        set { CsvDelimiter = value; }
    }

    private bool isProvisioned;
    public bool IsProvisioned {
        get { return isProvisioned; }
        set
        {
            isProvisioned = value;
            OnPropertyChanged(new PropertyChangedEventArgs("IsProvisioned"));
        }
    }
    private bool mustChangePassword;
    public bool MustChangePassword
    {
        get { return mustChangePassword; }
        set
        {
            mustChangePassword = value;
            OnPropertyChanged(new PropertyChangedEventArgs("MustChangePassword"));
        }
    }
    private bool enableNext;
    public bool EnableNext {
        get { return enableNext; }
        set
        {
            enableNext = value;
            OnPropertyChanged(new PropertyChangedEventArgs("EnableNext"));
        }
    }
    private bool enablePopButtons;
    public bool EnablePopButtons {
        get { return enablePopButtons; }
        set
        {
            enablePopButtons = value;
            OnPropertyChanged(new PropertyChangedEventArgs("EnablePopButtons"));
        }
    }
    private bool domainsFilledIn;
    public bool DomainsFilledIn
    {
        get { return domainsFilledIn; }
        set
        {
            domainsFilledIn = value;
            OnPropertyChanged(new PropertyChangedEventArgs("DomainsFilledIn"));
        }
    }
}
}
