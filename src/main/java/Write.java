public class Write extends Operation {

    public Write() {
        super("write");
    }

    public Write(String variable) {
        super("write");
        super.setVariable(variable);
    }
}
