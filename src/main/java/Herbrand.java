import java.util.*;

public class Herbrand {

    private static List<Operation> operations = new ArrayList<>();
    private static Map<List<Operation>, List<Operation>> herbrandSemantics = new HashMap<>();

    public static void main(String[] args) {
        System.out.print("First History ");
        initialiseFirstHistory();
        generateHerbrandSemantic();
        printWithBackTrack();
        System.out.print("Second History ");
        initialiseSecondHistory();
        generateHerbrandSemantic();
        printWithBackTrack();
    }


    private static void generateHerbrandSemantic() {
        herbrandSemantics = new HashMap<>();
        for (int iterator = 0; iterator < operations.size(); iterator++) {
            Operation operation = operations.get(iterator);
            if (!operation.getType().equals("commit")) {
                if (operation.getType().equals("read")) {
                    Operation read = getTheLastWriteOnVariableBeforeIndex(operation.getVariable(), iterator);
                    herbrandSemantics.putIfAbsent(Arrays.asList(operation), read == null ? null : Arrays.asList(read));

                } else if (operation.getType().equals("write")) {
                    List<Operation> writes = getAllReadsInTransactionWithIndexBeforeIndex(operation.getTransactionIndex(), iterator);
                    herbrandSemantics.putIfAbsent(Arrays.asList(operation), writes);
                }
            }
        }
    }


    private static void printWithBackTrack() {
        for (Operation operation : operations) {
            if (!operation.getType().equals("commit")) {
                mapBacktrack(Arrays.asList(operation), "");
            }
        }
    }

    private static void mapBacktrack(List<Operation> operations, String semantic) {
        if (operations == null) {
            System.out.println(semantic);
        } else {
            if (!semantic.isEmpty()) {
                semantic = semantic + "= ";
            }
            for (Operation op : operations) {
                semantic = semantic + "Hs(" + op.execute() + ")";
            }
            mapBacktrack(herbrandSemantics.get(operations), semantic);
        }
    }


    private static Operation getTheLastWriteOnVariableBeforeIndex(String variable, int index) {
        for (int iterator = index; iterator >= 0; iterator--) {
            Operation operation = operations.get(iterator);
            if (operation.getType().equals("write") && operation.getVariable().equals(variable)) {
                return operation;
            }
        }
        return null;
    }

    private static List<Operation> getAllReadsInTransactionWithIndexBeforeIndex(int transactionIndex, int index) {
        List<Operation> reads = new ArrayList<>();
        for (int iterator = index; iterator >= 0; iterator--) {
            Operation operation = operations.get(iterator);
            if (operation.getType().equals("read") && operation.getTransactionIndex() == transactionIndex) {
                reads.add(operation);
            }
        }
        return reads.isEmpty() ? null : reads;
    }


    private static void initialiseFirstHistory() {
        operations = new ArrayList<>();
        operations.add(new Read("x", 1));
        operations.add(new Write("z", 1));
        operations.add(new Read("z", 2));
        operations.add(new Write("y", 1));
        operations.add(new Commit(1));
        operations.add(new Read("y", 3));
        operations.add(new Write("z", 2));
        operations.add(new Commit(2));
        operations.add(new Write("x", 3));
        operations.add(new Write("y", 3));
        operations.add(new Commit(3));
        System.out.println(new Transaction(operations).print());
    }

    private static void initialiseSecondHistory() {
        operations = new ArrayList<>();
        operations.add(new Read("x", 1));
        operations.add(new Read("x", 3));
        operations.add(new Write("y", 3));
        operations.add(new Write("x", 2));
        operations.add(new Commit(3));
        operations.add(new Read("y", 4));
        operations.add(new Write("x", 4));
        operations.add(new Commit(2));
        operations.add(new Read("x", 5));
        operations.add(new Commit(4));
        operations.add(new Write("z", 5));
        operations.add(new Write("z", 1));
        operations.add(new Commit(1));
        operations.add(new Commit(5));
        System.out.println(new Transaction(operations).print());
    }
}
