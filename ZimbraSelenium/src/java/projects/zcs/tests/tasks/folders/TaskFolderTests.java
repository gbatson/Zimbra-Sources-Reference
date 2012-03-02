package projects.zcs.tests.tasks.folders;

import java.lang.reflect.Method;
import junit.framework.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import framework.util.RetryFailedTests;
import projects.zcs.tests.CommonTest;

@SuppressWarnings( { "static-access" })
public class TaskFolderTests extends CommonTest {
	@DataProvider(name = "taskCreateDataProvider")
	protected Object[][] createData(Method method) {
		String test = method.getName();
		if (test.equals("createTaskFolder") || test.equals("deleteTaskFolder")
				|| test.equals("renameTaskFolder")
				|| test.equals("moveTaskFolder")
				|| test.equals("tryToCreateDuplicateTaskFolder")) {
			return new Object[][] { {} };
		} else {
			return new Object[][] { { "" } };
		}
	}

	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();
		Thread.sleep(2000);
		page.zTaskApp.zNavigateToTasks();
		isExecutionARetry = false;
	}

	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (needReset && !isExecutionARetry) {
			zLogin();
		}
		needReset = true;
	}

	/**
	 * Creates a task list folder Verifies that the folder is created
	 * successfully
	 * 
	 */
	@Test(dataProvider = "taskCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createTaskFolder() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String taskListBtn = getLocalizedData_NoSpecialChar();
		String taskListRtClick = getLocalizedData_NoSpecialChar();
		page.zTaskApp.zTaskListCreateNewBtn(taskListBtn);
		Thread.sleep(1000);
		page.zTaskApp.zTaskListCreateRtClick(taskListRtClick);
		obj.zTaskFolder.zExists(taskListBtn);
		obj.zTaskFolder.zExists(taskListRtClick);

		needReset = false;
	}

	/**
	 * Creates a task list folder Deletes the task list folder Verifies that the
	 * task list folder is deleted successfully
	 * 
	 */
	@Test(dataProvider = "taskCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteTaskFolder() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String taskList = getLocalizedData_NoSpecialChar();
		page.zTaskApp.zTaskListCreateNewBtn(taskList);
		obj.zTaskFolder.zExists(taskList);
		page.zTaskApp.zTaskListDelete(taskList);
		obj.zTaskFolder.zNotExists(taskList);

		needReset = false;
	}

	/**
	 * Creates a task list folder Renames the task list folder Verifies that the
	 * task list folder is renamed successfully
	 * 
	 */
	@Test(dataProvider = "taskCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void renameTaskFolder() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String orgTaskList = getLocalizedData_NoSpecialChar();
		String renamedTaskList = getLocalizedData_NoSpecialChar();
		page.zTaskApp.zTaskListCreateNewBtn(orgTaskList);
		obj.zTaskFolder.zExists(orgTaskList);
		page.zTaskApp.zTaskListRename(orgTaskList, renamedTaskList);
		obj.zTaskFolder.zNotExists(orgTaskList);
		obj.zTaskFolder.zExists(renamedTaskList);

		needReset = false;
	}

	@Test(dataProvider = "taskCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void moveTaskFolder() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String orgTaskList = getLocalizedData_NoSpecialChar();
		page.zTaskApp.zTaskListCreateNewBtn(orgTaskList);
		zDragAndDrop(
				"//td[contains(@id, 'zti__main_Tasks') and contains(text(), '"
						+ orgTaskList + "')]", page.zTaskApp.zTasksFolder);
		Assert
				.assertTrue(selenium
						.isElementPresent("//div[@id='zti__main_Tasks__15']/div[@class='DwtTreeItemChildDiv']//td[contains(text(), '"
								+ orgTaskList + "')]"));

		needReset = false;
	}

	@Test(dataProvider = "taskCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void tryToCreateDuplicateTaskFolder() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String orgTaskList = getLocalizedData_NoSpecialChar();
		page.zTaskApp.zTaskListCreateNewBtn(orgTaskList);
		Thread.sleep(1000);
		obj.zButton
				.zRtClick(replaceUserNameInStaticId(page.zTaskApp.zNewTasksOverviewPaneIcon));
		obj.zMenuItem.zClick(localize(locator.newTaskFolder));
		obj.zEditField.zTypeInDlgByName(localize(locator.name), orgTaskList,
				localize(locator.createNewTaskFolder));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.createNewTaskFolder));
		assertReport(localize(locator.errorAlreadyExists, orgTaskList, ""),
				obj.zDialog.zGetMessage(localize(locator.criticalMsg)),
				"Verifying dialog message");
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.criticalMsg));
		obj.zButton.zClickInDlgByName(localize(locator.cancel),
				localize(locator.createNewTaskFolder));

		needReset = false;
	}

	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		zLogin();
	}
}
