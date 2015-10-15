Zimbra Source Code - Reference Only
==
This repository is a **heavily modified** version of the official zimbra repository <https://wiki.zimbra.com/wiki/Building_Zimbra_using_Git>.  
Its purpose is to be a read only **reference** for developers and sysadmins who want to understand Zimbra behaviour.

 * **doesn't build** since many parts are missing
 * **commits are squashed**: one commit for each version to easily compare behaviour changes
 * **binaries were removed** to make the repository github compatible and to reduce its size

These paths were fully removed:  

  * ZimbraCommon/jars-test/
  * ZimbraWebClient/jars/
  * ZimbraSoap/jars/
  * ZimbraBuild/
  * ZimbraWebClient/WebRoot/help/

Every file but those with the following extensions were removed:  

    bat bmp build c conf config cpp cs csproj css def diff dtd gif h htm html ico idl java jpg js json jsp less license manifest php pl pm png pom production properties ps1 py rb rc scss sh sql tag template tpl txt vbs vcproj wsdl wxs xaml xml xsd xsl xul yml
