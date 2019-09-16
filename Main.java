package com.cem.byteland;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

class LogicViolationException extends Exception {
    public LogicViolationException(String message){
        super(message);
    }
}

class NotUnifiableException extends Exception{
    public NotUnifiableException(String message){
        super(message);
    }
}

class NullNodeException extends Exception {
    public NullNodeException(){

    }
    public NullNodeException(String message){
        super(message);
    }
}

class WrongInputException extends Exception {
    public WrongInputException(){

    }
    public WrongInputException(String message){
        super(message);
    }
}

class Node {
    private int nodeId;
    private int unionCount;
    private boolean unified=false;
    public Node(Integer nodeId){
        this.nodeId = nodeId;
        this.unionCount = 0;
    }
    public boolean isUnified(){
        return unified;
    }
    public void setUnified(boolean unified){
        this.unified = unified;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        return nodeId == node.nodeId;

    }

    @Override
    public int hashCode() {
        return nodeId;
    }

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public int getUnionCount() {
        return unionCount;
    }

    public void setUnionCount(int unionCount) {
        this.unionCount = unionCount;
    }
}

class Relation {
    private Node firstNode;
    private Node secondNode;

    public Node getFirstNode() {
        return firstNode;
    }

    public void setFirstNode(Node firstNode) {
        this.firstNode = firstNode;
    }

    public Node getSecondNode() {
        return secondNode;
    }

    public void setSecondNode(Node secondNode) {
        this.secondNode = secondNode;
    }


    public Relation(Node firstNode, Node secondNode) throws LogicViolationException{
        if(firstNode.getNodeId()==secondNode.getNodeId())
            throw new LogicViolationException("Node cannot connect itself");
        this.firstNode = firstNode;
        this.secondNode = secondNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Relation relation = (Relation) o;

        return (firstNode.equals(relation.firstNode)&&secondNode.equals(relation.secondNode))||
                (firstNode.equals(relation.secondNode)&&(secondNode.equals(relation.firstNode))) ;

    }

    @Override
    public int hashCode() {
        return Math.min(firstNode.getNodeId(), secondNode.getNodeId()) +
                3100*Math.max(firstNode.getNodeId(), secondNode.getNodeId());
    }
}

