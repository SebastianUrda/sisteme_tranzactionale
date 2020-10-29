public class Write extends Operation {

    public Write(String variable,int index) {
        super("write");
        super.setVariable(variable);
        super.setTransactionIndex(index);
    }


    public Write(String variable) {
        super("write");
        super.setVariable(variable);
    }

}
