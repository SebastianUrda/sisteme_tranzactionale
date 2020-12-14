public class Abort extends Operation {
    public Abort(int index) {
        super("abort");
        super.setVariable("");
        super.setTransactionIndex(index);
    }
}
