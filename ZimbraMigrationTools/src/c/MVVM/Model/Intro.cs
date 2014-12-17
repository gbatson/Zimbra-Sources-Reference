namespace MVVM.Model
{
using System;

public class Intro
{
    internal Intro()
    {
        Populate();
    }
    public string BuildNum {
        get;
        set;
    }
    public string WelcomeMsg {
        get;
        set;
    }
    public bool IsServerMigration {
        get;
        set;
    }
    public bool IsUserMigration {
        get;
        set;
    }
    public bool IsDesktopMigration
    {
        get;
        set;
    }
    public string InstallDir {
        get;
        set;
    }
    public Intro Populate()
    {
        this.BuildNum = new BuildNum().BUILD_NUM;
        this.InstallDir = Environment.CurrentDirectory;
        this.WelcomeMsg =
            "This application will guide you through the process of migrating from Microsoft products to Zimbra.Select a Migration type below";
        return this;
    }
}
}
