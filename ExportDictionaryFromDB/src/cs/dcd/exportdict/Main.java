package cs.dcd.exportdict;

import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException
	{
		ExportDictionary ed = new ExportDictionary();
		
		String outputPath = ed.readDirectoryPath("Output Target Path: ");
		
		ed.generateDictionary("Medicine", "medNameZH", outputPath);
		ed.generateDictionary("老版medicine", "medNameZH", outputPath);
		ed.generateDictionary("MedicineBasicInformation", "medicineName", outputPath);
    	
		ed.generateDictionary("Prescription", "preNameZH", outputPath);
		ed.generateDictionary("PrescriptionOld", "preNameZH", outputPath);
		
		ed.generateDictionary("Disease", "DiseaseName", outputPath);
		ed.generateDictionary("SynDiseaseMap", "DiseaseName", outputPath);
		
		ed.generateDictionary("Syndrome", "synNameZH", outputPath);
		
		
    	System.out.println("Done!");
    	
	}
	
}
