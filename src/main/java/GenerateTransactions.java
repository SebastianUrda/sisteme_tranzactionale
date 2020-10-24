import com.google.gson.Gson;

import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GenerateTransactions {
    private static List<String> variables = Arrays.asList("x", "y", "z");
    private static int M = 1;//read
    private static int N = 2;//write
    private static int K = 3;//transactions
    private static List<String> trans=new ArrayList<>();
    public static void main(String[] args) {
      List<Transaction> transactions = new ArrayList<>();
        System.out.println("M=" + M + " N=" + N +" K= "+K);
        System.out.println("L=" + variables);
        Transaction transaction = null;
        int index=1;
        while (transactions.size() < K) {
            do {
                transaction = generateRandomTransaction(new ArrayList<Operation>());
            } while (transaction == null);
            transaction.setIndex(index);
            transactions.add(transaction);
            index++;
            System.out.println(transaction.print());
        }
//        generateAllTransactions();
        try {
            Gson gson = new Gson();
            Writer writer = Files.newBufferedWriter(Paths.get("transactions.json"));
            gson.toJson(transactions, writer);
            writer.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static Transaction generateRandomTransaction(List<Operation> operations) {
        int reads = 0;
        int writes = 0;
        while (reads + writes != M + N) {
            Operation toAdd = null;
            int tries = 100;
            do {
                int variableIndex = ThreadLocalRandom.current().nextInt(0, variables.size());
                int operationIndex = ThreadLocalRandom.current().nextInt(0, 2);
                toAdd = operationIndex == 0 ? new Read(variables.get(variableIndex)) : new Write(variables.get(variableIndex));
                tries--;
            } while ((!isNextOperationValid(operations, toAdd, reads, writes)) && (tries > 0));
            if (toAdd != null) {
                operations.add(toAdd);
                if (toAdd.getType().equals("read")) {
                    reads++;
                } else {
                    writes++;
                }
            }
            if (tries == 0) {
                return null;
            }
        }
        return new Transaction(operations);
    }

    public static boolean isNextOperationValid(List<Operation> operations, Operation operation, int readsUsed, int writesUsed) {
        boolean valid = true;
        if (operation.getType().equals("read") && readsUsed >= M) {
            valid = false;
        }
        if (operation.getType().equals("write") && writesUsed >= N) {
            valid = false;
        }
        for (Operation iterator : operations) {
            if ((iterator.getType().equals(operation.getType()) &&
                    iterator.getVariable().equals(operation.getVariable())) ||
                    (iterator.getVariable().equals(operation.getVariable()) &&
                            iterator.getType().equals("write") && operation.getType().equals("read"))) {
                valid = false;
            }
        }
        return valid;
    }

//    public static void generateAllTransactions() {
//        List<Operation> allPossibleOperations = new ArrayList<>();
//        for (String variable : variables) {
//            allPossibleOperations.add(new Read(variable));
//            allPossibleOperations.add(new Write(variable));
//        }
//
//        heapPermutation(allPossibleOperations.toArray(Operation[]::new), allPossibleOperations.size(), allPossibleOperations.size());
//        System.out.println("Print all transactions");
//        int count=1;
//        for(String t:trans){
//            System.out.println(count+" "+t);
//            count++;
//        }
//
//    }
//
//
//    static void heapPermutation(Operation a[], int size, int n) {
//        if (size == 1) {
//           if(isTransactionValid(new Transaction(Arrays.asList(a).subList(0, M + N)))&&!isTransactionAlreadyInList(new Transaction(Arrays.asList(a).subList(0, M + N)))){
//               trans.add(new Transaction(Arrays.asList(a).subList(0, M + N)).print());
//           }
//        }
//
//        for (int i = 0; i < size; i++) {
//            heapPermutation(a, size - 1, n);
//
//            // if size is odd, swap 0th i.e (first) and
//            // (size-1)th i.e (last) element
//            Operation temp;
//            if (size % 2 == 1) {
//                temp = a[0];
//                a[0] = a[size - 1];
//            }
//
//            // If size is even, swap ith
//            // and (size-1)th i.e last element
//            else {
//                temp = a[i];
//                a[i] = a[size - 1];
//            }
//            a[size - 1] = temp;
//        }
//    }







//    public static boolean isTransactionValid(Transaction transaction) {
//        boolean valid = true;
//        List<Operation> temporaryOperationsList = new ArrayList<>();
//        int reads = 0;
//        int writes = 0;
//        for (Operation op : transaction.getOperations()) {
//            if (isNextOperationValid(temporaryOperationsList, op, reads, writes)) {
//                temporaryOperationsList.add(op);
//                if (op.getType().equals("read")) {
//                    reads++;
//                } else {
//                    writes++;
//                }
//            } else {
//                valid = false;
//            }
//        }
//        return valid;
//    }
//
//    public static boolean isTransactionAlreadyInList( Transaction transaction){
//        boolean present=false;
//        for(String t:trans){
//           if(t.equals(transaction.print()))
//            present=true;
//        }
//        return present;
//    }

}
