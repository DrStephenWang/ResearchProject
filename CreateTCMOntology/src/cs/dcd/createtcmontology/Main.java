package cs.dcd.createtcmontology;

import java.io.IOException;

import edu.stanford.smi.protege.exception.OntologyLoadException;

import cs.dcd.createtcmontology.CreateTCMOntology;

public class Main {

	public static void main(String[] args) throws IOException, OntologyLoadException
	{		
		CreateTCMOntology createTCMOntology = new CreateTCMOntology();
		createTCMOntology.CreateOntology();
		
		System.out.println("Done!");
	}
	
}
