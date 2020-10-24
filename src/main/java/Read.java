public class Read extends Operation {

    public Read() {
        super("read");
    }

    public Read(String variable) {
        super("read");
        super.setVariable(variable);
    }

}
