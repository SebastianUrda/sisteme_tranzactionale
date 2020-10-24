import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Transaction {
    private List<Operation> operations = new ArrayList<Operation>();

    private int index=0;

    public List<Operation> getOperations() {
        return operations;
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }

    public Transaction(List<Operation> operations) {
        this.operations = operations;
    }

    public Transaction(List<Operation> operations, int index) {
        this.operations = operations;
        this.index = index;
        for(Operation operation:this.operations){
            operation.setTransactionIndex(index);
        }
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        for(Operation operation:this.operations){
            operation.setTransactionIndex(index);
        }
        this.index = index;
    }

    public String print() {
        String toReturn="";
        for (Operation operation : operations) {
            toReturn=toReturn+operation.execute();
        }
        return toReturn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return index == that.index &&
                Objects.equals(operations, that.operations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operations, index);
    }
}
