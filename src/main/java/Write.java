public class Write extends Operation {

    private boolean reversed;

    public Write(String variable, int index) {
        super("write");
        super.setVariable(variable);
        super.setTransactionIndex(index);
    }

    public Write(String variable, int index, boolean reversed) {
        super("write");
        super.setVariable(variable);
        super.setTransactionIndex(index);
        this.reversed = reversed;
    }


    public Write(String variable) {
        super("write");
        super.setVariable(variable);
    }

    public String execute() {
        return reversed ? super.getType() + super.getTransactionIndex() + "(" + super.getVariable() + ")-1" :
                super.getType() + super.getTransactionIndex() + "(" + super.getVariable() + ")";
    }

}
