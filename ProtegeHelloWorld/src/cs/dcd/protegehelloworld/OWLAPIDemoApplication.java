package cs.dcd.protegehelloworld;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.ProtegeOWL;

public class OWLAPIDemoApplication {

    public static void main(String[] args) throws OntologyLoadException {
        OWLModel owlModel = ProtegeOWL.createJenaOWLModel();
        owlModel.getNamespaceManager().setDefaultNamespace("http://hello.com#");
        OWLNamedClass worldClass = owlModel.createOWLNamedClass("World");
        System.out.println("Class URI: " + worldClass.getURI());
    }
}

