public class Read extends Operation {

    public Read(String variable, int index) {
        super("read");
        super.setVariable(variable);
        super.setTransactionIndex(index);
    }

    public Read(String variable) {
        super("read");
        super.setVariable(variable);
    }

}
