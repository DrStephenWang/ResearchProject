package cs.dcd.createtcmontology;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.Jena;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLAllValuesFrom;
import edu.stanford.smi.protegex.owl.model.OWLComplementClass;
import edu.stanford.smi.protegex.owl.model.OWLEnumeratedClass;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLIntersectionClass;
import edu.stanford.smi.protegex.owl.model.OWLMaxCardinality;
import edu.stanford.smi.protegex.owl.model.OWLMinCardinality;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLRestriction;
import edu.stanford.smi.protegex.owl.model.OWLUnionClass;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.*;

public class CreateTCMOntology {

	public void CreateOntology() throws IOException, OntologyLoadException{
		OWLModel owlModel = ProtegeOWL.createJenaOWLModel();
		
		OWLNamedClass symptomClass = owlModel.createOWLNamedClass("症状");
		OWLNamedClass prescriptionClass = owlModel.createOWLNamedClass("方剂");
		OWLNamedClass medicineClass = owlModel.createOWLNamedClass("中药");
		
		OWLObjectProperty conjugation = owlModel.createOWLObjectProperty("伴随");
		conjugation.setDomain(symptomClass);
		conjugation.setRange(symptomClass);	   
	    OWLObjectProperty similarity = owlModel.createOWLObjectProperty("类似");
	    similarity.setDomain(prescriptionClass);
	    similarity.setRange(prescriptionClass);   	    
	    OWLObjectProperty sharing = owlModel.createOWLObjectProperty("共用");
	    sharing.setDomain(medicineClass);
	    sharing.setRange(medicineClass);  	    
	    OWLObjectProperty function = owlModel.createOWLObjectProperty("主治");
	    function.setDomain(prescriptionClass);
	    function.setRange(symptomClass);	    
	    OWLObjectProperty bases = owlModel.createOWLObjectProperty("主要成分");
	    bases.setDomain(prescriptionClass);
	    bases.setRange(medicineClass);
	    
	    OWLObjectProperty support1 = owlModel.createAnnotationOWLObjectProperty("支持度1");
	    support1.addSuperproperty(conjugation);	    
	    
	    OWLDatatypeProperty support = owlModel.createAnnotationOWLDatatypeProperty("支持度");
	    support.setDomain(symptomClass);
	    support.setRange(owlModel.getXSDdouble());	    
	    support.addSuperproperty(conjugation);
	    
	    String inputPath = "D:/Eclipse-WorkSpace/CreateTCMOntology/input/";
	    BufferedReader br = null;
	    String str = null;
	    
	    TreeSet<RDFIndividual> symptomTreeSet = new TreeSet<RDFIndividual>();
	    TreeSet<RDFIndividual> prescriptionTreeSet = new TreeSet<RDFIndividual>();
	    TreeSet<RDFIndividual> medicineTreeSet = new TreeSet<RDFIndividual>();
	    
	    TreeSet<String> symptomStringTreeSet = new TreeSet<String>();
	    TreeSet<String> prescriptionStringTreeSet = new TreeSet<String>();
	    TreeSet<String> medicineStringTreeSet = new TreeSet<String>();
	    
	    NumberFormat sup = NumberFormat.getInstance();
		sup.setMaximumFractionDigits(4);
	    
	    br = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath + "SymptomAndDiseaseDataSet.txt")));
	    while ((str = br.readLine()) != null)
		{	
			str.replaceAll("", "\r");
			symptomTreeSet.add(symptomClass.createRDFIndividual(str));		
			symptomStringTreeSet.add(str);
		}
	    System.out.println("病症本体实例添加结束");
	    
	    br = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath + "PrescriptionDataSet.txt")));
	    while ((str = br.readLine()) != null)
		{	
			str.replaceAll("", "\r");
			prescriptionTreeSet.add(prescriptionClass.createRDFIndividual(str));
			prescriptionStringTreeSet.add(str);
		}
	    System.out.println("方剂本体实例添加结束");
	    
	    br = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath + "MedicineDataSet.txt")));
	    while ((str = br.readLine()) != null)
		{	
			str.replaceAll("", "\r");
			medicineTreeSet.add(medicineClass.createRDFIndividual(str));
			medicineStringTreeSet.add(str);
		}
	    System.out.println("中药本体实例添加结束");
	    
	    br = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath + "ResultSymptomAndDisease.txt")));
	    while ((str = br.readLine()) != null)
		{	
	    	str.replaceAll("", "\r");
	    	String[] parameter = str.split(" ");
	    	
	    	if ((symptomStringTreeSet.contains(parameter[0])) && (symptomStringTreeSet.contains(parameter[1])))
	    	{
	    		
	    	}
	    	else
	    	{
	    		continue;
	    	}
	    	
	    	RDFIndividual rdfIndividual1 = null;
	    	RDFIndividual rdfIndividual2 = null;
	    	double numParameter1 = Double.parseDouble(parameter[2]);
	    	double numParameter2 = Double.parseDouble(parameter[3]);
	    	double numParameter3 = Double.parseDouble(parameter[4]);
	    	double numParameter4 = Double.parseDouble(parameter[5]);
	    	
	    	Iterator<RDFIndividual> iterator1 = symptomTreeSet.iterator();
	    	while (iterator1.hasNext())
	    	{
	    		rdfIndividual1 = iterator1.next();

	    		if (rdfIndividual1.getBrowserText().equals(parameter[0]))
	    		{
	    			break;
	    		}
	    	}
	    	Iterator<RDFIndividual> iterator2 = symptomTreeSet.iterator();
	    	while (iterator2.hasNext())
	    	{
	    		rdfIndividual2 = iterator2.next();

	    		if (rdfIndividual2.getBrowserText().equals(parameter[1]))
	    		{
	    			break;
	    		}
	    	}
	    	
	    	if ((rdfIndividual1 != null) && (rdfIndividual2 != null))
	    	{
	    		rdfIndividual1.addPropertyValue(conjugation, rdfIndividual2);	
	    		//conjugation.addPropertyValue(support, sup.format(numParameter1));
	    		rdfIndividual1.addPropertyValue(support1, sup.format(numParameter1));
	    	}
		}
	    System.out.println("症状伴随关系添加结束");
	    
	    br = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath + "ResultPrescription.txt")));
	    while ((str = br.readLine()) != null)
		{	
	    	str.replaceAll("", "\r");
	    	String[] parameter = str.split(" ");
	    	
	    	if ((prescriptionStringTreeSet.contains(parameter[0])) && (prescriptionStringTreeSet.contains(parameter[1])))
	    	{
	    		
	    	}
	    	else
	    	{
	    		continue;
	    	}
	    	
	    	RDFIndividual rdfIndividual1 = null;
	    	RDFIndividual rdfIndividual2 = null;
	    	double numParameter1 = Double.parseDouble(parameter[2]);
	    	double numParameter2 = Double.parseDouble(parameter[3]);
	    	double numParameter3 = Double.parseDouble(parameter[4]);
	    	double numParameter4 = Double.parseDouble(parameter[5]);
	    	
	    	Iterator<RDFIndividual> iterator1 = prescriptionTreeSet.iterator();
	    	while (iterator1.hasNext())
	    	{
	    		rdfIndividual1 = iterator1.next();

	    		if (rdfIndividual1.getBrowserText().equals(parameter[0]))
	    		{
	    			break;
	    		}
	    	}
	    	Iterator<RDFIndividual> iterator2 = prescriptionTreeSet.iterator();
	    	while (iterator2.hasNext())
	    	{
	    		rdfIndividual2 = iterator2.next();

	    		if (rdfIndividual2.getBrowserText().equals(parameter[1]))
	    		{
	    			break;
	    		}
	    	}
	    	
	    	if ((rdfIndividual1 != null) && (rdfIndividual2 != null))
	    	{
	    		rdfIndividual1.addPropertyValue(similarity, rdfIndividual2);
	    	}
		}
	    System.out.println("方剂类似关系添加结束");
	    
	    br = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath + "ResultMedicine.txt")));
	    while ((str = br.readLine()) != null)
		{	
	    	str.replaceAll("", "\r");
	    	String[] parameter = str.split(" ");
	    	
	    	if ((medicineStringTreeSet.contains(parameter[0])) && (medicineStringTreeSet.contains(parameter[1])))
	    	{
	    		
	    	}
	    	else
	    	{
	    		continue;
	    	}
	    	
	    	RDFIndividual rdfIndividual1 = null;
	    	RDFIndividual rdfIndividual2 = null;
	    	double numParameter1 = Double.parseDouble(parameter[2]);
	    	double numParameter2 = Double.parseDouble(parameter[3]);
	    	double numParameter3 = Double.parseDouble(parameter[4]);
	    	double numParameter4 = Double.parseDouble(parameter[5]);
	    	
	    	Iterator<RDFIndividual> iterator1 = medicineTreeSet.iterator();
	    	while (iterator1.hasNext())
	    	{
	    		rdfIndividual1 = iterator1.next();

	    		if (rdfIndividual1.getBrowserText().equals(parameter[0]))
	    		{
	    			break;
	    		}
	    	}
	    	Iterator<RDFIndividual> iterator2 = medicineTreeSet.iterator();
	    	while (iterator2.hasNext())
	    	{
	    		rdfIndividual2 = iterator2.next();

	    		if (rdfIndividual2.getBrowserText().equals(parameter[1]))
	    		{
	    			break;
	    		}
	    	}
	    	
	    	if ((rdfIndividual1 != null) && (rdfIndividual2 != null))
	    	{
	    		rdfIndividual1.addPropertyValue(sharing, rdfIndividual2);
	    	}
		}
	    System.out.println("中药共用关系添加结束");
	    
	    br = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath + "ResultWithoutMedicine.txt")));
	    while ((str = br.readLine()) != null)
		{	
	    	str.replaceAll("", "\r");
	    	String[] parameter = str.split(" ");
	    	
	    	if ((prescriptionStringTreeSet.contains(parameter[0])) && (symptomStringTreeSet.contains(parameter[1])))
	    	{
	    		
	    	}
	    	else
	    	{
	    		continue;
	    	}
	    	
	    	RDFIndividual rdfIndividual1 = null;
	    	RDFIndividual rdfIndividual2 = null;
	    	double numParameter1 = Double.parseDouble(parameter[2]);
	    	double numParameter2 = Double.parseDouble(parameter[3]);
	    	double numParameter3 = Double.parseDouble(parameter[4]);
	    	double numParameter4 = Double.parseDouble(parameter[5]);
	    	
	    	Iterator<RDFIndividual> iterator1 = prescriptionTreeSet.iterator();
	    	while (iterator1.hasNext())
	    	{
	    		rdfIndividual1 = iterator1.next();

	    		if (rdfIndividual1.getBrowserText().equals(parameter[0]))
	    		{
	    			break;
	    		}
	    	}
	    	Iterator<RDFIndividual> iterator2 = symptomTreeSet.iterator();
	    	while (iterator2.hasNext())
	    	{
	    		rdfIndividual2 = iterator2.next();

	    		if (rdfIndividual2.getBrowserText().equals(parameter[1]))
	    		{
	    			break;
	    		}
	    	}
	    	
	    	if ((rdfIndividual1 != null) && (rdfIndividual2 != null))
	    	{
	    		rdfIndividual1.addPropertyValue(function, rdfIndividual2);
	    	}
		}
	    System.out.println("方剂主治关系添加结束");
	    
	    br = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath + "ResultMedAndPre.txt")));
	    while ((str = br.readLine()) != null)
		{	
	    	str.replaceAll("", "\r");
	    	String[] parameter = str.split(" ");
	    	
	    	if ((prescriptionStringTreeSet.contains(parameter[0])) && (medicineStringTreeSet.contains(parameter[1])))
	    	{
	    		
	    	}
	    	else
	    	{
	    		continue;
	    	}
	    	
	    	RDFIndividual rdfIndividual1 = null;
	    	RDFIndividual rdfIndividual2 = null;
	    	double numParameter1 = Double.parseDouble(parameter[2]);
	    	double numParameter2 = Double.parseDouble(parameter[3]);
	    	double numParameter3 = Double.parseDouble(parameter[4]);
	    	double numParameter4 = Double.parseDouble(parameter[5]);
	    	
	    	Iterator<RDFIndividual> iterator1 = prescriptionTreeSet.iterator();
	    	while (iterator1.hasNext())
	    	{
	    		rdfIndividual1 = iterator1.next();

	    		if (rdfIndividual1.getBrowserText().equals(parameter[0]))
	    		{
	    			break;
	    		}
	    	}
	    	Iterator<RDFIndividual> iterator2 = medicineTreeSet.iterator();
	    	while (iterator2.hasNext())
	    	{
	    		rdfIndividual2 = iterator2.next();

	    		if (rdfIndividual2.getBrowserText().equals(parameter[1]))
	    		{
	    			break;
	    		}
	    	}
	    	
	    	if ((rdfIndividual1 != null) && (rdfIndividual2 != null))
	    	{
	    		rdfIndividual1.addPropertyValue(bases, rdfIndividual2);
	    	}
		}
	    System.out.println("方剂组成关系添加结束");
	    
	    
	    String outputPath = "D:/Eclipse-WorkSpace/CreateTCMOntology/output/";
	    File file = new File(outputPath + "TCM.owl");
	    file.createNewFile();
	    OutputStream out = new FileOutputStream(file);
	    Jena.dumpRDF(owlModel.getOntModel(), out);		
	}
	
}
