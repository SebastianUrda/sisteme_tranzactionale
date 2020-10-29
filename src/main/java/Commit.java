public class Commit extends Operation {

    public Commit(int index) {
        super("commit");
        super.setVariable("");
        super.setTransactionIndex(index);
    }
}
