package projects.html.clients;



/**
 * @author raodv
 * Essentially same as Folder in html-client but with some extra apis like zCheck zunCheck etc.
 */
public class CalendarFolder extends Folder {

	public CalendarFolder() {
		super();
	} 
	/**
	 * Check Calendar folder
	 * @param folder
	 */
	public  void zCheck(String folder) {
		selenium.call("folderCore_html",  folder+"_check", "click","", "");
	}	
	/**
	 * unchecks Calendar folder
	 * @param folder
	 */
	public  void zUnCheck(String folder) {
		selenium.call("folderCore_html",  folder+"_uncheck", "click","", "");
	}		

	/**
	 * Checks if Calendar folder's checkbox is unchecked
	 * @param folder
	 */
	public  String zIsUnChecked(String folder) {
		return selenium.call("folderCore_html",  folder+"_uncheck", "exists","", "");
	}

	/**
	 * Checks if Calendar folder's checkbox is checked
	 * @param folder
	 */
	public  String zIsChecked(String folder) {
		return selenium.call("folderCore_html",  folder+"_check", "exists","", "");
	}	
}