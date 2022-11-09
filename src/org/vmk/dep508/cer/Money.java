package org.vmk.dep508.cer;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

public class Money {

    private final Map<BigDecimal, Integer> REMAINS_FROM_DIVISION = new HashMap<>();

    private Currency currency;
    private BigDecimal amount;

    public Money(Currency currency, BigDecimal amount) {
        this.currency = currency;
        this.amount = amount.setScale(this.currency.getDefaultFractionDigits());
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Money add(Money m) {
        checkCurrencies(m);
        return new Money(this.currency, amount.add(m.getAmount()));
    }

    public Money subtract(Money m) {
        checkCurrencies(m);
        return new Money(this.currency, amount.subtract(m.getAmount()));
    }

    public Money multiply(BigDecimal ratio) {
        return new Money(this.currency, amount.multiply(ratio).setScale(currency.getDefaultFractionDigits(), RoundingMode.HALF_UP));
    }

    public Money divide(BigDecimal ratio) {
        if (REMAINS_FROM_DIVISION.containsKey(ratio)) {
            Integer divisionNum = REMAINS_FROM_DIVISION.get(ratio);
            if (divisionNum == 0) {
                REMAINS_FROM_DIVISION.remove(ratio);
            } else {
                REMAINS_FROM_DIVISION.put(ratio, --divisionNum);
                return new Money(this.currency, amount.divide(ratio, RoundingMode.CEILING));
            }
        } else {
            BigDecimal floorDiv = amount.divide(ratio, RoundingMode.FLOOR);
            BigDecimal remains = amount.subtract(floorDiv.multiply(ratio));
            REMAINS_FROM_DIVISION.put(ratio, remains.divide(new BigDecimal("0.01"), RoundingMode.FLOOR).intValue());
        }
        return new Money(this.currency, amount.divide(ratio, RoundingMode.FLOOR));
    }

    private void checkCurrencies(Money m) {
        if (!this.currency.equals(m.getCurrency())) {
            throw new DifferentCurrenciesException(
                    String.format("Different currencies: [%s] and [%s]", this.currency, m.getCurrency())
            );
        }
    }
}
