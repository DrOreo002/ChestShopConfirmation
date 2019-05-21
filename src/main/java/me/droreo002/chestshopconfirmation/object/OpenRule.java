package me.droreo002.chestshopconfirmation.object;

import lombok.Getter;
import lombok.Setter;

public class OpenRule {

    @Getter @Setter
    private String world;
    @Getter @Setter
    private TransactionType transactionType;

    public OpenRule(String syntax) {
        if (!syntax.contains(":")) throw new IllegalStateException("Invalid OpenRule syntax! (" + syntax + ")");
        String[] sp = syntax.split(":");
        if (sp.length != 2) throw new IllegalStateException("Invalid OpenRule syntax! (" + syntax + ")");
        this.world = sp[0];
        this.transactionType = TransactionType.valueOf(sp[1]);
    }

    public enum TransactionType {
        BUY,
        SELL,
        BOTH,
        DISABLED;
    }
}
