import com.google.gson.Gson;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.LongStream;

public class TransactionMerge {

    private static List<Transaction> transactions = new ArrayList<>();
    private static List<String> histories = new ArrayList<>();
    private static int totalNoOfOperations=0;

    public static void main(String[] args) {
        initializeTransactions();
        generateAllHistories();
    }

    private static void generateAllHistories() {
        List<Operation> allPossibleOperations = new ArrayList<>();
        long productOfSizeFactorials=1;
        for (Transaction transaction : transactions) {
            productOfSizeFactorials=productOfSizeFactorials*calculateFactorial(transaction.getOperations().size());
            allPossibleOperations.addAll(transaction.getOperations());
        }
       totalNoOfOperations=allPossibleOperations.size();
        System.out.println("Expected number of histories "+calculateFactorial(totalNoOfOperations)/productOfSizeFactorials);
        heapPermutation(allPossibleOperations.toArray(Operation[]::new), allPossibleOperations.size(), allPossibleOperations.size());
        System.out.println("Print all generated histories");
        int count = 1;
        for (String t : histories) {
            System.out.println(count + " " + t);
            count++;
        }
    }

    private static void heapPermutation(Operation a[], int size, int n) {
        if (size == 1) {
            if (isInitialOrderKept(new Transaction(Arrays.asList(a).subList(0, totalNoOfOperations)))) {
                histories.add(new Transaction(Arrays.asList(a).subList(0, totalNoOfOperations)).print());
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


    private static boolean isInitialOrderKept(Transaction transaction) {
        boolean kept = true;
        for (int i = 1; i <= transactions.size(); i++) {
            int finalI = i;
            List<Operation> operations = new ArrayList<>();
            for (Operation operation : transaction.getOperations()) {
                if (operation.getTransactionIndex() == i) {
                    operations.add(operation);
                }
            }
            Transaction toTest = new Transaction(operations, finalI);
            if (!transactions.contains(toTest)) {
                kept = false;
            }
        }
        return kept;
    }


    private static  long calculateFactorial(int n) {
        return LongStream.rangeClosed(1, n)
                .reduce(1, (long x, long y) -> x * y);
    }

    private static void initializeTransactions(){
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("transactions.json"));
           transactions  = Arrays.asList(gson.fromJson(reader,  Transaction[].class));
            reader.close();
            for(Transaction t:transactions){
                System.out.println(t.print());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
