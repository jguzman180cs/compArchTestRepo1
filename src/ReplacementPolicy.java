public enum ReplacementPolicy {
    RR("Round Robin"),LRU("Least Recently Used"),RND("Random Replacement Cache");

    private String stringName;
    ReplacementPolicy(String s) {
        this.stringName = s;
    }

    public String getStringName(){
        return stringName;
    }
}
