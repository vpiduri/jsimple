import jsimple.io.JSimpleIO;
import jsimple.util.HashMap;

/**
 * @author Dorin Suletea
 * @since 12/5/2014
 */
public class Main {
	public static void main(String[] args) throws Exception {
		JSimpleIO.init();
		String myFacebookToken = "<facebook token>";

		// get azure token
		String myAzureToken = AzureAuthenticator.obtainAzureToken(myFacebookToken);
		System.out.println("Azure mobile token obtained: " + myAzureToken + "\n");

		// insert data
		HashMap<String, String> dataToInsert = new HashMap<String, String>();
		dataToInsert.put("A", "myNewRowA");
		dataToInsert.put("B", "myNewRowB");
		boolean insertDoneOk = AzureMobileWriter.insertDataWithAppID(Constants.TABLE_1_NAME, dataToInsert);
		System.out.println("Data inserted " + insertDoneOk + "\n");

		int itemID = 1; // <-- update the item with id 1
		HashMap<String, String> dataToInsert2 = new HashMap<String, String>();
		dataToInsert2.put("A", "updatedA");
		dataToInsert2.put("B", "updatedB");
		AzureMobileWriter.updateFieldFilteredWithAppID(itemID, Constants.TABLE_1_NAME, Constants.AZURE_APP_ID, dataToInsert2);
		System.out.println("Data updated\n");
		
		
		// read the data using appID
		String table1Data = AzureMobileReader.selectAllAuthWithAppID(Constants.TABLE_1_NAME, Constants.AZURE_APP_ID);
		System.out.println("Extracted data form Table1 using AppID" + table1Data + "\n");

		// read the data using myAzureToken (obtained step1)
		String table2Data = AzureMobileReader.selectAllAuthWithToken(Constants.TABLE_1_NAME, myAzureToken);
		System.out.println("Extracted data form Table1 using authToken" + table2Data +"\n");
		

		
	}
}