class UnionGraph {
    private int unificationStep = 0;
    private List<List<Integer>> adjacencyMatrix;
    private boolean graphBuilt = false;
    public UnionGraph(List<List<Integer>> adjacencyMatrix) throws LogicViolationException{
        this.adjacencyMatrix = adjacencyMatrix;
        buildGraph();
    }
    private Set<Relation> relations;
    private Set<Node> nodes;
    public void setAdjacencyMatrix(List<List<Integer>> adjacencyMatrix) throws LogicViolationException{
        this.adjacencyMatrix = adjacencyMatrix;
        buildGraph();
    }
    private void buildGraph() throws LogicViolationException {
        if(this.adjacencyMatrix==null){
            throw new NullPointerException("Adjacency matrix is not set yet.");
        }
        relations = new HashSet<Relation>();
        nodes = new HashSet<Node>();
        for(int i = 0; i<adjacencyMatrix.size(); i++){
            List<Integer> currentRow = adjacencyMatrix.get(i);
            Node firstNode = new Node(i);
            addNode(firstNode);
            for(Integer second: currentRow){
                Node secondNode = new Node(second);
                addNode(secondNode);
                //Checking if there is connection loop
                if(!firstNode.equals(secondNode))
                    addRelation(new Relation(retrieveNode(firstNode), retrieveNode(secondNode)));//Adds nodes and relations
            }
        }
    }
    private Node getBestNodeToUnify(Node node){
        Integer minCost=null;
        Node nodeWithMinCost = null;
        List<Node> neighbours = getNotUnifiedNeighbours(node);
        for(Node currentNode: neighbours){
            int numberOfNeighbours = getNeighbours(currentNode).size();
            if(minCost==null){
                minCost=numberOfNeighbours;
                nodeWithMinCost = currentNode;
            }
            else if(numberOfNeighbours<minCost){
                minCost=numberOfNeighbours;
                nodeWithMinCost = currentNode;
            }
        }
        return nodeWithMinCost;
    }
    public Node retrieveNode(int nodeId){
        return retrieveNode(new Node(nodeId));
    }
    public Node retrieveNode(Node node){
        Node foundNode = null;
        for(Node currentNode: getNodes()){
            if(currentNode.equals(node)){
                foundNode = currentNode;
                break;
            }
        }
        return foundNode;
    }
    public void addRelation(Relation relation){
        if(retrieveNode(relation.getFirstNode())==null||retrieveNode(relation.getSecondNode())==null)
            throw new NullPointerException("Node not found");
        this.relations.add(relation);
    }
    public Set<Node> getNodes(){
        return this.nodes;
    }
    public Set<Relation> getRelations(){
        return this.relations;
    }
    public void addNode(Node node){
        this.nodes.add(node);
    }
    public void removeNode(Node node){
        for(Relation relation:new HashSet<Relation>(getRelations())){
            if(relation.getFirstNode().equals(node)||relation.getSecondNode().equals(node)){
                removeRelation(relation);
            }
        }
        this.nodes.remove(node);
    }
    public void removeRelation(Relation relation){
        this.relations.remove(relation);
    }
    private List<Node> getNeighbours(Node node){
        List<Node> neighbours = new ArrayList<Node>();
        for(Relation relation: this.relations){
            if(relation.getFirstNode().equals(node)){
                neighbours.add(relation.getSecondNode());
            }
            else if(relation.getSecondNode().equals(node)){
                neighbours.add(relation.getFirstNode());
            }
        }
        return neighbours;
    }
    private List<Node> getNotUnifiedNeighbours(Node node){
        List<Node> neighbours = new ArrayList<Node>();
        for(Relation relation: this.relations){
            if(relation.getFirstNode().equals(node)&&!relation.getSecondNode().isUnified()){
                neighbours.add(relation.getSecondNode());
            }
            else if(relation.getSecondNode().equals(node)&&!relation.getFirstNode().isUnified()){
                neighbours.add(relation.getFirstNode());
            }
        }
        return neighbours;
    }
    private void clearUnifiedState(){
        for(Node node: nodes){
            node.setUnified(false);
        }
    }
    public int unifyAndReturnStepNumber() throws LogicViolationException{
        if(getNodes().size()==1){
            return this.unificationStep;
        }
        else {
            Set<Node> copyOfNodes = new HashSet<Node>(nodes);
            for(Node node:copyOfNodes){
                if(!node.isUnified()){
                    Node otherNode = getBestNodeToUnify(node);
                    if(otherNode != null){
                        node.setUnified(true);
                        otherNode.setUnified(true);
                        unifyNodes(node, otherNode);
                    }
                }
            }
            clearUnifiedState();
            this.unificationStep++;
            return unifyAndReturnStepNumber();
        }
    }
    private void unifyNodes(Node firstNode, Node secondNode) throws LogicViolationException{
        if(firstNode.equals(secondNode)){
            throw new LogicViolationException("First and second node are same");
        }
        for(Node node: getNeighbours(secondNode)){
            if(!firstNode.equals(node))
                addRelation(new Relation(firstNode, node));
        }
        removeNode(secondNode);
    }
}

class Experiment {
    private List<List<Integer>> adjacencyMatrix;
    private Integer numOfCities;

    /**
     * Creates experiment object which represents single experiment.
     * @param numOfCities Number of cities in the current experiment. This parameter should fullfill the condition [2<=numOfCities<=600)
     * @throws WrongInputException
     */
    public Experiment(Integer numOfCities) throws WrongInputException {
        //Number of cities in this experiment
        if(numOfCities<2 || numOfCities>600){
            throw new WrongInputException("numOfCities should be such that it can fulfill expression 2<=numOfCities<=600");
        }
        this.numOfCities = numOfCities;
        //Capacity pre-allocated for performance.
        this.adjacencyMatrix = new ArrayList<List<Integer>>(numOfCities);
    }

    /**
     * Sets adjacency matrix for given string value.
     * @param matrixString Matrix string
     * @throws WrongInputException
     */
    public void setAdjacencyMatrix(String matrixString) throws WrongInputException {
        if(matrixString == null){
            throw new NullPointerException("matrixString cannot be null!");
        }
        //Parsing of value string
        String[] matrixValueStrings = matrixString.trim().split("( )+");
        List<Integer> matrixValues = new LinkedList<Integer>();
        for(String valueString:matrixValueStrings){
            matrixValues.add(Integer.parseInt(valueString.trim()));
        }
        setAdjacencyMatrix(matrixValues.toArray(new Integer[matrixValues.size()]));

    }

