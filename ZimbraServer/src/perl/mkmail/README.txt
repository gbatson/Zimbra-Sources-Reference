QUICK START:

0) Install activeperl
1) Run "ppm" from a command-prompt
2) install Statistics-Basic (just type that from inside ppm).  quit.
3) mkmail.pl <NUMBER>
4) put the following onto classpath:
   ZimbraServer\build\classes
   ZimbraServer\jars\commons-cli-1.0.jar
   ZimbraServer\jars\mail.jar
   ZimbraServer\jars\activation.jar
   ZimbraServer\jars\commons-codec-1.2.jar
5) java com.zimbra.cs.tools.Journalize -i <path_to_mkmail>\out -o c:\opt\zimbra\mqueue\new
6) Run the archiver



