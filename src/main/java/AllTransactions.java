import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AllTransactions {

    private static List<String> variables = Arrays.asList("x", "y", "z");
    private static int M = 2;//read
    private static int N = 2;//write
    private static List<String> trans = new ArrayList<>();

    public static void main(String[] args) {

        System.out.println("M=" + M + " N=" + N);
        System.out.println("L=" + variables);

        generateAllTransactions();
    }

    private static void generateAllTransactions() {
        List<Operation> allPossibleOperations = new ArrayList<>();
        for (String variable : variables) {
            allPossibleOperations.add(new Read(variable));
            allPossibleOperations.add(new Write(variable));
        }

        heapPermutation(allPossibleOperations.toArray(Operation[]::new), allPossibleOperations.size(), allPossibleOperations.size());
        System.out.println("Print all transactions");
        int count = 1;
        for (String t : trans) {
            System.out.println(count + " " + t);
            count++;
        }

    }

    private static void heapPermutation(Operation a[], int size, int n) {
        if (size == 1) {
            if (isTransactionValid(new Transaction(Arrays.asList(a).subList(0, M + N))) && !isTransactionAlreadyInList(new Transaction(Arrays.asList(a).subList(0, M + N)))) {
                trans.add(new Transaction(Arrays.asList(a).subList(0, M + N)).print());
            }
        }

        for (int i = 0; i < size; i++) {
            heapPermutation(a, size - 1, n);

            Operation temp;
            if (size % 2 == 1) {
                temp = a[0];
                a[0] = a[size - 1];
            } else {
                temp = a[i];
                a[i] = a[size - 1];
            }
            a[size - 1] = temp;
        }
    }

    private static boolean isTransactionValid(Transaction transaction) {
        boolean valid = true;
        List<Operation> temporaryOperationsList = new ArrayList<>();
        int reads = 0;
        int writes = 0;
        for (Operation op : transaction.getOperations()) {
            if (isNextOperationValid(temporaryOperationsList, op, reads, writes)) {
                temporaryOperationsList.add(op);
                if (op.getType().equals("read")) {
                    reads++;
                } else {
                    writes++;
                }
            } else {
                valid = false;
            }
        }
        return valid;
    }

    private static boolean isTransactionAlreadyInList(Transaction transaction) {
        boolean present = false;
        for (String t : trans) {
            if (t.equals(transaction.print()))
                present = true;
        }
        return present;
    }

    private static boolean isNextOperationValid(List<Operation> operations, Operation operation, int readsUsed, int writesUsed) {
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

}
