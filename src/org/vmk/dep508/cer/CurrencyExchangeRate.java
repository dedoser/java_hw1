package org.vmk.dep508.cer;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

/**
 * Created by VPerov on 17.09.2018.
 */
public class CurrencyExchangeRate {

    private BigDecimal rate;
    private Currency from;
    private Currency to;

    public CurrencyExchangeRate(BigDecimal rate, Currency from, Currency to) {
        if (BigDecimal.ZERO.equals(rate)) {
            throw new IncorrectExchangeRateException("Exchange rate cannot be zero");
        }
        this.rate = rate;
        this.from = from;
        this.to = to;
    }

    public Money convert(Money m) {
        if (!Objects.equals(m.getCurrency(), from)) {
            throw new DifferentCurrenciesException(
                    String.format("Different currencies: [%s] and [%s]", from, m.getCurrency())
            );
        }
        return new Money(to, m.multiply(rate).getAmount());
    }
}
