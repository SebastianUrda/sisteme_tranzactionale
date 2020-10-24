import java.util.Objects;

public class Operation {
    private String variable;
    private String type;
    private int transactionIndex;

    public Operation(String type) {
        this.type = type;
    }


    public String  execute() {
        return type +" "+transactionIndex+ " (" + variable + ") ";
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getTransactionIndex() {
        return transactionIndex;
    }

    public void setTransactionIndex(int transactionIndex) {
        this.transactionIndex = transactionIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Operation operation = (Operation) o;
        return transactionIndex == operation.transactionIndex &&
                Objects.equals(variable, operation.variable) &&
                Objects.equals(type, operation.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variable, type, transactionIndex);
    }
}