    /**
     * Sets adjacency matrix for given array.
     * @param matrixValues Matrix values.
     * @throws WrongInputException
     */
    public void setAdjacencyMatrix(Integer[] matrixValues) throws WrongInputException {

        if(matrixValues.length != this.numOfCities-1){
            throw new WrongInputException(String.format("Number of cities should be equal to %s", this.numOfCities-1));
        }
        List<List<Integer>> adjacencyMatrix = new ArrayList<List<Integer>>(numOfCities);

        for(int i=0; i<numOfCities; i++){
            adjacencyMatrix.add(new LinkedList<Integer>());
        }
        for(int cityNum=0; cityNum<matrixValues.length; cityNum++){
            Integer city1 = cityNum+1;
            Integer city2 = matrixValues[cityNum];
            adjacencyMatrix.get(city1).add(city2);
            adjacencyMatrix.get(city2).add(city1);
        }
        setAdjacencyMatrix(adjacencyMatrix);
    }

    /**
     * Sets adjacency matrix.
     * @param adjacencyMatrix Adjacency matrix.
     * @throws WrongInputException
     */
    public void setAdjacencyMatrix(List<List<Integer>> adjacencyMatrix) throws WrongInputException {
        if(adjacencyMatrix.size() != this.numOfCities){
            throw new WrongInputException("Wrong number of cities! First dimension length of list should be (numOfCities-1)");
        }
        this.adjacencyMatrix = adjacencyMatrix;
    }

    /**
     * Calculates minimum number of steps needed for achievement of whole unified state.
     * @return Step count
     */
    public Integer calculateMinUnionCount() throws LogicViolationException {
        if(this.adjacencyMatrix==null){
            throw new NullPointerException("Adjacency matrix is not set yet");
        }
        UnionGraph graph = new UnionGraph(this.adjacencyMatrix);

        return graph.unifyAndReturnStepNumber();
    }

    /**
     * Gets adjacency matrix
     * @return Adjacency matrix.
     */
    public List<List<Integer>> getAdjacencyMatrix(){
        return this.adjacencyMatrix;
    }


}

public class Main {

    private static Integer EXPERIMENT_LIMIT = 1000;

    private static Integer getInteger(BufferedReader reader, boolean dieOnError){
        Integer result=null;
        while(result==null) {
            try {
                result = Integer.parseInt(reader.readLine().trim());
            } catch (NumberFormatException e) {
                System.err.println("You should feed integer for experiment number!");
                if(dieOnError) System.exit(1);
            } catch (IOException e) {
                System.err.println("Unknown error!");
                if(dieOnError) System.exit(1);
            }
        }
        return result;
    }

    private static String getString(BufferedReader reader, boolean dieOnError){
        String result=null;
        while(result==null) {
            try {
                result = reader.readLine();
            } catch (IOException e) {
                System.err.println("Unknown error!");
                if(dieOnError) System.exit(1);
            }
        }
        return result.trim();
    }

    public static void main(String[] args) throws Exception{

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        //Getting number of experiment
        int numOfExperiment=getInteger(reader, true);
        //Checking if experiment number is less than 1000
        if(numOfExperiment>=EXPERIMENT_LIMIT){
            System.err.println(String.format("Experiment number should be less than %s!", EXPERIMENT_LIMIT) );
            System.exit(1);
        }
        //Getting experiment values
        int num = 0;
        while(num<numOfExperiment){
            int numOfCities=getInteger(reader, false);
            try{
                Experiment experiment = new Experiment(numOfCities);
                experiment.setAdjacencyMatrix(getString(reader, false));
                System.out.println(experiment.calculateMinUnionCount());

            }
            catch (WrongInputException e){
                System.err.println(e.getMessage());
                continue;
            }
            catch (IndexOutOfBoundsException e){
                System.err.println("City indexes should be lower than number of cities.");
                continue;
            }

            num++;

        }
    }
}
