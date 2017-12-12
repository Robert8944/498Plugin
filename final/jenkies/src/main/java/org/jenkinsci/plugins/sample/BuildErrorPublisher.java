package org.jenkinsci.plugins.sample;

import hudson.Launcher;
import hudson.Extension;
import hudson.FilePath;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;
import hudson.model.AbstractProject;
import hudson.model.AbstractBuild;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.BuildListener;
import hudson.tasks.Recorder;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.IOException;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

public class BuildErrorPublisher extends Recorder {

    @Override
    public BuildStepMonitor getRequiredMonitorService(){
        return BuildStepMonitor.NONE;
    }

    @DataBoundConstructor
    public BuildErrorPublisher() {
    }

    public String arrayToString(Object[] a){
        if(a==null){
            return "File doesn't exist";
        }
        String ans = "";
        for( int i = 0; i < a.length; i++){
            ans += a[i];
        }
        return ans;
    }

    public ArrayList<ArrayList<String>> parseXML(){
	ArrayList<ArrayList<String>> ans = new ArrayList<ArrayList<String>>();
	ArrayList<String> keys = new ArrayList<String>();
	ArrayList<String> statements = new ArrayList<String>();
        try {
            File errorDB = new File("./work/jobs/test2/errorDatabase.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(errorDB);
            doc.getDocumentElement().normalize();
            doc.getDocumentElement().getNodeName();//root element
            NodeList nList = doc.getElementsByTagName("error");
            for (int temp = 0; temp < nList.getLength(); temp++) {
		Node nNode = nList.item(temp);
		if(nNode.getNodeName().equals("errorKey"))
		if (nNode.getNodeType() == Node.ELEMENT_NODE) {
			Element eElement = (Element) nNode;
			keys.add(eElement.getElementsByTagName("errorKey").item(0).getTextContent());
			statements.add(eElement.getElementsByTagName("errorStatement").item(0).getTextContent());
		}
	    }
        } catch (Exception e) {
	    e.printStackTrace();
        }
        ans.add(keys);
	ans.add(statements);
	return ans;
    }
         
    public ArrayList<Integer> parseLog(String log,ArrayList<String> ErrorKeywords){
        ArrayList<Integer> hitIndexes = new ArrayList<Integer>();
        for (int i = 0; i < ErrorKeywords.size(); i++){
            if(log.contains(ErrorKeywords.get(i))){
                hitIndexes.add(i);
            }
        }
        return hitIndexes;
    }

    @Override
    public boolean perform(AbstractBuild<?,?> build, Launcher launcher, BuildListener listener){
        listener.getLogger().println("Reading Log File");
	File log = new File("./work/jobs/test2/builds/lastFailedBuild/log");
        final StringBuilder contents = new StringBuilder();

        try {
            final BufferedReader input = new BufferedReader(new FileReader(log));
            try {
                String line = null; // not declared within while loop
                while ((line = input.readLine()) != null) {
                    contents.append(line + "\n");
                }
            } finally {
                input.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        ArrayList<ArrayList<String>> errors = parseXML();
	ArrayList<String> testErrors = new ArrayList<String>();
	ArrayList<String> testStatements = new ArrayList<String>();
	testErrors.add("NullPointerException");
	testStatements.add("Null Pointer Exception found!");
	testErrors.add("IndexOutOfBounds");
	testStatements.add("You tried to access an index outside of an array!");
	ArrayList<Integer> hitIndexes = parseLog(contents.toString(), testErrors);
	listener.getLogger().println("Your last failed build log:\n\n\n");
	listener.getLogger().println(contents.toString()+"\n\n\n");
        listener.getLogger().println("You hit " + hitIndexes.size() + " errors:\n");
	for( int i = 0; i < hitIndexes.size(); i++){
	    listener.getLogger().println(testErrors.get(hitIndexes.get(i)));
	    listener.getLogger().println("\t");
	    listener.getLogger().println(testStatements.get(hitIndexes.get(i)));
        }
        //listener.getLogger().println("Here is the last failed build:\n\n\n" + contents.toString() + "\n\n\n DONE \n\n\n");
        return true;
        }

    @Symbol("greet")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher>{

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }


        @Override
        public String getDisplayName(){
            return "Parse buildlog for errors";
        }

    }

}
