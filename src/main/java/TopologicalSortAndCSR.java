import java.util.*;
import java.util.stream.Collectors;

public class TopologicalSortAndCSR {

    private static List<Operation> operations;
    private static List<String> variables;
    private static ArrayList<ArrayList<Integer>> adjacencyMatrix;
    private static List<List<Integer>> adjacencyList;
    private static int V;

    public static void main(String[] args) {
        String h1 = "r3(z)r1(y)w3(z)w1(y)r1(x)r2(y)w2(y)w1(x)r2(x)w2(x)c1c2c3";
        generateTransactionFromString(h1);
        generateConflictGraph();
        if (isCyclic()) {
            System.out.println("Contains Cycles so it does not belong to CSR ");
        } else {
            System.out.println("It does not contain cycles so belongs to CSR ");
        }
        System.out.println("Topological sort");
        topologicalSort();
        String h2 = "r1(x)w1(z)r2(z)w1(y)c1r3(y)w2(z)c2w3(x)w3(y)c3";
        generateTransactionFromString(h2);
        generateConflictGraph();
        if (isCyclic()) {
            System.out.println("Contains Cycles so it does not belong to CSR ");
        } else {
            System.out.println("It does not contain cycles so belongs to CSR ");
        }
        System.out.println("Topological sort");
        topologicalSort();
        String h3 = "r1(z)w5(x)r4(z)w1(y)r5(z)w4(x)c4w3(z)w1(x)c1w2(y)c5c2r3(y)c3";
        generateTransactionFromString(h3);
        generateConflictGraph();
        if (isCyclic()) {
            System.out.println("Contains Cycles so it does not belong to CSR ");
        } else {
            System.out.println("It does not contain cycles so belongs to CSR ");
        }
        System.out.println("Topological sort");
        topologicalSort();

    }

    private static void generateTransactionFromString(String h1) {
        operations = new ArrayList<>();
        variables = new ArrayList<>();
        System.out.println(h1);
        for (int i = 0; i < h1.length(); i++) {
            if (h1.charAt(i) == 'r') {
                String variable = String.valueOf(h1.charAt(i + 3));
                if (!variables.contains(variable)) {
                    variables.add(variable);
                }
                operations.add(new Read(variable, Character.getNumericValue(h1.charAt(i + 1))));
            }
            if (h1.charAt(i) == 'w') {
                String variable = String.valueOf(h1.charAt(i + 3));
                if (!variables.contains(variable)) {
                    variables.add(variable);
                }
                operations.add(new Write(variable, Character.getNumericValue(h1.charAt(i + 1))));
            }
            if (h1.charAt(i) == 'c') {
                operations.add(new Commit(Character.getNumericValue(h1.charAt(i + 1))));
            }
        }
        Transaction transaction = new Transaction(operations);
        System.out.println(transaction.print());
        System.out.println(variables);
    }

    private static void generateConflictGraph() {
        Map<String, List<Operation>> variableOperationsMap = new HashMap<>();
        for (String variable : variables) {
            List<Operation> operations1 = operations.stream().filter(operation -> operation.getVariable().equals(variable)).collect(Collectors.toList());
            variableOperationsMap.putIfAbsent(variable, operations1);
        }
        Integer numberOfTransactions = operations.stream().max(Comparator.comparingInt(Operation::getTransactionIndex)).orElseThrow().getTransactionIndex();
        V = numberOfTransactions + 1;
        adjacencyMatrix = new ArrayList<ArrayList<Integer>>(numberOfTransactions + 1);

        for (int i = 0; i < numberOfTransactions + 1; i++)
            adjacencyMatrix.add(new ArrayList<Integer>());

        adjacencyList = new ArrayList<>(V);
        for (int i = 0; i < V; i++)
            adjacencyList.add(new LinkedList<>());

        variableOperationsMap.forEach((variable, operationList) -> {
            for (int i = 0; i < operationList.size() - 1; i++) {
                for (int j = i + 1; j < operationList.size(); j++) {
                    if (!(operationList.get(i).getType().equals("read") && operationList.get(j).getType().equals("read")) &&
                            (!operationList.get(i).getType().equals("commit") && !operationList.get(j).getType().equals("commit")) &&
                            (operationList.get(i).getTransactionIndex() != operationList.get(j).getTransactionIndex())) {
                        addEdgeToMatrix(operationList.get(i).getTransactionIndex(), operationList.get(j).getTransactionIndex());
                        addEdgeToList(operationList.get(i).getTransactionIndex(), operationList.get(j).getTransactionIndex());
                    }
                }
            }
        });
        System.out.println("Adjacency matrix");
        for (int i = 0; i < adjacencyMatrix.size(); i++) {
            for (int j = 0; j < adjacencyMatrix.get(i).size(); j++) {
                System.out.println(i + " " + adjacencyMatrix.get(i).get(j));
            }
        }

    }


    static void addEdgeToMatrix(int v, int w) {
        adjacencyMatrix.get(v).add(w);
    }

    private static boolean isCyclicUtil(int i, boolean[] visited,
                                        boolean[] recStack) {

        if (recStack[i])
            return true;

        if (visited[i])
            return false;

        visited[i] = true;

        recStack[i] = true;
        List<Integer> children = adjacencyList.get(i);

        for (Integer c : children)
            if (isCyclicUtil(c, visited, recStack))
                return true;

        recStack[i] = false;

        return false;
    }

    private static void addEdgeToList(int source, int dest) {
        adjacencyList.get(source).add(dest);
    }

    private static boolean isCyclic() {

        boolean[] visited = new boolean[V];
        boolean[] recStack = new boolean[V];


        for (int i = 0; i < V; i++)
            if (isCyclicUtil(i, visited, recStack))
                return true;

        return false;
    }

    static void topologicalSortUtil(int v, boolean visited[],
                                    Stack<Integer> stack) {

        visited[v] = true;
        Integer i;


        Iterator<Integer> it = adjacencyMatrix.get(v).iterator();
        while (it.hasNext()) {
            i = it.next();
            if (!visited[i])
                topologicalSortUtil(i, visited, stack);
        }

        stack.push(Integer.valueOf(v));
    }


    static void topologicalSort() {
        Stack<Integer> stack = new Stack<Integer>();

        boolean visited[] = new boolean[V];
        for (int i = 0; i < V; i++)
            visited[i] = false;


        for (int i = 0; i < V; i++)
            if (visited[i] == false)
                topologicalSortUtil(i, visited, stack);


        while (stack.empty() == false) {
            int toPrint = stack.pop();
            if (toPrint == 0) {
                System.out.print(" ");
            } else {
                System.out.print(toPrint + " ");
            }
        }
        System.out.println();
    }
}
