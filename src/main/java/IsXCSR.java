import java.util.*;
import java.util.stream.Collectors;

public class IsXCSR {
    private static List<Operation> operations;
    private static List<Integer> transactionIndexes;
    private static List<Operation> expandedSchedule;

    private static List<String> variables;
    private static List<List<Integer>> adjacencyList;
    private static int V;

    public static void main(String[] args) {
        String h1 = "r3(z)r1(y)w3(z)w1(y)r1(x)r2(y)w2(y)w1(x)r2(x)w2(x)c1a2c3";
        generateTransactionFromString(h1);
        generateExtendedSchedule();
        if(isXCSR()) System.out.println("Is XCSR");
        else{
            System.out.println("Is NOT XCSR");
        }


        String h2 = "r1(x)w1(z)r2(z)w1(y)a1r3(y)w2(z)c2w3(x)w3(y)a3";
        generateTransactionFromString(h2);
        generateExtendedSchedule();
        if(isXCSR()) System.out.println("Is XCSR");
        else{
            System.out.println("Is NOT XCSR");
        }


        String h3 = "r1(z)w5(x)r4(z)w1(y)r5(z)w4(x)a4w3(z)w1(x)a1w2(y)c5c2r3(y)c3";
        generateTransactionFromString(h3);
        generateExtendedSchedule();
        if(isXCSR()) System.out.println("Is XCSR");
        else{
            System.out.println("Is NOT XCSR");
        }
    }

    private static void generateTransactionFromString(String h1) {
        operations = new ArrayList<>();
        variables = new ArrayList<>();
        transactionIndexes = new ArrayList<>();
        System.out.println(h1);
        for (int i = 0; i < h1.length(); i++) {
            if (h1.charAt(i) == 'r') {
                String variable = String.valueOf(h1.charAt(i + 3));
                Integer transactionIndex = Character.getNumericValue(h1.charAt(i + 1));
                if (!variables.contains(variable)) {
                    variables.add(variable);
                }
                if (!transactionIndexes.contains(transactionIndex)) {
                    transactionIndexes.add(transactionIndex);
                }
                operations.add(new Read(variable, transactionIndex));
            }
            if (h1.charAt(i) == 'w') {
                String variable = String.valueOf(h1.charAt(i + 3));
                Integer transactionIndex = Character.getNumericValue(h1.charAt(i + 1));
                if (!variables.contains(variable)) {
                    variables.add(variable);
                }
                if (!transactionIndexes.contains(transactionIndex)) {
                    transactionIndexes.add(transactionIndex);
                }
                operations.add(new Write(variable, transactionIndex));
            }
            if (h1.charAt(i) == 'c') {
                Integer transactionIndex = Character.getNumericValue(h1.charAt(i + 1));
                if (!transactionIndexes.contains(transactionIndex)) {
                    transactionIndexes.add(transactionIndex);
                }
                Operation commit = new Commit(transactionIndex);
                operations.add(commit);
            }
            if (h1.charAt(i) == 'a') {
                Integer transactionIndex = Character.getNumericValue(h1.charAt(i + 1));
                if (!transactionIndexes.contains(transactionIndex)) {
                    transactionIndexes.add(transactionIndex);
                }
                Operation abort = new Abort(transactionIndex);
                operations.add(abort);
            }
        }
        Transaction transaction = new Transaction(operations);
        System.out.println("Schedule");
        System.out.println(transaction.print());

    }

    private static boolean isXCSR(){
        Map<String, List<Operation>> variableOperationsMap = new HashMap<>();
        for (String variable : variables) {
            List<Operation> operations1 = expandedSchedule.stream().filter(operation -> operation.getVariable().equals(variable)).collect(Collectors.toList());
            variableOperationsMap.putIfAbsent(variable, operations1);
        }
        V = transactionIndexes.size() + 1;
        adjacencyList = new ArrayList<>(V);
        for (int i = 0; i < V; i++)
            adjacencyList.add(new LinkedList<>());
        variableOperationsMap.forEach((variable, operationList) -> {
            for (int i = 0; i < operationList.size() - 1; i++) {
                for (int j = i + 1; j < operationList.size(); j++) {
                    if (!(operationList.get(i).getType().equals("read") && operationList.get(j).getType().equals("read")) &&
                            (!operationList.get(i).getType().equals("commit") && !operationList.get(j).getType().equals("commit")) &&
                            (operationList.get(i).getTransactionIndex() != operationList.get(j).getTransactionIndex())) {
                        addEdgeToAdjacencyList(operationList.get(i).getTransactionIndex(), operationList.get(j).getTransactionIndex());
                    }
                }
            }
        });
        return !isCyclic();
    }

    private static void generateExtendedSchedule() {
        List<Operation> resultingOperations = new ArrayList<>();
        Map<Integer, Boolean> hasTransactionEnded = new HashMap<>();
        for (Integer transactionIndex : transactionIndexes) {
            hasTransactionEnded.putIfAbsent(transactionIndex, false);
        }

        for (Operation operation : operations) {
            if (!operation.getType().equals("abort")) {
                resultingOperations.add(operation);
            }
            if (operation.getType().equals("abort")) {
                resultingOperations.addAll(generateReversedTransaction(getOperationsOfGivenTransaction(operation.getTransactionIndex())));
                resultingOperations.add(new Commit(operation.getTransactionIndex()));
                hasTransactionEnded.put(operation.getTransactionIndex(), true);
            }
            if (operation.getType().equals("commit")) {
                hasTransactionEnded.put(operation.getTransactionIndex(), true);
            }
        }
        List<Integer> notEndedTransactions = new ArrayList<>();
        hasTransactionEnded.forEach((transactionIndex, hasEnded) -> {

            if (!hasEnded) {
                notEndedTransactions.add(transactionIndex);
            }

        });
        resultingOperations.addAll(generateReversedTransaction(extractWritesOfUnfinishedTransactions(notEndedTransactions)));
        for(Integer i:notEndedTransactions){
            resultingOperations.add(new Commit(i));
            hasTransactionEnded.put(i,true);
        }

        System.out.println("Expanded schedule");
        expandedSchedule=resultingOperations;
        System.out.println(new Transaction(expandedSchedule).print());

    }

    private static List<Operation> generateReversedTransaction(List<Operation> operationsToReverse) {
        List<Operation> tempOperations = new ArrayList<>();
        for (int i = operationsToReverse.size(); i-- > 0; ) {
            if (operationsToReverse.get(i).getType().equals("write")) {
                tempOperations.add(new Write(operationsToReverse.get(i).getVariable(), operationsToReverse.get(i).getTransactionIndex(), true));
            }
        }
        return tempOperations;
    }

    private static List<Operation> getOperationsOfGivenTransaction(Integer transactionIndex) {
        return operations.stream()
                .filter(o -> o.getTransactionIndex() == transactionIndex).collect(Collectors.toList());
    }

    private static List<Operation> extractWritesOfUnfinishedTransactions(List<Integer> unfinishedTransactions) {
        return operations.stream()
                .filter(o -> unfinishedTransactions.contains(o.getTransactionIndex())).collect(Collectors.toList());
    }


    private static boolean isCyclic() {

        boolean[] visited = new boolean[V];
        boolean[] recStack = new boolean[V];


        for (int i = 0; i < V; i++)
            if (isCyclicUtil(i, visited, recStack))
                return true;

        return false;
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
    private static void addEdgeToAdjacencyList(int source, int dest) {
        adjacencyList.get(source).add(dest);
    }
}

